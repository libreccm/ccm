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
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.TermItemCountQuery;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

/**
 * A widget that outputs the term item count summary 
 * for a particular domain. This uses the global state
 * parameter 'key' to determine which domain to use.
 */
public abstract class AbstractTermItemSummary extends SimpleComponent {

    private static final String XML_NS = "http://xmlns.redhat.com/london/terms/1.0";

    protected abstract Domain getDomain(PageState state);

    public void generateXML(PageState state,
                            Element parent) {
        Element content = parent.newChildElement("terms:termItemCountSummary",
                                                 XML_NS);

        Domain domain = getDomain(state);
        TermItemCountQuery summary = domain.getTermItemCountSummary();
        while (summary.next()) {
            Element item = content.newChildElement("terms:term",
                                                   XML_NS);
            item.addAttribute("id", XML.format(summary.getUniqueID()));
            item.addAttribute("name", summary.getName());
            item.addAttribute("count", XML.format(summary.getCount()));
        }
    }
}
