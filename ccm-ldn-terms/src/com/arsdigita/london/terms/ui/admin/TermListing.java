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

import com.arsdigita.bebop.Component;
import com.arsdigita.london.terms.Terms;
import com.arsdigita.london.util.ui.ActionLink;
import com.arsdigita.london.util.ui.ModalContainer;
import com.arsdigita.london.util.ui.event.DomainObjectActionListener;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;

public class TermListing extends ModalContainer {

    private ActionLink m_showAll;
    private ActionLink m_showRoot;
    private AllTermListing m_allTermListing;
    private RootTermListing m_rootTermListing;
    private OrphanedTermListing m_orphanedTermListing;

    public static final String MODE_SHOW_ALL = "showAll";
    public static final String MODE_SHOW_ROOT = "showRoot";

    public TermListing(DomainObjectParameter domain,
                       DomainObjectParameter term) {
        super(Terms.XML_PREFIX + ":termListing",
              Terms.XML_NS);

        m_showRoot = new ActionLink("Show root terms");
        m_showRoot.addActionListener(
            new ModeChangeListener(MODE_SHOW_ROOT));
        m_showAll = new ActionLink("Show all terms");
        m_showAll.addActionListener(
            new ModeChangeListener(MODE_SHOW_ALL));

        m_allTermListing = new AllTermListing(domain, term);
        m_rootTermListing = new RootTermListing(domain, term);
        m_orphanedTermListing = new OrphanedTermListing(domain, term);
        
        add(m_allTermListing);
        add(m_rootTermListing);
        add(m_orphanedTermListing);
        add(m_showRoot);
        add(m_showAll);
        
        registerMode(MODE_SHOW_ALL,
                     new Component[] {
                         m_allTermListing,
                         m_showRoot
                     });
        registerMode(MODE_SHOW_ROOT,
                     new Component[] {
                         m_rootTermListing,
                         m_orphanedTermListing,
                         m_showAll
                     });
        
        setDefaultMode(MODE_SHOW_ROOT);
    }
    
    public void addDomainObjectActionListener(String name,
                                              DomainObjectActionListener l) {
        m_allTermListing.addDomainObjectActionListener(name, l);
        m_rootTermListing.addDomainObjectActionListener(name, l);
        m_orphanedTermListing.addDomainObjectActionListener(name, l);
    }
}
