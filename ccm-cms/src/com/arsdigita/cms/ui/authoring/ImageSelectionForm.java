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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.basetypes.Article;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ImageDisplay;
import org.apache.log4j.Logger;


/**
 * A form which displays the newly selected image and prompts
 * for image caption.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #9 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: ImageSelectionForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ImageSelectionForm extends BasicImageForm {

    private static Logger s_log =
        Logger.getLogger(ImageSelectionForm.class);

    private ImageDisplay m_display;

    /**
     * Construct a new ImageSelectionForm
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param assetModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current image asset
     */
    public ImageSelectionForm(ItemSelectionModel itemModel,
                              ItemSelectionModel assetModel) {
        super("ImageSelectionForm", itemModel, assetModel);

        m_display = new ImageDisplay(getAssetSelectionModel());
        add(m_display, ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);
    }


    public void init(FormSectionEvent e) throws FormProcessException {
        // Do nothing.
    }

    // process: update image association
    public void process(FormSectionEvent e) throws FormProcessException {
        s_log.debug("Selecting Image");
        FormData data = e.getFormData();
        PageState state = e.getPageState();
        Article item = this.getArticle(state);
        ImageAsset a = this.getImageAsset(state);

        if(a != null) {
            item.clearImages();
            item.addImage(a, (String)data.get(CAPTION));
            item.save();
            this.setImageAsset(state, a);
            s_log.debug("Image Selected");
        }
    }

    /**
     * @return the image display component
     */
    public ImageDisplay getImageDisplay() {
        return m_display;
    }

}
