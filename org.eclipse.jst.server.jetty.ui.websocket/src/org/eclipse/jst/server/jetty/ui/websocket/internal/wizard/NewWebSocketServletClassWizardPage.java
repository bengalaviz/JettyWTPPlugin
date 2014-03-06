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

import static org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties.PROJECT;
import static org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties.IS_SERVLET_TYPE;
import static org.eclipse.jst.j2ee.internal.web.operations.INewWebClassDataModelProperties.USE_EXISTING_CLASS;
import static org.eclipse.jst.servlet.ui.internal.wizard.IWebWizardConstants.CHOOSE_SERVLET_CLASS;
import static org.eclipse.jst.servlet.ui.internal.wizard.IWebWizardConstants.NEW_SERVLET_WIZARD_WINDOW_TITLE;
import static org.eclipse.jst.servlet.ui.internal.wizard.IWebWizardConstants.USE_EXISTING_SERVLET_CLASS;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.j2ee.internal.war.ui.util.WebServletGroupItemProvider;
import org.eclipse.jst.j2ee.webapplication.WebApp;
import org.eclipse.jst.jee.ui.internal.navigator.web.GroupServletItemProvider;
import org.eclipse.jst.jee.ui.internal.navigator.web.WebAppProvider;
import org.eclipse.jst.servlet.ui.internal.wizard.MultiSelectFilteredFileSelectionDialog;
import org.eclipse.jst.servlet.ui.internal.wizard.NewWebClassWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class NewWebSocketServletClassWizardPage extends NewWebClassWizardPage
{

    private final static String[] JSPEXTENSIONS =
    { "jsp" }; //$NON-NLS-1$

    public NewWebSocketServletClassWizardPage(IDataModel model, String pageName, String pageDesc, String pageTitle, String moduleType)
    {
        super(model,pageName,pageDesc,pageTitle,moduleType);
    }

    @Override
    protected String getUseExistingCheckboxText()
    {
        return USE_EXISTING_SERVLET_CLASS;
    }

    @Override
    protected String getUseExistingProperty()
    {
        return USE_EXISTING_CLASS;
    }

    @Override
    protected IProject getExtendedSelectedProject(Object selection)
    {
        if (selection instanceof WebServletGroupItemProvider)
        {
            WebApp webApp = (WebApp)((WebServletGroupItemProvider)selection).getParent();
            return ProjectUtilities.getProject(webApp);
        }
        else if (selection instanceof WebAppProvider)
        {
            return ((WebAppProvider)selection).getProject();
        }
        else if (selection instanceof GroupServletItemProvider)
        {
            org.eclipse.jst.javaee.web.WebApp webApp = (org.eclipse.jst.javaee.web.WebApp)((GroupServletItemProvider)selection).getJavaEEObject();
            return ProjectUtilities.getProject(webApp);
        }

        return super.getExtendedSelectedProject(selection);
    }

    @Override
    protected void handleClassButtonSelected()
    {
        getControl().setCursor(new Cursor(getShell().getDisplay(),SWT.CURSOR_WAIT));
        try
        {
            IProject project = (IProject)model.getProperty(PROJECT);

            if (project == null)
            {
                // show info message
                return;
            }

            IVirtualComponent component = ComponentCore.createComponent(project);
            MultiSelectFilteredFileSelectionDialog ms = new MultiSelectFilteredFileSelectionDialog(getShell(),NEW_SERVLET_WIZARD_WINDOW_TITLE,
                    CHOOSE_SERVLET_CLASS,JSPEXTENSIONS,false,project);
            IContainer root = component.getRootFolder().getUnderlyingFolder();
            ms.setInput(root);
            ms.open();
            if (ms.getReturnCode() == Window.OK)
            {
                String qualifiedClassName = ""; //$NON-NLS-1$
                if (ms.getSelectedItem() == MultiSelectFilteredFileSelectionDialog.JSP)
                {
                    Object obj = ms.getFirstResult();
                    if (obj != null)
                    {
                        if (obj instanceof IFile)
                        {
                            IFile file = (IFile)obj;
                            IPath pFull = file.getFullPath();
                            IPath pBase = root.getFullPath();
                            IPath path = pFull.removeFirstSegments(pBase.segmentCount());
                            qualifiedClassName = path.makeAbsolute().toString();
                            model.setProperty(IS_SERVLET_TYPE, Boolean.valueOf(false));
                        }
                    }
                }
                else
                {
                    IType type = (IType)ms.getFirstResult();
                    if (type != null)
                    {
                        qualifiedClassName = type.getFullyQualifiedName();
                        model.setProperty(IS_SERVLET_TYPE,new Boolean(true));
                    }
                }
                existingClassText.setText(qualifiedClassName);
            }
        }
        finally
        {
            getControl().setCursor(null);
        }
    }

    @Override
    protected Composite createTopLevelComposite(Composite parent)
    {
        Composite composite = super.createTopLevelComposite(parent);

        Object obj = getSelectedObject();
        if (isServlet(obj))
        {
            checkExistingButton(true);
            if (isServletJSP(obj))
            {
                existingClassText.setText(getServletJSPFile(obj));
                model.setBooleanProperty(IS_SERVLET_TYPE,false);
            }
            else
            {
                existingClassText.setText(getServletClass(obj));
                model.setBooleanProperty(IS_SERVLET_TYPE,true);
            }
        }
        else if (isJSP(obj))
        {
            checkExistingButton(true);
            existingClassText.setText(getWebResourcePath((IResource)obj));
            model.setBooleanProperty(IS_SERVLET_TYPE,false);
        }

        return composite;
    }

}
