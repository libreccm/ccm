package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.cms.contenttypes.Orga2OrgaUnit;
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
public class Orga2OrgaUnitTableModelBuilder extends LockableImpl implements TableModelBuilder {

    private final static Logger logger = Logger.getLogger(Orga2OrgaUnitTableModelBuilder.class);
    private ItemSelectionModel m_itemModel;

    public Orga2OrgaUnitTableModelBuilder(ItemSelectionModel itemModel) {
        this.m_itemModel = itemModel;
    }

    public TableModel makeModel(Table t, PageState s) {
        DataCollection units = getUnits(s);

        if (units.isEmpty()) {
            return Table.EMPTY_MODEL;
        } else {
            return new Orga2OrgaUnitTableModel(units);
        }
    }

    public DataCollection getUnits(PageState s) {
        Assert.isTrue(this.m_itemModel.isSelected(s), "item selected");
        GenericOrganization orga = (GenericOrganization) this.m_itemModel.getSelectedItem(s);
        return Orga2OrgaUnit.getUnits(orga);
    }

    public static class Orga2OrgaUnitTableModel implements TableModel {

        Orga2OrgaUnit m_o2ou;
        DataCollection m_units;

        public Orga2OrgaUnitTableModel(DataCollection units) {
            m_units = units;
            m_o2ou = null;
        }

        public int getColumnCount() {
            return (int) m_units.size();
        }

        public boolean nextRow() {
            if (m_units.next()) {
                DataObject obj = m_units.getDataObject();
                m_o2ou = (Orga2OrgaUnit) DomainObjectFactory.newInstance(obj);
                return true;
            } else {
                return false;
            }
        }

        public Object getElementAt(int columnIndex) {
            return m_o2ou;
        }

        public Object getKeyAt(int columnIndex) {
            return m_o2ou.getID();
        }

        public long size() {
            return m_units.size();
        }
    }
}