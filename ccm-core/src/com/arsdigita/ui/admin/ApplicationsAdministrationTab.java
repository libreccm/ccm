/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SplitPanel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;

/**
 *
 * @author pb
 * @author Jens Pelzetter
 */
public class ApplicationsAdministrationTab extends BoxPanel
        implements AdminConstants, ChangeListener {

    private GlobalizedMessage m_title;

    /**
     * Constructor
     */
    public ApplicationsAdministrationTab() {

        // m_title = "TEST für ein neues Pannel";
        setClassAttr("sidebarNavPanel");
        setAttribute("navbar-title", "Sitemap");
        //  m_componentList = new ArrayList();
        //  m_keys = new ArrayList();


        final BoxPanel box = new BoxPanel();
        box.setClassAttr("main");

        final SplitPanel panel = new SplitPanel();
        panel.setClassAttr("sidebarNavPanel");
        panel.setRightComponent(box);

        box.add(new ApplicationsTable());

        add(panel);
    }

    /**
     *
     * @param e
     */
    @Override
    public void stateChanged(ChangeEvent e) {

        PageState ps = e.getPageState();
        // String key = (String) m_tree.getSelectedKey(ps);
        // added cg - reset existing group add panel to the search screen 
        // when a new group is selected from the tree
        // ps.setValue(GROUP_ID_PARAM, new BigDecimal(key));
        // int selectedIndex = Integer.parseInt((String) m_list.getSelectedKey(ps));
        // setTab(selectedIndex, ps);
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
