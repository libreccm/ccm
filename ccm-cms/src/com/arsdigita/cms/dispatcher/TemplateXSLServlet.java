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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.util.IO;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.BaseServlet;

import com.arsdigita.cms.Template;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.ByteArrayInputStream;

import java.io.UnsupportedEncodingException;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.apache.log4j.Logger;


/**
 * A servlet that takes an Template OID and a delegated URL and
 * combines them into a single XSL file.
 */
public class TemplateXSLServlet extends BaseServlet {
    
    private static final Logger s_log = 
        Logger.getLogger(ContentItemXSLServlet.class);

    //cache for the template resolver
    public static Map s_templateResolverCache =
        Collections.synchronizedMap(new HashMap());

    protected void doService(HttpServletRequest sreq,
                             HttpServletResponse sresp)
        throws ServletException, IOException {

        String oid = sreq.getParameter("oid");

        if (s_log.isDebugEnabled()) {
            s_log.debug("Template OID is " + oid);
        }

        if (oid == null) {
            sresp.sendError(404, "Page Not Found; No oid specified");
            return;
        }

        Template template = null;
        try {
            template = (Template)
                DomainObjectFactory.newInstance(OID.valueOf(oid));
        } catch (DataObjectNotFoundException ex) {
            sresp.sendError(404, "Page not found; Cannot retrieve template");
            return;
        }

        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(template.getText().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new UncheckedWrapperException("Cannot convert to UTF-8");
        }

        sresp.setContentType("text/xml; charset=UTF-8");
        IO.copy(is,
                sresp.getOutputStream());
    }
}
   
