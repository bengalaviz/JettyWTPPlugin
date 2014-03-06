/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Angelo Zerr <angelo.zerr@gmail.com> - Jetty packages
 *******************************************************************************/
package org.eclipse.jst.server.jetty.core.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jst.server.jetty.core.JettyPlugin;

/**
 * Helper class to route trace output.
 */
public class Trace
{
    public static final byte CONFIG = 0;
    public static final byte PERF = 1;
    public static final byte WARNING = 2;
    public static final byte SEVERE = 3;
    public static final byte FINEST = 4;

    private static final String[] levelNames = new String[]
    { "CONFIG   ", "PERF     ", "WARNING  ", "SEVERE   ", "FINER    ", "FINEST   " };
    private static final String spacer = "                                   ";

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm.ss.SSS");

    protected static int pluginLength = -1;

    /**
     * Trace constructor comment.
     */
    private Trace()
    {
        super();
    }

    /**
     * Trace the given text.
     * 
     * @param level
     *            the trace level
     * @param s
     *            a message
     */
    public static void trace(byte level, String s)
    {
        Trace.trace(level,s,null);
    }

    /**
     * Trace the given message and exception.
     * 
     * @param level
     *            the trace level
     * @param s
     *            a message
     * @param t
     *            a throwable
     */
    public static void trace(byte level, String s, Throwable t)
    {
        if (!JettyPlugin.getDefault().isDebugging())
            return;
        trace(JettyPlugin.PLUGIN_ID,level,s,t);
    }

    /**
     * Trace the given message and exception.
     * 
     * @param level
     *            a trace level
     * @param s
     *            a message
     * @param t
     *            a throwable
     */
    private static void trace(String pluginId, int level, String s, Throwable t)
    {
        if (pluginId == null || s == null)
            return;

        if (!JettyPlugin.getDefault().isDebugging())
            return;

        StringBuilder sb = new StringBuilder(pluginId);
        if (pluginId.length() > pluginLength)
            pluginLength = pluginId.length();
        else if (pluginId.length() < pluginLength)
            sb.append(spacer.substring(0,pluginLength - pluginId.length()));
        sb.append(" ");
        sb.append(levelNames[level]);
        sb.append(" ");
        sb.append(sdf.format(new Date()));
        sb.append(" ");
        sb.append(s);
        // Platform.getDebugOption(ServerCore.PLUGIN_ID + "/" + "resources");

        System.out.println(sb.toString());
        if (t != null)
            t.printStackTrace();
    }

    /**
     * Gets state of debug flag for the plug-in.
     * 
     * @return true if tracing is enabled
     */
    public static boolean isTraceEnabled()
    {
        return JettyPlugin.getDefault().isDebugging();
    }
}
