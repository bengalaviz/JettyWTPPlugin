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
package org.eclipse.jst.server.jetty.core.internal.jetty9;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jst.server.jetty.core.IJettyConfiguration;
import org.eclipse.jst.server.jetty.core.internal.IJettyVersionHandler;
import org.eclipse.jst.server.jetty.core.internal.IJettyVersionProvider;
import org.eclipse.jst.server.jetty.core.internal.jetty9.Jetty9Configuration;

public class Jetty9Provider implements IJettyVersionProvider
{

    public static final IJettyVersionProvider __INSTANCE = new Jetty9Provider();

    private IJettyVersionHandler _versionHandler = new Jetty9Handler();

    public IJettyVersionHandler getJettyVersionHandler()
    {
        return _versionHandler;
    }

    public IJettyConfiguration createJettyConfiguration(IFolder path)
    {
        return new Jetty9Configuration(path);
    }
}
