/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.IServerLocator.IServerSearchListener;
import org.eclipse.wst.server.core.internal.provisional.ServerLocatorDelegate;

public class JettyServerLocator extends ServerLocatorDelegate
{

    public void searchForServers(String host, final IServerSearchListener listener, final IProgressMonitor monitor)
    {

        JettyRuntimeLocator.IRuntimeSearchListener listener2 = new JettyRuntimeLocator.IRuntimeSearchListener()
        {
            public void runtimeFound(IRuntimeWorkingCopy runtime)
            {
                String runtimeTypeId = runtime.getRuntimeType().getId();
                String serverTypeId = runtimeTypeId.substring(0,runtimeTypeId.length() - 8);
                IServerType serverType = ServerCore.findServerType(serverTypeId);
                try
                {
                    IServerWorkingCopy server = serverType.createServer(serverTypeId,null,runtime,monitor);
                    listener.serverFound(server);
                }
                catch (Exception e)
                {
                    Trace.trace(Trace.WARNING,"Could not create Jetty server",e);
                }
            }
        };
        
        JettyRuntimeLocator.searchForRuntimes2(null,listener2,monitor);
    }

}
