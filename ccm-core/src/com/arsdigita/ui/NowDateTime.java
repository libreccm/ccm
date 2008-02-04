/*
 * Copyright (C) 2002-2005 Runtime Collective Ltd. All Rights Reserved.
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
 */

package com.arsdigita.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.xml.Element;

import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 * This generate a bit of XML containing the current Date and Time. The output looks like this:
 * &lt;ui:nowDateTime&gt;
 * &nbsp;&nbsp;&lt;year&gt;2004&lt;/year&gt;
 * &nbsp;&nbsp;&lt;month&gt;February&lt;/month&gt;
 * &nbsp;&nbsp;&lt;monthNo&gt;01&lt;/monthNo&gt;
 * &nbsp;&nbsp;&lt;day&gt;Friday&lt;/day&gt;
 * &nbsp;&nbsp;&lt;dayOfMonth&gt;09&lt;/dayOfMonth&gt;
 * &nbsp;&nbsp;&lt;dayOfWeek&gt;06&lt;/dayOfWeek&gt;
 * &nbsp;&nbsp;&lt;hour&gt;09&lt;/hour&gt;
 * &nbsp;&nbsp;&lt;minute&gt;07&lt;/minute&gt;
 * &nbsp;&nbsp;&lt;second&gt;03&lt;/second&gt;
 * &nbsp;&nbsp;&lt;apm&gt;am&lt;/apm&gt;
 * &lt;/ui:nowDateTime&gt;
 * <p>
 * To activate this component in a jsp, add the following:<br>
 * &lt;define:component name="contentList"
 * classname="com.arsdigita.ui.NowDateTime" /&gt;
 * </ul>
 *
 * @version $Revision: 1.2 $ $Date: 2005/01/07 18:48:45 $
 */
public class NowDateTime extends SimpleComponent {

    public static final String versionId = "$Id: NowDateTime.java 469 2005-03-20 23:12:44Z mbooth $";

    private static Logger log = Logger.getLogger(NowDateTime.class);;
    
    private static final String TAG_NOWDATETIME = "ui:nowDateTime";
    private static final String TAG_YEAR = "year";
    private static final String TAG_MONTH = "month";
    private static final String TAG_MONTHNO = "monthNo";
    private static final String TAG_DAY = "day";
    private static final String TAG_DAYOFMONTH = "dayOfMonth";
    private static final String TAG_DAYOFWEEK = "dayOfWeek";
    private static final String TAG_HOUR = "hour";
    private static final String TAG_MINUTE = "minute";
    private static final String TAG_SECOND = "second";
    private static final String TAG_APM = "apm";

    private static final String NULL = "";

    public NowDateTime() {
        super();
    }

    /**
     * Generates the XML.
     *
     * @param state The page state
     * @param parent The parent DOM element
     */
    public void generateXML(PageState state, Element parent) {
        if (isVisible(state)) {

            // get the date/time
            Calendar now = Calendar.getInstance();

            // output the XML
            Element dateElement;
            Element element;
            String value;

            dateElement = parent.newChildElement(TAG_NOWDATETIME, UIConstants.UI_XML_NS);
            
            element = dateElement.newChildElement(TAG_YEAR, UIConstants.UI_XML_NS);
            element.setText(NULL+now.get(Calendar.YEAR));
            
            element = dateElement.newChildElement(TAG_MONTH, UIConstants.UI_XML_NS);
            value = NULL;
            switch (now.get(Calendar.MONTH)) {
            case Calendar.JANUARY:   value = "January"; break;
            case Calendar.FEBRUARY:  value = "February"; break;
            case Calendar.MARCH:     value = "March"; break;
            case Calendar.APRIL:     value = "April"; break;
            case Calendar.MAY:       value = "May"; break;
            case Calendar.JUNE:      value = "June"; break;
            case Calendar.JULY:      value = "July"; break;
            case Calendar.AUGUST:    value = "August"; break;
            case Calendar.SEPTEMBER: value = "September"; break;
            case Calendar.OCTOBER:   value = "October"; break;
            case Calendar.NOVEMBER:  value = "November"; break;
            case Calendar.DECEMBER:  value = "December"; break;
            }
            element.setText(value);

            element = dateElement.newChildElement(TAG_MONTHNO, UIConstants.UI_XML_NS);
            element.setText(NULL+now.get(Calendar.MONTH));

            element = dateElement.newChildElement(TAG_DAY, UIConstants.UI_XML_NS);
            value = NULL;
            switch (now.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:    value = "Monday"; break;
            case Calendar.TUESDAY:   value = "Tuesday"; break;
            case Calendar.WEDNESDAY: value = "Wednesday"; break;
            case Calendar.THURSDAY:  value = "Thursday"; break;
            case Calendar.FRIDAY:    value = "Friday"; break;
            case Calendar.SATURDAY:  value = "Saturday"; break;
            case Calendar.SUNDAY:    value = "Sunday"; break;
            }
            element.setText(value);

            element = dateElement.newChildElement(TAG_DAYOFMONTH, UIConstants.UI_XML_NS);
            element.setText(NULL+now.get(Calendar.DAY_OF_MONTH));

            element = dateElement.newChildElement(TAG_DAYOFWEEK, UIConstants.UI_XML_NS);
            element.setText(NULL+now.get(Calendar.DAY_OF_WEEK));

            element = dateElement.newChildElement(TAG_HOUR, UIConstants.UI_XML_NS);
            element.setText(NULL+now.get(Calendar.HOUR));

            element = dateElement.newChildElement(TAG_MINUTE, UIConstants.UI_XML_NS);
            element.setText(NULL+now.get(Calendar.MINUTE));

            element = dateElement.newChildElement(TAG_SECOND, UIConstants.UI_XML_NS);
            element.setText(NULL+now.get(Calendar.SECOND));

            element = dateElement.newChildElement(TAG_APM, UIConstants.UI_XML_NS);
            value = NULL;
            switch (now.get(Calendar.AM_PM)) {
            case Calendar.AM: value = "am"; break;
            case Calendar.PM: value = "pm"; break;
            }
            element.setText(value);
        }
    }
}
