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

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jst.server.jetty.core.IJettyConfiguration;
import org.eclipse.jst.server.jetty.core.internal.jetty7.Jetty7Provider;
import org.eclipse.jst.server.jetty.core.internal.jetty8.Jetty8Provider;
import org.eclipse.jst.server.jetty.core.internal.jetty9.Jetty9Provider;

public class JettyVersionManager
{

    public static final JettyVersionManager __INSTANCE = new JettyVersionManager();

    private Map<String, IJettyVersionProvider> _versionProviders = new HashMap<String, IJettyVersionProvider>();

    private List<String> _runtimeTypes = new ArrayList<String>();

    public enum JettyVersion
    {
        V71, V72, V73, V74, V75, V76, V81, V91
    }

    private JettyVersionManager()
    {
        register(JettyVersion.V71, Jetty7Provider.__INSTANCE);
        register(JettyVersion.V72, Jetty7Provider.__INSTANCE);
        register(JettyVersion.V73, Jetty7Provider.__INSTANCE);
        register(JettyVersion.V74, Jetty7Provider.__INSTANCE);
        register(JettyVersion.V75, Jetty7Provider.__INSTANCE);
        register(JettyVersion.V76, Jetty7Provider.__INSTANCE);
        register(JettyVersion.V81, Jetty8Provider.__INSTANCE);
        register(JettyVersion.V91, Jetty9Provider.__INSTANCE);
    }

    public void register(JettyVersion version, IJettyVersionProvider versionProvider)
    {
        _versionProviders.put(version.name(),versionProvider);

        String versionNumber = version.name().substring(1,version.name().length());
        
        _runtimeTypes.add("org.eclipse.jst.server.jetty.runtime." + versionNumber);
    }

    public IJettyVersionHandler getJettyVersionHandler(String id)
    {
        String version = getVersion(id);
        IJettyVersionProvider versionProvider = _versionProviders.get(version);
        if (versionProvider == null)
        {
            throw new JettyVersionHandlerNotFoundException(version);
        }
        return versionProvider.getJettyVersionHandler();
    }

    public IJettyConfiguration getJettyConfiguration(String id, IFolder path)
    {
        String version = getVersion(id);
        IJettyVersionProvider versionProvider = _versionProviders.get(version);
        if (versionProvider == null)
        {
            throw new JettyVersionHandlerNotFoundException(version);
        }
        return versionProvider.createJettyConfiguration(path);
    }

    private String getVersion(String id)
    {
        String version = id;
        
        int index = version.lastIndexOf('.');
        
        if (index != -1)
        {
            version = version.substring(index + 1,version.length());
        }
        
        if (!version.startsWith("v"))
        {
            version = "v" + version;
        }
        
        version = version.toUpperCase();
        return version;
    }

    private static class JettyVersionHandlerNotFoundException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        private static final String MESSAGE = "Version Handler not founded with serverType={0}.";

        public JettyVersionHandlerNotFoundException(String serverType)
        {
            super(format(MESSAGE,serverType));
        }
    }

    public Collection<String> getRuntimeTypes()
    {
        return _runtimeTypes;
    }

}
