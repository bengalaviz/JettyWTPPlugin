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
package org.eclipse.jst.server.jetty.core.internal.jetty8;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jst.server.jetty.core.IJettyConfiguration;
import org.eclipse.jst.server.jetty.core.internal.IJettyVersionHandler;
import org.eclipse.jst.server.jetty.core.internal.IJettyVersionProvider;
import org.eclipse.jst.server.jetty.core.internal.jetty7.Jetty7Configuration;

public class Jetty8Provider implements IJettyVersionProvider
{

    public static final IJettyVersionProvider __INSTANCE = new Jetty8Provider();

    private IJettyVersionHandler _versionHandler = new Jetty8Handler();

    public IJettyVersionHandler getJettyVersionHandler()
    {
        return _versionHandler;
    }

    public IJettyConfiguration createJettyConfiguration(IFolder path)
    {
        return new Jetty7Configuration(path);
    }
}
