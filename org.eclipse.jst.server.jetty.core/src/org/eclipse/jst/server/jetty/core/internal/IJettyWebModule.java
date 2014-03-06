/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - Jetty packages
 *******************************************************************************/
package org.eclipse.jst.server.jetty.core.internal;

/**
 * A Web module deployed on Jetty.
 */
public interface IJettyWebModule
{
    /**
     * Get the document base.
     * 
     * @return java.lang.String
     */
    public String getDocumentBase();

    /**
     * Return the path. (context root)
     * 
     * @return java.lang.String
     */
    public String getPath();

    /**
     * Return the memento.
     * 
     * @return java.lang.String
     */
    public String getMemento();

    /**
     * Return true if the web module is auto-reloadable.
     * 
     * @return java.lang.String
     */
    public boolean isReloadable();
}