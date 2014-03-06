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

public interface ContextIds
{

    public static final String RUNTIME_COMPOSITE = JettyUIPlugin.__PLUGIN_ID + ".twnr0000";

    public static final String CONFIGURATION_EDITOR_PORTS = JettyUIPlugin.__PLUGIN_ID + ".tecp0000";
    public static final String CONFIGURATION_EDITOR_PORTS_LIST = JettyUIPlugin.__PLUGIN_ID + ".tecp0002";

    public static final String CONFIGURATION_EDITOR_WEBMODULES = JettyUIPlugin.__PLUGIN_ID + ".tecw0000";
    public static final String CONFIGURATION_EDITOR_WEBMODULES_LIST = JettyUIPlugin.__PLUGIN_ID + ".tecw0002";
    public static final String CONFIGURATION_EDITOR_WEBMODULES_ADD_PROJECT = JettyUIPlugin.__PLUGIN_ID + ".tecw0004";
    public static final String CONFIGURATION_EDITOR_WEBMODULES_ADD_EXTERNAL = JettyUIPlugin.__PLUGIN_ID + ".tecw0006";
    public static final String CONFIGURATION_EDITOR_WEBMODULES_EDIT = JettyUIPlugin.__PLUGIN_ID + ".tecw0008";
    public static final String CONFIGURATION_EDITOR_WEBMODULES_REMOVE = JettyUIPlugin.__PLUGIN_ID + ".tecw0010";

    public static final String CONFIGURATION_EDITOR_WEBMODULE_DIALOG = JettyUIPlugin.__PLUGIN_ID + ".tdwm0000";
    public static final String CONFIGURATION_EDITOR_WEBMODULE_DIALOG_PROJECT = JettyUIPlugin.__PLUGIN_ID + ".tdpr0002";
    public static final String CONFIGURATION_EDITOR_WEBMODULE_DIALOG_PATH = JettyUIPlugin.__PLUGIN_ID + ".tdpr0004";
    public static final String CONFIGURATION_EDITOR_WEBMODULE_DIALOG_DOCBASE = JettyUIPlugin.__PLUGIN_ID + ".tdpr0006";
}
