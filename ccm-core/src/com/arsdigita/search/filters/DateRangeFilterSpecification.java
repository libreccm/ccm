/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.filters;

import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.FilterType;

import java.util.Date;

/**
 * A filter spec for supplying two dates (start & end) 
 * for filtering on the item's launch date.
 */
public class DateRangeFilterSpecification extends FilterSpecification {

    public final static String START_DATE = "startDate";
    public final static String END_DATE = "endDate";
    
    /**
     * Creates a new filter restricting results to have
     * a launch date within a given range, inclusive.
     *
     * @param start the start date
     * @param end the end date
     */
    public DateRangeFilterSpecification(Date start,
                                        Date end,
                                        FilterType filterType) {
        super(new Object[] { START_DATE, start, END_DATE, end},
              filterType);
    }
    
    /**
     * Start of the date range for filtering
     * @return the start date
     */
    public Date getStartDate() {
        return (Date)get(START_DATE);
    }

    /**
     * End of the date range for filtering
     * @return the end date
     */
    public Date getEndDate() {
        return (Date)get(END_DATE);
    }
}
