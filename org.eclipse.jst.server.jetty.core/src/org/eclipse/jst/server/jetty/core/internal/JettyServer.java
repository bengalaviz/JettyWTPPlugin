/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Angelo Zerr <angelo.zerr@gmail.com> - Jetty packages
 *******************************************************************************/
package org.eclipse.jst.server.jetty.core.internal;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.FacetUtil;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.core.internal.J2EEUtil;
import org.eclipse.jst.server.jetty.core.IJettyConfiguration;
import org.eclipse.jst.server.jetty.core.IJettyServer;
import org.eclipse.jst.server.jetty.core.IJettyServerWorkingCopy;
import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.jst.server.jetty.core.WebModule;
import org.eclipse.jst.server.jetty.core.internal.util.IOUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.ServerDelegate;

public class JettyServer extends ServerDelegate implements IJettyServer, IJettyServerWorkingCopy
{

    private static final ServerPort[] _EMPTY_SERVER_PORTS = new ServerPort[0];
    public static final String __PROPERTY_SECURE = "secure";
    public static final String __PROPERTY_DEBUG = "debug";

    private static final String __JST_WEB_MODULETYPE = "jst.web";

    private static final IModule[] __EMPTY_MODULES = new IModule[0];
    protected transient IJettyConfiguration _configuration;
    protected transient IJettyVersionHandler _versionHandler;

    /**
     * JettyServer.
     */
    public JettyServer()
    {
        super();
    }

    /**
     * Get the Jetty runtime for this server.
     * 
     * @return Jetty runtime for this server
     */
    public JettyRuntime getJettyRuntime()
    {
        if (getServer().getRuntime() == null)
        {
            return null;
        }

        return (JettyRuntime)getServer().getRuntime().loadAdapter(JettyRuntime.class,null);
    }

    /**
     * Gets the Jetty version handler for this server.
     * 
     * @return version handler for this server
     */
    public IJettyVersionHandler getJettyVersionHandler()
    {
        if (_versionHandler == null)
        {
            if (getServer().getRuntime() == null || getJettyRuntime() == null)
            {
                return null;
            }

            _versionHandler = getJettyRuntime().getVersionHandler();
        }
        return _versionHandler;
    }

    public IJettyConfiguration getJettyConfiguration() throws CoreException
    {
        if (_configuration == null)
        {
            IFolder folder = getServer().getServerConfiguration();
            if (folder == null || !folder.exists())
            {
                String path = null;
                if (folder != null)
                {
                    path = folder.getFullPath().toOSString();
                    IProject project = folder.getProject();
                    if (project != null && project.exists() && !project.isOpen())
                        throw new CoreException(new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,NLS.bind(Messages.errorConfigurationProjectClosed,path,
                                project.getName()),null));
                }
                throw new CoreException(new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,NLS.bind(Messages.errorNoConfiguration,path),null));
            }

            String id = getServer().getServerType().getId();
            _configuration = JettyPlugin.getJettyConfiguration(id,folder);
            try
            {
                _configuration.load(folder,getRuntimeBaseDirectory(),null);
            }
            catch (CoreException ce)
            {
                // ignore
                _configuration = null;
                throw ce;
            }
        }
        return _configuration;
    }

    @Override
    public void configurationChanged()
    {
        _configuration = null;
    }

    @Override
    public void importRuntimeConfiguration(IRuntime runtime, IProgressMonitor monitor) throws CoreException
    {
        if (runtime == null)
        {
            _configuration = null;
            return;
        }
        IPath path = runtime.getLocation();
        String id = getServer().getServerType().getId();
        IPath runtimeBaseDirectory = getRuntimeBaseDirectory();
        IFolder folder = getServer().getServerConfiguration();
        _configuration = JettyPlugin.getJettyConfiguration(id,folder);
        try
        {
        	_configuration.importFromPath(path,runtimeBaseDirectory,isTestEnvironment(),monitor);
        	if (runtime.getRuntimeType().getName().equals("Jetty v9.1") && folder != null){
        		//import files from Runtime path
        		IPath runtimeLocation = runtime.getLocation();
        		IFolder runtimeLocationFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(runtimeLocation);
        		
        		IWorkspace workspace = ResourcesPlugin.getWorkspace();
                File serverPath = workspace.getRoot().getLocation().toFile();
        		String jettyBase = serverPath.getAbsolutePath() + folder.getFolder("/jettyBase").getFullPath().toOSString();
        		IFolder jettyBaseFolder = folder.getFolder("/jettyBase");
        		//if (jettyBaseFolder.exists()){
        			jettyBaseFolder.delete(true, monitor);
        			jettyBaseFolder.create(false, true, monitor);
        		//}
                
                
        		System.setProperty("jetty.base", jettyBase);
        		System.setProperty("jetty.home", runtimeLocation.toOSString());
        		try{
        			File file = new File(runtimeLocation.toOSString() + "/start.jar");
        			URL url = file.toURL();
        			URL[] urls = new URL[]{url};
	        		URLClassLoader urlcl = URLClassLoader.newInstance(urls);
	                Class<?> jettyClass = urlcl.loadClass("org.eclipse.jetty.start.Main");
	                Constructor constructor = jettyClass.getDeclaredConstructor();
	                constructor.setAccessible(true);
	                Object jettyObj = constructor.newInstance();
	                Method method = jettyClass.getDeclaredMethod("main", new Class[]{String[].class});
	                String[] params = {"--add-to-start=http,deploy jetty.home=\"" + runtimeLocation.toOSString() + "\" jetty.base=\"" + jettyBase + "\""};
	                method.invoke(jettyObj, (Object) params);
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}else{
        		
        	}
        }
        catch (CoreException ce)
        {
            // ignore
            _configuration = null;
            throw ce;
        }
    }

    @Override
    public void saveConfiguration(IProgressMonitor monitor) throws CoreException
    {
        if (_configuration == null)
            return;
        _configuration.save(getServer().getServerConfiguration(),monitor);
    }

    @Override
    public ServerPort[] getServerPorts()
    {
        if (getServer().getServerConfiguration() == null)
            return _EMPTY_SERVER_PORTS;

        try
        {
            Collection<ServerPort> list = getJettyConfiguration().getServerPorts();
            ServerPort[] sp = new ServerPort[list.size()];
            list.toArray(sp);
            return sp;
        }
        catch (Exception e)
        {
            return _EMPTY_SERVER_PORTS;
        }
    }

    @Override
    public void setDefaults(IProgressMonitor monitor)
    {
        setTestEnvironment(true);
        setAttribute("auto-publish-setting",2);
        setAttribute("auto-publish-time",1);
        setDeployDirectory(DEFAULT_DEPLOYDIR);
    }

    /**
     * Sets this process to debug mode. This feature only works with Jetty v4.0.
     * 
     * @param b
     *            boolean
     */
    public void setDebug(boolean b)
    {
        setAttribute(__PROPERTY_DEBUG,b);
    }

    /**
     * Sets this process to secure mode.
     * 
     * @param b
     *            boolean
     */
    public void setSecure(boolean b)
    {
        setAttribute(__PROPERTY_SECURE,b);
    }

    /**
     * Sets this server to test environment mode.
     * 
     * @param b
     *            boolean
     */
    public void setTestEnvironment(boolean b)
    {
        setAttribute(PROPERTY_TEST_ENVIRONMENT,b);
    }

    /**
     * @see IJettyServerWorkingCopy#setInstanceDirectory(String)
     */
    public void setInstanceDirectory(String instanceDir)
    {
        setAttribute(PROPERTY_INSTANCE_DIR,instanceDir);
    }

    /**
     * @see IJettyServerWorkingCopy#setDeployDirectory(String)
     */
    public void setDeployDirectory(String deployDir)
    {
        // Remove attribute if setting to legacy value assumed in prior versions
        // of WTP.
        // Allowing values that differ only in case is asking for more trouble
        // that it is worth.
        if (LEGACY_DEPLOYDIR.equalsIgnoreCase(deployDir))
            setAttribute(PROPERTY_DEPLOY_DIR,(String)null);
        else
            setAttribute(PROPERTY_DEPLOY_DIR,deployDir);
    }

    /**
     * Gets the base directory where the server instance runs. This path can vary depending on the configuration. Null may be returned if a runtime hasn't been
     * specified for the server.
     * 
     * @return path to base directory for the server or null if runtime hasn't been specified.
     */
    public IPath getRuntimeBaseDirectory()
    {
        IJettyVersionHandler tvh = getJettyVersionHandler();
        if (tvh != null)
            return tvh.getRuntimeBaseDirectory(this);
        return null;
    }

    /**
     * Gets the directory to which modules should be deployed for this server.
     * 
     * @return full path to deployment directory for the server
     */
    public IPath getServerDeployDirectory()
    {
        String deployDir = getDeployDirectory();
        IPath deployPath = new Path(deployDir);
        if (!deployPath.isAbsolute())
        {
            IPath base = getRuntimeBaseDirectory();
            deployPath = base.append(deployPath);
        }
        return deployPath;
    }

    /**
     * Returns true if the given project is supported by this server, and false otherwise.
     * 
     * @param add
     *            modules
     * @param remove
     *            modules
     * @return the status
     */
    @Override
    public IStatus canModifyModules(IModule[] add, IModule[] remove)
    {
        if (add != null)
        {
            int size = add.length;
            for (int i = 0; i < size; i++)
            {
                IModule module = add[i];
                if (!__JST_WEB_MODULETYPE.equals(module.getModuleType().getId()))
                    return new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,Messages.errorWebModulesOnly,null);

                if (getJettyVersionHandler() == null)
                    return new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,Messages.errorNoRuntime,null);

                IStatus status = getJettyVersionHandler().canAddModule(module);
                if (status != null && !status.isOK())
                    return status;

                if (module.getProject() != null)
                {
                    status = FacetUtil.verifyFacets(module.getProject(),getServer());
                    if (status != null && !status.isOK())
                        return status;
                }
            }
        }

        return Status.OK_STATUS;
    }

    /**
     * @see ServerDelegate#modifyModules(IModule[], IModule[], IProgressMonitor)
     */
    @Override
    public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException
    {
        IStatus status = canModifyModules(add,remove);
        if (status == null || !status.isOK())
            throw new CoreException(status);

        IJettyConfiguration config = getJettyConfiguration();

        if (add != null)
        {
            int size = add.length;
            for (int i = 0; i < size; i++)
            {
                IModule module3 = add[i];
                IWebModule module = (IWebModule)module3.loadAdapter(IWebModule.class,monitor);
                String contextRoot = module.getContextRoot();
                if (contextRoot != null && !contextRoot.startsWith("/") && contextRoot.length() > 0)
                    contextRoot = "/" + contextRoot;
                String docBase = config.getDocBasePrefix() + module3.getName();
                WebModule module2 = new WebModule(contextRoot,docBase,module3.getId(),true);
                config.addWebModule(-1,module2);
            }
        }

        if (remove != null)
        {
            int size2 = remove.length;
            for (int j = 0; j < size2; j++)
            {
                IModule module3 = remove[j];
                String memento = module3.getId();
                List<WebModule> modules = getJettyConfiguration().getWebModules();
                int size = modules.size();
                for (int i = 0; i < size; i++)
                {
                    WebModule module = (WebModule)modules.get(i);
                    if (memento.equals(module.getMemento()))
                        config.removeWebModule(i);
                }
            }
        }
        // config.save(config.getFolder(), monitor);
    }

    /**
     * Returns the child module(s) of this module.
     * 
     * @param module
     *            module from which to get child module(s)
     * @return array of child module(s)
     */
    @Override
    public IModule[] getChildModules(IModule[] module)
    {
        if (module == null)
            return null;

        IModuleType moduleType = module[0].getModuleType();

        if (module.length == 1 && moduleType != null && __JST_WEB_MODULETYPE.equals(moduleType.getId()))
        {
            IWebModule webModule = (IWebModule)module[0].loadAdapter(IWebModule.class,null);
            if (webModule != null)
            {
                IModule[] modules = webModule.getModules();
                // if (modules != null)
                // System.out.println(modules.length);
                return modules;
            }
        }
        return __EMPTY_MODULES;
    }

    /**
     * Returns the root module(s) of this module.
     * 
     * @param module
     *            module from which to get the root module
     * @return root module
     * @throws CoreException
     */
    @Override
    public IModule[] getRootModules(IModule module) throws CoreException
    {
        if (__JST_WEB_MODULETYPE.equals(module.getModuleType().getId()))
        {
            IStatus status = canModifyModules(new IModule[]
            { module },null);
            if (status == null || !status.isOK())
                throw new CoreException(status);
            return new IModule[]
            { module };
        }

        return J2EEUtil.getWebModules(module,null);
    }

    /**
     * Return the root URL of this module.
     * 
     * @param module
     *            org.eclipse.wst.server.core.model.IModule
     * @return java.net.URL IJettyServerWorkingCopy.java
     */

    public URL getModuleRootURL(IModule module)
    {
        try
        {
            if (module == null)
                return null;

            IJettyConfiguration config = getJettyConfiguration();
            if (config == null)
                return null;

            String url = "http://" + getServer().getHost();
            int port = config.getMainPort().getPort();
            port = ServerUtil.getMonitoredPort(getServer(),port,"web");
            if (port != 80)
                url += ":" + port;

            url += config.getWebModuleURL(module);

            if (!url.endsWith("/"))
                url += "/";

            return new URL(url);
        }
        catch (Exception e)
        {
            Trace.trace(Trace.SEVERE,"Could not get root URL",e);
            return null;
        }
    }

    /**
     * Returns true if the process is set to run in debug mode.
     * 
     * @return boolean
     */
    public boolean isDebug()
    {
        return getAttribute(__PROPERTY_DEBUG,false);
    }

    /**
     * Returns true if this is a test (run code out of the workbench) server.
     * 
     * @return boolean
     */
    public boolean isTestEnvironment()
    {
        return getAttribute(PROPERTY_TEST_ENVIRONMENT,false);
    }

    /**
     * Returns true if the process is set to run in secure mode.
     * 
     * @return boolean
     */
    public boolean isSecure()
    {
        return getAttribute(__PROPERTY_SECURE,false);
    }

    public String getInstanceDirectory()
    {
        return getAttribute(PROPERTY_INSTANCE_DIR,(String)null);
    }

    /**
     * @see IJettyServer#getDeployDirectory()
     */
    public String getDeployDirectory()
    {
        // Default to value used by prior WTP versions
        return getAttribute(PROPERTY_DEPLOY_DIR,LEGACY_DEPLOYDIR);
    }

    /**
     * Returns true if modules should be served without publishing.
     * 
     * @return boolean
     */
    public boolean isServeModulesWithoutPublish()
    {
        // If feature is supported, return current setting
        IJettyVersionHandler tvh = getJettyVersionHandler();
        if (tvh != null && tvh.supportsServeModulesWithoutPublish())
            return getAttribute(PROPERTY_SERVE_MODULES_WITHOUT_PUBLISH,false);
        return false;
    }

    public IJettyConfiguration getServerConfiguration() throws CoreException
    {
        return getJettyConfiguration();
    }
}
