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
package org.eclipse.jst.server.jetty.core.command;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.jetty.core.IJettyConfigurationWorkingCopy;

/**
 * Configuration command.
 */
public abstract class ConfigurationCommand extends AbstractOperation
{
    private IJettyConfigurationWorkingCopy _configuration;

    /**
     * ConfigurationCommand constructor comment.
     * 
     * @param configuration
     *            a Jetty configuration
     * @param label
     *            a label
     */
    public ConfigurationCommand(IJettyConfigurationWorkingCopy configuration, String label)
    {
        super(label);
        this._configuration = configuration;
    }

    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
    {
        return execute(monitor,info);
    }

    public abstract void execute();

    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
    {
        execute();
        return null;
    }

    public abstract void undo();

    public IJettyConfigurationWorkingCopy getWorkingCopy()
    {
        return _configuration;
    }
    
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException
    {
        undo();
        return null;
    }
}