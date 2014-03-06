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

import org.eclipse.jst.server.jetty.core.IJettyConfigurationWorkingCopy;
import org.eclipse.jst.server.jetty.core.WebModule;
import org.eclipse.jst.server.jetty.core.internal.Messages;

/**
 * Command to change a web module.
 */
public class ModifyWebModuleCommand extends ConfigurationCommand
{
    protected int _index;
    protected WebModule _oldModule;
    protected WebModule _newModule;

    public ModifyWebModuleCommand(IJettyConfigurationWorkingCopy configuration, int index, WebModule module)
    {
        super(configuration,Messages.configurationEditorActionModifyWebModule);
        _index = index;
        _newModule = module;
    }

    /**
     * Execute the command.
     */
    public void execute()
    {
        _oldModule = getWorkingCopy().getWebModules().get(_index);
        getWorkingCopy().modifyWebModule(_index,_newModule.getDocumentBase(),_newModule.getPath(),_newModule.isReloadable());
    }

    /**
     * Undo the command.
     */
    public void undo()
    {
        getWorkingCopy().modifyWebModule(_index,_oldModule.getDocumentBase(),_oldModule.getPath(),_oldModule.isReloadable());
    }
}