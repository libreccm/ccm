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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.categorization.Category;

import java.math.BigDecimal;


class CategoryFormProcessListener implements FormProcessListener {
    
    private static final org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(CategoryFormProcessListener.class);

    private CategorySelectionModel m_category;
    private Widget m_mappingID, m_template, m_dispatcherContext, m_useContext;
    private Submit m_button;

    public CategoryFormProcessListener(
        Widget mappingID,
        CategorySelectionModel category,
        Widget template,
        Widget dispatcherContext,
        Widget useContext,
        Submit button
    ) {
        m_mappingID = mappingID;
        m_category = category;
        m_template = template;
        m_dispatcherContext = dispatcherContext;
        m_useContext = useContext;
        m_button = button;
    }

    public void process(FormSectionEvent e) 
        throws FormProcessException {
        
        PageState state = e.getPageState();

        if (m_button.isSelected(state)) {
            
            BigDecimal mappingID = (BigDecimal) m_mappingID.getValue(state);
            Category category = m_category.getSelectedCategory(state);
            String dispatcherContext = (String) m_dispatcherContext.getValue(state);
            String useContext = (String) m_useContext.getValue(state);

            TemplateMapping mapping = null;
            if( null != mappingID ) mapping = TemplateMapping.retrieve( mappingID );

            if( s_log.isDebugEnabled() ) {
                StringBuffer buf = new StringBuffer();
                if( null != mapping ) {
                    buf.append( "Template mapping: " ).append( mapping.getOID() );
                } else {
                    buf.append( "No existing template mapping" );
                }
            }

            BigDecimal newID = (BigDecimal)m_template.getValue(state);
            // Setting the template to inherit, so just delete the mapping
            if (newID == null) {
                if (mapping != null) mapping.delete();
            }
            
            // Altering or creating a new mapping
            else {
                Template current = Template.retrieve(newID);

                if( null == mapping ) {
                    new TemplateMapping( current, category, dispatcherContext, useContext );
                }

                else {
                    mapping.setTemplate( current );
                    mapping.setUseContext( useContext );
                }
            }
        }
        //m_category.clearSelection(state);
    }
}
