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
import com.arsdigita.navigation.TemplateCollection;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.categorization.Category;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.xml.Element;
import java.util.TooManyListenersException;

import java.math.BigDecimal;

public class CategoryForm extends Form {
    
    private CategorySelectionModel m_category;
    private SingleSelect m_template;
    private Label m_dispatcherContext;
    private TextField m_useContext;
    private Hidden m_mappingID;
    private Submit m_change;
    private Hidden m_dispatcherContextHidden;

    public CategoryForm(CategorySelectionModel category) {
        super("category", new ColumnPanel(4));
        m_category = category;

        m_mappingID = new Hidden(new BigDecimalParameter("mappingID"));
        add(m_mappingID);

        m_dispatcherContext = new Label("");
        add(m_dispatcherContext);

        m_useContext = new TextField("useContext");
        m_useContext.setMaxLength( 100 );
        m_useContext.setSize( 10 );
        m_useContext.addValidationListener( new NotNullValidationListener() );
        add(m_useContext);

        m_template = templateSelectionWidget();
        add(m_template);

        m_change = new Submit("change");
        m_change.setButtonLabel("Change");
        add(m_change);

        m_dispatcherContextHidden = new Hidden("dispatcherContext");
        add(m_dispatcherContextHidden);
        
        addProcessListener(new CategoryFormProcessListener(
                               m_mappingID, m_category, m_template,
                               m_dispatcherContextHidden, m_useContext, m_change));
    }
    
    /**
     * Resuable widget factory.
     */
    static SingleSelect templateSelectionWidget() {
        SingleSelect widget = new SingleSelect(new BigDecimalParameter("template"));
        try {
            widget.addPrintListener(new TemplatePrintListener());
        } catch (TooManyListenersException ex) {
            throw new RuntimeException("This cannot happen");
        }
        return widget;
    }

    private static class TemplatePrintListener implements PrintListener {
        public void prepare(PrintEvent ev) {
            SingleSelect target = (SingleSelect)ev.getTarget();
            target.clearOptions();

            target.addOption(new Option(null, "Inherit from parent"));
            TemplateCollection templates = Template.retrieveAll();
            while (templates.next()) {
                Template template = templates.getTemplate();
                target.addOption(new Option(template.getID().toString(),
                                            template.getTitle()));
            }
        }
    }

    public void generateXML(PageState state, Element parent) {
        // loop through all contexts for the selected category,
        // HACK generate one instance of the same form for each context
        Category category = m_category.getSelectedCategory(state);

        DataCollection templates = Template.retrieveForCategory(category);
        templates.addPath( "templateMappings.id" );
        templates.addPath( "templateMappings.dispatcherContext" );
        templates.addPath( "templateMappings.useContext" );
        templates.addOrder( "templateMappings.dispatcherContext, templateMappings.useContext" );

        while (templates.next()) {
            BigDecimal mappingID = (BigDecimal) templates.get("templateMappings.id");
            BigDecimal templateID = (BigDecimal) templates.get("id");
            String dispatcherContext = (String) templates.get("templateMappings.dispatcherContext");
            String useContext = (String) templates.get("templateMappings.useContext");

            m_dispatcherContext.setLabel(dispatcherContext, state);

            m_mappingID.setValue(state, mappingID);
            m_template.setValue(state, templateID);
            m_useContext.setValue(state, useContext);
            m_dispatcherContextHidden.setValue(state, dispatcherContext);

            super.generateXML(state, parent);
        }
    }
}
