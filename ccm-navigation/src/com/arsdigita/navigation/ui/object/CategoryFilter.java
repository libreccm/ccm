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
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class CategoryFilter {

    private final String label;
    private String separator = " ";
    private boolean multiple = true;
    private final Category filterRootCat;
    private final List<String> values = new ArrayList<String>();
    private final Map<String, String> catNameToCatId = new HashMap<String, String>();

    public static CategoryFilter createCategoryFilter(final String label, final String categoryName) {
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

    public void applyFilter(final DataCollection objects) {
        if (!values.isEmpty()) {
            final List<String> categoryIds = new ArrayList<String>();
            for (String value : values) {
                //if (multiple) {
                    //When using multiple search we assume text input for now
                 //   if (catNameToCatId.containsKey(value)) {
                 //       categoryIds.add(catNameToCatId.get(value));
                 //   }
                //} else {
                    //Otherwise, we assume that we get the ID of a single category
                //    categoryIds.add(value);
                //}
                categoryIds.add(value);
            }

            final List<List<BigDecimal>> results = new ArrayList<List<BigDecimal>>();

            for (String categoryId : categoryIds) {
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

            final StringBuilder filterBuilder = new StringBuilder();
            for (List<BigDecimal> result : results) {
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

            objects.addFilter(filterBuilder.toString());
            //final com.arsdigita.persistence.Filter filter = objects.addNotInSubqueryFilter(
            //        "parent.id", "com.arsdigita.categorization.objectIDsInMultipleSubtrees");
            //filter.set("categoryIDs", categoryIds);
        }
    }

    public Element getXml() {
        final Element filter = new Element("filter");
        final Element categoriesElem = new Element("categories");
        final Element invalid = new Element("invalid");
        boolean invalidFound = false;
        final StringBuffer searchString = new StringBuffer();
        final StringBuffer categoriesStr = new StringBuffer();

        filter.addAttribute("type", "categoryFilter");
        filter.addAttribute("label", label);

        final CategoryCollection categories = filterRootCat.getChildren();
        categories.addOrder("name");

        Category category;
        while (categories.next()) {
            category = categories.getCategory();
            addCategoryToFilter(categoriesElem, category, searchString);
            if (categoriesStr.length() > 0) {
                categoriesStr.append("; ");
            }
            categoriesStr.append('"').append(category.getName()).append('"');
        }

        filter.newChildElement("searchString").setText(searchString.toString());
        filter.newChildElement("categoriesStr").setText(categoriesStr.toString());
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

    private void addCategoryToFilter(final Element parent,
                                     final Category category,
                                     final StringBuffer searchString) {
        final Element elem = new Element("category");
        elem.addAttribute("id", category.getID().toString());
//        if (multiple) {
//            if ((values != null) && !values.isEmpty() && values.contains(category.getName())) {
//                elem.addAttribute("selected", "selected");
////                if (searchString.length() > 0) {
////                    searchString.append(separator);
////                }
//                searchString.append(category.getName());
//                searchString.append(separator);
//            }
//        } else {
            if ((values != null) && !values.isEmpty() && values.
                    contains(category.getID().toString())) {
                elem.addAttribute("selected", "selected");
                searchString.append(category.getID().toString());
                searchString.append(separator);
            }
        //}
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
