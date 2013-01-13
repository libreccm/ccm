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

package com.arsdigita.london.terms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.london.terms.Term;
import com.arsdigita.categorization.ui.AbstractCategoryPicker;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

/**
 * 
 * 
 */
public class FlatCategoryPicker extends AbstractCategoryPicker {
    
    public FlatCategoryPicker(String name) {
        super(name);
    }
    
    protected void addOptions(PageState state,
                              SingleSelect target) {            
        DataCollection terms = SessionManager.getSession()
            .retrieve(Term.BASE_DATA_OBJECT_TYPE);
        terms.addPath("model.id");
        terms.addPath("model.objectType");
        terms.addPath("model.name");
        terms.addPath("domain.title");
        terms.addOrder("domain.title");
        terms.addOrder("model.name");
        
        target.addOption(new Option(null, "-- pick one --"));
        while (terms.next()) {
            target.addOption(
                new Option(new OID((String)terms.get("model.objectType"),
                                   terms.get("model.id")).toString(),
                           terms.get("domain.title") + " -> " + 
                           terms.get("model.name")));
        }
    } 
}
