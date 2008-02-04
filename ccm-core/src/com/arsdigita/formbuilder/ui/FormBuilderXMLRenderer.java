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
package com.arsdigita.formbuilder.ui;


import com.arsdigita.bebop.util.Attributes;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.formbuilder.DataDrivenSelect;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentDataQuery;
import com.arsdigita.formbuilder.PersistentDate;
import com.arsdigita.formbuilder.util.AttributeHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import org.apache.log4j.Logger;


/**
 * An implementation of DomainObjectTraversal that generates an XML
 * tree representing the DomainObject. The output format of the XML
 * can be controlled using the various setWrapXXX methods detailed
 * below.
 *
 * This handles the special case attributes that are presented by
 * the form builder but otherwise is similar to the DomainObjectXMLRenderer
 *
 * @version $Id: FormBuilderXMLRenderer.java 751 2005-09-02 12:52:23Z sskracic $
 */
public class FormBuilderXMLRenderer extends DomainObjectXMLRenderer {

    private static final Logger s_log = Logger.getLogger
        (FormBuilderXMLRenderer.class);


    /**
     * Creates a new DomainObject XML renderer
     * that outputs XML into the element passed into
     * the constructor.
     *
     * @param root the XML element in which to output children
     */
    public FormBuilderXMLRenderer(Element root) {
        super(root);
    }

    protected void endAssociation(DomainObject obj,
                                  String path,
                                  Property property) {
        if (obj instanceof PersistentDate) {
            handlePersistentDate((PersistentDate)obj, path, property);
        } else if (obj instanceof DataDrivenSelect) {
            handleDataDrivenSelect((DataDrivenSelect)obj, path, property);
        }
        super.endAssociation(obj, path, property);
    }

    /**
     *  This overrides the handleAttribute method to provide the special case
     *  for the PersistentComponent.ATTRIBUTE_STRING
     */
    //  the PersistentComponent.ATTRIBUTE_STRING is actually a serialized
    //  map of many different attributes so when we see this attribute
    //  we want to create a mini-DOM instead of just a single element
    protected void handleAttribute(DomainObject obj,
                                   String path,
                                   Property property) {
        if (obj instanceof PersistentComponent) {
            String name = property.getName();
            if (obj instanceof PersistentDate && 
                PersistentDate.DEFAULT_VALUE.equals(name)) {
                // we know that we have a java.util.date for the defaultValue
                // so we break it out in to a mini-dom
                Date defaultDate = (Date)PersistentDate.deserializeDefaultValue
                    ((String)DomainServiceInterfaceExposer.get(obj, name));
                if (defaultDate != null) {
                    Element element = newElement(getCurrentElement(), 
                                                 PersistentDate.DEFAULT_VALUE);
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(defaultDate);
                    String value = Integer.toString
                        (calendar.get(Calendar.DAY_OF_MONTH));
                    addElement(element, "day", value);
                    value = Integer.toString(calendar.get(Calendar.MONTH));
                    addElement(element, "month", value);
                    value = Integer.toString(calendar.get(Calendar.YEAR));
                    addElement(element, "year", value);
                }
            } else if (PersistentComponent.ATTRIBUTE_STRING.equals(name)) {
                // we have the attribute that we need to special case
                Object value = DomainServiceInterfaceExposer.get(obj, name);
                if (value != null) {
                    Attributes attributes = 
                        (new AttributeHelper()).getAttributesMap
                        (value.toString());
                    Collection keysCollection = attributes.getAttributeKeys();

                    if (keysCollection != null) {
                        Iterator keys = keysCollection.iterator();
                        Element currentElement = getCurrentElement();
                        while (keys.hasNext()) {
                            String keyName = (String)keys.next();
                            String keyValue = attributes.getAttribute(keyName);
                            addElement(currentElement, keyName, keyValue);
                        }
                    }
                }
            } else {
                super.handleAttribute(obj, path, property);
            }
        } else {
            super.handleAttribute(obj, path, property);
        }
    }

    private Element addElement(Element currentElement, String name, 
                               String value) {
        // we don't call format because we already
        // have a string.  The formatting should have
        // been done before the initial serialization
        if (isWrappingAttributes()) {
            Element element = newElement(currentElement, name);
            element.setText(value);
            return element;
        } else {
            currentElement.addAttribute(name, value);
            return currentElement;
        }
    }


    private void handlePersistentDate(PersistentDate date,
                                      String path,
                                      Property property) {
        // we have to print out the months and years for use by the xsl
        Integer startYear = date.getStartYear();
        Integer endYear = date.getEndYear(); 
        Element currentElement = getCurrentElement();
        Element monthElement = newElement(currentElement, "monthList");
        Element yearElement = newElement(currentElement, "yearList");
        int defaultMonth = -1;
        int defaultYear = -1;
        Date defaultDate = (Date)date.getDefaultValue();
        if (defaultDate != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(defaultDate);
            defaultMonth = calendar.get(Calendar.MONTH);
            defaultYear = calendar.get(Calendar.YEAR);
        }
        
        // for the Date, we have to add month and year support
        Locale locale = Kernel.getContext().getLocale();

        DateFormatSymbols dfs = null;
        if (locale != null) {
            dfs = new DateFormatSymbols(locale);
        } else {
            dfs = new DateFormatSymbols();
        }
        String [] months = dfs.getMonths();
        
        for (int i=0; i<months.length; i+=1) {        
            // This check is necessary because
            // java.text.DateFormatSymbols.getMonths() returns an array
            // of 13 Strings: 12 month names and an empty string.
            if ( months[i].length() > 0 ) {
                Element month = newElement(monthElement, "month");
                month.setText(months[i]);
                month.addAttribute("value", Integer.toString(i));
                if (defaultMonth == i) {
                    month.addAttribute("selected", "selected");
                }
            } 
        }
        
        // now, do the years
        int startYearInt = -1;
        int endYearInt = -1;
        if (startYear != null) {
            startYearInt = startYear.intValue();
        } else {
            startYearInt = 
                GregorianCalendar.getInstance()
                .get(Calendar.YEAR) - 5;
        }
        if (endYear != null) {
            endYearInt = endYear.intValue();
        } else {
            endYearInt = startYearInt + 10;
        }
        
        for (int i = startYearInt; i <= endYearInt; i++) {
            Element year = newElement(yearElement, "year");
            year.setText(Integer.toString(i));
            year.addAttribute("value", Integer.toString(i));
            if (defaultYear == i) {
                year.addAttribute("selected", "selected");
            }
        }
    }

    private void handleDataDrivenSelect(DataDrivenSelect obj,
                                        String path,
                                        Property property) {
        Element dataSelect = newElement(getCurrentElement(), "selectOptions");
        
        if (obj.isMultiple()) {
            dataSelect.addAttribute("multiple", "true");
        }

        PersistentDataQuery query = null;
        try {
            query = obj.getQuery();
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException(ex);
        }

        DataQuery items = SessionManager.getSession()
            .retrieveQuery(query.getName());

        while (items.next()) {
            String id = items.get("id").toString();
            String label = items.get("label").toString();
            Element optionElement = newElement(dataSelect, "option");
            optionElement.addAttribute("id", id);
            optionElement.addAttribute("label", label);
        }
    }
}
