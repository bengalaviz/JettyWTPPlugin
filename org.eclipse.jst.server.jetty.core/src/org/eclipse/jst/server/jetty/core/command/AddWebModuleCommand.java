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
package org.eclipse.jst.server.jetty.core.command;

import org.eclipse.jst.server.jetty.core.IJettyConfigurationWorkingCopy;
import org.eclipse.jst.server.jetty.core.WebModule;
import org.eclipse.jst.server.jetty.core.internal.Messages;

/**
 * Command to add a web module.
 */
public class AddWebModuleCommand extends ConfigurationCommand
{
    protected WebModule _module;
    protected int _moduleCount = -1;

    /**
     * AddWebModuleCommand constructor comment.
     * 
     * @param configuration
     *            a Jetty configuration
     * @param module
     *            a web module
     */
    public AddWebModuleCommand(IJettyConfigurationWorkingCopy configuration, WebModule module)
    {
        super(configuration,Messages.configurationEditorActionAddWebModule);
        this._module = module;
    }

    /**
     * Execute the command.
     */
    public void execute()
    {
        _moduleCount = getWorkingCopy().getWebModules().size();
        getWorkingCopy().addWebModule(-1,_module);
    }

    /**
     * Undo the command.
     */
    public void undo()
    {
        getWorkingCopy().removeWebModule(_moduleCount);
    }
}
