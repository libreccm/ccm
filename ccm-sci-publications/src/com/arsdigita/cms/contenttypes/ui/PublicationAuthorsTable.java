package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.AuthorshipCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationAuthorsTable
        extends Table
        implements TableActionListener {

    private static final Logger s_log =
                                Logger.getLogger(PublicationAuthorsTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;

    public PublicationAuthorsTable(ItemSelectionModel itemModel) {
        super();
        m_itemModel = itemModel;
        
        setEmptyView(
                new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.none")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.author.name").localize(),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.author.isEditor").localize()));
        colModel.add(new TableColumn(
                2,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.author.delete").localize(),
                TABLE_COL_DEL));
        colModel.add(new TableColumn(
                3,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.author.up").localize(),
                TABLE_COL_UP));
        colModel.add(new TableColumn(
                4,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.author.down").localize(),
                TABLE_COL_DOWN));

        setModelBuilder(
                new PublicationAuthorsTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());        
        colModel.get(2).setCellRenderer(new DeleteCellRenderer());
        colModel.get(3).setCellRenderer(new UpCellRenderer());
        colModel.get(4).setCellRenderer(new DownCellRenderer());

        s_log.info("Adding table action listener...");
        addTableActionListener(this);
    }

    private class PublicationAuthorsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public PublicationAuthorsTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            Publication publication =
                        (Publication) m_itemModel.getSelectedObject(state);
            return new PublicationAuthorsTableModel(table, state, publication);
        }
    }

    private class PublicationAuthorsTableModel implements TableModel {

        private final int MAX_DESC_LENGTH = 25;
        private Table m_table;
        private AuthorshipCollection m_authorshipCollection;
        private GenericPerson m_author;

        private PublicationAuthorsTableModel(
                Table table,
                PageState state,
                Publication publication) {
            m_table = table;
            m_authorshipCollection = publication.getAuthors();
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((m_authorshipCollection != null)
                && m_authorshipCollection.next()) {
                m_author = m_authorshipCollection.getAuthor();
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return m_author.getFullName();
                case 1:
                    if (m_authorshipCollection.isEditor()) {
                        return (String) PublicationGlobalizationUtil.globalize(
                                "publications.ui.authors.author.is_editor").
                                localize();
                    } else {
                        return PublicationGlobalizationUtil.globalize(
                                "publications.ui.authors.author.is_not_editor").
                                localize();
                    }
                case 2:
                    return PublicationGlobalizationUtil.globalize(
                            "publications.ui.authors.author.remove").
                            localize();
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return m_author.getID();
        }
    }

    private class EditCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            Publication publication = (Publication) m_itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.EDIT_ITEM,
                    publication);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    private class DeleteCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {

            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            Publication publication = (Publication) m_itemModel.
                    getSelectedObject(state);

            boolean canDelete = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    publication);

            if (canDelete) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) PublicationGlobalizationUtil.
                        globalize(
                        "publications.ui.authors.author.confirm_remove").
                        localize());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    private class UpCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {

            if (0 == row) {
                s_log.debug("Row is first row in table, don't show up link");
                Label label = new Label("");
                return label;
            } else {
                ControlLink link = new ControlLink("up");
                return link;
            }
        }
    }

    private class DownCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {

            Publication publication = (Publication) m_itemModel.
                    getSelectedObject(state);
            AuthorshipCollection authors = publication.getAuthors();

            if ((authors.size() - 1)
                == row) {
                s_log.debug("Row is last row in table, don't show down link");
                Label label = new Label("");
                return label;
            } else {
                ControlLink link = new ControlLink("down");
                return link;
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        s_log.info("cellSelected!");

        GenericPerson author =
                      new GenericPerson(new BigDecimal(event.getRowKey().
                toString()));

        Publication publication = (Publication) m_itemModel.getSelectedObject(
                state);

        AuthorshipCollection authors = publication.getAuthors();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            publication.removeAuthor(author);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            s_log.info("UP");
            authors.swapWithPrevious(author);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            s_log.info("DOWN");
            authors.swapWithNext(author);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do here.
    }
}
