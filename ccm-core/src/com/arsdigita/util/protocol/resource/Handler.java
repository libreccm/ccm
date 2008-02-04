/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util.protocol.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Handler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/16 $
 **/

public class Handler extends URLStreamHandler {

    public final static String versionId = "$Id: Handler.java 1509 2007-03-22 00:12:09Z apevec $ by $Author: apevec $, $DateTime: 2004/08/16 18:10:38 $";

    protected void parseURL(URL url, String spec, int start, int limit) {
        // trim leading slashes
        while (start < spec.length()) {
            char c = spec.charAt(start);
            if (c != '/') { break; }
            start++;
        }

        setURL(url, url.getProtocol(), null, -1, null, null,
               spec.substring(start, limit), null, url.getRef());
    }

    protected URLConnection openConnection(URL url) {
        return new URLConnection(url) {

            public void connect() {
                // do nothing
            }

            public InputStream getInputStream() throws IOException {
                ClassLoader ldr =
                    Thread.currentThread().getContextClassLoader();
                URL url = getURL();
                String resource = url.getPath();
                if (resource == null || resource.equals("")) {
                    throw new FileNotFoundException(url.toExternalForm());
                }
                InputStream is = ldr.getResourceAsStream(resource);
                if (is == null) {
                    // try SCL
                    ldr = ClassLoader.getSystemClassLoader();
                    is = ldr.getResourceAsStream(resource);
                }
                if (is == null) {
                    throw new FileNotFoundException(url.toExternalForm());
                }
                return is;
            }

        };
    }

}
