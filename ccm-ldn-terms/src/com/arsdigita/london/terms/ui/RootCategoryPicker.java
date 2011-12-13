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
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.util.ui.AbstractCategoryPicker;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;


/** 
 * 
 * 
 */
public class RootCategoryPicker extends AbstractCategoryPicker {
    
    /**
     * 
     * @param name 
     */
    public RootCategoryPicker(String name) {
        super(name);
    }
    
    /**
     * 
     * @param state
     * @param target 
     */
    protected void addOptions(PageState state,
                              SingleSelect target) {
        DataCollection domains = SessionManager.getSession()
            .retrieve(Domain.BASE_DATA_OBJECT_TYPE);
        domains.addPath("model.id");
        domains.addPath("model.objectType");
        domains.addOrder("title");
        
        target.addOption(new Option(null, "-- pick one --"));
        while (domains.next()) {
            Domain domain = (Domain)
                DomainObjectFactory.newInstance(domains.getDataObject());
            
            target.addOption(
                new Option(new OID((String)domains.get("model.objectType"),
                                   domains.get("model.id")).toString(),
                           domain.getTitle()));
        }
    } 
}
