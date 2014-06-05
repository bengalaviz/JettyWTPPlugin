/*******************************************************************************
 * Copyright (c) 2014 Benjamin Galaviz and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Benjamin Galaviz <ben.galaviz@gmail.com> - Initial API and implementation 
 *******************************************************************************/
package org.eclipse.jst.server.jetty.core.internal.jetty9;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.internal.ProgressUtil;
import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.jst.server.jetty.core.WebModule;
import org.eclipse.jst.server.jetty.core.internal.IJettyWebModule;
import org.eclipse.jst.server.jetty.core.internal.JettyConfiguration;
import org.eclipse.jst.server.jetty.core.internal.JettyConstants;
import org.eclipse.jst.server.jetty.core.internal.JettyServer;
import org.eclipse.jst.server.jetty.core.internal.Messages;
import org.eclipse.jst.server.jetty.core.internal.Trace;
import org.eclipse.jst.server.jetty.core.internal.config.JettyXMLConfig;
import org.eclipse.jst.server.jetty.core.internal.config.PathFileConfig;
import org.eclipse.jst.server.jetty.core.internal.config.StartConfig;
import org.eclipse.jst.server.jetty.core.internal.config.StartIni;
import org.eclipse.jst.server.jetty.core.internal.config.WebdefaultXMLConfig;
import org.eclipse.jst.server.jetty.core.internal.util.IOUtils;
import org.eclipse.jst.server.jetty.core.internal.xml.Factory;
import org.eclipse.jst.server.jetty.core.internal.xml.jetty9.ServerInstance;
import org.eclipse.jst.server.jetty.core.internal.xml.jetty9.server.Connector;
import org.eclipse.jst.server.jetty.core.internal.xml.jetty9.server.Server;
import org.eclipse.jst.server.jetty.core.internal.xml.jetty9.server.WebApp;
import org.eclipse.jst.server.jetty.core.internal.xml.jetty9.webapp.WebAppContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.ServerPort;

public class Jetty9Configuration extends JettyConfiguration implements JettyConstants{
    private StartIni _startIniConfig;

    protected ServerInstance _serverInstance;
    
    private boolean _isServerDirty;
    
    // property change listeners
    private transient List<PropertyChangeListener> _propertyListeners;

    
    public Jetty9Configuration(IFolder path){
        super(path);
    }
    
    public Collection<ServerPort> getServerPorts(){
        List<ServerPort> ports = new ArrayList<ServerPort>();

        // first add server port
        try {
        	int port = Integer.parseInt(_serverInstance.getAdminPort());
        	ports.add(new ServerPort("server", Messages.portServer, port, "TCPIP"));
        } catch (Exception e) {
        	// ignore
        }

        // add connectors
        try{

            Collection<Connector> connectors = _serverInstance.getConnectors();
            if (connectors != null){
            	int portId = 0;
                for (Connector connector : connectors){
                    int port = -1;
                    try{
                        port = Integer.parseInt(connector.getPort());
                    }catch (Exception e){
                        // ignore
                    }
                    
                    String id = Integer.toString(portId++);
                    String type = connector.getType();
                    String name = "HTTP";
                    String className = type.substring(type.lastIndexOf('.')+1); 
                    if ("SelectChannelConnector".equals(className) || 
                    		"SocketConnector".equals(className)) {
                    	name = "HTTP";
                    } else if ("SslSelectChannelConnector".equals(className) ||
                    		"SslSocketConnector".equals(className)) {
                    	name = "SSL";
                    } else if ("Ajp13SocketConnector".equals(className)) {
                    	name = "AJP";
                    }
                    	
                    ports.add(new ServerPort(id,name,port,name));
                }
            }

        }catch (Exception e){
            Trace.trace(Trace.SEVERE,"Error getting server ports",e);
        }

        return ports;
    }
    
    /**
     * Return the port number.
     * 
     * @return int
     */
    public ServerPort getAdminPort(){
        Collection<ServerPort> serverPorts = getServerPorts();
        
        for (ServerPort serverPort : serverPorts){
            // Return only an HTTP port from the selected Service
            if (serverPort.getId().equals("server")){
                return serverPort;
            }
        }
        
        return null;
    }


    /**
     * Return a list of the web modules in this server.
     * 
     * @return java.util.List
     */
    public List<WebModule> getWebModules(){
        List<WebModule> list = new ArrayList<WebModule>();

        try{
            Collection<WebAppContext> contexts = _serverInstance.getContexts();
            if (contexts != null){
                for (WebAppContext context : contexts){
                    String documentBase = context.getDocumentBase();
                    String path = context.getContextPath();
                    String memento = context.getMemento();
                    WebModule module = new WebModule(path,documentBase,memento,true);
                    list.add(module);
                }
            }
        }catch (Exception e){
            Trace.trace(Trace.SEVERE,"Error getting project refs",e);
        }
        return list;
    }

    public void addWebModule(int i, IJettyWebModule module){
        try{
            WebAppContext context = _serverInstance.createContext(module.getDocumentBase(),module.getMemento(),module.getPath());
            if (context != null){
                _isServerDirty = true;
                firePropertyChangeEvent(__ADD_WEB_MODULE_PROPERTY,null,module);
            }
        }catch (Exception e){
            Trace.trace(Trace.SEVERE,"Error adding web module " + module.getPath(),e);
        }

    }

    /**
     * Removes a web module.
     * 
     * @param index
     *            int
     */
    public void removeWebModule(int index){
        try{
            _serverInstance.removeContext(index);
            _isServerDirty = true;
            firePropertyChangeEvent(__REMOVE_WEB_MODULE_PROPERTY,null,index);
        }catch (Exception e){
            Trace.trace(Trace.SEVERE,"Error removing module ref " + index,e);
        }
    }

    protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue){
        if (_propertyListeners == null){
            return;
        }

        PropertyChangeEvent event = new PropertyChangeEvent(this,propertyName,oldValue,newValue);
        try{
            Iterator<PropertyChangeListener> iterator = _propertyListeners.iterator();
            while (iterator.hasNext()){
                try{
                    PropertyChangeListener listener = iterator.next();
                    listener.propertyChange(event);
                }catch (Exception e){
                    Trace.trace(Trace.SEVERE,"Error firing property change event",e);
                }
            }
        }catch (Exception e){
            Trace.trace(Trace.SEVERE,"Error in property event",e);
        }
    }

    /**
     * Adds a property change listener to this server.
     * 
     * @param listener
     *            java.beans.PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener){
        if (_propertyListeners == null){
            _propertyListeners = new ArrayList<PropertyChangeListener>();
        }
        _propertyListeners.add(listener);
    }

    /**
     * Removes a property change listener from this server.
     * 
     * @param listener
     *            java.beans.PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener){
        if (_propertyListeners != null){
            _propertyListeners.remove(listener);
        }
    }

    /**
     * @see JettyConfiguration#load(IPath, IProgressMonitor)
     */
    public void load(IPath path, IPath runtimeBaseDirectory, IProgressMonitor monitor) throws CoreException{
        try{
            monitor = ProgressUtil.getMonitorFor(monitor);
            monitor.beginTask(Messages.loadingTask,5);

            Factory serverFactory = null;

            // Load config.ini
            this._startIniConfig = new StartIni(path);

            // Load other resources
            
            // Load jetty.xml files
            List<PathFileConfig> jettyXMLConfiFiles = _startIniConfig.getJettyXMLFiles();
            List<Server> servers = new ArrayList<Server>();
            Server server = null;
            File file = null;
            IPath jettyPath = null;
            if (jettyXMLConfiFiles.size() > 0){
                for (PathFileConfig jettyXMLConfig : jettyXMLConfiFiles){
                    file = jettyXMLConfig.getFile();

                    jettyPath = jettyXMLConfig.getPath();
                    serverFactory = new Factory();
                    serverFactory.setPackageName("org.eclipse.jst.server.jetty.core.internal.xml.jetty9.server");
                    server = (Server)serverFactory.loadDocument(JettyXMLConfig.getInputStream(file));
                    server.setFile(file);
                    server.setPath(jettyPath);
                    servers.add(server);
                }
            }

            WebApp webApp = null;
            PathFileConfig pathFileConfig = _startIniConfig.getWebdefaultXMLConfig();
            if (pathFileConfig != null){
                File webAppFile = pathFileConfig.getFile();
                IPath webAppPath = pathFileConfig.getPath();

                Factory webdefaultFactory = new Factory();
                webdefaultFactory.setPackageName("org.eclipse.jst.server.jetty.core.internal.xml.jetty9.server");
                webApp = (WebApp)webdefaultFactory.loadDocument(WebdefaultXMLConfig.getInputStream(webAppFile));
                webApp.setFile(webAppFile);
                webApp.setPath(webAppPath);
            }
            
            File adminPortFile = _startIniConfig.getAdminPortFile();
            String adminPort = null;
            if (adminPortFile != null && adminPortFile.exists()){
            	BufferedReader reader = new BufferedReader(new FileReader(adminPortFile));
            	adminPort = reader.readLine();
            	reader.close();
            }
            monitor.worked(1);

            _serverInstance = new ServerInstance(servers,webApp,runtimeBaseDirectory);
            if (adminPort != null)
            {
            	_serverInstance.setAdminPort(adminPort);
            }
            monitor.worked(1);

            if (monitor.isCanceled()){
                return;
            }
            monitor.done();
        }catch (Exception e){
            Trace.trace(Trace.WARNING,"Could not load Jetty v9.x configuration from " + path.toOSString() + ": " + e.getMessage());
            throw new CoreException(new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,NLS.bind(Messages.errorCouldNotLoadConfiguration,path.toOSString()),e));
        }
    }

    public void load(IFolder folder, IPath runtimeBaseDirectory, IProgressMonitor monitor) throws CoreException{
        try{
            monitor = ProgressUtil.getMonitorFor(monitor);
            monitor.beginTask(Messages.loadingTask,800);

            Factory serverFactory = null;

            // Load config.ini
            this._startIniConfig = new StartIni(folder);

            // Load jetty.xml files
            List<PathFileConfig> jettyXMLConfiFiles = _startIniConfig.getJettyXMLFiles();
            List<Server> servers = new ArrayList<Server>();
            Server server = null;
            File file = null;
            IPath jettyPath = null;
            if (jettyXMLConfiFiles.size() > 0)
            {
                for (PathFileConfig jettyXMLConfig : jettyXMLConfiFiles)
                {
                    file = jettyXMLConfig.getFile();
                    jettyPath = jettyXMLConfig.getPath();
                    serverFactory = new Factory();
                    serverFactory.setPackageName("org.eclipse.jst.server.jetty.core.internal.xml.jetty9.server");
                    server = (Server)serverFactory.loadDocument(new FileInputStream(file));
                    server.setFile(file);
                    server.setPath(jettyPath);
                    servers.add(server);
                }
            }
            monitor.worked(1);

            WebApp webApp = null;
            PathFileConfig pathFileConfig = _startIniConfig.getWebdefaultXMLConfig();
            if (pathFileConfig != null){
                File webAppFile = pathFileConfig.getFile();
                IPath webAppPath = pathFileConfig.getPath();

                Factory webdefaultFactory = new Factory();
                webdefaultFactory.setPackageName("org.eclipse.jst.server.jetty.core.internal.xml.jetty9.server");
                webApp = (WebApp)webdefaultFactory.loadDocument(new FileInputStream(webAppFile));
                webApp.setFile(webAppFile);
                webApp.setPath(webAppPath);
            }
            File adminPortFile = _startIniConfig.getAdminPortFile();
            String adminPort = null;
            if (adminPortFile != null && adminPortFile.exists()){
            	BufferedReader reader = new BufferedReader(new FileReader(adminPortFile));
            	adminPort = reader.readLine();
            	reader.close();
            }
            _serverInstance = new ServerInstance(servers,webApp,runtimeBaseDirectory);
            if (adminPort != null){
            	_serverInstance.setAdminPort(adminPort);
            }
            monitor.worked(200);

            if (monitor.isCanceled()){
                throw new Exception("Cancelled");
            }
            monitor.done();
        }catch (Exception e){
            Trace.trace(Trace.WARNING,"Could not reload Jetty v9.x configuration from: " + folder.getFullPath() + ": " + e.getMessage());
            throw new CoreException(new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,NLS.bind(Messages.errorCouldNotLoadConfiguration,folder.getFullPath()
                    .toOSString()),e));
        }
    }

    /**
     * Save the information held by this object to the given directory.
     * 
     * @param folder
     *            a folder
     * @param monitor
     *            a progress monitor
     * @throws CoreException
     */
    public void save(IFolder folder, IProgressMonitor monitor) throws CoreException{
        try{
            monitor = ProgressUtil.getMonitorFor(monitor);
            monitor.beginTask(Messages.savingTask,1200);
            if (monitor.isCanceled()){
                return;
            }

            _startIniConfig.save(folder.getFile(__START_INI),monitor);
            _serverInstance.save(folder,monitor);

            // get etc/realm.properties
            // get etc/webdefault.xml

            InputStream in = null;
            IFolder newFolder = folder;
            IPath path = null;
            String filename = null;
            List<PathFileConfig> otherConfigs = _startIniConfig.getOtherConfigs();
            for (PathFileConfig pathFileConfig : otherConfigs)
            {
                path = pathFileConfig.getPath();
                if (path.segmentCount() > 1)
                {
                    newFolder = folder.getFolder(path.removeLastSegments(1));
                    IOUtils.createFolder(newFolder,monitor);
                }
                filename = pathFileConfig.getFile().getName();
                in = new FileInputStream(pathFileConfig.getFile());
                IFile file = newFolder.getFile(filename);
                if (file.exists()){
                    file.setContents(in,true,true,ProgressUtil.getSubMonitorFor(monitor,200));                
                }else{
                    file.create(in,true,ProgressUtil.getSubMonitorFor(monitor,200));
                }
            }

            //Create Base
            /**IFolder jettyBaseFolder = folder.getFolder("/jettyBase");
            IOUtils.createFolder(jettyBaseFolder, monitor);
            
            IFolder jettyStartd = folder.getFolder(jettyBaseFolder.getName() + "/start.d");
            IOUtils.createFolder(jettyStartd, monitor);
            
            IFolder jettyWebapps = folder.getFolder(jettyBaseFolder.getName() + "/webapps");
            IOUtils.createFolder(jettyWebapps, monitor);**/

            
            
            monitor.done();
        }catch (Exception e){
            Trace.trace(Trace.SEVERE,"Could not save Jetty v9.x configuration to " + folder.toString(),e);
            throw new CoreException(new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,NLS.bind(Messages.errorCouldNotSaveConfiguration,new String[]
            { e.getLocalizedMessage() }),e));
        }
    }

    public void importFromPath(IPath path, IPath runtimeBaseDirectory, boolean isTestEnv, IProgressMonitor monitor) throws CoreException{
        load(path,runtimeBaseDirectory,monitor);

        // for test environment, remove existing contexts since a separate
        // catalina.base will be used
        if (isTestEnv){
            while (_serverInstance.removeContext(0)){
                // no-op
            }
        }
    }

    /**
     * Modify the port with the given id.
     * 
     * @param id
     *            java.lang.String
     * @param port
     *            int
     */
    public void modifyServerPort(String id, int port){
        try{
            if ("server".equals(id)){
            	_serverInstance.setAdminPort(port+"");
                _isServerDirty = true;
                firePropertyChangeEvent(__MODIFY_PORT_PROPERTY,id, Integer.valueOf(port));
                return;
            }

			int connNum = Integer.parseInt(id);
			List<Connector> connectors = _serverInstance.getConnectors();
			Connector connector = connectors.get(connNum);
			connector.setPort(port + "");
			_isServerDirty = true;
			firePropertyChangeEvent(__MODIFY_PORT_PROPERTY, id, new Integer(port));            
        }catch (Exception e){
            Trace.trace(Trace.SEVERE,"Error modifying server port " + id,e);
        }
    }

    /**
     * Change a web module.
     * 
     * @param index
     *            int
     * @param docBase
     *            java.lang.String
     * @param path
     *            java.lang.String
     * @param reloadable
     *            boolean
     */
    public void modifyWebModule(int index, String docBase, String path, boolean reloadable){
        try{
            WebAppContext context = _serverInstance.getContext(index);
            if (context != null){
                context.setContextPath(path);
                context.save();
                _isServerDirty = true;
                WebModule module = new WebModule(path,docBase,null,reloadable);
                firePropertyChangeEvent(__MODIFY_WEB_MODULE_PROPERTY, Integer.valueOf(index),module);
            }
        }catch (Exception e){
            Trace.trace(Trace.SEVERE,"Error modifying web module " + index,e);
        }
    }

}
