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
package org.eclipse.jst.server.jetty.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.jetty.core.internal.IJettyVersionHandler;
import org.eclipse.jst.server.jetty.core.internal.JettyVersionManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JettyPlugin extends AbstractUIPlugin
{

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.jst.server.jetty.core"; //$NON-NLS-1$

    // The shared instance
    private static JettyPlugin _plugin;

    /**
     * The constructor
     */
    public JettyPlugin()
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
        _plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop(BundleContext context) throws Exception
    {
        _plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static JettyPlugin getDefault()
    {
        return _plugin;
    }

    /**
     * Return the install location preference.
     * 
     * @param id
     *            a runtime type id
     * @return the install location
     */
    public static String getPreference(String id)
    {
        return getDefault().getPluginPreferences().getString(id);
    }

    /**
     * Set the install location preference.
     * 
     * @param id
     *            the runtimt type id
     * @param value
     *            the location
     */
    public static void setPreference(String id, String value)
    {
        getDefault().getPluginPreferences().setValue(id,value);
        getDefault().savePluginPreferences();
    }

    /**
     * Return a <code>java.io.File</code> object that corresponds to the specified <code>IPath</code> in the plugin directory.
     * 
     * @return a file
     */
    public static File getPlugin()
    {
        try
        {
            URL installURL = getDefault().getBundle().getEntry("/");
            URL localURL = FileLocator.toFileURL(installURL);
            return new File(localURL.getFile());
        }
        catch (IOException ioe)
        {
            return null;
        }
    }

    /**
     * Convenience method for logging.
     * 
     * @param status
     *            a status object
     */
    public static void log(IStatus status)
    {
        getDefault().getLog().log(status);
    }

    /**
     * Log message.
     * 
     * @param message
     */
    public static void log(String message)
    {
        log(new Status(IStatus.ERROR,PLUGIN_ID,IStatus.ERROR,message,null));
    }

    /**
     * Log error.
     * 
     * @param e
     */
    public static void log(Throwable e)
    {
        log(new Status(IStatus.ERROR,PLUGIN_ID,IStatus.ERROR,e.getMessage(),e));
    }

    public static IJettyVersionHandler getJettyVersionHandler(String id)
    {
        return JettyVersionManager.__INSTANCE.getJettyVersionHandler(id);
    }

    public static IJettyConfiguration getJettyConfiguration(String id, IFolder path)
    {
        return JettyVersionManager.__INSTANCE.getJettyConfiguration(id,path);
    }

    public static Collection<String> getRuntimeTypes()
    {
        return JettyVersionManager.__INSTANCE.getRuntimeTypes();
    }

}
