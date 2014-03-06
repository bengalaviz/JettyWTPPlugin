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
package org.eclipse.jst.server.jetty.core.internal.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.zip.ZipException;

import org.eclipse.jst.server.jetty.core.internal.util.IOUtils;

public class StartConfig
{

    public static InputStream getInputStream(File startJARFile) throws ZipException, IOException
    {
        // Load start.config from the start.jar coming from the Jetty serveur
        // install
        InputStream stream = IOUtils.getInputStream(startJARFile,"org/eclipse/jetty/start/start.config");

        // Read start.config to preprocess the content to :
        // 1) add jetty.home/=$(jetty.home)
        // 2) comments the whole jetty.home= declaration
        // 3) replace for the JAR lib 'jetty.home' to 'install.jetty.home'
        // 4) add JSP support if needed

        boolean hasJSPModule = false;

        InputStreamReader input = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(input);
        StringWriter newContent = new StringWriter();

        // 1) add jetty.home/=$(jetty.home)
        newContent.append("jetty.home/=$(jetty.home)");
        newContent.append('\n');
        for (String line = reader.readLine(); line != null; line = reader.readLine())
        {

            if (line.startsWith("jetty.home"))
            {
                // 2) comments the whole jetty.home= declaration
                line = "#" + line;
            }
            else
            {
                // 3) replace for the JAR lib 'jetty.home' to
                // 'install.jetty.home'
                if (line.indexOf("$(jetty.home)") != -1)
                {
                    if (line.indexOf("/lib/") != -1 || line.indexOf(".jar") != -1)
                    {
                        line = line.replaceAll("jetty.home","install.jetty.home");
                    }
                }
                hasJSPModule = line.startsWith("[jsp]");
            }
            newContent.append(line);
            newContent.append('\n');
        }

        // 4) add JSP support if needed
        if (!hasJSPModule)
        {
            newContent.append("# Add jsp");
            newContent.append('\n');
            newContent.append("[jsp]");
            newContent.append('\n');
            newContent.append("$(install.jetty.home)/lib/jsp/**");
            newContent.append('\n');
        }
        return IOUtils.toInputStream(newContent.toString(),"UTF-8");
    }

}
