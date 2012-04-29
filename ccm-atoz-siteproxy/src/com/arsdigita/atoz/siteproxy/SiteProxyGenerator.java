/*
 * Copyright (C) 2005 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.atoz.siteproxy;

import com.arsdigita.atoz.AtoZGeneratorAbstractImpl;
import com.arsdigita.atoz.AtoZAtomicEntry;
import com.arsdigita.atoz.AtoZCompoundEntry;
import com.arsdigita.atoz.AtoZEntry;
import com.arsdigita.atoz.AtoZProvider;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.contenttypes.SiteProxy;
import com.arsdigita.cms.dispatcher.SiteProxyPanel;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.url.URLData;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * 
 */
public class SiteProxyGenerator extends AtoZGeneratorAbstractImpl {

    /**
     * Compound Entry for matched Categories
     * 
     */
    private class AtoZCategoriesCompoundEntry implements AtoZCompoundEntry {

        private List entries = new ArrayList();

        private String m_title;

        private String m_description;

        /**
         * 
         */
        public AtoZCategoriesCompoundEntry(String title, String description) {
            m_title = title;
            m_description = description;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.arsdigita.atoz.AtoZCompoundEntry#getEntries()
         */
        public AtoZEntry[] getEntries() {
            return (AtoZEntry[]) entries.toArray(new AtoZEntry[entries.size()]);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.arsdigita.atoz.AtoZEntry#getTitle()
         */
        public String getTitle() {
            return m_title;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.arsdigita.atoz.AtoZEntry#getDescription()
         */
        public String getDescription() {
            return m_description;
        }

    }

    /**
     * 
     */
    private class SiteProxyAtomicEntry implements AtoZAtomicEntry {
        
        private static final String SITE_PROXY_PANEL_NAME = "cms:siteProxyPanel";

        private OID m_oid;

        private String m_title;

        private String m_url;

        /**
         * Constructor
         * 
         * @param oid
         * @param title
         * @param url 
         */
        public SiteProxyAtomicEntry(OID oid, String title, String url) {
            m_oid = oid;
            m_title = title;
            m_url = url;
        }

        public String getTitle() {
            return m_title;
        }

        public String getDescription() {
            return null;
        }

        public String getLink() {
            ParameterMap map = new ParameterMap();
            map.setParameter("oid", m_oid.toString());

            URL here = Web.getContext().getRequestURL();

            return (new URL(here.getScheme(), here.getServerName(), here
                    .getServerPort(), "", "", "/redirect/", map)).toString();
        }

        public Element getContent() {
            if (m_url == null)
                return null;

            Element child = new Element(
                                    SiteProxyAtomicEntry.SITE_PROXY_PANEL_NAME,
                                    CMS.CMS_XML_NS);
            child.addAttribute("title", m_title);
            child.addAttribute("oid", m_oid.toString());

            URLData data = SiteProxyPanel.internalGetRemoteXML(child,
                                                               this.m_url);

            /* check for data and exception */
            if (data == null)
                return null;
            if (data.getException() != null)
                return null;

            return child;
        }
    }

    public SiteProxyGenerator(AtoZProvider provider) {
        super(provider);
    }

    public AtoZEntry[] getEntries(String letter) {
        SiteProxyProvider siteProxyProvider = (SiteProxyProvider) getProvider();

        DataQuery entries = siteProxyProvider.getAtomicEntries(letter);

        List list = new ArrayList();
        /* init previousCatId */
        BigDecimal previousCatId = new BigDecimal(-1);
        /* watch categoryID for changes */

        AtoZCategoriesCompoundEntry compoundEntry = null;
        while (entries.next()) {
            /* on category change add previous compoundEntry and create new one */
            if (previousCatId.compareTo( (BigDecimal) entries.get("categoryId")) != 0) {
                if ((compoundEntry != null)
                        && (compoundEntry.entries.size() > 0))
                    list.add(compoundEntry);

                /* create compound entry */
                compoundEntry = new AtoZCategoriesCompoundEntry(
                        (String) entries.get("categoryTitle"), (String) entries
                                .get("categoryDescription"));
                /* assign current categoryId to previousCatId */
                previousCatId = (BigDecimal) entries.get("categoryId");
            }

            /* create atomic entry */
            SiteProxyAtomicEntry atomicEntry = new SiteProxyAtomicEntry(
                    new OID(SiteProxy.BASE_DATA_OBJECT_TYPE, entries.get("id")),
                    (String) entries.get("title"), (String) entries.get("url"));

            /* add it to coumpoundEntry if siteProxy content is not null */
            if (atomicEntry.getContent() != null)
                compoundEntry.entries.add(atomicEntry);
        }
        /* finally add compoundEntry if exist and not empty */
        if ((compoundEntry != null) && (compoundEntry.entries.size() > 0))
            list.add(compoundEntry);

        return (AtoZEntry[]) list.toArray(new AtoZEntry[list.size()]);
    }
}
