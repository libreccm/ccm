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
import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

public abstract class AbstractAssignedTerms extends SimpleComponent {

    private static final String XML_NS = "http://xmlns.redhat.com/london/terms/1.0";
	
    public void generateXML(PageState state,
                            Element parent) {
        Element content = parent.newChildElement("terms:assignedTerms",
                                                 XML_NS);

        ACSObject object = getObject(state);


        //Do nothing if object does not exist.avoid NPE and hence make this
        //class's XML generation optional.
        if (null == object) {
            return;
        }

        DataCollection terms = SessionManager.getSession()
            .retrieve(Term.BASE_DATA_OBJECT_TYPE);
        
        // join with domain first, to speed up the query
        terms.addPath(Term.DOMAIN + "." + Domain.URL);
        terms.addPath(Term.DOMAIN + "." + Domain.KEY);
        terms.addPath(Term.MODEL + "." + Category.NAME);
        terms.addEqualsFilter("model.childObjects.id", object.getID());

        while (terms.next()) {
            DataObject dobj = terms.getDataObject();

            Element t = content.newChildElement("terms:term",
                                                XML_NS);
            t.addAttribute("id", XML.format(dobj.get(Term.UNIQUE_ID)));
            /* XXX persistence bug
            t.addAttribute("name", (String)model.get(Term.MODEL + "." +
                                                     Category.NAME));
            t.addAttribute("domain", (String)dobj.get(Term.DOMAIN + "." + 
                                                      Domain.KEY));
            t.addAttribute("url", (String)dobj.get(Term.DOMAIN + "." + 
                                                   Domain.URL));
            */
            DataObject model = (DataObject)dobj.get(Term.MODEL);
            DataObject domain = (DataObject)dobj.get(Term.DOMAIN);
            
            t.addAttribute("name", (String)model.get(Category.NAME));
            t.addAttribute("domain", (String)domain.get(Domain.KEY));
            t.addAttribute("url", (String)domain.get(Domain.URL));

        }
    }

    protected abstract ACSObject getObject(PageState state);
}
