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
package org.eclipse.jst.server.jetty.core.internal;

import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.wst.server.core.IModule;

public interface IJettyVersionHandler
{

    /**
     * Verifies if the specified path points to a a Jetty installation of this version.
     * 
     * @param installPath
     *            an installation path
     * @return OK status if a valid installation exists at the location. If not valid, the IStatus contains an indication of why.
     */
    public IStatus verifyInstallPath(IPath installPath);

    /**
     * Gets the startup class for the Jetty server.
     * 
     * @return server startup class
     */
    public String getRuntimeClass();

    /**
     * Gets the startup classpath for the Jetty server.
     * 
     * @param installPath
     *            an installation path
     * @return list of classpath entries required to start the Jetty server.
     */
    public Collection<IRuntimeClasspathEntry> getRuntimeClasspath(IPath installPath, IPath configPath);

    /**
     * Return the program's runtime arguments.
     * 
     * @param configPath
     *            a config path
     * @param debug
     *            <code>true</code> if debug mode is on
     * @param starting
     *            <code>true</code> if the server is starting
     * @return a string array of program arguments
     */
    public String[] getRuntimeProgramArguments(IPath configPath, boolean debug, boolean starting);

    /**
     * Arguments that should not appear in the runtime arguments based on the specified configuration.
     * 
     * @param debug
     *            <code>true</code> if debug mode is on
     * @param starting
     *            <code>true</code> if the server is starting
     * @return array of excluded arguments
     */
    public String[] getExcludedRuntimeProgramArguments(boolean debug, boolean starting);

    /**
     * Gets the startup VM arguments for the Jetty server.
     * 
     * @param installPath
     *            installation path for the server
     * @param configPath
     *            configuration path for the server
     * @param deployPath
     *            deploy path for the server
     * @param isTestEnv
     *            test environment flag
     * @return array of VM arguments for starting the server
     */
    public String[] getRuntimeVMArguments(IPath installPath, IPath configPath, IPath deployPath, int mainPort, int adminPort, boolean isTestEnv);

    /**
     * Gets the contents of the Java policy file for the Jetty server.
     * 
     * @param configPath
     *            path to configuration
     * @return contents of Java policy file in the configuration
     */
    public String getRuntimePolicyFile(IPath configPath);

    /**
     * Returns the runtime base path for relative paths in the server configuration.
     * 
     * @param server
     *            JettyServer instance from which to determine the base path.
     * @return path to Jetty instance directory
     */
    public IPath getRuntimeBaseDirectory(JettyServer server);

    /**
     * 
     * @param path
     * @param vmInstall
     * @return
     */
    public IStatus validate(IPath path, IVMInstall vmInstall);

    /**
     * Returns true if the given project is supported by this server, and false otherwise.
     * 
     * @param module
     *            a web module
     * @return the status
     */
    public IStatus canAddModule(IModule module);

    /**
     * Returns true if this server supports serving modules without publishing.
     * 
     * @return true if serving modules without publishing is supported
     */
    public boolean supportsServeModulesWithoutPublish();

    public IStatus prepareRuntimeDirectory(IPath confDir);
}
