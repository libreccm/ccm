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
import com.arsdigita.cms.contentassets.PublicationCollection;
import com.arsdigita.cms.contentassets.SciPublicationsAboutGlobalizationUtil;
import com.arsdigita.cms.contentassets.SciPublicationsAboutService;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsAboutDiscussingTable extends Table implements TableActionListener {

    private static final String TABLE_COL_DEL = "table_col_del";
    private final ItemSelectionModel itemModel;

    public SciPublicationsAboutDiscussingTable(final ItemSelectionModel itemModel) {

        super();

        this.itemModel = itemModel;

        setEmptyView(new Label(SciPublicationsAboutGlobalizationUtil.globalize(
            "com.arsdigita.cms.contentassets.about.discussing.none")));

        final TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
            0,
            SciPublicationsAboutGlobalizationUtil.globalize(
            "com.arsdigita.cms.contentassets.about.discussing.publication")));
        colModel.add(new TableColumn(
            1,
            SciPublicationsAboutGlobalizationUtil.globalize(
            "com.arsdigita.cms.contentassets.about.discussing.publication.remove"),
            TABLE_COL_DEL));

        setModelBuilder(new SciPublicationsAboutTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new PublicationCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class SciPublicationsAboutTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        private final ItemSelectionModel itemModel;

        public SciPublicationsAboutTableModelBuilder(final ItemSelectionModel itemModel) {
            super();
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            final Publication publication = (Publication) itemModel.getSelectedObject(state);
            return new SciPublicationsAboutTableModel(table, publication);
        }

    }

    private class SciPublicationsAboutTableModel implements TableModel {

        private final Table table;
        private final PublicationCollection discussingPublications;
        private Publication discussing;

        public SciPublicationsAboutTableModel(final Table table,
                                              final Publication publication) {
            this.table = table;

            final SciPublicationsAboutService service = new SciPublicationsAboutService();

            discussingPublications = service.getDiscussingPublications(publication);
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if (discussingPublications != null && discussingPublications.next()) {
                discussing = discussingPublications.getPublication();
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
                    return discussing.getTitle();
                case 1:
                    return SciPublicationsAboutGlobalizationUtil.globalize(
                        "com.arsdigita.cms.contentassets.about.discussing.publication.remove");
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return discussing.getID();
        }
    }
    
    private class PublicationCellRenderer extends LockableImpl implements TableCellRenderer {
        
        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final Publication discussed = (Publication) itemModel.getSelectedObject(state);

            final boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                discussed);

            if (canEdit) {
                final Publication discussing;
                try {
                    discussing = new Publication((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                final ContentSection section = discussing.getContentSection();
                final ItemResolver resolver = section.getItemResolver();
                final Link link = new Link(value.toString(),
                                           resolver.generateItemURL(state,
                                                                    discussing,
                                                                    section,
                                                                    discussing.getVersion()));
                return link;
            } else {
                return new Label(value.toString());
            }
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
            final Publication discussed = (Publication) itemModel.getSelectedObject(state);
            
            final boolean canEdit = securityManager.canAccess(
            state.getRequest(),
                com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                discussed);
            
            if (canEdit) {
                final ControlLink link = new ControlLink(new Label((GlobalizedMessage) value));
                link.setConfirmation(SciPublicationsAboutGlobalizationUtil.globalize(
                    "com.arsdigita.cms.contentassets.about.discussing.publication.remove.confirm"));
                return link;
            } else {
                return new Label(value.toString());
            }
        }

    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        
        final PageState state = event.getPageState();
        
        final Publication discussing = new Publication(new BigDecimal(event.getRowKey().toString()));
        final Publication discussed = (Publication) itemModel.getSelectedObject(state);
        final SciPublicationsAboutService service = new SciPublicationsAboutService();
        
        final TableColumn column = getColumn(event.getColumn().intValue());
        
        if (TABLE_COL_DEL.equals(column.getHeaderKey())) {
            service.removeDiscussingPublication(discussed, discussing);
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing to do
    }

}
