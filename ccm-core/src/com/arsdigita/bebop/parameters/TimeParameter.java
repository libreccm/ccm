/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.parameters;

import com.arsdigita.globalization.Globalization;
import com.arsdigita.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.servlet.http.HttpServletRequest;



/**
 * A class that represents the model for a time form parameter.
 *
 * @see com.arsdigita.bebop.parameters.DateTimeParameter
 * @author Dave Turner
 * @version $Id: TimeParameter.java 1114 2006-04-19 21:24:07Z apevec $
 */
public class TimeParameter extends ParameterModel
{

    public TimeParameter ( String name ) {
        super(name);
    }

    /**
     * This method returns a new Calendar object that is manipulated
     * within transformValue to create a Date Object. This method should
     * be overridden if you wish to use a Calendar other than the
     * lenient GregorianCalendar.
     *
     * @param request the servlet request from which Locale can be
     * extracted if needed
     *
     * @return a new Calendar object
     * */
    protected Calendar getCalendar(HttpServletRequest request) {
        return new GregorianCalendar();
    }


    /**
     * Computes a dateTime object from multiple parameters in the
     * request. This method searches for parameters named
     * <code>getName() + ".hour"<code>,
     * <code>getName() + ".minute"<code>,
     * <code>getName() + ".second"<code>, and
     * <code>getName() + ".amOrPm"<code>.
     * */
    public Object transformValue(HttpServletRequest request)
        throws IllegalArgumentException {

        Calendar c = getCalendar(request);
        c.clear();

        String hour = Globalization.decodeParameter(request, getName()+".hour");
        String minute = Globalization.decodeParameter(request, getName()+".minute");
        String second = Globalization.decodeParameter(request, getName()+".second");
        String amOrPm = Globalization.decodeParameter(request, getName()+".amOrPm");
        
        if (StringUtils.emptyString(hour) &&
            StringUtils.emptyString(minute) &&
            StringUtils.emptyString(second)) {
            return transformSingleValue(request);
        }

        if (!StringUtils.emptyString(hour)) {
            int hourInt = Integer.parseInt(hour);
            if (hourInt == 12) {
                hourInt = 0;
            }
            c.set(Calendar.HOUR, hourInt);
        }

        if (!StringUtils.emptyString(minute)) {
            c.set(Calendar.MINUTE, Integer.parseInt(minute));
        }

        if (!StringUtils.emptyString(second)) {
            c.set(Calendar.SECOND, Integer.parseInt(second));
        }

        if ( amOrPm != null ) {
            c.set(Calendar.AM_PM, Integer.parseInt(amOrPm));
        }

        return c.getTime();
    }


    public Object unmarshal ( String encoded ) {
        try {
            return new Date(Long.parseLong(encoded));
        } catch ( NumberFormatException ex ) {
            throw new IllegalArgumentException("Cannot unmarshal time '"
                                               + encoded + "': " + ex.getMessage());
        }
    }

    public String marshal ( Object value ) {
        return Long.toString(((Date)value).getTime());
    }

    public Class getValueClass () {
        return Date.class;
    }

}
