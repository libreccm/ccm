package com.arsdigita.cms.contenttypes.ui.organizationalunit;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.MembershipStatus;
import com.arsdigita.cms.contenttypes.MembershipStatusCollection;
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
public class MembershipStatusTableModelBuilder extends LockableImpl implements TableModelBuilder {

    private final static Logger logger = Logger.getLogger(MembershipStatusTableModelBuilder.class);
    private ItemSelectionModel m_itemModel;

    public MembershipStatusTableModelBuilder(ItemSelectionModel itemModel) {
        this.m_itemModel = itemModel;
    }

    public TableModel makeModel(Table t, PageState s) {
        DataCollection statusValues = getStatus(s);

        if (statusValues.isEmpty()) {
            return Table.EMPTY_MODEL;
        } else {
            return new MembershipStatusTableModel(statusValues);
        }
    }

    public DataCollection getStatus(PageState state) {
        //Assert.isTrue(this.m_itemModel.isSelected(state), "item selected");
        return (DataCollection) MembershipStatusCollection.getMembershipStatusCollection().getDomainObject();
    }

    public static class MembershipStatusTableModel implements TableModel {

        MembershipStatus m_status;
        DataCollection m_statusValues;

        public MembershipStatusTableModel(DataCollection statusValues) {
            m_statusValues = statusValues;
            m_status = null;
        }

        public int getColumnCount() {
            return (int) m_statusValues.size();
        }

        public boolean nextRow() {
            if (m_statusValues.next()) {
                DataObject obj = m_statusValues.getDataObject();
                m_status = (MembershipStatus) DomainObjectFactory.newInstance(obj);
                return true;
            } else {
                return false;
            }
        }

        public Object getElementAt(int columnIndex) {
            return m_status;
        }

        public Object getKeyAt(int columnIndex) {
            return m_status.getID();
        }

        public long size() {
            return m_statusValues.size();
        }
    }
}
