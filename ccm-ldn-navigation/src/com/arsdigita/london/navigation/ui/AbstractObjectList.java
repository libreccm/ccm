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


package com.arsdigita.london.navigation.ui;

import com.arsdigita.categorization.Category;
import com.arsdigita.london.navigation.DataCollectionDefinition;
import com.arsdigita.london.navigation.DataCollectionRenderer;

import com.arsdigita.persistence.DataCollection;

import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import com.arsdigita.xml.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractObjectList 
    extends AbstractComponent implements ObjectList {
    
    private DataCollectionRenderer m_renderer = new DataCollectionRenderer();
    private DataCollectionDefinition m_definition = new DataCollectionDefinition();

    public final void setDefinition(DataCollectionDefinition definition) {
        Assert.isUnlocked(this);
        m_definition = definition;
    }

    public final void setRenderer(DataCollectionRenderer renderer) {
        Assert.isUnlocked(this);
        m_renderer = renderer;
    }

    public final DataCollectionDefinition getDefinition() {
        return m_definition;
    }

    public final DataCollectionRenderer getRenderer() {
        return m_renderer;
    }
    
    protected DataCollection getObjects(HttpServletRequest request,
                                        HttpServletResponse response) {
    	// definition needs to know if the renderer is rendering a date 
    	// attribute so that it can decide whether to order by date for 
    	// a date order category
    	m_definition.setDateAttribute(m_renderer);
       
        return m_definition.getDataCollection(getModel());
    }

    public void lock() {
        super.lock();
        m_renderer.lock();
        m_definition.lock();
    }

    public Element generateObjectListXML(HttpServletRequest request,
                                         HttpServletResponse response) {
        Assert.isLocked(this);
        
        String pageNumberValue = request.getParameter("pageNumber");
        Integer pageNumber = null;
        try {
            if (pageNumberValue == null) {
                pageNumber = new Integer(1);
            } else {
                pageNumber = new Integer(pageNumberValue);
            }
        } catch (NumberFormatException ex) {
            throw new UncheckedWrapperException(
                "cannot parse page number " + pageNumber, ex
            );
        }
        
        DataCollection objects = getObjects(request, response);

        // Quasimodo: Begin
        // Limit list to objects in the negotiated language
        if (objects != null && objects.size() > 0) {
            String locale = com.arsdigita.dispatcher.DispatcherHelper.getRequestContext().getLocale().getLanguage();

            // if supported lang doesn't contain locale
            if(!Category.getConfig().hasLanguage(locale)) {

                // use default language instead
                locale = Category.getConfig().getDefaultLanguage();
            }

            objects.addEqualsFilter("language", locale);
        }
        // Quasimodo: End
        
        return m_renderer.generateXML(objects, pageNumber.intValue());
    }
}
