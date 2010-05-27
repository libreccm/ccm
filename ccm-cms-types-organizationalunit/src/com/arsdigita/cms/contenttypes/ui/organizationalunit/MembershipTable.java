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
import com.arsdigita.cms.SecurityManager;
import org.apache.log4j.Logger;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.basetypes.Person;
import com.arsdigita.cms.contenttypes.Membership;
import com.arsdigita.cms.contenttypes.MembershipStatus;
import com.arsdigita.cms.contenttypes.OrganizationalUnitGlobalizationUtil;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class MembershipTable extends Table {

    private final static Logger logger = Logger.getLogger(MembershipTable.class);
    private MembershipSelectionModel m_membershipModel;
    private ItemSelectionModel m_itemModel;
    private TableColumn m_personCol;
    private TableColumn m_statusCol;
    private TableColumn m_editCol;
    //private TableColumn m_delCol;
    private RequestLocal m_size;
    private RequestLocal m_editor;

    protected final static String EDIT_EVENT = "Edit";
    protected final static String DELETE_EVENT = "Delete";

    public MembershipTable(ItemSelectionModel itemModel, MembershipSelectionModel membershipModel) {
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

        Label empty = new Label(OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.nomembers"));
        setEmptyView(empty);
        addTableActionListener(new MembershipTableActionListener());
        setRowSelectionModel(m_membershipModel);
        setDefaultCellRenderer(new MembershipTableRenderer());
        setModelBuilder(new MembershipTableModelBuilder(itemModel));
    }

    public void addColumns() {
        TableColumnModel model = getColumnModel();
        int i = 0;
        this.m_personCol = new TableColumn(i, OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.person"));
        this.m_statusCol = new TableColumn(++i, OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.status"));
        this.m_editCol = new TableColumn(++i, OrganizationalUnitGlobalizationUtil.globalize("cms.contenttypes.ui.organizationalunit.edit"));

        model.add(this.m_personCol);
        model.add(this.m_statusCol);
        model.add(this.m_editCol);
        setColumnModel(model);
    }

    private class MembershipTableRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value, boolean isSelected, Object key, int row, int column) {
            Membership membership = (Membership) value;
            if (m_size.get(state) == null) {
                m_size.set(state, new Long(((MembershipTableModelBuilder.MembershipTableModel) table.getTableModel(state)).size()));
            }

            String url = membership.getURI(state);
            if (column == m_personCol.getModelIndex()) {
                Person person = membership.getTargetItem();
                StringBuilder fullNameBuilder = new StringBuilder();

                if(person.getTitlePre() != null) {
                    fullNameBuilder.append(person.getTitlePre());
                    fullNameBuilder.append(" ");
                }
                if(person.getGivenName() != null) {
                    fullNameBuilder.append(person.getGivenName());
                    fullNameBuilder.append(" ");
                }
                if(person.getSurname() != null) {
                    fullNameBuilder.append(person.getSurname());
                    fullNameBuilder.append(" ");
                }
                if(person.getTitlePost() != null) {
                    fullNameBuilder.append(person.getTitlePost());
                }

                //String fullName = String.format("%s %s %s, %s", person.getTitlePre(), person.getGivenName(), person.getSurname(), person.getTitlePost());
                String fullName = fullNameBuilder.toString();
                ExternalLink extLink = new ExternalLink(fullName, url);
                return extLink;
            } else if(column == m_statusCol.getModelIndex()) {
                MembershipStatus status = membership.getStatus();
                return new Label(status.getStatusName());
            } else if(column == m_editCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    if (isSelected) {
                        return new Label(EDIT_EVENT, Label.BOLD);
                    } else {
                        return new ControlLink(EDIT_EVENT);
                    }
                } else {
                    return new Label(EDIT_EVENT);
                }
            } else {
                throw new UncheckedWrapperException("column out of bounds");
            }
        }
    }

    private class MembershipTableActionListener implements TableActionListener {

        private Membership getMembership(TableActionEvent event) {
            Object obj = event.getRowKey();
            BigDecimal id;
            if (obj instanceof String) {
                id = new BigDecimal((String) obj);
            } else {
                id = (BigDecimal) obj;
            }

            Assert.exists(id);
            Membership membership;
            try {
                membership = (Membership) DomainObjectFactory.newInstance(new OID(Membership.BASE_DATA_OBJECT_TYPE, id));
            } catch (DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException(ex);
            }
            return membership;
        }

        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();
            PageState state = e.getPageState();
            Membership membership = getMembership(e);
            Assert.exists(membership);

            if (col == m_editCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    m_membershipModel.setSelectedObject(state, membership);
                }
            }
        }

        public void headSelected(TableActionEvent e) {            
        }

    }

}
