/**
 * ComplexObjectList.java
 *
 * Autor: Sören Bernstein
 *
 * Diese Klasse realisiert eine ObjectList für Navigation,
 * der man Filterbefehle für die SQL-Abfrage mitgeben kann.
 * Auf diese Weise lassen sich Objekte listen, die bestimmte
 * Kriterien erfüllen.
 *
 * Angelegt wurde Sie für die Auflistung der aktuellen News
 * und Veranstalungen auf einer Navigationsseite.
 */
package com.arsdigita.navigation.ui.object;

import com.arsdigita.navigation.Navigation;
import com.arsdigita.navigation.ui.AbstractObjectList;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.xml.Element;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A complex object list
 * 
 * An object list which will accept SQL filters to customize the content easily.
 * 
 * @author Sören Bernstein <sbernstein@quasiweb.de>
 */
public class ComplexObjectList extends AbstractObjectList {

    public static final String CUSTOM_NAME = "customName";
    protected String m_customName = null;
    protected String m_filter = null;
    protected Map m_filterParameters = new HashMap();
    protected Map<String, String> m_customAttributes =
            new HashMap<String, String>();

    // Getter / Setter
    
    /**
     * Sets a custom name for this list.
     * @param name the list name
     */
    public void setCustomName(String name) {
        m_customName = name;
    }

    /**
     * Gets the costum name for this list.
     * @return the list name
     */
    public String getCustomName() {
        return m_customName;
    }

    /**
     * Adds a custom attribute
     * 
     * @param attribute
     * @param value 
     */
    public void addCustomAttribute(final String attribute, final String value) {
        m_customAttributes.put(attribute, value);
    }

    /**
     * Gets a custom attribute
     * 
     * @param attribute
     * @return 
     */
    public String getCustomAttribute(final String attribute) {
        return m_customAttributes.get(attribute);
    }


    /**
     * Add a SQL filter to query.
     * This filter can handle wildcards which have to be set with {@link #setParameter(java.lang.String, java.lang.Object)}
     * 
     *See PostgreSQL handbook about where clause
     * 
     * @param sqlfilter the sql filter
     */
    public void setSQLFilter(String sqlfilter) {

        m_filter = sqlfilter;

    }

    /**
     * Set parameter for for sql filter.
     * 
     * @param parameterName the parameter name of the 
     * @param value the value attached to the parameter
     */
    public void setParameter(String parameterName, Object value) {

        m_filterParameters.put(parameterName, value);

    }

    /** 
     * Get all objects for this list.
     * 
     * Overrides the parent class to allow for sql filter
     */
    @Override
    protected DataCollection getObjects(HttpServletRequest request,
                                        HttpServletResponse response) {
        DataCollection objects = super.getObjects(request, response);

        // Don't try do anything with a null object
        if (objects != null) {

            // Setze den Filter
            if (m_filter != null) {

                FilterFactory fact = objects.getFilterFactory();
                Filter sql = fact.simple(m_filter);

                // Setze die Parameter
                Iterator params = m_filterParameters.entrySet().iterator();
                while (params.hasNext()) {

                    Map.Entry entry = (Map.Entry) params.next();
                    String param = (String) entry.getKey();
                    Object value = (Object) entry.getValue();
                    if (value != null) {
                        sql.set(param, value);
                    }

                }

                objects.addFilter(sql);

            }
        }
        return objects;
    }

    /* This method will be called by the servlet */
    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        Element content = Navigation.newElement("complexObjectList");

        if (m_customName != null) {
            content.addAttribute(CUSTOM_NAME, m_customName);
        }

        for (Map.Entry<String, String> attribute : m_customAttributes.entrySet()) {
            content.addAttribute(attribute.getKey(), attribute.getValue());
        }

        content.addContent(generateObjectListXML(request, response));

        return content;
    }
}
