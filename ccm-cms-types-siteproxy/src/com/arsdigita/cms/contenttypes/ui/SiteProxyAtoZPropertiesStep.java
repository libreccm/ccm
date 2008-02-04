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
 * connection to new AtoZ
 */
public class SiteProxyAtoZPropertiesStep extends SimpleEditStep {

	/** The name of the editing sheet added to this step */
	public static final String EDIT_ATOZ_SHEET_NAME = "editAtoZ";

	public static final String LINK_EDIT = "cms.contenttypes.ui.siteproxy.link.editatoz";

	/**
	 * @param itemModel
	 * @param parent
	 */
	public SiteProxyAtoZPropertiesStep(ItemSelectionModel itemModel,
			AuthoringKitWizard parent) {
		super(itemModel, parent);

		BasicItemForm form;

		form = new SiteProxyAtoZPropertyForm(itemModel);
		add(EDIT_ATOZ_SHEET_NAME, SiteProxyGlobalizationUtil.globalize(
				LINK_EDIT).localize().toString(),
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
				.globalize(Constants.LABEL_TITLE_ATOZ), SiteProxy.TITLE_ATOZ);

		sheet.add(SiteProxyGlobalizationUtil
				.globalize(Constants.LABEL_USED_IN_ATOZ),
				SiteProxy.USED_IN_ATOZ, new BooleanAttributeFormater(
						(String) SiteProxyGlobalizationUtil.globalize(
								Constants.OPTION_USED_IN_ATOZ_YES).localize(),
						(String) SiteProxyGlobalizationUtil.globalize(
								Constants.OPTION_USED_IN_ATOZ_NO).localize()));

		return sheet;
	}

	private static class BooleanAttributeFormater implements
			DomainObjectPropertySheet.AttributeFormatter {

		private static final String DEFAULT = "-";

		private String trueValue;

		private String falseValue;

		public BooleanAttributeFormater(String trueValue, String falseValue) {
			this.trueValue = trueValue;
			this.falseValue = falseValue;
		}

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
					if (value.booleanValue())
						return trueValue;
					else
						return falseValue;
				}
			}
			return BooleanAttributeFormater.DEFAULT;
		}

	}

}
