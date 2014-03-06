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
package org.eclipse.jst.server.jetty.core.internal.config;

import java.io.File;

import org.eclipse.core.runtime.IPath;

public class PathFileConfig
{

    private File _file;
    private IPath _path;

    public PathFileConfig(File file, IPath path)
    {
        this._file = file;
        this._path = path;
    }

    public File getFile()
    {
        return _file;
    }

    public IPath getPath()
    {
        return _path;
    }
}
