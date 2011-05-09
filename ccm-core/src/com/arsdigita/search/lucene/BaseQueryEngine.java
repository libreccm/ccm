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
package com.arsdigita.search.lucene;

import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.permissions.PermissionManager;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.UniversalPermissionDescriptor;
import com.arsdigita.persistence.OID;
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
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.lucene.search.Filter;

/**
 * This provides the basic lucene query engine implementation
 * which can restrict based on object type and categories.
 * @see com.arsdigita.search.QueryEngine
 */
public class BaseQueryEngine implements QueryEngine {

    private static final Logger s_log = Logger.getLogger(BaseQueryEngine.class);

    /*
     * Processes a query specification generating a document
     * result set
     * @param spec the query specification
     * @return the document result set
     */
    public ResultSet process(QuerySpecification spec) {

        String terms = spec.getTerms();

        if (terms == null || "".equals(terms)) {
            return Search.EMPTY_RESULT_SET;
        }

        s_log.debug("terms are " + terms);

        List filters = new ArrayList();
        addFilters(filters, spec.getFilters());
        s_log.debug("filters size is " + filters.size());
        LuceneSearch search = null;
        if (filters.size() == 0) {
            search = new LuceneSearch(terms);
        } else {
            search = new LuceneSearch(
                terms,
                new IntersectionFilter((Filter[])filters
                                      .toArray(new Filter[filters.size()])));
        }

        return new LuceneResultSet(search);
    }

    protected void addFilters(List list,
                              FilterSpecification[] filters) {
        SqlFilter sql = new SqlFilter();
        for (int i = 0 ; i < filters.length ; i++) {
            addFilter(list, sql, filters[i]);
        }
        if (sql.getNumClauses() > 0) {
            list.add(sql);
        }
    }

    protected void addFilter(List list, SqlFilter sql,
                             FilterSpecification filter) {
        FilterType type = filter.getType();

        if (ObjectTypeFilterType.KEY.equals(type.getKey())) {
            addObjectTypeFilter(list, (ObjectTypeFilterSpecification)filter);
        } else if (ContentSectionFilterType.KEY.equals(type.getKey())) {
            addContentSectionFilter(list, (ContentSectionFilterSpecification)filter);
        } else if (CategoryFilterType.KEY.equals(type.getKey())) {
            addCategoryFilter(sql, (CategoryFilterSpecification) filter);
        } else if (PermissionFilterType.KEY.equals(type.getKey())) {
            addPermissionFilter(sql, (PermissionFilterSpecification) filter);
        }
    }

    protected String getCategorisationQuery() {
        return "com.arsdigita.search.categoryObjects";
    }

    protected void addCategoryFilter(SqlFilter sql,
                                     CategoryFilterSpecification filterSpec) {
        Category[] cats = filterSpec.getCategories();
        if (cats == null  ||  cats.length == 0) {
            return;
        }
        com.arsdigita.persistence.Filter f = sql.appendClause(getCategorisationQuery(), "id");
        List catList = new ArrayList();
        for (int i=0; i<cats.length; i++) {
            catList.add(cats[i].getID());
        }
        f.set("ids", catList);
        // 999999 - just need a number that is grater than max. category
        // tree depth
        f.set("pathLimit", new Integer(filterSpec.isDescending() ? 999999 : 0));
    }

    protected void addPermissionFilter(SqlFilter sql,
                                       PermissionFilterSpecification filterSpec) {
        PrivilegeDescriptor privilege = filterSpec.getPrivilege();
        OID partyOID = filterSpec.getParty().getOID();
        UniversalPermissionDescriptor universalPermission =
            new UniversalPermissionDescriptor(privilege, partyOID);
        if (PermissionService.checkPermission(universalPermission)) {
            return;
        }
        com.arsdigita.persistence.Filter f = sql.getFilterFactory().simple(
              Document.ID + " in ( com.arsdigita.search.partyPermissionFilterStub "
              + " and  RAW[" + privilege.getColumnName() + " = '1' ])");
        f.set("partyID", PermissionManager.constructAccessList(partyOID));
        sql.appendClause(f);
    }

    protected void addObjectTypeFilter(List list,
                                       ObjectTypeFilterSpecification filter) {
        List l = new ArrayList();
        ObjectType[] types = filter.getTypes();
        if (types == null || types.length == 0) {
            return;
        }

        Filter[] filters = new Filter[types.length];
        for (int i = 0 ; i < types.length ; i++) {
            filters[i] = new ObjectTypeFilter(types[i].getQualifiedName());
        }
        if (filter.isExclusion()) {
            list.add(new NegationFilter(new UnionFilter(filters)));
        } else {
            list.add(new UnionFilter(filters));
        }
    }

    protected void addContentSectionFilter(List list,
                                           ContentSectionFilterSpecification filter) {
        List l = new ArrayList();
        Object[] contentSections = filter.getSections();
        if (contentSections == null || contentSections.length == 0) {
            return;
        }
        s_log.debug("Adding content section filter to search");
        Filter[] filters = new Filter[contentSections.length];
        for (int i = 0 ; i < contentSections.length ; i++) {
            s_log.debug("content section filter is " + (String)contentSections[i]);
            filters[i] = new ContentSectionFilter((String)contentSections[i]);
        }
        list.add(new UnionFilter(filters));

//          if (filter.isExclusion()) {
//              list.add(new NegationFilter(new UnionFilter(filters)));
//          } else {
//              list.add(new UnionFilter(filters));
//          }

    }
}

