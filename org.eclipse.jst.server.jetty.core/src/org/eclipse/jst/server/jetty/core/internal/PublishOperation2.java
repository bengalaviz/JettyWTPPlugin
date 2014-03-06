/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - Jetty packages
 *******************************************************************************/
package org.eclipse.jst.server.jetty.core.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jst.server.core.IJ2EEModule;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.util.PublishHelper;

/**
 * Jetty publish helper.
 */
public class PublishOperation2 extends PublishOperation
{
    protected JettyServerBehaviour _server;
    protected IModule[] _module;
    protected int _kind;
    protected int _deltaKind;
    private PublishHelper _helper;

    /**
     * Construct the operation object to publish the specified module to the specified server.
     * 
     * @param server
     *            server to which the module will be published
     * @param kind
     *            kind of publish
     * @param module
     *            module to publish
     * @param deltaKind
     *            kind of change
     */
    public PublishOperation2(JettyServerBehaviour server, int kind, IModule[] module, int deltaKind)
    {
        super("Publish to server","Publish Web module to Jetty server");
        
        this._server = server;
        this._module = module;
        this._kind = kind;
        this._deltaKind = deltaKind;
        IPath base = server.getRuntimeBaseDirectory();
        if (base != null)
        {
            _helper = new PublishHelper(base.append("temp").toFile());
        }
        else
        {
            // We are doomed without a base directory. However, allow the catastrophe
            // to occur elsewhere and hope for a useful error message.
            _helper = new PublishHelper(null);
        }
    }

    /**
     * @see PublishOperation#getOrder()
     */
    public int getOrder()
    {
        return 0;
    }

    /**
     * @see PublishOperation#getKind()
     */
    public int getKind()
    {
        return REQUIRED;
    }

    /**
     * @see PublishOperation#execute(IProgressMonitor, IAdaptable)
     */
    public void execute(IProgressMonitor monitor, IAdaptable info) throws CoreException
    {
        List<IStatus> status = new ArrayList<IStatus>();
        // If parent web module
        if (_module.length == 1)
        {
            publishDir(_module[0],status,monitor);
        }
        // Else a child module
        else
        {
            Properties p = _server.loadModulePublishLocations();

            // Try to determine the URI for the child module
            IWebModule webModule = (IWebModule)_module[0].loadAdapter(IWebModule.class,monitor);
            String childURI = null;
            if (webModule != null)
            {
                childURI = webModule.getURI(_module[1]);
            }
            // Try to determine if child is binary
            IJ2EEModule childModule = (IJ2EEModule)_module[1].loadAdapter(IJ2EEModule.class,monitor);
            boolean isBinary = false;
            if (childModule != null)
            {
                isBinary = childModule.isBinary();
            }

            if (isBinary)
            {
                publishArchiveModule(childURI,p,status,monitor);
            }
            else
            {
                publishJar(childURI,p,status,monitor);
            }
            _server.saveModulePublishLocations(p);
        }
        throwException(status);
        _server.setModulePublishState2(_module,IServer.PUBLISH_STATE_NONE);
    }

    private void publishDir(IModule module2, List status, IProgressMonitor monitor) throws CoreException
    {
        IPath path = _server.getModuleDeployDirectory(module2);

        // Remove if requested or if previously published and are now serving without publishing
        if (_kind == IServer.PUBLISH_CLEAN || _deltaKind == ServerBehaviourDelegate.REMOVED || _server.getJettyServer().isServeModulesWithoutPublish())
        {
            File f = path.toFile();
            if (f.exists())
            {
                IStatus[] stat = PublishHelper.deleteDirectory(f,monitor);
                addArrayToList(status,stat);
            }

            if (_deltaKind == ServerBehaviourDelegate.REMOVED || _server.getJettyServer().isServeModulesWithoutPublish())
                return;
        }

        if (_kind == IServer.PUBLISH_CLEAN || _kind == IServer.PUBLISH_FULL)
        {
            IModuleResource[] mr = _server.getResources(_module);
            IStatus[] stat = _helper.publishFull(mr,path,monitor);
            addArrayToList(status,stat);
            return;
        }

        IModuleResourceDelta[] delta = _server.getPublishedResourceDelta(_module);

        int size = delta.length;
        for (int i = 0; i < size; i++)
        {
            IStatus[] stat = _helper.publishDelta(delta[i],path,monitor);
            addArrayToList(status,stat);
        }
    }

    private void publishJar(String jarURI, Properties p, List status, IProgressMonitor monitor) throws CoreException
    {
        IPath path = _server.getModuleDeployDirectory(_module[0]);
        boolean moving = false;
        // Get URI used for previous publish, if known
        String oldURI = (String)p.get(_module[1].getId());
        if (oldURI != null)
        {
            // If old URI found, detect if jar is moving or changing its name
            if (jarURI != null)
            {
                moving = !oldURI.equals(jarURI);
            }
        }
        // If we don't have a jar URI, make a guess so we have one if we need it
        if (jarURI == null)
        {
            jarURI = "WEB-INF/lib/" + _module[1].getName() + ".jar";
        }
        IPath jarPath = path.append(jarURI);
        // Make our best determination of the path to the old jar
        IPath oldJarPath = jarPath;
        if (oldURI != null)
        {
            oldJarPath = path.append(oldURI);
        }
        // Establish the destination directory
        path = jarPath.removeLastSegments(1);

        // Remove if requested or if previously published and are now serving without publishing
        if (moving || _kind == IServer.PUBLISH_CLEAN || _deltaKind == ServerBehaviourDelegate.REMOVED || _server.getJettyServer().isServeModulesWithoutPublish())
        {
            File file = oldJarPath.toFile();
            if (file.exists())
                file.delete();
            p.remove(_module[1].getId());

            if (_deltaKind == ServerBehaviourDelegate.REMOVED || _server.getJettyServer().isServeModulesWithoutPublish())
                return;
        }
        if (!moving && _kind != IServer.PUBLISH_CLEAN && _kind != IServer.PUBLISH_FULL)
        {
            // avoid changes if no changes to module since last publish
            IModuleResourceDelta[] delta = _server.getPublishedResourceDelta(_module);
            if (delta == null || delta.length == 0)
                return;
        }

        // make directory if it doesn't exist
        if (!path.toFile().exists())
            path.toFile().mkdirs();

        IModuleResource[] mr = _server.getResources(_module);
        IStatus[] stat = _helper.publishZip(mr,jarPath,monitor);
        addArrayToList(status,stat);
        p.put(_module[1].getId(),jarURI);
    }

    private void publishArchiveModule(String jarURI, Properties p, List status, IProgressMonitor monitor)
    {
        IPath path = _server.getModuleDeployDirectory(_module[0]);
        boolean moving = false;
        // Get URI used for previous publish, if known
        String oldURI = (String)p.get(_module[1].getId());
        if (oldURI != null)
        {
            // If old URI found, detect if jar is moving or changing its name
            if (jarURI != null)
            {
                moving = !oldURI.equals(jarURI);
            }
        }
        // If we don't have a jar URI, make a guess so we have one if we need it
        if (jarURI == null)
        {
            jarURI = "WEB-INF/lib/" + _module[1].getName();
        }
        IPath jarPath = path.append(jarURI);
        // Make our best determination of the path to the old jar
        IPath oldJarPath = jarPath;
        if (oldURI != null)
        {
            oldJarPath = path.append(oldURI);
        }
        // Establish the destination directory
        path = jarPath.removeLastSegments(1);

        // Remove if requested or if previously published and are now serving without publishing
        if (moving || _kind == IServer.PUBLISH_CLEAN || _deltaKind == ServerBehaviourDelegate.REMOVED || _server.getJettyServer().isServeModulesWithoutPublish())
        {
            File file = oldJarPath.toFile();
            if (file.exists())
            {
                file.delete();
            }
            p.remove(_module[1].getId());

            if (_deltaKind == ServerBehaviourDelegate.REMOVED || _server.getJettyServer().isServeModulesWithoutPublish())
                return;
        }
        if (!moving && _kind != IServer.PUBLISH_CLEAN && _kind != IServer.PUBLISH_FULL)
        {
            // avoid changes if no changes to module since last publish
            IModuleResourceDelta[] delta = _server.getPublishedResourceDelta(_module);
            if (delta == null || delta.length == 0)
                return;
        }

        // make directory if it doesn't exist
        if (!path.toFile().exists())
            path.toFile().mkdirs();

        IModuleResource[] mr = _server.getResources(_module);
        IStatus[] stat = _helper.publishToPath(mr,jarPath,monitor);
        addArrayToList(status,stat);
        p.put(_module[1].getId(),jarURI);
    }

    /**
     * Utility method to throw a CoreException based on the contents of a list of error and warning status.
     * 
     * @param status
     *            a List containing error and warning IStatus
     * @throws CoreException
     */
    protected static void throwException(List<IStatus> status) throws CoreException
    {
        if (status == null || status.size() == 0)
            return;

        if (status.size() == 1)
        {
            IStatus status2 = (IStatus)status.get(0);
            throw new CoreException(status2);
        }
        IStatus[] children = new IStatus[status.size()];
        status.toArray(children);
        String message = Messages.errorPublish;
        MultiStatus status2 = new MultiStatus(JettyPlugin.PLUGIN_ID,0,children,message,null);
        throw new CoreException(status2);
    }

    protected static void addArrayToList(List<IStatus> list, IStatus[] a)
    {
        if (list == null || a == null || a.length == 0)
            return;

        int size = a.length;
        for (int i = 0; i < size; i++)
            list.add(a[i]);
    }
}
