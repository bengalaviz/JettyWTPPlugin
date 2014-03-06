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
package org.eclipse.jst.server.jetty.ui.websocket;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.j2ee.internal.plugin.J2EEPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class JettyUIWebSocketPlugin extends AbstractUIPlugin
{

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.jst.server.jetty.ui.websocket"; //$NON-NLS-1$

    // The shared instance
    private static JettyUIWebSocketPlugin plugin;

    /**
     * The constructor
     */
    public JettyUIWebSocketPlugin()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static JettyUIWebSocketPlugin getDefault()
    {
        return plugin;
    }

    /**
     * This gets a .gif from the icons folder.
     */
    public ImageDescriptor getImageDescriptor(String key)
    {
        ImageDescriptor imageDescriptor = null;
        URL gifImageURL = getImageURL(key);
        if (gifImageURL != null)
            imageDescriptor = ImageDescriptor.createFromURL(gifImageURL);
        return imageDescriptor;
    }

    /**
     * @param key
     * @return
     */
    private URL getImageURL(String key)
    {
        return J2EEPlugin.getImageURL(key,getBundle());
    }
}
