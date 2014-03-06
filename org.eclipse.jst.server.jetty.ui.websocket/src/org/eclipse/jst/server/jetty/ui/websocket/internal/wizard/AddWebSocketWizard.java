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
package org.eclipse.jst.server.jetty.ui.websocket.internal.wizard;

import java.lang.reflect.InvocationTargetException;

import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.IS_SERVLET_TYPE;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.jst.server.jetty.ui.websocket.IWebSocketUIContextIds;
import org.eclipse.jst.server.jetty.ui.websocket.IWebSocketWizardConstants;
import org.eclipse.jst.server.jetty.ui.websocket.JettyUIWebSocketPlugin;
import org.eclipse.jst.server.jetty.ui.websocket.internal.operations.NewWebSocketClassDataModelProvider;
import org.eclipse.jst.servlet.ui.internal.wizard.IWebWizardConstants;
import org.eclipse.jst.servlet.ui.internal.wizard.NewWebArtifactWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

public class AddWebSocketWizard extends NewWebArtifactWizard
{

    private static final String PAGE_ONE = "pageOne"; //$NON-NLS-1$
    private static final String PAGE_TWO = "pageTwo"; //$NON-NLS-1$
    private static final String PAGE_THREE = "pageThree"; //$NON-NLS-1$

    public AddWebSocketWizard()
    {
        this(null);
    }

    public AddWebSocketWizard(IDataModel model)
    {
        super(model);
    }

    @Override
    protected String getTitle()
    {
        return IWebSocketWizardConstants.ADD_WEBSOCKET_WIZARD_WINDOW_TITLE;
    }

    @Override
    protected ImageDescriptor getImage()
    {
        return JettyUIWebSocketPlugin.getDefault().getImageDescriptor("newwebsocket_wiz");
    }

    @Override
    protected IDataModelProvider getDefaultProvider()
    {
        return new NewWebSocketClassDataModelProvider();
    }

    @Override
    protected void doAddPages()
    {
        NewWebSocketServletClassWizardPage page1 = new NewWebSocketServletClassWizardPage(getDataModel(),PAGE_ONE,
                IWebWizardConstants.NEW_JAVA_CLASS_DESTINATION_WIZARD_PAGE_DESC,IWebSocketWizardConstants.ADD_WEBSOCKET_WIZARD_PAGE_TITLE,
                J2EEProjectUtilities.DYNAMIC_WEB);
        page1.setInfopopID(IWebSocketUIContextIds.WEBEDITOR_WEBSOCKET_PAGE_ADD_WEBSOCKET_WIZARD_1);
        addPage(page1);
        AddWebSocketWizardPage page2 = new AddWebSocketWizardPage(getDataModel(),PAGE_TWO);
        page2.setInfopopID(IWebSocketUIContextIds.WEBEDITOR_WEBSOCKET_PAGE_ADD_WEBSOCKET_WIZARD_2);
        addPage(page2);
        NewWebSocketClassOptionsWizardPage page3 = new NewWebSocketClassOptionsWizardPage(getDataModel(),PAGE_THREE,
                IWebWizardConstants.NEW_JAVA_CLASS_OPTIONS_WIZARD_PAGE_DESC,IWebSocketWizardConstants.ADD_WEBSOCKET_WIZARD_PAGE_TITLE);
        page3.setInfopopID(IWebSocketUIContextIds.WEBEDITOR_WEBSOCKET_PAGE_ADD_WEBSOCKET_WIZARD_3);
        addPage(page3);
    }

    @Override
    protected void postPerformFinish() throws InvocationTargetException
    {
        boolean isWebSocket = getDataModel().getBooleanProperty(IS_SERVLET_TYPE);
        if (isWebSocket)
            openJavaClass();
        else
            openWebFile();
    }
}
