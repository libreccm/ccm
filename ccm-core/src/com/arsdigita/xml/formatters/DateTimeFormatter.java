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
package com.arsdigita.xml.formatters;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.xml.Formatter;
import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;

/**
 * The default formatter for java.util.Date objects, outputing the date in
 * 'medium' format and the time in 'short' format.
 */
public class DateTimeFormatter implements Formatter {
    
    public String format(Object value) {
        Date date = (Date)value;
		Locale locale;
		DateFormatterConfig dfc = DateFormatter.getConfig();
		if (dfc.getLocale() != null) {
			locale = new Locale(dfc.getLocale());
		} else {
			locale = Kernel.getContext().getLocale();
		}
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
		DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.SHORT, locale);
        
        return format.format(date);
    }
}
