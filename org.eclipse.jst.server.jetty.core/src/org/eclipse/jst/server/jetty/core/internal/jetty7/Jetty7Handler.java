/*******************************************************************************
sd * Copyright (c) 2010 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - Initial API and implementation 
 *******************************************************************************/
package org.eclipse.jst.server.jetty.core.internal.jetty7;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.jst.server.jetty.core.internal.IJettyVersionHandler;
import org.eclipse.jst.server.jetty.core.internal.JettyHandler;
import org.eclipse.jst.server.jetty.core.internal.JettyServer;
import org.eclipse.jst.server.jetty.core.internal.Messages;
import org.eclipse.jst.server.jetty.core.internal.util.JettyVersionHelper;
import org.eclipse.wst.server.core.IModule;

public class Jetty7Handler extends JettyHandler
{

    protected static final IStatus __START_JAR_REQUIRED_STATUS = new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,    
            Messages.startJarRequiredInstallDirStatus,null);

    public IStatus verifyInstallPath(IPath installPath)
    {
        IStatus result = JettyVersionHelper.checkJettyVersion(installPath);
        
        if (result.getSeverity() == IStatus.CANCEL)
        {
            // TODO : search in a folder.
            return __START_JAR_REQUIRED_STATUS;
        }

        return result;
    }

    public IStatus validate(IPath path, IVMInstall vmInstall)
    {
        // TODO : validate JVM
        return null;
    }

    /**
     * @see IJettyVersionHandler#canAddModule(IModule)
     */
    public IStatus canAddModule(IModule module)
    {
        String version = module.getModuleType().getVersion();
        
        if ("2.2".equals(version) || 
            "2.3".equals(version) || 
            "2.4".equals(version) || 
            "2.5".equals(version))
        {
            return Status.OK_STATUS;
        }

        return new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,Messages.errorSpec70,null);
    }

    public IPath getRuntimeBaseDirectory(JettyServer server)
    {
        return JettyVersionHelper.getStandardBaseDirectory(server);
    }

    /**
     * @see IJettyVersionHandler#getRuntimeVMArguments(IPath, IPath, IPath, boolean)
     */
    public String[] getRuntimeVMArguments(IPath installPath, IPath configPath, IPath deployPath, int mainPort, int adminPort, boolean isTestEnv)
    {
        return JettyVersionHelper.getJettyVMArguments(installPath,configPath,deployPath,getEndorsedDirectories(installPath), mainPort, adminPort, isTestEnv);
    }

    public String getEndorsedDirectories(IPath installPath)
    {
        return installPath.append("endorsed").toOSString();
    }

    public String getRuntimePolicyFile(IPath configPath)
    {
        return configPath.append("lib").append("policy").append("jetty.policy").toOSString();
    }

    public String[] getRuntimeProgramArguments(IPath configPath, boolean debug, boolean starting)
    {
        return JettyVersionHelper.getJettyProgramArguments(configPath,debug,starting);
    }

    public String[] getExcludedRuntimeProgramArguments(boolean debug, boolean starting)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean supportsServeModulesWithoutPublish()
    {
        return true;
    }
}
