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
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.jetty.core.internal.util.JettyVersionHelper;

public abstract class JettyHandler implements IJettyVersionHandler, JettyConstants
{

    /** configuration attribute for the full class name of the bootstrap class. */
    private static final String __BOOTSTRAP_CLASS_NAME = "org.eclipse.jetty.start.Main";

    /**
     * @see IJettyVersionHandler#getRuntimeClass()
     */
    public String getRuntimeClass()
    {
        return __BOOTSTRAP_CLASS_NAME;
    }

    public Collection<IRuntimeClasspathEntry> getRuntimeClasspath(IPath installPath, IPath configPath)
    {
        Collection<IRuntimeClasspathEntry> cp = new ArrayList<IRuntimeClasspathEntry>();

        // Add ${jetty.home}/start.jar
        IPath startJAR = installPath.append(__START_JAR);
        cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(startJAR));

        // add all jars from the Jetty ${jetty.home}/lib directory
        IPath libPath = installPath.append(__LIB_FOLDER);
        File libDir = libPath.toFile();
        if (libDir.exists())
        {
            // lib folder, exists, loop for each JAR
            String[] libs = libDir.list();
            for (int i = 0; i < libs.length; i++)
            {
                if (libs[i].endsWith(__JAR_EXT))
                {
                    IPath path = installPath.append(__LIB_FOLDER).append(libs[i]);
                    cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
                }
            }

            // add all jars from the Jetty ${jetty.home}/lib/jsp directory
            IPath jspLibPath = libPath.append(__JSP_FOLDER);
            File jspLibDir = jspLibPath.toFile();
            if (jspLibDir.exists())
            {
                libs = jspLibDir.list();
                for (int i = 0; i < libs.length; i++)
                {
                    if (libs[i].endsWith(__JAR_EXT))
                    {
                        IPath path = jspLibPath.append(libs[i]);
                        cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
                    }
                }
            }
        }
        return cp;
    }

    /**
     * @see IJettyVersionHandler#prepareRuntimeDirectory(IPath)
     */
    public IStatus prepareRuntimeDirectory(IPath baseDir)
    {
        return JettyVersionHelper.createJettyInstanceDirectory(baseDir);
    }
}
