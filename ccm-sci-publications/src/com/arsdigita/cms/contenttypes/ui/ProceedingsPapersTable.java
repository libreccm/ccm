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
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolumeCollection;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InProceedingsCollection;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class ProceedingsPapersTable
        extends Table
        implements TableActionListener {

    private final Logger s_log = Logger.getLogger(
            ProceedingsPapersTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;

    public ProceedingsPapersTable(ItemSelectionModel itemModel) {
        super();
        m_itemModel = itemModel;

        setEmptyView(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.procreedings.no_papers")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.paper").localize(),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.paper.remove").localize(),
                TABLE_COL_DEL));
        colModel.add(new TableColumn(
                2,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.procedings.paper.up").localize(),
                TABLE_COL_UP));
        colModel.add(new TableColumn(
                3,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.paper.down").localize(),
                TABLE_COL_DOWN));

        setModelBuilder(
                new ProceedingsPapersTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());
        colModel.get(2).setCellRenderer(new UpCellRenderer());
        colModel.get(3).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);
    }

    private class ProceedingsPapersTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public ProceedingsPapersTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            Proceedings proceedings =
                        (Proceedings) m_itemModel.getSelectedObject(state);
            return new ProceedingsPapersTableModel(table,
                                                   state,
                                                   proceedings);
        }
    }

    private class ProceedingsPapersTableModel implements TableModel {

        private Table m_table;
        private InProceedingsCollection m_papers;
        private InProceedings m_paper;

        private ProceedingsPapersTableModel(Table table,
                                            PageState state,
                                            Proceedings proceedings) {
            m_table = table;
            m_papers = proceedings.getPapers();
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((m_papers != null) && m_papers.next()) {
                m_paper = m_papers.getPaper();
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
                    return m_paper.getTitle();
                case 1:
                    return PublicationGlobalizationUtil.globalize(
                            "publications.ui.proceedings.paper.remove").
                            localize();
                case 2:
                    return PublicationGlobalizationUtil.globalize(
                            "publications.ui.proceedings.paper.up").
                            localize();
                case 3:
                    return PublicationGlobalizationUtil.globalize(
                            "publications.ui.proceedings.paper.down").
                            localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return m_paper.getID();
        }
    }

    private class EditCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int col) {
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            Proceedings proceedings = (Proceedings) m_itemModel.getSelectedItem(
                    state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.EDIT_ITEM,
                    proceedings);

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
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            Proceedings proceedings = (Proceedings) m_itemModel.getSelectedItem(
                    state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    proceedings);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) PublicationGlobalizationUtil.
                        globalize(
                        "publications.ui.proceedings.paper.confirm_remove").
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
        public Component getComponent(Table table,
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
                ControlLink link = new ControlLink(value.toString());
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

            CollectedVolume collectedVolume =
                            (CollectedVolume) m_itemModel.getSelectedObject(
                    state);
            ArticleInCollectedVolumeCollection articles =
                                               collectedVolume.getArticles();

            if ((articles.size() - 1)
                == row) {
                s_log.debug("Row is last row in table, don't show down link");
                Label label = new Label("");
                return label;
            } else {
                ControlLink link = new ControlLink(value.toString());
                return link;
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        InProceedings paper =
                      new InProceedings(
                new BigDecimal(event.getRowKey().toString()));

        Proceedings proceedings =
                (Proceedings) m_itemModel.getSelectedItem(state);

        InProceedingsCollection papers =
                proceedings.getPapers();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {

        } else if(column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            proceedings.removePaper(paper);
        } else if(column.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            papers.swapWithPrevious(paper);
        } else if(column.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            papers.swapWithNext(paper);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Noting to do
    }
}
