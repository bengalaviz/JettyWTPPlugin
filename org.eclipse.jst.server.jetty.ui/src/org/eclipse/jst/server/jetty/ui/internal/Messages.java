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
package org.eclipse.jst.server.jetty.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

    public static String wizardTitle;
    public static String wizardDescription;
    public static String runtimeName;
    public static String browse;
    public static String selectInstallDir;
    public static String installDir;
    public static String install;
    public static String installDialogTitle;
    public static String installedJRE;
    public static String installedJREs;
    public static String runtimeDefaultJRE;
    public static String configurationEditorPortsSection;
    public static String configurationEditorPortsDescription;
    public static String configurationEditorPortNameColumn;
    public static String configurationEditorPortValueColumn;
    public static String configurationEditorWebModuleDialogTitleEdit;
    public static String configurationEditorWebModuleDialogTitleAdd;
    public static String configurationEditorWebModuleDialogProjects;
    public static String configurationEditorWebModuleDialogDocumentBase;
    public static String configurationEditorWebModuleDialogSelectDirectory;
    public static String configurationEditorWebModuleDialogPath;
    public static String configurationEditorPathColumn;
    public static String configurationEditorDocBaseColumn;
    public static String configurationEditorProjectColumn;
    public static String configurationEditorWebModulesPageTitle;
    public static String configurationEditorWebModulesSection;
    public static String configurationEditorWebModulesDescription;
    public static String configurationEditorAddProjectModule;
    public static String configurationEditorAddExternalModule;
    public static String editorEdit;
    public static String editorRemove;
    public static String errorMissingWebModule;
    public static String configurationEditorReloadDisabled;
    public static String configurationEditorReloadEnabled;
    public static String configurationEditorProjectMissing;

    static
    {
        NLS.initializeMessages(JettyUIPlugin.__PLUGIN_ID + ".internal.Messages",Messages.class);
    }
}
