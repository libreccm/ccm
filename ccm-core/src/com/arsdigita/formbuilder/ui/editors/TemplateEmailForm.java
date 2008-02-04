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

import com.arsdigita.formbuilder.CompoundComponent;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.PersistentProcessListener;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.actions.TemplateEmailListener;
import com.arsdigita.formbuilder.util.GlobalizationUtil ; 
import com.arsdigita.formbuilder.util.FormBuilderUtil ; 

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

public class TemplateEmailForm extends ProcessListenerForm {
    private TextField m_to;
    private TextField m_subject;
    private TextArea m_body;
    private SingleSelect m_controls;

    private SingleSelectionModel m_form;

    private static final Logger s_log =
        Logger.getLogger( TemplateEmailForm.class );

    public TemplateEmailForm(String name,
                             SingleSelectionModel form,
                             SingleSelectionModel action) {
        super(name, form, action);

        m_form = form;
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        if (FormBuilderUtil.getConfig().getInterpolateEmailActionsToAddress()) {
            m_to = new TextField(new StringParameter("to"));
        } else {
        	m_to = new TextField(new EmailParameter("to"));
        }
        m_to.setSize(50);
        m_to.addValidationListener(new NotNullValidationListener());
        //m_to.addValidationListener(new StringInRangeValidationListener(1, 120));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.to")), ColumnPanel.RIGHT);
        section.add(m_to);

        m_subject = new TextField(new StringParameter("subject"));
        m_subject.setSize(50);
        m_subject.addValidationListener(new NotNullValidationListener());
        m_subject.addValidationListener(new StringInRangeValidationListener(1, 120));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.subject")), ColumnPanel.RIGHT);
        section.add(m_subject);

        m_body = new TextArea(new StringParameter("body"));
        m_body.setIdAttr("templatedEmailBody");
        m_body.setCols(50);
        m_body.setRows(20);
        m_body.addValidationListener(new NotNullValidationListener());
        m_body.addValidationListener(new StringInRangeValidationListener(1, 4000));

        m_controls = new SingleSelect("controls");
        m_controls.addOption(new Option("", "Select a control"));
        m_controls.setOnChange("if(this.value.length>0) document.getElementById('templatedEmailBody').value+=this.value;");
        try {
            m_controls.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent ev) {
                    PageState ps = ev.getPageState();

                    BigDecimal formID = (BigDecimal)
                        m_form.getSelectedKey( ps );
                    Assert.exists( formID, BigDecimal.class );

                    OID formOID =
                        new OID( PersistentFormSection.BASE_DATA_OBJECT_TYPE,
                                 formID );

                    m_controls.addOption(new Option("::user.email::", "Email address"), ps);

                    PersistentFormSection form = (PersistentFormSection)
                        DomainObjectFactory.newInstance( formOID );

                    addComponents(ps, form.getComponentsIter(), m_controls);
                }
            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }

        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.control_names")), ColumnPanel.RIGHT);
        section.add(m_controls);
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.body")), ColumnPanel.RIGHT);
        section.add(m_body);
    }

    private void addComponents(PageState ps, Iterator components,
                               SingleSelect controls) {
        while( components.hasNext() ) {
            PersistentComponent c = (PersistentComponent) components.next();

            if( c instanceof PersistentWidget ) {
                PersistentWidget w = (PersistentWidget) c;

                if (!"::user.email::".equals(w.getParameterName())) {
                    String parameter = "::form." +
                                       w.getParameterName() + "::";
                    controls.addOption(new Option(parameter, w.getParameterName()), ps);
                }
            } else if (c instanceof CompoundComponent) {
                CompoundComponent compound = (CompoundComponent) c;
                addComponents(ps, compound.getComponentsIter(), controls);
            }
        }
    }

    protected PersistentProcessListener getProcessListener() {
        return new TemplateEmailListener();
    }

    protected PersistentProcessListener getProcessListener(BigDecimal id)
        throws DataObjectNotFoundException {

        return new TemplateEmailListener(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentProcessListener listener)
        throws FormProcessException {
        super.initWidgets(e, listener);

        TemplateEmailListener l = (TemplateEmailListener)listener;

        PageState state = e.getPageState();

        if (l == null) {
            m_to.setValue(state, "");
            m_subject.setValue(state, "");
            m_body.setValue(state, "");
        } else {
            m_to.setValue(state, l.getTo());
            m_subject.setValue(state, l.getSubject());
            m_body.setValue(state, l.getBody());
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentProcessListener listener)
        throws FormProcessException {
        super.processWidgets(e, listener);

        TemplateEmailListener l = (TemplateEmailListener)listener;

        FormData data = e.getFormData();

        String to = data.get("to").toString();
        String subject = (String)data.get("subject");
        String body = (String)data.get("body");

        l.setTo(to);
        l.setSubject(subject);
        l.setBody(body);
    }

    protected boolean showName() {
        return false;
    }
}
