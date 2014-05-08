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
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.core.internal.ProgressUtil;
import org.eclipse.jst.server.jetty.core.internal.JettyConstants;
import org.eclipse.jst.server.jetty.core.internal.util.IOUtils;

public class StartIni implements JettyConstants{

    private List<PathFileConfig> _jettyXMLFiles = new ArrayList<PathFileConfig>();
    private List<PathFileConfig> _otherConfigs = new ArrayList<PathFileConfig>();
    private PathFileConfig _startConfig = null;
    private PathFileConfig _webdefaultXMLConfig = null;
    private File _adminPortFile = null;
    private File _startIniFile;

    private boolean _isStartIniDirty;

    public StartIni(IPath baseDirPath){
        loadStartIni(baseDirPath,null);
        loadOtherConfigs(baseDirPath);
        loadAdminPortFile (baseDirPath,null);
    }

    public StartIni(IFolder baseDirFolder){
        loadStartIni(null,baseDirFolder);
        // loadOtherConfigs(null, baseDirFolder);
        loadAdminPortFile (null,baseDirFolder);
    }

    private List<String> loadStartIni(IPath baseDirPath, IFolder baseDirFolder){
        List<String> args = new ArrayList<String>();
        if (baseDirPath != null){
            IPath startIniPath = baseDirPath.append(__START_INI);
            this._startIniFile = startIniPath.toFile();
        }else{
            try{
                this._startIniFile = IOUtils.toLocalFile(baseDirFolder.getFile(__START_INI),null);
            }catch (CoreException e){
                e.printStackTrace();
            }
        }

        if (_startIniFile.exists() && _startIniFile.canRead()){
            FileReader reader = null;
            BufferedReader buf = null;
            try{
                reader = new FileReader(_startIniFile);
                buf = new BufferedReader(reader);

                File jettyXMLFile = null;
                String arg;
                while ((arg = buf.readLine()) != null){
                    arg = arg.trim();
                    if (arg.length() == 0 || arg.startsWith("#")){
                        continue;
                    }
                    if (arg.indexOf('=') == -1){
                        if (baseDirPath != null){
                            jettyXMLFile = baseDirPath.append(arg).toFile();
                        }else{
                            try{
                                jettyXMLFile = IOUtils.toLocalFile(baseDirFolder.getFile(arg),null);
                            }catch (CoreException e){
                                e.printStackTrace();
                            }
                        }
                        if (jettyXMLFile != null && jettyXMLFile.exists() && jettyXMLFile.canRead()){
                            _jettyXMLFiles.add(new PathFileConfig(jettyXMLFile,new Path(arg)));
                        }
                    }
                    args.add(arg);
                }
            }catch (IOException e){
            	
            }finally{
                close(buf);
                close(reader);
            }
        }
        return args;
    }

    private void close(Closeable c){
        if (c == null){
            return;
        }
        try{
            c.close();
        }catch (IOException e){
            e.printStackTrace(System.err);
        }
    }

    private void loadOtherConfigs(IPath baseDirPath){
        IPath realmPropertiesPath = baseDirPath.append("etc/realm.properties");
        File realmPropertiesFile = realmPropertiesPath.toFile();
        if (realmPropertiesFile.exists()){
            _otherConfigs.add(new PathFileConfig(realmPropertiesFile,new Path("etc/realm.properties")));
        }

        IPath webdefaultPath = baseDirPath.append("etc/webdefault.xml");
        File webdefaultFile = webdefaultPath.toFile();
        if (webdefaultFile.exists()){
            _webdefaultXMLConfig = new PathFileConfig(webdefaultFile,new Path("etc/webdefault.xml"));
        }
        
        IPath startJARPath = baseDirPath.append(__START_JAR);
        File startConfigFile = startJARPath.toFile();
        if (startConfigFile.exists()){
            _startConfig = new PathFileConfig(startConfigFile,new Path(__START_JAR));
        }
    }
    
    private void loadAdminPortFile (IPath baseDirPath, IFolder baseDirFolder){
        if (baseDirPath != null){
            IPath adminPortPath = baseDirPath.append("adminPort");
            this._adminPortFile = adminPortPath.toFile();
        }else{
            try{
                this._adminPortFile = IOUtils.toLocalFile(baseDirFolder.getFile("adminPort"),null);
            }catch (CoreException e){
                e.printStackTrace();
            }
        }
    }

    public List<PathFileConfig> getJettyXMLFiles(){
        return _jettyXMLFiles;
    }

    public PathFileConfig getWebdefaultXMLConfig(){
        return _webdefaultXMLConfig;
    }
    
    public File getAdminPortFile(){
    	return _adminPortFile;
    }

    /**
     * Saves the Web app document.
     * 
     * @param path
     *            a path
     * @param forceDirty
     *            true to force a save
     * @throws IOException
     *             if anything goes wrong
     */
    // public void save(String path, boolean forceDirty) throws IOException {
    // if (forceDirty || isWebAppDirty)
    // //XMLUtil.save(path, webAppDocument);
    // }

    /**
     * Saves the Web app document.
     * 
     * @param file
     *            a file
     * @param monitor
     *            a progress monitor
     * @throws Exception
     *             if anything goes wrong
     */
    public void save(IFile file, IProgressMonitor monitor) throws Exception{
        if (file.exists() && !_isStartIniDirty)
            return;
        if (_startIniFile == null || !(_startIniFile.exists() && _startIniFile.canRead()))
            return;

        InputStream in = null;
        try{
            in = new FileInputStream(_startIniFile);
            if (file.exists())
                file.setContents(in,true,true,ProgressUtil.getSubMonitorFor(monitor,200));
            else
                file.create(in,true,ProgressUtil.getSubMonitorFor(monitor,200));
        }catch (Exception e){
            // ignore
        }finally{
            try{in.close();}catch (Exception e){}
        }
        _isStartIniDirty = false;
    }

    public List<PathFileConfig> getOtherConfigs(){
        return _otherConfigs;
    }

    public PathFileConfig getStartConfig(){
        return _startConfig;
    }
}
