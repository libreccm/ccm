package com.arsdigita.cms.contenttypes.ui.project;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Project;
import com.arsdigita.cms.contenttypes.Project2Person;
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
public class Project2PersonTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

    private final static Logger s_log =
            Logger.getLogger(Project2PersonTableModelBuilder.class);
    private ItemSelectionModel m_itemModel;

    public Project2PersonTableModelBuilder(ItemSelectionModel itemModel) {
        m_itemModel = itemModel;
    }

    public TableModel makeModel(Table t, PageState s) {
        DataCollection persons = getPersons(s);

        if (persons.isEmpty()) {
            return Table.EMPTY_MODEL;
        } else {
            return new Project2PersonTableModel(persons);
        }
    }

    public DataCollection getPersons(PageState s) {
        Assert.isTrue(m_itemModel.isSelected(s), "item selected");
        Project project = (Project) m_itemModel.getSelectedItem(s);
        return Project2Person.getProject2Persons(project);
    }

    public static class Project2PersonTableModel implements TableModel {

        DataCollection m_persons;
        Project2Person m_p2p;

        public Project2PersonTableModel(DataCollection persons) {
            m_persons = persons;
            m_p2p = null;
        }

        public int getColumnCount() {
            return (int) m_persons.size();
        }

        public boolean nextRow() {
            if (m_persons.next()) {
                DataObject obj = m_persons.getDataObject();
                m_p2p = (Project2Person) DomainObjectFactory.newInstance(obj);
                return true;
            } else {
                return false;
            }
        }

        public Object getElementAt(int columnIndex) {
            return m_p2p;
        }

        public Object getKeyAt(int columnIndex) {
            return m_p2p.getID();
        }

        public long size() {
            return m_persons.size();
        }
        
    }
}
