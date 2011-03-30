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
package com.arsdigita.london.navigation.ui.object;

import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.navigation.ui.AbstractObjectList;

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
 */
public class ComplexObjectList extends AbstractObjectList {

    public static final String CUSTOM_NAME = "customName";
    protected String m_customName = null;
    protected String m_filter = null;
    protected Map m_filterParameters = new HashMap();
    protected Map<String, String> m_customAttributes =
                                  new HashMap<String, String>();

    public void setCustomName(String name) {
        m_customName = name;
    }

    public String getCustomName() {
        return m_customName;
    }

    /**
     * Hinzufügen eines SQL-Filter zur Abfrage
     * Verarbeitet einen boolschen Filter, der SQL-konform Formatiert ist.
     * Siehe PostgreSQL-Handbuch zur where-Klausel
     * @param sqlfilter
     */
    public void setSQLFilter(String sqlfilter) {

        m_filter = sqlfilter;

    }

    public void setParameter(String parameterName, Object value) {

        m_filterParameters.put(parameterName, value);

    }

    public String getCustomAttribute(final String attribute) {
        return m_customAttributes.get(attribute);
    }

    public void addCustomAttribute(final String attribute, final String value) {
        m_customAttributes.put(attribute, value);
    }

    /* Diese Methode überschreibt die Methode aus der Eltern-Klasse, um
     * die SQL-Filter berücksichtigen zu können
     */
    @Override
    protected DataCollection getObjects(HttpServletRequest request,
                                        HttpServletResponse response) {
        DataCollection objects = super.getObjects(request, response);

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

        return objects;
    }

    /* Diese Methode wird vom Servlet aufgerufen */
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
