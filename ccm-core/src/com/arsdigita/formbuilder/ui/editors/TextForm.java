/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.formbuilder.PersistentFormSection;
import com.arsdigita.formbuilder.PersistentText;
import com.arsdigita.formbuilder.ui.PropertiesForm;
import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.globalization.GlobalizedMessage;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class TextForm extends PropertiesForm
    implements FormInitListener, FormProcessListener {

    private static final Logger s_log =
        Logger.getLogger(TextForm.class);
	/**
	 * The text entry widget
	 */
	public static final String TEXT_ENTRY = "text_entry";
    private Widget m_text;
    private SingleSelectionModel m_action;
    private SingleSelectionModel m_form;

    public TextForm(String name,
                    SingleSelectionModel form,
                    SingleSelectionModel action) {
        super(name);
        m_action = action;
        m_form = form;
        addProcessListener(this);
        addInitListener(this);
    }


    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_text = getTextWidget(new StringParameter("text"));
        m_text.addValidationListener(new NotEmptyValidationListener());
        section.add(new Label(getTextTitle()), ColumnPanel.RIGHT);
        section.add(m_text);
    }

    /**
     *  this returns the widget that is used in the "addWidgets" method
     *  can provides subclasses with the ability to use any size desired
     */
    protected Widget getTextWidget(StringParameter parameter) {
		TextArea m_textWidget = new DHTMLEditor(TEXT_ENTRY);
		m_textWidget.setRows(25);
		m_textWidget.setCols(70);
		m_textWidget.setWrap(DHTMLEditor.SOFT);

        return m_textWidget;
    }

    /**
     *  Subclasses can override this so that it will give the correct
     *  name to the text.
     */
    protected GlobalizedMessage getTextTitle() {
        return GlobalizationUtil.globalize
            ("formbuilder.ui.editors.text_form_title");
    }

    public void init(FormSectionEvent e)
        throws FormProcessException {
        
        PageState state = e.getPageState();

        BigDecimal action = (BigDecimal)m_action.getSelectedKey(state);
        if (action != null) {
            PersistentText text = getPersistentText(action);
            m_text.setValue(state, text.getText());
        }
    }


    public void process(FormSectionEvent e)
        throws FormProcessException {
        
        PageState state = e.getPageState();
        
        if (isCancelled(state)) {
            return;
        }
        
        PersistentText text = getPersistentText(state);

        String textString = (String)m_text.getValue(state);
        text.setText(textString);
        text.save();
        
        BigDecimal action = (BigDecimal)m_action.getSelectedKey(state);
        if (action == null) {
            addToForm(e, text);
        }
    }

    protected void addToForm(FormSectionEvent e,
                             PersistentText widget)
        throws FormProcessException {

        BigDecimal form_id = (BigDecimal)m_form.getSelectedKey(e.getPageState());

        PersistentFormSection form = new PersistentFormSection(form_id);
        form.addComponent(widget);
        form.save();
    }

    protected PersistentText getPersistentText() {
        return new PersistentText();
    }

    protected PersistentText getPersistentText(BigDecimal id) {
        return new PersistentText(id);
    }

    protected PersistentText getPersistentText(PageState state) {
        BigDecimal action = (BigDecimal)m_action.getSelectedKey(state);
        if (action != null) {
            return getPersistentText(action);
        } else {
            return getPersistentText();
        }
    }
}
