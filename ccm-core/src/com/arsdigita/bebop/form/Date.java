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


import java.text.DateFormatSymbols;

import java.util.Calendar;
import java.util.GregorianCalendar;


import com.arsdigita.util.Assert;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.kernel.Kernel;
// This interface contains the XML element name of this class
// in a constant which is used when generating XML
import com.arsdigita.bebop.util.BebopConstants;

import com.arsdigita.xml.Element;
import java.util.Locale;

/**
 *    A class representing a date field in an HTML form.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Michael Pih 
 *    @version $Id: Date.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Date extends Widget implements BebopConstants {

    public static final String versionId = "$Id: Date.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    protected OptionGroup m_year;
    protected OptionGroup m_month;
    protected TextField   m_day;

    // Inner classes for the fragment widgets
    protected class YearFragment extends SingleSelect {

        protected Date parent;

        public YearFragment(String name, Date parent) {
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

    protected class MonthFragment extends SingleSelect {

        protected Date parent;

        public MonthFragment(String name, Date parent) {
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

    protected class DayFragment extends TextField {

        protected Date parent;

        public DayFragment(String name, Date parent) {
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

    /**
     * Construct a new Date. The model must be a DateParameter
     */
    public Date(ParameterModel model) {
        super(model);

        if ( ! (model instanceof DateParameter)) {
            throw new IllegalArgumentException(
                                               "The Date widget " + model.getName() +
                                               " must be backed by a DateParameter parmeter model");
        }

        String name = model.getName();
        String nameYear = name + ".year";
        String nameMonth = name + ".month";
        String nameDay = name + ".day";

        DateFormatSymbols dfs = null;
        Locale locale = Kernel.getContext().getLocale();
        if (locale != null) {
            dfs = new DateFormatSymbols(locale);
        } else {
            dfs = new DateFormatSymbols();
        }
        Calendar currentTime = GregorianCalendar.getInstance();

        m_year = new YearFragment(nameYear, this);
        m_month = new MonthFragment(nameMonth, this);
        m_day = new DayFragment(nameDay, this);

        m_day.setMaxLength(2);
        m_day.setSize(2);
        String [] months = dfs.getMonths();

        for (int i=0; i<months.length; i+=1) {        
            // This check is necessary because
            // java.text.DateFormatSymbols.getMonths() returns an array
            // of 13 Strings: 12 month names and an empty string.
            if ( months[i].length() > 0 ) {
                m_month.addOption(new Option(String.valueOf(i),months[i]));
            }
        }
        int currentYear = currentTime.get(Calendar.YEAR);
        setYearRange(currentYear - 1, currentYear + 3);

    }

    public Date(String name) {
        this(new DateParameter(name));
    }

    public void setYearRange(int startYear, int endYear) {
        Assert.assertNotLocked(this);
        m_year.clearOptions();
        for (int j= startYear; j<=endYear; j+=1) {
            m_year.addOption(new Option(String.valueOf(j)));
        }
    }

    /**
     * Returns a string naming the type of this widget.
     */
    public String getType() {
        return "date";
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
        return BEBOP_DATE;
    }

    public void generateWidget(PageState ps, Element parent) {

        if ( ! isVisible(ps) ) {
            return;
        }

        Element date = parent.newChildElement(getElementTag(), BEBOP_XML_NS);
        //        parent.addContent(date);
        date.addAttribute("name", getParameterModel().getName());
        exportAttributes(date);
        m_month.generateXML(ps, date);
        m_day  .generateXML(ps, date);
        m_year .generateXML(ps, date);
    }

    public void setDisabled() {
        m_month.setDisabled();
        m_day.setDisabled();
        m_year.setDisabled();
    }

    public void setReadOnly() {
        m_month.setReadOnly();
        m_day.setReadOnly();
        m_year.setReadOnly();
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
    }

    public Object getFragmentValue(PageState ps, int field) {
        Assert.assertNotNull(ps, "PageState");
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

    public void setClassAttr(String at) {
        m_month.setClassAttr(at);
        m_year.setClassAttr(at);
        m_day.setClassAttr(at);
        super.setClassAttr(at);
    }
}
