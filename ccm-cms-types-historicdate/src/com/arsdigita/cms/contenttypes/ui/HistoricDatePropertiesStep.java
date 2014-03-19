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
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.HistoricDate;
import com.arsdigita.cms.contenttypes.HistoricDateGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class HistoricDatePropertiesStep extends SimpleEditStep {
    
    public static String EDIT_SHEET_NAME = "edit";
    
    public HistoricDatePropertiesStep(final ItemSelectionModel itemModel, 
                                      AuthoringKitWizard parent) {
        super(itemModel, parent);
        
        setDefaultEditKey(EDIT_SHEET_NAME);
        final BasicPageForm editSheet = new HistoricDatePropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, 
            GlobalizationUtil.globalize("cms.ui.edit"),
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());
        
        setDisplayComponent(getHistoricDatePropertySheet(itemModel));
    }
    
    public static Component getHistoricDatePropertySheet(final ItemSelectionModel itemModel) {
        
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);
        
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"), HistoricDate.TITLE);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"), HistoricDate.NAME);
        
        sheet.add(HistoricDateGlobalizationUtil.globalize("historicdate.ui.year"), 
                  HistoricDate.YEAR);
        sheet.add(HistoricDateGlobalizationUtil.globalize("historicdate.ui.month"), 
                  HistoricDate.MONTH);
        sheet.add(HistoricDateGlobalizationUtil.globalize("historicdate.ui.day_of_month"), 
                  HistoricDate.DAY_OF_MONTH);
        
        sheet.add(HistoricDateGlobalizationUtil.globalize("historicdate.ui.date_is_approx"), 
                  HistoricDate.DATE_IS_APPROX);
        
        sheet.add(HistoricDateGlobalizationUtil.globalize("historicdate.ui.lead"), 
                  HistoricDate.LEAD);
        
        return sheet;
        
    }
}
