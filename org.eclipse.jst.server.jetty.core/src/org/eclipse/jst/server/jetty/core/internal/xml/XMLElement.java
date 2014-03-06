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

import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * An XML element.
 */
public class XMLElement
{
    private Element _xmlElement;
    protected Factory _factory;

    public XMLElement()
    {
        // do nothing
    }

    public void setText(String textContent)
    {
        _xmlElement.setTextContent(textContent);
    }

    public Element getElementNode()
    {
        return _xmlElement;
    }

    public Attr addAttribute(String s, String s1)
    {
        Attr attr = _factory.createAttribute(s,_xmlElement);
        attr.setValue(s1);
        return attr;
    }

    public XMLElement createElement(int index, String s)
    {
        return _factory.createElement(index,s,_xmlElement);
    }

    public XMLElement createElement(String s)
    {
        return _factory.createElement(s,_xmlElement);
    }

    public XMLElement findElement(String s)
    {
        NodeList nodelist = _xmlElement.getElementsByTagName(s);
        int i = nodelist == null?0:nodelist.getLength();
        for (int j = 0; j < i; j++)
        {
            Node node = nodelist.item(j);
            String s1 = node.getNodeName().trim();
            if (s1.equals(s))
                return _factory.newInstance((Element)node);
        }

        return createElement(s);
    }

    public XMLElement findElement(String s, int i)
    {
        NodeList nodelist = _xmlElement.getElementsByTagName(s);
        int j = nodelist == null?0:nodelist.getLength();
        for (int k = 0; k < j; k++)
        {
            Node node = nodelist.item(k);
            String s1 = node.getNodeName().trim();
            if (s1.equals(s) && k == i)
                return _factory.newInstance((Element)node);
        }

        return createElement(s);
    }

    public Element findElement(String s, String attrName)
    {
        return findElement(_xmlElement,s,attrName);
    }

    public Element findElement(Element element, String s, String attrName)
    {
        NodeList nodelist = element.getElementsByTagName(s);
        int j = nodelist == null?0:nodelist.getLength();
        for (int k = 0; k < j; k++)
        {
            Element node = (Element)nodelist.item(k);
            if (hasAttribute(node,attrName))
            {
                return node;
            }
        }
        Element newElement = element.getOwnerDocument().createElement(s);
        element.getOwnerDocument().getDocumentElement().appendChild(newElement);
        return newElement;
    }

    public boolean hasAttribute(Element node, String attrName)
    {
        NamedNodeMap attributes = node.getAttributes();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++)
        {
            Node n = attributes.item(i);
            if (attrName.equals(n.getNodeValue()))
            {
                return true;
            }
        }
        return false;
    }

    public String getAttributeValue(String s)
    {
        Attr attr = _xmlElement.getAttributeNode(s);
        if (attr != null)
            return attr.getValue();

        return null;
    }

    public Map getAttributes()
    {
        Map attributes = new LinkedHashMap();
        NamedNodeMap attrs = _xmlElement.getAttributes();
        if (null != attrs)
        {
            for (int i = 0; i < attrs.getLength(); i++)
            {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                attributes.put(name,value);
            }
        }
        return attributes;
    }

    public String getElementName()
    {
        return _xmlElement.getNodeName();
    }

    public String getElementValue()
    {
        return getElementValue(_xmlElement);
    }

    protected static String getElementValue(Element element)
    {
        String s = element.getNodeValue();
        if (s != null)
            return s;
        NodeList nodelist = element.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++)
            if (nodelist.item(i) instanceof Text)
                return ((Text)nodelist.item(i)).getData();

        return null;
    }

    public Element getSubElement(String s)
    {
        NodeList nodelist = _xmlElement.getElementsByTagName(s);
        int i = nodelist == null?0:nodelist.getLength();
        for (int j = 0; j < i; j++)
        {
            Node node = nodelist.item(j);
            String s1 = node.getNodeName().trim();
            if (s1.equals(s))
                return (Element)node;
        }

        return null;
    }

    public String getSubElementValue(String s)
    {
        Element element = getSubElement(s);
        if (element == null)
            return null;

        String value = getElementValue(element);
        if (value == null)
            return null;

        return value.trim();
    }

    public boolean removeAttribute(String s)
    {
        try
        {
            _xmlElement.removeAttribute(s);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    public boolean removeElement(String s, int i)
    {
        NodeList nodelist = _xmlElement.getElementsByTagName(s);
        int j = nodelist == null?0:nodelist.getLength();
        for (int k = 0; k < j; k++)
        {
            Node node = nodelist.item(k);
            String s1 = node.getNodeName().trim();
            if (s1.equals(s) && k == i)
            {
                _xmlElement.removeChild(node);
                return true;
            }
        }

        return false;
    }

    public void setAttributeValue(String s, String s1)
    {
        Attr attr = _xmlElement.getAttributeNode(s);
        if (attr == null)
            attr = addAttribute(s,s1);
        else
            attr.setValue(s1);
    }

    void setElement(Element element)
    {
        _xmlElement = element;
    }

    protected static void setElementValue(Element element, String value)
    {
        String s = element.getNodeValue();
        if (s != null)
        {
            element.setNodeValue(value);
            return;
        }
        NodeList nodelist = element.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++)
            if (nodelist.item(i) instanceof Text)
            {
                Text text = (Text)nodelist.item(i);
                text.setData(value);
                return;
            }

        return;
    }

    void setFactory(Factory factory1)
    {
        _factory = factory1;
    }

    public Factory getFactory()
    {
        return _factory;
    }

    public void setSubElementValue(String s, String value)
    {
        Element element = getSubElement(s);
        if (element == null)
        {
            element = _factory._document.createElement(s);
            element.appendChild(_factory._document.createTextNode("temp"));
            _xmlElement.appendChild(element);
        }
        setElementValue(element,value);
    }

    public int sizeOfElement(String s)
    {
        NodeList nodelist = _xmlElement.getElementsByTagName(s);
        int i = nodelist == null?0:nodelist.getLength();
        return i;
    }

    public void updateElementValue(String s)
    {
        try
        {
            _xmlElement.setNodeValue(s);
        }
        catch (DOMException ex)
        {
            NodeList nodelist = _xmlElement.getChildNodes();
            int i = nodelist == null?0:nodelist.getLength();
            if (i > 0)
            {
                for (int j = 0; j < i; j++)
                    if (nodelist.item(j) instanceof Text)
                    {
                        ((Text)nodelist.item(j)).setData(s);
                        return;
                    }
            }
            else
            {
                _xmlElement.appendChild(_factory._document.createTextNode(s));
            }
        }
    }

    public boolean hasChildNodes()
    {
        return _xmlElement.hasChildNodes();
    }

    public void removeChildren()
    {
        while (_xmlElement.hasChildNodes())
        {
            _xmlElement.removeChild(_xmlElement.getFirstChild());
        }
    }

    public void copyChildrenTo(XMLElement destination)
    {
        NodeList nodelist = _xmlElement.getChildNodes();
        int len = nodelist == null?0:nodelist.getLength();
        for (int i = 0; i < len; i++)
        {
            Node node = nodelist.item(i);
            destination.importNode(node,true);
        }
    }

    public void importNode(Node node, boolean deep)
    {
        _xmlElement.appendChild(_xmlElement.getOwnerDocument().importNode(node,deep));
    }

    /**
     * This method tries to compare two XMLElements for equivalence. Due to the lack of normalization, they aren't compared for equality. Elements are required
     * to have the same attributes or the same node value if attributes aren't present. Attributes and node value are assumed to be mutually exclusive for Jetty
     * configuration XML files. The same non-text child nodes are required to be present in an element and appear in the same order. If a node type other than
     * element or comment is encountered, this method punts and returns false.
     * 
     * @param obj
     *            XMLElement to compare
     * @return true if the elements are equivalent
     */
    public boolean isEquivalent(XMLElement obj)
    {
        if (obj != null)
        {
            try
            {
                return elementsAreEquivalent(_xmlElement,obj.getElementNode());
            }
            catch (Exception e)
            {
                // Catch and ignore just to be safe
            }
        }
        return false;
    }

    /**
     * Same as isEquivalent() but doesn't ignore exceptions for test purposes. This avoids hiding an expected mismatch behind an unexpected exception.
     * 
     * @param obj
     *            XMLElement to compare
     * @return true if the elements are equivalent
     */
    public boolean isEquivalentTest(XMLElement obj)
    {
        if (obj != null)
        {
            return elementsAreEquivalent(_xmlElement,obj.getElementNode());
        }
        return false;
    }

    private static boolean elementsAreEquivalent(Element element, Element otherElement)
    {
        if (element == otherElement)
            return true;

        if (!element.getNodeName().equals(otherElement.getNodeName()))
            return false;

        if (element.hasChildNodes())
        {
            if (otherElement.hasChildNodes() && attributesAreEqual(element,otherElement))
            {
                // Compare child nodes
                NodeList nodelist = element.getChildNodes();
                NodeList otherNodelist = otherElement.getChildNodes();
                if (nodelist.getLength() == otherNodelist.getLength())
                {
                    Node node = nextNonTextNode(element.getFirstChild());
                    Node otherNode = nextNonTextNode(otherElement.getFirstChild());
                    while (node != null)
                    {
                        if (otherNode == null)
                            return false;
                        short nextNodeType = node.getNodeType();
                        if (nextNodeType != otherNode.getNodeType())
                            return false;
                        // If elements, compare
                        if (nextNodeType == Node.ELEMENT_NODE)
                        {
                            if (!elementsAreEquivalent((Element)node,(Element)otherNode))
                                return false;
                        }
                        // Else if comment, compare
                        else if (nextNodeType == Node.COMMENT_NODE)
                        {
                            if (!nodeValuesAreEqual(node,otherNode))
                                return false;
                        }
                        // Else punt on other node types
                        else
                        {
                            return false;
                        }
                        node = nextNonTextNode(node.getNextSibling());
                        otherNode = nextNonTextNode(otherNode.getNextSibling());
                    }
                    // If also at end of other children, return equal
                    if (otherNode == null)
                        return true;
                }
            }
        }
        else if (!otherElement.hasChildNodes())
        {
            return attributesAreEqual(element,otherElement);
        }
        return false;
    }

    private static Node nextNonTextNode(Node node)
    {
        while (node != null && node.getNodeType() == Node.TEXT_NODE)
            node = node.getNextSibling();
        return node;
    }

    private static boolean attributesAreEqual(Element element, Element otherElement)
    {
        NamedNodeMap attrs = element.getAttributes();
        NamedNodeMap otherAttrs = otherElement.getAttributes();
        if (attrs == null && otherAttrs == null)
        {
            // Return comparison of element values if there are no attributes
            return nodeValuesAreEqual(element,otherElement);
        }

        if (attrs.getLength() == otherAttrs.getLength())
        {
            if (attrs.getLength() == 0)
                // Return comparison of element values if there are no
                // attributes
                return nodeValuesAreEqual(element,otherElement);

            for (int i = 0; i < attrs.getLength(); i++)
            {
                Node attr = attrs.item(i);
                Node otherAttr = otherAttrs.getNamedItem(attr.getNodeName());
                if (!nodeValuesAreEqual(attr,otherAttr))
                    return false;
            }
            return true;
        }
        return false;
    }

    private static boolean nodeValuesAreEqual(Node node, Node otherNode)
    {
        String value = node.getNodeValue();
        String otherValue = otherNode.getNodeValue();
        if (value != null && otherValue != null)
        {
            if (value.equals(otherValue))
                return true;
        }
        else if (value == null && otherValue == null)
            return true;
        return false;
    }
}
