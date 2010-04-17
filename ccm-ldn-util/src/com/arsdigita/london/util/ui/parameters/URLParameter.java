/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.london.util.ui.parameters;

import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.web.Web;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;

/**
 * Bebop URLParameter is useless because it merely
 * check URL form, doesn't actually return a
 * java.net.URL object.
 *
 * @version $Id: URLParameter.java 2086 2010-04-12 09:55:35Z pboy $  
 */
public class URLParameter extends ParameterModel {

    public URLParameter(String name) {
        super(name);
    }

    public Object transformValue(HttpServletRequest request)
        throws IllegalArgumentException {
        return transformSingleValue(request);
    }


    public Object unmarshal(String encoded)
        throws IllegalArgumentException {

        if (encoded == null ||
            "".equals(encoded.trim())) {
            return null;
        }
        URL url;
        try {
            url = new URL(encoded);
        } catch (MalformedURLException e) {
            try {
                if (encoded.startsWith("/")) {
                    url = new URL("http://" + 
                                  Web.getConfig().getServer().toString() +
                                  encoded);
                } else {
                    url = new URL("http://" + encoded);
                }
            } catch (MalformedURLException e2) {
                throw new IllegalArgumentException
                    (getName() + " is not a valid URL: '" + encoded +
                     "'; " + e2.getMessage());
            }
        }
        return url;
    }

    public Class getValueClass() {
        return URL.class;
    }


}
