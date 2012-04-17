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

package com.arsdigita.categorization.ui;


import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;

import java.util.TooManyListenersException;


public abstract class AbstractCategoryPicker extends SingleSelect 
                                             implements CategoryPicker {
    
    public AbstractCategoryPicker(String name) {
        super(new OIDParameter(name));
        
        try {
            addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent ev) {
                        addOptions(ev.getPageState(),
                                   (SingleSelect)ev.getTarget());
                    }
                });
        } catch (TooManyListenersException ex) {
            throw new RuntimeException("this cannot happen");
        }        
    }
    
    public Category getCategory(PageState state) {
        OID oid = (OID)getValue(state);
        
        if (oid == null) {
            return null;
        }
        return (Category)DomainObjectFactory.newInstance(oid);
    }
    
    public void setCategory(PageState state,
                            Category category) {
        if (category == null) {
            setValue(state, null);
        } else {
            setValue(state, category.getOID());
        }
    }
    
    protected abstract void addOptions(PageState state,
                                       SingleSelect target);
}
