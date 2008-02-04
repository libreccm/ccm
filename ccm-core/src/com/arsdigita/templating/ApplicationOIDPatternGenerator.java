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
package com.arsdigita.templating;

import com.arsdigita.templating.PatternGenerator;
import com.arsdigita.web.Web;
import com.arsdigita.web.Application;

import javax.servlet.http.HttpServletRequest;

import java.net.URLEncoder;

/**
 *  This looks for the current application and will return its OID if
 *  it is available
 */
public class ApplicationOIDPatternGenerator implements PatternGenerator {

    public String[] generateValues(String key,
                                   HttpServletRequest req) {
        Application application = Web.getContext().getApplication();
        if (application != null) {
            String[] oid = new String[1];
            // FR: better URLEncode this
            oid[0] = URLEncoder.encode(application.getOID().toString());
            return oid;
        } else {
            return new String[] {};
        }
    }
}
