/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.navigation.ui.admin;

import com.arsdigita.navigation.Template;
import com.arsdigita.navigation.TemplateMapping;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.TemplateContext;
import com.arsdigita.cms.TemplateContextCollection;
import com.arsdigita.xml.Element;
import java.util.TooManyListenersException;

public class CategoryFormAddContext extends Form {
    
    // A mapping ID. For this form this will always be null
    private Hidden m_mappingID = new Hidden(new BigDecimalParameter("mappingID"));

    private CategorySelectionModel m_category;
    private SingleSelect m_template;
    private SingleSelect m_dispatcherContext;
    private TextField m_useContext;
    private Submit m_save, m_cancel;

    public CategoryFormAddContext(CategorySelectionModel category) {
        super("categoryAdd", new ColumnPanel(4));
        m_category = category;

        add(new Label("Dispatcher context"));
        add(new Label("Use Context. Set to 'default' for the default context"));
        add(new Label("Template"));
        add(new Label());

        m_dispatcherContext = new SingleSelect("dispatcherContext");
        try {
            m_dispatcherContext.addPrintListener(new ContextPrintListener());
        } catch (TooManyListenersException ex) {
            throw new RuntimeException("This cannot happen");
        }
        m_dispatcherContext.addValidationListener( new NotNullValidationListener() );
        m_dispatcherContext.setOptionSelected( Template.DEFAULT_DISPATCHER_CONTEXT );
        add(m_dispatcherContext);

        m_useContext = new TextField("useContext");
        m_useContext.setMaxLength( 100 );
        m_useContext.setSize( 10 );
        m_useContext.addValidationListener( new NotNullValidationListener() );
        add(m_useContext);

        m_template = CategoryForm.templateSelectionWidget();
        add(m_template);

        SimpleContainer buttons = new SimpleContainer();
        m_save = new Submit("save");
        m_save.setButtonLabel("Save");
        buttons.add(m_save);
        m_cancel = new Submit("cancel");
        m_cancel.setButtonLabel("Cancel");
        buttons.add(m_cancel);
        add(buttons);

        add(m_mappingID);

        addProcessListener(new CategoryFormProcessListener(
            m_mappingID, m_category, m_template,
            m_dispatcherContext, m_useContext, m_save));

        addValidationListener( new FormValidationListener() {
            public void validate(FormSectionEvent ev) throws FormProcessException {
                PageState ps = ev.getPageState();

                Category category = m_category.getSelectedCategory( ps );
                String dispatcherContext = (String) m_dispatcherContext.getValue( ps );
                String useContext = (String) m_useContext.getValue( ps );

                TemplateMapping mapping = TemplateMapping.retrieve( category, dispatcherContext, useContext );

                if( null != mapping ) {
                    StringBuffer buf = new StringBuffer();
                    buf.append( "A template already exists for dispatcher context " );
                    buf.append( dispatcherContext ).append( " and use context " );
                    buf.append( useContext ).append( ". You may edit it below" );

                    throw new FormProcessException( buf.toString() );
                }
            }
        } );
    }

    
    private class ContextPrintListener implements PrintListener {
        public void prepare(PrintEvent ev) {
            SingleSelect target = (SingleSelect)ev.getTarget();
            target.clearOptions();
            TemplateContextCollection contexts = TemplateContext.retrieveAll();
            while (contexts.next()) {
                TemplateContext context = contexts.getTemplateContext();
                target.addOption(new Option(context.getContext(),
                                            context.getLabel()));
            }
        }
    }

    public void generateXML( PageState state, Element parent ) {
        m_mappingID.setValue( state, null );
        m_template.setValue( state, null );
        m_dispatcherContext.setValue( state, Template.DEFAULT_DISPATCHER_CONTEXT );
        m_useContext.setValue( state, Template.DEFAULT_USE_CONTEXT );

        super.generateXML( state, parent );
    }
}
