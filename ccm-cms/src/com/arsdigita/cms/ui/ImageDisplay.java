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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.Service;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.util.Assert;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;

/**
 * Displays a single ImageAsset, showing its image, width, height, name and mime-type.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ImageDisplay.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ImageDisplay extends SimpleComponent {

    private final ItemSelectionModel m_item;

    /**
     * Construct a new ImageDisplay
     *
     * @param m The {@link ItemSelectionModel} which will supply this component with the
     *          {@link ImageAsset}
     */
    public ImageDisplay(ItemSelectionModel m) {
        super();
        m_item = m;
    }

    /**
     * @return the {@link ItemSelectionModel} which supplies this component with the
     *         {@link ImageAsset}
     */
    public final ItemSelectionModel getImageSelectionModel() {
        return m_item;
    }

    /**
     *
     * @param state
     * @param parent
     */
    @Override
    public void generateXML(PageState state, Element parent) {
        if (isVisible(state)) {

            ImageAsset image = getImageAsset(state);

            if (image == null) {
                return;
            }

            Element element = new Element("cms:imageDisplay", CMS.CMS_XML_NS);

            if (image != null) {
                generateImagePropertiesXML(image, state, element);
            }

            exportAttributes(element);
            parent.addContent(element);
        }
    }

    /**
     * Generates the property xml.
     *
     * @param image
     * @param state
     * @param element
     */
    protected void generateImagePropertiesXML(ImageAsset image,
                                              PageState state,
                                              Element element) {

        element.addAttribute("name_label", (String) GlobalizationUtil.globalize(
                             "cms.contentasset.image.ui.display.name")
                             .localize());
        element.addAttribute("name", image.getName());
        element.addAttribute("src", URL.getDispatcherPath()
                                        + Service.getImageURL(image));

        element.addAttribute("mime_type_label", (String) GlobalizationUtil.globalize(
                             "cms.contentasset.image.ui.display.type")
                             .localize());
        MimeType mimeType = image.getMimeType();
        if (mimeType != null) {
            element.addAttribute("mime_type", mimeType.getLabel());
        }

        element.addAttribute("width_label", (String) GlobalizationUtil.globalize(
                             "cms.contentasset.image.ui.display.width")
                             .localize());
        BigDecimal width = image.getWidth();
        if (width != null) {
            element.addAttribute("width", width.toString());
        } else {
            element.addAttribute("width", (String) GlobalizationUtil.globalize(
                                 "cms.ui.unknown")
                                 .localize());
        }

        element.addAttribute("height_label", (String) GlobalizationUtil.globalize(
                             "cms.contentasset.image.ui.display.height")
                             .localize());
        BigDecimal height = image.getHeight();
        if (height != null) {
            element.addAttribute("height", height.toString());
        } else {
            element.addAttribute("height", (String) GlobalizationUtil.globalize(
                                 "cms.ui.unknown")
                                 .localize());
        }

        element.addAttribute("dimension_label", (String) GlobalizationUtil.globalize(
                             "cms.contentasset.image.ui.display.dimensions")
                             .localize());

    }

    /**
     *
     * @param state
     *
     * @return
     */
    protected ImageAsset getImageAsset(PageState state) {
        ImageAsset image = (ImageAsset) m_item.getSelectedObject(state);
        Assert.exists(image, "Image asset");
        return image;
    }

}
