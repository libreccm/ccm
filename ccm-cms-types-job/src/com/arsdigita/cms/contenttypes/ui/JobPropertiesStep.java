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
import com.arsdigita.cms.contenttypes.Job;
import com.arsdigita.cms.contenttypes.util.JobGlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;

import java.text.DateFormat;

/**
 * Authoring step to edit the simple attributes of the Job content type (and its
 * subclasses). The attributes edited are 'name', 'title', 'event date',
 * 'location', 'lead', 'main contributor', 'event type', 'map link', and
 * 'cost'. This authoring step replaces the
 * <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 **/
public class JobPropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    /**
     * 
     * @param itemModel
     * @param parent 
     */
    public JobPropertiesStep(ItemSelectionModel itemModel,
                             AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new JobPropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, 
            "Edit", 
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getJobPropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the Job specified by
     * the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     **/
    public static Component getJobPropertySheet(ItemSelectionModel
                                                itemModel) {

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add( GlobalizationUtil.globalize("cms.contenttypes.ui.title"),  
                Job.TITLE);
        sheet.add( GlobalizationUtil.globalize("cms.contenttypes.ui.name"),  
                   Job.NAME);

        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new DomainObjectPropertySheet.AttributeFormatter() {
                          public String format(DomainObject item,
                                               String attribute,
                                               PageState state) {
                              ContentPage page = (ContentPage) item;
                              if(page.getLaunchDate() != null) {
                                  return DateFormat.getDateInstance(DateFormat.LONG)
                                      .format(page.getLaunchDate());
                              } else {
                                  return (String)GlobalizationUtil
                                          .globalize("cms.ui.unknown")
                                          .localize();
                              }
                          }
                      });
        }

        // Job content type currently does not use the default 
        // basic descriuption properties (as persisted in cms-pages and by
        // default part of the object list). Would be convenient to move the
        // ct specific overview property to basic description.
        sheet.add( JobGlobalizationUtil
                   .globalize("cms.contenttypes.ui.job.overview"),
                   Job.BODY);

        sheet.add( JobGlobalizationUtil
                   .globalize("cms.contenttypes.ui.job.grade"),  
                   Job.GRADE);
        sheet.add( JobGlobalizationUtil
                   .globalize("cms.contenttypes.ui.job.closing_date"),  
                  Job.CLOSING_DATE,
                  new DomainObjectPropertySheet.AttributeFormatter() {

                      public String format(DomainObject item,
                                           String attribute,
                                           PageState state) {
                          Job job = (Job) item;
                          if(job.getClosingDate() != null) {
                              return DateFormat.getDateInstance(DateFormat.LONG)
                                  .format(job.getClosingDate());
                          } else {
                              return (String)GlobalizationUtil
                                      .globalize("cms.ui.unknown")
                                      .localize();
                          }
                      }
                  });
        sheet.add( JobGlobalizationUtil
                   .globalize("cms.contenttypes.ui.job.salary"),
                   Job.SALARY);
        sheet.add( JobGlobalizationUtil
                   .globalize("cms.contenttypes.ui.job.ref_number"),
                   Job.REF_NUMBER);
        sheet.add( JobGlobalizationUtil
                   .globalize("cms.contenttypes.ui.job.department"),
                   Job.DEPARTMENT);
        sheet.add( JobGlobalizationUtil
                   .globalize("cms.contenttypes.ui.job.job_description"),
                   Job.JOB_DESCRIPTION);
        sheet.add( JobGlobalizationUtil
                   .globalize("cms.contenttypes.ui.job.person_specification"),
                   Job.PERSON_SPECIFICATION);
        sheet.add( JobGlobalizationUtil
                   .globalize("cms.contenttypes.ui.job.contact_details"),
                   Job.CONTACT_DETAILS);

        return sheet;
    }
}
