/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.workflow.CMSEngine;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.cms.workflow.CMSTaskType;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.Workflow;
import java.util.Collections;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 *
 *
 */
class AssignedTaskTableModelBuilder extends AbstractTableModelBuilder {

    private static final Logger s_log = Logger.getLogger(AssignedTaskTableModelBuilder.class);
    private final WorkflowRequestLocal m_workflow;

    public AssignedTaskTableModelBuilder(final WorkflowRequestLocal workflow) {
        m_workflow = workflow;
    }

    public TableModel makeModel(final Table table, final PageState state) {
        return new Model(m_workflow.getWorkflow(state));
    }

    private static class Model implements TableModel {

        private final Iterator m_iter;
        private CMSTask m_task;

        Model(final Workflow workflow) {
            Assert.exists(workflow, Workflow.class);

            if (workflow.getProcessState() == Workflow.STARTED) {

                final Engine engine = Engine.getInstance(CMSEngine.CMS_ENGINE_TYPE);

                Assert.exists(engine, Engine.class);

                m_iter = engine.getEnabledTasks(Web.getWebContext().getUser(), workflow.getID()).iterator();
            } else {
                m_iter = Collections.emptyList().iterator();
            }

        }

        public final int getColumnCount() {
            return 3;
        }

        public final boolean nextRow() {
            if (m_iter.hasNext()) {
                m_task = (CMSTask) m_iter.next();

                return true;
            } else {
                return false;
            }
        }

        public final Object getKeyAt(final int column) {
            return m_task.getID();
        }

        public final Object getElementAt(final int column) {
            switch (column) {
                case 0:
                    return m_task.getLabel();
                case 1:
                    // SF patch [ 1587168 ] Show locking user
                    return m_task.isLocked() ? m_task.getLockedUser() : null;
                case 2:
                    return m_task.getTaskType().getID().equals(CMSTaskType.DEPLOY)
                            ? (Object) new Label("") : // null should work as well
                            (Object) lz("cms.ui.workflow.task.finish");
                default:
                    throw new IllegalStateException();
            }
        }
    }

    protected final static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    protected final static String lz(final String key) {
        return (String) gz(key).localize();
    }
}
