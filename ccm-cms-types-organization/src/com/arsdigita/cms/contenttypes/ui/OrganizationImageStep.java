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

import com.arsdigita.cms.contenttypes.Organization;
import com.arsdigita.cms.contenttypes.ui.OrganizationImageForm;
import com.arsdigita.cms.contenttypes.util.OrganizationGlobalizationUtil;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.WorkflowLockedComponentAccess;
import com.arsdigita.cms.ImageAsset;

import java.math.BigDecimal;


/**
 * Authoring step to assign an image to the Organization content 
 * type (and its subclasses). 
 *
 * @version $Id: OrganizationImageStep.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class OrganizationImageStep
    extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String IMAGE_SHEET_NAME = "image";

    public OrganizationImageStep( ItemSelectionModel itemModel,
				  AuthoringKitWizard parent ) {
	super( itemModel, parent, "_image" );

    setDefaultEditKey(IMAGE_SHEET_NAME);
	add( IMAGE_SHEET_NAME, "Change", new WorkflowLockedComponentAccess(new OrganizationImageForm("OrganizationImageForm", itemModel, this), itemModel));   

	DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel, false);
	sheet.add(OrganizationGlobalizationUtil.globalize
              ("cms.contenttypes.image"), Organization.IMAGE, new ImageFormatter());

	setDisplayComponent( sheet );
    }


    public static class ImageFormatter
        implements DomainObjectPropertySheet.AttributeFormatter {

        private String m_default;

        public ImageFormatter() {
            this("<i>no image</i>");
        }

        public ImageFormatter(String def) {
            m_default = def;
        }

        public String getDefaultString() {
            return m_default;
        }

        public String format (DomainObject obj,
                              String attribute,
                              PageState state) {

            DataObject imageDO = (DataObject) ((ContentItem)obj).get(attribute);
            if (imageDO == null) {
                return getDefaultString();
            } else {
                ImageAsset image = new ImageAsset(imageDO);
                return getHTMLDisplay(image);
            }
        }

        public static String getHTMLDisplay(ImageAsset image) {
            if (image == null)
                return ("<i>no image</i>");

            BigDecimal width = image.getWidth();
            String widthStr = "";
            if ( width != null ) {
                widthStr = " width=\"" + width.toString() + "\" ";
            }
            BigDecimal height = image.getHeight();
            String heightStr = "";
            if ( height != null ) {
                heightStr = " height=\"" + height.toString() + "\" ";
            }

            String labelStr = "<img src=\"" +
                Utilities.getImageURL(image) +
                "\" " + widthStr + heightStr + "/>";
            return labelStr;
        }
    }

}
