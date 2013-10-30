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

import com.arsdigita.atoz.AtoZProvider;
import com.arsdigita.atoz.ItemProvider;
import com.arsdigita.atoz.ui.AtoZGlobalizationUtil;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.ui.AbstractCategoryPicker;
import com.arsdigita.categorization.ui.CategoryPicker;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * 
 */
public class ItemProviderForm extends AbstractProviderForm {

    private CategoryPicker picker;
    private TextField loadPaths;

    public ItemProviderForm(final ACSObjectSelectionModel provider) {
        this(provider, null);
    }
        
    public ItemProviderForm(final ACSObjectSelectionModel provider,
                            final ApplicationInstanceAwareContainer parent) {
        super("itemProvider", ItemProvider.class, provider, parent);

        setMetaDataAttribute("title", "Item provider properties");
    }

    @Override
    protected void addWidgets() {
        super.addWidgets();

        loadPaths = new TextField(ItemProvider.LOAD_PATHS);
        //((SimpleComponent) loadPaths).setMetaDataAttribute("label", "Attributes to retrieve");        
        loadPaths.addValidationListener(new StringInRangeValidationListener(0, 200));

        picker = new AllCategoryPicker("rootCategory");
        //((SimpleComponent) picker).setMetaDataAttribute("label", "Category filter");
        ((SingleSelect) picker).addValidationListener(new NotNullValidationListener());

        add(new Label(AtoZGlobalizationUtil.globalize("atoz.ui.load_paths")));
        add(loadPaths);
        add(new Label(AtoZGlobalizationUtil.globalize("atoz.ui.category_filter")));
        add(picker);
    }

    @Override
    protected void processWidgets(final PageState state, final AtoZProvider provider) {
        super.processWidgets(state, provider);

        final ItemProvider myprovider = (ItemProvider) provider;

        myprovider.setCategory(picker.getCategory(state));
        myprovider.setLoadPaths((String) loadPaths.getValue(state));
    }

    @Override
    protected void initWidgets(final PageState state, final AtoZProvider provider) {
        super.initWidgets(state, provider);

        final ItemProvider myprovider = (ItemProvider) provider;
        if (provider != null) {
            //m_compound.setValue(state, new Boolean(myprovider.isCompound()));

            picker.setCategory(state, myprovider.getCategory());
            loadPaths.setValue(state, myprovider.getLoadPaths());
        }
    }

    private class AllCategoryPicker extends AbstractCategoryPicker {

        public AllCategoryPicker(final String name) {
            super(name);
        }

        protected void addOptions(final PageState state, final SingleSelect target) {
            target.addOption(new Option(null, "-- pick one --"));

            final DataCollection domains = SessionManager.getSession().retrieve("com.arsdigita.london.terms.Domain");
            domains.addPath("model.id");
            domains.addPath("model.objectType");
            domains.addOrder("title");
            while (domains.next()) {
                final Category rootCategory = (Category) DomainObjectFactory.newInstance((DataObject) domains.get(
                        "model"));
                categorySubtreePath(target, rootCategory, " > ");
            }
        }

        private void categorySubtreePath(final SingleSelect target, final Category root, final String join) {
            final DomainCollection cats = new DomainCollection(
                    SessionManager.getSession().retrieve(Category.BASE_DATA_OBJECT_TYPE));
            cats.addFilter("defaultAncestors like :ancestors")
                    .set("ancestors",
                         ((String) DomainServiceInterfaceExposer
                          .get(root, "defaultAncestors")) + "%");
            cats.addEqualsFilter("parents.link.isDefault", Boolean.TRUE);
            cats.addOrder("defaultAncestors");
            cats.addPath("parents.id");

            final Map path2cat = new TreeMap();
            final Map cat2path = new HashMap();

            path2cat.put(root.getName(), root);
            cat2path.put(root.getID(), root.getName());
            target.addOption(new Option(root.getOID().toString(),
                                        " +++++++++++++++++++++++++ "
                                        + root.getName()
                                        + " +++++++++++++++++++++++++ "));

            while (cats.next()) {
                final Category cat = (Category) cats.getDomainObject();
                final BigDecimal parent = (BigDecimal) cats.get("parents.id");

                if (parent == null) {
                    path2cat.put(cat.getName(), cat);
                    cat2path.put(cat.getID(), cat.getName());
                    target.addOption(new Option(cat.getOID().toString(), cat.getName()));
                } else {
                    String parentPath = (String) cat2path.get(parent);
                    String path = parentPath + join + cat.getName();
                    path2cat.put(path, cat);
                    cat2path.put(cat.getID(), path);
                    //
                    int breakPos = 0;
                    String prefix = "---";
                    while ((breakPos = 1 + parentPath.indexOf(join, breakPos)) > 0) {
                        prefix += "---";
                    }
                    target.addOption(new Option(cat.getOID().toString(), prefix + " " + cat.getName()));
                }
            }
        }

    }
}
