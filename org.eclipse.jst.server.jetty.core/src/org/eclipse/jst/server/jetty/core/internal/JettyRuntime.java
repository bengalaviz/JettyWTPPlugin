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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jst.server.jetty.core.IJettyRuntimeWorkingCopy;
import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.jst.server.jetty.core.internal.util.StringUtils;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.model.RuntimeDelegate;

/**
 * Jetty Runtime.
 * 
 */
public class JettyRuntime extends RuntimeDelegate implements IJettyRuntime, IJettyRuntimeWorkingCopy
{

    // private static final String TOOLS_JAR_PATH = "lib" + File.separator + "tools.jar";
    private static final String __JAVAC_MAIN = "com.sun.tools.javac.Main";
    private static final String __CLASS_DETECTOR = "org.eclipse.jst.server.jetty.core.internal.ClassDetector";

    protected static final String __PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";
    protected static final String __PROP_VM_INSTALL_ID = "vm-install-id";

    protected final static Map<File, Boolean> __SDK_MAP = new HashMap<File, Boolean>(2);
    
    private static Map<String, Integer> __JAVA_VERSION_MAP = new ConcurrentHashMap<String, Integer>();

    public JettyRuntime()
    {
        super();  // possible error
        // do nothing
    }

    public IJettyVersionHandler getVersionHandler()
    {
        IRuntimeType type = getRuntime().getRuntimeType();
        return JettyPlugin.getJettyVersionHandler(type.getId());
    }

    protected String getVMInstallTypeId()
    {
        return getAttribute(__PROP_VM_INSTALL_TYPE_ID,(String)null);
    }

    protected String getVMInstallId()
    {
        return getAttribute(__PROP_VM_INSTALL_ID,(String)null);
    }

    public boolean isUsingDefaultJRE()
    {
        return getVMInstallTypeId() == null;
    }

    public IVMInstall getVMInstall()
    {
        if (getVMInstallTypeId() == null)
            return JavaRuntime.getDefaultVMInstall();
        try
        {
            IVMInstallType vmInstallType = JavaRuntime.getVMInstallType(getVMInstallTypeId());
            IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();
            int size = vmInstalls.length;
            String id = getVMInstallId();
            for (int i = 0; i < size; i++)
            {
                if (id.equals(vmInstalls[i].getId()))
                    return vmInstalls[i];
            }
        }
        catch (Exception e)
        {
            JettyPlugin.log(e);
        }
        
        return null;
    }

    public Collection<IRuntimeClasspathEntry> getRuntimeClasspath(IPath configPath)
    {
        IPath installPath = getRuntime().getLocation();
        // If installPath is relative, convert to canonical path and hope for
        // the best
        if (!installPath.isAbsolute())
        {
            try
            {
                String installLoc = (new File(installPath.toOSString())).getCanonicalPath();
                installPath = new Path(installLoc);
            }
            catch (IOException e)
            {
                JettyPlugin.log(e);
            }
        }
        return getVersionHandler().getRuntimeClasspath(installPath,configPath);
    }

    /**
     * Verifies the Jetty installation directory. If it is correct, true is returned. Otherwise, the user is notified and false is returned.
     * 
     * @return boolean
     */
    public IStatus verifyLocation()
    {
        return getVersionHandler().verifyInstallPath(getRuntime().getLocation());
    }

    /*
     * Validate the runtime
     */
    public IStatus validate()
    {
        IStatus status = super.validate();
        if (!status.isOK())
            return status;

        status = verifyLocation();
        if (!status.isOK())
            return status;
        // don't accept trailing space since that can cause startup problems
        if (getRuntime().getLocation().hasTrailingSeparator())
            return new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,Messages.errorInstallDirTrailingSlash,null);
        if (getVMInstall() == null)
            return new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,Messages.errorJRE,null);

        // check for tools.jar (contains the javac compiler on Windows & Linux)
        // to see whether
        // Jetty will be able to compile JSPs.
        // boolean found = false;
        // File file = getVMInstall().getInstallLocation();
        // if (file != null) {
        // File toolsJar = new File(file, TOOLS_JAR_PATH);
        // if (toolsJar.exists())
        // found = true;
        // }

        status = getVersionHandler().validate(getRuntime().getLocation(),getVMInstall());
        
        if (status != null)
        {
            return status;
        }
        return Status.OK_STATUS;
        // // on Jetty 5.5 and 6.0, the Eclipse JDT compiler is used for JSP's
        // String id = getRuntime().getRuntimeType().getId();
        // if (!found) {
        // if (id != null && (id.indexOf("55") > 0 || id.indexOf("60") > 0))
        // found = true;
        // }
        //
        // // on Mac, tools.jar is merged into classes.zip. if tools.jar wasn't
        // // found,
        // // try loading the javac class by running a check inside the VM
        // if (!found) {
        // String os = Platform.getOS();
        // if (os != null && os.toLowerCase().indexOf("mac") >= 0)
        // found = checkForCompiler();
        // }
        //
        // if (!found)
        // return new Status(IStatus.WARNING, JettyPlugin.PLUGIN_ID, 0,
        // Messages.warningJRE, null);
        //
        // File f = getRuntime().getLocation().append("conf").toFile();
        // File[] conf = f.listFiles();
        // if (conf != null) {
        // int size = conf.length;
        // for (int i = 0; i < size; i++) {
        // if (!f.canRead())
        // return new Status(IStatus.WARNING, JettyPlugin.PLUGIN_ID,
        // 0, Messages.warningCantReadConfig, null);
        // }
        // }
        //
        // // For Jetty 6.0, ensure we have J2SE 5.0
        // if (id != null && id.indexOf("60") > 0) {
        // IVMInstall vmInstall = getVMInstall();
        // if (vmInstall instanceof IVMInstall2) {
        // String javaVersion = ((IVMInstall2) vmInstall).getJavaVersion();
        // if (javaVersion != null
        // && !isVMMinimumVersion(javaVersion, 105)) {
        // return new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, 0,
        // Messages.errorJREJetty60, null);
        // }
        // }
        // }
        // // Else for Jetty 7.0, ensure we have J2SE 6.0
        // else if (id != null && id.indexOf("70") > 0) {
        // IVMInstall vmInstall = getVMInstall();
        // if (vmInstall instanceof IVMInstall2) {
        // String javaVersion = ((IVMInstall2) vmInstall).getJavaVersion();
        // if (javaVersion != null
        // && !isVMMinimumVersion(javaVersion, 106)) {
        // return new Status(IStatus.ERROR, JettyPlugin.PLUGIN_ID, 0,
        // Messages.errorJREJetty70, null);
        // }
        // }
        // }

        // return Status.OK_STATUS;
    }

    /**
     * @see RuntimeDelegate#setDefaults(IProgressMonitor)
     */
    public void setDefaults(IProgressMonitor monitor)
    {
        IRuntimeType type = getRuntimeWorkingCopy().getRuntimeType();
        getRuntimeWorkingCopy().setLocation(new Path(JettyPlugin.getPreference("location" + type.getId())));
    }

    public void setVMInstall(IVMInstall vmInstall)
    {
        if (vmInstall == null)
        {
            setVMInstall(null,null);
        }
        else
        {
            setVMInstall(vmInstall.getVMInstallType().getId(),vmInstall.getId());
        }
    }

    protected void setVMInstall(String typeId, String id)
    {
        if (typeId == null)
        {
            setAttribute(__PROP_VM_INSTALL_TYPE_ID,(String)null);
        }
        else
        {
            setAttribute(__PROP_VM_INSTALL_TYPE_ID,typeId);
        }

        if (id == null)
        {
            setAttribute(__PROP_VM_INSTALL_ID,(String)null);
        }
        else
        {
            setAttribute(__PROP_VM_INSTALL_ID,id);
        }
    }

    /**
     * Checks for the existence of the Java compiler in the given java executable. A main program is run (<code>org.eclipse.jst.Jetty.core.
     * internal.ClassDetector</code>), that dumps a true or false value depending on whether the compiler is found. This output is then parsed and cached for
     * future reference.
     * 
     * @return true if the compiler was found
     */
    protected boolean checkForCompiler()
    {
        // first try the cache
        File javaHome = getVMInstall().getInstallLocation();
        
        try
        {
            Boolean b = (Boolean)__SDK_MAP.get(javaHome);
            return b.booleanValue();
        }
        catch (Exception e)
        {
            // ignore
        }

        // locate Jettycore.jar - it contains the class detector main program
        File file = JettyPlugin.getPlugin();
        if (file != null && file.exists())
        {
            IVMRunner vmRunner = getVMInstall().getVMRunner(ILaunchManager.RUN_MODE);
            VMRunnerConfiguration config = new VMRunnerConfiguration(__CLASS_DETECTOR,new String[]
            { file.getAbsolutePath() });
            config.setProgramArguments(new String[]
            { __JAVAC_MAIN });
            ILaunch launch = new Launch(null,ILaunchManager.RUN_MODE,null);
            try
            {
                vmRunner.run(config,launch,null);
                for (int i = 0; i < 600; i++)
                {
                    // wait no more than 30 seconds (600 * 50 mils)
                    if (launch.isTerminated())
                    {
                        break;
                    }
                    try
                    {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e)
                    {
                        // ignore
                    }
                }
                IStreamsProxy streamsProxy = launch.getProcesses()[0].getStreamsProxy();
                String text = null;
                if (streamsProxy != null)
                {
                    text = streamsProxy.getOutputStreamMonitor().getContents();

                    if (text != null && text.length() > 0)
                    {
                        boolean found = false;
                        if (StringUtils.isTrue(text))
                            found = true;

                        __SDK_MAP.put(javaHome,Boolean.valueOf(found));
                        return found;
                    }
                }
            }
            catch (Exception e)
            {
                Trace.trace(Trace.SEVERE,"Error checking for JDK",e);
            }
            finally
            {
                if (!launch.isTerminated())
                {
                    try
                    {
                        launch.terminate();
                    }
                    catch (Exception ex)
                    {
                        // ignore
                    }
                }
            }
        }

        // log error that we were unable to check for the compiler
        JettyPlugin.log(MessageFormat.format("Failed compiler check for {0}",javaHome.getAbsolutePath()));
        return false;
    }

    private boolean isVMMinimumVersion(String javaVersion, int minimumVersion)
    {
        Integer version = (Integer)__JAVA_VERSION_MAP.get(javaVersion);
        if (version == null)
        {
            int index = javaVersion.indexOf('.');
            if (index > 0)
            {
                try
                {
                    int major = Integer.parseInt(javaVersion.substring(0,index)) * 100;
                    index++;
                    int index2 = javaVersion.indexOf('.',index);
                    if (index2 > 0)
                    {
                        int minor = Integer.parseInt(javaVersion.substring(index,index2));
                        version = Integer.valueOf(major + minor);
                        __JAVA_VERSION_MAP.put(javaVersion,version);
                    }
                }
                catch (NumberFormatException e)
                {
                    // Ignore
                }
            }
        }
        // If we have a version, and it's less than the minimum, fail the check
        if (version != null && version.intValue() < minimumVersion)
        {
            return false;
        }
        return true;
    }
}
