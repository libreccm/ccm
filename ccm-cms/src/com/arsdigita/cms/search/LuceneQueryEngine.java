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
package com.arsdigita.cms.search;

import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.search.ContentTypeFilterSpecification;
import com.arsdigita.cms.search.ContentTypeFilterType;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.FilterType;
import com.arsdigita.search.filters.DateRangeFilterSpecification;
import com.arsdigita.search.filters.PartyFilterSpecification;
import com.arsdigita.search.lucene.BaseQueryEngine;
import com.arsdigita.search.lucene.Document;
import com.arsdigita.search.lucene.ObjectTypeFilter;
import com.arsdigita.search.lucene.PartyFilter;
import com.arsdigita.search.lucene.SqlFilter;
import com.arsdigita.search.lucene.TypeSpecificFilter;
import com.arsdigita.search.lucene.UnionFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.lucene.search.DateFilter;
import org.apache.lucene.search.Filter;


public class LuceneQueryEngine extends BaseQueryEngine {

    protected void addFilter(List list, SqlFilter sql,
                             FilterSpecification filter) {
        super.addFilter(list, sql, filter);

        FilterType type = filter.getType();

        if (ContentTypeFilterType.KEY.equals(type.getKey())) {
           addContentTypeFilter(list, (ContentTypeFilterSpecification)filter);
        } else if (VersionFilterType.KEY.equals(type.getKey())) {
           addVersionFilter(list, (VersionFilterSpecification)filter);
        } else if (LastModifiedDateFilterType.KEY.equals(type.getKey())) {
            addDateRangeFilter(list,
                               (DateRangeFilterSpecification)filter,
                               Document.LAST_MODIFIED_DATE);
        } else if (CreationDateFilterType.KEY.equals(type.getKey())) {
            addDateRangeFilter(list,
                               (DateRangeFilterSpecification)filter,
                               Document.CREATION_DATE);
        } else if (LastModifiedUserFilterType.KEY.equals(type.getKey())) {
            addPartyFilter(list, (PartyFilterSpecification)filter,
                           Document.LAST_MODIFIED_PARTY);
        } else if (CreationUserFilterType.KEY.equals(type.getKey())) {
            addPartyFilter(list, (PartyFilterSpecification)filter,
                           Document.CREATION_PARTY);
        } else if (CMSContentSectionFilterType.KEY.equals(type.getKey())) {
            addCMSContentSectionFilter(list, sql, (CMSContentSectionFilterSpecification)filter);
        }
    }

    protected void addCMSContentSectionFilter(List list, SqlFilter sql,
                                              CMSContentSectionFilterSpecification filter) {
        if (filter.getSections() == null) {
            return;
        }
        com.arsdigita.persistence.Filter f = null;
        if (filter.isExclusion()) {
            f = sql.getFilterFactory().notIn("id", "com.arsdigita.cms.getContentSectionItems");
        } else {
            f = sql.getFilterFactory().in("id", "com.arsdigita.cms.getContentSectionItems");
        }
        f.set("sectionName", Arrays.asList(filter.getSections()));
        sql.appendClause(f);
    }

    protected void addDateRangeFilter(List list,
                                      DateRangeFilterSpecification filter,
                                      String paramName) {
        Date startDate = filter.getStartDate();
        Date endDate = filter.getEndDate();
        if (startDate != null && endDate != null) {
            list.add(new DateFilter(paramName, startDate, endDate));
        } else if (startDate != null) {
            list.add(DateFilter.After(paramName, startDate));
        } else if (endDate != null) {
            list.add(DateFilter.Before(paramName, startDate));
        }
    }

    protected void addPartyFilter(List list,
                                  PartyFilterSpecification filter,
                                  String filterType) {
        PartyCollection parties = filter.getParties();
        if (parties == null) {
            return;
        }
        List filters = new ArrayList();
        int count = 0;
        while (parties.next()) {
            filters.add(new PartyFilter(parties.getID(), filterType));
            count++;
        }
        list.add(new UnionFilter((Filter[])filters.toArray(new Filter[count])));
    }


    protected void addVersionFilter(List list,
                                    VersionFilterSpecification filter) {
        list.add(new TypeSpecificFilter(filter.getVersion()));
    }

    protected void addContentTypeFilter(List list,
                                        ContentTypeFilterSpecification filter) {
        List l = new ArrayList();
        ContentType[] types = filter.getTypes();
        if (types == null || types.length == 0) {
            return;
        }

        Filter[] filters = new Filter[types.length];
        for (int i = 0 ; i < types.length ; i++) {
            filters[i] = new ObjectTypeFilter(types[i].getAssociatedObjectType());
        }
        list.add(new UnionFilter(filters));
    }

    protected String getCategorisationQuery() {
        return "com.arsdigita.cms.searchCategoryObjects";
    }

}
