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

import com.arsdigita.bebop.PageState;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.london.terms.Term;
import com.arsdigita.london.terms.Terms;
import com.arsdigita.london.util.ui.AbstractDomainObjectDetails;
import com.arsdigita.london.util.ui.event.DomainObjectActionEvent;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;

public class TermDetails extends AbstractDomainObjectDetails {

    private DomainObjectParameter m_term;

    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_DELETE = "delete";

    public TermDetails(DomainObjectParameter term) {
        super("termDetails", 
              Terms.XML_PREFIX, 
              Terms.XML_NS);
        setRedirecting(true);

        m_term = term;

        registerDomainObjectAction(ACTION_EDIT);
        registerDomainObjectAction(ACTION_DELETE);

        addDomainObjectActionListener(
            ACTION_DELETE,
            new DomainObjectActionListener() {
                public void actionPerformed(DomainObjectActionEvent e) {
                    e.getPageState().setValue(m_term, null);

                    Term term = (Term)e.getObject();
                    
                    // Orphan the narrower terms before deleting
                    DomainCollection children = term.getNarrowerTerms();
                    while (children.next()) {
                        Term child = (Term)children.getDomainObject();
                        term.removeNarrowerTerm(child);
                    }

                    term.delete();
                }
            });
    }
    
    protected DomainObject getDomainObject(PageState state) {
        return (DomainObject)state.getValue(m_term);
    }
}
