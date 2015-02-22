/*
 * Copyright (C) 2015 University of Bremen. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ResourceParameter;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import java.io.InputStream;

/**
 * Loader executes nonrecurring once at install time and loads the 
 * ExternalLink contenttype package persistently into database.
 *
 * It uses the base class to create the database schema and the required 
 * table entries for the contenttype.
 *
 * NOTE: Configuration parameters used at load time MUST be part of 
 * Loader class and can not delegated to a Config object (derived 
 * from AbstractConfig). They will (and can) not be persisted into 
 * an registry object (file).
 *
 * @author tosmers
 * @version $Revision: #1 $ $Date: 2015/02/22 $
 */
public class ExternalLinkLoader extends AbstractContentTypeLoader {

    /**
     * Defines the xml file containing the ExternalLink content types 
     * property definitions.
     */
    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/ExternalLink.xml"
    };

    /**
     * Configures a special default template to register at install time.
     */
    private final Parameter m_template = new ResourceParameter(
            "com.arsdigita.cms.contenttypes.externallink.defaulttemplate",
            Parameter.REQUIRED,
            "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/externallink-item.jsp");

    /**
     * Constructor, just registers Loader parameter.
     */
    public ExternalLinkLoader() {
        register(m_template);
    }

    /**
     * Provides the of ExternalLink contenttype property definitions.
     *
     * The file defines the types name as displayed in content center 
     * select box and the authoring steps. These are loaded into database.
     *
     * Implements the method of the parent class.
     *
     * @return String array of fully qualified file names
     */
    @Override
    public String[] getTypes() {
        return TYPES;
    }

    /**
     * Overwrites base class to register its own specific default template.
     *
     * @param section
     * @param type
     * @param ld
     * @param wf
     */
    @Override
    protected void prepareSection(final ContentSection section,
                                  final ContentType type,
                                  final LifecycleDefinition ld,
                                  final WorkflowTemplate wf) {
        super.prepareSection(section, type, ld, wf);

        setDefaultTemplate("ExternalLinkDefaultTemplate",
                           "externallink-item",
                           (InputStream) get(m_template),
                           section, type, ld, wf);

    }

}
