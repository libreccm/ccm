/*
 * Copyright (C) 2005 RuntimeCollective Ltd. All Rights Reserved.
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
package com.arsdigita.subsite;

import com.arsdigita.web.ServerDynamicHostProvider;

import org.apache.log4j.Logger;

import java.net.URL;
import java.net.MalformedURLException;

public class SubsiteDynamicHostProvider extends ServerDynamicHostProvider {

    /** A logger instance, primarily to assist debugging .  */
    private static final Logger s_log = 
                         Logger.getLogger(SubsiteDynamicHostProvider.class);

    public SubsiteDynamicHostProvider() {
        super();
    }

    @Override
    public String getName() {
        if (!Subsite.getContext().hasSite()) {
            return super.getName();
        }

        URL url = getSubsiteURL();
        return url.getHost();
    }
    
    @Override
    public int getPort() {
        if (!Subsite.getContext().hasSite()) {
            return super.getPort();
        }

        URL url = getSubsiteURL();
        int port = url.getPort();
        if (port == -1) {
            port = 80;
        }

        return port;
    }

    public URL getSubsiteURL() {
        String hostname = Subsite.getContext().getSite().getHostname();
        if (hostname.indexOf('/') == -1) {
            hostname = "http://"+hostname;
        }

        URL url = null;
        try {
            url = new URL(hostname);
        } catch (MalformedURLException e) {
            s_log.error("Could not generate URL out of subsite hostname : "+hostname, e);
        }

        return url;
    }
}
