package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.ExternalLink;
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
import com.arsdigita.cms.contenttypes.OrgaUnit2OrgaUnit;
import com.arsdigita.cms.dispatcher.Utilities;
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
public class OrgaUnit2OrgaUnitTable extends Table {

    private final static Logger logger = Logger.getLogger(OrgaUnit2OrgaUnitTable.class);
    private OrgaUnit2OrgaUnitSelectionModel m_ou2ouModel;
    private ItemSelectionModel m_itemModel;
    private TableColumn m_orgaUnitCol;
    private TableColumn m_moveUpCol;
    private TableColumn m_moveDownCol;
    private TableColumn m_editCol;
    private TableColumn m_delCol;
    private RequestLocal m_size;
    private RequestLocal m_editor;
    /**
     * ID String for the "edit" event.
     */
    protected final static String EDIT_EVENT = "Edit";
    /**
     * ID for the "delete" event.
     */
    protected final static String DELETE_EVENT = "Delete";
    /**
     * ID fpr the "up" event.
     */
    protected final static String UP_EVENT = "up";
    /**
     * ID for the "down" event.
     */
    protected final static String DOWN_EVENT = "down";

    public OrgaUnit2OrgaUnitTable(ItemSelectionModel itemModel, OrgaUnit2OrgaUnitSelectionModel ou2ouModel) {
        super();
        this.m_itemModel = itemModel;
        this.m_ou2ouModel = ou2ouModel;
        addColumns();

        this.m_size = new RequestLocal();
        this.m_editor = new RequestLocal() {

            @Override
            public Object initialValue(PageState state) {
                SecurityManager sm = Utilities.getSecurityManager(state);
                ContentItem item = m_itemModel.getSelectedItem(state);
                Boolean val = new Boolean(sm.canAccess(state.getRequest(), SecurityManager.EDIT_ITEM, item));
                return val;
            }
        };

        Label empty = new Label("There are no organizational units associated with this organizational unit.");
        setEmptyView(empty);
        addTableActionListener(new OrgaUnit2OrgaUnitTableActionListener());
        setRowSelectionModel(this.m_ou2ouModel);
        setDefaultCellRenderer(new OrgaUnit2OrgaUnitTableRenderer());
        setModelBuilder(new OrgaUnit2OrgaUnitTableModelBuilder(itemModel));
    }

    public void addColumns() {
        TableColumnModel model = getColumnModel();
        int i = 0;
        this.m_orgaUnitCol = new TableColumn(i, "Organizational Unit");
        this.m_editCol = new TableColumn(++i, "Edit");
        this.m_delCol = new TableColumn(++i, "Delete");
        this.m_moveUpCol = new TableColumn(++i, "");
        this.m_moveDownCol = new TableColumn(++i, "");

        model.add(this.m_orgaUnitCol);
        model.add(this.m_editCol);
        model.add(this.m_delCol);
        model.add(this.m_moveUpCol);
        model.add(this.m_moveDownCol);
        setColumnModel(model);
    }

    private class OrgaUnit2OrgaUnitTableRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
            OrgaUnit2OrgaUnit ou2ou = (OrgaUnit2OrgaUnit) value;
            boolean isFirst = (row == 0);
            if (m_size.get(state) == null) {
                m_size.set(state, new Long(((OrgaUnit2OrgaUnitTableModelBuilder.OrgaUnit2OrgaUnitTableModel) table.getTableModel(state)).size()));
            }
            boolean isLast = (row == ((Long) m_size.get(state)).intValue() - 1);

            String url = ou2ou.getURI(state);
            if (column == m_orgaUnitCol.getModelIndex()) {
                ExternalLink extLink = new ExternalLink(ou2ou.getTargetItem().getOrganizationalUnitName(), url);
                return extLink;
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
            } else if (column == m_moveUpCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state)) && !isFirst) {
                    Label downLabel = new Label(UP_EVENT);
                    downLabel.setClassAttr("linkSort");
                    return new ControlLink(downLabel);
                } else {
                    return new Label("");
                }
            } else if (column == m_moveDownCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state)) && !isLast) {
                    Label downLabel = new Label(DOWN_EVENT);
                    downLabel.setClassAttr("linkSort");
                    return new ControlLink(downLabel);
                } else {
                    return new Label("");
                }
            } else {
                throw new UncheckedWrapperException("column out of bounds");
            }
        }
    }

    private class OrgaUnit2OrgaUnitTableActionListener implements TableActionListener {

        private OrgaUnit2OrgaUnit getOrgaUnit2OrgaUnit(TableActionEvent event) {
            Object o = event.getRowKey();
            BigDecimal id;
            if (o instanceof String) {
                id = new BigDecimal((String) o);
            } else {
                id = (BigDecimal) event.getRowKey();
            }

            Assert.exists(id);
            OrgaUnit2OrgaUnit ou2ou;
            try {
                ou2ou = (OrgaUnit2OrgaUnit) DomainObjectFactory.newInstance(new OID(OrgaUnit2OrgaUnit.BASE_DATA_OBJECT_TYPE, id));
            } catch (Exception ex) {
                throw new UncheckedWrapperException(ex);
            }
            return ou2ou;
        }

        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();
            PageState state = e.getPageState();
            OrgaUnit2OrgaUnit ou2ou = getOrgaUnit2OrgaUnit(e);
            Assert.exists(ou2ou);

            if (col == m_editCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    m_ou2ouModel.setSelectedObject(state, ou2ou);
                }
            } else if (col == m_delCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    try {
                        m_ou2ouModel.clearSelection(state);
                        ou2ou.delete();
                    } catch (Exception ex) {
                        throw new UncheckedWrapperException(ex);
                    }
                }
            } else if (col == m_moveUpCol.getModelIndex()) {
                m_ou2ouModel.clearSelection(state);
                ou2ou.swapWithPrevious();
            } else if (col == m_moveDownCol.getModelIndex()) {
                m_ou2ouModel.clearSelection(state);
                ou2ou.swapWithNext();
            }
        }

        public void headSelected(TableActionEvent e) {
        }
    }
}
