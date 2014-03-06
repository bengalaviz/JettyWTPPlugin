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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.eclipse.jst.server.jetty.core.internal.util.IOUtils;

public class WebdefaultXMLConfig
{

    public static InputStream getInputStream(File webdefaultXMLFile) throws IOException
    {
        InputStream stream = new FileInputStream(webdefaultXMLFile);
        try
        {
            boolean useFileMappedBuffer = false;
            InputStreamReader input = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(input);
            StringWriter newContent = new StringWriter();
            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                if (useFileMappedBuffer)
                {
                    line = "<param-value>false</param-value>";
                    useFileMappedBuffer = false;
                }
                if (line.indexOf("<param-name>useFileMappedBuffer</param-name>") != -1)
                {
                    useFileMappedBuffer = true;
                }
                newContent.append(line);
                newContent.append('\n');
            }
            return IOUtils.toInputStream(newContent.toString(),"UTF-8");
        }
        finally
        {
            stream.close();
        }
    }
}
