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

package com.arsdigita.atoz;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Filter;

import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import java.util.List;
import java.util.ArrayList;

/**
 * 
 * @author pb
 */
public class AtoZCategoryGenerator extends AbstractAtoZGenerator {

    public AtoZCategoryGenerator(AtoZCategoryProvider provider) {
        super(provider);
    }

    public AtoZEntry[] getEntries(String letter) {
        AtoZCategoryProvider provider = (AtoZCategoryProvider) getProvider();

        DataQuery entries = provider.getAtomicEntries();
        Filter f = entries.addFilter("sortKey like :sortKey");
        f.set("sortKey", letter.toLowerCase() + "%");
        entries.addOrder("sortKey");

        List l = new ArrayList();
        while (entries.next()) {
            l.add(new AtoZCategoryAtomicEntry(new OID((String) entries
                    .get("objectType"), entries.get("id")), (String) entries
                    .get("title"), (String) entries.get("description")));
        }

        return (AtoZEntry[]) l.toArray(new AtoZEntry[l.size()]);
    }

    private class AtoZCategoryAtomicEntry implements AtoZAtomicEntry {
        private OID m_oid;

        private String m_title;

        private String m_description;

        public AtoZCategoryAtomicEntry(OID oid, String title, String description) {
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

            URL here = Web.getContext().getRequestURL();

            return (new URL(here.getScheme(), here.getServerName(), here
                    .getServerPort(), "", "", "/redirect/", map)).toString();
        }

        public Element getContent() {
            // empty
            return null;
        }
    }
}
