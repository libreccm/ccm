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
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.Member;
import com.arsdigita.cms.contenttypes.ResearchNetworkGlobalizationUtil;
import com.arsdigita.cms.contenttypes.ResearchNetworkMembership;
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
public class ResearchNetworkMembershipTable extends Table {

    private final static Logger s_log = Logger.getLogger(ResearchNetworkMembershipTable.class);
    private ResearchNetworkMembershipSelectionModel m_membershipModel;
    private ItemSelectionModel m_itemModel;
    private TableColumn m_personCol;
    private TableColumn m_editCol;
    private TableColumn m_delCol;
    private RequestLocal m_size;
    private RequestLocal m_editor;
    protected final static String EDIT_EVENT = "Edit";
    protected final static String DELETE_EVENT = "Delete";

    public ResearchNetworkMembershipTable(ItemSelectionModel itemModel, ResearchNetworkMembershipSelectionModel membershipModel) {
        super();
        this.m_itemModel = itemModel;
        this.m_membershipModel = membershipModel;
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

        Label empty = new Label(ResearchNetworkGlobalizationUtil.globalize("cms.contenttypes.ui.researchnetwork.membership.noMembers"));
        setEmptyView(empty);
        addTableActionListener(new ResearchNetworkMembershipTableActionListener());
        setRowSelectionModel(m_membershipModel);
        setDefaultCellRenderer(new ResearchNetworkMembershipTableCellRenderer());
        setModelBuilder(new ResearchNetworkMembershipTableModelBuilder(itemModel));
    }

    public void addColumns() {
        TableColumnModel model = getColumnModel();
        this.m_personCol = new TableColumn(0, ResearchNetworkGlobalizationUtil.globalize("cms.contenttypes.ui.researchnetwork.membership.person"));
        this.m_editCol = new TableColumn(1, ResearchNetworkGlobalizationUtil.globalize("cms.contenttypes.ui.researchnetwork.membership.edit"));
        this.m_delCol = new TableColumn(2, ResearchNetworkGlobalizationUtil.globalize("cms.contenttypes.ui.researchnetwork.membership.delete"));

        model.add(this.m_personCol);
        model.add(this.m_editCol);
        model.add(this.m_delCol);
        setColumnModel(model);
    }

    private class ResearchNetworkMembershipTableCellRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
            ResearchNetworkMembership membership = (ResearchNetworkMembership) value;
            if (m_size.get(state) == null) {
                m_size.set(state, new Long(((ResearchNetworkMembershipTableModelBuilder.ResearchNetworkMembershipTableModel) table.getTableModel(state)).size()));
            }

            String url = membership.getURI(state);
            if (column == m_personCol.getModelIndex()) {
                Member person = membership.getTargetItem();
                StringBuilder fullNameBuilder = new StringBuilder();

                if (person.getTitlePre() != null) {
                    fullNameBuilder.append(person.getTitlePre());
                    fullNameBuilder.append(" ");
                }
                if (person.getGivenName() != null) {
                    fullNameBuilder.append(person.getGivenName());
                    fullNameBuilder.append(" ");
                }
                if (person.getSurname() != null) {
                    fullNameBuilder.append(person.getSurname());
                    fullNameBuilder.append(" ");
                }
                if (person.getTitlePost() != null) {
                    fullNameBuilder.append(person.getTitlePost());
                }

                String fullName = fullNameBuilder.toString();
                ExternalLink extLink = new ExternalLink(fullName, url);
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
            } else {
                throw new UncheckedWrapperException("column out of bounds");
            }
        }
    }

    private class ResearchNetworkMembershipTableActionListener implements TableActionListener {

        private ResearchNetworkMembership getMembership(TableActionEvent event) {
            Object obj = event.getRowKey();
            BigDecimal id;
            if (obj instanceof String) {
                id = new BigDecimal((String) obj);
            } else {
                id = (BigDecimal) obj;
            }

            Assert.exists(id);
            ResearchNetworkMembership membership;
            try {
                membership = (ResearchNetworkMembership) DomainObjectFactory.newInstance(new OID(ResearchNetworkMembership.BASE_DATA_OBJECT_TYPE, id));
            } catch(DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException(ex);
            }
            return membership;
        }

        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();
            PageState state = e.getPageState();
            ResearchNetworkMembership membership = getMembership(e);
            Assert.exists(membership);

            if (col == m_editCol.getModelIndex()) {
                if(Boolean.TRUE.equals(m_editor.get(state))) {
                    m_membershipModel.setSelectedObject(state, membership);
                }
            } else if(col == m_delCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    try {
                        m_membershipModel.clearSelection(state);
                        membership.delete();
                    } catch(Exception ex) {
                        throw new UncheckedWrapperException(ex);
                    }
                }
            }
        }

        public void headSelected(TableActionEvent e) {            
        }
        
    }
}
