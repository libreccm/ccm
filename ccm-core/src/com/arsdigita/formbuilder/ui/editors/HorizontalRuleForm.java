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
import com.arsdigita.formbuilder.PersistentHorizontalRule;
import com.arsdigita.formbuilder.ui.PropertiesForm;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class HorizontalRuleForm extends PropertiesForm 
    implements FormInitListener, FormProcessListener {

    private static final Logger s_log =
        Logger.getLogger(HorizontalRuleForm.class);

    private SingleSelectionModel m_action;
    private SingleSelectionModel m_form;

    public HorizontalRuleForm(String name,
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

        section.add( new Label( "Click Save to create a section break" ) );
    }

    public void init(FormSectionEvent e)
        throws FormProcessException {
        
        PageState state = e.getPageState();

        BigDecimal action = (BigDecimal)m_action.getSelectedKey(state);
        if (action != null) {
            // Do some init type stuff
        }
    }

    public void process(FormSectionEvent e)
        throws FormProcessException {
        
        PageState state = e.getPageState();
        
        if (isCancelled(state)) {
            return;
        }

        BigDecimal action = (BigDecimal)m_action.getSelectedKey(state);
        if (action == null) {
            PersistentHorizontalRule rule = new PersistentHorizontalRule();
            rule.save();

            BigDecimal form_id =
                (BigDecimal)m_form.getSelectedKey(e.getPageState());

            PersistentFormSection form = new PersistentFormSection(form_id);
            form.addComponent(rule);
            form.save();
        }
    }
}
