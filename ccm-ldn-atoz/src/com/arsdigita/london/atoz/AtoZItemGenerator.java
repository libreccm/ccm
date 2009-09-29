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

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AtoZItemGenerator extends AbstractAtoZGenerator {

    public AtoZItemGenerator(AtoZItemProvider provider) {
        super(provider);
    }

    public AtoZEntry[] getEntries(String letter) {
        AtoZItemProvider provider = (AtoZItemProvider) getProvider();

        DataQuery entries = provider.getAtomicEntries();
        Filter f = entries.addFilter("sortKey like :sortKey");
        f.set("sortKey", letter.toLowerCase() + "%");
        entries.addOrder("sortKey");

        List l = new ArrayList();
        ContentBundle bundle;
        ContentItem item;
        ContentItem live;
        String description;
        String title;

        while (entries.next()) {
            bundle = new ContentBundle(new BigDecimal(entries.get("id")
                    .toString()));
            if (bundle != null) {
                /* Fix by Quasimodo*/
                /* getPrimaryInstance doesn't negotiate the language of the content item */
                /* item = bundle.getPrimaryInstance(); */
                item = bundle.negotiate(DispatcherHelper.getRequest().getLocales());
                
                if (item != null) {
                    // this is necessary because aliases refer to the non-live
                    // version,
                    // while straight items refer to the live-version (to avoid
                    // duplicates)
                    live = item.getLiveVersion();
                    if (live != null) {
                        // should always be a ContentPage
                        description = (live instanceof ContentPage) ? ((ContentPage) live)
                                .getSearchSummary()
                                : live.getName();
                        title = "";
                        if (entries.get("aliasTitle") != null) {
                            title = entries.get("aliasTitle").toString();
                        }
                        if (title.equals("")) {
                            title = live.getDisplayName();
                        }
                        l.add(new AtoZItemAtomicEntry(live.getOID(), title,
                                description));
                    }
                }
            }
        }

        return (AtoZEntry[]) l.toArray(new AtoZEntry[l.size()]);
    }

    private class AtoZItemAtomicEntry implements AtoZAtomicEntry {
        private OID m_oid;

        private String m_title;

        private String m_description;

        public AtoZItemAtomicEntry(OID oid, String title, String description) {
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
