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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.contentassets.ItemImageAttachment;
import com.arsdigita.cms.ui.ImageComponent;
import com.arsdigita.cms.ui.ImageLibraryComponent;
import com.arsdigita.cms.ui.ImageUploadComponent;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class ImageStepEdit extends SimpleContainer
        implements FormProcessListener, FormInitListener {

    private static final Logger s_log = Logger.getLogger(ImageStepEdit.class);

    private final ImageStep m_imageStep;

    private final StringParameter m_imageComponentKey;
    private final MapComponentSelectionModel m_imageComponent;

    private final String UPLOAD = "upload";
    private final String LIBRARY = "library";

    public ImageStepEdit( ImageStep step ) {
        m_imageStep = step;

        m_imageComponentKey = new StringParameter( "imageComponent" );

        ParameterSingleSelectionModel componentModel =
            new ParameterSingleSelectionModel( m_imageComponentKey );
        m_imageComponent =
            new MapComponentSelectionModel( componentModel, new HashMap() );

        Map selectors = m_imageComponent.getComponentsMap();

        ImageUploadComponent upload = new ImageUploadComponent();
        upload.getForm().addInitListener(this);
        upload.getForm().addProcessListener(this);
        selectors.put( UPLOAD, upload );
        add( upload );

        ImageLibraryComponent library = new ImageLibraryComponent();
        library.getForm().addInitListener(this);
        library.getForm().addProcessListener(this);
        selectors.put( LIBRARY, library );
        add( library );
    }

    @Override
    public void register( Page p ) {
        super.register( p );

        Map componentsMap = m_imageComponent.getComponentsMap();
        Iterator i = componentsMap.keySet().iterator();
        while( i.hasNext() ) {
            Object key = i.next();
            Component component = (Component) componentsMap.get( key );

            p.setVisibleDefault( component, LIBRARY.equals( key ) );
        }

        p.addComponentStateParam( this, m_imageComponentKey );
    }

    Iterator getImageComponents() {
        return m_imageComponent.getComponentsMap().values().iterator();
    }

    private ImageComponent getImageComponent( PageState ps ) {
        if( !m_imageComponent.isSelected( ps ) ) {
            if( s_log.isDebugEnabled() ) {
                s_log.debug( "No component selected" );
                s_log.debug( "Selected: " + m_imageComponent.getComponent( ps ) );
            }

            m_imageComponent.setSelectedKey( ps, UPLOAD );
        }

        return (ImageComponent) m_imageComponent.getComponent( ps );

    }

    private void setImageComponent( PageState ps, final String activeKey ) {
        m_imageComponent.setSelectedKey( ps, activeKey );

        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Selected component: " + activeKey );
        }

        Map componentsMap = m_imageComponent.getComponentsMap();
        Iterator i = componentsMap.keySet().iterator();
        while( i.hasNext() ) {
            Object key = i.next();
            Component component = (Component) componentsMap.get( key );

            boolean isVisible = activeKey.equals( key );

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Key: " + key + "; Visibility: " + isVisible );
            }

            ps.setVisible( component, isVisible );
        }
    }

    public void init(FormSectionEvent event)
        throws FormProcessException {
        PageState ps = event.getPageState();

        ItemImageAttachment attachment = m_imageStep.getAttachment( ps );
        if( null == attachment ) {
            // XXX: Do something
        }
    }

    public void process( FormSectionEvent event ) throws FormProcessException {
        PageState ps = event.getPageState();
        ImageComponent component = getImageComponent( ps );

        if( !component.getSaveCancelSection().getSaveButton().isSelected( ps ) ) {
            return;
        }

        ContentItem item = m_imageStep.getItem( ps );
        if( null == item ) {
            s_log.error( "No item selected in ImageStepEdit",
                         new RuntimeException() );
            return;
        }

        ReusableImageAsset image = component.getImage( event );

        ItemImageAttachment attachment = m_imageStep.getAttachment( ps );
        if( null == attachment ) {
            attachment = new ItemImageAttachment( item, image );
        }
        attachment.setCaption( component.getCaption( event ) );

        // We only set the description and title based on the UI in
        // the case where getIsImageStepDescriptionAndTitleShown is true.
        // Otherwise, we leave this as the default value.  This means 
        // existing values are not overwritten if the image is edited when
        // isImageStepDescriptionAndTitleShown is false.
		if(ItemImageAttachment.getConfig().getIsImageStepDescriptionAndTitleShown()) {
	        attachment.setDescription(component.getDescription( event ));
			attachment.setTitle(component.getTitle( event ));
		}
        attachment.setUseContext( component.getUseContext( event ) );
    }

//    interface ImageComponent {
//        ReusableImageAsset getImage( FormSectionEvent event )
//            throws FormProcessException;
//        String getCaption( FormSectionEvent event );
//		String getDescription( FormSectionEvent event );
//		String getTitle( FormSectionEvent event );
//        String getUseContext( FormSectionEvent event );
//        SaveCancelSection getSaveCancelSection();
//        Form getForm();
//    }
//
//    private class ImageUploadComponent extends Form
//                                       implements ImageComponent {
//        private final FileUploadSection m_imageFile;
//        private final TextField m_caption;
//		private final TextField m_title;
//		private final TextArea m_description;
//        private final TextField m_useContext;
//        private final SaveCancelSection m_saveCancel;
//
//        public ImageUploadComponent() {
//            super("imageStepEditUpload", new ColumnPanel(2));
//
//            setEncType("multipart/form-data");
//
//			// Ignoring deprecated constructor.
//            m_imageFile = new FileUploadSection("Image Type",
//                                                "image", ImageAsset.MIME_JPEG);
//            m_imageFile.getFileUploadWidget()
//                .addValidationListener(new NotNullValidationListener());
//            
//            add( m_imageFile, ColumnPanel.FULL_WIDTH );
//
//            add(new Label("Caption"));
//            m_caption = new TextField("caption");
//            m_caption.addValidationListener(new NotNullValidationListener());
//			m_caption.addValidationListener(new StringLengthValidationListener(40));
//            m_caption.setSize(40);
//            add(m_caption);
//
//			m_title = new TextField("title");
//			m_description = new TextArea("description");
//
//			// We only show the title and description fields in the case where 
//			// getIsImageStepDescriptionAndTitleShown is false.
//			if (ItemImageAttachment.getConfig().getIsImageStepDescriptionAndTitleShown()) {
//				add(new Label("Title"));
//				m_title.addValidationListener(new NotNullValidationListener());
//				m_title.setSize(40);
//				m_title.addValidationListener(new StringLengthValidationListener(40));
//				add(m_title);
//
//				add(new Label("Description"));
//				m_description.addValidationListener(new NotNullValidationListener());
//				m_description.addValidationListener(new StringLengthValidationListener(600));
//				m_description.setCols(30);
//				m_description.setRows(5);
//				add(m_description);
//
//			}
//
//            add(new Label("Use Context"));
//            m_useContext = new TextField("useContext");
//            m_useContext.setSize(40);
//// Removed to use multiple images with fancyBox
////            m_useContext.addValidationListener( new UniqueUseContextListener() );
//            add(m_useContext);
//
//            m_saveCancel = new SaveCancelSection();
//            add(m_saveCancel);
//
//            /* Removed by Quasimodo: Changed editing workflow, so that library comes first
//             * Also, library mode has now a link to upload images whixh will link to this 
//             * form. Consequently, this link will create a loop, which isn't fatal but 
//             * confusing
//            ActionLink library = new ActionLink( "Select an existing image" );
//            library.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent ev ) {
//                    setImageComponent( ev.getPageState(), LIBRARY );
//                }
//            } );
//            add( library, ColumnPanel.FULL_WIDTH );
//            */
//        }
//
//        public SaveCancelSection getSaveCancelSection() {
//            return m_saveCancel;
//        }
//
//        public ReusableImageAsset getImage( FormSectionEvent event )
//            throws FormProcessException
//        {
//            PageState ps = event.getPageState();
//
//            String filename = (String) m_imageFile.getFileName( event );
//            File imageFile = m_imageFile.getFile( event );
//
//            try {
//                ReusableImageAsset image = new ReusableImageAsset();
//                image.loadFromFile( filename, imageFile, ImageAsset.MIME_JPEG );
//                image.setDescription( ( String ) m_caption.getValue( ps ) );
//
//                return image;
//            } catch ( IOException ex ) {
//                s_log.error( "Error loading image from file", ex );
//                throw new FormProcessException( ex.getMessage() );
//            }
//        }
//
//        public String getCaption( FormSectionEvent event ) {
//            PageState ps = event.getPageState();
//            return (String) m_caption.getValue( ps );
//        }
//
//		public String getDescription( FormSectionEvent event ) {
//			PageState ps = event.getPageState();
//			return (String) m_description.getValue( ps );
//		}
//
//		public String getTitle( FormSectionEvent event ) {
//			PageState ps = event.getPageState();
//			return (String) m_title.getValue( ps );
//		}
//
//        public String getUseContext( FormSectionEvent event ) {
//            PageState ps = event.getPageState();
//            return (String) m_useContext.getValue( ps );
//        }
//
//        public Form getForm() {
//            return this;
//        }
//    }
//
//    private class ImageLibraryComponent extends SimpleContainer
//                                        implements ImageComponent {
//        private final ImageChooser m_chooser;
//        private final ItemSelectionModel m_imageModel;
//        private final BigDecimalParameter m_imageID;
//
//        private final Form m_form;
//        private final TextField m_caption;
//		private final TextField m_description;
//		private final TextField m_title;
//        private final TextField m_useContext;
//        private final SaveCancelSection m_saveCancel;
//
//        public ImageLibraryComponent() {
//            m_imageID = new BigDecimalParameter( "imageID" );
//            m_imageModel = new ItemSelectionModel( m_imageID );
//
//            m_chooser = new ImageChooser( ContentItem.DRAFT, ImageBrowser.SELECT_IMAGE );
//            m_chooser.addImageActionListener( new ImageBrowser.LinkActionListener() {
//                public void deleteClicked( PageState ps, BigDecimal imageID ) {
//                    s_log.debug( "Clicked delete" );
//
//                    ReusableImageAsset image =
//                        new ReusableImageAsset( imageID );
//                    image.delete();
//                }
//
//                public void linkClicked( PageState ps, BigDecimal imageID ) {
//                    s_log.debug( "Clicked select" );
//                    try {
//                        ReusableImageAsset image =
//                            new ReusableImageAsset( imageID );
//
//                        m_imageModel.setSelectedObject( ps, image );
//                    } catch( DataObjectNotFoundException ex ) {
//                        s_log.error( "Selected non-existant image: " + imageID, ex );
//                    }
//                }
//            } );
//            // Don't display the delete links
//            /*
//            m_chooser.getImageBrowser().getColumn(5)
//                     .setCellRenderer( new TableCellRenderer() {
//                public Component getComponent( Table table, PageState state,
//                                               Object value, boolean isSelected,
//                                               Object key, int row,
//                                               int column ) {
//                    return new Label( "&nbsp;", false );
//                }
//            } );
//            */
//            add( m_chooser );
//
//            m_form = new Form( "imageStepEditLibrary", new ColumnPanel( 2 ) );
//            add( m_form );
//
//            m_form.add(new Label("Caption"));
//            m_caption = new TextField("caption");
//            m_caption.addValidationListener(new NotNullValidationListener());
//            m_caption.setSize(40);
//            m_form.add(m_caption);
//
//			m_description = new TextField("description");
//			m_description.addValidationListener(new NotNullValidationListener());
//			m_description.setSize(40);
//
//			m_title = new TextField("title");
//			m_title.addValidationListener(new NotNullValidationListener());
//			m_title.setSize(40);
//
//			// Only show the title and description fields where these have
//			// been explicitly requested.
//			if (ItemImageAttachment.getConfig().getIsImageStepDescriptionAndTitleShown()) {
//				m_form.add(new Label("Description"));
//				m_form.add(m_description);
//				m_form.add(new Label("Title"));
//				m_form.add(m_title);
//			}
//
//
//            m_form.add(new Label("Use Context"));
//            m_useContext = new TextField("useContext");
//            m_useContext.setSize(40);
//// Removed to use multiple images with fancyBox
////            m_useContext.addValidationListener( new UniqueUseContextListener() );
//            m_form.add(m_useContext);
//
//            m_saveCancel = new SaveCancelSection();
//            m_form.add(m_saveCancel);
//
//            ActionLink upload = new ActionLink( "Upload a new image" );
//            upload.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent ev ) {
//                    setImageComponent( ev.getPageState(), UPLOAD );
//                }
//            } );
//            add( upload, ColumnPanel.FULL_WIDTH );
//        }
//
//        public ReusableImageAsset getImage( FormSectionEvent event ) {
//            PageState ps = event.getPageState();
//
//            return (ReusableImageAsset) m_imageModel.getSelectedItem( ps );
//        }
//
//        @Override
//        public void register( Page p ) {
//            super.register( p );
//
//            p.addComponentStateParam( this, m_imageID );
//        }
//
//        public String getCaption( FormSectionEvent event ) {
//            PageState ps = event.getPageState();
//            return (String) m_caption.getValue( ps );
//        }
//
//		public String getDescription( FormSectionEvent event ) {
//			PageState ps = event.getPageState();
//			return (String) m_description.getValue( ps );
//		}
//
//		public String getTitle( FormSectionEvent event ) {
//			PageState ps = event.getPageState();
//			return (String) m_title.getValue( ps );
//		}
//
//
//        public String getUseContext( FormSectionEvent event ) {
//            PageState ps = event.getPageState();
//            return (String) m_useContext.getValue( ps );
//        }
//
//        public Form getForm() {
//            return m_form;
//        }
//
//        public SaveCancelSection getSaveCancelSection() {
//            return m_saveCancel;
//        }
//    }

    private class UniqueUseContextListener implements ParameterListener {
        public void validate( ParameterEvent ev )
            throws FormProcessException
        {
            PageState ps = ev.getPageState();
            ParameterData data = ev.getParameterData();

            ContentItem item = m_imageStep.getItem( ps );
            Assert.exists( item, ContentItem.class );

            String value = data.getValue().toString();
            value = StringUtils.trimleft( value );
            if( StringUtils.emptyString( value ) ) {
                value = null;
            }
            data.setValue( value );

            DataCollection attachments =
                ItemImageAttachment.getImageAttachments( item );
            attachments
                .addEqualsFilter(ItemImageAttachment.USE_CONTEXT, value );

            try {
                if( attachments.next() ) {
                    ItemImageAttachment attachment = m_imageStep.getAttachment( ps );
                    BigDecimal dupAttachmentID = (BigDecimal)
                        attachments.get( ItemImageAttachment.ID );

                    if( null == attachment ||
                        !attachment.getID().equals( dupAttachmentID ) )
                    {
                        data.addError( "There is already an image for this " +
                                       "item with this context" );
                    }
                }
            } finally {
                attachments.close();
            }
        }
    }
}
