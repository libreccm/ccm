/* -*- mode: java; c-basic-offset: 4; indent-tabs-mode: nil -*- */

package com.arsdigita.cms.docmgr.search;

//import com.arsdigita.cms.docmgr.search.dispatcher.Dispatcher;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import com.arsdigita.kernel.User;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.search.intermedia.SearchDataQuery;
import com.arsdigita.search.intermedia.SearchSpecification;
import com.arsdigita.search.intermedia.SimpleSearchSpecification;
import com.arsdigita.util.StringUtils;

/**
 * <p>
 * Implement the Intermedia-based simple and advanced searches for the site.
 * </p>
 */
public class IntermediaSearcher implements Searcher
{
    protected static final String[] m_columns = {
        "object_id",
        "object_type",
        "summary",
        "link_text",
        "url_stub",
        "score"
    };

    private static final org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(IntermediaSearcher.class);

    private static SimpleDateFormat lastModifiedDateFormatter =
        new SimpleDateFormat("yyyy MM dd HH:mm");

    public SearchResults simpleSearch (String terms, User user)
    {
        String cleaned = cleanUpSearchString (terms, " and ");
        String searchString = createSearchString
            (null, null, cleaned, null, null, null, null, null);
        SearchDataQuery query = createSimpleQuery (searchString, user);
        return new IntermediaSearchResults (query);
    }

    protected SearchDataQuery createSimpleQuery (String searchString,
                                                 User user)
    {
        Session session = SessionManager.getSession();
        SearchDataQuery q
            = new SearchDataQuery (session, searchString, m_columns);
        // NOTE: simple search includes spidered content
        //return addLivePublicFilters (q, user, true);
        return q;
    }

    /* Note: this method searches only the Document content
       type to acheive filtering by last modified date.
     */
    public SearchResults advancedSearch (String terms,
                                         String author,
                                         String mimeType,
                                         BigDecimal workspaceID,
                                         Date lastModifiedStartDate,
                                         Date lastModifiedEndDate,                                         
                                         String[] types,
                                         String[] sections,
                                         User user,
                                         Collection categoryIDs)
    {
        String cleanedTerms = cleanUpSearchString (terms, " and ");
        String cleanedAuthor = cleanUpSearchString (author, " ");
        String cleanedMimeType = cleanUpSearchString (mimeType, " ");
        String searchString = createSearchString
            (types, sections, cleanedTerms, cleanedAuthor, 
             cleanedMimeType, workspaceID, 
             lastModifiedStartDate, lastModifiedEndDate);
        SearchDataQuery query
            = createAdvancedQuery (searchString, user, categoryIDs);
        
        return new IntermediaSearchResults (query);
    }

    protected SearchDataQuery createAdvancedQuery (String searchString,
                                                   User user,
                                                   Collection categoryIDs)
    {
        SearchSpecification spec =
            new SearchSpecification( searchString, m_columns);

        SearchDataQuery query = (SearchDataQuery) spec.getPage(1);
        // NOTE: advanced search DOES NOT include spidered content
        //SearchDataQuery lpq = addLivePublicFilters (query, user, false);
        SearchDataQuery lpq = query;

        if (categoryIDs != null && ! categoryIDs.isEmpty())
            {
                Filter catFilter
                    = lpq.addInSubqueryFilter
                    (SearchDataQuery.OBJECT_ID,
                     "com.arsdigita.categorization.cmsIDsInMultipleSubtrees");
                catFilter.set( "categoryIDs", categoryIDs );
                Iterator i = categoryIDs.iterator();
                while(i.hasNext()) {
                    s_log.debug("categoryIDs: " +i.next().toString());
                }
            }
        s_log.debug("lpq "+lpq.toString());

        return lpq;
    }

    protected String cleanUpSearchString (String terms, String joiner)
    {
        return SimpleSearchSpecification.cleanSearchString (terms, joiner);
    }

    //protected SearchDataQuery
    //    addLivePublicFilters (SearchDataQuery query, User user, 
    //                          boolean includeSpideredContent)
    //{
    //    
    //    FilterFactory ff = SessionManager.getSession().getFilterFactory();
    //    Filter searchQueryFilter = null;
    //    
    //    CompoundFilter liveAndPermFilter = ff.and();
    //    liveAndPermFilter
    //        .addFilter(ff.in(SearchDataQuery.OBJECT_ID,
    //                         "com.arsdigita.cms.docmgr.search.LiveItems"));
    //
    //    liveAndPermFilter
    //        .addFilter(PermissionService.getFilterQuery
    //                   (ff,
    //                    SearchDataQuery.OBJECT_ID,
    //                    PrivilegeDescriptor.get
    //                    (com.arsdigita.cms.SecurityManager.CMS_READ_ITEM),
    //                    user == null ? null : user.getOID()));
    //    
    //
    //    if (includeSpideredContent) {
    //        /* When search should include spidered content,
    //         * add an OR filter which looks for IDs of spidered stuff.
    //         */
    //        CompoundFilter temp = ff.or();
    //        // items have to be LIVE and readable CMS items
    //        temp.addFilter(liveAndPermFilter);
    //        // *OR* they have to be SpideredContent
    //        temp.
    //            addFilter(ff.in(SearchDataQuery.OBJECT_ID,
    //                            "com.arsdigita.cms.docmgr.search.spider.IncludeSpideredContent"));
    //        searchQueryFilter = temp;
    //    }
    //    else {
    //        /* When search should NOT include spidered content
    //         * just use the "live items and permissions filter" as-is
    //         */
    //        searchQueryFilter = liveAndPermFilter;
    //    }
    //
    //    query.addFilter( searchQueryFilter );
    //
    //    return query;
    //}


    protected String createSearchString (String[] types,
                                         String[] sections,
                                         String searchString,
                                         String author,
                                         String mimeType,
                                         BigDecimal workspaceID,
                                         Date lastModifiedStartDate,
                                         Date lastModifiedEndDate)
    {
        Vector from = new Vector();
        Vector where_clause = new Vector();

        String s
            = "select c.object_id, c.object_type," 
            + " c.link_text, c.url_stub, c.summary ";
        from.add ("search_content c");

        if (searchString != null && searchString.length() > 0) {
            s = s + ", (score(1)+score(2)) as score ";

            where_clause.add (SimpleSearchSpecification.containsClause
                          ("c", searchString, "1", "2"));
        } else {
            s = s + ", 1 as score ";
        }

        if(author != null && author.length() > 0) {
            where_clause.add("contains(c.xml_content,"+
                             quote(author+" within author")+
                             ",3) > 0");
        }
        if(mimeType != null && mimeType.length() > 0) {
            where_clause.add("contains(c.xml_content,"+
                             quote(mimeType+" within mimeType")+
                             ",4) > 0");
        }
        if(workspaceID != null) {
            where_clause.add("contains(c.xml_content,"+
                             quote("\""+workspaceID.toString()+
                                   "\" within workspace")+
                             ",5) > 0");
        }

        // START/END DATES
        if (lastModifiedStartDate != null || lastModifiedEndDate != null) {
            // Filtering for all results will now only occur on Documents
            from.add("cms_documents cd");
            where_clause.add("cs.item_id = cd.doc_id");
            if (lastModifiedStartDate != null) {
                where_clause.add
                    ("cd.last_modified_cached > to_date('"+
                     lastModifiedDateFormatter.format(lastModifiedStartDate)
                     +"','YYYY MM DD HH24:MI')");
            }
            if (lastModifiedEndDate != null) {
                where_clause.add
                    ("cd.last_modified_cached < to_date('"+
                     lastModifiedDateFormatter.format(lastModifiedEndDate)
                     +"','YYYY MM DD HH24:MI')");
            }
        }

        // CONTENT TYPES
        if (types != null && types.length > 0) {
            from.add("content_types ct");
            where_clause.add("ct.type_id in ("
                             + StringUtils.join(types, ',')
                             + ")");
            where_clause.add("ct.object_type = c.object_type");
        }

        // CONTENT SECTIONS
        if (sections != null && sections.length > 0) {
            from.add("cms_items cs");
            where_clause.add("cs.item_id = c.object_id");
            where_clause.add("cs.section_id in ("
                             + StringUtils.join(sections, ',')
                             + ")");
        }

        s += " from "
            + StringUtils.join (objectToString (from.toArray()), ',');
        s += " where "
            + StringUtils.join (objectToString (where_clause.toArray()),
                                " and ");
        if (searchString != null && searchString.length() > 0) { 
            s += " order by score desc";
        }

        s_log.debug("Search query: " + s);    

        return s;
    }

    /**
     * Yet another dumb method brought to you by Java
     * @param object An array of Objects that need converting to strings.
     */
    protected String[] objectToString(Object[] objects) {
        String[] results = new String[objects.length];
        for (int i=0; i < objects.length; i++) {
            results[i] = objects[i].toString();
        }
        return results;
    }
    private static String quote (String text) {

        // our special character
        char q = '\'';

        // a string buffer to store the result
        StringBuffer result = new StringBuffer(text.length());
        result.append(q);

        // a character array to convert from
        char[] c = text.toCharArray();

        // expand the internal quotes, copy the rest
        for (int i = 0; i < c.length; i++) {
            if (c[i] == q) {
                result.append(q);
            }
            result.append(c[i]);
        }

        // add the final quote and convert to a String
        return result.append(q).toString();
    }
}
