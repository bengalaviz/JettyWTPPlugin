/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - Jetty packages
 *******************************************************************************/
package org.eclipse.jst.server.jetty.core.internal;

import java.net.URL;

import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.core.Servlet;
import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IURLProvider;
import org.eclipse.wst.server.core.model.LaunchableAdapterDelegate;
import org.eclipse.wst.server.core.util.HttpLaunchable;
import org.eclipse.wst.server.core.util.WebResource;

/**
 * Launchable adapter delegate for Web resources in Jetty.
 */
public class JettyLaunchableAdapterDelegate extends LaunchableAdapterDelegate
{

    private static final String __SERVLET_PATH = "servlet/";

    /*
     * @see LaunchableAdapterDelegate#getLaunchable(IServer, IModuleArtifact)
     */
    public Object getLaunchable(IServer server, IModuleArtifact moduleObject)
    {
        Trace.trace(Trace.FINEST,"JettyLaunchableAdapter " + server + "-" + moduleObject);
        
        if (server.getAdapter(JettyServer.class) == null)
        {
            return null;
        }
        
        if (!(moduleObject instanceof Servlet) && !(moduleObject instanceof WebResource))
        {
            return null;
        }
        
        if (moduleObject.getModule().loadAdapter(IWebModule.class,null) == null)
        {
            return null;
        }

        try
        {
            URL url = ((IURLProvider)server.loadAdapter(IURLProvider.class,null)).getModuleRootURL(moduleObject.getModule());

            Trace.trace(Trace.FINEST,"root: " + url);

            if (moduleObject instanceof Servlet)
            {
                Servlet servlet = (Servlet)moduleObject;
                if (servlet.getAlias() != null)
                {
                    String path = servlet.getAlias();
                    if (path.startsWith("/"))
                        path = path.substring(1);
                    url = new URL(url,path);
                }
                else
                    url = new URL(url,__SERVLET_PATH + servlet.getServletClassName());
            }
            else if (moduleObject instanceof WebResource)
            {
                WebResource resource = (WebResource)moduleObject;
                String path = resource.getPath().toString();
                Trace.trace(Trace.FINEST,"path: " + path);
                if (path != null && path.startsWith("/") && path.length() > 0)
                    path = path.substring(1);
                if (path != null && path.length() > 0)
                    url = new URL(url,path);
            }
            return new HttpLaunchable(url);
        }
        catch (Exception e)
        {
            Trace.trace(Trace.SEVERE,"Error getting URL for " + moduleObject,e);
            JettyPlugin.log(e);
        }
        
        return null;
    }
}
