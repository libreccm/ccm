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
 *
 */
package com.arsdigita.cms.ui.workflow;


import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.xml.Element;

/** 
 *
 * @version $Id: WorkflowComponent.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class WorkflowComponent extends SimpleComponent {

    Workflow m_workflow;
    public WorkflowComponent(Workflow w) {
        m_workflow = w;
    }

    public void generateXML(PageState state, Element parent) {
        Element element = new Element("cms:workflow", CMS.CMS_XML_NS);
        parent.addContent(element);
        exportAttributes(element);
        element.addAttribute("name", m_workflow.getLabel());
        element.addAttribute("state", m_workflow.getStateString());
        element.addAttribute("process-state", String.valueOf
                             (m_workflow.getProcessState()));
        element.addAttribute("description", m_workflow.getDescription());
        element.addAttribute("count",
                             String.valueOf(m_workflow.getTaskCount()));

    }
}
