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
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jst.server.core.IJavaRuntime;

/**
 * 
 */
public interface IJettyRuntime extends IJavaRuntime
{
    /**
     * Returns the runtime classpath that is used by this runtime.
     * 
     * @return the runtime classpath
     */
    public Collection<IRuntimeClasspathEntry> getRuntimeClasspath(IPath configPath);
}