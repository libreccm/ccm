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

import com.arsdigita.formbuilder.ui.PropertiesEditor;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.xml.Element;

public class OptionGroupEditor extends PropertiesEditor {

    private Form m_editor;
    private Form m_buttons;
    private Submit m_props;
    private Submit m_done;

    private SingleSelectionModel m_control;
    private SingleSelectionModel m_form;

    public OptionGroupEditor(String name,
                             SingleSelectionModel form,
                             SingleSelectionModel control,
                             OptionGroupForm frm,
                             OptionEditor editor) {
        super(frm);

        m_form = form;
        m_control = control;

        frm.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e)
                    throws FormProcessException {
                    PageState state = e.getPageState();

                    getPropertiesForm().setVisible(state, false);
                    m_editor.setVisible(state, true);
                    m_buttons.setVisible(state, true);
                }
            });

        m_editor = new Form("option_editor", new BoxPanel(BoxPanel.VERTICAL));
        m_editor.add(editor);
        add(m_editor);

        m_props = new Submit("props", "Properties");
        m_done = new Submit("done", "Done");

        m_buttons = new Form("buttons");
        m_buttons.add(m_props);
        m_buttons.add(m_done);
        add(m_buttons);
    }

    public void generateXML(PageState state,
                            Element parent) {
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

    public void addProcessListener(FormProcessListener l) {
        super.addProcessListener(l);

        m_buttons.addProcessListener(new PropertiesFormProcessListener(l));
    }

    public boolean isComplete(PageState state) {
        return m_done.isSelected(state);
    }
}
