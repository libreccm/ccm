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

import com.arsdigita.cms.contentassets.ItemImageAttachment;

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
import com.arsdigita.cms.ui.ImageDisplay;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

/**
 * Pluggable authoring step to add an ImageAsset to a content item
 * Currently only supports adding one image though the PDL has
 * association for multiple.
 */

public class ImageStepDisplay extends SimpleContainer {
    private static final Logger s_log = Logger.getLogger(ImageStepDisplay.class);

    private final ImageStep m_imageStep;

    private final static String DELETE = "deleteAttachment";

    public ImageStepDisplay( ImageStep step ) {
        super();

        m_imageStep = step;

        Label mainLabel = new Label
            ("This item does not have any associated images.");
        mainLabel.setFontWeight(Label.ITALIC);

        List imageList = new List( new ImageListModelBuilder() ) {
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

        add( imageList );
    }

    private class ImageListModelBuilder extends LockableImpl
                                        implements ListModelBuilder {
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

    private class ImageListCellRenderer implements ListCellRenderer {
        public Component getComponent( final List list, PageState state,
                                       Object value, String key,
                                       int index, boolean isSelected ) {
            final ItemImageAttachment attachment = (ItemImageAttachment) value;

            BoxPanel container = new BoxPanel( BoxPanel.VERTICAL );
            container.setBorder( 1 );

            container.add( new ImageDisplay(null) {
                protected void generateImagePropertiesXML( ImageAsset image,
                                                           PageState state,
                                                           Element element ) {
                    super.generateImagePropertiesXML(image, state, element);

                    String caption = attachment.getCaption();
                    if (caption != null) {
                        element.addAttribute("caption", caption);
                    }

                    // We check here to see whether IsImageStepDescriptionAndTitleShown
                    // is set to true.  If it is, we display the description and title options
                    if(ItemImageAttachment.getConfig().getIsImageStepDescriptionAndTitleShown()) {
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

                protected ImageAsset getImageAsset( PageState ps ) {
                    return attachment.getImage();
                }
            } );

            BoxPanel useContextPanel = new BoxPanel( BoxPanel.HORIZONTAL );
            useContextPanel.add( new Label( "Use Context: " ) );
            Label useContextLabel = new Label( new PrintListener() {
                public void prepare( PrintEvent ev ) {
                    Label l = (Label) ev.getTarget();
                    String useContext = attachment.getUseContext();
                    if( null == useContext ) {
                        l.setLabel( "<i>Unknown</i>" );
                    } else {
                        l.setLabel( useContext );
                    }
                }
            } );
            useContextLabel.setOutputEscaping( false );
            useContextPanel.add( useContextLabel );
            container.add( useContextPanel );

            ControlLink delete = new ControlLink( "Delete" ) {
                public void setControlEvent( PageState ps ) {
                    String oid = ps.getControlEventValue();
                    ps.setControlEvent( list, DELETE, oid );
                }
            };
            container.add( delete );

            return container;
        }
    }
}
