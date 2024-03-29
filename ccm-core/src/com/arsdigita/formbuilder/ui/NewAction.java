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


import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.formbuilder.MetaObject;
import com.arsdigita.formbuilder.MetaObjectCollection;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Page;


public class NewAction extends Form {
    ParameterSingleSelectionModel m_selection;
    SingleSelect m_type;

    public NewAction(String app) {
        super("new_action");

        FormSection fs = new FormSection(new BoxPanel(BoxPanel.HORIZONTAL));

        fs.add(new Label(GlobalizationUtil.globalize(
                         "formbuilder.ui.form_action.add_new_label")));

        m_selection = new ParameterSingleSelectionModel(new BigDecimalParameter("type"));

        m_type = new SingleSelect(m_selection.getStateParameter());
        loadComponents(app);

        fs.add(m_type);
        fs.add(new Submit(GlobalizationUtil.globalize(
                          "formbuilder.ui.form_action.add_button")));

        add(fs);
    }

    @Override
    public void register(Page page) {
        super.register(page);
        page.addComponentStateParam(this,
                                    m_selection.getStateParameter());
    }

    /**
     * Loads the PersistenProcessListener from database using the preselected
     * object by application. The preselection is made in enterprise.init
     */
    protected void loadComponents(String app) {
        try {
            MetaObjectCollection objects = MetaObject
                                           .getWidgets(app,
                                                       PersistentProcessListener.class);
            objects.addOrder(MetaObject.PRETTY_NAME);

            while (objects.next()) {
                MetaObject control = objects.getMetaObject();

                m_type.addOption(new Option(control.getID().toString(),
                                            control.getPrettyName()));
            }
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    public SingleSelectionModel getSelection() {
        return m_selection;
    }
}
