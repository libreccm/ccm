/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;


import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.Article;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * A form which edits the width/height/caption of the image.
 * Displays appropriate textboxes.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #8 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class ImagePropertiesForm extends BasicImageForm {

    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String CAPTION = "caption";

    public ImagePropertiesForm(ItemSelectionModel itemModel,
                               ItemSelectionModel assetModel) {
        super("ImagePropertiesForm", itemModel, assetModel);
    }

    // Add the basic widgeys
    public void addWidgets() {
        super.addWidgets();
    }

    // Init: load the asset and preset fields
    public void init(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        PageState state = e.getPageState();
        Article item = this.getArticle(state);
        ImageAsset asset = this.getImageAsset(state);

        if ( asset == null ) {
            throw new FormProcessException( (String) GlobalizationUtil.globalize("cms.ui.authoring.no_asset").localize());
        }

        initCaption(e);
    }

    // Process: save the data
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        PageState state = e.getPageState();
        Article item = this.getArticle(state);
        ImageAsset asset = this.getImageAsset(state);

        if ( asset == null ) {
            throw new FormProcessException( (String) GlobalizationUtil.globalize("cms.ui.authoring.no_asset").localize());
        }

        processCaption(e);
        item.save();
    }

}
