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

package com.arsdigita.london.terms.ui.admin;

import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.bebop.PageState;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.london.util.ui.AbstractDomainObjectList;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.london.util.ui.event.DomainObjectActionEvent;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.Terms;
import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.xml.XML;
import com.arsdigita.xml.Element;

public class AllTermListing extends AbstractDomainObjectList {

    private DomainObjectParameter m_domain;
    private DomainObjectParameter m_term;

    public static final String ACTION_VIEW = "view";

    public AllTermListing(DomainObjectParameter domain,
                          DomainObjectParameter term) {
        super("allTermListing",
              Terms.XML_PREFIX,
              Terms.XML_NS);

        m_domain = domain;
        m_term = term;

        setRedirecting(true);
        setPageSize(30);
        
        registerDomainObjectAction(ACTION_VIEW);
        
        addDomainObjectActionListener(
            ACTION_VIEW,
            new DomainObjectActionListener() {
                public void actionPerformed(DomainObjectActionEvent e) {
                    e.getPageState().setValue(m_term,
                                              e.getObject());
                }
            });
    }

    
    protected Element generateObjectXML(PageState state,
                                        DomainObject dobj) {
        Term term = (Term)state.getValue(m_term);
        Element el = super.generateObjectXML(state, dobj);
        if (dobj.equals(term)) {
            el.addAttribute("isSelected", XML.format(Boolean.TRUE));
        }
        return el;
    }

    protected DomainCollection getDomainObjects(PageState state) {
        Domain domain = (Domain)state.getValue(m_domain);
        DomainCollection terms = domain.getTerms();

        terms.addPath(Term.MODEL + "." + ACSObject.ID);
        terms.addPath(Term.MODEL + "." + ACSObject.OBJECT_TYPE);
        terms.addPath(Term.MODEL + "." + ACSObject.DEFAULT_DOMAIN_CLASS);
        terms.addPath(Term.MODEL + "." + Category.NAME);
        terms.addPath(Term.MODEL + "." + Category.DESCRIPTION);
        terms.addOrder(Term.UNIQUE_ID);
        
        return terms;
    }

}
