/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui.project;

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
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.Person;
import com.arsdigita.cms.contenttypes.Project2Person;
import com.arsdigita.cms.contenttypes.ProjectGlobalizationUtil;
import com.arsdigita.cms.dispatcher.Utilities;
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
public class Project2PersonTable extends Table {

    private final static Logger s_log =
            Logger.getLogger(Project2PersonTable.class);
    private Project2PersonSelectionModel m_project2personModel;
    private ItemSelectionModel m_itemModel;
    private TableColumn m_personCol;
    private TableColumn m_editCol;
    private TableColumn m_delCol;
    private TableColumn m_moveUpCol;
    private TableColumn m_moveDownCol;
    private RequestLocal m_size;
    private RequestLocal m_editor;
    protected final static String EDIT_EVENT = "Edit";
    protected final static String DELETE_EVENT = "Delete";
    protected final static String UP_EVENT = "up";
    protected final static String DOWN_EVENT = "down";

    public Project2PersonTable(
            ItemSelectionModel itemModel,
            Project2PersonSelectionModel project2PersonModel) {
        super();
        m_itemModel = itemModel;
        m_project2personModel = project2PersonModel;
        addColumns();

        m_size = new RequestLocal();
        m_editor = new RequestLocal() {

            @Override
            public Object initialValue(PageState s) {
                SecurityManager sm = Utilities.getSecurityManager(s);
                ContentItem item = m_itemModel.getSelectedItem(s);
                Boolean val = new Boolean(sm.canAccess(s.getRequest(), SecurityManager.EDIT_ITEM, item));
                return val;
            }
        };

        Label empty = new Label(ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project.noPersonsAssociated"));
        setEmptyView(empty);
        addTableActionListener(new Project2PersonTableActionListener());
        setRowSelectionModel(project2PersonModel);
        setDefaultCellRenderer(new Project2PersonTableRenderer());
        setModelBuilder(new Project2PersonTableModelBuilder(itemModel));
    }

    public void addColumns() {
        TableColumnModel model = getColumnModel();

        m_personCol = new TableColumn(0,
                ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project.person").localize());
        m_editCol = new TableColumn(1,
                ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project.edit").localize());
        m_delCol = new TableColumn(2,
                ProjectGlobalizationUtil.globalize("cms.contenttypes.ui.project_del").localize());
        m_moveUpCol = new TableColumn(3, "");
        m_moveDownCol = new TableColumn(4, "");

        model.add(m_personCol);
        model.add(m_editCol);
        model.add(m_delCol);
        model.add(m_moveUpCol);
        model.add(m_moveDownCol);
        setColumnModel(model);
    }

    private class Project2PersonTableRenderer implements TableCellRenderer {

        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int column) {
            Project2Person project2Person = (Project2Person) value;
            boolean isFirst = (row == 0);
            if (m_size.get(state) == null) {
                m_size.set(state,
                        new Long(((Project2PersonTableModelBuilder.Project2PersonTableModel) table.getTableModel(state)).size()));
            }
            boolean isLast = (row == ((Long) m_size.get(state)).intValue() - 1);

            String url = project2Person.getURI(state);
            if (column == m_personCol.getModelIndex()) {
                Person person = project2Person.getTargetItem();
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
            } else if (column == m_moveUpCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state)) && !isFirst) {
                    Label downLabel = new Label(UP_EVENT);
                    downLabel.setClassAttr("linkSort");
                    return new ControlLink(downLabel);
                } else {
                    return new Label("");
                }
            } else if (column == m_moveDownCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state)) && !isLast) {
                    Label downLabel = new Label(DOWN_EVENT);
                    downLabel.setClassAttr("linkSort");
                    return new ControlLink(downLabel);
                } else {
                    return new Label("");
                }

            } else {
                throw new UncheckedWrapperException("column out of bounds.");
            }
        }
    }

    private class Project2PersonTableActionListener
            implements TableActionListener {

        private Project2Person getProject2Person(TableActionEvent e) {
            Object o = e.getRowKey();
            BigDecimal id;
            if (o instanceof String) {
                id = new BigDecimal((String) o);
            } else {
                id = (BigDecimal) e.getRowKey();
            }

            Assert.exists(id);
            Project2Person project2Person;
            try {
                project2Person = (Project2Person) DomainObjectFactory.newInstance(
                        new OID(Project2Person.BASE_DATA_OBJECT_TYPE, id));
            } catch (DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException(ex);
            }

            return project2Person;
        }

        public void cellSelected(TableActionEvent e) {
            s_log.debug("this is cellSelected...");
            int col = e.getColumn().intValue();
            PageState state = e.getPageState();
            Project2Person project2Person = getProject2Person(e);
            Assert.exists(project2Person);

            if (col == m_editCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    m_project2personModel.setSelectedObject(state,
                            project2Person);
                }
            } else if (col == m_delCol.getModelIndex()) {
                if (Boolean.TRUE.equals(m_editor.get(state))) {
                    try {
                        m_project2personModel.clearSelection(state);
                        s_log.debug("trying to delete project2Person...");
                        project2Person.delete();
                    } catch (Exception ex) {
                        throw new UncheckedWrapperException(ex);
                    }
                }
            } else if (col == m_moveUpCol.getModelIndex()) {
                m_project2personModel.clearSelection(state);
                project2Person.swapWithPrevious();
            } else if (col == m_moveDownCol.getModelIndex()) {
                m_project2personModel.clearSelection(state);
                project2Person.swapWithNext();
            }
        }

        public void headSelected(TableActionEvent e) {
        }
    }
}
