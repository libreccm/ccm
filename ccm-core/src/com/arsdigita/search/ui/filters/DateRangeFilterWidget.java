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
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.search.filters.DateRangeFilterSpecification;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * This class represents a date range filter used by item search.
 *
 * Changed to respect the date format string according to negotiated locale.
 * Effect will be visible in Mandalay beginning with r163.
 *
 * @author unknown...
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class DateRangeFilterWidget extends FilterWidget {

    private FilterType m_type;

    public DateRangeFilterWidget(FilterType type, String name) {
        super(type, new DateRangeParameter(name));
        m_type = type;
    }

    public FilterSpecification getFilter(PageState state) {
        DateRange range = (DateRange) getValue(state);

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

    @Override
    public void generateBodyXML(PageState state, Element parent) {
        super.generateBodyXML(state, parent);

        String x = "";

        Locale defaultLocale = Locale.getDefault();
        Locale locale = GlobalizationHelper.getNegotiatedLocale();

        // Get the current Pattern
        // XXX This is really, really, really, really, really, really bad
        // but there is no way to get a SimpleDateFormat object for a
        // different locale the the system default (the one you get with
        // Locale.getDefault();). Also there is now way getting the pattern
        // in another way (up until JDK 1.1 there was), so I have to temporarly
        // switch the default locale to my desired locale, get a SimpleDateFormat
        // and switch back.
        Locale.setDefault(locale);
        String format = new SimpleDateFormat().toPattern();
        Locale.setDefault(defaultLocale);

        DateFormatSymbols dfs = new DateFormatSymbols(locale);
        Calendar currentTime = GregorianCalendar.getInstance();

        DateRange range = (DateRange) getValue(state);
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
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(end);
                endDay = cal.get(Calendar.DAY_OF_MONTH);
                endMonth = cal.get(Calendar.MONTH);
                endYear = cal.get(Calendar.YEAR);
            }
        }

        // Localize the date range widget according to the date format string
        char[] chars = format.toCharArray();
        for (int i = 0; i < chars.length; i++) {

            // Test for doublettes
            if (i >= 1 && chars[i - 1] == chars[i]) {
                continue;
            }

            switch (chars[i]) {
                case 'd':
                    // Day fragment
                    x += "day ";
                    Element day = Search.newElement("day");
                    if (startDay != -1) {
                        day.addAttribute("startDay", String.valueOf(startDay));
                    }
                    if (endDay != -1) {
                        day.addAttribute("endDay", String.valueOf(endDay));
                    }
                    parent.addContent(day);

                    break;
                case 'M':
                    // Month fragment
                    x += "month ";
                    if (startMonth == -1) {
                        startMonth = currentTime.get(Calendar.MONTH);
                    }
                    if (endMonth == -1) {
                        endMonth = currentTime.get(Calendar.MONTH);
                    }

                    String[] monthsList = dfs.getMonths();
                    for (int monthIndex = 0; monthIndex < monthsList.length; monthIndex++) {
                        // This check is necessary because
                        // java.text.DateFormatSymbols.getMonths() returns an array
                        // of 13 Strings: 12 month names and an empty string.
                        if (monthsList[monthIndex].length() > 0) {
                            Element month = Search.newElement("month");
                            month.addAttribute("value", String.valueOf(monthIndex));
                            month.addAttribute("title", monthsList[monthIndex]);
                            if (startMonth == monthIndex) {
                                month.addAttribute("startMonth", "1");
                            }
                            if (endMonth == monthIndex) {
                                month.addAttribute("endMonth", "1");
                            }
                            parent.addContent(month);
                        }
                    }

                    break;
                case 'y':
                    // Year fragment
                    x += "year ";
                    if (startYear == -1) {
                        startYear = currentTime.get(Calendar.YEAR);
                    }
                    if (endYear == -1) {
                        endYear = currentTime.get(Calendar.YEAR);
                    }

                    int currentYear = currentTime.get(Calendar.YEAR);

                    // TODO: use start_year and end_year_deta config params

                    for (int yearStep = currentYear - 5; yearStep <= currentYear + 5; yearStep++) {
                        Element year = Search.newElement("year");
                        year.addAttribute("value", String.valueOf(yearStep));
                        year.addAttribute("title", String.valueOf(yearStep));
                        if (startYear == yearStep) {
                            year.addAttribute("startYear", "1");
                        }
                        if (endYear == yearStep) {
                            year.addAttribute("endYear", "1");
                        }
                        parent.addContent(year);
                    }
                    break;
                default:
                    break;
            }

        }

        parent.addAttribute("format", x.trim());
    }
}
