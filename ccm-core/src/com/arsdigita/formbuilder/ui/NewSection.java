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

import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;
import java.util.TooManyListenersException;

public class NewSection extends Form {
    private SingleSelectionModel m_form;
    private SingleSelect m_sections;

    public NewSection(SingleSelectionModel form) {
        super("new_section");

        m_form = form;

        FormSection fs = new FormSection(new BoxPanel(BoxPanel.HORIZONTAL));

        m_sections = new SingleSelect(new BigDecimalParameter("section"));
        m_sections.addValidationListener
            (new NotNullValidationListener
             (GlobalizationUtil.globalize("formbuilder.ui.form_section_null_error")));
        fs.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.add_new")));
        fs.add(m_sections);
        fs.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.form_section")));
        fs.add(new Submit("Add"));

        add(fs);
    }

    public BigDecimal getSelectedSection(PageState state) {
        return (BigDecimal)m_sections.getValue(state);
    }

    // XXX PrintListener will change to ListModel when (if)
    // optiongroups finally become model driven
    public void setFormSectionModelBuilder(PrintListener l) {
        try {
            m_sections.addPrintListener(l);
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("Cannot set form model", ex);
        }
    }

    public void generateXML(PageState state, Element parent) {
        OID formOID = new OID( PersistentFormSection.BASE_DATA_OBJECT_TYPE,
                               m_form.getSelectedKey(state) );

        PersistentFormSection form = (PersistentFormSection)
            DomainObjectFactory.newInstance( formOID );

        setVisible(state, form instanceof PersistentForm ? true : false);

        super.generateXML(state, parent);
    }
}
