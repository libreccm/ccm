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
import com.arsdigita.cms.contentassets.ItemImageAttachment;
import com.arsdigita.cms.contentassets.util.ImageStepGlobalizationUtil;
import com.arsdigita.cms.ui.ImageDisplay;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;

/**
 * Component displays the currently attached images for an content item. It is
 * part of the entry point image authoring step {@see ImageStep}.
 * 
 * It creates a list of images including meta information (name, type, width,
 * etc.), a link to remove from the list for each image and at the bottom a
 * link to add another image.
 * 
 * @author unknown
 * @author Sören Bernstein (quasimodo) <sbernstein@quasiweb.de>
 */
public class ImageStepDisplay extends SimpleContainer {

    private static final Logger S_LOG = Logger.getLogger(ImageStepDisplay.class);

    /** Represents invoking parent component                                 */
    private final ImageStep m_imageStep;

    /** Name of the delete event                                             */
    private final static String DELETE = "deleteAttachment";

    /**
     * Constructor.
     * 
     * @param step 
     */
    public ImageStepDisplay( ImageStep step ) {
        super();

        m_imageStep = step;

        /* Message to show in case no image has been attached yet.          */
        Label mainLabel = new Label(ImageStepGlobalizationUtil.globalize(
            "cms.contentassets.ui.image_step.no_image_attached"));
        mainLabel.setFontWeight(Label.ITALIC);

        List imageList = new List( new ImageListModelBuilder() ) {
            @Override
            public void respond( PageState ps ) throws ServletException {
                if( DELETE.equals( ps.getControlEventName() ) ) {
                    String attachment = ps.getControlEventValue();

                    OID oid = OID.valueOf( attachment );
                    DomainObjectFactory.newInstance( oid ).delete();
                }

                else {
                    super.respond( ps );
                }
            }
        };
        imageList.setCellRenderer( new ImageListCellRenderer() );
        imageList.setEmptyView( mainLabel );

        add( imageList );  // finally add the component
    }

    /**
     * Inner class 
     */
    private class ImageListModelBuilder extends LockableImpl
                                        implements ListModelBuilder {
        /**
         * 
         * @param list
         * @param ps
         * @return 
         */
        public ListModel makeModel( List list, PageState ps ) {
            ContentItem item = m_imageStep.getItem( ps );

            DataCollection attachments =
                ItemImageAttachment.getImageAttachments( item );
            attachments.addPath( ItemImageAttachment.IMAGE + "." +
                                 ReusableImageAsset.ID );
            attachments.addPath( ItemImageAttachment.IMAGE + "." +
                                 ReusableImageAsset.OBJECT_TYPE );
            attachments.addPath( ItemImageAttachment.IMAGE + "." +
                                 ReusableImageAsset.HEIGHT );
            attachments.addPath( ItemImageAttachment.IMAGE + "." +
                                 ReusableImageAsset.WIDTH );


            return new ImageListModel( attachments );
        }
    }

    /**
     * 
     */
    private class ImageListModel implements ListModel {

        private final DataCollection m_attachments;

        ImageListModel( DataCollection attachments ) {
            m_attachments = attachments;
        }

        public Object getElement() {
            return DomainObjectFactory.newInstance
                ( m_attachments.getDataObject() );
        }

        public String getKey() {
            return m_attachments.getDataObject().getOID().toString();
        }

        public boolean next() {
            return m_attachments.next();
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
         * @return 
         */
        public Component getComponent( final List list, PageState state,
                                       Object value, String key,
                                       int index, boolean isSelected ) {
            final ItemImageAttachment attachment = (ItemImageAttachment) value;

            BoxPanel container = new BoxPanel( BoxPanel.VERTICAL );
            container.setBorder( 1 );

            // Add CMS ImageDisplay element to BoxPanel container an overwrite
            // generateImagePropertiesXM to add attachment's meta data.
            container.add( new ImageDisplay(null) {
                @Override
                protected void generateImagePropertiesXML( ImageAsset image,
                                                           PageState state,
                                                           Element element ) {
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
                    if(ItemImageAttachment.getConfig()
                                          .getIsImageStepDescriptionAndTitleShown()) {
						String description = attachment.getDescription();
						if (description != null) {
							element.addAttribute("description", description);
						}

						String title = attachment.getTitle();
						if (title!= null) {
							element.addAttribute("title", title);
						}
					}


                }

                @Override
                protected ImageAsset getImageAsset( PageState ps ) {
                    return attachment.getImage();
                }
            } );

            /* Create a box panel beloy the image to display the caption     */
            BoxPanel captionPanel = new BoxPanel( BoxPanel.HORIZONTAL );
            
            captionPanel.add(new Label(ImageStepGlobalizationUtil.globalize(
                                "cms.contentasset.image.ui.caption")));
            Label captionText = new Label( new PrintListener() {
                public void prepare( PrintEvent ev ) {
                    Label l = (Label) ev.getTarget();
                    String caption = attachment.getCaption();
                    if( null == caption ) {
                        l.setLabel( ImageStepGlobalizationUtil.globalize(
                                    "cms.ui.unknown") );
                    } else {
                        l.setLabel( caption );
                    }
                }
            } );
            captionText.setOutputEscaping( false );
            captionPanel.add( captionText );
            container.add( captionPanel );

            /* Create a box panel beloy the image to display use context*/
            BoxPanel useContextPanel = new BoxPanel( BoxPanel.HORIZONTAL );
            
            useContextPanel.add(new Label( ImageStepGlobalizationUtil.globalize(
                                "cms.contentasset.image.ui.use_context") ) );
            Label useContextLabel = new Label( new PrintListener() {
                public void prepare( PrintEvent ev ) {
                    Label l = (Label) ev.getTarget();
                    String useContext = attachment.getUseContext();
                    if( null == useContext ) {
                        l.setLabel( ImageStepGlobalizationUtil.globalize(
                                    "cms.ui.unknown") );
                    } else {
                        l.setLabel( useContext );
                    }
                }
            } );
            useContextLabel.setOutputEscaping( false );
            useContextPanel.add( useContextLabel );
            container.add( useContextPanel );

            /* Add a link to remove the image in a separate container elemet */
            ControlLink deleteLink = new ControlLink(new Label(
                    ImageStepGlobalizationUtil.globalize(
                    "cms.contentassets.ui.image_step.remove_attached_image") )) {
                @Override
                public void setControlEvent( PageState ps ) {
                    String oid = ps.getControlEventValue();
                    ps.setControlEvent( list, DELETE, oid );
                }
            };
            container.add( deleteLink );

            return container;
        }
    }
}
