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
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class CategoryFilter {

    private final String label;
    private final Category filterRootCat;
//    private boolean descendCategories = true;
    //private String value;
    private final List<String> values = new ArrayList<String>();

    public static CategoryFilter createCategoryFilter(final String label, final String categoryName) {
        final DataCollection collection = SessionManager.getSession().retrieve(
                Category.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter(Category.NAME, categoryName);

        if (collection.next()) {
            final Category category = (Category) DomainObjectFactory.newInstance(
                    collection.getDataObject());
            return new CategoryFilter(label, category);
        } else {
            throw new IllegalArgumentException(String.format(
                    "A category with the provided name '%s' does not exist", categoryName));
        }
    }

    public CategoryFilter(final String label, final Category filterRootCat) {
        this.label = label;
        this.filterRootCat = filterRootCat;
    }

//    public boolean getDescendCategories() {
//        return descendCategories;
//    }
//
//    public void setDescendCategories(final boolean descendCategories) {
//        this.descendCategories = descendCategories;
//    }

    public void applyFilter(final DataCollection objects) {
//        for(String value : values) {
//            if ((value != null) && !value.isEmpty()) {                
//                if(descendCategories) {
//                    com.arsdigita.persistence.Filter filter = objects.addInSubqueryFilter("parent.id",
//                                            "com.arsdigita.categorization.objectIDsInSubtree");
//                    filter.set("categoryID", value);
//                    objects.addFilter(filter);
//                } else {
//                    objects.addEqualsFilter("parent.categories.id", value);
//                }
//            }
//        }


        if (!values.isEmpty()) {
//            if (descendCategories) {
                final FilterFactory filterFactory = objects.getFilterFactory();
                final CompoundFilter compoundFilter = filterFactory.and();
                for (String value : values) {
                    final com.arsdigita.persistence.Filter filter = filterFactory.in("parent.id",
                                                                                     "com.arsdigita.categorization.objectIDsInSubtree");
                    filter.set("categoryID", value);
                    compoundFilter.addFilter(filter);
                }

                objects.addFilter(compoundFilter);

//                com.arsdigita.persistence.Filter filter = objects.addInSubqueryFilter("parent.id",
//                                            "com.arsdigita.categorization.objectIDsInSubtree");
//                filter.set("categoryID", values.get(0));

//            } else {
//                final com.arsdigita.persistence.Filter filter = objects.addFilter(
//                        "parent.categories.id IN :categories");
//                filter.set("categories", values);
                //objects.addEqualsFilter("parent.categories.id", values.get(0));
//                final FilterFactory filterFactory = objects.getFilterFactory();
//                final CompoundFilter compoundFilter = filterFactory.or();
//                for (String value : values) {
//                    final com.arsdigita.persistence.Filter filter = filterFactory.equals(
//                            "parent.categories.id", value);
//                    compoundFilter.addFilter(filter);
//                }
//                
//                objects.addFilter(compoundFilter);
//            }
        }
    }

    public Element getXml() {
        final Element filter = new Element("filter");
        filter.addAttribute("name", "categoryFilter");


        filter.addAttribute("label", label);

        final CategoryCollection categories = filterRootCat.getChildren();
        categories.addOrder("name");

        Category category;
        while (categories.next()) {
            category = categories.getCategory();
            addCategoryToFilter(filter, category);
        }

        return filter;
    }

    private void addCategoryToFilter(final Element parent, final Category category) {
        final Element elem = new Element("category");
        elem.addAttribute("id", category.getID().toString());
        //if ((value != null) && !value.isEmpty() && value.equals(category.getID().toString())) {
        if ((values != null) && !values.isEmpty() && values.contains(category.getID().toString())) {
            elem.addAttribute("selected", "selected");
        }
        elem.setText(category.getName());
        parent.addContent(elem);
    }

    public void setValue(final String value) {
        if ((value != null) && !value.isEmpty()) {
            final String[] tokens = value.split(" ");
            for (String token : tokens) {
                values.add(token.trim());
            }
        }
    }

}
