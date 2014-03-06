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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.model.IURLProvider;

public interface IJettyServer extends IURLProvider
{

    /**
     * Property which specifies whether this server is configured for testing environment.
     */
    public static final String PROPERTY_TEST_ENVIRONMENT = "testEnvironment";

    /**
     * Property which specifies the directory where the server instance exists. If not specified, instance directory is derived from the textEnvironment
     * setting.
     */
    public static final String PROPERTY_INSTANCE_DIR = "instanceDir";

    /**
     * Property which specifies the directory where web applications are published.
     */
    public static final String PROPERTY_DEPLOY_DIR = "deployDir";

    /**
     * Property which specifies if modules should be served without publishing.
     */
    public static final String PROPERTY_SERVE_MODULES_WITHOUT_PUBLISH = "serveModulesWithoutPublish";

    IJettyConfiguration getJettyConfiguration() throws CoreException;

    IJettyConfiguration getServerConfiguration() throws CoreException;

}
