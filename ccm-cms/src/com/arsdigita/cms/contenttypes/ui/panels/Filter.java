/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui.panels;

import com.arsdigita.xml.Element;


/**
 * Implementations of these interface are used by the 
 * {@link GenericOrganizationalUnitPanel} for filtering the objects in the 
 * various list.
 * @author Jens Pelzetter 
 */
public interface Filter {
    
    public String getLabel();
    
    public String getProperty();
    
   /**
     *
     * @return The SQL filter for filtering the object list.
     */
    String getFilter();

    /**
     * Generates the XML for the filter with the provided element as parent.
     * 
     * @parent The parent XML element for the XML of the filter.
     */
    void generateXml(Element parent);

    /**
     * Used to set the value of the filter if the HTTP request contains a value
     * for the filter.
     *
     * @param value The value from the input component.
     */
    void setValue(String value);
    
}
