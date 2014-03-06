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
package org.eclipse.jst.server.jetty.ui.websocket.internal.operations;

import static org.eclipse.jst.j2ee.web.IServletConstants.QUALIFIED_GENERIC_SERVLET;
import static org.eclipse.jst.j2ee.web.IServletConstants.QUALIFIED_HTTP_SERVLET;
import static org.eclipse.jst.j2ee.web.IServletConstants.QUALIFIED_SERVLET;

import org.eclipse.jst.j2ee.internal.web.operations.AbstractSupertypesValidator;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class WebSocketServletSupertypesValidator extends AbstractSupertypesValidator
{

    public static boolean isHttpServletSuperclass(IDataModel dataModel)
    {
        if (QUALIFIED_HTTP_SERVLET.equals(getSuperclass(dataModel)))
            return true;

        if (hasSuperclass(dataModel,getSuperclass(dataModel),QUALIFIED_HTTP_SERVLET))
            return true;

        return false;
    }

    public static boolean isGenericServletSuperclass(IDataModel dataModel)
    {
        if (QUALIFIED_HTTP_SERVLET.equals(getSuperclass(dataModel)))
            return true;

        if (QUALIFIED_GENERIC_SERVLET.equals(getSuperclass(dataModel)))
            return true;

        if (hasSuperclass(dataModel,getSuperclass(dataModel),QUALIFIED_GENERIC_SERVLET))
            return true;

        return false;
    }

    public static boolean isServletSuperclass(IDataModel dataModel)
    {
        if (QUALIFIED_HTTP_SERVLET.equals(getSuperclass(dataModel)))
            return true;

        if (QUALIFIED_GENERIC_SERVLET.equals(getSuperclass(dataModel)))
            return true;

        if (getInterfaces(dataModel).contains(QUALIFIED_SERVLET))
            return true;

        if (hasSuperInterface(dataModel,getSuperclass(dataModel),QUALIFIED_SERVLET))
            return true;

        for (Object iface : getInterfaces(dataModel))
        {
            if (hasSuperInterface(dataModel,(String)iface,QUALIFIED_SERVLET))
                return true;
        }

        return false;
    }

}
