/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.atoz.ui.admin;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZProvider;
import com.arsdigita.atoz.CategoryProvider;
import com.arsdigita.atoz.ui.AtoZGlobalizationUtil;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.ui.CategoryPicker;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.util.Classes;

/**
 * 
 * 
 */
public class CategoryProviderForm extends AbstractProviderForm {

    //private CheckboxGroup m_compound;
    private CategoryPicker rootCategory;

    public CategoryProviderForm(final ACSObjectSelectionModel provider) {
        super("categoryProvider", CategoryProvider.class, provider);

        setMetaDataAttribute("title", "Category provider properties");
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        // XXX Disabled till I implement the corresponding query to pull out items
        //m_compound = new CheckboxGroup("compound");
        //m_compound.addOption(new Option(Boolean.TRUE.toString(), "yes"));
        //add(m_compound);

        rootCategory = (CategoryPicker) Classes.newInstance(
                AtoZ.getConfig().getRootCategoryPicker(),
                new Class[]{String.class},
                new Object[]{"rootCategory"});
        ((SimpleComponent) rootCategory).setMetaDataAttribute("label", "Root category");

        add(new Label(AtoZGlobalizationUtil.globalize("atoz.ui.category_picker.root_category")));
        add(rootCategory);
    }

    @Override
    protected void processWidgets(final PageState state, final AtoZProvider provider) {
        super.processWidgets(state, provider);

        final CategoryProvider myprovider = (CategoryProvider) provider;
        myprovider.setCompound(false);
        //myprovider.setCompound(Boolean.TRUE.equals(m_compound.getValue(state)));

        final Category root = rootCategory.getCategory(state);
        myprovider.setRootCategory(root);
    }

    @Override
    protected void initWidgets(final PageState state, final AtoZProvider provider) {
        super.initWidgets(state, provider);

        final CategoryProvider myprovider = (CategoryProvider) provider;
        if (provider != null) {
            //m_compound.setValue(state, new Boolean(myprovider.isCompound()));

            final Category root = myprovider.getRootCategory();
            rootCategory.setCategory(state, root);
        }
    }

}
