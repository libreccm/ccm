/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.formbuilder.ui.editors;


import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.formbuilder.PersistentOption;
import com.arsdigita.formbuilder.PersistentOptionGroup;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;

import java.math.BigDecimal;
import java.io.IOException;

public abstract class OptionEditor extends FormSection {

    //*** Fields

    SingleSelectionModel m_control;

    Table m_table;
    TextField m_value;
    TextField m_label;

    SaveCancelSection m_buttons;

    // Only create the Option Group once per request
    private RequestLocal m_optionGroup =
        new RequestLocal() {
            public Object initialValue(PageState pageState) {

                BigDecimal control = (BigDecimal)m_control.getSelectedKey(pageState);
                PersistentOptionGroup group = null;
                try {
                    group = getOptionGroup(control);
                } catch (DataObjectNotFoundException ex) {
                    throw new UncheckedWrapperException("cannot find option group", ex);
                }

                return group;
            }
        };

    /**
     * Constructor
     */
    public OptionEditor(SingleSelectionModel control) {
        super(new BoxPanel(BoxPanel.VERTICAL));

        m_control = control;

        String[] tableHeaders = null;
        if (showOptionValue()) {
            tableHeaders = new String[] {"Value", "Option Label", "", ""};
        } else {
            tableHeaders = new String[] {"Option Label", "", ""};
        }

        m_table = new Table(new OptionTableModelBuilder(),
                            tableHeaders);
        m_table.setDefaultCellRenderer(new TableCellRenderer() {
                public Component getComponent(Table table,
                                              PageState state,
                                              Object value,
                                              boolean param4,
                                              Object key,
                                              int row,
                                              int column){
                    PersistentOption option = (PersistentOption)value;

                    // If we are not displaying the HTML value we have
                    // to shift the columns one step to the left
                    int columnShift = 0;
                    if (!showOptionValue()) {
                        columnShift = -1;
                    }

                    if (column == 0 + columnShift) {
                        return new Label(option.getParameterValue());
                    } else if (column == 1 + columnShift) {
                        return new Label(option.getLabel());
                    } else if (column == 2 + columnShift) {
                        return new ControlLink( (String) GlobalizationUtil.globalize("formbuilder.ui.editors.edit").localize());
                    } else if (column == 3 + columnShift) {
                        return new ControlLink( (String) GlobalizationUtil.globalize("formbuilder.ui.editors.delete").localize());
                    }
                    return null;
                }
            });
        m_table.addTableActionListener(new TableActionListener() {
                public void cellSelected(TableActionEvent e) {
                    PageState state = e.getPageState();

                    Integer column = e.getColumn();
                    String key = (String)e.getRowKey();
                    BigDecimal id = new BigDecimal(key);

                    PersistentOption option = null;
                    try {
                        option = new PersistentOption(id);
                    } catch (DataObjectNotFoundException ex) {
                        throw new UncheckedWrapperException("cannot find option", ex);
                    }

                    if (column.intValue() == 2) {
                        if (showOptionValue()) {
                            m_value.setValue(state, option.getParameterValue());
                        }
                        m_label.setValue(state, option.getLabel());
                    } else if (column.intValue() == 3) {
                        PersistentOptionGroup group = fetchOptionGroup(state);
                        group.removeOption(option);
                        option.delete();
                        m_table.getRowSelectionModel().clearSelection(state);
                    }

                    state.clearControlEvent();
                    try {
                        throw new RedirectSignal( state.stateAsURL(), true );
                    } catch( IOException ex ) {
                        throw new UncheckedWrapperException( ex );
                    }
                }

                public void headSelected(TableActionEvent e) {}
            });

        add(m_table);

        if (showOptionValue()) {
            m_value = new TextField(new StringParameter("opt_value"));
            m_value.addValidationListener( new NotNullValidationListener() );
        }

        m_label = new TextField(new StringParameter("opt_label"));
        m_label.addValidationListener( new NotNullValidationListener() );

        m_buttons = new SaveCancelSection();

        m_buttons.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e)
                    throws FormProcessException {
                    PageState state = e.getPageState();

                    if (m_buttons.getSaveButton().isSelected(state)) {
                        String key = (String)m_table.getRowSelectionModel().getSelectedKey(state);
                        PersistentOption option = null;
                        if (key == null) {
                            option = new PersistentOption();
                        } else {
                            BigDecimal id = new BigDecimal(key);
                            try {
                                option = new PersistentOption(id);
                            } catch (DataObjectNotFoundException ex) {
                                throw new FormProcessException("cannot find option", ex);
                            }
                        }

                        FormData data = e.getFormData();
                        option.setParameterValue(getOptionName(data, option));
                        option.setLabel((String)data.get("opt_label"));

                        option.save();

                        if (key == null) {
                            PersistentOptionGroup group = fetchOptionGroup(state);
                            group.addOption(option);
                        }
                    }

                    if (showOptionValue()) {
                        m_value.setValue(state, "");
                    }

                    m_label.setValue(state, "");
                    m_table.getRowSelectionModel().clearSelection(state);
                }
            });

        ColumnPanel editor = new ColumnPanel(2);

        editor.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.add_option")), ColumnPanel.FULL_WIDTH);

        if (showOptionValue()) {
            editor.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.value")));

            editor.add(m_value);
        }

        editor.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.option_label")));
        editor.add(m_label);

        editor.add(m_buttons);
        editor.setConstraint(m_buttons, ColumnPanel.FULL_WIDTH);

        add(editor);
    }

    /**
     * Sub classes may opt to not have the admin enter the HTML names of the
     * options by overriding the showOptionValue() and getOptionName() methods
     */
    protected boolean showOptionValue() {
        return true;
    }

    /**
     * Sub classes may opt to not have the admin enter the HTML names of the
     * options by overriding the showOptionValue() and getOptionName() methods
     */
    protected String getOptionName(FormData formData, PersistentOption option) {

        return (String)formData.get("opt_value");
    }

    private class OptionTableModelBuilder extends AbstractTableModelBuilder  {
        public TableModel makeModel(Table table, PageState state)
        {
            PersistentOptionGroup group = fetchOptionGroup(state);

            return new OptionTableModel(group.getOptions());
        }

        private class OptionTableModel implements TableModel  {
            DataAssociationCursor m_options;
            PersistentOption m_option;

            public OptionTableModel(DataAssociationCursor options) {
                m_options = options;
                m_option = null;
            }

            public Object getKeyAt(int param1)
            {
                return m_option.getID();
            }

            public Object getElementAt(int param1)
            {
                return m_option;
            }

            public boolean nextRow()
            {
                boolean hasNext = m_options.next();

                if( hasNext ) {
                    m_option = (PersistentOption)
                        DomainObjectFactory.newInstance( m_options.getDataObject() );
                } else {
                    m_option = null;
                }

                return hasNext;
            }

            public int getColumnCount()
            {
                return 2;
            }
        }
    }

    protected PersistentOptionGroup fetchOptionGroup(PageState pageState) {
        return (PersistentOptionGroup)m_optionGroup.get(pageState);
    }

    protected abstract PersistentOptionGroup getOptionGroup(BigDecimal id)
        throws DataObjectNotFoundException;
}
