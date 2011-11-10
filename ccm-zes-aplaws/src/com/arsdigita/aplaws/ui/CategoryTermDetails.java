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

package com.arsdigita.aplaws.ui;

import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.ui.AbstractTermDetails;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.categorization.Category;
import com.arsdigita.bebop.PageState;
import com.arsdigita.domain.DomainObjectFactory;

import org.apache.log4j.Logger;

public class CategoryTermDetails extends AbstractTermDetails {
    
    private static final Logger s_log = Logger.getLogger(CategoryTermDetails.class);

    protected Term getTerm(PageState state) {
        Category cat = Navigation.getConfig().getDefaultModel()
            .getCategory();
        
        if (cat == null) {
            s_log.debug("No category found, skiping term");
            return null;
        }
        
        DataCollection terms = SessionManager.getSession()
            .retrieve(Term.BASE_DATA_OBJECT_TYPE);
        terms.addEqualsFilter(Term.MODEL + "." + Category.ID,
                              cat.getID());
        
        if (terms.next()) {
            Term term = (Term)DomainObjectFactory
                .newInstance(terms.getDataObject());
            terms.close();
            if (s_log.isInfoEnabled()) {
                s_log.info("Found term " + term + " for category " + cat);
            }
            return term;
        }
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("No term found for category " + cat);
        }
        return null;
    }

}
