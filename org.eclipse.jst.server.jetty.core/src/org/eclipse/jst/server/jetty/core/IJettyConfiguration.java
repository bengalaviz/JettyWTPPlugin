/*******************************************************************************
 * Copyright (c) 2010 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - Initial API and implementation 
 *******************************************************************************/
package org.eclipse.jst.server.jetty.core;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.jetty.core.internal.IJettyWebModule;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerPort;

public interface IJettyConfiguration
{

    public static final String __NAME_PROPERTY = "name";
    public static final String __PORT_PROPERTY = "port";
    public static final String __MODIFY_PORT_PROPERTY = "modifyPort";

    public static final String __MODIFY_WEB_MODULE_PROPERTY = "modifyWebModule";
    public static final String __ADD_WEB_MODULE_PROPERTY = "addWebModule";
    public static final String __REMOVE_WEB_MODULE_PROPERTY = "removeWebModule";

    /**
     * Returns a list of ServerPorts that this configuration uses.
     * 
     * @return the server ports
     */
    Collection<ServerPort> getServerPorts();

    /**
     * Returns the main server port.
     * 
     * @return ServerPort
     */
    ServerPort getMainPort();
    
    /**
     * Returns the admin port
     * 
     * @return ServerPort
     */
    ServerPort getAdminPort();

    /**
     * 
     * @param path
     * @param monitor
     * @throws CoreException
     */
    void load(IPath path, IPath runtimeBaseDirectory, IProgressMonitor monitor) throws CoreException;

    /**
     * 
     * @param path
     * @param monitor
     * @throws CoreException
     */
    void load(IFolder folder, IPath runtimeBaseDirectory, IProgressMonitor monitor) throws CoreException;

    /**
     * Save the information held by this object to the given directory.
     * 
     * @param folder
     *            a folder
     * @param monitor
     *            a progress monitor
     * @throws CoreException
     */
    void save(IFolder folder, IProgressMonitor monitor) throws CoreException;

    /**
     * Return a list of the web modules in this server.
     * 
     * @return the web modules
     */
    public List<WebModule> getWebModules();

    void addWebModule(int i, IJettyWebModule module);

    void removeWebModule(int i);

    String getWebModuleURL(IModule module);

    String getDocBasePrefix();

    void importFromPath(IPath path, IPath runtimeBaseDirectory, boolean isTestEnv, IProgressMonitor monitor) throws CoreException;

    IStatus cleanupServer(IPath confDir, IPath installDir, IProgressMonitor monitor);

    IStatus backupAndPublish(IPath confDir, boolean b, IProgressMonitor monitor);

    IStatus localizeConfiguration(IPath confDir, IPath serverDeployDirectory, IJettyServer jettyServer, IProgressMonitor subMonitorFor);

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);
}
