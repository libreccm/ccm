package com.arsdigita.london.navigation.ui.object;

import com.arsdigita.xml.Element;

/**
 * Implementations of these interface are used by the 
 * {@link CustomizableObjectList} for filtering the objects in the list.
 *
 * @author Jens Pelzetter
 * @version $Id$
 * @see CustomizableObjectList
 */
interface Filter {

    /**
     *
     * @return The SQL filter for filtering the object list.
     */
    String getFilter();

    /**
     *
     * @return XML representing the input component for the filter.
     */
    Element getXml();

    /**
     * Used to set the value of the filter if the HTTP request contains a value
     * for the filter.
     *
     * @param value The value from the input component.
     */
    void setValue(String value);
}
