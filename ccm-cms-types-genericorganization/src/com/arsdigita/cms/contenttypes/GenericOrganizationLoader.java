/*
 * Copyright (C) 2009 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.cms.contenttypes.AbstractContentTypeLoader;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ResourceParameter;
import com.arsdigita.workflow.simple.WorkflowTemplate;

import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationLoader extends AbstractContentTypeLoader {
   
    private static final Logger s_log = Logger.getLogger(GenericOrganizationLoader.class);
    private static final String[] TYPES = {
	"/WEB-INF/content-types/com/arsdigita/cms/contenttypes/GenericOrganization.xml"
    };

    public String[] getTypes() {
	return TYPES;
    }

    private ResourceParameter m_template;
    
    public GenericOrganizationLoader() {
        super();
	m_template = new ResourceParameter("com.arsdigita.cms.contenttypes.genericorganization.template",
					   Parameter.REQUIRED,
					   "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/genericorganization-item.jsp");
	register(m_template);
    }

    @Override
	protected void prepareSection(final ContentSection section,
				      final ContentType type,
				      final LifecycleDefinition ld,
				      final WorkflowTemplate wf) {
	super.prepareSection(section, type, ld, wf);
	
	setDefaultTemplate("GenericOrganization-genericorganization-item",
			   "genericorganization-item",
			   (InputStream)get(m_template),
			   section,
			   type,
			   ld,
			   wf);
    }
    
}