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
package org.eclipse.jst.server.jetty.core.internal.jetty9;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.jetty.core.JettyPlugin;
import org.eclipse.jst.server.jetty.core.internal.IJettyVersionHandler;
import org.eclipse.jst.server.jetty.core.internal.Messages;
import org.eclipse.jst.server.jetty.core.internal.jetty7.Jetty7Handler;
import org.eclipse.wst.server.core.IModule;

public class Jetty9Handler extends Jetty7Handler
{

    /**
     * @see IJettyVersionHandler#canAddModule(IModule)
     */
    public IStatus canAddModule(IModule module)
    {
        String version = module.getModuleType().getVersion();
        
        if ("3.1".equals(version))
        {
            return Status.OK_STATUS;
        }

        return new Status(IStatus.ERROR,JettyPlugin.PLUGIN_ID,0,Messages.errorSpec70,null);
    }
}
