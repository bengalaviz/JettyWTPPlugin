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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;

public interface IJettyServerBehaviour
{

    /**
     * Returns the main class that is used to launch the Jetty server.
     * 
     * @return the main runtime class
     */
    public String getRuntimeClass();

    /**
     * Setup for starting the server.
     * 
     * @param launch
     *            ILaunch
     * @param launchMode
     *            String
     * @param monitor
     *            IProgressMonitor
     * @throws CoreException
     *             if anything goes wrong
     */
    public void setupLaunch(ILaunch launch, String launchMode, IProgressMonitor monitor) throws CoreException;
}
