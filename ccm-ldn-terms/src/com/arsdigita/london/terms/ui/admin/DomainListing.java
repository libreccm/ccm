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
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.london.util.ui.AbstractDomainObjectList;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.london.util.ui.event.DomainObjectActionEvent;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Terms;
import com.arsdigita.xml.XML;
import com.arsdigita.xml.Element;

public class DomainListing extends AbstractDomainObjectList {

    private DomainObjectParameter m_domain;

    public static final String ACTION_VIEW = "view";

    public DomainListing(DomainObjectParameter domain) {
        super("domainListing",
              Terms.XML_PREFIX,
              Terms.XML_NS);
        setRedirecting(true);

        m_domain = domain;
        
        registerDomainObjectAction(ACTION_VIEW);
        
        addDomainObjectActionListener(
            ACTION_VIEW,
            new DomainObjectActionListener() {
                public void actionPerformed(DomainObjectActionEvent e) {
                    e.getPageState().setValue(m_domain,
                                              e.getObject());
                }
            });
    }

    protected Element generateObjectXML(PageState state,
                                        DomainObject dobj) {
        Domain domain = (Domain)state.getValue(m_domain);
        Element el = super.generateObjectXML(state, dobj);
        if (dobj.equals(domain)) {
            el.addAttribute("isSelected", XML.format(Boolean.TRUE));
        }
        return el;
    }

    protected DomainCollection getDomainObjects(PageState state) {
        DataCollection objs = SessionManager.getSession().retrieve
            (Domain.BASE_DATA_OBJECT_TYPE);
        objs.addOrder(Domain.TITLE);

        return new DomainCollection(objs);
    }

}
