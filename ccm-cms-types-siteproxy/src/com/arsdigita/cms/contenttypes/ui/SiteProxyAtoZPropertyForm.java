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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SiteProxy;
import com.arsdigita.cms.contenttypes.util.SiteProxyGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 * Form to edit the basic properties of an SiteProxy. This form can be extended
 * to create forms for SiteProxy subclasses.
 */
public class SiteProxyAtoZPropertyForm extends BasicItemForm {

	private TextField m_title_atoz;

	private RadioGroup m_radiogroupUsedInAtoZ;

	private ItemSelectionModel m_selectionModel;

	private SiteProxy siteProxy;

	/**
	 * Creates a new form to edit the SiteProxy object specified by the item
	 * selection model passed in.
	 * 
	 * @param itemModel
	 *            The ItemSelectionModel to use to obtain the SiteProxy to work
	 *            on
	 */
	public SiteProxyAtoZPropertyForm(ItemSelectionModel itemModel) {
		super("siteProxyAtoZEdit", itemModel);
		m_selectionModel = itemModel;
	}

	/**
	 * Adds widgets to the form.
	 */
	protected void addWidgets() {
		add(new Label(SiteProxyGlobalizationUtil
				.globalize(Constants.LABEL_TITLE_ATOZ)));
		m_title_atoz = new TextField(SiteProxy.TITLE_ATOZ);
		m_title_atoz.setSize(50);
		add(m_title_atoz);
		add(new Label(SiteProxyGlobalizationUtil
				.globalize(Constants.LABEL_USED_IN_ATOZ)));
		m_radiogroupUsedInAtoZ = new RadioGroup(SiteProxy.USED_IN_ATOZ);
		m_radiogroupUsedInAtoZ.addOption(new Option(Boolean.TRUE.toString(),
				SiteProxyGlobalizationUtil.globalize(
						Constants.OPTION_USED_IN_ATOZ_YES).localize()
						.toString()));
		m_radiogroupUsedInAtoZ
				.addOption(new Option(Boolean.FALSE.toString(),
						SiteProxyGlobalizationUtil.globalize(
								Constants.OPTION_USED_IN_ATOZ_NO).localize()
								.toString()));
		add(m_radiogroupUsedInAtoZ);
	}

	/** Form initialisation hook. Fills widgets with data. */
	public void init(FormSectionEvent fse) {
		PageState pageState = fse.getPageState();
		siteProxy = (SiteProxy) m_selectionModel.getSelectedObject(pageState);
		if (siteProxy == null)
			return;

		m_title_atoz.setValue(pageState, siteProxy.getAtoZTitle());
		m_radiogroupUsedInAtoZ.setValue(pageState, new Boolean(siteProxy
				.isUsedInAtoZ()).toString());
	}

	/** Form processing hook. Saves SiteProxy object. */
	public void process(FormSectionEvent fse) {
		PageState pageState = fse.getPageState();
		siteProxy = (SiteProxy) m_selectionModel.getSelectedObject(pageState);
		/* proced only when siteProxy is present and save button was pressed */
		if ((siteProxy == null)
				&& !getSaveCancelSection().getSaveButton()
						.isSelected(pageState))
			return;

		siteProxy.setAtoZTitle((String) m_title_atoz.getValue(pageState));
		siteProxy.setUsedInAtoZ(new Boolean((String) m_radiogroupUsedInAtoZ
				.getValue(pageState)).booleanValue());
		siteProxy.save();
	}
}
