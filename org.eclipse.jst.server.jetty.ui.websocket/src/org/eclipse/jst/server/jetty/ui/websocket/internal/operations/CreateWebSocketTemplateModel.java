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

import java.util.Collection;

import org.eclipse.jst.j2ee.internal.web.operations.CreateServletTemplateModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class CreateWebSocketTemplateModel extends CreateServletTemplateModel
{

    public CreateWebSocketTemplateModel(IDataModel dataModel)
    {
        super(dataModel);
    }

    @Override
    public Collection<String> getImports()
    {
        Collection<String> collection = super.getImports();
        collection.add("java.util.Set");
        collection.add("java.util.concurrent.CopyOnWriteArraySet");
        collection.add("org.eclipse.jetty.websocket.WebSocket");
        return collection;
    }

    public String getWebSocketClassName()
    {
        final String webSocketServletClassName = super.getClassName();
        if (webSocketServletClassName == null)
            return null;

        String webSocketClassName = webSocketServletClassName;
        webSocketClassName = webSocketClassName.replaceAll("Servlet","");
        webSocketClassName = webSocketClassName.replaceAll("WebSocket","");
        webSocketClassName = webSocketClassName + "WebSocket";
        if (!webSocketClassName.equals(webSocketServletClassName))
            return webSocketClassName;
        return "Internal" + webSocketClassName;
    }

    public boolean canSupportAnnotation()
    {
        return dataModel.getBooleanProperty(NewWebSocketClassDataModelProvider.SUPPORT_ANNOTATION);
    }

}
