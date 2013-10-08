package com.arsdigita.navigation.ui.object;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
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
            final FilterFactory filterFactory = objects.getFilterFactory();
            final CompoundFilter compoundFilter = filterFactory.and();
            for (String value : values) {
                if (multiple) {
                    //When using multiple search we assume text input for now
                    if (catNameToCatId.containsKey(value)) {
                        final com.arsdigita.persistence.Filter filter = filterFactory.in(
                                "parent.id", "com.arsdigita.categorization.objectIDsInSubtree");
                        //filter.set("categoryID", value);
                        filter.set("categoryID", catNameToCatId.get(value));
                        compoundFilter.addFilter(filter);
                    }
                } else {
                    //Otherwise, we assume that we get the ID of a single category
                    final com.arsdigita.persistence.Filter filter = filterFactory.in(
                            "parent.id", "com.arsdigita.categorization.objectIDsInSubtree");
                    filter.set("categoryID", value);
                    compoundFilter.addFilter(filter);
                }
            }

            objects.addFilter(compoundFilter);
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
                categoriesStr.append(", ");
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
        if (multiple) {
        if ((values != null) && !values.isEmpty() && values.contains(category.getName())) {
            elem.addAttribute("selected", "selected");
            if (searchString.length() > 0) {
                searchString.append(' ');
            }
            searchString.append(category.getName());
        }
        } else {
            if ((values != null) && !values.isEmpty() && values.contains(category.getID().toString())) {
                elem.addAttribute("selected", "selected");
            }
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
        if ((value != null) && !value.isEmpty()) {
            final String[] tokens = value.split(separator);
            for (String token : tokens) {
                values.add(token.trim());
            }
        }
    }

}
