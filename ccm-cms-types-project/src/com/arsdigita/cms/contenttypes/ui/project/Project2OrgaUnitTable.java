package com.arsdigita.cms.contenttypes.ui.project;

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
import com.arsdigita.cms.contenttypes.Project2OrgaUnit;
import com.arsdigita.cms.contenttypes.ProjectGlobalizationUtil;
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
public class Project2OrgaUnitTable extends Table {

    private final static Logger s_log =
            Logger.getLogger(Project2OrgaUnitTable.class);
    private Project2OrgaUnitSelectionModel m_p2ouModel;
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

    public Project2OrgaUnitTable(
            ItemSelectionModel itemModel,
            Project2OrgaUnitSelectionModel p2ouModel) {
        super();
        m_itemModel = itemModel;
        m_p2ouModel = p2ouModel;
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

        Label empty = new Label(ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project.noOrgaUnitsAssociated"));
        setEmptyView(empty);
        addTableActionListener(new Project2OrgaUnitTableActionListener());
        setRowSelectionModel(p2ouModel);
        setDefaultCellRenderer(new Project2OrgaUnitTableRenderer());
        setModelBuilder(new Project2OrgaUnitTableModelBuilder(itemModel));
    }

    /**
     * Adds the columns of the table.
     */
    public void addColumns() {
        TableColumnModel model = getColumnModel();

        m_orgaUnitCol = new TableColumn(0,
                ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project.orgaunit").localize());
        m_editCol = new TableColumn(1,
                ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project.edit").localize());
        m_delCol = new TableColumn(2,
                ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project_del").localize());
        m_moveUpCol = new TableColumn(3, "");
        m_moveDownCol = new TableColumn(4, "");

        model.add(m_orgaUnitCol);
        model.add(m_editCol);
        model.add(m_delCol);
        model.add(m_moveUpCol);
        model.add(m_moveDownCol);
        setColumnModel(model);
    }

    private class Project2OrgaUnitTableRenderer implements TableCellRenderer {

        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int column) {
            Project2OrgaUnit p2ou = (Project2OrgaUnit) value;
            boolean isFirst = (row == 0);
            if (m_size.get(state) == null) {
                m_size.set(state,
                        new Long(((Project2OrgaUnitTableModelBuilder.Project2OrgaUnitTableModel) table.getTableModel(state)).size()));
            }
            boolean isLast = (row == ((Long) m_size.get(state)).intValue() - 1);

            String url = p2ou.getURI(state);
            if (column == m_orgaUnitCol.getModelIndex()) {
                ExternalLink extLink =
                        new ExternalLink(p2ou.getTargetItem().getOrganizationalUnitName(), url);
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
                throw new UncheckedWrapperException("column out of bounds.");
            }
        }
    }

    private class Project2OrgaUnitTableActionListener
            implements TableActionListener {

        private Project2OrgaUnit getProject2OrgaUnit(TableActionEvent e) {
            Object o = e.getRowKey();
            BigDecimal id;
            if (o instanceof String) {
                id = new BigDecimal((String) o);
            } else {
                id = (BigDecimal) e.getRowKey();
            }

            Assert.exists(id);
            Project2OrgaUnit p2ou;
            try {
                p2ou = (Project2OrgaUnit) DomainObjectFactory.newInstance(
                        new OID(Project2OrgaUnit.BASE_DATA_OBJECT_TYPE, id));
            } catch(DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException(ex);
            }

            return p2ou;
        }

        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();
            PageState state = e.getPageState();
            Project2OrgaUnit p2ou = getProject2OrgaUnit(e);
            Assert.exists(p2ou);

            if(col == m_editCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    m_p2ouModel.setSelectedObject(state, p2ou);
                }
            } else if(col == m_delCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    try {
                        m_p2ouModel.clearSelection(state);
                        p2ou.delete();
                    } catch(Exception ex) {
                        throw new UncheckedWrapperException(ex);
                    }
                }
            } else if (col == m_moveUpCol.getModelIndex()) {
                m_p2ouModel.clearSelection(state);
                p2ou.swapWithPrevious();
            } else if (col == m_moveDownCol.getModelIndex()) {
                m_p2ouModel.clearSelection(state);
                p2ou.swapWithNext();
            }
        }

        public void headSelected(TableActionEvent e) {
            
        }
    }
}
