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
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.AbstractListModelBuilder;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import org.apache.log4j.Logger;

/**
 * Builds a list of workflow templates registered to the current
 * content section.
 *
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: WorkflowListModelBuilder.java 287 2005-02-22 00:29:02Z sskracic $
 */
class WorkflowListModelBuilder extends AbstractListModelBuilder {
    public static final String versionId =
        "$Id: WorkflowListModelBuilder.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger
        (AssignedTaskTableModelBuilder.class);

    public final ListModel makeModel(final List list, final PageState state) {
        return new Model();
    }

    private class Model implements ListModel {
        private final TaskCollection m_templates;

        public Model() {
            final ContentSection section =
                CMS.getContext().getContentSection();

            m_templates = section.getWorkflowTemplates();
            m_templates.addOrder("label");
        }

        public boolean next() {
            return m_templates.next();
        }

        public Object getElement() {
            return ((WorkflowTemplate) m_templates.getDomainObject())
                .getLabel();
        }

        public String getKey() {
            WorkflowTemplate template =
                (WorkflowTemplate) m_templates.getDomainObject();

            return template.getID().toString();
        }
    }
}
