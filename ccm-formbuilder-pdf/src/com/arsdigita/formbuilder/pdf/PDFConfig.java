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
package com.arsdigita.formbuilder.pdf;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

import org.apache.log4j.Logger;

/**
 * A set of configuration parameters for the formbuilder PDF action.
 *
 * @author Matthew Booth <mbooth@redhat.com>
 * @version $Id: PDFConfig.java 285 2005-02-22 00:29:02Z sskracic $
 */

public class PDFConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger(PDFConfig.class);

    private static final PDFConfig s_config = new PDFConfig();
    
    private Parameter m_xslFile;

    static {
        s_log.debug("Static initalizer starting...");
        try {
            s_config.load();
        } catch (java.lang.IllegalArgumentException ex) {
            s_log.info("Unable to load PDFConfig. This is not a problem " +
                       "during ccm load, but is a problem at all other times");
        }
        s_log.debug("Static initalizer finished.");
    }

    public PDFConfig() {
        m_xslFile = new StringParameter(
            "com.arsdigita.formbuilder.pdf.xsl_file",
            Parameter.REQUIRED,
            "/themes/heirloom/packages/ccm-formbuilder-pdf/xsl/main.xsl");
            // if installed into its own webapp context, originally:
            // "webapps/ccm-formbuilder-pdf/xsl/main.xsl");

        register(m_xslFile);

        loadInfo();
    }


    public static PDFConfig retrieve() {
        return s_config;
    }

    public String getXSLFile() {
        return (String) get(m_xslFile);
    }
}
