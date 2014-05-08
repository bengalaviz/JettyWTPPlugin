/*******************************************************************************
 * Copyright (c) 2014 Benjamin Galaviz and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Benjamin Galaviz <ben.galaviz@gmail.com> - Initial API and implementation 
 *******************************************************************************/
package org.eclipse.jst.server.jetty.core.internal.xml.jetty9.server;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.server.jetty.core.internal.xml.XMLElement;

public class WebApp extends XMLElement {
	private File _file;
	private IPath _path;
	
	public File getFile(){
		return _file;
	}
	
	public void setFile(File file){
		this._file = file;
	}
	
	public IPath getPath(){
		return _path;
	}
	
	public void setPath(IPath path){
		this._path = path;
	}
}
