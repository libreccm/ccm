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
package com.arsdigita.cms.ui.search;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.categorization.Category;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.search.CreationDateFilterType;
import com.arsdigita.cms.search.CreationUserFilterType;
import com.arsdigita.cms.search.LastModifiedDateFilterType;
import com.arsdigita.cms.search.LastModifiedUserFilterType;
import com.arsdigita.cms.search.LaunchDateFilterType;
import com.arsdigita.cms.ui.ContentSectionPage;
import com.arsdigita.search.FilterType;
import com.arsdigita.search.Search;
import com.arsdigita.search.ui.BaseQueryComponent;
import com.arsdigita.search.ui.filters.DateRangeFilterWidget;
import com.arsdigita.search.ui.filters.PartyFilterWidget;
import com.arsdigita.search.ui.filters.PermissionFilterComponent;
import com.arsdigita.search.ui.filters.SimpleCategoryFilterWidget;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a basic query form for CMS admin pages
 * that automatically adds components for the maximal set of
 * filters supported by the current search query engine.
 */
public class ItemQueryComponent extends BaseQueryComponent {

    private String m_context;

    public ItemQueryComponent(String context) {
        m_context = context;

        if (Search.getConfig().isIntermediaEnabled()) {
            add(new LaunchDateFilterWidget(new LaunchDateFilterType(),
                    LaunchDateFilterType.KEY));
        }

        if (Search.getConfig().isIntermediaEnabled()
                || Search.getConfig().isLuceneEnabled()) {

            add(new PermissionFilterComponent(
                    SecurityManager.CMS_PREVIEW_ITEM));

            add(new SimpleCategoryFilterWidget() {

                @Override
                protected Category[] getRoots(PageState state) {
                    Category[] roots;
                    if (CMS.getContext().hasContentSection()) {
                        ContentSection section = CMS.getContext().getContentSection();
                        roots = new Category[]{section.getRootCategory()};
                    } else {
                        ContentSectionCollection sections =
                                ContentSection.getAllSections();
                        List cats = new ArrayList();
                        while (sections.next()) {
                            ContentSection section = sections.getContentSection();
                            cats.add(section.getRootCategory());
                        }
                        roots = (Category[]) cats.toArray(new Category[cats.size()]);
                    }
                    return roots;
                }
            });

            add(new ContentTypeFilterWidget() {

                @Override
                protected ContentSection getContentSection() {
                    if (CMS.getContext().hasContentSection()) {
                        return CMS.getContext().getContentSection();
                    } else {
                        return super.getContentSection();
                    }
                }
            });

            add(new VersionFilterComponent(context));
            add(new ContentSectionFilterComponent());
            add(new DateRangeFilterWidget(new LastModifiedDateFilterType(),
                    LastModifiedDateFilterType.KEY));
            add(new DateRangeFilterWidget(new CreationDateFilterType(),
                    CreationDateFilterType.KEY));
            add(new PartyFilterWidget(new CreationUserFilterType(),
                    CreationUserFilterType.KEY));
            add(new PartyFilterWidget(new LastModifiedUserFilterType(),
                    LastModifiedUserFilterType.KEY));
        }

        Submit submit = new Submit(m_context + "_search",
                ContentSectionPage.globalize("cms.ui.search"));
        add(submit);
    }

    private class LaunchDateFilterWidget extends DateRangeFilterWidget {

        public LaunchDateFilterWidget(FilterType type, String name) {
            super(type, name);

        }

        @Override
        public boolean isVisible(PageState state) {
            return !ContentSection.getConfig().getHideLaunchDate()
                    && super.isVisible(state);
        }
    }
}
