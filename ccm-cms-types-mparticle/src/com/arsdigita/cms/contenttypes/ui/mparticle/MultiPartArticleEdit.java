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
package com.arsdigita.cms.contenttypes.ui.mparticle;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.MultiPartArticle;
import com.arsdigita.cms.contenttypes.util.MPArticleGlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;

import java.text.DateFormat;


/**
 * A MultiPartArticle editing component.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @version $Id: MultiPartArticleEdit.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class MultiPartArticleEdit extends SimpleEditStep {

    /**
     * Constructor.
     *
     * @param itemModel the ItemSelectionModel which holds the current
     *   MutliPartArticle
     * @param parent the parent wizard which contains the form
     */
    public MultiPartArticleEdit(ItemSelectionModel itemModel,
                                AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey("edit");
        MultiPartArticleForm form = getForm(itemModel);
        add("edit",
            GlobalizationUtil.globalize("cms.ui.edit"), 
            new WorkflowLockedComponentAccess(form, itemModel),
            form.getSaveCancelSection().getCancelButton()
           );
        
        setDisplayComponent(getMultiPartArticlePropertiesSheet(itemModel));
    }

    protected MultiPartArticleForm getForm(ItemSelectionModel model) {
        return new MultiPartArticleEditForm(model, this);
    }


    public Component getMultiPartArticlePropertiesSheet(
        ItemSelectionModel itemModel
    ) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add( GlobalizationUtil
                   .globalize("cms.contenttypes.ui.title"),
                   MultiPartArticle.TITLE   );
        sheet.add( GlobalizationUtil
                   .globalize("cms.contenttypes.ui.name"), 
                   MultiPartArticle.NAME    );
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil
                      .globalize("cms.contenttypes.ui.launch_date"),
                      ContentPage.LAUNCH_DATE,
                      new LaunchDateAttributeFormatter() );
        }
        sheet.add( GlobalizationUtil.globalize("cms.contenttypes.ui.summary"),  
                   MultiPartArticle.SUMMARY );

        return sheet;
    }
}
