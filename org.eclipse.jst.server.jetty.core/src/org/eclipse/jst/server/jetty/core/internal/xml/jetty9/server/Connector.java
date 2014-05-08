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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Connector {
	private Element _portElement;
	private Element _typeElement;
	
	public Connector(Element portElement, Element typeElement){
		this._portElement = portElement;
		this._typeElement = typeElement;
	}
	
	public String getPort(){
		Node firstChild = _portElement.getFirstChild();
		if (firstChild.getNodeType() == Node.ELEMENT_NODE){
			return ((Element)firstChild).getAttribute("default");
		}else{
			return _portElement.getTextContent();
		}
	}
	
	public String getType(){
		return _typeElement.getAttribute("class");
	}
	
	public void setPort(String port){
		_portElement.setTextContent(port);
	}
}
