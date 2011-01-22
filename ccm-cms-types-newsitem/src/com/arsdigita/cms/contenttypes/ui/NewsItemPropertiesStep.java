/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.NewsItem;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.contenttypes.util.NewsItemGlobalizationUtil;
import com.arsdigita.dispatcher.DispatcherHelper;

import java.text.DateFormat;

/**
 * Authoring step to edit the simple attributes of the NewsItem content type
 * (and its subclasses). The attributes edited are 'name', 'title', 'lead' 
 * and 'item date'. This authoring step replaces the
 * <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 *
 * @see com.arsdigita.cms.contenttypes.NewsItem
 **/
public class NewsItemPropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    public NewsItemPropertiesStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new NewsItemPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel),
                editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getNewsDomainObjectPropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the NewsItem
     * specified by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the item
     **/
    public static Component getNewsDomainObjectPropertySheet(ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add((String) NewsItemGlobalizationUtil.globalize("cms.contenttypes.ui.title").localize(), NewsItem.TITLE);
        sheet.add((String) NewsItemGlobalizationUtil.globalize("cms.contenttypes.ui.newsitem.name").localize(), NewsItem.NAME);
        sheet.add((String) NewsItemGlobalizationUtil.globalize("cms.contenttypes.ui.newsitem.lead").localize(), NewsItem.LEAD);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(NewsItemGlobalizationUtil.globalize("cms.contenttypes.ui.launch_date"),
                    ContentPage.LAUNCH_DATE,
                    new DomainObjectPropertySheet.AttributeFormatter() {

                        public String format(DomainObject item,
                                String attribute,
                                PageState state) {
                            ContentPage page = (ContentPage) item;
                            if (page.getLaunchDate() != null) {
                                return DateFormat.getDateInstance(DateFormat.LONG, DispatcherHelper.getNegotiatedLocale()).format(page.getLaunchDate());
                            } else {
                                return (String) NewsItemGlobalizationUtil.globalize("cms.ui.unknown").localize();
                            }
                        }
                    });
        }

        // Show news item on homepage?
        if (!NewsItem.getConfig().getHideHomepageField()) {
            sheet.add((String) NewsItemGlobalizationUtil.globalize("cms.contenttypes.ui.newsitem.homepage").localize(),
                    NewsItem.IS_HOMEPAGE,
                    new DomainObjectPropertySheet.AttributeFormatter() {

                        public String format(DomainObject item,
                                String attribute,
                                PageState state) {
                            NewsItem pr = (NewsItem) item;

                            if (pr.isHomepage().booleanValue()) {
                                return (String) NewsItemGlobalizationUtil.globalize("cms.ui.yes").localize();
                            }

                            return (String) NewsItemGlobalizationUtil.globalize("cms.ui.no").localize();
                        }
                    });
        }

        sheet.add((String) NewsItemGlobalizationUtil.globalize("cms.contenttypes.ui.newsitem.news_date").localize(), NewsItem.NEWS_DATE,
                new DomainObjectPropertySheet.AttributeFormatter() {

                    public String format(DomainObject item,
                            String attribute,
                            PageState state) {
                        NewsItem pr = (NewsItem) item;
                        if (pr.getNewsDate() != null) {
                            return DateFormat.getDateInstance(DateFormat.LONG, DispatcherHelper.getNegotiatedLocale()).format(pr.getNewsDate());
                        } else {
                            return (String) NewsItemGlobalizationUtil.globalize("cms.ui.unknown").localize();
                        }
                    }
                });

        return sheet;
    }
}
