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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.TooManyListenersException;

/**
 * This panel displays a radio group of available Workflow Templates
 * in this content section that can be applied to this item.
 *
 * @author Uday Mathur
 * @version $Id: ItemWorkflowSelectForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
class ItemWorkflowSelectForm extends CMSForm {
    
    private static final Logger s_log = Logger.getLogger(ItemWorkflowSelectForm.class);

    private RadioGroup m_radio;

    public ItemWorkflowSelectForm() {
        super("applyWorkflow", new SimpleContainer());

        addFormWidgets();
        addProcessListener(new ProcessListener());
    }

    protected void addFormWidgets() {
        m_radio = new RadioGroup(new BigDecimalParameter("workflowSelect"));
        m_radio.setClassAttr("vertical");

        try {
            m_radio.addPrintListener(new WorkflowsOptionPrintListener());
        } catch (TooManyListenersException t) {
            s_log.error("Too many listeners", t);
        }

        m_radio.addValidationListener(new NotNullValidationListener());
        add(m_radio);

        add(new Submit("apply_wf", "Apply Workflow"));
    }

     // Adds a FormProcessListener to that applies a clone of the
     // WorkflowTemplate to this ContentItem. In case of double-click,
     // no change is made.
    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final BigDecimal flowID = (BigDecimal) m_radio.getValue(state);

            // XXX hideous hack
            final BigDecimal itemID =
                CMS.getContext().getContentItem().getID();

            if (Workflow.getObjectWorkflowID(itemID) == null) {
                final WorkflowTemplate template = new WorkflowTemplate(flowID);

                final Workflow flow = template.instantiateNewWorkflow();

                flow.setObjectID(itemID);
                flow.start(Web.getContext().getUser());
                flow.save();
            }
        }
    }
}
