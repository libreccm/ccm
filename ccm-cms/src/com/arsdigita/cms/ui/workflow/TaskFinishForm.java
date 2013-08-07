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
import com.arsdigita.bebop.FormValidationException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentCenter;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.workflow.CMSEngine;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.cms.workflow.CMSTaskType;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.Task;
import com.arsdigita.workflow.simple.TaskException;
import org.apache.log4j.Logger;

import java.util.Iterator;

/**
 * <p>A form that prompts the user to comment on and approve tasks and then
 * finishes the task if it was approved.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: TaskFinishForm.java 1563 2007-04-18 15:58:17Z apevec $
 */
public final class TaskFinishForm extends CommentAddForm {

    private static final Logger s_log = Logger.getLogger(TaskFinishForm.class);
    private final TaskRequestLocal m_task;
    private final Label m_approvePrompt;
    private final RadioGroup m_approve;

    public TaskFinishForm(final TaskRequestLocal task) {
        super(task);

        m_task = task;

        m_approve = new RadioGroup(new BooleanParameter("approve"));
        m_approve.addOption(new Option("true", lz("cms.ui.workflow.task.approve")));
        m_approve.addOption(new Option("false", lz("cms.ui.workflow.task.reject")));

        m_approvePrompt = new Label(gz("cms.ui.workflow.task.approve_prompt"));

        addComponent(m_approvePrompt);
        addComponent(m_approve);

        addInitListener(new InitListener());
        addValidationListener(new ValidationListener());
        addProcessListener(new ProcessListener());
    }

    private class InitListener implements FormInitListener {

        public final void init(final FormSectionEvent e) {
            s_log.debug("Initializing task finish");

            final PageState state = e.getPageState();

            if (isVisible(state)) {
                final CMSTask task = m_task.getTask(state);

                if (requiresApproval(task)) {
                    m_approvePrompt.setVisible(state, true);
                    m_approve.setVisible(state, true);
                } else {
                    m_approvePrompt.setVisible(state, false);
                    m_approve.setVisible(state, false);
                }
            }
        }
    }

    private class ValidationListener implements FormValidationListener {

        public final void validate(final FormSectionEvent e)
                throws FormProcessException {
            s_log.debug("Validating task finish");

            final PageState state = e.getPageState();
            final CMSTask task = m_task.getTask(state);

            if (requiresApproval(task) && m_approve.getValue(state) == null) {
                throw new FormProcessException(lz("cms.ui.workflow.task.approval_or_reject_required"));
            }
        }
    }

    private class ProcessListener implements FormProcessListener {

        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            s_log.debug("Processing task finish");

            final PageState state = e.getPageState();
            final CMSTask task = m_task.getTask(state);
            boolean finishedTask = false;

            // double check that user is allowed to finish this task
            Party user = Kernel.getContext().getParty();
            if (user == null) {
                user = Kernel.getPublicUser();
            }

            PermissionDescriptor taskAccess = new PermissionDescriptor(task.getTaskType().getPrivilege(), task.getWorkflow().getObject(), user);
            PermissionService.assertPermission(taskAccess);

            if (requiresApproval(task)) {
                s_log.debug("The task requires approval; checking to see "
                        + "if it's approved");

                // XXX I think the fact that this returns a Boolean is
                // the effect of broken parameter marshalling in
                // Bebop.
                final Boolean isApproved = (Boolean) m_approve.getValue(state);

                if (isApproved.equals(Boolean.TRUE)) {
                    s_log.debug("The task is approved; finishing the task");

                    try {
                        task.finish(Web.getContext().getUser());
                        finishedTask = true;
                    } catch (TaskException te) {
                        throw new FormValidationException(te.toString());
                    }
                } else {
                    s_log.debug("The task is rejected; reenabling dependent "
                            + "tasks");

                    // Reenable the previous tasks.

                    final Iterator iter = task.getDependencies();

                    while (iter.hasNext()) {
                        final Task dependent = (Task) iter.next();

                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Reenabling task " + dependent.getLabel());
                        }

                        dependent.enable();
                        dependent.save();
                    }
                }
            } else {
                s_log.debug("The task does not require approval; finishing "
                        + "it");

                try {
                    task.finish(Web.getContext().getUser());
                    finishedTask = true;
                } catch (TaskException te) {
                    throw new FormValidationException(te.toString());
                }
            }
            if (finishedTask) {
                Iterator tasks = Engine.getInstance(CMSEngine.CMS_ENGINE_TYPE).getEnabledTasks(Web.getContext().getUser(),
                        task.getParentID()).iterator();
                if (tasks.hasNext()) {
                    CMSTask thisTask = (CMSTask) tasks.next();
                    PermissionDescriptor thisTaskAccess = new PermissionDescriptor(thisTask.getTaskType().getPrivilege(), task.getWorkflow().getObject(), user);
                    if (PermissionService.checkPermission(thisTaskAccess)) {

                        // Lock task for user
                        thisTask.lock((User) user);
                        int targetTab = (thisTask.getTaskType().getID().equals(CMSTaskType.DEPLOY)) ? ContentItemPage.PUBLISHING_TAB : ContentItemPage.AUTHORING_TAB;
                        throw new RedirectSignal(URL.there(state.getRequest(),
                                ContentItemPage.getItemURL(task.getItem(),
                                targetTab)),
                                true);
                    }
                }
                // redirect to /content-center if streamlined creation mode is active.
                if (ContentSection.getConfig().getUseStreamlinedCreation()) {
                    throw new RedirectSignal(URL.there(state.getRequest(),
                            ContentCenter.getURL()),
                            true);
                }

            }
        }
    }

    private static boolean requiresApproval(final CMSTask task) {
        return !task.getTaskType().getID().equals(CMSTaskType.AUTHOR);
    }
}
