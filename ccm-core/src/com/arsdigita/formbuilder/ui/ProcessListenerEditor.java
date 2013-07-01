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

package com.arsdigita.formbuilder.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.formbuilder.util.FormBuilderUtil;
import com.arsdigita.formbuilder.util.GlobalizationUtil ; 
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * This class provides a pluggable widget for editing the persistent process
 * listeners for a persistent form.
 * It is designed to be used without requiring any significant
 * infrastructure on a page.
 */
public class ProcessListenerEditor extends SimpleContainer {

    protected SingleSelectionModel m_form;
    protected SingleSelectionModel m_action;
    protected NewAction m_newAction;

    protected BoxPanel m_list_actions;
    protected ProcessListenerProperties m_edit_action;

    /**
     * Constructor, creates a new control editor widget for editing the form
     * specified in the single selection model. The key returned by the single
     * selection model should be an instance of the
     * {@link java.math.BigDecimal} class.
     *
     * @param form the form to edit
     */
    public ProcessListenerEditor(String app,
                                 SingleSelectionModel form) {
        m_form = form;

        // Help system is currently not workable
        String helpURL = FormBuilderUtil.getConfig().getActionsHelpLink();
        if (helpURL != null) {
            add(new Link(new Label(GlobalizationUtil.globalize
                                   ("formbuilder.ui.help")), helpURL));
        }

        m_newAction = new NewAction(app);

        Table t = new Table(new ProcessListenerTableModelBuilder(m_form),
                            new String[] { "Form action", "", "" });
        m_action = new DecimalSingleSelectionModel(t.getRowSelectionModel());
        t.setDefaultCellRenderer(new TableCellRenderer() {
                public Component getComponent(Table table, PageState state, Object value,
                                              boolean isSelected, Object key,
                                              int row, int column) {
                    PersistentProcessListener l = (PersistentProcessListener)value;

                    if (column == 0) {
                        return new Label(l.getDescription());
                    } else if (column == 1) {
                        ControlLink lk = new ControlLink( new Label(GlobalizationUtil
                                         .globalize("formbuilder.ui.edit") ));
                        return lk;
                    } else if (column == 2) {
                        ControlLink lk = new ControlLink( new Label(GlobalizationUtil
                                         .globalize("formbuilder.ui.delete") ));
                        lk.setConfirmation(GlobalizationUtil.globalize(
                                "formbuilder.ui.form_action.delete_confirm"));
                        return lk;
                    }

                    return null;
                }
            });
        t.addTableActionListener(new FormItemActionListener());

        m_list_actions = new BoxPanel(BoxPanel.VERTICAL);
        m_edit_action = new ProcessListenerProperties(m_form,
                                                      m_newAction.getSelection(),
                                                      m_action,
                                                      app);

        m_newAction.addProcessListener(new FormItemProcessListener(m_edit_action,
                                                         m_list_actions));
        m_edit_action.addCompletionListener(new FormItemCompletionListener(m_list_actions,
                                                                           m_edit_action));

        m_list_actions.add(m_newAction);
        m_list_actions.add(t);

        addComponents();

        add(m_list_actions);
        add(m_edit_action);
   }

    protected void addComponents() { }

    public void register(Page page) {
        super.register(page);

        page.addComponentStateParam(this,
                                    m_action.getStateParameter());

        page.setVisibleDefault(m_list_actions, true);
        page.setVisibleDefault(m_edit_action, false);
    }

    private class FormItemProcessListener implements FormProcessListener {
        Component m_show;
        Component m_hide;

        public FormItemProcessListener(Component show,
                                       Component hide) {
            m_show = show;
            m_hide = hide;
        }

        public void process(FormSectionEvent e)
            throws FormProcessException {
            m_action.clearSelection(e.getPageState());
            m_show.setVisible(e.getPageState(), true);
            m_hide.setVisible(e.getPageState(), false);
        }
    }

    private class FormItemCompletionListener implements FormCompletionListener {
        Component m_show;
        Component m_hide;

        public FormItemCompletionListener(Component show,
                                          Component hide) {
            m_show = show;
            m_hide = hide;
        }

        public void complete(FormSectionEvent e)
            throws FormProcessException {
            m_action.clearSelection(e.getPageState());
            m_show.setVisible(e.getPageState(), true);
            m_hide.setVisible(e.getPageState(), false);
        }
    }

    private class FormItemActionListener implements TableActionListener {

        public void headSelected(TableActionEvent e) {}

        public void cellSelected(TableActionEvent e) {
            PageState state = e.getPageState();

            Integer type = e.getColumn();

            if (type.intValue() == 1) {
                m_edit_action.setVisible(state, true);
                m_list_actions.setVisible(state, false);
            } else {
                BigDecimal action_id = (BigDecimal)m_action.getSelectedKey(state);
                
                try {
                    BigDecimal formID = (BigDecimal)
                        m_form.getSelectedKey( state );
                    PersistentFormSection form =
                        new PersistentFormSection( formID );

                    PersistentProcessListener l = (PersistentProcessListener)
                        DomainObjectFactory.newInstance(
                            new OID(PersistentProcessListener.BASE_DATA_OBJECT_TYPE,
                                    action_id));
                    //l.delete();
                    form.removeProcessListener( l );
                } catch (DataObjectNotFoundException ex) {
                    throw new UncheckedWrapperException("cannot find listener", ex);
                }
            }

            state.clearControlEvent();
            try {
                throw new RedirectSignal( state.stateAsURL(), true );
            } catch( IOException ex ) {
                throw new UncheckedWrapperException( ex );
            }
        }
    }

}
