package com.arsdigita.cms.contenttypes.ui;

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
import com.arsdigita.cms.contenttypes.Orga2OrgaUnit;
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
public class Orga2OrgaUnitTable extends Table {

    private final static Logger logger = Logger.getLogger(Orga2OrgaUnitTable.class);
    private Orga2OrgaUnitSelectionModel m_o2ouModel;
    private ItemSelectionModel m_itemModel;
    private TableColumn m_orgaUnitCol;
    private TableColumn m_moveUpCol;
    private TableColumn m_moveDownCol;
    private TableColumn m_editCol;
    private TableColumn m_delCol;
    private RequestLocal m_size;
    private RequestLocal m_editor;
    protected final static String EDIT_EVENT = "Edit";
    protected final static String DELETE_EVENT = "Delete";
    protected final static String UP_EVENT = "up";
    protected final static String DOWN_EVENT = "down";

    public Orga2OrgaUnitTable(ItemSelectionModel itemModel, Orga2OrgaUnitSelectionModel o2ouModel) {
        super();
        this.m_itemModel = itemModel;
        this.m_o2ouModel = o2ouModel;
        addColumns();

        m_size = new RequestLocal();
        m_editor = new RequestLocal() {

            @Override
            public Object initialValue(PageState s) {
                SecurityManager sm = Utilities.getSecurityManager(s);
                ContentItem item = m_itemModel.getSelectedItem(s);
                Boolean val = new Boolean(sm.canAccess(s.getRequest(), SecurityManager.EDIT_ITEM, item));
                return val;
            }
        };

        Label empty = new Label("There are no organizational units for this organization.");
        setEmptyView(empty);
        addTableActionListener(new Orga2OrgaUnitTableActionListener());
        setRowSelectionModel(m_o2ouModel);
        setDefaultCellRenderer(new Orga2OrgaUnitTableRenderer());
        setModelBuilder(new Orga2OrgaUnitTableModelBuilder(itemModel));
    }

    public void addColumns() {
        TableColumnModel model = getColumnModel();
        int i = 0;
        this.m_orgaUnitCol = new TableColumn(i, "Organizational Unit");
        this.m_editCol = new TableColumn(++i, "Edit");
        model.add(this.m_delCol);
        model.add(this.m_moveUpCol);
        model.add(this.m_moveDownCol);
        setColumnModel(model);
    }

    private class Orga2OrgaUnitTableRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
            Orga2OrgaUnit o2ou = (Orga2OrgaUnit) value;
            boolean isFirst = (row == 0);
            if (m_size.get(state) == null) {
                m_size.set(state, new Long(((Orga2OrgaUnitTableModelBuilder.Orga2OrgaUnitTableModel) table.getTableModel(state)).size()));
            }
            boolean isLast = (row == ((Long) m_size.get(state)).intValue() - 1);

            String url = o2ou.getURI(state);
            if (column == m_orgaUnitCol.getModelIndex()) {
                ExternalLink extLink = new ExternalLink(o2ou.getTargetItem().getOrganizationalUnitName(), url);
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

    private class Orga2OrgaUnitTableActionListener implements TableActionListener {

        private Orga2OrgaUnit getOrga2OrgaUnit(TableActionEvent e) {
            Object o = e.getRowKey();
            BigDecimal id;
            if (o instanceof String) {
                id = new BigDecimal((String) o);
            } else {
                id = (BigDecimal) e.getRowKey();
            }

            Assert.exists(id);
            Orga2OrgaUnit o2ou;
            try {
                o2ou = (Orga2OrgaUnit) DomainObjectFactory.newInstance(new OID(Orga2OrgaUnit.BASE_DATA_OBJECT_TYPE, id));
            } catch (DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException(ex);
            }
            return o2ou;
        }

        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();
            PageState state = e.getPageState();
            Orga2OrgaUnit o2ou = getOrga2OrgaUnit(e);
            Assert.exists(o2ou);

            if (col == m_editCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    m_o2ouModel.setSelectedObject(state, o2ou);
                }
            } else if (col == m_delCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    try {
                        m_o2ouModel.clearSelection(state);
                        o2ou.delete();
                    } catch (Exception ex) {
                        throw new UncheckedWrapperException(ex);
                    }
                }
            } else if (col == m_moveUpCol.getModelIndex()) {
                m_o2ouModel.clearSelection(state);
                o2ou.swapWithPrevious();
            } else if (col == m_moveDownCol.getModelIndex()) {
                m_o2ouModel.clearSelection(state);
                o2ou.swapWithNext();
            }

        }

        public void headSelected(TableActionEvent e) {
        }
    }
}