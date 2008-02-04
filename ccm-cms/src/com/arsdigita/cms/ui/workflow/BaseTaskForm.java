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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.cms.ui.DataCollectionOptionPrintListener;
import com.arsdigita.cms.ui.DataQueryOptionPrintListener;
import com.arsdigita.cms.workflow.CMSTaskType;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.workflow.simple.Task;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TooManyListenersException;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: BaseTaskForm.java 1280 2006-07-27 09:12:09Z cgyg9330 $
 */
class BaseTaskForm extends BaseForm {
    public static final String versionId =
        "$Id: BaseTaskForm.java 1280 2006-07-27 09:12:09Z cgyg9330 $" +
        "$Author: cgyg9330 $" +
        "$DateTime: 2004/04/07 16:07:11 $";

    private static final Logger s_log = Logger.getLogger(BaseTaskForm.class);

    final WorkflowRequestLocal m_workflow;

    final TextField m_name;
    final TextArea m_description;
    final OptionGroup m_type;
    final OptionGroup m_deps;

    BaseTaskForm(final String key,
                 final GlobalizedMessage message,
                 final WorkflowRequestLocal workflow) {
        super(key, message);

        m_workflow = workflow;

        m_name = new Name("name", 200, true);
        addField(gz("cms.ui.name"), m_name);

        m_type = new SingleSelect(new IntegerParameter("task_type"));
        addField(gz("cms.ui.workflow.task.type"), m_type);

        try {
            m_type.addPrintListener(new TaskTypePrintListener());
        } catch (TooManyListenersException tmle) {
            throw new UncheckedWrapperException(tmle);
        }

        m_description = new Description("desc", 4000, true);
        addField(gz("cms.ui.description"), m_description);

        m_deps = new CheckboxGroup("dep");
        addField(gz("cms.ui.workflow.task.dependencies"), m_deps);

        addAction(new Finish());
        addAction(new Cancel());

        addSecurityListener(SecurityManager.WORKFLOW_ADMIN);
        addValidationListener(new ValidationListener());
    }

    private class ValidationListener implements FormValidationListener {
        public final void validate(final FormSectionEvent e)
                throws FormProcessException {
            final String name = (String) m_name.getValue(e.getPageState());

            // XXX do a dupe check here ala commented out code below
        }
    }

    /*
    protected void addValidationListener() {
        addValidationListener(new DataQueryExistsListener(ERROR_MSG) {
                private final String QUERY_NAME =
                    "com.arsdigita.workflow.simple.getTasks";

                public void validate(FormSectionEvent event)
                    throws FormProcessException {
                    String name = (String) m_name.getValue(event.getPageState());
                    if ( name != null ) {
                        super.validate(event);
                    } else {
                        // Do nothing. Let the NotNullValidationListener fire.
                    }
                }

                public DataQuery getDataQuery(FormSectionEvent e) {
                    PageState s = e.getPageState();
                    Session session = SessionManager.getSession();
                    DataQuery query = session.retrieveQuery(QUERY_NAME);
                    Filter f = query.addFilter("lower(taskLabel) = lower(:label)");
                    f.set("label", ((String) m_name.getValue(s)).trim());
                    Filter parentFilter = query.addFilter("taskParentId = :parent_id");
                    parentFilter.set("parent_id", m_processes.getSelectedKey(s));
                    Filter itemFilter = query.addNotEqualsFilter
                        ("taskId", (BigDecimal)m_id.getValue(s));

                    return query;
                }
            });
    }
    */

    // Fix this one too
    private class TaskTypePrintListener extends DataCollectionOptionPrintListener {
        protected DataCollection getDataCollection(PageState s) {
            // XXX qelim
            final DataCollection taskTypes = SessionManager.getSession().retrieve(CMSTaskType.BASE_DATA_OBJECT_TYPE);
            taskTypes.addOrder("upper(" + CMSTaskType.NAME + ")");
            return taskTypes;

        }

        public String getKey(DomainObject d) {
            return ((CMSTaskType)d).getID().toString();
        }

        public String getValue(DomainObject d) {
            return ((CMSTaskType) d).getName();
        }
    }

    /**
     * This method decides which dependencies have to be removed and
     * which ones newly added. Unfortunately we cannot just do "remove
     * all", and add the new ones in since it is possible that Tasks
     * will fire events when dependencies are added and removed.
     *
     * XXX domlay
     */
    final void processDependencies(Task t, String[] selectedDependencies) {
        Iterator iter = t.getDependencies();
        HashMap toAdd = new HashMap();
        HashMap toRemove = new HashMap();
        Task temp;

        // Everything is to be removed unless it is in the array.

        while (iter.hasNext()) {
            temp = (Task) iter.next();
            toRemove.put(temp.getID(), temp);
        }

        BigDecimal b;
        Object addedTask;

        if (selectedDependencies != null) {
            for (int i = 0 ; i < selectedDependencies.length; i+=1) {
                b = new BigDecimal(selectedDependencies[i]);

                addedTask = toRemove.remove(b);

                if (addedTask == null) {
                    toAdd.put (b, new Task(b));
                }
            }
        }

        iter = toRemove.values().iterator();

        while (iter.hasNext()) {
            t.removeDependency((Task) iter.next());
        }

        iter = toAdd.values().iterator();

        while (iter.hasNext()) {
            t.addDependency((Task) iter.next());
        }
    }
}
