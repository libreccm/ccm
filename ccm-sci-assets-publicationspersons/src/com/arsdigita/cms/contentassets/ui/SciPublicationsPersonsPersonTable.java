/*
 * Copyright (c) 2014 Jens Pelzetter
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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.RelationAttributeResourceBundleControl;
import com.arsdigita.cms.contentassets.SciPublicationsPersonsPersonCollection;
import com.arsdigita.cms.contentassets.SciPublicationsPersonsService;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPersonsPersonTable extends Table implements TableActionListener {

    private static final String TABLE_COL_DEL = "table_col_del";
    private final ItemSelectionModel itemModel;
    private final SciPublicationsPersonsGlobalisationUtil globalisationUtil
                                                              = new SciPublicationsPersonsGlobalisationUtil();

    public SciPublicationsPersonsPersonTable(final ItemSelectionModel itemModel) {

        super();

        this.itemModel = itemModel;

        setEmptyView(new Label(globalisationUtil.globalize(
            "com.arsdigita.cms.contentassets.publications_persons.persons.none")));

        final TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
            0,
            globalisationUtil.globalize(
                "com.arsdigita.cms.contentassets.publications_persons.person")));
        colModel.add(new TableColumn(
            1,
            globalisationUtil.globalize(
                "com.arsdigita.cms.contentassets.publications_persons.person.relation")));
        colModel.add(new TableColumn(
            2,
            globalisationUtil.globalize(
                "com.arsdigita.cms.contentassets.publications_persons.person.remove"),
            TABLE_COL_DEL));

        setModelBuilder(new SciPublicationsPersonsTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new PersonNameCellRenderer());
        colModel.get(1).setCellRenderer(new PersonRelationCellRenderer());
        colModel.get(2).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);

    }

    private class SciPublicationsPersonsTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        private final ItemSelectionModel itemModel;

        public SciPublicationsPersonsTableModelBuilder(final ItemSelectionModel itemModel) {

            super();
            this.itemModel = itemModel;

        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {

            table.getRowSelectionModel().clearSelection(state);
            final Publication publication = (Publication) itemModel.getSelectedObject(state);
            return new SciPublicationsPersonsPersonTableModel(table, publication);

        }

    }

    private class SciPublicationsPersonsPersonTableModel implements TableModel {

        private final Table table;
        private final SciPublicationsPersonsPersonCollection persons;
        private GenericPerson person;
        private String relation;

        public SciPublicationsPersonsPersonTableModel(final Table table,
                                                      final Publication publication) {

            this.table = table;

            final SciPublicationsPersonsService service = new SciPublicationsPersonsService();
            persons = service.getPersons(publication);

        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {

            boolean ret;

            if ((persons != null) && persons.next()) {
                person = persons.getPerson();
                relation = persons.getRelation();
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
                    return person.getFullName();
                case 1:
                    return relation;
                case 2:
                    return globalisationUtil.globalize(
                        "com.arsdigita.cms.contentassets.publication_persons.person.remove");
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return person.getID();
        }

    }

    private class PersonNameCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final Publication publication = (Publication) itemModel.getSelectedObject(state);

            final boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                publication);

            if (canEdit) {

                final GenericPerson person;
                try {
                    person = new GenericPerson((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                final ContentSection section = person.getContentSection();
                final ItemResolver resolver = section.getItemResolver();
                final Link link = new Link(value.toString(),
                                           resolver.generateItemURL(state,
                                                                    person,
                                                                    section,
                                                                    person.getVersion()));
                return link;

            } else {
                return new Label(value.toString());
            }
        }

    }

    private class PersonRelationCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            final GlobalizedMessage relation = new GlobalizedMessage((String) value, 
            SciPublicationsPersonsService.RELATION_ATTRIBUTE,
            new RelationAttributeResourceBundleControl());
            
            return new Label(relation);
            
//            final String relation = (String) value;
//
//            final RelationAttributeCollection relations = new RelationAttributeCollection(
//                SciPublicationsPersonsService.RELATION_ATTRIBUTE,
//                relation);
//            relations.addLanguageFilter(GlobalizationHelper.getNegotiatedLocale().getLanguage());
//            if (relations.isEmpty()) {
//                return new Label(relation);
//            } else {
//                relations.next();
//                final String label = relations.getName();
//                relations.close();
//                return new Label(label);
//            }

            //return new Label(value.toString());
        }

    }

    private class DeleteCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final Publication publication = (Publication) itemModel.getSelectedObject(state);

            final boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                publication);

            if (canEdit) {
                final ControlLink link = new ControlLink(new Label((GlobalizedMessage) value));
                link.setConfirmation(globalisationUtil.globalize(
                    "com.arsdigita.cms.contentassets.publications_persons.person.remove.confirm"));
                return link;
            } else {
                return new Label((GlobalizedMessage) value);
            }
        }

    }

    @Override
    public void cellSelected(final TableActionEvent event) {

        final PageState state = event.getPageState();

        final GenericPerson person = new GenericPerson(new BigDecimal(event.getRowKey().toString()));
        final Publication publication = (Publication) itemModel.getSelectedObject(state);
        final SciPublicationsPersonsService service = new SciPublicationsPersonsService();

        final TableColumn column = getColumnModel().get(event.getColumn().intValue());
        if (TABLE_COL_DEL.equals(column.getHeaderKey())) {
            service.removePublication(person, publication);
        }

    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing to do
    }

}
