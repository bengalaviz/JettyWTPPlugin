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
package org.eclipse.jst.server.jetty.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.jetty.core.IJettyConfiguration;
import org.eclipse.jst.server.jetty.core.IJettyConfigurationWorkingCopy;
import org.eclipse.jst.server.jetty.core.IJettyServer;
import org.eclipse.jst.server.jetty.core.IJettyServerWorkingCopy;
import org.eclipse.jst.server.jetty.core.WebModule;
import org.eclipse.jst.server.jetty.core.command.AddModuleCommand;
import org.eclipse.jst.server.jetty.core.command.AddWebModuleCommand;
import org.eclipse.jst.server.jetty.core.command.ModifyWebModuleCommand;
import org.eclipse.jst.server.jetty.core.command.RemoveModuleCommand;
import org.eclipse.jst.server.jetty.core.command.RemoveWebModuleCommand;
import org.eclipse.jst.server.jetty.ui.internal.ContextIds;
import org.eclipse.jst.server.jetty.ui.internal.JettyUIPlugin;
import org.eclipse.jst.server.jetty.ui.internal.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.editor.ServerEditorPart;

/**
 * Jetty configuration web module editor page.
 */
public class ConfigurationWebModuleEditorPart extends ServerEditorPart
{
    protected IJettyServerWorkingCopy _server2;
    protected IJettyConfigurationWorkingCopy _configuration;

    protected Table _webAppTable;
    protected int _selection = -1;
    protected Button _addProjectButton;
    protected Button _addExtProjectButton;
    protected Button _removeButton;
    protected Button _editButton;

    protected PropertyChangeListener _listener;

    /**
     * ConfigurationWebModuleEditorPart constructor comment.
     */
    public ConfigurationWebModuleEditorPart()
    {
        super();
    }

    /**
	 * 
	 */
    protected void addChangeListener()
    {
        _listener = new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                if (IJettyConfiguration.__MODIFY_WEB_MODULE_PROPERTY.equals(event.getPropertyName()))
                {
                    initialize();
                }
                else if (IJettyConfiguration.__ADD_WEB_MODULE_PROPERTY.equals(event.getPropertyName()))
                {
                    initialize();
                }
                else if (IJettyConfiguration.__REMOVE_WEB_MODULE_PROPERTY.equals(event.getPropertyName()))
                {
                    initialize();
                }
            }
        };
        _configuration.addPropertyChangeListener(_listener);
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent)
    {
        FormToolkit toolkit = getFormToolkit(parent.getDisplay());

        ScrolledForm form = toolkit.createScrolledForm(parent);
        toolkit.decorateFormHeading(form.getForm());
        form.setText(Messages.configurationEditorWebModulesPageTitle);
        form.setImage(JettyUIPlugin.getImage(JettyUIPlugin.__IMG_WEB_MODULE));
        GridLayout layout = new GridLayout();
        layout.marginTop = 6;
        layout.marginLeft = 6;
        form.getBody().setLayout(layout);

        Section section = toolkit.createSection(form.getBody(),ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
        section.setText(Messages.configurationEditorWebModulesSection);
        section.setDescription(Messages.configurationEditorWebModulesDescription);
        section.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite composite = toolkit.createComposite(section);
        layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 5;
        layout.marginWidth = 10;
        layout.verticalSpacing = 5;
        layout.horizontalSpacing = 15;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
        whs.setHelp(composite,ContextIds.CONFIGURATION_EDITOR_WEBMODULES);
        toolkit.paintBordersFor(composite);
        section.setClient(composite);

        _webAppTable = toolkit.createTable(composite,SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
        _webAppTable.setHeaderVisible(true);
        _webAppTable.setLinesVisible(true);
        whs.setHelp(_webAppTable,ContextIds.CONFIGURATION_EDITOR_WEBMODULES_LIST);
        // toolkit.paintBordersFor(webAppTable);

        TableLayout tableLayout = new TableLayout();

        TableColumn col = new TableColumn(_webAppTable,SWT.NONE);
        col.setText(Messages.configurationEditorPathColumn);
        ColumnWeightData colData = new ColumnWeightData(8,85,true);
        tableLayout.addColumnData(colData);

        TableColumn col2 = new TableColumn(_webAppTable,SWT.NONE);
        col2.setText(Messages.configurationEditorDocBaseColumn);
        colData = new ColumnWeightData(13,135,true);
        tableLayout.addColumnData(colData);

        TableColumn col3 = new TableColumn(_webAppTable,SWT.NONE);
        col3.setText(Messages.configurationEditorProjectColumn);
        colData = new ColumnWeightData(8,85,true);
        tableLayout.addColumnData(colData);

        // TableColumn col4 = new TableColumn(webAppTable, SWT.NONE);
        // col4.setText(Messages.configurationEditorReloadColumn);
        // colData = new ColumnWeightData(7, 75, true);
        // tableLayout.addColumnData(colData);

        _webAppTable.setLayout(tableLayout);

        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = 450;
        data.heightHint = 120;
        _webAppTable.setLayoutData(data);
        _webAppTable.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                selectWebApp();
            }
        });

        Composite rightPanel = toolkit.createComposite(composite);
        layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        rightPanel.setLayout(layout);
        data = new GridData();
        rightPanel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
        // toolkit.paintBordersFor(rightPanel);

        // buttons still to add:
        // add project, add external module, remove module
        _addProjectButton = toolkit.createButton(rightPanel,Messages.configurationEditorAddProjectModule,SWT.PUSH);
        data = new GridData(GridData.FILL_HORIZONTAL);
        _addProjectButton.setLayoutData(data);
        whs.setHelp(_addProjectButton,ContextIds.CONFIGURATION_EDITOR_WEBMODULES_ADD_PROJECT);

        // disable the add project module button if there are no
        // web projects in the workbench
        if (!canAddWebModule())
            _addProjectButton.setEnabled(false);
        else
        {
            _addProjectButton.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    WebModuleDialog dialog = new WebModuleDialog(getEditorSite().getShell(),getServer(),_server2,_configuration,true);
                    dialog.open();
                    if (dialog.getReturnCode() == IDialogConstants.OK_ID)
                    {
                        execute(new AddModuleCommand(getServer(),dialog._module4));
                    }
                }
            });
        }

        _addExtProjectButton = toolkit.createButton(rightPanel,Messages.configurationEditorAddExternalModule,SWT.PUSH);
        data = new GridData(GridData.FILL_HORIZONTAL);
        _addExtProjectButton.setLayoutData(data);
        _addExtProjectButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                WebModuleDialog dialog = new WebModuleDialog(getEditorSite().getShell(),getServer(),_server2,_configuration,false);
                dialog.open();
                if (dialog.getReturnCode() == IDialogConstants.OK_ID)
                {
                    execute(new AddWebModuleCommand(_configuration,dialog.getWebModule()));
                }
            }
        });
        whs.setHelp(_addExtProjectButton,ContextIds.CONFIGURATION_EDITOR_WEBMODULES_ADD_EXTERNAL);

        _editButton = toolkit.createButton(rightPanel,Messages.editorEdit,SWT.PUSH);
        data = new GridData(GridData.FILL_HORIZONTAL);
        _editButton.setLayoutData(data);
        _editButton.setEnabled(false);
        _editButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (_selection < 0)
                    return;
                WebModule module = (WebModule)_configuration.getWebModules().get(_selection);
                WebModuleDialog dialog = new WebModuleDialog(getEditorSite().getShell(),getServer(),_server2,_configuration,module);
                dialog.open();
                if (dialog.getReturnCode() == IDialogConstants.OK_ID)
                {
                    execute(new ModifyWebModuleCommand(_configuration,_selection,dialog.getWebModule()));
                }
            }
        });
        whs.setHelp(_editButton,ContextIds.CONFIGURATION_EDITOR_WEBMODULES_EDIT);

        _removeButton = toolkit.createButton(rightPanel,Messages.editorRemove,SWT.PUSH);
        data = new GridData(GridData.FILL_HORIZONTAL);
        _removeButton.setLayoutData(data);
        _removeButton.setEnabled(false);
        _removeButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (_selection < 0)
                    return;
                TableItem item = _webAppTable.getItem(_selection);
                if (item.getData() != null)
                {
                    IModule module = (IModule)item.getData();
                    execute(new RemoveModuleCommand(getServer(),module));
                }
                else
                {
                    execute(new RemoveWebModuleCommand(_configuration,_selection));
                }
                _removeButton.setEnabled(false);
                _editButton.setEnabled(false);
                _selection = -1;
            }
        });
        whs.setHelp(_removeButton,ContextIds.CONFIGURATION_EDITOR_WEBMODULES_REMOVE);

        form.setContent(section);
        form.reflow(true);

        initialize();
    }

    protected boolean canAddWebModule()
    {
        IModule[] modules = ServerUtil.getModules(server.getServerType().getRuntimeType().getModuleTypes());
        if (modules != null)
        {
            int size = modules.length;
            for (int i = 0; i < size; i++)
            {
                IWebModule webModule = (IWebModule)modules[i].loadAdapter(IWebModule.class,null);
                if (webModule != null)
                {
                    IStatus status = server.canModifyModules(new IModule[]
                    { modules[i] },null,null);
                    if (status != null && status.isOK())
                        return true;
                }
            }
        }
        return false;
    }

    public void dispose()
    {
        super.dispose();

        if (_configuration != null)
            _configuration.removePropertyChangeListener(_listener);
    }

    /*
     * (non-Javadoc) Initializes the editor part with a site and input.
     */
    public void init(IEditorSite site, IEditorInput input)
    {
        super.init(site,input);

        IJettyServer ts = (IJettyServer)server.loadAdapter(IJettyServer.class,null);
        try
        {
            _configuration = (IJettyConfigurationWorkingCopy)ts.getJettyConfiguration();
        }
        catch (Exception e)
        {
            // ignore
        }
        if (_configuration != null)
            addChangeListener();

        if (server != null)
            _server2 = (IJettyServerWorkingCopy)server.loadAdapter(IJettyServerWorkingCopy.class,null);

        initialize();
    }

    /**
	 * 
	 */
    protected void initialize()
    {
        if (_webAppTable == null)
            return;

        _webAppTable.removeAll();
        setErrorMessage(null);

        ILabelProvider labelProvider = ServerUICore.getLabelProvider();
        List<WebModule> list = _configuration.getWebModules();
        Iterator<WebModule> iterator = list.iterator();
        
        while (iterator.hasNext())
        {
            WebModule module = iterator.next();
            TableItem item = new TableItem(_webAppTable,SWT.NONE);

            String memento = module.getMemento();
            String projectName = "";
            Image projectImage = null;
            if (memento != null && memento.length() > 0)
            {
                projectName = NLS.bind(Messages.configurationEditorProjectMissing,new String[]
                { memento });
                projectImage = JettyUIPlugin.getImage(JettyUIPlugin.__IMG_PROJECT_MISSING);
                IModule module2 = ServerUtil.getModule(memento);
                if (module2 != null)
                {
                    projectName = labelProvider.getText(module2);
                    projectImage = labelProvider.getImage(module2);
                    item.setData(module2);
                }
            }

            String reload = module.isReloadable()?Messages.configurationEditorReloadEnabled:Messages.configurationEditorReloadDisabled;
            String[] s = new String[]
            { module.getPath(), module.getDocumentBase(), projectName, reload };
            item.setText(s);
            item.setImage(0,JettyUIPlugin.getImage(JettyUIPlugin.__IMG_WEB_MODULE));
            if (projectImage != null)
                item.setImage(2,projectImage);

            if (!isDocumentBaseValid(module.getDocumentBase()))
            {
                item.setImage(1,JettyUIPlugin.getImage(JettyUIPlugin.__IMG_PROJECT_MISSING));
                setErrorMessage(NLS.bind(Messages.errorMissingWebModule,module.getDocumentBase()));
            }
        }
        labelProvider = null;

        if (readOnly)
        {
            _addProjectButton.setEnabled(false);
            _addExtProjectButton.setEnabled(false);
            _editButton.setEnabled(false);
            _removeButton.setEnabled(false);
        }
        else
        {
            _addProjectButton.setEnabled(canAddWebModule());
            _addExtProjectButton.setEnabled(true);
        }
    }

    /**
	 * 
	 */
    protected void selectWebApp()
    {
        if (readOnly)
            return;

        try
        {
            _selection = _webAppTable.getSelectionIndex();
            _removeButton.setEnabled(true);
            _editButton.setEnabled(true);
        }
        catch (Exception e)
        {
            _selection = -1;
            _removeButton.setEnabled(false);
            _editButton.setEnabled(false);
        }
    }

    protected boolean isDocumentBaseValid(String s)
    {
        if (s == null || s.length() < 2)
            return true;

        // check absolute path
        File f = new File(s);
        if (f.exists())
            return true;

        // check workspace
        try
        {
            if (ResourcesPlugin.getWorkspace().getRoot().getProject(s).exists())
                return true;
        }
        catch (Exception e)
        {
            // bad path
        }

        if (s.startsWith(_configuration.getDocBasePrefix()))
        {
            try
            {
                String t = s.substring(_configuration.getDocBasePrefix().length());
                if (ResourcesPlugin.getWorkspace().getRoot().getProject(t).exists())
                    return true;
            }
            catch (Exception e)
            {
                // bad path
            }
        }

        // check server relative path
        try
        {
            f = server.getRuntime().getLocation().append(s).toFile();
            if (f.exists())
                return true;
        }
        catch (Exception e)
        {
            // bad path
        }

        return false;
    }

    /*
     * @see IWorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        if (_webAppTable != null)
            _webAppTable.setFocus();
    }
}
