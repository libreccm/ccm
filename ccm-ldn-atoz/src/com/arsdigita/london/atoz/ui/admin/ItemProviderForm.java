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

package com.arsdigita.london.atoz.ui.admin;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.london.atoz.AtoZItemProvider;
import com.arsdigita.london.atoz.AtoZProvider;
import com.arsdigita.london.util.ui.AbstractCategoryPicker;
import com.arsdigita.london.util.ui.CategoryPicker;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ItemProviderForm extends ProviderForm {

    private CategoryPicker m_picker;
    private TextField m_loadPaths;

    public ItemProviderForm(ACSObjectSelectionModel provider) {
        super("itemProvider", AtoZItemProvider.class, provider);

        setMetaDataAttribute("title", "Item provider properties");
    }

    protected void addWidgets() {
        super.addWidgets();

        m_loadPaths = new TextField(AtoZItemProvider.LOAD_PATHS);
        ((SimpleComponent)m_loadPaths).setMetaDataAttribute("label", "Attributes to retrieve");
        add(m_loadPaths);
        m_loadPaths.addValidationListener(new StringInRangeValidationListener(0, 200));
        m_picker = new AllCategoryPicker("rootCategory");
        ((SimpleComponent)m_picker).setMetaDataAttribute("label", "Category filter");
        ((SingleSelect)m_picker).addValidationListener(new NotNullValidationListener());
        add(m_picker);
    }

    protected void processWidgets(PageState state,
                                  AtoZProvider provider) {
        super.processWidgets(state, provider);

        AtoZItemProvider myprovider = (AtoZItemProvider)provider;

        myprovider.setCategory(m_picker.getCategory(state));
        myprovider.setLoadPaths( (String) m_loadPaths.getValue(state));
    }

    protected void initWidgets(PageState state,
                               AtoZProvider provider) {
        super.initWidgets(state, provider);

        AtoZItemProvider myprovider = (AtoZItemProvider)provider;
        if (provider != null) {
            //m_compound.setValue(state, new Boolean(myprovider.isCompound()));

            m_picker.setCategory(state, myprovider.getCategory());
            m_loadPaths.setValue(state, myprovider.getLoadPaths());
        }
    }

    private class AllCategoryPicker extends AbstractCategoryPicker {

        public AllCategoryPicker(String name) {
            super(name);
        }

        protected void addOptions(PageState state,
                                  SingleSelect target) {
            target.addOption(new Option(null, "-- pick one --"));

            DataCollection domains = SessionManager.getSession()
                .retrieve("com.arsdigita.london.terms.Domain");
            domains.addPath("model.id");
            domains.addPath("model.objectType");
            domains.addOrder("title");
            while (domains.next()) {
                Category rootCategory = (Category)
                    DomainObjectFactory.newInstance( (DataObject) domains.get("model"));
                categorySubtreePath(target, rootCategory, " > ");
            }
        }

        private void categorySubtreePath(SingleSelect target, Category root, String join) {
            DomainCollection cats = new DomainCollection(
                SessionManager.getSession().retrieve(Category.BASE_DATA_OBJECT_TYPE)
            );
            cats.addFilter("defaultAncestors like :ancestors")
                .set("ancestors",
                     ((String)DomainServiceInterfaceExposer
                      .get(root, "defaultAncestors")) + "%");
            cats.addEqualsFilter("parents.link.isDefault", Boolean.TRUE);
            cats.addOrder("defaultAncestors");
            cats.addPath("parents.id");

            Map path2cat = new TreeMap();
            Map cat2path = new HashMap();

            path2cat.put(root.getName(), root);
            cat2path.put(root.getID(), root.getName());
            target.addOption(new Option(root.getOID().toString(),
                    " +++++++++++++++++++++++++ "
                    + root.getName()
                    + " +++++++++++++++++++++++++ "));

            while (cats.next()) {
                Category cat = (Category)cats.getDomainObject();
                BigDecimal parent = (BigDecimal)cats.get("parents.id");

                if (parent == null) {
                    path2cat.put(cat.getName(), cat);
                    cat2path.put(cat.getID(), cat.getName());
                    target.addOption(new Option(cat.getOID().toString(), cat.getName()));
                } else {
                    String parentPath = (String)cat2path.get(parent);
                    String path = parentPath + join + cat.getName();
                    path2cat.put(path, cat);
                    cat2path.put(cat.getID(), path);
                    //
                    int breakPos = 0;
                    String prefix = "---";
                    while ((breakPos = 1+parentPath.indexOf(join, breakPos)) > 0) {
                        prefix += "---";
                    }
                    target.addOption(new Option(cat.getOID().toString(), prefix + " " + cat.getName()));
                }
            }
        }

    }

}
