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
import com.arsdigita.cms.util.GlobalizationUtil;

import com.arsdigita.globalization.GlobalizationHelper;

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
        add(EDIT_SHEET_NAME, 
            "Edit", 
            new WorkflowLockedComponentAccess(editSheet,itemModel),
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

        sheet.add(NewsItemGlobalizationUtil
                  .globalize("cms.contenttypes.ui.title"), NewsItem.TITLE);
        sheet.add(NewsItemGlobalizationUtil
                  .globalize("cms.contenttypes.ui.name"), NewsItem.NAME);
        sheet.add(NewsItemGlobalizationUtil
                  .globalize("cms.contenttypes.ui.newsitem.lead"), NewsItem.LEAD);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }

        // Show news item on homepage?
        if (!NewsItem.getConfig().getHideHomepageField()) {
            sheet.add(NewsItemGlobalizationUtil
                      .globalize("cms.contenttypes.ui.newsitem.homepage"),
                      NewsItem.IS_HOMEPAGE,
                      new DomainObjectPropertySheet.AttributeFormatter() {

                public String format(DomainObject item,
                                     String attribute,
                                     PageState state) {
                    NewsItem pr = (NewsItem) item;

                    if (pr.isHomepage().booleanValue()) {
                        return (String) NewsItemGlobalizationUtil
                                .globalize("cms.contenttypes.ui.newsitem.yes")
                                .localize();
                    }

                    return (String) NewsItemGlobalizationUtil
                            .globalize("cms.contenttypes.ui.newsitem.no")
                            .localize();
                }
            });
        }

        sheet.add(NewsItemGlobalizationUtil.globalize(
                                           "cms.contenttypes.ui.newsitem.news_date"), 
                  NewsItem.NEWS_DATE,
                  new NewsItemDateAttributeFormatter() );

        return sheet;
    }

	/**
     * Private class which implements an AttributeFormatter interface for 
     * NewsItem's date values.
     * Its format(...) class returns a string representation for either a
     * false or a true value.
     */
    private static class NewsItemDateAttributeFormatter 
                         implements DomainObjectPropertySheet.AttributeFormatter {

        /**
         * Constructor, does nothing.
         */
        public NewsItemDateAttributeFormatter() {
        }

        /**
         * Formatter for the value of an attribute.
         * 
         * It currently relays on the prerequisite that the passed in property
         * attribute is in fact a date property. No type checking yet!
         * 
         * Note: the format method has to be executed at each page request. Take
         * care to properly adjust globalization and localization here!
         * 
         * @param obj        Object containing the attribute to format.
         * @param attribute  Name of the attribute to retrieve and format
         * @param state      PageState of the request
         * @return           A String representation of the retrieved boolean
         *                   attribute of the domain object.
         */
        public String format(DomainObject obj, String attribute, PageState state) {
 
            if ( obj != null && obj instanceof NewsItem) {
                
                NewsItem newsItem = (NewsItem) obj;
                Object field = newsItem.get(attribute);

                if( field != null ) {
                    // Note: No type safety here! We relay that it is
                    // attached to a date property!
                    return DateFormat.getDateInstance(
                                         DateFormat.LONG, 
                                         GlobalizationHelper.getNegotiatedLocale()
                                          )
                                     .format(field);
                } else {
                    return (String)GlobalizationUtil
                                   .globalize("cms.ui.unknown")
                                   .localize();
                }
            }

            return (String) GlobalizationUtil
                            .globalize("cms.ui.unknown")
                            .localize();
        }
    }
}
