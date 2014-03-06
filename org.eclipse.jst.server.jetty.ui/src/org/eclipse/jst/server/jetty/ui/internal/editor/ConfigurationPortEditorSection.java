/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Angelo Zerr <angelo.zerr@gmail.com> - Jetty packages
 *******************************************************************************/
package org.eclipse.jst.server.jetty.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jst.server.jetty.core.IJettyConfiguration;
import org.eclipse.jst.server.jetty.core.IJettyConfigurationWorkingCopy;
import org.eclipse.jst.server.jetty.core.IJettyServer;
import org.eclipse.jst.server.jetty.core.command.ModifyPortCommand;
import org.eclipse.jst.server.jetty.ui.internal.ContextIds;
import org.eclipse.jst.server.jetty.ui.internal.JettyUIPlugin;
import org.eclipse.jst.server.jetty.ui.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.ServerPort;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

/**
 * Jetty configuration port editor page.
 */
public class ConfigurationPortEditorSection extends ServerEditorSection
{
    protected IJettyConfigurationWorkingCopy _jettyConfiguration;

    protected boolean _updating;

    protected Table _ports;
    protected TableViewer _viewer;

    protected PropertyChangeListener _listener;

    /**
     * ConfigurationPortEditorSection constructor comment.
     */
    public ConfigurationPortEditorSection()
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
                if (IJettyConfiguration.__MODIFY_PORT_PROPERTY.equals(event.getPropertyName()))
                {
                    String id = (String)event.getOldValue();
                    Integer i = (Integer)event.getNewValue();
                    changePortNumber(id,i.intValue());
                }
            }
        };
        _jettyConfiguration.addPropertyChangeListener(_listener);
    }

    /**
     * 
     * @param id
     *            java.lang.String
     * @param port
     *            int
     */
    protected void changePortNumber(String id, int port)
    {
        TableItem[] items = _ports.getItems();
        int size = items.length;
        for (int i = 0; i < size; i++)
        {
            ServerPort sp = (ServerPort)items[i].getData();
            if (sp.getId().equals(id))
            {
                items[i].setData(new ServerPort(id,sp.getName(),port,sp.getProtocol()));
                items[i].setText(1,port + "");
                /*
                 * if (i == selection) { selectPort(); }
                 */
                return;
            }
        }
    }

    /**
     * Creates the SWT controls for this workbench part.
     * 
     * @param parent
     *            the parent control
     */
    public void createSection(Composite parent)
    {
        super.createSection(parent);
        FormToolkit toolkit = getFormToolkit(parent.getDisplay());

        Section section = toolkit.createSection(parent,ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR
                | Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
        section.setText(Messages.configurationEditorPortsSection);
        section.setDescription(Messages.configurationEditorPortsDescription);
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

        // ports
        Composite composite = toolkit.createComposite(section);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 8;
        layout.marginWidth = 8;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.FILL_HORIZONTAL));
        IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
        whs.setHelp(composite,ContextIds.CONFIGURATION_EDITOR_PORTS);
        toolkit.paintBordersFor(composite);
        section.setClient(composite);

        _ports = toolkit.createTable(composite,SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
        _ports.setHeaderVisible(true);
        _ports.setLinesVisible(true);
        whs.setHelp(_ports,ContextIds.CONFIGURATION_EDITOR_PORTS_LIST);

        TableLayout tableLayout = new TableLayout();

        TableColumn col = new TableColumn(_ports,SWT.NONE);
        col.setText(Messages.configurationEditorPortNameColumn);
        ColumnWeightData colData = new ColumnWeightData(15,150,true);
        tableLayout.addColumnData(colData);

        col = new TableColumn(_ports,SWT.NONE);
        col.setText(Messages.configurationEditorPortValueColumn);
        colData = new ColumnWeightData(8,80,true);
        tableLayout.addColumnData(colData);

        GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
        data.widthHint = 230;
        data.heightHint = 100;
        _ports.setLayoutData(data);
        _ports.setLayout(tableLayout);

        _viewer = new TableViewer(_ports);
        _viewer.setColumnProperties(new String[]
        { "name", "port" });

        initialize();
    }

    protected void setupPortEditors()
    {
        _viewer.setCellEditors(new CellEditor[]
        { null, new TextCellEditor(_ports) });

        ICellModifier cellModifier = new ICellModifier()
        {
            public Object getValue(Object element, String property)
            {
                ServerPort sp = (ServerPort)element;
                if (sp.getPort() < 0)
                    return "-";
                return sp.getPort() + "";
            }

            public boolean canModify(Object element, String property)
            {
                if ("port".equals(property))
                    return true;

                return false;
            }

            public void modify(Object element, String property, Object value)
            {
                try
                {
                    Item item = (Item)element;
                    ServerPort sp = (ServerPort)item.getData();
                    int port = Integer.parseInt((String)value);
                    execute(new ModifyPortCommand(_jettyConfiguration,sp.getId(),port));
                }
                catch (Exception ex)
                {
                    // ignore
                }
            }
        };
        _viewer.setCellModifier(cellModifier);

        // preselect second column (Windows-only)
        String os = System.getProperty("os.name");
        if (os != null && os.toLowerCase().indexOf("win") >= 0)
        {
            _ports.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent event)
                {
                    try
                    {
                        int n = _ports.getSelectionIndex();
                        _viewer.editElement(_ports.getItem(n).getData(),1);
                    }
                    catch (Exception e)
                    {
                        // ignore
                    }
                }
            });
        }
    }

    public void dispose()
    {
        if (_jettyConfiguration != null)
            _jettyConfiguration.removePropertyChangeListener(_listener);
    }

    /*
     * (non-Javadoc) Initializes the editor part with a site and input.
     */
    public void init(IEditorSite site, IEditorInput input)
    {
        super.init(site,input);

        IJettyServer ts = (IJettyServer)server.getAdapter(IJettyServer.class);
        try
        {
            if (ts == null)
            {
                ts = (IJettyServer)server.loadAdapter(IJettyServer.class,null);
            }
            _jettyConfiguration = (IJettyConfigurationWorkingCopy)ts.getJettyConfiguration();
        }
        catch (Exception e)
        {
            // ignore
        }
        addChangeListener();
        initialize();
    }

    /**
     * Initialize the fields in this editor.
     */
    protected void initialize()
    {
        if (_ports == null)
            return;

        _ports.removeAll();

        Iterator<ServerPort> iterator = _jettyConfiguration.getServerPorts().iterator();
        
        while (iterator.hasNext())
        {
            ServerPort port = iterator.next();
            TableItem item = new TableItem(_ports,SWT.NONE);
            String portStr = "-";
            if (port.getPort() >= 0)
                portStr = port.getPort() + "";
            String[] s = new String[]
            { port.getName(), portStr };
            item.setText(s);
            item.setImage(JettyUIPlugin.getImage(JettyUIPlugin.__IMG_PORT));
            item.setData(port);
        }

        if (readOnly)
        {
            _viewer.setCellEditors(new CellEditor[]
            { null, null });
            _viewer.setCellModifier(null);
        }
        else
        {
            setupPortEditors();
        }
    }
}
