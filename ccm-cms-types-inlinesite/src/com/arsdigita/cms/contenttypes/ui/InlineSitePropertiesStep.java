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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.contenttypes.InlineSite;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.InlineSiteGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import java.text.DateFormat;

/**
 * Authoring step to edit the simple attributes of the InlineSite content type (and its subclasses).
 */
public class InlineSitePropertiesStep extends SimpleEditStep {

    /**
     * The name of the editing sheet added to this step
     */
    public static final String EDIT_SHEET_NAME = "edit";

    public InlineSitePropertiesStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent) {
        super(itemModel, parent);

        final BasicPageForm editSheet = new InlineSitePropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit",
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());
        setDefaultEditKey(EDIT_SHEET_NAME);

        setDisplayComponent(getInlineSitePropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the InlineSite specified by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties of the release
     */
    public static Component getInlineSitePropertySheet(final ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"),
                  InlineSite.TITLE);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"),
                  InlineSite.NAME);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                    .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new DomainObjectPropertySheet.AttributeFormatter() {
                @Override
                public String format(final DomainObject item,
                                     final String attribute,
                                     final PageState state) {
                    ContentPage page = (ContentPage) item;
                    if (page.getLaunchDate() != null) {
                        return DateFormat
                                .getDateInstance(DateFormat.LONG)
                                .format(page.getLaunchDate());
                    } else {
                        return (String) GlobalizationUtil
                                .globalize("cms.ui.unknown")
                                .localize();
                    }
                }
            });
        }

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.summary"),
                  InlineSite.DESCRIPTION);
        sheet.add(InlineSiteGlobalizationUtil
                .globalize("cms.contenttypes.ui.inlinesite.url"),
                  InlineSite.URL);

        return sheet;
    }
}
