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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Image;
import com.arsdigita.cms.ui.ImageDisplay;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import java.text.DateFormat;

/**
 * Authoring step to edit the simple attributes of the Image content
 * type (and its subclasses). The attributes edited are 'name', 'title',
 * 'article date', 'location', 'lead', and 'article type'.
 * This authoring step replaces
 * the <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 */
public class ImagePropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";

    public ImagePropertiesStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);

        BasicPageForm editSheet;

        editSheet = new ImagePropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel),
                editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getImagePropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the
     * Image specified by the ItemSelectionModel passed in.
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public static Component getImagePropertySheet(final ItemSelectionModel itemModel) {
        SimpleContainer container = new SimpleContainer();

        container.add(new ImageDisplay(null) {

            @Override
            protected ImageAsset getImageAsset(PageState state) {
                ImageAsset image = ((Image) itemModel.getSelectedItem(state)).getImage();
                return image;
            }
        });

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.name"), Image.NAME);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.title"), Image.TITLE);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.launch_date"),
                    ContentPage.LAUNCH_DATE,
                    new DomainObjectPropertySheet.AttributeFormatter() {

                        public String format(DomainObject item,
                                String attribute,
                                PageState state) {
                            ContentPage page = (ContentPage) item;
                            if (page.getLaunchDate() != null) {
                                return DateFormat.getDateInstance(DateFormat.LONG).format(page.getLaunchDate());
                            } else {
                                return (String) GlobalizationUtil.globalize("cms.ui.unknown").localize();
                            }
                        }
                    });
        }


//        sheet.add( GlobalizationUtil.globalize("cms.contenttypes.ui.image"), new ImageDisplay(null));
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.caption"), Image.CAPTION);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.description"), Image.DESCRIPTION);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.artist"), Image.ARTIST);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.publishDate"), Image.PUBLISHDATE);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.source"), Image.SOURCE);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.media"), Image.MEDIA);
        sheet.add(GlobalizationUtil.globalize("cms.contenttypes.ui.copyright"), Image.COPYRIGHT);
//        sheet.add( GlobalizationUtil.globalize("cms.contenttypes.ui.lead"), Image.LEAD );

        container.add(sheet);

        return container;
    }

    public ContentItem getItem( PageState ps ) {
        return getItemSelectionModel().getSelectedItem( ps );
    }
}
