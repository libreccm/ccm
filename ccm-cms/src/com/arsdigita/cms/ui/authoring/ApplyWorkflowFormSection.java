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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.CMSContext;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeWorkflowTemplate;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.cms.ui.workflow.WorkflowsOptionPrintListener;
import com.arsdigita.cms.workflow.CMSEngine;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.Filter;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

/**
 * A FormSection which will allow users with
 * SecrityConstants.APPLY_ALTERNATE_WORFLOWS permission to choose a
 * different workflow to apply to a new item.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @version $Revision: #5 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class ApplyWorkflowFormSection extends FormSection implements FormInitListener {
    
    private RadioGroup m_radio;
    private CreationSelector m_creationSelector;
    private ContentType m_type;
    private ApplyWorkflowPrintListener m_listener;

    private static final Logger s_log = Logger.getLogger(ApplyWorkflowFormSection.class);

    /**
     * Construct a new ApplyWorkflowFormSection
     */     
    public ApplyWorkflowFormSection() {
        this(null);
    }

    /**
     * Construct a new ApplyWorkflowFormSection
     */     
    public ApplyWorkflowFormSection(ContentType type) {
        this(type, new ColumnPanel(2, true));
    }
    
    /**
     * Construct a new ApplyWorkflowFormSection
     *
     * @param panel Container to use for this FormSection
     */     
    public ApplyWorkflowFormSection(ContentType type, Container panel) {
        super(panel);
        m_radio = new RadioGroup(new BigDecimalParameter("workflowSelect"));
        m_radio.setClassAttr("vertical");
        m_type = type;
        m_listener = new ApplyWorkflowPrintListener();
        try {
            // should we filter on WorkflowDefinitions where this user
            // is assigned to at least one initial task, or should we
            // assume that users with "alternate workflow" permission
            // are advanced enough to know what they're doing? 
            m_radio.addPrintListener(m_listener);
        } catch (TooManyListenersException t) {
            s_log.error("Too many listeners", t);
        }
        
        add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.workflow")));
        m_radio.addValidationListener(new NotNullValidationListener() {
                public void validate (ParameterEvent e) {
                    PageState state = e.getPageState();
                    if (!ApplyWorkflowFormSection.this.isVisible(state)) {
                        return;
                    }
                    super.validate(e);
                }
            });
        add(m_radio);
        addInitListener(this);
    }
    
    /**
     * Initializes the workflow selection widget to the default workflow for the content type.
     */
    public void init(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        final ContentSection section = m_creationSelector.getContentSection(state);
        WorkflowTemplate template = ContentTypeWorkflowTemplate.getWorkflowTemplate
                (section, m_type);
        if (template != null) {
            m_radio.setValue(state,template.getID());
        }
    }

    /**
     * Sets the CreationSelector which should be the same as that of
     * the creation component. This cannot be set in the constructor
     * since for most creation components, addWidgets() is called via
     * the superclass constructor, so this member will not yet be set.
     *
     * @param creationSelector CreationSelector to use for this FormSection
     */     
    public void setCreationSelector(CreationSelector creationSelector) {
        m_creationSelector = creationSelector;
    }

    /**
     * Sets the ContentType for the creation component.
     *
     * @param contentType ContentType to use for this FormSection
     */     
    public void setContentType(ContentType contentType) {
        m_type = contentType;
    }

    /**
     * Whether or not this component is visible. The additional
     * visibility requirement is that the user must have the
     * SecurityConstants.APPLY_ALTERNATE_WORKFLOWS privilege on the
     * parent folder.
     *
     * @param state The PageState
     */
    public boolean isVisible(PageState state) {
        boolean result = false;
        if (super.isVisible(state) && 
            getSecurityManager(state).canAccess
            (Web.getContext().getUser(), SecurityConstants.APPLY_ALTERNATE_WORKFLOWS, 
             m_creationSelector.getFolder(state))) {
            TaskCollection t = m_listener.getCollection(state);
            if (t.next()) {
                t.close();
                result = true;
            }
        }
        return result;
    }
    
    /**
     * Apply the proper initial workflow to the item. If the user has
     * SecurityConstants.APPLY_ALTERNATE_WORKFLOWS permission on the
     * parent folder <em>and</em> a workflow has been chosen, use this
     * workflow. Otherwise use the default workflow for the content type.
     *
     * @param state The PageState
     * @param item The new ContentItem
     */
    public void applyWorkflow(PageState state, ContentItem item) {
        final BigDecimal flowID = (BigDecimal) m_radio.getValue(state);
        User user = Web.getContext().getUser();
        final ContentSection section = m_creationSelector.getContentSection(state);
        Folder f = m_creationSelector.getFolder(state);
        final WorkflowTemplate template;
        
        if (flowID != null && 
            getSecurityManager(state).canAccess
            (user, SecurityConstants.APPLY_ALTERNATE_WORKFLOWS, f)) {
            template = new WorkflowTemplate(flowID);
        } else {
            template = ContentTypeWorkflowTemplate.getWorkflowTemplate
                (section, item.getContentType());
        }
        
        if (template != null) {
            
            final Workflow workflow = template.instantiateNewWorkflow();
            workflow.setObjectID(item.getID());
            workflow.start(user);
            
            final Engine engine = Engine.getInstance(CMSEngine.CMS_ENGINE_TYPE);
            Assert.exists(engine, CMSEngine.class);
            Iterator iter = engine.getEnabledTasks
                (user, workflow.getID()).iterator();
            while (iter.hasNext()) {
                CMSTask task = (CMSTask) iter.next();
                if (!task.isLocked()) {
                    task.lock(user);
                }
            }
        }
        
    }

    private SecurityManager getSecurityManager(PageState state) {
        CMSContext context = CMS.getContext();
        SecurityManager sm;
        if (context.hasSecurityManager()) {
            sm = CMS.getContext().getSecurityManager();
        } else { 
            sm = new SecurityManager(m_creationSelector.getContentSection(state));
        }
        return sm;
    }

    private class ApplyWorkflowPrintListener extends WorkflowsOptionPrintListener {
        protected ContentSection getContentSection(PageState state) {
            return m_creationSelector.getContentSection(state);
        }

        protected TaskCollection getCollection(PageState state) {
            TaskCollection templates = super.getCollection(state);
            Filter f = templates.addInSubqueryFilter
                ("id", "com.arsdigita.cms.getWorkflowTemplateUserFilter");
            f.set("userId", Web.getContext().getUser().getID());
            return templates;
        }

    }
}
