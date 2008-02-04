/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.util.url;

import org.apache.log4j.Logger;

/**
 * Helper class for fetching remote URLs. Provides a pool of worker threads
 * that actually fetch the URLs, thus enabling the URL connections to be
 * interrupted early if the remote server hangs or doesn't respond.
 *
 * @author Dirk Gomez
 */

import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.arsdigita.util.StringUtils;

public class URLPool {

    private static final Logger s_log = Logger.getLogger(URLPool.class);
    private int m_currentThreadCount = 0;
    private int m_maxThreadCount;
    private long m_timeOut;

    /**
     * Create a new URLPool with a default poolsize of 10 and a default
     * timeout of 4 seconds.
     *
     */
    public URLPool() {
        this(10, 4000);
    }

    /**
     * Create a new URLPool with a default timeout of 4 seconds.
     *
     * @param poolsize - maximum number of threads allowed to be running at
     * any given time, any subsequent requests for urls are queued until a
     * thread becomes available.
     *
     * @pre poolsize > 0
     */
    public URLPool(int poolsize) {
        this(poolsize, 4000);
    }

    /**
     * Create a new URLPool with a default timeout of 4 seconds.
     *
     * @param threadCount - maximum number of threads allowed to be running at
     * any given time, any subsequent requests for urls are queued until a
     * thread becomes available.
     *
     * @param timeout - timeout in milliseconds to use when fetching URLs to
     * prevent a slow remote server from delaying the calling application
     * indefinitely.
     *
     * @pre threadCount > 0 && timeout > 0
     */
    public URLPool(int threadCount, long timeout) {
        assertThreadCount(threadCount);
        assertTimeout(timeout);
        m_maxThreadCount = threadCount;
        m_timeOut = timeout;
    }

    private static void assertThreadCount(int threadCount) {
        if (threadCount <= 0 ) {
            throw new IllegalArgumentException("Thread Count must be greater than 0, not " + threadCount);
        }
    }

    private static void assertTimeout(long timeout) {
        if (timeout <= 0 ) {
            throw new IllegalArgumentException("Timeout must be greater than 0, not " + timeout);
        }
    }

    /**
     * Returns the thread count - maximum number of threads allowed to be running
     * at any given time, any subsequent requests for urls are queued until a
     * thread becomes available.
     *
     * @return the thread count - maximum number of threads allowed to be running
     * at any given time, any subsequent requests for urls are queued until a
     * thread becomes available.
     */
    public int getMaxThreadCount() {
        return m_maxThreadCount;
    };

    /**
     * Sets the thread count - maximum number of threads allowed to be running
     * at any given time, any subsequent requests for urls are queued until a
     * thread becomes available.
     *
     * @param maxThreadCount the maximum number of threads allowed to be running
     * at any given time
     *
     * @pre maxThreadCount > 0
     */
    public void setMaxThreadCount(int maxThreadCount) {
        assertThreadCount(maxThreadCount);
        m_maxThreadCount = maxThreadCount;
    };

    /**
     * Returns the timeout to use when fetching URLs to prevent a slow remote
     * server from delaying the calling application indefinitely.
     *
     * @return the timeout to use when fetching URLs to prevent a slow remote
     * server from delaying the calling application indefinitely.
     */
    public long getTimeOut() {
        return m_timeOut;
    };

    /**
     * Sets the timeout to use when fetching URLs to prevent a slow remote
     * server from delaying the calling application indefinitely.
     *
     * @param timeOut the timeout to use when fetching URLs to prevent a slow
     * remote server from delaying the calling application indefinitely.
     *
     * @param timeOut > 0
     */
    public void setTimeOut(long timeOut) {
        assertTimeout(timeOut);
        m_timeOut = timeOut;
    };

    /**
     *
     * fetches the remote URL, returning the data from the page, or null if an
     * error occurred.
     *
     * @param url The URL to fetch data from
     *
     * @return Data from the URL, or null if unable to fetch.
     * @deprecated Use {@link #fetchURLData(String)} instead
     */
    public String fetchURL(String url) {
        URLData data = fetchURLData(url);
        if (data != null) {
            return data.getContentAsString();
        } else {
            return null;
        }
    }

    /**
     *
     * fetches the remote URL, returning the data from the page, or null if an
     * error occurred.
     *
     * @param url The URL to fetch data from
     *
     * @return Data from the URL, or null if unable to fetch.
     */
    public URLData fetchURLData(String url) {
        URL theURL = null;
        try {
            theURL = makeURL(url);
        } catch(MalformedURLException e) {
            throw new IllegalArgumentException("URL " + url + " is invalid: " + e.getMessage());
        }
        // Check whether there is a "slot" available to fetch the URL
        // If not then sleep for half the fetch timeout time.
        while (m_currentThreadCount > m_maxThreadCount) {
            try {
                Thread.sleep(m_timeOut/2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        m_currentThreadCount++;
        URLFetcher fetcher = new URLFetcher(theURL);
        s_log.debug("Starting fetch thread for " + url);

        try {
            fetcher.start();
            try {
                fetcher.join(m_timeOut);
                if (fetcher.isAlive()) {
                    s_log.debug("Thread still alive after " + m_timeOut + " milliseconds " + url);
                    fetcher.shutdown();
                    s_log.debug("Shutdown thread");
                }
            } catch (InterruptedException ex) {
                s_log.debug("URL Fetcher interrupted", ex);
            }
        } finally {
            s_log.debug("decrement");
            m_currentThreadCount--;
        }
        s_log.debug("Getting data");
        return fetcher.getData();
    }

    private static URL makeURL(String url) throws MalformedURLException {
        if (StringUtils.emptyString(url)) {
            throw new IllegalArgumentException("Cannot have an empty URL!");
        }

        // Let's be nice to the user. If there's no "://" in the string,
        // just prepend "http://"
        if (url.indexOf("://") == -1) {
            url = "http://" + url;
        }

        URL theURL = new URL(url);
        return theURL;
    }

    private static class URLFetcher extends Thread {
        private URLData m_data;
        private URL m_url;
        private boolean m_running = true;

        public URLFetcher(URL url) {
            m_url = url;
            m_data = new URLData(url.toString());
        }

        public URLData getData() {
            return m_data;
        }

        public void shutdown() {
            m_running = false;
        }

        public synchronized void start() {
            super.start();
        }

        public void run() {
            if (!m_running) {
                return;
            }

            InputStream is = null;
            InputStreamReader isr = null;
            BufferedReader input = null;
            try {
                URLConnection con = m_url.openConnection();

                is = con.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                byte[] buffer = new byte[8];
                int length = -1;
                while ((length = is.read(buffer)) != -1) {
                    os.write(buffer, 0, length);
                }
                
                m_data.setContent(os.toByteArray());

                int i = 1;
                String currentKey = con.getHeaderFieldKey(i);
                HashMap headers = new HashMap();
                m_data.setHeaders(headers);
                while (currentKey != null) {
                    String currentValue = con.getHeaderField(i);
                    // we make all of the keys lowercase to standardize
                    // the syntax and make things easy to look up within 
                    // the map
                    headers.put(currentKey.toLowerCase(), currentValue);
                    i++;
                    currentKey = con.getHeaderFieldKey(i);
                }
            } catch (IOException io) {
                s_log.error("IO Error fetching URL: " + m_url, io);
                m_data.setException(io);
                return;
            } finally {
                if (null != input) {
                    try {
                        input.close();
                        isr.close();
                        is.close();
                    } catch(IOException ioe) {
                        s_log.error("Error closing connection", ioe);
                    }
                }
            }

        }
    }
}
