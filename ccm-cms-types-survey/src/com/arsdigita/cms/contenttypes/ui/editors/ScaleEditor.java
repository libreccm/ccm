/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui.editors;

/**
 *
 * @author quasi
 */
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.contenttypes.PersistentScale;
import com.arsdigita.formbuilder.PersistentOptionGroup;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.formbuilder.PersistentOption;
import com.arsdigita.formbuilder.ui.PropertiesEditor;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.xml.Element;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ScaleEditor extends PropertiesEditor {

    private Table m_table;
    private Form m_editor;
    private Form m_buttons;
    private Submit m_props;
    private Submit m_done;
    private SingleSelectionModel m_control;
    private SingleSelectionModel m_form;

    public ScaleEditor(String name,
            SingleSelectionModel form,
            SingleSelectionModel control) {

        super(new ScaleForm(name, form, control));


        super.addProcessListener(
                new FormProcessListener() {

                    public void process(FormSectionEvent e)
                            throws FormProcessException {
                        PageState state = e.getPageState();

                        getPropertiesForm().setVisible(state, false);
                        m_editor.setVisible(state, true);
                        m_buttons.setVisible(state, true);
                    }
                });

        String[] tableHeaders = new String[]{"Nr", "Frage", "", ""};

        m_table = new Table(new QuestionTableModelBuilder(), tableHeaders);
        m_table.setDefaultCellRenderer(new QuestionTableCellRenderer());
        m_table.addTableActionListener(new QuestionTableActionListener());
        add(m_table);

        m_form = form;
        m_control = control;

        m_editor = new Form("option_editor", new BoxPanel(BoxPanel.VERTICAL));
        m_editor.add(new ScaleOptionEditor(control) {

            @Override
            protected PersistentOptionGroup getOptionGroup(BigDecimal id)
                    throws DataObjectNotFoundException {
                return new PersistentScale(id).getOptionList();
            }
        });
        add(m_editor);

        m_props = new Submit("props", "Properties");
        m_done = new Submit("done", "Done");

        m_buttons = new Form("buttons");
        m_buttons.add(m_props);
        m_buttons.add(m_done);
        add(m_buttons);
    }

    @Override
    public void generateXML(PageState state, Element parent) {
        if (m_control.getSelectedKey(state) == null
                || m_props.isSelected(state)) {
            getPropertiesForm().setVisible(state, true);
            m_buttons.setVisible(state, false);
            m_editor.setVisible(state, false);
        } else {
            getPropertiesForm().setVisible(state, false);
            m_buttons.setVisible(state, true);
            m_editor.setVisible(state, true);
        }

        super.generateXML(state, parent);
    }

    @Override
    public void addProcessListener(FormProcessListener l) {
        super.addProcessListener(l);

        m_buttons.addProcessListener(new PropertiesFormProcessListener(l));
    }

    @Override
    public boolean isComplete(PageState state) {
        return m_done.isSelected(state);
    }

    private class QuestionTableModelBuilder extends AbstractTableModelBuilder {

        public TableModel makeModel(Table table, PageState state) {
            PersistentOptionGroup questions = fetchOptionGroup(state);

            return new QuestionTableModel(questions);
        }

        private class QuestionTableModel implements TableModel {

            ArrayList m_questions;
            PersistentOption m_option;

            public QuestionTableModel(ArrayList questions) {
                m_questions = questions;
                m_option = null;
            }

            public Object getKeyAt(int param1) {
                return m_option.getID();
            }

            public Object getElementAt(int param1) {
                return m_option;
            }

            public boolean nextRow() {
                boolean hasNext = m_questions.next();

                if (hasNext) {
                    m_option = (PersistentOption) DomainObjectFactory.newInstance(m_questions.getDataObject());
                } else {
                    m_option = null;
                }

                return hasNext;
            }

            public int getColumnCount() {
                return 2;
            }
        }
    }

    private class QuestionTableCellRenderer implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean param4, Object key, int row, int column) {

            PersistentOption question = (PersistentOption) value;

            switch (column) {
                case 0: return new Label(row + ".");
// XXX
                case 1: return new Label(question.getLabel());
                case 2: return new ControlLink((String) GlobalizationUtil.globalize("formbuilder.ui.editors.edit").localize());
                case 3: return new ControlLink((String) GlobalizationUtil.globalize("formbuilder.ui.editors.delete").localize());
                default: return null;
            }
        }
    }

    private class QuestionTableActionListener implements TableActionListener {

        public void cellSelected(TableActionEvent e) {
            PageState state = e.getPageState();

            Integer column = e.getColumn();
            String key = (String) e.getRowKey();
            BigDecimal id = new BigDecimal(key);

            PersistentOption option = null;
            try {
                option = new PersistentOption(id);
            } catch (DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException("cannot find option", ex);
            }

            if (column.intValue() == 2) {
                m_label.setValue(state, option.getLabel());
            } else if (column.intValue() == 3) {
                PersistentOptionGroup group = fetchOptionGroup(state);
                group.removeOption(option);
                option.delete();
                m_table.getRowSelectionModel().clearSelection(state);
            }

            state.clearControlEvent();
            try {
                throw new RedirectSignal(state.stateAsURL(), true);
            } catch (IOException ex) {
                throw new UncheckedWrapperException(ex);
            }
        }

        public void headSelected(TableActionEvent e) {
        }
    }
}
