/*
 * Copyright (C) 2005 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.atoz.siteproxy.ui.admin;

import com.arsdigita.atoz.ui.admin.ProviderForm;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.atoz.AtoZ;
import com.arsdigita.atoz.AtoZProvider;
import com.arsdigita.atoz.siteproxy.AtoZSiteProxyProvider;
import com.arsdigita.atoz.ui.admin.ProviderForm;
import com.arsdigita.london.util.ui.CategoryPicker;
import com.arsdigita.util.Classes;

public class SiteProxyProviderForm extends ProviderForm {

    private CategoryPicker m_category_picker;

    public SiteProxyProviderForm(ACSObjectSelectionModel provider) {
        super("siteProxyProvider", AtoZSiteProxyProvider.class, provider);

        setMetaDataAttribute("title", "SiteProxy provider properties");
    }

    protected void addWidgets() {
        super.addWidgets();
        m_category_picker = (CategoryPicker) Classes.newInstance(AtoZ
                .getConfig().getRootCategoryPicker(),
                new Class[] { String.class }, new Object[] { "rootCategory" });
        ((SimpleComponent) m_category_picker).setMetaDataAttribute("label",
                "Root category");
        add(m_category_picker);
    }

    protected void initWidgets(PageState state, AtoZProvider provider) {
        super.initWidgets(state, provider);
        AtoZSiteProxyProvider siteProxyProvider = (AtoZSiteProxyProvider) provider;
        if (siteProxyProvider != null)
            m_category_picker.setCategory(state, siteProxyProvider
                    .getCategory());
    }

    protected void processWidgets(PageState state, AtoZProvider provider) {
        super.processWidgets(state, provider);

        AtoZSiteProxyProvider siteProxyProvider = (AtoZSiteProxyProvider) provider;

        siteProxyProvider.setCategory(m_category_picker.getCategory(state));
    }
}
