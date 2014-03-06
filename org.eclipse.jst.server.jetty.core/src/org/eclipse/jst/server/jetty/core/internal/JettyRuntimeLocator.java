/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
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
import java.io.FileFilter;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.jetty.core.IJettyRuntimeWorkingCopy;
import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;

public class JettyRuntimeLocator extends RuntimeLocatorDelegate
{

    @Override
    public void searchForRuntimes(IPath path, IRuntimeSearchListener listener, IProgressMonitor monitor)
    {
        searchForRuntimes2(path,listener,monitor);
    }

    protected static void searchForRuntimes2(IPath path, IRuntimeSearchListener listener, IProgressMonitor monitor)
    {
        File[] files = null;
        if (path != null)
        {
            File f = path.toFile();
            if (f.exists())
                files = f.listFiles();
            else
                return;
        }
        else
            files = File.listRoots();

        if (files != null)
        {
            int size = files.length;
            int work = 100 / size;
            int workLeft = 100 - (work * size);
            for (int i = 0; i < size; i++)
            {
                if (monitor.isCanceled())
                    return;
                if (files[i] != null && files[i].isDirectory())
                    searchDir(listener,files[i],4,monitor);
                monitor.worked(work);
            }
            monitor.worked(workLeft);
        }
        else
            monitor.worked(100);
    }

    protected static void searchDir(IRuntimeSearchListener listener, File dir, int depth, IProgressMonitor monitor)
    {
        if ("conf".equals(dir.getName()))
        {
            IRuntimeWorkingCopy runtime = getRuntimeFromDir(dir.getParentFile(),monitor);
            if (runtime != null)
            {
                listener.runtimeFound(runtime);
                return;
            }
        }

        if (depth == 0)
            return;

        File[] files = dir.listFiles(new FileFilter()
        {
            public boolean accept(File file)
            {
                return file.isDirectory();
            }
        });
        if (files != null)
        {
            int size = files.length;
            for (int i = 0; i < size; i++)
            {
                if (monitor.isCanceled())
                    return;
                searchDir(listener,files[i],depth - 1,monitor);
            }
        }
    }

    protected static IRuntimeWorkingCopy getRuntimeFromDir(File dir, IProgressMonitor monitor)
    {

        Collection<String> runtimeTypes = JettyPlugin.getRuntimeTypes();
        for (String runtimeTypeName : runtimeTypes)
        {
            try
            {
                IRuntimeType runtimeType = ServerCore.findRuntimeType(runtimeTypeName);

                String absolutePath = dir.getAbsolutePath();
                String id = absolutePath.replace(File.separatorChar,'_').replace(':','-');
                IRuntimeWorkingCopy runtime = runtimeType.createRuntime(id,monitor);
                runtime.setName(dir.getName());
                runtime.setLocation(new Path(absolutePath));
                IJettyRuntimeWorkingCopy wc = (IJettyRuntimeWorkingCopy)runtime.loadAdapter(IJettyRuntimeWorkingCopy.class,null);
                wc.setVMInstall(JavaRuntime.getDefaultVMInstall());
                IStatus status = runtime.validate(monitor);
                if (status == null || status.getSeverity() != IStatus.ERROR)
                    return runtime;

                Trace.trace(Trace.FINEST,"False runtime found at " + dir.getAbsolutePath() + ": " + status.getMessage());
            }
            catch (Exception e)
            {
                Trace.trace(Trace.SEVERE,"Could not find runtime",e);
                JettyPlugin.log(e);
            }
        }
        return null;
    }
}
