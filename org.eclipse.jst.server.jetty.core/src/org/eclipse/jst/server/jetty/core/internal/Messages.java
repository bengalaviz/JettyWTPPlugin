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
package org.eclipse.jst.server.jetty.core.internal;

import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.osgi.util.NLS;

/**
 * Jetty Core Messages.
 * 
 */
public class Messages extends NLS
{

    public static String errorInstallDirTrailingSlash;
    public static String errorJRE;
    public static String portServer;
    public static String errorConfigurationProjectClosed;
    public static String errorNoConfiguration;
    public static String errorWebModulesOnly;
    public static String errorNoRuntime;
    public static String errorPortInvalid;
    public static String errorPortInUse;
    public static String errorPortsInUse;
    public static String errorDuplicateContextRoot;
    public static String errorSpec70;
    public static String errorCouldNotLoadConfiguration;
    public static String errorCouldNotSaveConfiguration;
    public static String savingTask;
    public static String loadingTask;
    public static String errorPublish;
    public static String startJarRequiredInstallDirStatus;
    public static String publishServerTask;
    public static String errorPublishCouldNotRemoveModule;
    public static String copyingTask;
    public static String errorCopyingFile;
    public static String publishConfigurationTask;
    public static String publisherPublishTask;
    public static String errorPublishConfiguration;
    public static String errorXMLNullContextArg;
    public static String configurationEditorActionModifyPort;
    public static String configurationEditorActionAddWebModule;
    public static String configurationEditorActionModifyWebModule;
    public static String configurationEditorActionRemoveWebModule;

    static
    {
        NLS.initializeMessages(JettyPlugin.PLUGIN_ID + ".internal.Messages",Messages.class);
    }
}
