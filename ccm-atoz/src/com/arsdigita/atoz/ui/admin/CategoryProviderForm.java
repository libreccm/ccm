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
import com.arsdigita.atoz.AtoZCategoryProvider;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.categorization.ui.CategoryPicker;
import com.arsdigita.util.Classes;


/**
 * 
 * 
 */
public class CategoryProviderForm extends ProviderForm {

    private CheckboxGroup m_compound;
    private CategoryPicker m_rootCategory;

    public CategoryProviderForm(ACSObjectSelectionModel provider) {
        super("categoryProvider", 
              AtoZCategoryProvider.class, provider);

        setMetaDataAttribute("title", "Category provider properties");
    }
        
    @Override
    protected void addWidgets() {
        super.addWidgets();

        // XXX Disabled till I implement the corresponding query to pull out items
        //m_compound = new CheckboxGroup("compound");
        //m_compound.addOption(new Option(Boolean.TRUE.toString(), "yes"));
        //add(m_compound);
        
        m_rootCategory = (CategoryPicker)Classes.newInstance(
            AtoZ.getConfig().getRootCategoryPicker(),
            new Class[] { String.class },
            new Object[] { "rootCategory" });
        ((SimpleComponent)m_rootCategory).setMetaDataAttribute("label", "Root category");
        add(m_rootCategory);
    }
    
    @Override
    protected void processWidgets(PageState state,
                                  AtoZProvider provider) {
        super.processWidgets(state, provider);
        
        AtoZCategoryProvider myprovider = (AtoZCategoryProvider)provider;
        myprovider.setCompound(false);
        //myprovider.setCompound(Boolean.TRUE.equals(m_compound.getValue(state)));
        
        Category root = m_rootCategory.getCategory(state);
        myprovider.setRootCategory(root);
    }
   
    @Override
    protected void initWidgets(PageState state,
                               AtoZProvider provider) {
        super.initWidgets(state, provider);

        AtoZCategoryProvider myprovider = (AtoZCategoryProvider)provider;
        if (provider != null) {
            //m_compound.setValue(state, new Boolean(myprovider.isCompound()));
            
            Category root = myprovider.getRootCategory();
            m_rootCategory.setCategory(state, root);
        }
    }

}