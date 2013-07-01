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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.MetaObject;
import com.arsdigita.formbuilder.MetaObjectCollection;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.util.GlobalizationUtil ; 
import com.arsdigita.util.UncheckedWrapperException;


/**
 * Class to create and manage a new control widget selected from a list of
 * available form widets.
 * 
 * 
 */
public class NewControl extends Form {

    /**   */
    ParameterSingleSelectionModel m_selection;
    /**   */
    SingleSelect m_type;

    /**
     * NewControl constructor, creates a form to select from a list of
     * available form widgets and creates one upon request. Typically it is
     * used by pachages which interactively create user input forms, e.g. the
     * form item content type.
     * 
     * @param app 
     */
    public NewControl(String app) {
        super("new_control");

        /* Create a form to select and add a form control widget             */
        FormSection fs = new FormSection(new BoxPanel(BoxPanel.HORIZONTAL));
        fs.add(new Label(GlobalizationUtil.globalize(
                         "formbuilder.ui.form_widget.add_new_label")));

        m_selection = new ParameterSingleSelectionModel(
                          new BigDecimalParameter("type"));

        m_type = new SingleSelect(m_selection.getStateParameter());
     // Doesn't work. Checks for null right at the construction of the form.
     // m_type.addOption(new 
     //            Option("",
     //                   new Label(GlobalizationUtil.globalize(
     //                             "formbuilder.ui.form_widget.select_one"))
     //            ));
     // Compare to  NewSection.java 
     // m_type.addValidationListener(new NotNullValidationListener(
     //                                  GlobalizationUtil.globalize(
     //                                  "formbuilder.ui.form_widget.null_error")
     //                                  ));  
        loadComponents(app);

        fs.add(m_type);
        fs.add(new Submit( GlobalizationUtil.globalize(
                           "formbuilder.ui.form_widget.create_button") ));

        addInitListener(new NewControlInitListener());

        add(fs);
    }

    /**
     * 
     * @param page 
     */
    @Override
    public void register(Page page) {
        super.register(page);
        page.addComponentStateParam(this,
                                    m_selection.getStateParameter());
    }

    /**
     * 
     * @param app 
     */
    protected void loadComponents(String app) {
        try {
            MetaObjectCollection objects = MetaObject
                                           .getWidgets(app,PersistentComponent.class);
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

    /**
     * 
     * @return 
     */
    public SingleSelectionModel getSelection() {
        return m_selection;
    }

    /**
     * 
     */
    private class NewControlInitListener implements FormInitListener {
        public void init(FormSectionEvent e)
            throws FormProcessException {

            // FIXME: what is the point of this method? -- 2002-11-26

            // BigDecimal type = (BigDecimal)m_selection.getSelectedKey(e.getPageState());
            //m_type.setOptionSelected(type.toString());
        }
    }
}
