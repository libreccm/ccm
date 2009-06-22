package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.OrgaUnit2OrgaUnit;
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
public class OrgaUnit2OrgaUnitTableModelBuilder extends LockableImpl implements TableModelBuilder {

    private final static Logger logger = Logger.getLogger(OrgaUnit2OrgaUnitTableModelBuilder.class);

    private ItemSelectionModel m_itemModel;

    public OrgaUnit2OrgaUnitTableModelBuilder(ItemSelectionModel itemModel) {
        this.m_itemModel = itemModel;
    }

    public TableModel makeModel(Table t, PageState s) {
        DataCollection units = getUnits(s);

        if(units.isEmpty()) {
            return Table.EMPTY_MODEL;
        } else {
            return new OrgaUnit2OrgaUnitTableModel(units);
        }
    }

    public DataCollection getUnits(PageState state) {
        Assert.isTrue(this.m_itemModel.isSelected(state), "item selected");
        OrganizationalUnit unit = (OrganizationalUnit) this.m_itemModel.getSelectedItem(state);
        return OrgaUnit2OrgaUnit.getUnits(unit);
    }

    public static class OrgaUnit2OrgaUnitTableModel implements TableModel {

        OrgaUnit2OrgaUnit m_ou2ou;
        DataCollection m_units;

        public OrgaUnit2OrgaUnitTableModel(DataCollection units) {
            this.m_units = units;
            this.m_ou2ou = null;
        }

        public int getColumnCount() {
            return (int) this.m_units.size();
        }

        public boolean nextRow() {
            if (this.m_units.next()) {
                DataObject obj = this.m_units.getDataObject();
                this.m_ou2ou = (OrgaUnit2OrgaUnit) DomainObjectFactory.newInstance(obj);
                return true;
            } else {
                return false;
            }
        }

        public Object getElementAt(int columnIndex) {
            return this.m_ou2ou;
        }

        public Object getKeyAt(int columnIndex) {
            return this.m_ou2ou.getID();
        }

        public long size() {
            return this.m_units.size();
        }
        
    }
}
