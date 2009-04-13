package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Person;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.contenttypes.util.PersonGlobalizationUtil;

import java.text.DateFormat;

public class PersonPropertiesStep extends SimpleEditStep {
    public static final String EDIT_SHEET_NAME = "edit";

    public PersonPropertiesStep(ItemSelectionModel itemModel,
				AuthoringKitWizard parent) {
	super(itemModel, parent);
	
	setDefaultEditKey(EDIT_SHEET_NAME);
	BasicPageForm editSheet;

	editSheet = new PersonPropertyForm(itemModel, this);
	add(EDIT_SHEET_NAME, "Edit",
	    new WorkflowLockedComponentAccess(editSheet, itemModel),
	    editSheet.getSaveCancelSection().getCancelButton());
	
	setDisplayComponent(getPersonPropertySheet(itemModel));
    }

    public static Component getPersonPropertySheet(ItemSelectionModel itemModel) {
	DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

	sheet.add((String)PersonGlobalizationUtil.globalize("cms.contenttypes.ui.name").localize(), Person.SURNAME);
	sheet.add((String)PersonGlobalizationUtil.globalize("cms.contenttypes.ui.name").localize(), Person.GIVENNAME);
	sheet.add((String)PersonGlobalizationUtil.globalize("cms.contenttypes.ui.name").localize(), Person.TITLEPRE);
	sheet.add((String)PersonGlobalizationUtil.globalize("cms.contenttypes.ui.name").localize(), Person.TITLEPOST);
	
	if(!ContentSection.getConfig().getHideLaunchDate()) {
	    sheet.add((String)PersonGlobalizationUtil.globalize("cms.ui.authoring.page_launch_date").localize(),
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
				  return (String)PersonGlobalizationUtil.globalize("cms.ui.unknown").localize();
			      }
			  }
		      });
		      }

	return sheet;
    }
}