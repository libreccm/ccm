/*
 * Copyright (c) 2011 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ResourceParameter;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import java.io.InputStream;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciDepartmentWithPublicationsLoader extends AbstractContentTypeLoader {
    
    private ResourceParameter m_template;
    private static final String[] TYPES = {
        "/WEB-INF/content-types/com/arsdigita/cms/contenttypes/SciDepartmentWithPublications.xml"
    };
    
    public SciDepartmentWithPublicationsLoader() {
        super();
        
        m_template =
        new ResourceParameter(
                "com.arsdigita.cms.contenttypes.SciDepartmentWithPublicationsTemplate",
                Parameter.REQUIRED,
                "/WEB-INF/content-types/com/arsdigita/"
                + "cms/contenttypes/scidepartment-with-publications-item.jsp");
        
        register(m_template);               
    }
    
    public String[] getTypes() {
        return TYPES;
    }
    
    @Override
    protected void prepareSection(final ContentSection section,
                                  final ContentType type,
                                  final LifecycleDefinition lifecycle,
                                  final WorkflowTemplate workflow) {
        super.prepareSection(section, type, lifecycle, workflow);
        
        setDefaultTemplate("SciDepartmentWithPublications Item",
                           "scidepartment-with-publications-item",
                           (InputStream) get(m_template),
                           section,
                           type,
                           lifecycle,
                           workflow);
    }
}
