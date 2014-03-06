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
package org.eclipse.jst.server.jetty.core.internal.xml;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jst.server.jetty.core.internal.Trace;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Factory for reading and writing from XML files.
 */
public class Factory
{
    protected String _packageName;
    protected Document _document;

    public Factory()
    {
        // do nothing
    }

    protected Attr createAttribute(String s, Element element)
    {
        Attr attr = _document.createAttribute(s);
        element.setAttributeNode(attr);
        return attr;
    }

    protected XMLElement createElement(int index, String s, Node node)
    {
        if (index < 0)
            return createElement(s,node);

        Element element = _document.createElement(s);
        try
        {
            Node child = node.getFirstChild();
            while (child != null && !s.equals(child.getNodeName()))
            {
                child = child.getNextSibling();
            }
            for (int i = 0; child != null && i < index; i++)
            {
                child = child.getNextSibling();
                while (child != null && !s.equals(child.getNodeName()))
                {
                    child = child.getNextSibling();
                }
            }
            // TODO Try to improve formating, maybe dup an appropriate text node
            if (child != null)
                node.insertBefore(element,child);
            else
                node.appendChild(element);
        }
        catch (Exception e)
        {
            node.appendChild(element);
        }
        return newInstance(element);
    }

    protected XMLElement createElement(String s, Node node)
    {
        Element element = _document.createElement(s);
        node.appendChild(element);
        return newInstance(element);
    }

    public byte[] getContents() throws IOException
    {
        return XMLUtil.getContents(_document);
    }

    /**
     * 
     * @return org.w3c.dom.Document
     */
    public Document getDocument()
    {
        return _document;
    }

    public String getPackageName()
    {
        return _packageName;
    }

    public XMLElement loadDocument(InputStream in) throws IOException, SAXException
    {
        try
        {
            _document = XMLUtil.getDocumentBuilder().parse(new InputSource(in));
            Element element = _document.getDocumentElement();
            return newInstance(element);
        }
        catch (IllegalArgumentException exception)
        {
            Trace.trace(Trace.WARNING,"Error loading document",exception);
            throw new IOException("Could not load document");
        }
    }

    protected XMLElement newInstance(Element element)
    {
        // String s = element.getNodeName();

        String s = element.getAttribute("class");
        if (s.length() < 1)
        {
            s = element.getNodeName();
        }
        int index = s.lastIndexOf('.');
        s = s.substring(index + 1,s.length());

        try
        {
            // change "web-app:test" to "WebAppTest"
            s = s.substring(0,1).toUpperCase() + s.substring(1);
            int i = s.indexOf("-");
            while (i >= 0)
            {
                s = s.substring(0,i) + s.substring(i + 1,i + 2).toUpperCase() + s.substring(i + 2);
                i = s.indexOf("-");
            }
            i = s.indexOf(":");
            while (i >= 0)
            {
                s = s.substring(0,i) + s.substring(i + 1,i + 2).toUpperCase() + s.substring(i + 2);
                i = s.indexOf(":");
            }

            // add package name
            if (_packageName != null)
                s = _packageName + "." + s;
            Class class1 = Class.forName(s);

            XMLElement xmlElement = (XMLElement)class1.newInstance();
            xmlElement.setElement(element);
            xmlElement.setFactory(this);
            return xmlElement;
        }
        catch (Exception exception)
        {
            // ignore
        }
        return null;
    }

    public void save(String filename) throws IOException
    {
        XMLUtil.save(filename,_document);
    }

    public void setDocument(Document d)
    {
        _document = d;
    }

    public void setPackageName(String s)
    {
        _packageName = s;
    }
}
