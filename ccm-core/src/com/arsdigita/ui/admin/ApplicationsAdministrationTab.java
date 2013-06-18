/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.ui.admin.applications.ApplicationCreateForm;
import com.arsdigita.ui.admin.applications.ApplicationInstancePane;
import com.arsdigita.ui.admin.applications.ApplicationManager;
import com.arsdigita.ui.admin.applications.BaseApplicationPane;
import com.arsdigita.ui.admin.applications.MultiInstanceApplicationPane;
import com.arsdigita.ui.admin.applications.SingletonApplicationPane;
import com.arsdigita.ui.admin.applications.tree.ApplicationTreeModelBuilder;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.ApplicationTypeCollection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * A tab for managing Application and application instances.
 * 
 * @author pb
 * @author Jens Pelzetter
 */
public class ApplicationsAdministrationTab extends BoxPanel implements AdminConstants, ChangeListener {

    //private GlobalizedMessage m_title;
    private final Tree applicationTree;
    private final Map<String, SingletonApplicationPane> singletonAppPanes =
                                                        new HashMap<String, SingletonApplicationPane>();
    private final Map<String, MultiInstanceApplicationPane<?>> multiInstAppPanes =
                                                               new HashMap<String, MultiInstanceApplicationPane<?>>();
    private final Map<String, BaseApplicationPane> appPanes = new HashMap<String, BaseApplicationPane>();
    private final Map<String, ApplicationInstancePane> instancePanes = new HashMap<String, ApplicationInstancePane>();
    private SimpleContainer visiblePane;
    //private final Label label1;
    //private final Label label2;

    /**
     * Constructor
     */
    public ApplicationsAdministrationTab() {

        super();

        // m_title = "TEST für ein neues Pannel";
        setClassAttr("sidebarNavPanel");
        setAttribute("navbar-title", "Sitemap");
        //  m_componentList = new ArrayList();
        //  m_keys = new ArrayList();

        applicationTree = new Tree(new ApplicationTreeModelBuilder());
        applicationTree.addChangeListener(this);

        final Section treeSection = new Section();
        treeSection.setHeading(GlobalizationUtil.globalize("ui.admin.applications.tree.heading"));
        treeSection.setBody(applicationTree);

        //final BoxPanel panel1 = new BoxPanel();
//        label1 = new Label("login");
//        label2 = new Label("ppp");
//        panel1.add(label1);
//        panel1.add(label2);


        final LayoutPanel panel = new LayoutPanel();
        //panel.setLeft(applicationTree);
        panel.setLeft(treeSection);
        //panel.setRight(new Label("<<<Placeholder>>>"));
        //panel.setRight(panel1);

        final ApplicationTypeCollection applicationTypes = ApplicationType.retrieveAllApplicationTypes();

        final Map<String, ApplicationCreateForm<?>> createForms = retrieveAppCreateForms();
        final Map<String, ApplicationManager<?>> managementForms = retrieveAppManagers();

        while (applicationTypes.next()) {
            if (applicationTypes.getApplicationType().isSingleton()) {
                createSingletonAppPane(applicationTypes.getApplicationType(), managementForms);
            } else {
                createAppPane(applicationTypes.getApplicationType(), createForms, managementForms);
            }
        }

        final BoxPanel appPanel = new BoxPanel();
        for (Map.Entry<String, BaseApplicationPane> entry : appPanes.entrySet()) {
            appPanel.add(entry.getValue());
        }

        for (Map.Entry<String, ApplicationInstancePane> entry : instancePanes.entrySet()) {
            appPanel.add(entry.getValue());
        }
        panel.setRight(appPanel);


       


//        final BoxPanel box = new BoxPanel();
//        box.setClassAttr("main");
//
//        final SplitPanel panel = new SplitPanel();
//        panel.setClassAttr("sidebarNavPanel");
//        panel.setLeftComponent(applicationTree);        
//        panel.setRightComponent(box);
//
//        //box.add(new ApplicationsTable());
//        box.add(new Label("<<<Placeholder>>>"));
//

        add(panel);
    }

    @SuppressWarnings("rawtypes")
    private Map<String, ApplicationCreateForm<?>> retrieveAppCreateForms() {
        final Map<String, ApplicationCreateForm<?>> appCreateForms = new HashMap<String, ApplicationCreateForm<?>>();

        final ServiceLoader<ApplicationCreateForm> loader = ServiceLoader.load(ApplicationCreateForm.class);
        for (ApplicationCreateForm<?> appCreateForm : loader) {
            appCreateForms.put(appCreateForm.getAppClassName(), appCreateForm);
        }

        return appCreateForms;
    }

    @SuppressWarnings("rawtypes")
    private Map<String, ApplicationManager<?>> retrieveAppManagers() {
        final Map<String, ApplicationManager<?>> appManagers = new HashMap<String, ApplicationManager<?>>();

        final ServiceLoader<ApplicationManager> loader = ServiceLoader.load(ApplicationManager.class);
        for (ApplicationManager<?> appManager : loader) {
            appManagers.put(appManager.getApplication().getName(), appManager);
        }

        return appManagers;
    }

    private void createSingletonAppPane(final ApplicationType applicationType,
                                        final Map<String, ApplicationManager<?>> managementForms) {
        final String appObjectType = applicationType.getApplicationObjectType();

        final ApplicationManager<?> manager = managementForms.get(appObjectType);
        final SingletonApplicationPane pane;
        if (manager == null) {
            pane = new SingletonApplicationPane(applicationType, null);
        } else {
            pane = new SingletonApplicationPane(
                    applicationType, managementForms.get(appObjectType).getApplicationAdminForm());
        }
        //singletonAppPanes.put(appObjectType, pane);
        appPanes.put(appObjectType, pane);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void createAppPane(final ApplicationType applicationType,
                               final Map<String, ApplicationCreateForm<?>> createForms,
                               final Map<String, ApplicationManager<?>> managementForms) {
        final MultiInstanceApplicationPane<?> appPane = new MultiInstanceApplicationPane(
                applicationType,
                createForms.get(applicationType.getApplicationObjectType()));
        //multiInstAppPanes.put(applicationType.getApplicationObjectType(), appPane);
        appPanes.put(applicationType.getApplicationObjectType(), appPane);

        final ApplicationCollection instances = Application.retrieveAllApplications(
                applicationType.getApplicationObjectType());

        while (instances.next()) {
            createInstancePane(instances.getApplication(), managementForms);
        }
    }

    private void createInstancePane(final Application application,
                                    final Map<String, ApplicationManager<?>> managementForms) {
        final ApplicationManager<?> manager = managementForms.get(application.getClass().getName());

        final ApplicationInstancePane instPane;
        if (manager == null) {
            instPane = new ApplicationInstancePane(application, null);
        } else {
            instPane = new ApplicationInstancePane(
                    application,
                    managementForms.get(application.getClass().getName()).getApplicationAdminForm());
        }
        instancePanes.put(application.getClass().getName(), instPane);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        for (Map.Entry<String, BaseApplicationPane> entry : appPanes.entrySet()) {
            page.setVisibleDefault(entry.getValue(), false);
        }
        for (Map.Entry<String, ApplicationInstancePane> entry : instancePanes.entrySet()) {
            page.setVisibleDefault(entry.getValue(), false);
        }
        //page.setVisibleDefault(label1, false);
        //page.setVisibleDefault(label2, false);
    }

    /**
     *
     * @param event      
     */
    @Override
    public void stateChanged(final ChangeEvent event) {

        final PageState state = event.getPageState();

        System.out.println("State changed.");

        final String selectedKey = (String) applicationTree.getSelectedKey(state);
        if (selectedKey != null) {
            if (selectedKey.contains(".")) {
                // Selected key is a classname and therefore the key of an ApplicationPane
                final BaseApplicationPane pane = appPanes.get(selectedKey);
                if (pane != null) {
                    setPaneVisible(pane, state);
                }
            } else {
                // Selected key is the name of a instance pane
                final ApplicationInstancePane pane = instancePanes.get(selectedKey);
                if (pane != null) {
                    setPaneVisible(pane, state);
                }
            }
        }

        //ToDo Find out if the key is the key of a application type (contains dots) or the key of an instance (may 
        //contains Slashes


//        if ("ui.login.Login".equals(applicationTree.getSelectedKey(state))) {
//            label1.setVisible(state, true);
//            label2.setVisible(state, false);
//        } else if ("com.arsdigita.cms.publicpersonalprofile.PublicPersonalProfile".equals(applicationTree.
//                getSelectedKey(state))) {
//            label1.setVisible(state, false);
//            label2.setVisible(state, true);
//        } else {
//            label1.setVisible(state, false);
//            label2.setVisible(state, false);
//        }

        // String key = (String) m_tree.getSelectedKey(ps);
        // added cg - reset existing group add panel to the search screen 
        // when a new group is selected from the tree
        // ps.setValue(GROUP_ID_PARAM, new BigDecimal(key));
        // int selectedIndex = Integer.parseInt((String) m_list.getSelectedKey(ps));
        // setTab(selectedIndex, ps);
    }

    private void setPaneVisible(final SimpleContainer pane, final PageState state) {
        if (visiblePane != null) {
            visiblePane.setVisible(state, false);
        }

        pane.setVisible(state, true);
        visiblePane = pane;
    }

    private class ApplicationsTable extends Table implements TableActionListener {

        private static final String COL_APP_CLASS = "col_app_class";
        private static final String COL_APP_TYPE = "col_app_type";
        private static final String COL_APP_VIEW_URL = "col_app_view_url";
        private static final String COL_APP_ADMIN_URL = "col_app_admin_url";
        private static final String COL_APP_SINGLETON = "col_app_singleton";

        public ApplicationsTable() {
            super();

            setEmptyView(new Label("No applications installed."));

            final TableColumnModel colModel = getColumnModel();
            colModel.add(new TableColumn(
                    0,
                    "App Class",
                    COL_APP_CLASS));
            colModel.add(new TableColumn(
                    1,
                    "App Type",
                    COL_APP_TYPE));
            colModel.add(new TableColumn(
                    2,
                    "App View URL",
                    COL_APP_VIEW_URL));
            colModel.add(new TableColumn(
                    3,
                    "Is Singleton?",
                    COL_APP_SINGLETON));

            setModelBuilder(new ApplicationsTableModelBuilder());

            colModel.get(0).setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    return new Label(value.toString());
                }

            });

            colModel.get(1).setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    return new Label(value.toString());
                }

            });

            colModel.get(2).setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    return new Link(value.toString(), value.toString());
                }

            });

            colModel.get(3).setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    return new Label(value.toString());
                }

            });

            addTableActionListener(this);



        }

        private class ApplicationsTableModelBuilder extends LockableImpl implements TableModelBuilder {

            @Override
            public TableModel makeModel(final Table table, final PageState state) {
                return new ApplicationsTableModel(table);
            }

        }

        private class ApplicationsTableModel implements TableModel {

            private final Table table;
            private final ApplicationCollection apps;
            private Application app;

            public ApplicationsTableModel(final Table table) {
                this.table = table;

                apps = Application.retrieveAllApplications();
                apps.addOrder("objectType");
            }

            @Override
            public int getColumnCount() {
                return table.getColumnModel().size();
            }

            @Override
            public boolean nextRow() {
                boolean ret;

                if ((apps != null) && apps.next()) {
                    app = apps.getApplication();
                    ret = true;
                } else {
                    ret = false;
                }

                return ret;
            }

            @Override
            public Object getElementAt(final int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return String.format("%s (%s)", app.getObjectType().getName(), app.getClass().getName());
                    case 1:
                        return app.getApplicationType().getTitle();
                    case 2:
                        return app.getPath();
                    case 3:
                        return Boolean.toString(app.getApplicationType().isSingleton());
                    default:
                        return null;
                }
            }

            @Override
            public Object getKeyAt(final int columnIndex) {
                return app.getID();
            }

            private String constructAppPath(final Application app) {
                if (app.getParentApplication() == null) {
                    return app.getPath();
                } else {
                    return String.format("%s/%s", constructAppPath(app.getParentApplication()), app.getPath());
                }
            }

        }

        @Override
        public void cellSelected(final TableActionEvent event) {
            //Nothing for now
        }

        @Override
        public void headSelected(final TableActionEvent event) {
            //Nothing for now
        }

    }
}
