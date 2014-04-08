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
 */
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.Service;
import com.arsdigita.cms.contentassets.ItemImageAttachment;
import com.arsdigita.cms.contentassets.util.ImageStepGlobalizationUtil;
import com.arsdigita.cms.ui.ImageDisplay;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;

/**
 * Component displays the currently attached images for an content item. It is part of the entry
 * point image authoring step {
 *
 * @see ImageStep}.
 *
 * It creates a list of images including meta information (name, type, width, etc.), a link to
 * remove from the list for each image and at the bottom a link to add another image.
 *
 * @author unknown
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class ImageStepDisplay extends SimpleContainer {

    private static final Logger S_LOG = Logger.getLogger(ImageStepDisplay.class);
    /**
     * Represents invoking parent component
     */
    private final ImageStep m_imageStep;
    private ImageListModelBuilder m_listModelBuilder;
    /**
     * Name of the delete event
     */
    private final static String DELETE = "deleteAttachment";
    private final static String MOVEUP = "moveAttachmentUp";
    private final static String MOVEDOWN = "moveAttachmentDown";
    private final static String EDIT = "editAttachment";

    /**
     * Constructor.
     *
     * @param step
     */
    public ImageStepDisplay(ImageStep step) {
        super();

        m_imageStep = step;

        /* Message to show in case no image has been attached yet.          */
        Label mainLabel = new Label(ImageStepGlobalizationUtil.globalize(
            "cms.contentassets.ui.image_step.no_image_attached"));
        mainLabel.setFontWeight(Label.ITALIC);

        m_listModelBuilder = new ImageListModelBuilder();
        List imageList = new List(m_listModelBuilder) {
            @Override
            public void respond(PageState ps) throws ServletException {
                if (DELETE.equals(ps.getControlEventName())) {
                    DomainObjectFactory.newInstance(OID.valueOf(ps.getControlEventValue())).delete();
                    // Regenerate sortkeys
                    m_listModelBuilder.getModel().regenSortKeys(ps);
                } else if (EDIT.equals(ps.getControlEventName())) {
                    m_imageStep.setAttachment(
                        ps, ItemImageAttachment.retrieve(OID.valueOf(ps.
                                getControlEventValue())));

                    m_imageStep.showComponent(ps, "edit");
                } else if (MOVEUP.equals(ps.getControlEventName())) {
                    m_listModelBuilder.getModel().move(OID.valueOf(ps.getControlEventValue()), -1,
                                                       ps);
                } else if (MOVEDOWN.equals(ps.getControlEventName())) {
                    m_listModelBuilder.getModel().
                        move(OID.valueOf(ps.getControlEventValue()), 1, ps);
                } else {
                    super.respond(ps);
                }
            }

        };

        imageList.setCellRenderer(new ImageListCellRenderer());
        imageList.setEmptyView(mainLabel);

        add(imageList);  // finally add the component
    }

    /**
     * Inner class
     */
    private class ImageListModelBuilder extends LockableImpl
        implements ListModelBuilder {

        private ImageListModel m_listModel;

        /**
         *
         * @param list
         * @param ps
         *
         * @return
         */
        @Override
        public ListModel makeModel(List list, PageState ps) {
            ContentItem item = m_imageStep.getItem(ps);

            DataCollection attachments = ItemImageAttachment.getImageAttachments(item);
            attachments.addPath(ItemImageAttachment.IMAGE + "."
                                + ReusableImageAsset.ID);
            attachments.addPath(ItemImageAttachment.IMAGE + "."
                                + ReusableImageAsset.OBJECT_TYPE);
            attachments.addPath(ItemImageAttachment.IMAGE + "."
                                + ReusableImageAsset.HEIGHT);
            attachments.addPath(ItemImageAttachment.IMAGE + "."
                                + ReusableImageAsset.WIDTH);

            m_listModel = new ImageListModel(attachments);
            return m_listModel;
        }

        protected ImageListModel getModel() {
            return m_listModel;
        }

    }

    /**
     *
     */
    private class ImageListModel implements ListModel {

        private final DataCollection m_attachments;

        ImageListModel(DataCollection attachments) {
            m_attachments = attachments;
        }

        @Override
        public Object getElement() {
            return DomainObjectFactory.newInstance(m_attachments.getDataObject());
        }

        @Override
        public String getKey() {
            return m_attachments.getDataObject().getOID().toString();
        }

        @Override
        public boolean next() {
            return m_attachments.next();
        }

        public boolean isFirst() {
            return m_attachments.isFirst();
        }

        public boolean isLast() {
            return m_attachments.isLast();
        }

        /**
         * Move an image's position inside the list.
         *
         * @param oid  {@link OID} of the image to move
         * @param move position steps (positive or negative) to move
         * @param ps   Current {@link PageState}
         */
        protected void move(OID oid, int move, PageState ps) {
            // Get the current ContentItem
            ContentItem item = m_imageStep.getItem(ps);
            // Get the collection of attached images
            DataCollection attachments = ItemImageAttachment.getImageAttachments(item);

            // Always need an oid of the image to move
            if (oid == null) {
                throw new IllegalArgumentException("OID must not be null");
            }

            // No move, nothing to do
            if (move == 0) {
                return;
            }

            // Find the image in the collection
            while (attachments.next()) {
                if (attachments.getDataObject().getOID().equals(oid)) {
                    break;
                }
            }

            // Throw an {@link IllegalArgumentxception} if the oid was not found
            if (!attachments.getDataObject().getOID().equals(oid)) {
                throw new IllegalArgumentException("OID " + oid + " is not in collection");
            }

            // Get the image to move and test if it is really an ItemImageAttachment
            DomainObject sortDomainObject = DomainObjectFactory.newInstance(attachments.
                getDataObject());
            if (sortDomainObject instanceof ItemImageAttachment) {

                // Change the sortKey of the ItemImageAttachment to the desired
                // value but respect bounds of the current list
                int newSortKey = Math.max(1,
                                          Math.min((int) attachments.size(),
                                                   ((ItemImageAttachment) sortDomainObject).
                    getSortKey() + move));
                ((ItemImageAttachment) sortDomainObject).setSortKey(newSortKey);
                ((ItemImageAttachment) sortDomainObject).save();

                // Now, move all the object between the original position and the 
                // new postition one step in the nessecary direction
                if (move < 0) {
                    while (attachments.previous() && move < 0) {
                        DomainObject domainObject = DomainObjectFactory.newInstance(attachments.
                            getDataObject());
                        if (domainObject instanceof ItemImageAttachment) {
                            ((ItemImageAttachment) domainObject).setSortKey(
                                ((ItemImageAttachment) domainObject).getSortKey() + 1);
                            ((ItemImageAttachment) domainObject).save();
                            move++;
                        }
                    }
                }
                if (move > 0) {
                    while (attachments.next() && move > 0) {
                        DomainObject domainObject = DomainObjectFactory.newInstance(attachments.
                            getDataObject());
                        if (domainObject instanceof ItemImageAttachment) {
                            ((ItemImageAttachment) domainObject).setSortKey(
                                ((ItemImageAttachment) domainObject).getSortKey() - 1);
                            ((ItemImageAttachment) domainObject).save();
                            move--;
                        }
                    }
                }
            }

            // close the collection manually to avoid warnings because the list
            // will not be closed automatically
            attachments.close();
        }

        /**
         * Reorganize the sortKeys after removing an item.
         *
         * @param ps The current {@link PageState}
         */
        protected void regenSortKeys(PageState ps) {
            // Get the current ContentItem
            ContentItem item = m_imageStep.getItem(ps);
            // Get the collection of attached images
            DataCollection attachments = ItemImageAttachment.getImageAttachments(item);

            // Current Position
            int pos = 0;
            // Iterate through all items and set item sortKey to pos
            while (attachments.next()) {
                pos++;
                DomainObject domainObject = DomainObjectFactory.newInstance(attachments.
                    getDataObject());
                if (domainObject instanceof ItemImageAttachment) {
                    int sortKey = ((ItemImageAttachment) domainObject).getSortKey();
                    if (sortKey != pos) {
                        ((ItemImageAttachment) domainObject).setSortKey(pos);
                        domainObject.save();
                    }
                }
            }
        }

    }

    /**
     *
     */
    private class ImageListCellRenderer implements ListCellRenderer {

        /**
         *
         * @param list
         * @param state
         * @param value
         * @param key
         * @param index
         * @param isSelected
         *
         * @return
         */
        @Override
        public Component getComponent(final List list, PageState state,
                                      Object value, String key,
                                      int index, boolean isSelected) {
            final ItemImageAttachment attachment = (ItemImageAttachment) value;

            BoxPanel container = new BoxPanel(BoxPanel.VERTICAL);
            container.setBorder(1);

            // Add CMS ImageDisplay element to BoxPanel container an overwrite
            // generateImagePropertiesXM to add attachment's meta data.
            container.add(new ImageDisplay(null) {
                @Override
                protected void generateImagePropertiesXML(ImageAsset image,
                                                          PageState state,
                                                          Element element) {
                    /* Use CMS ImageDisplay to display the image including    *
                     * metadata as name, type, widht, height etc.             */
                    super.generateImagePropertiesXML(image, state, element);

                    // We check config here to see whether additional meta data
                    // as title and description are configured to be displayed.
                    // If it is, we display the description and title options
                    // TODO: Currently without Label, labels for each attribut
                    // are provided by the theme. Has to be refactored to
                    // provide labels in Java (including localization).
                    // Title and description - if displayed - have to be
                    // positioned above the image and its metadata.
                    if (ItemImageAttachment.getConfig()
                        .getIsImageStepDescriptionAndTitleShown()) {
                        String description = attachment.getDescription();
                        if (description != null) {
                            element.addAttribute("description", description);
                        }

                        String title = attachment.getTitle();
                        if (title != null) {
                            element.addAttribute("title", title);
                        }
                    }

                    element.addAttribute("caption_label", (String) GlobalizationUtil.globalize(
                        "cms.contentasset.image.ui.caption")
                        .localize());
                    element.addAttribute("caption", attachment.getCaption());

                    element.addAttribute("context_label", (String) GlobalizationUtil.globalize(
                        "cms.contentasset.image.ui.use_context")
                        .localize());
                    String useContext = attachment.getUseContext();
                    if (null == useContext) {
                        element.addAttribute("context", (String) GlobalizationUtil.globalize(
                            "cms.ui.unknown")
                            .localize());
                    } else {
                        element.addAttribute("context", useContext);
                    }

                }

                @Override
                protected ImageAsset getImageAsset(PageState ps) {
                    return attachment.getImage();
                }

            });

            /* Adds links to move and remove the image in a separate container element */
            if (!((ImageListModel) list.getModel(state)).isFirst()) {
                ControlLink moveUpLink = new ControlLink(new Label(
                    ImageStepGlobalizationUtil.globalize(
                        "cms.contentassets.ui.image_step.move_attached_image_up"))) {
                            @Override
                            public void setControlEvent(PageState ps) {
                                String oid = ps.getControlEventValue();
                                ps.setControlEvent(list, MOVEUP, oid);
                            }

                            // Override generateURL to prevent deleting of the page state
                            @Override
                            protected void generateURL(PageState state, Element parent) {
                                setControlEvent(state);
                                try {
                                    parent.addAttribute("href", state.stateAsURL());
                                } catch (IOException e) {
                                    parent.addAttribute("href", "");
                                }
                                exportAttributes(parent);
                            }

                        };
                    container.add(moveUpLink);
            }

            if (!((ImageListModel) list.getModel(state)).isLast()) {
                ControlLink moveDownLink = new ControlLink(new Label(
                    ImageStepGlobalizationUtil.globalize(
                        "cms.contentassets.ui.image_step.move_attached_image_down"))) {
                            @Override
                            public void setControlEvent(PageState ps) {
                                String oid = ps.getControlEventValue();
                                ps.setControlEvent(list, MOVEDOWN, oid);
                            }

                            // Override generateURL to prevent deleting of the page state
                            @Override
                            protected void generateURL(PageState state, Element parent) {
                                setControlEvent(state);
                                try {
                                    parent.addAttribute("href", state.stateAsURL());
                                } catch (IOException e) {
                                    parent.addAttribute("href", "");
                                }
                                exportAttributes(parent);
                            }

                        };
                    container.add(moveDownLink);
            }

            ControlLink editLink = new ControlLink(new Label(
                ImageStepGlobalizationUtil.globalize(
                    "cms.contentassets.ui.image_step.edit_attached_image"))) {

                        @Override
                        public void setControlEvent(final PageState state) {
                            final String oid = state.getControlEventValue();
                            state.setControlEvent(list, EDIT, oid);
                        }

                        @Override
                        public void generateURL(final PageState state, Element parent) {
                            setControlEvent(state);
                            try {
                                parent.addAttribute("href", state.stateAsURL());
                            } catch (IOException ex) {
                                parent.addAttribute("href", "");
                            }
                        }

                    };

                container.add(editLink);

                ControlLink deleteLink = new ControlLink(new Label(
                    ImageStepGlobalizationUtil.globalize(
                        "cms.contentassets.ui.image_step.remove_attached_image"))) {

                            @Override
                            public void setControlEvent(PageState ps) {
                                String oid = ps.getControlEventValue();
                                ps.setControlEvent(list, DELETE, oid);
                            }

                            // Override generateURL to prevent deleting of the page state
                            @Override
                            protected void generateURL(PageState state, Element parent) {
                                setControlEvent(state);
                                try {
                                    parent.addAttribute("href", state.stateAsURL());
                                } catch (IOException e) {
                                    parent.addAttribute("href", "");
                                }
                                exportAttributes(parent);
                            }

                        };

                    container.add(deleteLink);

                    return container;
        }

    }
}
