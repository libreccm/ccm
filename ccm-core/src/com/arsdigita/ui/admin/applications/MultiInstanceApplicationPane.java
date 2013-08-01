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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;

/**
 * Pane for multi instance applications. Additional to the data provided by {@link BaseApplicationPane} it shows a
 * table of all instances of the application type and a form for creating new instances of the application type.
 * 
 * @param <T> 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class MultiInstanceApplicationPane<T extends Application> extends BaseApplicationPane {

    private final static int COL_TITLE = 0;
    private final static int COL_URL = 1;
    private final static int COL_DESC = 2;

    public MultiInstanceApplicationPane(final ApplicationType applicationType, final Form createForm) {
        super(applicationType);

        //final ApplicationCollection applications = Application.retrieveAllApplications(applicationType.
        //        getApplicationObjectType());
        //applications.rewind();
        final Table table = new Table();
        table.getColumnModel().add(new TableColumn(COL_TITLE,
                                                   new Label(GlobalizationUtil.globalize(
                "ui.admin.applicationsMultiInstanceApplicationPane.instances.table.col_title.header"))));
        table.getColumnModel().add(new TableColumn(COL_URL,
                                                   new Label(GlobalizationUtil.globalize(
                "ui.admin.applicationsMultiInstanceApplicationPane.instances.table.col_url.header"))));
        table.getColumnModel().add(new TableColumn(COL_DESC,
                                                   new Label(GlobalizationUtil.globalize(
                "ui.admin.applicationsMultiInstanceApplicationPane.instances.table.col_desc.header"))));

        //table.setModelBuilder(new ApplicationInstancesTableModelBuilder(applications));
        table.setModelBuilder(new ApplicationInstancesTableModelBuilder(applicationType.getApplicationObjectType()));

        addSegment(new Label(GlobalizationUtil.globalize(
                "ui.admin.MultiInstanceApplicationPane.instances")),
                   table);

        if (createForm == null) {
            addSegment(new Label(GlobalizationUtil.globalize(
                    "ui.admin.MultiInstanceApplicationPane.manage_instances.heading")),
                       new Label(GlobalizationUtil.globalize(
                    "ui.admin.MultiInstancePane.manage.no_create_form_found",
                    new String[]{applicationType.getApplicationObjectType()})));
        } else {
            addSegment(new Label(GlobalizationUtil.globalize(
                    "ui.admin.MultiInstanceApplicationPane.create_instance")),
                       createForm);

        }
    }

    private class ApplicationInstancesTableModelBuilder extends LockableImpl implements TableModelBuilder {

        private final ApplicationCollection applications;

        public ApplicationInstancesTableModelBuilder(final ApplicationCollection applications) {
            super();

            this.applications = applications;
        }
        
        public ApplicationInstancesTableModelBuilder(final String appType) {
            super();
            
            this.applications = Application.retrieveAllApplications(appType);
        }

        public TableModel makeModel(final Table table, final PageState state) {
            return new ApplicationInstancesTableModel(table, applications);
        }

    }

    private class ApplicationInstancesTableModel implements TableModel {

        private final Table table;
        private final ApplicationCollection applications;

        public ApplicationInstancesTableModel(final Table table, final ApplicationCollection applications) {
            this.table = table;
            this.applications = applications;
        }

        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            if (applications.isAfterLast()) {
                applications.rewind();
            }
            return applications.next();
        }

        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case COL_TITLE:
                    return applications.getApplication().getTitle();
                case COL_DESC:
                    return applications.getApplication().getDescription();
                case COL_URL:
                    return applications.getApplication().getPath();
                default:
                    return null;
            }
        }

        public Object getKeyAt(final int columnIndex) {
            if (SessionManager.getSession().getTransactionContext().inTxn()) {
                SessionManager.getSession().getTransactionContext().commitTxn();
            }
            return applications.getApplication().getPath();
        }

    }
}
