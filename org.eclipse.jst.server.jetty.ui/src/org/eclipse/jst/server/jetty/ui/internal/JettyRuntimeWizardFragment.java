/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * 
 */
public class JettyRuntimeWizardFragment extends WizardFragment
{
    protected JettyRuntimeComposite _runtimeComposite;

    public JettyRuntimeWizardFragment()
    {
        // do nothing
    }

    public boolean hasComposite()
    {
        return true;
    }

    public boolean isComplete()
    {
        IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

        if (runtime == null)
            return false;
        IStatus status = runtime.validate(null);
        return (status == null || status.getSeverity() != IStatus.ERROR);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.server.ui.task.WizardFragment#createComposite()
     */
    public Composite createComposite(Composite parent, IWizardHandle wizard)
    {
        _runtimeComposite = new JettyRuntimeComposite(parent,wizard);
        return _runtimeComposite;
    }

    public void enter()
    {
        if (_runtimeComposite != null)
        {
            IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);
            _runtimeComposite.setRuntime(runtime);
        }
    }

    public void exit()
    {
        IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);
        IPath path = runtime.getLocation();
        if (runtime.validate(null).getSeverity() != IStatus.ERROR)
        {
            JettyPlugin.setPreference("location" + runtime.getRuntimeType().getId(),path.toString());
        }
    }
}