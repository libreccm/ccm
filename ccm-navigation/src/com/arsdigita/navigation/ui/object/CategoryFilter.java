package com.arsdigita.navigation.ui.object;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This filter allows it to filter the objects in a customisable object list by categories assigned
 * to them. This filter is for example useful to filter objects after keywords. The keywords are
 * managed as an additional category system. The include the filter into a customisable object list
 * are special JSP template is required.
 *
 * First you have to add a customisable object list to the page by
 *
 * <pre>
 * {@code
 * <define:component name="itemList"
 * classname="com.arsdigita.navigation.ui.object.CustomizableObjectList"/> }
 * <
 * /pre>
 *
 * In a scriptlet block following your component definitions you have to set several parameters for
 * the object list (see {@link CustomizableObjectList}) To add and configure a category filter to
 * the list add these lines:
 *
 * <pre>
 * {@code
 * CustomizableObjectList objList = (CustomizableObjectList) itemList;
 *
 * ...
 *
 * CategoryFilter catFilter = objList.addCategoryFilter("labelOfCatFilter", "rootCategory");
 * catFilter.setSeparator(";");
 * }
 * </pre>
 *
 * {@link CustomizableObjectList#addCategoryFilter(java.lang.String, java.lang.String)} adds a
 * category filter to the object list. The method requires two parameters: An identifier for the
 * label of the category filter (localisation for the label has to done in theme at the moment) and
 * the name of the root category of the category system to use for the filter.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class CategoryFilter {

    /**
     * Label of the category filter
     */
    private final String label;
    /**
     * Separator for multiple categories.
     */
    private String separator = " ";
    /**
     * Enable multiple selection?
     */
    private boolean multiple = true;
    /**
     * Root category. Categories below this category are used for the filter.
     */
    private final Category filterRootCat;
    /**
     * Currently selected categories.
     */
    private final List<String> values = new ArrayList<String>();
    /**
     * Used to to translate between the name a category and the id of the category.
     */
    private final Map<String, String> catNameToCatId = new HashMap<String, String>();

    /**
     * Factory method for creating a category filter.
     *
     * @param label
     * @param categoryName
     * @return
     */
    public static CategoryFilter createCategoryFilter(final String label,
                                                      final String categoryName) {
        final DataCollection collection = SessionManager.getSession().retrieve(
                Category.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter(Category.NAME, categoryName);

        if (collection.next()) {
            final Category category = (Category) DomainObjectFactory.newInstance(
                    collection.getDataObject());
            collection.close();
            return new CategoryFilter(label, category);
        } else {
            throw new IllegalArgumentException(String.format(
                    "A category with the provided name '%s' does not exist", categoryName));
        }
    }

    public CategoryFilter(final String label, final Category filterRootCat) {
        this.label = label;
        this.filterRootCat = filterRootCat;

        final CategoryCollection categories = filterRootCat.getChildren();
        Category category;
        while (categories.next()) {
            category = categories.getCategory();
            catNameToCatId.put(category.getName(), category.getID().toString());
        }
    }

    /**
     * Apply the filter the {@link DataCollection} of the object list. The solution for the query is
     * a bit weird, due to limitations of PDL. The simple solution would be to use the predefined
     * query {@code objectIDsInMultipleSubtrees} from the {@code Category.pdl} of the module as a
     * subquery in the {@code where} clause, like this:
     * {@code WHERE parent.id ALL ($objectIDsInMultipleSubtrees)}. But PDL does not support the
     * {@code ALL} operator. So we have to use another solution. First we retrieve the IDs of
     * <emph>all</emph> objects assigned to each selected category using the
     * {@code objectIDsInSubtree} query. Using this IDs we build a long filter. For each selected
     * category there is an segment like this: {@code (parent.id IN (1,2,3,4)} The IDs for the
     * {@code IN} operator a the ones with the IDs of all objects in category. All segments are
     * combined with {@code AND}. If no items are assigned to a category, a segment with {@code 0}
     * as ID is added for this category. Because there will never be an object in the database with
     * the ID {@code 0} this works.
     *
     * @param objects
     */
    public void applyFilter(final DataCollection objects) {
        if (!values.isEmpty()) {
            final List<String> categoryIds = new ArrayList<String>();
            for (String value : values) {
                categoryIds.add(value);
            }

            final List<List<BigDecimal>> results = new ArrayList<List<BigDecimal>>();

            for (String categoryId : categoryIds) {
                retrieveItemsInCategories(categoryId, results);
            }

            final StringBuilder filterBuilder = new StringBuilder();
            for (List<BigDecimal> result : results) {
                buildFilterCondition(filterBuilder, result);
            }

            objects.addFilter(filterBuilder.toString());
        }
    }

    private void retrieveItemsInCategories(final String categoryId,
                                           final List<List<BigDecimal>> results) {
        final DataQuery query = SessionManager.getSession().retrieveQuery(
                "com.arsdigita.categorization.objectIDsInSubtree");
        query.setParameter("categoryID", categoryId);

        final List<BigDecimal> result = new ArrayList<BigDecimal>();
        while (query.next()) {
            result.add((BigDecimal) query.get("id"));
        }

        if (result.isEmpty()) {
            result.add(BigDecimal.ZERO);
        }

        results.add(result);
    }

    private void buildFilterCondition(final StringBuilder filterBuilder,
                                      final List<BigDecimal> result) {
        if (filterBuilder.length() > 0) {
            filterBuilder.append(" AND ");
        }

        final StringBuilder conditionBuilder = new StringBuilder();
        for (BigDecimal id : result) {
            if (conditionBuilder.length() > 0) {
                conditionBuilder.append(',');
            }
            conditionBuilder.append(id.toString());
        }
        filterBuilder.append("(parent.id IN (");
        filterBuilder.append(conditionBuilder);
        filterBuilder.append("))");
    }

    /**
     * Creates the XML for the category filter.
     *
     * @return
     */
    public Element getXml() {
        final Element filter = new Element("filter");
        final Element categoriesElem = new Element("categories");
        final Element invalid = new Element("invalid");
        boolean invalidFound = false;
        final StringBuffer searchString = new StringBuffer();
        //final StringBuffer categoriesStr = new StringBuffer();

        filter.addAttribute("type", "categoryFilter");
        filter.addAttribute("label", label);

        final CategoryCollection categories = filterRootCat.getChildren();
        //categories.addOrder("name");
        categories.sort(true);

        Category category;
        while (categories.next()) {
            category = categories.getCategory();
            if (category.hasChildCategories()) {
                addCategoryGroupToFilter(categoriesElem, category, searchString);
            } else {
                addCategoryToFilter(categoriesElem, category, searchString);
//                if (categoriesStr.length() > 0) {
//                    categoriesStr.append("; ");
//                }
//                categoriesStr.append('"').append(category.getName()).append('"');
            }
        }

        filter.newChildElement("searchString").setText(searchString.toString());
        //filter.newChildElement("categoriesStr").setText(categoriesStr.toString());
        filter.newChildElement("separator").setText(separator);
        final Element multipleElem = filter.newChildElement("multiple");
        if (multiple) {
            multipleElem.setText("true");
        } else {
            multipleElem.setText("false");
        }

        if (!multiple) {
            for (String value : values) {
                if (!catNameToCatId.containsKey(value)) {
                    invalid.newChildElement("value").setText(value);
                    invalidFound = true;
                }
            }
        }

        filter.addContent(categoriesElem);
        if (invalidFound) {
            filter.addContent(invalid);
        }

        return filter;
    }

    private void addCategoryGroupToFilter(final Element parent,
                                          final Category category,
                                          final StringBuffer searchString) {
        final Element elem = parent.newChildElement("categoryGroup");
        elem.addAttribute("label", category.getName());
        final CategoryCollection childs = category.getChildren();
        childs.sort(true);

        while(childs.next()) {
            final Category child = childs.getCategory();
            addCategoryToFilter(elem, child, searchString);
        }
    }
    
    private void addCategoryToFilter(final Element parent,
                                     final Category category,
                                     final StringBuffer searchString) {
        final Element elem = new Element("category");
        elem.addAttribute("id", category.getID().toString());
        if ((values != null) && !values.isEmpty() && values.
                contains(category.getID().toString())) {
            elem.addAttribute("selected", "selected");
            searchString.append(category.getID().toString());
            searchString.append(separator);
        }

        elem.setText(category.getName());
        parent.addContent(elem);
    }

    public String getLabel() {
        return label;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(final String separator) {
        this.separator = separator;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(final boolean multiple) {
        this.multiple = multiple;
    }

    public void setValue(final String value) {
        values.clear();
        if ((value != null) && !value.isEmpty()) {
            final String[] tokens = value.split(separator);
            for (String token : tokens) {
                values.add(token.trim());
            }
        }
    }
}
