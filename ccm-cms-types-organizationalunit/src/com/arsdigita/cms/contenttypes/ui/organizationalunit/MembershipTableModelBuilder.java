package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Membership;
import com.arsdigita.cms.contenttypes.OrganizationalUnit;
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
public class MembershipTableModelBuilder extends LockableImpl implements TableModelBuilder {

    private final static Logger logger = Logger.getLogger(MembershipTableModelBuilder.class);

    private ItemSelectionModel m_itemModel;

    public MembershipTableModelBuilder(ItemSelectionModel itemModel) {
        this.m_itemModel = itemModel;
    }

    public TableModel makeModel(Table t, PageState s) {
        DataCollection members = getMembers(s);

        if(members.isEmpty()) {
            return Table.EMPTY_MODEL;
        } else {
            return new MembershipTableModel(members);
        }
    }

    public DataCollection getMembers(PageState state) {
        Assert.isTrue(this.m_itemModel.isSelected(state), "item selected");
        OrganizationalUnit unit = (OrganizationalUnit) this.m_itemModel.getSelectedItem(state);
        return Membership.getMemberships(unit);
        //return unit.getMemberships();
    }

    public static class MembershipTableModel implements TableModel {

        Membership m_membership;
        DataCollection m_memberships;

        public MembershipTableModel(DataCollection memberships) {
            m_memberships = memberships;
            m_membership = null;
        }

        public int getColumnCount() {
            return (int) m_memberships.size();
        }

        public boolean nextRow() {
            if (m_memberships.next()) {
                DataObject obj = m_memberships.getDataObject();
                m_membership = (Membership) DomainObjectFactory.newInstance(obj);
                //m_membership = (Membership) m_memberships.getDataObject();
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
