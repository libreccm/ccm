package com.arsdigita.london.navigation.ui.object;

import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * <p>
 * An object list variant which can be filtered and sorted by the visitor of 
 * the website. The available filters and sort options are added in a JSP 
 * template. There are three kinds of filters yet:
 * </p>
 * <dl>
 * <dt><code>TextFilter</code></dt>
 * <dd>This filter filters the object list using a user provided string, which 
 * is put into the <code>WHERE</code> clause with <code>LIKE</code> operator. 
 * You might use this filter to allow the visitor to filter an object list for
 * items with a specific name.</dd>
 * <dt><code>SelectFilter</code></dt>
 * <dd>This filter traverses through the objects displayed by the list and 
 * determines all distinct values of a property. The visitor can choose one
 * of this values, and the displayed list will only contain items which where 
 * the property has the selected value.</dd>
 * <dt><code>CompareFilter</code></dt>
 * <dd>This filter also provides selectable options. But these options
 * can be configured by the developer in the template.</dd>
 * </dl>
 * <p>
 * If there is more than one filter, the values of all filters are combined
 * using <code>AND</code>.
 * </p>
 * <p>
 * This object list class was developed for displaying list of items from
 * the Sci modules (SciPublications and SciOrganization). For example, we use
 * this list to provide lists of publications which be filtered for publications
 * from a specific year, for a specific author and for a specific title. The
 * list can be sorted by the titles of the publications,  the years of the
 * publications and the (surnames of the) authors of the publications.
 * </p>
 * <p>
 * As an example how to use this object list in a JSP template here are the
 * relevant parts from the template for the publication list:
 * </p>
 * <pre>
 * {@code
 * ...
 * <define:component name="itemList"
                           classname="com.arsdigita.london.navigation.ui.object.CustomizableObjectList"/>
 * ...
 *   <jsp:scriptlet>
      CustomizableObjectList objList = (CustomizableObjectList) itemList;
      objList.setDefinition(new CMSDataCollectionDefinition());
      objList.setRenderer(new CMSDataCollectionRenderer());
      objList.getDefinition().setObjectType("com.arsdigita.cms.contenttypes.Publication");
      objList.getDefinition().setDescendCategories(false);
      objList.addTextFilter("title", "title");
      objList.addTextFilter("authors.surname", "author");
      objList.addSelectFilter("yearOfPublication", "year", true, true, true, true);
      objList.addSortField("title", "title asc");
      objList.addSortField("yearAsc", "yearOfPublication asc");
      objList.addSortField("yearDesc", "yearOfPublication desc");
      objList.addSortField("authors", "authors.surname asc, authors.givenname asc");
      objList.getDefinition().addOrder(objList.getOrder(request.getParameter("sort")));

      objList.getRenderer().setPageSize(20);
      objList.getRenderer().setSpecializeObjects(true);

      </jsp:scriptlet>
 * ...
 * }
 * </pre>
 * <p>
 * You may notice the line
 * <code>objList.getDefinition().addOrder(objList.getOrder(request.getParameter("sort")));</code>.
 * This line may looks a bit weird to you. The reason is that it is not possible
 * to access the <code>DataCollectionDefinition</code> from the methods in this
 * class. If you try call the <code>addOrder()</code> from within this class
 * you will cause an locking error. 
 * </p>
 * @author Jens Pelzetter
 * @version $Id$
 * @see Filter
 * @see TextFilter
 * @see SelectFilter
 * @see CompareFilter
 */
public class CustomizableObjectList extends ComplexObjectList {

    private static final Logger logger = Logger.getLogger(
            CustomizableObjectList.class);
    /**
     * The filters for the list. We use an {@link LinkedHashMap} here to
     * preserve the insertation order.
     *
     */
    private final Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
    /**
     * The available sort fields. We use an {@link LinkedHashMap} here to
     * preserve the insertation order.
     *
     */
    private final Map<String, String> sortFields = new LinkedHashMap<String, String>();
    /**
     * Sort by which property?
     */
    private String sortBy = null;

    /**
     * Adds a new text filter to the list.
     *
     * @param property The property to filter using the new filter.
     * @param label The label of the filter and its component.
     * @see TextFilter#TextFilter(java.lang.String, java.lang.String) 
     */
    public void addTextFilter(final String property, final String label) {
        TextFilter filter;

        filter = new TextFilter(property, label);
        filters.put(label, filter);
    }

    /**
     * Adds a new compare filter to the list.
     *
     * @param property The property to filter using the new filter.
     * @param label The label of the filter and its component.
     * @param allOption Add an <em>all</em> option to the filter.
     * @param allOptionIsDefault Is the all option the default?
     * @param propertyIsNumeric Is the property to filter numeric?
     * @return The new filter. Options can be added to the filter by calling
     * the {@link CompareFilter#addOption(java.lang.String, java.lang.String)} or
     * the {@link CompareFilter#addOption(java.lang.String, com.arsdigita.london.navigation.ui.object.CompareFilter.Operators, java.lang.String)}
     * method.
     * @see CompareFilter#CompareFilter(java.lang.String, java.lang.String, boolean, boolean, boolean)
     *
     */
    public CompareFilter addCompareFilter(final String property,
                                          final String label,
                                          final boolean allOption,
                                          final boolean allOptionIsDefault,
                                          final boolean propertyIsNumeric) {
        CompareFilter filter;

        filter = new CompareFilter(property,
                                   label,
                                   allOption,
                                   allOptionIsDefault,
                                   propertyIsNumeric);
        filters.put(label, filter);

        return filter;
    }

    /**
     * Adds a select filter.
     *
     * @param property The property to filter.
     * @param label The label of the filter.
     * @param reverseOptions Reverse the order of the options.
     * @param allOption Add an all option?
     * @param allOptionIsDefault Is the all option the default.
     * @param propertyIsNumeric Is the property numeric?
     * @see SelectFilter#SelectFilter(java.lang.String, java.lang.String, com.arsdigita.london.navigation.ui.object.CustomizableObjectList, boolean, boolean, boolean, boolean) 
     */
    public void addSelectFilter(final String property,
                                final String label,
                                final boolean reverseOptions,
                                final boolean allOption,
                                final boolean allOptionIsDefault,
                                final boolean propertyIsNumeric) {
        SelectFilter filter;

        filter = new SelectFilter(property,
                                  label,
                                  this,
                                  reverseOptions,
                                  allOption,
                                  allOptionIsDefault,
                                  propertyIsNumeric);
        filters.put(label, filter);
    }

    /**
     * Add a sort field option.
     *
     * @param label The label of the sort field.
     * @param property The property to sort by.
     */
    public void addSortField(final String label, final String property) {
        sortFields.put(label, property);
    }

    /**
     * Determines which property is currently used for sorting.
     *
     * @param id The id of the sort field to sort by.
     * @return The property to sort by.
     */
    public String getOrder(final String id) {
        String order = sortFields.get(id);
        if ((order == null) || order.isEmpty()) {
            return new ArrayList<String>(sortFields.values()).get(0);
        }
        return order;
    }

    /**
     * This overwritten version of the <code>getObjects</code> method evaluates
     * the parameters in HTTP request for the filters and creates an
     * appropriate SQL filter and sets this filter.
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    protected DataCollection getObjects(HttpServletRequest request,
                                        HttpServletResponse response) {
        //Set filters (using the SQL)
        StringBuilder sqlFilters = new StringBuilder();
        for (Map.Entry<String, Filter> filterEntry : filters.entrySet()) {
            if ((filterEntry.getValue().getFilter() == null)
                || (filterEntry.getValue().getFilter().isEmpty())) {
                continue;
            }

            if (sqlFilters.length() > 0) {
                sqlFilters.append(" AND ");
            }
            sqlFilters.append(filterEntry.getValue().getFilter());
        }

        logger.debug(String.format("filters: %s", sqlFilters));
        if (sqlFilters.length() > 0) {
            setSQLFilter(sqlFilters.toString());
        }

        DataCollection objects = super.getObjects(request, response);

        return objects;
    }

    /**
     * <p>
     * Generates the XML for the list. The root element for the list is
     * <code>customizableObjectList</code>. The available filters are
     * put into a <code>filters</code> element.
     * </p>
     * <p>
     * The available sort fields are put into a <code>sortFields</code> element.
     * This element has also an attribute indicating the current selected sort
     * field.
     * </p>
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        //Some stuff for the list (copied from ComplexObjectList)
        Element content = Navigation.newElement("customizableObjectList");

        if (m_customName != null) {
            content.addAttribute(CUSTOM_NAME, m_customName);
        }

        for (Map.Entry<String, String> attribute : m_customAttributes.entrySet()) {
            content.addAttribute(attribute.getKey(), attribute.getValue());
        }

         //Look for values for the filters and the sort fields in the HTTP
        //request. We are not using the Bebop parameters for two reasons:
        //- They have to be registered very early, so we can't add new parameters
        //  from a JSP.
        //- The HttpRequest is available here.
        //So we use the HTTP request directly, which allows use to use a
        //dedicated parameter for each of the filters.
        for (Map.Entry<String, Filter> filterEntry : filters.entrySet()) {
            String value = request.getParameter(filterEntry.getKey());

            if ((value != null) && !value.isEmpty()) {
                filterEntry.getValue().setValue(value);
            }
        }

        //Look for a sort parameter. If one is found, use one to sort the data
        //collection (if it is a valid value). If no sort parameter is found,
        //use the first sort field as default.
        String sortByKey = request.getParameter("sort");
        sortBy = sortFields.get(sortByKey);
        if (((sortBy == null)
             || sortBy.isEmpty()
             || !sortFields.containsKey(sortBy))
            && !sortFields.isEmpty()) {
            sortByKey = new ArrayList<String>(sortFields.keySet()).get(0);
            sortBy = new ArrayList<String>(sortFields.values()).get(0);
        }

        Element controls = content.newChildElement("controls");

        Element filterElems = controls.newChildElement("filters");
        for (Map.Entry<String, Filter> filterEntry : filters.entrySet()) {
            filterElems.addContent(filterEntry.getValue().getXml());
        }
       
        Element sortFieldElems = controls.newChildElement("sortFields");
        sortFieldElems.addAttribute("sortBy", sortByKey);
        for (Map.Entry<String, String> sortField : sortFields.entrySet()) {
            Element sortFieldElem = sortFieldElems.newChildElement("sortField");
            sortFieldElem.addAttribute("label", sortField.getKey());
        }

        //Add object list
        content.addContent(generateObjectListXML(request, response));

        return content;
    }
}
