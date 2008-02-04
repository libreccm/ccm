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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.search.ContentTypeFilterSpecification;
import com.arsdigita.cms.search.ContentTypeFilterType;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.FilterType;
import com.arsdigita.search.Search;
import com.arsdigita.search.filters.CategoryFilterSpecification;
import com.arsdigita.search.filters.DateRangeFilterSpecification;
import com.arsdigita.search.filters.PartyFilterSpecification;
import com.arsdigita.search.intermedia.BaseQueryEngine;


public class IntermediaQueryEngine extends BaseQueryEngine {

    public IntermediaQueryEngine() {
        addColumn("version", "i.version");
        addColumn("launch_date", "p.launch_date");
        addColumn("last_modified", "audited.last_modified" );
        addColumn("modifying_user", "audited.modifying_user" );
        addColumn("creation_date", "audited.creation_date" );
        addColumn("creation_user", "audited.creation_user");
        //  cg column map has been switched round (map fields to aliases)
        // allowing us to override the score field to include keyword/title score
        if (ContentSection.getConfig().scoreKeywordsAndTitle()) {
            addColumn(SCORE, "((score(1) * "  + Search.getConfig().getXMLContentWeight() + ") +"
            + " (score(2) * " + Search.getConfig().getRawContentWeight() + ") +"
            + " (score(3) * " + ContentSection.getConfig().getKeywordSearchWeight() + ") +"
            + " (score(4) * " + ContentSection.getConfig().getTitleSearchWeight() + "))");
        }
                                                                                           


        // XXX: I am going to hell for the line below. I console myself only
        // because this is a hack to work around a terrible interface.
        // This line replaces the existing hash entry for 'search_content' and
        // abuses the alias to add a left outer join.
        // mbooth@redhat.com 02/02/2005
        //
        // amended to include (other) outer joins, otherwise you
        // can't search for anything other than cms items
        // cg - 11/10/2005
        //
        // likewise going to hell.  Seb - 30/11/2005
        addTable( "search_content", "c left outer join cms_pages p on ( p.item_id = c.object_id )"
                     + " left outer join cms_items i on (c.object_id = i.item_id )"
                     + " left outer join acs_auditing audited on (c.object_id = audited.object_id)" );
    }
    
    protected String cleanSearchString(String terms) {
        if (ContentSection.getConfig().scoreKeywordsAndTitle()) {
            return PathSearchSpecification.cleanSearchString(terms, " AND ");
        } else {
            return super.cleanSearchString(terms);
        }
    }
 
    protected String getContainsClause (String terms) {
        if (ContentSection.getConfig().scoreKeywordsAndTitle()) {
            return PathSearchSpecification.containsClause("c", terms, "1", "2", "3", "4") + "\n";
        } else {
            return super.getContainsClause(terms);
        }
    }
                                                                                                                                          
    
    protected void addFilter(DataQuery query,
                             FilterSpecification filter) {
        super.addFilter(query, filter);

        FilterType type = filter.getType();

        if (ContentTypeFilterType.KEY.equals(type.getKey())) {
            addContentTypeFilter(query, (ContentTypeFilterSpecification)filter);
        } else if (VersionFilterType.KEY.equals(type.getKey())) {
            addVersionFilter(query, (VersionFilterSpecification)filter);
        } else if (LaunchDateFilterType.KEY.equals(type.getKey())) {
            //addColumn("p.launch_date", "launch_date");
            //addTable("cms_pages", "p");
            //addCondition("c.object_id = p.item_id");
            addLaunchDateFilter(query, (DateRangeFilterSpecification)filter);
        } else if (LastModifiedDateFilterType.KEY.equals(type.getKey())) {
            addLastModifiedDateFilter(query, (DateRangeFilterSpecification)filter);
        } else if (CreationDateFilterType.KEY.equals(type.getKey())) {
            addCreationDateFilter(query, (DateRangeFilterSpecification)filter);
        } else if (LastModifiedUserFilterType.KEY.equals(type.getKey())) {
            addPartyFilter(query, (PartyFilterSpecification)filter,
                           "modifying_user");
        } else if (CreationUserFilterType.KEY.equals(type.getKey())) {
            addPartyFilter(query, (PartyFilterSpecification)filter,
                           "creation_user");
        } else if (CMSContentSectionFilterType.KEY.equals(type.getKey())) {
            addCMSContentSectionFilter(query, (CMSContentSectionFilterSpecification)filter);
        }
    }

    protected void addCMSContentSectionFilter(DataQuery query,
                                              CMSContentSectionFilterSpecification filter) {
        if (filter.getSections() == null) {
            return;
        }
        Filter f = null;
        if (filter.isExclusion()) {
            f = query.addNotInSubqueryFilter(
                "object_id",
                "com.arsdigita.cms.getContentSectionItems");
        } else {
            f = query.addInSubqueryFilter(
                "object_id",
                "com.arsdigita.cms.getContentSectionItems");
        }
        f.set("sectionName", Arrays.asList(filter.getSections()));
    }

    protected void addVersionFilter(DataQuery query,
                                    VersionFilterSpecification filter) {
        Filter f = query.addFilter("nvl(version,'" + filter.getVersion() + "') = :version");
        f.set("version", filter.getVersion());
    }

    protected void addLaunchDateFilter(DataQuery query,
                                       DateRangeFilterSpecification filter) {
        addDateRangeFilter(query, filter, "launch_date");
    }

    protected void addLastModifiedDateFilter(DataQuery query,
                                         DateRangeFilterSpecification filter) {
        // TODO: https://bugzilla.redhat.com/bugzilla/show_bug.cgi?id=113394
        // this will not work until the above bug is fixed
        addDateRangeFilter(query, filter, "last_modified");
    }

    protected void addCreationDateFilter(DataQuery query,
                                         DateRangeFilterSpecification filter) {
        // TODO: https://bugzilla.redhat.com/bugzilla/show_bug.cgi?id=113394
        // this will not work until the above bug is fixed
        addDateRangeFilter(query, filter, "creation_date");
    }

    private void addDateRangeFilter(DataQuery query,
                                    DateRangeFilterSpecification filter,
                                    String columnName) {
        Date start = filter.getStartDate();
        Date end = filter.getEndDate();
        // XXX The query should actually be the commented out code at the
        // bottom of this method instead of the query that we are using
        // we have to comment it out because of a bug with persistence

        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        if (start != null) {
            Filter f = query.addFilter(columnName + " >= :" +
                                       columnName + "StartDate");
            // we truncate the value using java because we cannot do it
            // using sql because of the persistence bug
            Calendar startCal = GregorianCalendar.getInstance();
            startCal.setTime(start);
            /*
            Calendar truncCal = GregorianCalendar.getInstance();
            truncCal.set(Calendar.DAY_OF_YEAR, startCal.get(Calendar.DAY_OF_YEAR));
            truncCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
            truncCal.set(Calendar.MINUTE, 0);
            truncCal.set(Calendar.HOUR_OF_DAY, 0);
            truncCal.set(Calendar.SECOND, 0);
            f.set(columnName + "StartDate", truncCal.getTime());
            */
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.SECOND, 0);
            f.set(columnName + "StartDate", startCal.getTime());
        }
        if (end != null) {
            Filter f = query.addFilter(columnName + " < :" +
                                       columnName + "EndDate");
            // we truncate the value using java because we cannot do it
            // using sql because of the persistence bug
            Calendar endCal = GregorianCalendar.getInstance();
            endCal.setTime(end);
            endCal.set(Calendar.MINUTE, 0);
            endCal.set(Calendar.HOUR_OF_DAY, 0);
            endCal.set(Calendar.SECOND, 0);
            f.set(columnName + "EndDate", endCal.getTime());
        }

        /*
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        if (start != null) {
            // XXX persistence bug - can't deal with functions on fields
            Filter f = query.addFilter("trunc(" + columnName + ") >= to_date('" +
                                       format.format(start) +
                                       "','MM/DD/YYYY')");
        }
        if (end != null) {
            // XXX persistence bug - can't deal with functions on fields
            Filter f = query.addFilter("trunc(" + columnName + ") <= to_date('" +
                                       format.format(end) +
                                       "','MM/DD/YYYY')");
        }
        */
    }

    protected void addPartyFilter(DataQuery query,
                                  PartyFilterSpecification filter,
                                  String columnName) {
        PartyCollection parties = filter.getParties();
        if (parties == null) {
            return;
        }
        List partyIDs = new ArrayList();
        while (parties.next()) {
            partyIDs.add(parties.getID());
        }

        if (partyIDs.size() == 0) {
            // no parties match so we make sure to return no results
            query.addFilter("1=2");
        } else {
            Filter f = query.addFilter(columnName + " in :" +
                                       columnName + "parties");
            f.set(columnName + "parties", partyIDs);
        }
    }

    protected void addContentTypeFilter(DataQuery query,
                                        ContentTypeFilterSpecification filter) {
        List l = new ArrayList();
        ContentType[] types = filter.getTypes();
        if (types == null || types.length == 0) {
            return;
        }

        for (int i = 0 ; i < types.length ; i++) {
            ContentType type = types[i];
            l.add(type.getAssociatedObjectType());
        }
        Filter f = query.addFilter("object_type in :types");
        f.set("types", l);
    }

    // Override to use query that takes account of bundles :-(
    protected void addCategoryFilter(DataQuery query,
                                     CategoryFilterSpecification filter) {
        Category[] categories = filter.getCategories();
        if (categories != null && categories.length > 0) {
            List ids = new ArrayList();
            for (int i = 0 ; i < categories.length ; i++) {
                ids.add(categories[i].getID());
            }

            Filter f = query.addInSubqueryFilter(
                "object_id",
                "id",
                "com.arsdigita.cms.searchCategoryObjects");

            f.set("ids", ids);
            f.set("pathLimit", new Integer(filter.isDescending() ? 999999 : 0));
        }
    }

}
