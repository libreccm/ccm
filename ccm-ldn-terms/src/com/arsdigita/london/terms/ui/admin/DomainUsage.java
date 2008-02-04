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

import com.arsdigita.london.util.ui.AbstractDomainObjectList;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.london.util.ui.event.DomainObjectActionEvent;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainCollection;

import com.arsdigita.bebop.PageState;
import com.arsdigita.london.terms.Terms;
import com.arsdigita.london.terms.Domain;

public class DomainUsage extends AbstractDomainObjectList {
    
    public static final String ACTION_REMOVE = "remove";

    private DomainObjectParameter m_domain;
    
    public DomainUsage(DomainObjectParameter domain) {
        super("domainUsage",
              Terms.XML_PREFIX,
              Terms.XML_NS);
        
        m_domain = domain;

        registerDomainObjectAction(ACTION_REMOVE);
        
        addDomainObjectActionListener(
            ACTION_REMOVE,
            new DomainObjectActionListener() {
                public void actionPerformed(DomainObjectActionEvent e) {
                    DomainObject ctx = e.getObject();
                    ctx.delete();
                }
            }
        );
    }
    
    protected DomainCollection getDomainObjects(PageState state) {
        Domain domain = (Domain)state.getValue(m_domain);
        
        DomainCollection ctx = domain.getUseContexts();
        ctx.addOrder("categoryOwner.displayName");
        return ctx;
    }
}
