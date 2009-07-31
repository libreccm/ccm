package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.contenttypes.*;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import org.apache.log4j.Logger;


/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ResearchNetworkMembershipTableModelBuilder extends LockableImpl implements TableModelBuilder {

    private final static Logger s_log = Logger.getLogger(ResearchNetworkMembershipTableModelBuilder.class);

    private ItemSelectionModel m_itemModel;

    public ResearchNetworkMembershipTableModelBuilder(ItemSelectionModel itemModel) {
        this.m_itemModel = itemModel;
    }

    public TableModel makeModel(Table t, PageState s) {
        DataCollection members = getMembers(s);

        if(members.isEmpty()) {
            return Table.EMPTY_MODEL;
        } else {
            return new ResearchNetworkMembershipTableModel(members);
        }
    }

    public DataCollection getMembers(PageState state) {
        Assert.isTrue(this.m_itemModel.isSelected(state), "item selected");
        ResearchNetwork network = (ResearchNetwork) this.m_itemModel.getSelectedItem(state);
        return ResearchNetworkMembership.getMemberships(network);
    }

    public static class ResearchNetworkMembershipTableModel implements TableModel {

        ResearchNetworkMembership m_membership;
        DataCollection m_memberships;

        public ResearchNetworkMembershipTableModel(DataCollection memberships) {
            m_memberships = memberships;
            m_membership = null;
        }

        public int getColumnCount() {
            return (int) m_memberships.size();
        }

        public boolean nextRow() {
            if (m_memberships.next()) {
                DataObject obj = m_memberships.getDataObject();
                m_membership = (ResearchNetworkMembership) DomainObjectFactory.newInstance(obj);
                return true;
            } else {
                return false;
            }
        }

        public Object getElementAt(int columnIndex) {
            return m_membership;
        }

        public Object getKeyAt(int columnIndex) {
            return m_membership.getID();
        }

        public long size() {
            return m_memberships.size();
        }
    }
}
