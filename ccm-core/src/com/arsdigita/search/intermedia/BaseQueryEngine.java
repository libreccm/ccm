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
package com.arsdigita.search.intermedia;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.FilterType;
import com.arsdigita.search.QueryEngine;
import com.arsdigita.search.QuerySpecification;
import com.arsdigita.search.ResultSet;
import com.arsdigita.search.Search;
import com.arsdigita.search.filters.CategoryFilterSpecification;
import com.arsdigita.search.filters.CategoryFilterType;
import com.arsdigita.search.filters.ContentSectionFilterSpecification;
import com.arsdigita.search.filters.ContentSectionFilterType;
import com.arsdigita.search.filters.ObjectTypeFilterSpecification;
import com.arsdigita.search.filters.ObjectTypeFilterType;
import com.arsdigita.search.filters.PermissionFilterSpecification;
import com.arsdigita.search.filters.PermissionFilterType;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;

/**
 * This provides the basic intermedia query engine implementation
 * which can restrict based on category, object type and
 * permissions
 * @see com.arsdigita.search.QueryEngine
 */
public class BaseQueryEngine extends LockableImpl implements QueryEngine {

    private static final Logger s_log =
        Logger.getLogger(BaseQueryEngine.class);

    public static final String OBJECT_ID= "object_id";
    public static final String OBJECT_TYPE = "object_type";
    public static final String SUMMARY = "summary";
    public static final String LINK_TEXT = "link_text";
    public static final String LANGUAGE = "language";
    public static final String SCORE = "score";
    public static final String CONTENT_SECTION = "content_section";

    private Map m_columns;
    private Map m_tables;
    private List m_conditions;

    public BaseQueryEngine() {
        m_columns = new HashMap();
        m_tables = new HashMap();
        m_conditions = new ArrayList();

        // cg - changed order of mapping - ie map alias to field. This allows subclasses to override
       // the definitions of fields ESPECIALLY SCORE by invoking addColumn and using the same alias
       addColumn(OBJECT_ID, "c.object_id");
       addColumn(OBJECT_TYPE, "c.object_type");
       addColumn(LINK_TEXT, "c.link_text");
       addColumn(SUMMARY, "c.summary");
       addColumn(LANGUAGE, "c.language");
       addColumn(CONTENT_SECTION, "c.content_section");
        // XML content is labeled as "1" and raw content is labeled as "2"
        // in buildQueryString().  Those labels must match the arguments
        // to the "score()" operator used here.
       addColumn(SCORE, "((score(1) * " + Search.getConfig().getXMLContentWeight() + ") +" +
       " (score(2) * " + Search.getConfig().getRawContentWeight() + "))");
                                                                    

        addTable("search_content", "c");
    }

    /**
     * Processes a query specification generating a document
     * result set
     * @param spec the query specification
     * @return the document result set
     */
    public ResultSet process(QuerySpecification spec) {
        if (!isLocked()) {
            lock();
        }

        String terms = cleanSearchString(spec.getTerms());

        if (terms == null || "".equals(terms)) {
            return Search.EMPTY_RESULT_SET;
        }

        DataQuery q = buildQuery(terms,
                                 spec.allowPartialMatch());
        addFilters(q, spec.getFilters());



        return new DataQueryResultSet(q);
    }

    protected DataQuery buildQuery(String terms,
                                   boolean partial) {
        List props = new ArrayList();
        String query = buildQueryString(terms,
                                        partial,
                                        props);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Query before adding any filters {\n" + query + "}");
        }

        Session session = SessionManager.getSession();
        SearchDataQuery q
            = new SearchDataQuery(
                session,
                query,
                (String[])props.toArray(new String[props.size()]));
        return q;
    }

    protected String buildQueryString(String terms,
                                      boolean partial,
                                      List props) {
        StringBuffer sb = new StringBuffer("select \n");
        // cg map turned around, so fields are mapped to aliases
        Iterator columns = m_columns.entrySet().iterator();
        while (columns.hasNext()) {
            Map.Entry column = (Map.Entry)columns.next();
            String field = (String)column.getValue();
            String alias = (String)column.getKey();
            props.add(alias);


            sb.append("  " + field + " as " + alias);
            if (columns.hasNext()) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }

        sb.append("from \n");
        Iterator tables = m_tables.keySet().iterator();
        while (tables.hasNext()) {
            String name = (String)tables.next();
            String alias = (String)m_tables.get(name);

            sb.append("  " + name + " " + alias);
            if (tables.hasNext()) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }

        sb.append("where\n");

        Iterator conditions = m_conditions.iterator();
        while (conditions.hasNext()) {
            sb.append("  " + (String)conditions.next() + " and\n");
        }
        sb.append("  " + getContainsClause(terms));
                
        sb.append("order by score desc\n");

        return sb.toString();
    }

    protected void addColumn(String field,
                             String propName) {
        Assert.unlocked(this);
        m_columns.put(field, propName);
    }

    protected void addTable(String table,
                           String alias) {
        Assert.unlocked(this);
        m_tables.put(table, alias);
    }

    protected void addCondition(String condition) {
        Assert.unlocked(this);
        m_conditions.add(condition);
    }

    protected String cleanSearchString(String terms) {
        return SimpleSearchSpecification.cleanSearchString(terms, " and ");
    }

    // cg extracted as a protected method so subclasses may override
    protected String getContainsClause (String terms) {
        return SimpleSearchSpecification.containsClause("c", terms, "1", "2") + "\n";
    }
     
    protected void addFilters(DataQuery query,
                              FilterSpecification[] filters) {
        Assert.locked(this);

        for (int i = 0 ; i < filters.length ; i++) {
            s_log.debug("adding filter " + filters[i]);
            addFilter(query, filters[i]);
        }
    }

    protected void addFilter(DataQuery query,
                             FilterSpecification filter) {
        Assert.locked(this);

        FilterType type = filter.getType();

        if (PermissionFilterType.KEY.equals(type.getKey())) {
            addPermissionFilter(query, (PermissionFilterSpecification)filter);
        } else if (ObjectTypeFilterType.KEY.equals(type.getKey())) {
            addObjectTypeFilter(query, (ObjectTypeFilterSpecification)filter);
        } else if (CategoryFilterType.KEY.equals(type.getKey())) {
            addCategoryFilter(query, (CategoryFilterSpecification)filter);
        } else if (ContentSectionFilterType.KEY.equals(type.getKey())) {
            s_log.debug("adding the ContentSectionFilterSpecification filter");
            addContentSectionFilter(query, (ContentSectionFilterSpecification)filter);
        }
    }

    protected void addPermissionFilter(DataQuery query,
                                       PermissionFilterSpecification filter) {
        Assert.locked(this);

        PermissionService.filterQuery(query,
                                      "object_id",
                                      filter.getPrivilege(),
                                      filter.getParty().getOID());
    }

    protected void addObjectTypeFilter(DataQuery query,
                                       ObjectTypeFilterSpecification filter) {
        Assert.locked(this);

        List l = new ArrayList();
        ObjectType[] types = filter.getTypes();
        if (types == null || types.length == 0) {
            return;
        }

        for (int i = 0 ; i < types.length ; i++) {
            ObjectType type = types[i];
            l.add(type.getQualifiedName());
        }
        if (filter.isExclusion()) {
            Filter f = query.addFilter("object_type not in :types");
            f.set("types", l);
        } else {
            Filter f = query.addFilter("object_type in :types");
            f.set("types", l);
        }
    }

    protected void addCategoryFilter(DataQuery query,
                                     CategoryFilterSpecification filter) {
        Assert.locked(this);

        Category[] categories = filter.getCategories();
        if (categories != null && categories.length > 0) {
            List ids = new ArrayList();
            for (int i = 0 ; i < categories.length ; i++) {
                ids.add(categories[i].getID());
            }

            Filter f = query.addInSubqueryFilter(
                "object_id",
                "id",
                "com.arsdigita.search.categoryObjects");

            f.set("ids", ids);
            f.set("pathLimit", new Integer( filter.isDescending() ? 100 : 0));
        }
    }

    protected void addContentSectionFilter(DataQuery query,
                                           ContentSectionFilterSpecification filter) {
        Assert.locked(this);

        Object[] contentSections = filter.getSections();
        s_log.debug("there are " + contentSections.length + " contentSections");
        if (contentSections != null && contentSections.length > 0) {
            List contentSectionsList = Arrays.asList(contentSections);
            Iterator i = contentSectionsList.iterator();
            StringBuffer filterSql = new StringBuffer();
            while (i.hasNext()) {
                filterSql.append("content_section = '");
                filterSql.append((String)i.next());
                filterSql.append("'");
                if (i.hasNext()) {
                    filterSql.append(" or ");
                }
            }
            s_log.debug("filterSql is " + filterSql.toString());
            query.addFilter(filterSql.toString());
        }
    }

}
