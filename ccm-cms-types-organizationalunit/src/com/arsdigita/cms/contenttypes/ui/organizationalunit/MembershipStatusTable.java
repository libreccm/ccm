package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.MembershipStatus;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class MembershipStatusTable extends Table {

    private final static Logger logger = Logger.getLogger(MembershipStatusTable.class);
    private MembershipStatusSelectionModel m_statusModel;
    private ItemSelectionModel m_itemModel;
    private TableColumn m_statusNameCol;
    private TableColumn m_editCol;
    private TableColumn m_delCol;
    private RequestLocal m_size;
    private RequestLocal m_editor;
    protected final static String EDIT_EVENT = "Edit";
    protected final static String DELETE_EVENT = "Delete";

    public MembershipStatusTable(ItemSelectionModel itemModel, MembershipStatusSelectionModel statusModel) {
        super();
        this.m_itemModel = itemModel;
        this.m_statusModel = statusModel;
        addColumns();

        m_size = new RequestLocal();
        m_editor = new RequestLocal() {

            @Override
            public Object initialValue(PageState state) {
                SecurityManager sm = Utilities.getSecurityManager(state);
                ContentItem item = m_itemModel.getSelectedItem(state);
                Boolean val = new Boolean(sm.canAccess(state.getRequest(), SecurityManager.EDIT_ITEM, item));
                return val;
            }
        };

        Label empty = new Label("There are no status.");
        setEmptyView(empty);
        addTableActionListener(new MembershipStatusTableActionListener());
        setRowSelectionModel(this.m_statusModel);
        setDefaultCellRenderer(new MembershipStatusTableRenderer());
        setModelBuilder(new MembershipStatusTableModelBuilder(itemModel));
    }

    protected void addColumns() {
        TableColumnModel model = getColumnModel();
        int i = 0;
        this.m_statusNameCol = new TableColumn(i, "Status name");
        this.m_editCol = new TableColumn(++i, "Edit");
        this.m_delCol = new TableColumn(++i, "Delete");

        model.add(this.m_statusNameCol);
        model.add(this.m_editCol);
        model.add(this.m_delCol);
        setColumnModel(model);
    }

    private class MembershipStatusTableRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {

            MembershipStatus status = (MembershipStatus) value;
            boolean isFirst = (row == 0);
            if (m_size.get(state) == null) {
                m_size.set(state, new Long(((MembershipStatusTableModelBuilder.MembershipStatusTableModel) table.getTableModel(state)).size()));
            }
            boolean isLast = (row == ((Long) m_size.get(state)).intValue() - 1);

            if (column == m_statusNameCol.getModelIndex()) {
                Label statusNameLabel = new Label(status.getStatusName());
                return statusNameLabel;
            } else if (column == m_editCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    if (isSelected) {
                        return new Label(EDIT_EVENT, Label.BOLD);
                    } else {
                        return new ControlLink(EDIT_EVENT);
                    }
                } else {
                    return new Label(EDIT_EVENT);
                }
            } else if (column == m_delCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    return new ControlLink(DELETE_EVENT);
                } else {
                    return new Label(DELETE_EVENT);
                }
            } else {
                throw new UncheckedWrapperException("column index out of bounds");
            }
        }
    }

    private class MembershipStatusTableActionListener implements TableActionListener {

        private MembershipStatus getMembershipStatus(TableActionEvent e) {
            Object o = e.getRowKey();
            BigDecimal id;
            if (o instanceof String) {
                id = new BigDecimal((String) o);
            } else {
                id = (BigDecimal) e.getRowKey();
            }

            Assert.exists(id);
            MembershipStatus status;
            try {
                status = (MembershipStatus) DomainObjectFactory.newInstance(new OID(MembershipStatus.BASE_DATA_OBJECT_TYPE, id));
            } catch (DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException(ex);
            }
            return status;
        }

        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();
            PageState state = e.getPageState();
            MembershipStatus status = getMembershipStatus(e);
            Assert.exists(status);

            if (col == m_editCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    m_statusModel.setSelectedObject(state, status);
                }
            } else if (col == m_delCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    try {
                        m_statusModel.clearSelection(state);
                        status.delete();
                    } catch (Exception ex) {
                        throw new UncheckedWrapperException(ex);
                    }
                }
            }
        }

        public void headSelected(TableActionEvent e) {
            //Nothing
        }
    }
}
