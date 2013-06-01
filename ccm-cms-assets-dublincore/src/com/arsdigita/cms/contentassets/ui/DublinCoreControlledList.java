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

package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.util.UncheckedWrapperException;

import java.util.TooManyListenersException;

/**
 * 
 * 
 */
public class DublinCoreControlledList extends SingleSelect {
    
    private Domain m_domain;

    /**
     * 
     * @param name
     * @param domain 
     */
    public DublinCoreControlledList(String name,
                                    Domain domain) {
        super(name);
        
        m_domain = domain;
        m_domain.disconnect();
     
        try {
            addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        DublinCoreControlledList target = 
                                (DublinCoreControlledList)e.getTarget();
                        target.populate();
                    }
                });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException("Cannot happen", ex);
        }
    }

    /**
     * 
     */
    private void populate() {
        DomainCollection terms = m_domain.getTerms();
        terms.addPath(Term.MODEL + "." + ACSObject.ID);
        terms.addPath(Term.MODEL + "." + ACSObject.OBJECT_TYPE);
        terms.addPath(Term.MODEL + "." + ACSObject.DEFAULT_DOMAIN_CLASS);
        terms.addOrder(Term.NAME);

        addOption(new Option(null, "--Select Term--"));
        while (terms.next()) {
            Term term = (Term)terms.getDomainObject();
            
            addOption(new Option(term.getName(),
                                 term.getName()));
        }
    }
}
