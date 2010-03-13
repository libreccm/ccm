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
package com.arsdigita.cms.publishToFile;

import com.arsdigita.util.Assert;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.log4j.Logger;
import sun.net.www.protocol.http.HttpURLConnection;

/**
 * HttpHTMLProvider
 *
 * @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *
 */
class HttpHTMLProvider
    implements PublishedHTMLProvider, SetLocalRequestPassword {

    private static final Logger s_log =
        Logger.getLogger(HttpHTMLProvider.class);
    private final int m_timeout;

    HttpHTMLProvider(final int timeout) {
        Assert.isTrue(timeout > 0, "Timeout must be > than 0.");
        m_timeout = timeout;
    }

    public RetrievedFile fetchHTML(String urlSource) {
        StringBuffer buffer = new StringBuffer(256);
        String contentType = "";
        int contentLength = 0;
        URL url = null;
        try {
            url = new URL(urlSource);

            if ( s_log.isDebugEnabled() ) {
                s_log.debug("urlsource is " + urlSource);
                s_log.debug("url protocol is " + url.getProtocol());
                s_log.debug("url host is " + url.getHost());
                s_log.debug("url port is " + url.getPort());
                s_log.debug("url path is " + url.getPath());
            }

            // setup local request password for possible use in validating
            // request

            HttpURLConnection con = new HttpURLConnection(url, url.getHost(), url.getPort());
            //HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //con.setRequestMethod("GET");

            // set passwords (cookies) on request if required (allows increased
            // security)
            setPassword(con);
            con.connect();
            contentType = con.getHeaderField("Content-Type");
            int status = con.getResponseCode();
            // FIXME: Should we allow any other codes here ? Some more 2xx codes,
            // perhaps ? [lutter]
            if ( HttpURLConnection.HTTP_OK != status ) {
                throw new PublishToFileException("Bad response code " + status
                                                 + " when reading " + urlSource);
            }

            s_log.debug("Opening input from connection...");
            BufferedReader input = new BufferedReader
                (new InputStreamReader(con.getInputStream()));
            ReadConnection rc = new ReadConnection(input, buffer);
            rc.start();
            try {
                rc.join(1000 * m_timeout);
            } catch ( InterruptedException ie ) {
                s_log.warn("Publishing thread interrupted " +
                           "when reading html: ", ie);
            }
            if (rc.isAlive()) {
                rc.interrupt();
                con.disconnect();
                throw new PublishToFileException("Timeout when reading html: "
                                                 + urlSource);
            }
            input.close();
        } catch ( MalformedURLException mal ) {
            throw new PublishToFileException("Malformed URL: " + url, mal);
        } catch ( IOException io ) {
            throw new PublishToFileException
                ("IO Error when reading html from: " + url, io);
        }
        return ( new RetrievedFile(buffer.toString(), contentType) );
    }

    public void setPassword(URLConnection con) {
        LocalRequestPassword.setLocalRequestPassword(con);
    }

    private final class ReadConnection extends Thread {
        private StringBuffer buffer;
        private BufferedReader input;
        ReadConnection(BufferedReader input, StringBuffer buffer) {
            this.buffer = buffer;
            this.input = input;
        }
        public void run() {
            setName("Read HTML");
            String line = null;
            try {
                while ((line = input.readLine()) != null) {
                    buffer.append(line).append('\n');
                }
            } catch ( IOException io ) {
                s_log.error("IO Error when reading html: ", io);
            }
        }
    }
}
