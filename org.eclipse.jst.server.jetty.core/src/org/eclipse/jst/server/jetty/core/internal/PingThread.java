/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
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

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.wst.server.core.IServer;

/**
 * Thread used to ping server to test when it is started.
 */
public class PingThread
{
    // delay before pinging starts
    private static final int __PING_DELAY = 2000;

    // delay between pings
    private static final int __PING_INTERVAL = 250;

    // maximum number of pings before giving up
    private int _maxPings;

    private boolean _stop = false;
    private String _url;
    private IServer _server;
    private JettyServerBehaviour _behaviour;

    /**
     * Create a new PingThread.
     * 
     * @param server
     * @param url
     * @param maxPings
     *            the maximum number of times to try pinging, or -1 to continue forever
     * @param behaviour
     */
    public PingThread(IServer server, String url, int maxPings, JettyServerBehaviour behaviour)
    {
        super();
        
        this._server = server;
        this._url = url;
        this._maxPings = maxPings;
        this._behaviour = behaviour;
        Thread t = new Thread("Jetty Ping Thread")
        {
            public void run()
            {
                ping();
            }
        };
        t.setDaemon(true);
        t.start();
    }

    /**
     * Ping the server until it is started. Then set the server state to STATE_STARTED.
     */
    protected void ping()
    {
        int count = 0;
        try
        {
            Thread.sleep(__PING_DELAY);
        }
        catch (Exception e)
        {
            // ignore
        }
        while (!_stop)
        {
            try
            {
                if (count == _maxPings)
                {
                    try
                    {
                        _server.stop(false);
                    }
                    catch (Exception e)
                    {
                        Trace.trace(Trace.FINEST,"Ping: could not stop server");
                    }
                    _stop = true;
                    break;
                }
                count++;

                Trace.trace(Trace.FINEST,"Ping: pinging " + count);
                URL pingUrl = new URL(_url);
                URLConnection conn = pingUrl.openConnection();
                ((HttpURLConnection)conn).getResponseCode();

                // ping worked - server is up
                if (!_stop)
                {
                    Trace.trace(Trace.FINEST,"Ping: success");
                    Thread.sleep(200);
                    _behaviour.setServerStarted();
                }
                _stop = true;
            }
            catch (FileNotFoundException fe)
            {
                try
                {
                    Thread.sleep(200);
                }
                catch (Exception e)
                {
                    // ignore
                }
                _behaviour.setServerStarted();
                _stop = true;
            }
            catch (Exception e)
            {
                Trace.trace(Trace.FINEST,"Ping: failed");
                // pinging failed
                if (!_stop)
                {
                    try
                    {
                        Thread.sleep(__PING_INTERVAL);
                    }
                    catch (InterruptedException e2)
                    {
                        // ignore
                    }
                }
            }
        }
    }

    /**
     * Tell the pinging to stop.
     */
    public void stop()
    {
        Trace.trace(Trace.FINEST,"Ping: stopping");
        _stop = true;
    }
}
