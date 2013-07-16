
/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundleCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PersonPublicationsTable extends Table {

    private final static String TABLE_COL_EDIT = "table_col_edit";
    private final static String TABLE_COL_YEAR = "table_col_year";
    private final static String TABLE_COL_TYPE = "table_col_type";
    //private final String TABLE_COL_DEL = "table_col_del";
    //private ItemSelectionModel itemModel;

    public PersonPublicationsTable(final ItemSelectionModel itemModel) {
        this(itemModel, false);
    }

    public PersonPublicationsTable(final ItemSelectionModel itemModel, final boolean useAlias) {
        super();

        //this.itemModel = itemModel;

        //setEmptyView(new Label(PublicationGlobalizationUtil.globalize("person.ui.publications.none")));
        setEmptyView(new Label(new PrintListener() {
            @Override
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();
                final Label target = (Label) event.getTarget();
                
                if (useAlias) {
                    final GenericPerson person = (GenericPerson) itemModel.getSelectedItem(state);
                    final GenericPerson alias = person.getAlias();
                    
                    if (alias == null) {
                        target.setLabel("");
                    } else {
                        target.setLabel(PublicationGlobalizationUtil.globalize("person.ui.publications.none"));
                    }
                    
                } else {
                    target.setLabel(PublicationGlobalizationUtil.globalize("person.ui.publications.none"));
                }
            }

        }));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                PublicationGlobalizationUtil.globalize("person.ui.publications.columns.name").localize(),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                PublicationGlobalizationUtil.globalize("person.ui.publications.columns.year").localize(),
                TABLE_COL_YEAR));
        columnModel.add(new TableColumn(
                2,
                PublicationGlobalizationUtil.globalize("person.ui.publications.columns.type").localize(),
                TABLE_COL_TYPE));

        setModelBuilder(new ModelBuilder(itemModel, useAlias));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());

    }

    private class ModelBuilder extends LockableImpl implements TableModelBuilder {

        private final ItemSelectionModel itemModel;
        private final boolean useAlias;

        public ModelBuilder(final ItemSelectionModel itemModel) {
            this(itemModel, false);
        }

        public ModelBuilder(final ItemSelectionModel itemModel, final boolean useAlias) {
            this.itemModel = itemModel;
            this.useAlias = useAlias;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            final GenericPerson person;
            if (useAlias && ((GenericPerson) itemModel.getSelectedItem(state)).getAlias() == null) {
                person = null;
            } else if (useAlias && ((GenericPerson) itemModel.getSelectedItem(state)).getAlias() != null) {
                person = ((GenericPerson) itemModel.getSelectedItem(state)).getAlias();
            } else {
                person = (GenericPerson) itemModel.getSelectedItem(state);
            }

            return new Model(table, state, person);
        }

    }

    private class Model implements TableModel {

        private final Table table;
        private final PublicationBundleCollection publications;

        public Model(final Table table, final PageState state, final GenericPerson person) {
            this.table = table;
            if (person == null) {
                publications = null;
            } else {
                publications = Publication.getPublications(person);
            }
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((publications != null) && publications.next()) {
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
                    return publications.getPublication().getTitle();
                case 1:
                    return publications.getPublication().getYearOfPublication();
                case 2:
                    return ((DataObject) publications.getPublication().get("type")).get("label");
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return publications.getPublication().getID();
        }

    }

    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final Publication publication = new Publication((BigDecimal) key);

//            final boolean canEdit = securityManager.canAccess(state.getRequest(),
//                                                              com.arsdigita.cms.SecurityManager.EDIT_ITEM,
//                                                              publication);

            //if (canEdit) {
            final ContentSection section = publication.getContentSection();//CMS.getContext().getContentSection();
            final ItemResolver resolver = section.getItemResolver();
            final Link link = new Link(value.toString(),
                                       resolver.generateItemURL(state,
                                                                publication,
                                                                section,
                                                                publication.getVersion()));
            return link;
//            } else {
//                final Label label = new Label(value.toString());
//                return label;
//            }

        }

    }
}
