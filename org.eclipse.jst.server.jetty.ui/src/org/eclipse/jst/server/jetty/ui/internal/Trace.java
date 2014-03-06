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
package org.eclipse.jst.server.jetty.ui.internal;

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
        Trace.trace(level,s,(Throwable)null);
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
        if (!JettyUIPlugin.getDefault().isDebugging())
            return;

        System.out.println(JettyUIPlugin.__PLUGIN_ID + " " + s);
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
        return JettyUIPlugin.getDefault().isDebugging();
    }
}
