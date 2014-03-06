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

package com.arsdigita.london.atoz;

import com.arsdigita.atoz.AtoZGeneratorAbstractImpl;
import com.arsdigita.atoz.AtoZAtomicEntry;
import com.arsdigita.atoz.AtoZEntry;
import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.Term;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import java.util.List;
import java.util.ArrayList;

/**
 * 
 * 
 */
public class DomainGenerator extends AtoZGeneratorAbstractImpl {

    /**
     * Constructor
     * @param provider 
     */
    public DomainGenerator(DomainProvider provider) {
        super(provider);
    }

    /**
     * 
     * @param letter
     * @return 
     */
    public AtoZEntry[] getEntries(String letter) {

        DomainProvider provider = (DomainProvider) getProvider();

        Domain domain = provider.getDomain();
        DomainCollection terms = domain.getTerms();
        terms.addPath(Term.MODEL + "." + Category.ID);
        terms.addPath(Term.MODEL + "." + Category.OBJECT_TYPE);
        terms.addPath(Term.MODEL + "." + Category.DESCRIPTION);
        terms.addEqualsFilter(Term.IN_ATOZ, Boolean.TRUE);
        Filter f = terms.addFilter("lower(" + Term.MODEL + "." + Category.NAME
                + ") like :key");
        f.set("key", letter.toLowerCase() + "%");
        terms.addOrder(Term.MODEL + "." + Category.NAME);

        List l = new ArrayList();
        while (terms.next()) {
            Term term = (Term) terms.getDomainObject();
            DataObject cat = (DataObject) terms.get(Term.MODEL);

            l
                    .add(new DomainAtomicEntry(cat.getOID(), (String) cat
                            .get(Category.NAME), (String) cat
                            .get(Category.DESCRIPTION)));
        }

        return (AtoZEntry[]) l.toArray(new AtoZEntry[l.size()]);

    }

    /**
     * 
     */
    private class DomainAtomicEntry implements AtoZAtomicEntry {

        private OID m_oid;
        private String m_title;
        private String m_description;

        /**
         * 
         * @param oid
         * @param title
         * @param description 
         */
        public DomainAtomicEntry(OID oid, String title, String description) {
            m_oid = oid;
            m_title = title;
            m_description = description;
        }

        public String getTitle() {
            return m_title;
        }

        public String getDescription() {
            return m_description;
        }

        public String getLink() {
            ParameterMap map = new ParameterMap();
            map.setParameter("oid", m_oid.toString());

            URL here = Web.getWebContext().getRequestURL();

            return (new URL(here.getScheme(), here.getServerName(), here
                    .getServerPort(), "", "", "/redirect/", map)).toString();
        }

        public Element getContent() {
            // empty
            return null;
        }
    }
}
