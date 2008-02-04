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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.VisibilityComponent;
import com.arsdigita.cms.workflow.CMSEngine;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.PropertyList;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.Workflow;

import java.math.BigDecimal;
import java.util.Collection;

/*
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: TaskItemPane.java 1280 2006-07-27 09:12:09Z cgyg9330 $
 */
final class TaskItemPane extends BaseItemPane {
    public static final String versionId =
        "$Id: TaskItemPane.java 1280 2006-07-27 09:12:09Z cgyg9330 $" +
        "$Author: cgyg9330 $" +
        "$DateTime: 2004/04/07 16:07:11 $";

    private final WorkflowRequestLocal m_workflow;
    private final TaskRequestLocal m_task;

    private final SimpleContainer m_detailPane;

    TaskItemPane(final WorkflowRequestLocal workflow,
                 final TaskRequestLocal task,
                 final ActionLink finishLink,
                 final ActionLink editLink,
                 final ActionLink deleteLink,
                 final ActionLink backLink) {
        m_workflow = workflow;
        m_task = task;

        m_detailPane = new SimpleContainer();
        m_detailPane.add(new Navigation(backLink));
        m_detailPane.add(new SummarySection(finishLink, editLink, deleteLink));

        // Users

        final ActionLink userAddLink = new ActionLink
            (new Label(gz("cms.ui.workflow.task.user.add")));

        final TaskAddUser userAddPane = new TaskAddUser(m_task);

        final Form search = userAddPane.getSearchForm();
        final Form add = userAddPane.getAddForm().getForm();

        m_detailPane.add(new UserSection(userAddLink));

        // Roles

        final ActionLink roleAddLink = new ActionLink
            (new Label(gz("cms.ui.workflow.task.role.add")));

        final TaskAddRole roleAddForm = new TaskAddRole(m_task);

        m_detailPane.add(new RoleSection(roleAddLink));

        add(m_detailPane);
        setDefault(m_detailPane);
        add(userAddPane);
        add(roleAddForm);

        userAddLink.addActionListener(new NavigationListener(userAddPane));
        search.addSubmissionListener(new CancelListener(search));
        add.addSubmissionListener(new CancelListener(add));
        add.addProcessListener(new FormNavigationListener(m_detailPane));

        connect(roleAddLink, roleAddForm);
        resume(roleAddForm, m_detailPane);
    }

    private boolean hasAdmin(final PageState state) {
        final SecurityManager sm = CMS.getContext().getSecurityManager();

        return sm.canAccess(state.getRequest(), WORKFLOW_ADMIN);
    }

    private class AdminVisible extends VisibilityComponent {
        public AdminVisible(final Component child) {
            super(child, WORKFLOW_ADMIN);
        }
    }

    private class AssigneeVisible extends AdminVisible {
        private final Component m_child;
        private final Assigned m_assigned;

        public AssigneeVisible(final Component child) {
            super(child);

            m_child = child;
            m_assigned = new Assigned();
        }

        public final boolean isVisible(final PageState state) {
            if (m_child.isVisible(state)) {
                return m_assigned.isAssigned(state) || hasPermission(state);
            } else {
                return false;
            }
        }

        private class Assigned extends RequestLocal {
            protected final Object initialValue(final PageState state) {
                if (assigned(state)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }

            private boolean assigned(final PageState state) {
                final Workflow workflow = m_workflow.getWorkflow(state);
                final User user = Web.getContext().getUser();
                final CMSTask task = m_task.getTask(state);

                final Engine engine = Engine.getInstance(CMSEngine.CMS_ENGINE_TYPE);

                final Collection tasks = engine.getEnabledTasks
                    (user, workflow.getID());

                return tasks.contains(task);
            }

            final boolean isAssigned(final PageState state) {
                return ((Boolean) get(state)).booleanValue();
            }
        }
    }

    private class VisibilityListener implements ActionListener {
        private final TableColumn m_column;

        VisibilityListener(final TableColumn column) {
            m_column = column;
        }

        public final void actionPerformed(final ActionEvent e) {
            final PageState state = e.getPageState();

            if (state.isVisibleOnPage(TaskItemPane.this) && !hasAdmin(state)) {
                m_column.setVisible(state, false);
            }
        }
    }

    private class Navigation extends ActionGroup {
        Navigation(final ActionLink backLink) {
            addAction(backLink, ActionGroup.RETURN);
        }
    }

    private class SummarySection extends Section {
        SummarySection(final ActionLink finishLink,
                       final ActionLink editLink,
                       final ActionLink deleteLink) {
            setHeading(new Label(gz("cms.ui.workflow.task.details")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new Properties());
            group.addAction(new AssigneeVisible(new LockLink()));
            group.addAction(new AssigneeVisible(new UnlockLink()));
            group.addAction(new AssigneeVisible(finishLink));
            group.addAction(new AdminVisible(editLink), ActionGroup.EDIT);
            group.addAction(new AdminVisible(deleteLink), ActionGroup.DELETE);
        }

        private class LockLink extends ActionLink {
            LockLink() {
                super(new Label(gz("cms.ui.workflow.task.lock")));

                addActionListener(new Listener());
            }

            public final boolean isVisible(final PageState state) {
                final CMSTask task = m_task.getTask(state);

                return task.isEnabled() && !task.isLocked();
            }

            private class Listener implements ActionListener {
                public final void actionPerformed(final ActionEvent e) {
                    final PageState state = e.getPageState();

                    if (hasAdmin(state)) {
                        final CMSTask task = m_task.getTask(state);
                        task.lock(Web.getContext().getUser());
                        task.save();
                    }
                }
            }
        }

        private class UnlockLink extends ActionLink {
            UnlockLink() {
                super(new Label(gz("cms.ui.workflow.task.unlock")));

                addActionListener(new Listener());
            }

            public final boolean isVisible(final PageState state) {
                final CMSTask task = m_task.getTask(state);

                return task.isEnabled() && task.isLocked();
            }

            private class Listener implements ActionListener {
                public final void actionPerformed(final ActionEvent e) {
                    final PageState state = e.getPageState();

                    if (hasAdmin(state)) {
                        final CMSTask task = m_task.getTask(state);
                        task.unlock(Web.getContext().getUser());
                        task.save();
                    }
                }
            }
        }

        private class Properties extends PropertyList {
            protected final java.util.List properties(final PageState state) {
                final java.util.List props = super.properties(state);
                final CMSTask task = m_task.getTask(state);

                props.add(new Property(gz("cms.ui.name"),
                                       task.getLabel()));
                props.add(new Property(gz("cms.ui.description"),
                                       task.getDescription()));
                props.add(new Property(gz("cms.ui.workflow.task.dependencies"),
                                       deps(task)));
                props.add(new Property(gz("cms.ui.workflow.task.state"),
                                       task.getStateString()));
                props.add(new Property(gz("cms.ui.workflow.task.locked"),
                                       task.isLocked()
                                       ? lz("cms.ui.yes") : lz("cms.ui.no")));

                return props;
            }

            private String deps(final CMSTask task) {
                final TaskCollection deps = task.getRequiredTasks();
                final StringBuffer buffer = new StringBuffer();

                while (deps.next()) {
                    buffer.append(deps.getTask().getLabel() + ", ");
                }

                final int len = buffer.length();

                if (len >= 2) {
                    buffer.setLength(len - 2);
                }

                deps.close();

                return buffer.toString();
            }
        }
    }

    private class UserSection extends Section {
        public UserSection(final ActionLink userAddLink) {
            setHeading(new Label(gz("cms.ui.workflow.task.users")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new UserTable());
            group.addAction(new AdminVisible(userAddLink), ActionGroup.ADD);
        }
    }

    private class UserTable extends Table {
        public UserTable() {
            super(new UserTableModelBuilder(m_task),
                  new String[] {
                      lz("cms.ui.name"),  // XXX globz
                      lz("cms.ui.email"),
                      lz("cms.ui.workflow.task.user.delete")
                  });

            setEmptyView(new Label(gz("cms.ui.workflow.task.user.none")));

            getColumn(2).setCellRenderer(new DefaultTableCellRenderer(true));

            addTableActionListener(new TableActionAdapter() {
                    public final void cellSelected(final TableActionEvent e) {
                        final PageState state = e.getPageState();
                        final int column = e.getColumn().intValue();

                        if (column == 2) {
                            if (hasAdmin(state)) {
                                final CMSTask task = m_task.getTask(state);
                                final BigDecimal id = new BigDecimal
                                    ((String) e.getRowKey());

                                task.removeUser(User.retrieve(id));
                                task.save();
                            }
                        }
                    }
                });
        }

        public final void register(final Page page) {
            super.register(page);

            page.addActionListener
                (new VisibilityListener(getColumn(2)));
        }
    }

    private static class UserTableModelBuilder extends LockableImpl
            implements TableModelBuilder {
        private final TaskRequestLocal m_task;

        public UserTableModelBuilder(final TaskRequestLocal task) {
            m_task = task;
        }

        public final TableModel makeModel(final Table table,
                                          final PageState state) {
            return new Model(m_task.getTask(state));
        }

        private class Model implements TableModel {
            private User m_user;
            private final UserCollection m_users;

            private Model(final CMSTask task) {
                m_users = task.getAssignedUserCollection();
            }

            public final int getColumnCount() {
                return 3;
            }

            public final boolean nextRow() {
                if (m_users.next()) {
                    m_user = m_users.getUser();

                    return true;
                } else {
                    m_users.close();

                    return false;
                }
            }

            public final Object getKeyAt(final int column) {
                return m_user.getID().toString();
            }

            public final Object getElementAt(final int column) {
                switch (column) {
                case 0:
                    return m_user.getName();
                case 1:
                    final EmailAddress email = m_user.getPrimaryEmail();

                    if (email == null) {
                        return "";
                    } else {
                        return email.toString();
                    }
                case 2:
                    return lz("cms.ui.workflow.task.user.delete");
                default:
                    throw new IllegalStateException();
                }
            }
        }
    }

    private class RoleSection extends Section {
        public RoleSection(final ActionLink roleAddLink) {
            setHeading(new Label(gz("cms.ui.workflow.task.roles")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new RoleTable());
            group.addAction(new AdminVisible(roleAddLink), ActionGroup.ADD);
        }
    }

    private class RoleTable extends Table {
        public RoleTable() {
            super(new RoleTableModelBuilder(m_task),
                  new String[] {
                      lz("cms.ui.name"),  // XXX globz
                      lz("cms.ui.workflow.task.role.delete")
                  });

            setEmptyView(new Label(gz("cms.ui.workflow.task.role.none")));

            getColumn(1).setCellRenderer(new DefaultTableCellRenderer(true));

            addTableActionListener(new TableActionAdapter() {
                    public final void cellSelected(final TableActionEvent e) {
                        final PageState state = e.getPageState();
                        final int column = e.getColumn().intValue();

                        if (column == 1) {
                            if (hasAdmin(state)) {
                                final CMSTask task = m_task.getTask(state);
                                final BigDecimal id = new BigDecimal
                                    ((String) e.getRowKey());

                                task.removeGroup(new Group(id));
                                task.save();
                            }
                        }
                    }
                });
        }

        public final void register(final Page page) {
            super.register(page);

            page.addActionListener
                (new VisibilityListener(getColumn(1)));
        }
    }

    private static class RoleTableModelBuilder extends LockableImpl
            implements TableModelBuilder {
        private final TaskRequestLocal m_task;

        public RoleTableModelBuilder(final TaskRequestLocal task) {
            m_task = task;
        }

        public final TableModel makeModel(final Table table,
                                          final PageState state) {
            return new Model(m_task.getTask(state));
        }

        private class Model implements TableModel {
            private Group m_role;
            private final GroupCollection m_roles;

            private Model(final CMSTask task) {
                m_roles = task.getAssignedGroupCollection();
            }

            public final int getColumnCount() {
                return 2;
            }

            public final boolean nextRow() {
                if (m_roles.next()) {
                    m_role = m_roles.getGroup();

                    return true;
                } else {
                    m_roles.close();

                    return false;
                }
            }

            public final Object getKeyAt(final int column) {
                return m_role.getID().toString();
            }

            public final Object getElementAt(final int column) {
                switch (column) {
                case 0:
                    return m_role.getName();
                case 1:
                    return lz("cms.ui.workflow.task.role.delete");
                default:
                    throw new IllegalStateException();
                }
            }
        }
    }
}
