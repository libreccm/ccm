/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.ui.filters;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.Search;
import com.arsdigita.search.filters.CategoryFilterSpecification;
import com.arsdigita.search.filters.CategoryFilterType;
import com.arsdigita.search.ui.FilterWidget;
import com.arsdigita.toolbox.ui.OIDParameter;
import com.arsdigita.xml.Element;


/**
 * A base component for presenting a list of categories
 * for filtering. This class needs to be subclassed to
 * implement the method for generating the list of
 * categories
 */
public abstract class CategoryFilterWidget extends FilterWidget {

    private Form m_form;
    private StringParameter includeCategoryHierarchy = new StringParameter("subcats");

    /**
     * Creates a new category filter component
     */
    public CategoryFilterWidget() {
        super(new CategoryFilterType(),
              new ArrayParameter(new OIDParameter(CategoryFilterType.KEY)));
    }

    public FilterSpecification getFilter(PageState state) {
        OID[] oids = (OID[])getValue(state);
        if (oids == null) {
            oids = new OID[0];
        }

        Category[] cats = new Category[oids.length];
        for (int i = 0 ; i < cats.length ; i++) {
            cats[i] = (Category)DomainObjectFactory.newInstance(oids[i]);
        }

        return new CategoryFilterSpecification(cats, searchSubcats(state));
    }

    private boolean searchSubcats(PageState state) {
        FormData fd = m_form.getFormData(state);
        boolean includeSubCats = false;
        if (fd != null) {
            includeSubCats = Boolean.TRUE.toString()
                .equals(fd.getString(includeCategoryHierarchy.getName()));
        }
        return includeSubCats;
    }


    public void register(Form form, FormModel model) {
        super.register(form, model);
        model.addFormParam(includeCategoryHierarchy);
        m_form = form;
    }


    /**
     * Returns a list of categories to display for
     * selection
     */
    public abstract Category[] getCategories(PageState state);

    public void generateBodyXML(PageState state,
                                Element parent) {
        super.generateBodyXML(state, parent);

        Element includeSubCats = Search.newElement("includeSubCats");
        includeSubCats.addAttribute("name", includeCategoryHierarchy.getName());
        includeSubCats.addAttribute("value", String.valueOf(searchSubcats(state)));
        parent.addContent(includeSubCats);

        OID[] oids = (OID[])getValue(state);
        Category[] cats = getCategories(state);

        for (int i = 0 ; i < cats.length ; i++) {
            Element type = Search.newElement("category");
            type.addAttribute("oid", cats[i].getOID().toString());
            type.addAttribute("title", cats[i].getName());
            type.addAttribute("description", cats[i].getDescription());
            if (oids != null) {
                for (int j = 0 ; j < oids.length ; j++) {
                    if (oids[j].equals(cats[i].getOID())) {
                        type.addAttribute("isSelected", "1");
                        break;
                    }
                }
            }
            parent.addContent(type);
        }
    }
}
