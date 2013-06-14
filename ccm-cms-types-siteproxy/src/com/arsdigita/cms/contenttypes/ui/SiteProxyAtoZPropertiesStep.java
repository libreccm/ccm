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
 *
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SiteProxy;
import com.arsdigita.cms.contenttypes.util.SiteProxyGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Authoring step to edit the simple attributes for the SiteProxy content
 * connection to new AtoZ.
 */
public class SiteProxyAtoZPropertiesStep extends SimpleEditStep {

	/** The name of the editing sheet added to this step */
	public static final String EDIT_ATOZ_SHEET_NAME = "editAtoZ";

	/**
     * Constructor. 
     * 
     * @param itemModel
     * @param parent
     */
    public SiteProxyAtoZPropertiesStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent) {

        super(itemModel, parent);

        BasicItemForm form;

        form = new SiteProxyAtoZPropertyForm(itemModel);
        add(EDIT_ATOZ_SHEET_NAME, 
            SiteProxyGlobalizationUtil.globalize(
                     "cms.contenttypes.ui.siteproxy.link.editatoz"),
			new WorkflowLockedComponentAccess(form, itemModel), form
						.getSaveCancelSection().getCancelButton());

		setDisplayComponent(getSiteProxyAtoZPropertySheet(itemModel));
	}

	/**
	 * Returns a component that displays AtoZ integration properties of the
	 * SiteProxy specified by the ItemSelectionModel passed in.
	 * 
	 * @param itemModel
	 *            The ItemSelectionModel to use
	 * @pre itemModel != null
	 * @return A component to display the state of the AtoZ integration
	 *         properties of the release
	 */
	public static Component getSiteProxyAtoZPropertySheet(
			                                     ItemSelectionModel itemModel) {

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
				itemModel);

		sheet.add(SiteProxyGlobalizationUtil
				  .globalize("cms.contenttypes.ui.siteproxy.label.atoztitle"), 
                  SiteProxy.TITLE_ATOZ);

		sheet.add(SiteProxyGlobalizationUtil
				  .globalize("cms.contenttypes.ui.siteproxy.label.usedinatoz"),
				  SiteProxy.USED_IN_ATOZ, 
                  new BooleanAttributeFormater());

		return sheet;
	}

	/**
     * Private class which implements an AttributeFormatter interface for 
     * boolean values.
     * Its format(...) class returns a string representation for either a
     * false or a true value.
     */
    private static class BooleanAttributeFormater 
                         implements DomainObjectPropertySheet.AttributeFormatter {

        /** Default value just in case there is no value at all.             */
        private static final String DEFAULT = "-";

        /**
         * Constructor, does nothing.
         */
        public BooleanAttributeFormater() {
        }

        /**
         * Formatter for the value of an attribute.
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
            if (obj == null)
				return BooleanAttributeFormater.DEFAULT;

			if ((obj instanceof ContentItem)) {
				ContentItem contentItem = (ContentItem) obj;
				Object field = contentItem.get(attribute);
				if (field == null)
					return BooleanAttributeFormater.DEFAULT;

				if (field instanceof Boolean) {
					Boolean value = (Boolean) contentItem.get(attribute);
					if (value.booleanValue()) {
                        
                        return (String) SiteProxyGlobalizationUtil.globalize(
                            "cms.contenttypes.ui.siteproxy.option.usedinatoz.yes")
                            .localize();

                    } else {

                        return (String) SiteProxyGlobalizationUtil.globalize(
                            "cms.contenttypes.ui.siteproxy.option.usedinatoz.no")
                            .localize();

                    }
				}
			}
			return BooleanAttributeFormater.DEFAULT;
		}

	}

}
