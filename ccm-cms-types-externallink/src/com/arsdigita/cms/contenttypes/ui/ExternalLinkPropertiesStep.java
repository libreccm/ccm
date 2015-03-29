/*
 * Copyright (C) 2015 University of Bremen. All Rights Reserved.
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
import com.arsdigita.cms.contenttypes.ExternalLink;
import com.arsdigita.cms.contenttypes.util.ExternalLinkGlobalizationUtil;
import com.arsdigita.cms.contenttypes.ui.ExternalLinkPropertyForm;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Authoring step to view/edit the simple attributes of the 
 * ExternalLink content type (and its subclasses).
 * 
 * @author tosmers
 * @version $Revision: #1 $ $Date: 2015/02/22 $
 */
public class ExternalLinkPropertiesStep extends SimpleEditStep {

    /**
     * The name of the editing sheet added to this step
     */
    public static final String EDIT_SHEET_NAME = "edit";

    /**
     * Constructor.
     *
     * @param itemModel
     * @param parent
     */
    public ExternalLinkPropertiesStep(final ItemSelectionModel itemModel, 
                                        final AuthoringKitWizard parent) {
        super(itemModel, parent);

        final BasicPageForm editSheet = new ExternalLinkPropertyForm(itemModel);
        add(EDIT_SHEET_NAME,
            ExternalLinkGlobalizationUtil.globalize("cms.contenttypes.ui.edit"),
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getExternalLinkPropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the ExternalLink 
     * content item specified by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     *
     * @pre itemModel != null
     * @return A component to display the state of the basic properties of the release
     *
     * Method add deprecated, use add(GlobalizedMessage label, String attribute) instead (but
     * probably Camden doesn't use globalized strings).
     */
    public static Component getExternalLinkPropertySheet(final ItemSelectionModel itemModel) {

        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(ExternalLinkGlobalizationUtil.globalize(
                "cms.contenttypes.externallink.name"), ExternalLink.NAME);
        sheet.add(ExternalLinkGlobalizationUtil.globalize(
                "cms.contenttypes.externallink.title"), ExternalLink.TITLE);
        sheet.add(ExternalLinkGlobalizationUtil.globalize(
                "cms.contenttypes.externallink.description"), ExternalLink.DESCRIPTION);
        sheet.add(ExternalLinkGlobalizationUtil.globalize(
                "cms.contenttypes.externallink.location"), ExternalLink.URL);
        sheet.add(ExternalLinkGlobalizationUtil.globalize(
                "cms.contenttypes.externallink.comment"), ExternalLink.COMMENT);
        sheet.add(ExternalLinkGlobalizationUtil.globalize(
                "cms.contenttypes.externallink.show_comment"), ExternalLink.SHOW_COMMENT);
        sheet.add(ExternalLinkGlobalizationUtil.globalize(
                "cms.contenttypes.externallink.target_window"), ExternalLink.TARGET_WINDOW);

        return sheet;
    }
}
