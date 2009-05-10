/*
 * Copyright (C) 2009 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganization;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;

import java.text.DateFormat;

public class GenericOrganizationPropertiesStep extends SimpleEditStep {
    
    public static final String EDIT_SHEET_NAME = "edit";

    public GenericOrganizationPropertiesStep(ItemSelectionModel itemModel,
					     AuthoringKitWizard parent) {
	super(itemModel, parent);

	setDefaultEditKey(EDIT_SHEET_NAME);
	BasicPageForm editSheet;

	editSheet = new GenericOrganizationPropertyForm(itemModel, this);
	add(EDIT_SHEET_NAME, "Edit",
	    new WorkflowLockedComponentAccess(editSheet, itemModel),
	    editSheet.getSaveCancelSection().getCancelButton());

	setDisplayComponent(getGenericOrganizationPropertySheet(itemModel));	
    }

    public static Component getGenericOrganizationPropertySheet(ItemSelectionModel itemModel) {
	DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

	sheet.add((String)GlobalizationUtil.globalize("cms.contenttypes.ui.organizationname").localize(), GenericOrganization.ORGANIZATIONNAME);

	if(!ContentSection.getConfig().getHideLaunchDate()) {
	    sheet.add((String)GlobalizationUtil.globalize("cms.ui.authoring.page_launch_date").localize(),
		      ContentPage.LAUNCH_DATE,
		      new DomainObjectPropertySheet.AttributeFormatter() {
			  public String format(DomainObject item,
					       String attribute,
					       PageState state) {
			      ContentPage page = (ContentPage)item;
			      if (page.getLaunchDate() != null) {
				  return DateFormat.getDateInstance(DateFormat.LONG).format(page.getLaunchDate());
			      }
			      else {
				  return (String)GlobalizationUtil.globalize("cms.ui.unknown").localize();
			      }
			  }
		      });
		      }

	return sheet;
    }
}
