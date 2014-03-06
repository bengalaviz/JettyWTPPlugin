/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - Jetty packages
 *******************************************************************************/
package org.eclipse.jst.server.jetty.ui.internal.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.jetty.core.IJettyConfiguration;
import org.eclipse.jst.server.jetty.core.IJettyServer;
import org.eclipse.jst.server.jetty.core.WebModule;
import org.eclipse.jst.server.jetty.ui.internal.ContextIds;
import org.eclipse.jst.server.jetty.ui.internal.Messages;
import org.eclipse.jst.server.jetty.ui.internal.Trace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServerAttributes;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.ServerUICore;

/**
 * Dialog to add or modify web modules.
 */
public class WebModuleDialog extends Dialog
{
    protected IModule _module4;
    protected WebModule _module;
    protected boolean _isEdit;
    protected boolean _isProject;
    protected Text _docBase;
    protected IServerAttributes _serverAttributes;
    protected IJettyServer _server;
    protected IJettyConfiguration _config;

    protected Table _projTable;

    /**
     * WebModuleDialog constructor comment.
     * 
     * @param parentShell
     *            a shell
     * @param server2
     *            a server
     * @param server
     *            a Jetty server
     * @param config
     *            a Jetty server configuration
     * @param module
     *            a module
     */
    public WebModuleDialog(Shell parentShell, IServerAttributes server2, IJettyServer server, IJettyConfiguration config, WebModule module)
    {
        super(parentShell);
        this._module = module;
        this._serverAttributes = server2;
        this._server = server;
        this._config = config;
        _isEdit = true;
    }

    /**
     * WebModuleDialog constructor comment.
     * 
     * @param parentShell
     *            a shell
     * @param server2
     *            a server
     * @param server
     *            a Jetty server
     * @param config
     *            a Jetty server configuration
     * @param isProject
     *            true if it is a project
     */
    public WebModuleDialog(Shell parentShell, IServerAttributes server2, IJettyServer server, IJettyConfiguration config, boolean isProject)
    {
        this(parentShell,server2,server,config,new WebModule("/","",null,true));
        _isEdit = false;
        this._isProject = isProject;
    }

    /**
	 *
	 */
    protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        if (_isEdit)
            newShell.setText(Messages.configurationEditorWebModuleDialogTitleEdit);
        else
            newShell.setText(Messages.configurationEditorWebModuleDialogTitleAdd);
    }

    /**
     * Creates and returns the contents of the upper part of this dialog (above the button bar).
     * <p>
     * The <code>Dialog</code> implementation of this framework method creates and returns a new <code>Composite</code> with standard margins and spacing.
     * Subclasses should override.
     * </p>
     * 
     * @param parent
     *            the parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent)
    {
        // create a composite with standard margins and spacing
        Composite composite = new Composite(parent,SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setFont(parent.getFont());
        IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
        whs.setHelp(composite,ContextIds.CONFIGURATION_EDITOR_WEBMODULE_DIALOG);
        // add project field if we are adding a project
        if (!_isEdit && _isProject)
        {
            Label l = new Label(composite,SWT.NONE);
            l.setText(Messages.configurationEditorWebModuleDialogProjects);
            GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
            l.setLayoutData(data);

            _projTable = new Table(composite,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
            data = new GridData();
            data.widthHint = 150;
            data.heightHint = 75;
            _projTable.setLayoutData(data);
            whs.setHelp(_projTable,ContextIds.CONFIGURATION_EDITOR_WEBMODULE_DIALOG_PROJECT);

            // fill table with web module projects
            ILabelProvider labelProvider = ServerUICore.getLabelProvider();
            IModule[] modules = ServerUtil.getModules(_serverAttributes.getServerType().getRuntimeType().getModuleTypes());
            if (modules != null)
            {
                int size = modules.length;
                for (int i = 0; i < size; i++)
                {
                    IModule module3 = modules[i];
                    if ("jst.web".equals(module3.getModuleType().getId()))
                    {
                        IStatus status = _serverAttributes.canModifyModules(new IModule[]
                        { module3 },null,null);
                        if (status != null && status.isOK())
                        {
                            TableItem item = new TableItem(_projTable,SWT.NONE);
                            item.setText(0,labelProvider.getText(module3));
                            item.setImage(0,labelProvider.getImage(module3));
                            item.setData(module3);
                        }
                    }
                }
            }
            labelProvider.dispose();
            new Label(composite,SWT.NONE).setText(" ");
        }

        new Label(composite,SWT.NONE).setText(Messages.configurationEditorWebModuleDialogDocumentBase);
        _docBase = new Text(composite,SWT.BORDER);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        _docBase.setLayoutData(data);
        _docBase.setText(_module.getDocumentBase());
        whs.setHelp(_docBase,ContextIds.CONFIGURATION_EDITOR_WEBMODULE_DIALOG_DOCBASE);

        // disable document base for project modules
        if (_isProject || (_module.getMemento() != null && _module.getMemento().length() > 0))
            _docBase.setEditable(false);
        else
        {
            _docBase.addModifyListener(new ModifyListener()
            {
                public void modifyText(ModifyEvent e)
                {
                    _module = new WebModule(_module.getPath(),_docBase.getText(),_module.getMemento(),_module.isReloadable());
                    validate();
                }
            });
        }

        if (_isEdit || _isProject)
            new Label(composite,SWT.NONE).setText(" ");
        else
        {
            Button browse = new Button(composite,SWT.NONE);
            browse.setText(Messages.browse);
            browse.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent se)
                {
                    try
                    {
                        DirectoryDialog dialog = new DirectoryDialog(getShell());
                        dialog.setMessage(Messages.configurationEditorWebModuleDialogSelectDirectory);
                        String selectedDirectory = dialog.open();
                        if (selectedDirectory != null)
                            _docBase.setText(selectedDirectory);
                    }
                    catch (Exception e)
                    {
                        Trace.trace(Trace.SEVERE,"Error browsing",e);
                    }
                }
            });
        }

        // path (context-root)
        new Label(composite,SWT.NONE).setText(Messages.configurationEditorWebModuleDialogPath);
        final Text path = new Text(composite,SWT.BORDER);
        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        data.widthHint = 150;
        path.setLayoutData(data);
        path.setText(_module.getPath());
        /*
         * if (module.getMemento() != null && module.getMemento().length() > 0) path.setEditable(false); else
         */
        path.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                _module = new WebModule(path.getText(),_module.getDocumentBase(),_module.getMemento(),_module.isReloadable());
            }
        });
        whs.setHelp(path,ContextIds.CONFIGURATION_EDITOR_WEBMODULE_DIALOG_PATH);

        new Label(composite,SWT.NONE).setText("");

        // if (!isProject) {
        // // auto reload
        // new Label(composite, SWT.NONE).setText("");
        // final Button reloadable = new Button(composite, SWT.CHECK);
        // reloadable.setText(Messages.configurationEditorWebModuleDialogReloadEnabled);
        // data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        // reloadable.setLayoutData(data);
        // reloadable.setSelection(module.isReloadable());
        // reloadable.addSelectionListener(new SelectionAdapter() {
        // public void widgetSelected(SelectionEvent e) {
        // module = new WebModule(module.getPath(), module.getDocumentBase(), module.getMemento(), reloadable.getSelection());
        // }
        // });
        // whs.setHelp(reloadable, ContextIds.CONFIGURATION_EDITOR_WEBMODULE_DIALOG_RELOAD);
        // }

        if (!_isEdit && _isProject)
        {
            _projTable.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent event)
                {
                    try
                    {
                        IModule module3 = (IModule)_projTable.getSelection()[0].getData();
                        IWebModule module2 = (IWebModule)module3.loadAdapter(IWebModule.class,null);
                        String contextRoot = module2.getContextRoot();
                        if (contextRoot != null && !contextRoot.startsWith("/") && contextRoot.length() > 0)
                            contextRoot = "/" + contextRoot;
                        _module = new WebModule(contextRoot,module3.getName(),module3.getId(),_module.isReloadable());
                        _docBase.setText(module3.getName());
                        path.setText(contextRoot);
                        _module4 = module3;
                    }
                    catch (Exception e)
                    {
                        // ignore
                    }
                    validate();
                }
            });
            new Label(composite,SWT.NONE).setText("");
        }

        Dialog.applyDialogFont(composite);
        return composite;
    }

    protected Control createButtonBar(Composite parent)
    {
        Control control = super.createButtonBar(parent);
        validate();

        return control;
    }

    protected void validate()
    {
        boolean ok = true;
        if (_module.getDocumentBase() == null || _module.getDocumentBase().length() < 1)
            ok = false;

        getButton(IDialogConstants.OK_ID).setEnabled(ok);
    }

    /**
     * Return the mime mapping.
     * 
     * @return org.eclipse.jst.server.jetty.WebModule
     */
    public WebModule getWebModule()
    {
        return _module;
    }
}
