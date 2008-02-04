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

import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.navigation.NavigationModel;

import com.arsdigita.kernel.ACSObject;

import com.arsdigita.categorization.Category;
import com.arsdigita.xml.Element;

import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.util.Assert;


/**
 * A base class for all components needing to access a
 * NavigationModel object.
 * 
 * XXX, in a future release, this will *not* extend 
 * bebop SimpleComponent
 */
public abstract class AbstractComponent 
    extends SimpleComponent implements Component, NavigationModel {
    
    private NavigationModel m_model = Navigation.getConfig().getDefaultModel();

    public void setModel(NavigationModel model) {
        Assert.unlocked(this);
        m_model = model;
    }

    public NavigationModel getModel() {
        return m_model;
    }
    
    public ACSObject getObject() {
        return m_model.getObject();
    }
    
    public Category getCategory() {
        return m_model.getCategory();
    }
    
    public Category[] getCategoryPath() {
        return m_model.getCategoryPath();
    }
    
    public Category getRootCategory() {
        return m_model.getRootCategory();
    }

    public final void generateXML(PageState state,
                                  Element parent) {
        Assert.locked(this);
        
        Element content = generateXML(state.getRequest(),
                                      state.getResponse());
        
        if (content != null) {
            exportAttributes(content);
            parent.addContent(content);
        }
    }

}
