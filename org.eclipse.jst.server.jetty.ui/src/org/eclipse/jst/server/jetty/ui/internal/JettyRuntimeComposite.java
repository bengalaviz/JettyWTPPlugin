/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - Jetty packages 
 *******************************************************************************/
package org.eclipse.jst.server.jetty.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.server.jetty.core.IJettyRuntimeWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.internal.IInstallableRuntime;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.fragment.LicenseWizardFragment;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * Wizard page to set the server install directory.
 */
public class JettyRuntimeComposite extends Composite
{
    protected IRuntimeWorkingCopy _runtimeWC;
    protected IJettyRuntimeWorkingCopy _jettyRuntimeWC;

    protected IWizardHandle _wizard;

    protected Text _installDir;
    protected Text _name;
    protected Combo _combo;
    protected List<IVMInstall> _installedJREs;
    protected String[] _jreNames;
    protected IInstallableRuntime _installableRuntime;
    protected Job _installableRuntimeJob;
    protected IJobChangeListener _jobListener;
    protected Label _installLabel;
    protected Button _installButton;

    /**
     * JettyRuntimeWizardPage constructor comment.
     * 
     * @param parent
     *            the parent composite
     * @param wizard
     *            the wizard handle
     */
    protected JettyRuntimeComposite(Composite parent, IWizardHandle wizard)
    {
        super(parent,SWT.NONE);
        this._wizard = wizard;

        wizard.setTitle(Messages.wizardTitle);
        wizard.setDescription(Messages.wizardDescription);
        wizard.setImageDescriptor(JettyUIPlugin.getImageDescriptor(JettyUIPlugin.__IMG_WIZ_JETTY));

        createControl();
    }

    protected void setRuntime(IRuntimeWorkingCopy newRuntime)
    {
        if (newRuntime == null)
        {
            _runtimeWC = null;
            _jettyRuntimeWC = null;
        }
        else
        {
            _runtimeWC = newRuntime;
            _jettyRuntimeWC = (IJettyRuntimeWorkingCopy)newRuntime.loadAdapter(IJettyRuntimeWorkingCopy.class,null);
        }

        if (_runtimeWC == null)
        {
            _installableRuntime = null;
            _installButton.setEnabled(false);
            _installLabel.setText("");
        }
        else
        {
            _installableRuntime = ServerPlugin.findInstallableRuntime(_runtimeWC.getRuntimeType().getId());
            if (_installableRuntime != null)
            {
                _installButton.setEnabled(true);
                _installLabel.setText(_installableRuntime.getName());
            }
        }

        init();
        validate();
    }

    public void dispose()
    {
        super.dispose();
        if (_installableRuntimeJob != null)
        {
            _installableRuntimeJob.removeJobChangeListener(_jobListener);
        }
    }

    /**
     * Provide a wizard page to change the Jetty installation directory.
     */
    protected void createControl()
    {        
        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: createControl(): start");

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        setLayout(layout);
        setLayoutData(new GridData(GridData.FILL_BOTH));
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this,ContextIds.RUNTIME_COMPOSITE);

        Label label = new Label(this,SWT.NONE);
        label.setText(Messages.runtimeName);
        GridData data = new GridData();
        data.horizontalSpan = 2;
        label.setLayoutData(data);

        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: createControl(): name field management");
        _name = new Text(this,SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        _name.setLayoutData(data);
        _name.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                _runtimeWC.setName(_name.getText());
                validate();
            }
        });

        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: createControl(): install location management");

        label = new Label(this,SWT.NONE);
        label.setText(Messages.installDir);
        data = new GridData();
        data.horizontalSpan = 2;
        label.setLayoutData(data);

        _installDir = new Text(this,SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        _installDir.setLayoutData(data);
        _installDir.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                _runtimeWC.setLocation(new Path(_installDir.getText()));
                validate();
            }
        });

        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: createControl(): browse for location management");

        Button browse = SWTUtil.createButton(this,Messages.browse);
        browse.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent se)
            {
                DirectoryDialog dialog = new DirectoryDialog(JettyRuntimeComposite.this.getShell());
                dialog.setMessage(Messages.selectInstallDir);
                dialog.setFilterPath(_installDir.getText());
                String selectedDirectory = dialog.open();
                if (selectedDirectory != null)
                    _installDir.setText(selectedDirectory);
            }
        });

        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: createControl(): installable runtime management");

        _installLabel = new Label(this,SWT.RIGHT);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalIndent = 10;
        _installLabel.setLayoutData(data);

        _installButton = SWTUtil.createButton(this,Messages.install);
        _installButton.setEnabled(false);
        _installButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent se)
            {
                String license = null;
                try
                {
                    Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: createControl(): getting license");

                    license = _installableRuntime.getLicense(new NullProgressMonitor());
                }
                catch (CoreException e)
                {
                    Trace.trace(Trace.SEVERE,"Error getting license",e);
                }
                TaskModel taskModel = new TaskModel();
                taskModel.putObject(LicenseWizardFragment.LICENSE,license);
                TaskWizard wizard2 = new TaskWizard(Messages.installDialogTitle,new WizardFragment()
                {
                    protected void createChildFragments(List list)
                    {
                        list.add(new LicenseWizardFragment());
                    }
                },taskModel);

                WizardDialog dialog2 = new WizardDialog(getShell(),wizard2);
                if (dialog2.open() == Window.CANCEL)
                {
                    Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: createControl(): not accepting license");

                    return;
                }

                
                DirectoryDialog dialog = new DirectoryDialog(JettyRuntimeComposite.this.getShell());
                dialog.setMessage(Messages.selectInstallDir);
                dialog.setFilterPath(_installDir.getText());
                String selectedDirectory = dialog.open();
                if (selectedDirectory != null)
                {
                    // ir.install(new Path(selectedDirectory));
                    final IPath installPath = new Path(selectedDirectory);
                    _installableRuntimeJob = new Job("Installing server runtime environment")
                    {
                        public boolean belongsTo(Object family)
                        {
                            return ServerPlugin.PLUGIN_ID.equals(family);
                        }

                        protected IStatus run(IProgressMonitor monitor)
                        {
                            try
                            {
                                Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: createControl(): installable runtime job start");
                                _installableRuntime.install(installPath,monitor);
                                Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: createControl(): installable runtime job end");
                            }
                            catch (CoreException ce)
                            {
                                return ce.getStatus();
                            }

                            return Status.OK_STATUS;
                        }
                    };

                    _installDir.setText(selectedDirectory);
                    _jobListener = new JobChangeAdapter()
                    {
                        public void done(IJobChangeEvent event)
                        {
                            Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: createControl(): installable runtime job done signal");

                            _installableRuntimeJob.removeJobChangeListener(this);
                            _installableRuntimeJob = null;
                            Display.getDefault().asyncExec(new Runnable()
                            {
                                public void run()
                                {
                                    if (!isDisposed())
                                    {
                                        validate();
                                    }
                                }
                            });
                        }
                    };
                    _installableRuntimeJob.addJobChangeListener(_jobListener);
                    _installableRuntimeJob.schedule();
                }
            }
        });

        updateJREs();

        // JDK location
        label = new Label(this,SWT.NONE);
        label.setText(Messages.installedJRE);
        data = new GridData();
        data.horizontalSpan = 2;
        label.setLayoutData(data);

        _combo = new Combo(this,SWT.DROP_DOWN | SWT.READ_ONLY);
        _combo.setItems(_jreNames);
        data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        _combo.setLayoutData(data);

        _combo.addSelectionListener(new SelectionListener()
        {
            public void widgetSelected(SelectionEvent e)
            {
                int sel = _combo.getSelectionIndex();
                IVMInstall vmInstall = null;
                if (sel > 0)
                    vmInstall = (IVMInstall)_installedJREs.get(sel - 1);

                _jettyRuntimeWC.setVMInstall(vmInstall);
                validate();
            }

            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }
        });

        Button button = SWTUtil.createButton(this,Messages.installedJREs);
        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                String currentVM = _combo.getText();
                if (showPreferencePage())
                {
                    updateJREs();
                    _combo.setItems(_jreNames);
                    _combo.setText(currentVM);
                    if (_combo.getSelectionIndex() == -1)
                        _combo.select(0);
                    validate();
                }
            }
        });

        init();
        validate();

        Dialog.applyDialogFont(this);

        _name.forceFocus();
        
        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: createControl(): end");

    }

    protected void updateJREs()
    {
        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: updateJRE(): start");

        // get all installed JVMs
        _installedJREs = new ArrayList<IVMInstall>();
        IVMInstallType[] vmInstallTypes = JavaRuntime.getVMInstallTypes();
        int size = vmInstallTypes.length;
        for (int i = 0; i < size; i++)
        {
            IVMInstall[] vmInstalls = vmInstallTypes[i].getVMInstalls();
            int size2 = vmInstalls.length;
            for (int j = 0; j < size2; j++)
            {
                _installedJREs.add(vmInstalls[j]);
            }
        }

        // get names
        size = _installedJREs.size();
        _jreNames = new String[size + 1];
        _jreNames[0] = Messages.runtimeDefaultJRE;
        for (int i = 0; i < size; i++)
        {
            IVMInstall vmInstall = (IVMInstall)_installedJREs.get(i);
            _jreNames[i + 1] = vmInstall.getName();
        }
        
        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: updateJRE(): end");
    }

    protected boolean showPreferencePage()
    {
        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: showPreferencePage()");

        String id = "org.eclipse.jdt.debug.ui.preferences.VMPreferencePage";

        // should be using the following API, but it only allows a single
        // preference page instance.
        // see bug 168211 for details
        // PreferenceDialog dialog =
        // PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[]
        // { id }, null);
        // return (dialog.open() == Window.OK);

        PreferenceManager manager = PlatformUI.getWorkbench().getPreferenceManager();
        IPreferenceNode node = manager.find("org.eclipse.jdt.ui.preferences.JavaBasePreferencePage").findSubNode(id);
        PreferenceManager manager2 = new PreferenceManager();
        manager2.addToRoot(node);
        PreferenceDialog dialog = new PreferenceDialog(getShell(),manager2);
        dialog.create();
        return (dialog.open() == Window.OK);
    }

    protected void init()
    {
        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: init(): start");

        if (_name == null || _jettyRuntimeWC == null)
        {
            Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: init(): name or jetty runtime working copy is null");
            
            return;
        }

        if (_runtimeWC.getName() != null)
        {
            _name.setText(_runtimeWC.getName());
        }
        else
        {
            _name.setText("");
        }

        if (_runtimeWC.getLocation() != null)
        {
            _installDir.setText(_runtimeWC.getLocation().toOSString());
        }
        else
        {
            _installDir.setText("");
        }

        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: init(): choosing initial jre");
        if (_jettyRuntimeWC.isUsingDefaultJRE())
        {
            _combo.select(0);
        }
        else
        {
            boolean found = false;
            int size = _installedJREs.size();
            for (int i = 0; i < size; i++)
            {
                IVMInstall vmInstall = (IVMInstall)_installedJREs.get(i);
                if (vmInstall.equals(_jettyRuntimeWC.getVMInstall()))
                {
                    _combo.select(i + 1);
                    found = true;
                }
            }
            
            if (!found)
            {
                _combo.select(0);
            }
        }
        
        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: init(): end");

    }

    protected void validate()
    {
        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: validate(): start");
        
        if (_jettyRuntimeWC == null)
        {
            _wizard.setMessage("",IMessageProvider.ERROR);
            Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: validate(): jetty runtime working copy null");

            return;
        }

        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: validate(): runtime working copy");

        IStatus status = _runtimeWC.validate(null);
        
        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: validate(): runtime working status was " + status.getMessage());

        if (status == null || status.isOK())
        {
            _wizard.setMessage(null,IMessageProvider.NONE);
        }
        else if (status.getSeverity() == IStatus.WARNING)
        {
            _wizard.setMessage(status.getMessage(),IMessageProvider.WARNING);
        }
        else
        {
            _wizard.setMessage(status.getMessage(),IMessageProvider.ERROR);
        }
            
        _wizard.update();
        
        Trace.trace(Trace.CONFIG, "JettyRuntimeComposite: validate(): end");

    }
}
