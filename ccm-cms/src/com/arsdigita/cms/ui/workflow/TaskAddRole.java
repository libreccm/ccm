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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.DataQueryOptionPrintListener;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Group;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TooManyListenersException;

class TaskAddRole extends CMSForm {
    private final TaskRequestLocal m_task;

    private final OptionGroup m_roles;
    private final Submit m_add;
    private final Submit m_cancel;

    TaskAddRole(final TaskRequestLocal task) {
        super("GroupAssignForm");

        m_task = task;

        add(new Label(gz("cms.ui.workflow.task.roles")), ColumnPanel.TOP);

        m_roles = new CheckboxGroup("roles");
        add(m_roles);

        try {
            m_roles.addPrintListener(new RoleOptionPrintListener());
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("TooManyListeners: " + e.getMessage(), e);
        }

        final SimpleContainer submits = new SimpleContainer();
        add(submits, GridPanel.FULL_WIDTH | GridPanel.CENTER);

        m_add = new Submit("add", gz("cms.ui.finish"));
        submits.add(m_add);

        m_cancel = new Submit("cancel", gz("cms.ui.cancel"));
        submits.add(m_cancel);

        addInitListener(new InitListener());
        addSubmissionListener(new SubmissionListener());
        addProcessListener(new ProcessListener());
    }

    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final CMSTask task = m_task.getTask(state);

            final Iterator iter = task.getAssignedGroups();
            final ArrayList list = new ArrayList();

            while (iter.hasNext()) {
                list.add(((Group) iter.next()).getID().toString());
            }

            m_roles.setValue(state, list.toArray());
        }
    }

    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final CMSTask task = m_task.getTask(state);

            task.removeAllGroupAssignees();

            final String[] roles = (String[]) m_roles.getValue(state);

            if (roles != null) {
                for (int i = 0; i < roles.length; i++) {
                    task.assignGroup(new Group(new BigDecimal(roles[i])));
                }
            }

            task.save();
        }
    }

    private class SubmissionListener implements FormSubmissionListener {
        public final void submitted(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final SecurityManager sm = Utilities.getSecurityManager(state);

            if (!sm.canAccess(state.getRequest(),
                              SecurityManager.WORKFLOW_ADMIN)) {
                throw new FormProcessException
                    (lz(("cms.ui.workflow.insufficient_privileges")));
            }
        }
    }

    private class RoleOptionPrintListener extends DataQueryOptionPrintListener {
        public static final String QUERY_NAME =
            "com.arsdigita.cms.getStaffRoles";

        public RoleOptionPrintListener() {
            super();
        }

        protected DataQuery getDataQuery(PageState s) {
            ContentSection section = CMS.getContext().getContentSection();
            Session session = SessionManager.getSession();
            DataQuery query = session.retrieveQuery(QUERY_NAME);
            Filter parentFilter = query.addFilter("sectionId = :sectionId");
            parentFilter.set("sectionId", section.getID());
            query.addOrder("upper(name)");
            return query;
        }

        public String getKey(DataQuery d) {
            return d.get("groupId").toString();
        }

        public String getValue(DataQuery d) {
            return (String)d.get("name");
        }
    }

    private static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }
}
