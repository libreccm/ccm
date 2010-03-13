/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.DateTimeParameter;
import com.arsdigita.bebop.parameters.NumberInRangeValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.util.Assert;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.xml.Element;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *    A class representing a date and time field in an HTML form.
 *    (based on the code in Date.java)
 *
 *    @author Scott Seago 
 *    @version $Id: DateTime.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DateTime extends Widget implements BebopConstants {

    private OptionGroup m_year;
    private OptionGroup m_month;
    private TextField   m_day;
    private TextField   m_hour;
    private TextField   m_minute;
    private TextField   m_second;
    private OptionGroup m_amOrPm;
    private static final String ZERO = "0";
    private boolean m_showSeconds;

    // Inner classes for the fragment widgets
    private class YearFragment extends SingleSelect {

        private DateTime parent;

        public YearFragment(String name, DateTime parent) {
            super(name);
            this.parent = parent;
        }

        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        public Object getValue(PageState ps) {
            Object value =  parent.getFragmentValue(ps, Calendar.YEAR);
	    if (value == null) {
		Calendar currentTime = GregorianCalendar.getInstance();
		int currentYear = currentTime.get(Calendar.YEAR);
		value = new Integer(currentYear);
	    }
	    return value;
        }

    }

    private class MonthFragment extends SingleSelect {

        private DateTime parent;

        public MonthFragment(String name, DateTime parent) {
            super(name);
            this.parent = parent;
        }

        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        public Object getValue(PageState ps) {
            return parent.getFragmentValue(ps, Calendar.MONTH);
        }

    }

    private class DayFragment extends TextField {

        private DateTime parent;

        public DayFragment(String name, DateTime parent) {
            super(name);
            this.parent = parent;
        }

        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        public Object getValue(PageState ps) {
            return parent.getFragmentValue(ps, Calendar.DATE);
        }
    }

    private class HourFragment extends TextField {

        private DateTime parent;

        public HourFragment(String name, DateTime parent) {
            super(name);
            this.parent = parent;
            this.addValidationListener(new NumberInRangeValidationListener(1,12));
        }

        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        public Object getValue(PageState ps) {
            return parent.getFragmentValue(ps, Calendar.HOUR);
        }
    }

    private class MinuteFragment extends TextField {

        private DateTime parent;

        public MinuteFragment(String name, DateTime parent) {
            super(name);
            this.parent = parent;
            this.addValidationListener(new NumberInRangeValidationListener(0,59));
        }

        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        public Object getValue(PageState ps) {
            Integer min = (Integer) parent.getFragmentValue(ps, Calendar.MINUTE);
            if (min == null) {
                return null;
            }
            if ( min.intValue() < 10 ) {
                return ZERO + min.toString();
            } else {
                return min.toString();
            }
        }
    }

    private class SecondFragment extends TextField {

        private DateTime parent;

        public SecondFragment(String name, DateTime parent) {
            super(name);
            this.parent = parent;
            this.addValidationListener(new NumberInRangeValidationListener(0,59));
        }

        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        public Object getValue(PageState ps) {
            Integer sec = (Integer) parent.getFragmentValue(ps, Calendar.SECOND);
            if (sec == null) {
                return null;
            }
            if ( sec.intValue() < 10 ) {
                return ZERO + sec.toString();
            } else {
                return sec.toString();
            }
        }
    }

    private class AmPmFragment extends SingleSelect {

        private DateTime parent;

        public AmPmFragment(String name, DateTime parent) {
            super(name);
            this.parent = parent;
        }

        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        public Object getValue(PageState ps) {
            return parent.getFragmentValue(ps, Calendar.AM_PM);
        }

    }

    /**
     * Construct a new DateTime. The model must be a DateTimeParameter
     */
    public DateTime(ParameterModel model) {
        this(model, false);
    }

    /**
     * Construct a new DateTime. The model must be a DateTimeParameter
     */
    public DateTime(ParameterModel model, boolean showSeconds) {
        super(model);

        if ( ! (model instanceof DateTimeParameter)) {
            throw new IllegalArgumentException(
                                               "The DateTime widget " + model.getName() +
                                               " must be backed by a DateTimeParameter parmeter model");
        }

        String name = model.getName();
        String nameYear = name + ".year";
        String nameMonth = name + ".month";
        String nameDay = name + ".day";
        String nameHour = name + ".hour";
        String nameMinute = name + ".minute";
        String nameSecond = name + ".second";
        String nameAmOrPm = name + ".amOrPm";


        DateFormatSymbols dfs = new DateFormatSymbols();
        Calendar currentTime = GregorianCalendar.getInstance();

        m_year = new YearFragment(nameYear, this);
        m_month = new MonthFragment(nameMonth, this);
        m_day = new DayFragment(nameDay, this);
        m_hour = new HourFragment(nameHour, this);
        m_minute = new MinuteFragment(nameMinute, this);
        m_showSeconds = showSeconds;
        if (m_showSeconds) {
            m_second = new SecondFragment(nameSecond, this);
        } else {
            m_second = null;
        }
        m_amOrPm = new AmPmFragment(nameAmOrPm, this);

        m_day.setMaxLength(2);
        m_day.setSize(2);
        m_hour.setMaxLength(2);
        m_hour.setSize(2);
        m_minute.setMaxLength(2);
        m_minute.setSize(2);
        if (m_showSeconds) {
            m_second.setMaxLength(2);
            m_second.setSize(2);
        }
        String [] months = dfs.getMonths();

        for (int i=0; i<months.length; i+=1) {        // globalize ?
            // This check is necessary because
            // java.text.DateFormatSymbols.getMonths() returns an array
            // of 13 Strings: 12 month names and an empty string.
            if ( months[i].length() > 0 ) {
                m_month.addOption(new Option(String.valueOf(i),months[i]));
            }
        }
        int currentYear = currentTime.get(Calendar.YEAR);
        setYearRange(currentYear - 1, currentYear + 3);


        String [] amPmStrings = dfs.getAmPmStrings();
        for (int i=0; i<amPmStrings.length; i+=1) {
            //if ( amPmStrings[i].length() > 0 ) {
            m_amOrPm.addOption(new Option(String.valueOf(i),amPmStrings[i]));
            //}
        }

    }

    public DateTime(String name) {
        this(new DateTimeParameter(name));
    }

    public void setYearRange(int startYear, int endYear) {
        Assert.isUnlocked(this);
        m_year.clearOptions();
        for (int j= startYear; j<=endYear; j+=1) {
            m_year.addOption(new Option(String.valueOf(j)));
        }
    }

    /**
     * Returns a string naming the type of this widget.
     */
    public String getType() {
        return "dateTime";
    }

    /**
     * Sets the <tt>MAXLENGTH</tt> attribute for the <tt>INPUT</tt> tag
     * used to render this form element.
     */
    public void setMaxLength(int length) {
        setAttribute("MAXLENGTH", String.valueOf(length));
    }

    public boolean isCompound() {
        return true;
    }

    /** The XML tag for this derived class of Widget.
     */
    protected String getElementTag() {
        return BEBOP_DATETIME;
    }

    public void generateWidget(PageState ps, Element parent) {

        if ( ! isVisible(ps) ) {
            return;
        }

        Element date = parent.newChildElement(getElementTag(), BEBOP_XML_NS);
        date.addAttribute("name", getParameterModel().getName());
        m_month .generateXML(ps, date);
        m_day   .generateXML(ps, date);
        m_year  .generateXML(ps, date);
        m_hour  .generateXML(ps, date);
        m_minute.generateXML(ps, date);
        if (m_showSeconds) {
            m_second.generateXML(ps, date);
        }
        m_amOrPm.generateXML(ps, date);
    }

    public void setDisabled() {
        m_month.setDisabled();
        m_day.setDisabled();
        m_year.setDisabled();
        m_hour.setDisabled();
        m_minute.setDisabled();
        if (m_showSeconds) {
            m_second.setDisabled();
        }
        m_amOrPm.setDisabled();
    }

    public void setReadOnly() {
        m_month.setReadOnly();
        m_day.setReadOnly();
        m_year.setReadOnly();
        m_hour.setReadOnly();
        m_minute.setReadOnly();
        if (m_showSeconds) {
            m_second.setReadOnly();
        }
        m_amOrPm.setReadOnly();
    }

    /**
     * Sets the Form Object for this Widget. This method will throw an
     * exception if the _form pointer is already set. To explicity
     * change the _form pointer the developer must first call
     * setForm(null)
     *
     * @param the <code>Form</code> Object for this Widget.
     * @exception IllegalStateException if form already set.
     */
    public void setForm(Form f) {
        super   .setForm(f);
        m_year .setForm(f);
        m_month.setForm(f);
        m_day  .setForm(f);
        m_hour.setForm(f);
        m_minute.setForm(f);
        if (m_showSeconds) {
            m_second.setForm(f);
        }
        m_amOrPm.setForm(f);
    }

    private Object getFragmentValue(PageState ps, int field) {
        Assert.exists(ps, "PageState");
        FormData f = getForm().getFormData(ps);
        if (f != null) {
            java.util.Date value = (java.util.Date)f.get(getName());
            if (value != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(value);
                return new Integer(c.get(field));
            }
        }
        return null;
    }
}
