/*
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
 * This class is newly created  to give support for user email field..
 * @author Shubham nagar
 * @company Infoaxon Technology
 * @date 13-12-2004
 */

package com.arsdigita.formbuilder.ui.editors;

import com.arsdigita.formbuilder.PersistentEmailField;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.parameters.PersistentParameterListener;
import com.arsdigita.formbuilder.util.GlobalizationUtil;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class EmailFieldForm extends WidgetLabelForm {
    private static final Logger s_log = Logger.getLogger(EmailFieldForm.class);
    private TextField m_width;
    private SingleSelectionModel m_control;

    public EmailFieldForm(String name,
                          SingleSelectionModel form,
                          SingleSelectionModel control) {
        super(name, form, control);
        m_control = control;
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);
        //Add code to add a message label for textarea dimension
        m_width = new TextField(new IntegerParameter("width"));
        m_width.setSize(5);
        //m_width.addValidationListener(new NotNullValidationListener());
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.width")), ColumnPanel.RIGHT);
        section.add(m_width);
    }

    protected PersistentWidget getWidget() {
        return new PersistentEmailField();
    }

    protected PersistentWidget getWidget(BigDecimal id) {
        return new PersistentEmailField(id);
    }

    protected void addName(FormSection section) {
        TextField name = new TextField(new StringParameter(NAME));
        name.setSize(50);
        section.add(name);

        m_name = name;
    }

    protected void automaticallySetName(ParameterModel model) { }

    protected String getName(PageState ps, FormData data) {
        return "::user.email::";
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentWidget w)
        throws FormProcessException {
        super.initWidgets(e, w);
        PersistentEmailField widget = (PersistentEmailField)w;
        PageState state = e.getPageState();

        if (widget == null) {
            m_width.setValue(state, "");
        }
        else {
            if(widget.getSize()==0) {
                m_width.setValue(state, "");
            } else {
                m_width.setValue(state, new Integer(widget.getSize()));
            }
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentWidget w)
        throws FormProcessException {
        super.processWidgets(e, w);

        String listenerClassNameEmail =
            "com.arsdigita.bebop.parameters.EmailValidationListener";
        PersistentParameterListener listenerEmail =
            new PersistentParameterListener(listenerClassNameEmail);
        w.addValidationListener(listenerEmail);
    }

    /**
    *  This determines whether or not the "required value" radio group
    *  is part of the form.  This returns true and should be overridden
    *  by fields where it does not make sense to ask.  For instance,
    *  when the widget is a hidden field then asking if it is required
    *  or not does not make any logical sense so those widgets should
    *  return false.
    *
    *  This will always return the same value for a given widget no matter
    *  what state the widget is in.
    */
   protected boolean includeRequiredRadioGroup() {
       return false;
   }

   public void register(Page p) {
       super.register(p);

       p.setVisibleDefault(m_name, false);
   }
}
