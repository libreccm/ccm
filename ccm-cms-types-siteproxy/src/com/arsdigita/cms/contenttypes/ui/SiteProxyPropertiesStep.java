/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.cms.contenttypes.SiteProxy;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.SiteProxyGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;


/**
 * Authoring step to edit the simple attributes of the SiteProxy content 
 * type (and its subclasses). 
 */
public class SiteProxyPropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static final String EDIT_SHEET_NAME = "edit";

    /**
     * Constructor.
     * 
     * @param itemModel
     * @param parent 
     */
    public SiteProxyPropertiesStep( ItemSelectionModel itemModel,
                                    AuthoringKitWizard parent ) {
        super( itemModel, parent );

        BasicPageForm editSheet;

        editSheet = new SiteProxyPropertyForm( itemModel );
        add( EDIT_SHEET_NAME, 
            SiteProxyGlobalizationUtil.globalize(
                     "cms.contenttypes.ui.siteproxy.edit_form_link"),
             new WorkflowLockedComponentAccess(editSheet, itemModel),
             editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent( getSiteProxyPropertySheet( itemModel ) );
    }

    /**
     * Returns a component that displays the properties of the SiteProxy
     * specified by the ItemSelectionModel passed in.
     * 
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public static Component getSiteProxyPropertySheet( ItemSelectionModel
                                                          itemModel ) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet( itemModel );

        sheet.add( GlobalizationUtil
                   .globalize("cms.contenttypes.ui.title"), 
                   SiteProxy.TITLE );
        sheet.add( GlobalizationUtil
                   .globalize("cms.contenttypes.ui.name"),
                   SiteProxy.NAME );
        sheet.add( SiteProxyGlobalizationUtil
                   .globalize("cms.contenttypes.ui.siteproxy.url"),  
                   SiteProxy.URL );

        return sheet;
    }
}

