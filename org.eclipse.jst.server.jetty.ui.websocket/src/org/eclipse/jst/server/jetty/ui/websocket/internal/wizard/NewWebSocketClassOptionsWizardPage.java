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

import static org.eclipse.jst.j2ee.web.IServletConstants.QUALIFIED_SERVLET;

import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jst.j2ee.internal.web.operations.INewServletClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.web.operations.ServletSupertypesValidator;
import org.eclipse.jst.servlet.ui.internal.wizard.NewWebClassOptionsWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class NewWebSocketClassOptionsWizardPage extends NewWebClassOptionsWizardPage implements ISelectionChangedListener
{

    protected Button initButton;
    protected Button destroyButton;
    protected Button getConfigButton;
    protected Button getInfoButton;
    protected Button serviceButton;
    protected Button doGetButton;
    protected Button doPostButton;
    protected Button doPutButton;
    protected Button doDeleteButton;
    protected Button doHeadButton;
    protected Button doOptionsButton;
    protected Button doTraceButton;

    public NewWebSocketClassOptionsWizardPage(IDataModel model, String pageName, String pageDesc, String pageTitle)
    {
        super(model,pageName,pageDesc,pageTitle);
    }

    @Override
    protected void enter()
    {
        super.enter();

        boolean httpServlet = ServletSupertypesValidator.isHttpServletSuperclass(model);
        doGetButton.setVisible(httpServlet);
        doPostButton.setVisible(httpServlet);
        doPutButton.setVisible(httpServlet);
        doDeleteButton.setVisible(httpServlet);
        doHeadButton.setVisible(httpServlet);
        doOptionsButton.setVisible(httpServlet);
        doTraceButton.setVisible(httpServlet);
    }

    /**
     * Create the composite with all the stubs
     */
    @Override
    protected void createStubsComposite(Composite parent)
    {
        super.createStubsComposite(parent);

        inheritButton.addSelectionListener(new SelectionListener()
        {
            public void widgetSelected(SelectionEvent e)
            {
                boolean enable = inheritButton.getSelection();
                enableGenericServletButtons(enable);
                enableHttpServletButtons(enable);
            }

            public void widgetDefaultSelected(SelectionEvent e)
            {
                // Do nothing
            }

            private void enableGenericServletButtons(boolean enable)
            {
                if (ServletSupertypesValidator.isGenericServletSuperclass(model))
                {
                    initButton.setEnabled(enable);
                    destroyButton.setEnabled(enable);
                    getConfigButton.setEnabled(enable);
                    getInfoButton.setEnabled(enable);
                    serviceButton.setEnabled(enable);
                }
            }

            private void enableHttpServletButtons(boolean enable)
            {
                doGetButton.setEnabled(enable);
                doPostButton.setEnabled(enable);
                doPutButton.setEnabled(enable);
                doDeleteButton.setEnabled(enable);
                doHeadButton.setEnabled(enable);
                doOptionsButton.setEnabled(enable);
                doTraceButton.setEnabled(enable);
            }
        });

        Composite comp = new Composite(methodStubs,SWT.NULL);
        GridLayout layout = new GridLayout(3,false);
        layout.marginWidth = 0;
        layout.makeColumnsEqualWidth = true;
        comp.setLayout(layout);
        GridData data = new GridData(GridData.FILL_BOTH);
        comp.setLayoutData(data);

        initButton = new Button(comp,SWT.CHECK);
        initButton.setText("init"); //$NON-NLS-1$
        synchHelper.synchCheckbox(initButton,INewServletClassDataModelProperties.INIT,null);

        destroyButton = new Button(comp,SWT.CHECK);
        destroyButton.setText("destroy"); //$NON-NLS-1$
        synchHelper.synchCheckbox(destroyButton,INewServletClassDataModelProperties.DESTROY,null);

        getConfigButton = new Button(comp,SWT.CHECK);
        getConfigButton.setText("getServletConfig"); //$NON-NLS-1$
        synchHelper.synchCheckbox(getConfigButton,INewServletClassDataModelProperties.GET_SERVLET_CONFIG,null);

        getInfoButton = new Button(comp,SWT.CHECK);
        getInfoButton.setText("getServletInfo"); //$NON-NLS-1$
        synchHelper.synchCheckbox(getInfoButton,INewServletClassDataModelProperties.GET_SERVLET_INFO,null);

        serviceButton = new Button(comp,SWT.CHECK);
        serviceButton.setText("service"); //$NON-NLS-1$
        synchHelper.synchCheckbox(serviceButton,INewServletClassDataModelProperties.SERVICE,null);

        doGetButton = new Button(comp,SWT.CHECK);
        doGetButton.setText("do&Get"); //$NON-NLS-1$
        synchHelper.synchCheckbox(doGetButton,INewServletClassDataModelProperties.DO_GET,null);

        doPostButton = new Button(comp,SWT.CHECK);
        doPostButton.setText("do&Post"); //$NON-NLS-1$
        synchHelper.synchCheckbox(doPostButton,INewServletClassDataModelProperties.DO_POST,null);

        doPutButton = new Button(comp,SWT.CHECK);
        doPutButton.setText("doP&ut"); //$NON-NLS-1$
        synchHelper.synchCheckbox(doPutButton,INewServletClassDataModelProperties.DO_PUT,null);

        doDeleteButton = new Button(comp,SWT.CHECK);
        doDeleteButton.setText("do&Delete"); //$NON-NLS-1$
        synchHelper.synchCheckbox(doDeleteButton,INewServletClassDataModelProperties.DO_DELETE,null);

        doHeadButton = new Button(comp,SWT.CHECK);
        doHeadButton.setText("doH&ead"); //$NON-NLS-1$
        synchHelper.synchCheckbox(doHeadButton,INewServletClassDataModelProperties.DO_HEAD,null);

        doOptionsButton = new Button(comp,SWT.CHECK);
        doOptionsButton.setText("do&Options"); //$NON-NLS-1$
        synchHelper.synchCheckbox(doOptionsButton,INewServletClassDataModelProperties.DO_OPTIONS,null);

        doTraceButton = new Button(comp,SWT.CHECK);
        doTraceButton.setText("do&Trace"); //$NON-NLS-1$
        synchHelper.synchCheckbox(doTraceButton,INewServletClassDataModelProperties.DO_TRACE,null);

        interfaceViewer.addSelectionChangedListener(this);

        Dialog.applyDialogFont(parent);
    }

    public void selectionChanged(SelectionChangedEvent event)
    {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        removeButton.setEnabled(canRemoveSelectedInterfaces(selection));
    }

    @Override
    protected KeyListener getInterfaceKeyListener()
    {
        return new KeyListener()
        {

            public void keyPressed(KeyEvent e)
            {
            }

            public void keyReleased(KeyEvent e)
            {
                if (e.keyCode == SWT.DEL)
                {
                    IStructuredSelection selection = (IStructuredSelection)interfaceViewer.getSelection();
                    if (canRemoveSelectedInterfaces(selection))
                    {
                        handleInterfaceRemoveButtonSelected();
                    }
                }
            }

        };
    }

    private boolean canRemoveSelectedInterfaces(IStructuredSelection selection)
    {
        // if the selection is empty, then remove is not possible
        if (selection.isEmpty())
        {
            return false;
        }

        // if the selection is non-empty and the servlet extends GenericServlet, then
        // remove is possible
        if (ServletSupertypesValidator.isGenericServletSuperclass(model))
        {
            return true;
        }

        // if the selection is non-empty and the servlet does not extend GenericServlet,
        // then remove is not possible only if the Servlet interface is in the selection
        Iterator iter = selection.iterator();
        while (iter.hasNext())
        {
            if (QUALIFIED_SERVLET.equals(iter.next()))
            {
                return false;
            }
        }

        // in all other cases remove is possible
        return true;
    }
}
