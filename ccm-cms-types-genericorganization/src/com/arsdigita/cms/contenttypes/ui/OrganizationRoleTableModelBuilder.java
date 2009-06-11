package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.contenttypes.OrganizationRole;
import com.arsdigita.cms.contenttypes.Person;
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
public class OrganizationRoleTableModelBuilder extends LockableImpl implements TableModelBuilder {

    private static final Logger logger = Logger.getLogger(OrganizationRoleTableModelBuilder.class);

    private ItemSelectionModel m_itemModel;

    public OrganizationRoleTableModelBuilder(ItemSelectionModel itemModel) {
        this.m_itemModel = itemModel;
    }

    public TableModel makeModel(Table t, PageState s) {
        logger.debug("OrganizationRoleTableModelBuilder.makemodel()");

        DataCollection roles = getRoles(s);

        if(roles.isEmpty()) {
            return Table.EMPTY_MODEL;
        } else {
            return new OrganizationRoleTableModel(roles);
        }
    }

    public DataCollection getRoles(PageState s) {
        Assert.isTrue(this.m_itemModel.isSelected(s), "item selected");
        GenericOrganization orga = (GenericOrganization) m_itemModel.getSelectedItem(s);
        return OrganizationRole.getRoles(orga);
    }

    public static class OrganizationRoleTableModel implements TableModel {

        OrganizationRole m_role;
        DataCollection m_roles;

        public OrganizationRoleTableModel(DataCollection roles) {
            m_roles = roles;
            m_role = null;
        }

        public int getColumnCount() {
            return (int)m_roles.size();
        }

        public boolean nextRow() {
            if (m_roles.next()) {
                DataObject obj = m_roles.getDataObject();
                m_role = (OrganizationRole)DomainObjectFactory.newInstance(obj);
                return true;
            } else {
                return false;
            }
        }

        public Object getElementAt(int columnIndex) {
            return m_role;
        }

        public Object getKeyAt(int columnIndex) {
            return m_role.getID();
        }

        public long size() {
            return m_roles.size();
        }
    }
}
