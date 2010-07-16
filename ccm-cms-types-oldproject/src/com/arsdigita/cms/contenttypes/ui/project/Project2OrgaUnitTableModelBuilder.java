package com.arsdigita.cms.contenttypes.ui.project;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Project;
import com.arsdigita.cms.contenttypes.Project2OrgaUnit;
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
public class Project2OrgaUnitTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

    private final static Logger s_log =
            Logger.getLogger(Project2OrgaUnitTableModelBuilder.class);
    private ItemSelectionModel m_itemModel;

    public Project2OrgaUnitTableModelBuilder(ItemSelectionModel itemModel) {
        m_itemModel = itemModel;
    }

    public TableModel makeModel(Table t, PageState s) {
        DataCollection units = getUnits(s);

        if (units.isEmpty()) {
            return Table.EMPTY_MODEL;
        } else {
            return new Project2OrgaUnitTableModel(units);
        }
    }

    public DataCollection getUnits(PageState s) {
        Assert.isTrue(m_itemModel.isSelected(s), "item selected");
        Project project = (Project) m_itemModel.getSelectedItem(s);
        return Project2OrgaUnit.getUnits(project);
    }

    public static class Project2OrgaUnitTableModel implements TableModel {

        Project2OrgaUnit m_p2ou;
        DataCollection m_units;

        public Project2OrgaUnitTableModel(DataCollection units) {
            m_units = units;
            m_p2ou = null;
        }

        public int getColumnCount() {
            return (int) m_units.size();
        }

        public boolean nextRow() {
            if (m_units.next()) {
                DataObject obj = m_units.getDataObject();
                m_p2ou = (Project2OrgaUnit) DomainObjectFactory.newInstance(obj);
                return true;
            } else {
                return false;
            }
        }

        public Object getElementAt(int columnIndex) {
            return m_p2ou;
        }

        public Object getKeyAt(int columnIndex) {
            return m_p2ou.getID();
        }

        public long size() {
            return m_units.size();
        }
    }
}
