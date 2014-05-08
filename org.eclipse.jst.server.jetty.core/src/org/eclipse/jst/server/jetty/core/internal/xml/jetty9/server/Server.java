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
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.server.jetty.core.internal.xml.XMLElement;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Server extends XMLElement {
	private File _file;
	private IPath _path;
	
	public List<Connector> getConnectors(){
		List<Connector> connectors = null;
		NodeList callNodes = getElementNode().getElementsByTagName("Call");
		int length = callNodes.getLength();
		Element node = null;
		for (int i=0;i<length;i++){
			node = (Element)callNodes.item(i);
			if (hasAttribute(node, "addConnector")){
				Element portElement = super.findElement(node, "Set", "port");
				NodeList typeElements = node.getElementsByTagName("New");
				if (portElement != null){
					Connector connector = new Connector(portElement, (Element)typeElements.item(0));
				}
			}
		}
		return connectors;
	}
	
	public void setFile(File jettyXMLFile){
		this._file = jettyXMLFile;
	}
	
	public File getFile(){
		return _file;
	}
	
	public IPath getPath(){
		return _path;
	}
	
	public void setPath(IPath path){
		this._path = path;
	}
}
