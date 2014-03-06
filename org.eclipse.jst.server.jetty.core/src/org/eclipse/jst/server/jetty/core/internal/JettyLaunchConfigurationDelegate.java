/*******************************************************************************
 * Copyright (c) 2003, 2009 IBM Corporation and others.
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
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jst.server.core.ServerProfilerDelegate;
import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.jst.server.jetty.core.internal.JettyServerBehaviour;
import org.eclipse.jst.server.jetty.core.internal.Trace;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;

public class JettyLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate
{

    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException
    {
        IServer server = ServerUtil.getServer(configuration);
        if (server == null)
        {
            Trace.trace(Trace.FINEST,"Launch configuration could not find server");
            // throw CoreException();
            return;
        }

        if (server.shouldPublish() && ServerCore.isAutoPublishing())
            server.publish(IServer.PUBLISH_INCREMENTAL,monitor);

        JettyServerBehaviour jettyServer = (JettyServerBehaviour)server.loadAdapter(JettyServerBehaviour.class,null);

        String mainTypeName = jettyServer.getRuntimeClass();

        IVMInstall vm = verifyVMInstall(configuration);

        IVMRunner runner = vm.getVMRunner(mode);
        if (runner == null)
            runner = vm.getVMRunner(ILaunchManager.RUN_MODE);

        File workingDir = verifyWorkingDirectory(configuration);
        String workingDirName = null;
        if (workingDir != null)
            workingDirName = workingDir.getAbsolutePath();

        // Program & VM args
        String pgmArgs = getProgramArguments(configuration);
        String vmArgs = getVMArguments(configuration);
        String[] envp = getEnvironment(configuration);

        ExecutionArguments execArgs = new ExecutionArguments(vmArgs,pgmArgs);

        // VM-specific attributes
        Map vmAttributesMap = getVMSpecificAttributesMap(configuration);

        // Classpath
        String[] classpath = getClasspath(configuration);

        // Create VM config
        VMRunnerConfiguration runConfig = new VMRunnerConfiguration(mainTypeName,classpath);
        runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());
        runConfig.setVMArguments(execArgs.getVMArgumentsArray());
        runConfig.setWorkingDirectory(workingDirName);
        runConfig.setEnvironment(envp);
        runConfig.setVMSpecificAttributesMap(vmAttributesMap);

        // Bootpath
        String[] bootpath = getBootpath(configuration);
        if (bootpath != null && bootpath.length > 0)
            runConfig.setBootClassPath(bootpath);

        setDefaultSourceLocator(launch,configuration);

        if (ILaunchManager.PROFILE_MODE.equals(mode))
        {
            try
            {
                ServerProfilerDelegate.configureProfiling(launch,vm,runConfig,monitor);
            }
            catch (CoreException ce)
            {
                jettyServer.stopImpl();
                throw ce;
            }
        }

        // Launch the configuration
        jettyServer.setupLaunch(launch,mode,monitor);
        try
        {
            runner.run(runConfig,launch,monitor);
            jettyServer.addProcessListener(launch.getProcesses()[0]);
        }
        catch (Exception e)
        {
            // Ensure we don't continue to think the server is starting
            jettyServer.stopImpl();
        }
    }

}
