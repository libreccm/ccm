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
package com.arsdigita.search.ui.filters;

import com.arsdigita.search.ui.FilterWidget;
import com.arsdigita.search.Search;
import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.FilterType;
import com.arsdigita.xml.Element;
import com.arsdigita.bebop.PageState;
import com.arsdigita.search.filters.DateRangeFilterSpecification;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.DateFormatSymbols;

public class DateRangeFilterWidget extends FilterWidget {

    private FilterType m_type;

    public DateRangeFilterWidget(FilterType type, String name) {
        super(type, new DateRangeParameter(name));
        m_type = type;
    }
    
    public FilterSpecification getFilter(PageState state) {
        DateRange range = (DateRange)getValue(state);

        if (range == null) {
            return new DateRangeFilterSpecification(null,
                                                    null,
                                                    m_type);
        } else {
            return new DateRangeFilterSpecification(range.getStartDate(),
                                                    range.getEndDate(),
                                                    m_type);
        }
    }

    public void generateBodyXML(PageState state,
                                Element parent) {
        super.generateBodyXML(state, parent);
        
        
        DateRange range = (DateRange)getValue(state);
        int startDay = -1;
        int startMonth = -1;
        int startYear = -1;

        int endDay = -1;
        int endMonth = -1;
        int endYear = -1;
        if (range != null) {
            Date start = range.getStartDate();
            if (start != null) {
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(start);

                startDay = cal.get(Calendar.DAY_OF_MONTH);
                startMonth = cal.get(Calendar.MONTH);
                startYear = cal.get(Calendar.YEAR);
            }

            Date end = range.getEndDate();
            if (end != null) {
                Element value = Search.newElement("end");
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(end);
                endDay = cal.get(Calendar.DAY_OF_MONTH);
                endMonth = cal.get(Calendar.MONTH);
                endYear = cal.get(Calendar.YEAR);
            }
        }

        Element day = Search.newElement("day");
        if (startDay != -1) {
            day.addAttribute("startDay", "1");
        }
        if (endDay != -1) {
            day.addAttribute("endDay", "1");
        }

        DateFormatSymbols dfs = new DateFormatSymbols();
        Calendar currentTime = GregorianCalendar.getInstance();

        if (startMonth == -1) {
            startMonth = currentTime.get(Calendar.MONTH);
        }
        if (endMonth == -1) {
            endMonth = currentTime.get(Calendar.MONTH);
        }
        
        String [] monthsList = dfs.getMonths();
        for (int i = 0 ; i < monthsList.length; i++) {
            // This check is necessary because
            // java.text.DateFormatSymbols.getMonths() returns an array
            // of 13 Strings: 12 month names and an empty string.
            if ( monthsList[i].length() > 0 ) {
                Element month = Search.newElement("month");
                month.addAttribute("value", String.valueOf(i));
                month.addAttribute("title", monthsList[i]);
                if (startMonth == i) {
                    month.addAttribute("startMonth", "1");
                }
                if (endMonth == i) {
                    month.addAttribute("endMonth", "1");
                }
                parent.addContent(month);
            }
        }

        if (startYear == -1) {
            startYear = currentTime.get(Calendar.YEAR);
        }
        if (endYear == -1) {
            endYear = currentTime.get(Calendar.YEAR);
        }
        
        int currentYear = currentTime.get(Calendar.YEAR);
        for ( int i = currentYear - 5 ; i <= currentYear+5 ; i++) {
            Element year = Search.newElement("year");
            year.addAttribute("value", String.valueOf(i));
            year.addAttribute("title", String.valueOf(i));
            if (startYear == i) {
                year.addAttribute("startYear", "1");
            }
            if (endYear == i) {
                year.addAttribute("endYear", "1");
            }
            parent.addContent(year);
        }
    }
}
