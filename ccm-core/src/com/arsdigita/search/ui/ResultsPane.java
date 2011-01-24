/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.search.ui;

import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.IntegerParameter;

import com.arsdigita.kernel.Party;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import com.arsdigita.search.Search;
import com.arsdigita.search.QuerySpecification;
import com.arsdigita.search.Document;
import com.arsdigita.search.ResultSet;

import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.Web;

import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class ResultsPane extends SimpleComponent {

    private static final Logger s_log = Logger.getLogger(ResultsPane.class);
    public static final int PAGE_SIZE = 10;
    private int m_pageSize = PAGE_SIZE;
    private String m_engine;
    private QueryGenerator m_query;
    private IntegerParameter m_pageNumber;
    private boolean m_relative;

    public ResultsPane(QueryGenerator query) {
        this(query, null);
    }

    /**
     *  Determines whether the links to the search results will be
     * relative or absolute.  The default is absolute.
     */
    public void setRelativeURLs(boolean relative) {
        m_relative = relative;
    }

    public ResultsPane(QueryGenerator query,
            String engine) {
        m_query = query;
        m_engine = engine;
        m_pageNumber = new IntegerParameter("page");
        m_relative = false;
    }

    @Override
    public void generateXML(PageState state, Element parent) {
        if (!m_query.hasQuery(state)) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No query available, skipping XMl generation");
            }
            return;
        }

        QuerySpecification spec = m_query.getQuerySpecification(state);
        ResultSet resultSet = null;
        try {
            resultSet = m_engine == null
                    ? Search.process(spec)
                    : Search.process(spec, Search.DEFAULT_RESULT_CACHE, m_engine);

            if (s_log.isDebugEnabled()) {
                s_log.debug("Got result set " + resultSet.getClass()
                        + " count: " + resultSet.getCount());
            }

            if (resultSet.getCount() > 0) {

                Integer page = (Integer) m_pageNumber.transformValue(state.getRequest());
                int pageNumber = (page == null ? 1 : page.intValue());
                long objectCount = resultSet.getCount();
                int pageCount = (int) Math.ceil((double) objectCount / (double) m_pageSize);

                if (pageNumber < 1) {
                    pageNumber = 1;
                }

                if (pageNumber > pageCount) {
                    pageNumber = (pageCount == 0 ? 1 : pageCount);
                }

                long begin = ((pageNumber - 1) * m_pageSize);
                int count = (int) Math.min(m_pageSize, (objectCount - begin));
                long end = begin + count;

                Iterator results = resultSet.getDocuments(begin, count);

                Element content = Search.newElement("results");
                exportAttributes(content);

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Paginator stats\n  page number:" + pageNumber
                            + "\n  page count: " + pageCount + "\n  page size: "
                            + m_pageSize + "\n start " + begin + "\n  end: "
                            + end + "\n count: " + objectCount);
                }

                content.addContent(generatePaginatorXML(state,
                        m_pageNumber.getName(),
                        pageNumber, pageCount,
                        m_pageSize, begin, end,
                        objectCount));
                content.addContent(generateDocumentsXML(state, results));

                parent.addContent(content);
            } else {
                // No search result, so we don't need a paginator, but we want
                // to inform the user, that there are no results for this search
                Element content = Search.newElement("results");
                Element info = content.newChildElement("info");
//                info.setText(GlobalizationUtil.globalize("cms.ui.search_no_results").localize().toString());
                info.setText("Sorry. Your search returned 0 results.");
                parent.addContent(content);
            }

        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e) {
                    /*
                     * If there is a problem closing the result set this probably means
                     * it has been closed elsewhere and is probably not fatal.   We write
                     * a line to the error log but otherwise ignore the exception allowing
                     * the code to continue normally.  Any issues willemerge in the log.
                     */
                    s_log.error("Error closing resultset: " + e.getMessage());
                }
            }
        }
    }

    protected Element generatePaginatorXML(PageState state,
            String pageParam,
            int pageNumber,
            int pageCount,
            int pageSize,
            long begin,
            long end,
            long objectCount) {
        Element paginator = Search.newElement("paginator");
        URL url = Web.getContext().getRequestURL();

        ParameterMap map = new ParameterMap();
        Iterator current = url.getParameterMap().keySet().iterator();
        while (current.hasNext()) {
            String key = (String) current.next();
            if (key.equals(pageParam)) {
                continue;
            }
            map.setParameterValues(key, url.getParameterValues(key));
        }

        paginator.addAttribute("pageParam", m_pageNumber.getName());
        paginator.addAttribute("baseURL", URL.there(url.getPathInfo(), map).toString());
        paginator.addAttribute("pageNumber", XML.format(new Integer(pageNumber)));
        paginator.addAttribute("pageCount", XML.format(new Integer(pageCount)));
        paginator.addAttribute("pageSize", XML.format(new Integer(pageSize)));
        paginator.addAttribute("objectBegin", XML.format(new Long(begin + 1)));
        paginator.addAttribute("objectEnd", XML.format(new Long(end)));
        paginator.addAttribute("objectCount", XML.format(new Long(objectCount)));
        return paginator;
    }

    protected Element generateDocumentsXML(PageState state,
            Iterator results) {
        Element documents = Search.newElement("documents");

        if (s_log.isDebugEnabled()) {
            s_log.debug("Outputting documents");
        }
        while (results.hasNext()) {
            Document doc = (Document) results.next();
            if (s_log.isDebugEnabled()) {
                s_log.debug("One doc " + doc.getOID() + " " + doc.getTitle());
            }
            documents.addContent(generateDocumentXML(state, doc));
        }

        return documents;
    }

    protected Element generateDocumentXML(PageState state,
            Document doc) {
        Element entry = Search.newElement("object");

        String summary = doc.getSummary();

        java.net.URL url = doc.getURL();

        entry.addAttribute("oid", XML.format(doc.getOID()));
        entry.addAttribute("url", XML.format(m_relative ? url.getPath() + "?" + url.getQuery() : url.toString()));
        entry.addAttribute("score", XML.format(doc.getScore()));
        entry.addAttribute("title", XML.format(doc.getTitle()));
        if (summary != null) {
            entry.addAttribute("summary", XML.format(summary));
        }

        entry.addAttribute("locale", XML.format(doc.getLocale()));

        Date creationDate = doc.getCreationDate();
        if (creationDate != null) {
            entry.addAttribute("creationDate", XML.format(creationDate.toString()));
        }
        Party creationParty = doc.getCreationParty();
        if (creationParty != null) {
            entry.addAttribute("creationParty",
                    XML.format(creationParty.getDisplayName()));
        }

        Date lastModifiedDate = doc.getLastModifiedDate();
        if (lastModifiedDate != null) {
            entry.addAttribute("lastModifiedDate",
                    XML.format(lastModifiedDate));
        }
        Party lastModifiedParty = doc.getLastModifiedParty();
        if (lastModifiedParty != null) {
            entry.addAttribute("lastModifiedParty",
                    XML.format(lastModifiedParty.getDisplayName()));
        }

        s_log.debug("about to add the contentSectionName from search index Doc to search result xml");
        entry.addAttribute("contentSectionName", XML.format(doc.getContentSection()));

        return entry;
    }
}
