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

package com.arsdigita.cms.contentassets.imagestep;

import com.arsdigita.navigation.DataCollectionPropertyRenderer;

import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.contentassets.ItemImageAttachment;

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

/**
 * Classes implementing this interface will render in XML a property which
 * will normally have been previously added to a
 * <code>DataCollectionDefinition</code>.
 */
public class ImagePropertyRenderer implements DataCollectionPropertyRenderer {

    public static final String XML_NS =
                               "http://ccm.redhat.com/london/image_attachments";

    /**
     * Called from DataCollectionRenderer for every returned item. This method
     * will add XML for the property to the renderer's output.
     */
    public void render( DataCollection dc, Element parent ) {

        Object images = dc.get( ItemImageAttachment.IMAGE_ATTACHMENTS );
        if( null == images ) return;

        if( images instanceof DataObject ) {
            Element root = rootElement( parent );
            render( (DataObject) images, root );
        }

        else if( images instanceof DataAssociation ) {
            // XXX: Unused, untested code path
            DataAssociationCursor cursor = ((DataAssociation) images).cursor();

            Element root = null;
            while( cursor.next() ) {
                if( null == root ) root = rootElement( parent );
                render( cursor.getDataObject(), root );
            }
        }

        else {
            throw new UncheckedWrapperException(
                "While trying to render image property, " +
                ItemImageAttachment.IMAGE_ATTACHMENTS +
                " association returned a " + images.getClass().getName() +
                " (" + images.toString() +
                "). Expected either a DataObject or a DataAssociation."
            );
        }
    }

    private void render( DataObject obj, Element root ) {
        Element ia = root.newChildElement( "ia:imageAttachment", XML_NS );

        DataObject image = (DataObject) obj.get( ItemImageAttachment.IMAGE );

        Object context = obj.get( ItemImageAttachment.USE_CONTEXT );
		Object description = obj.get( ItemImageAttachment.DESCRIPTION );
		Object title = obj.get( ItemImageAttachment.TITLE );
        Object caption = obj.get( ItemImageAttachment.CAPTION );
        Object imageID = image.get( ReusableImageAsset.ID );
        Object width = image.get( ReusableImageAsset.WIDTH );
        Object height = image.get( ReusableImageAsset.HEIGHT );

        Element imageIDE = ia.newChildElement( "ia:imageID", XML_NS );
        imageIDE.setText( imageID.toString() );

        if( null != context ) {
            Element contextE = ia.newChildElement( "ia:context", XML_NS );
            contextE.setText( context.toString() );
        }

        if( null != caption ) {
            Element captionE = ia.newChildElement( "ia:caption", XML_NS );
            captionE.setText( caption.toString() );
        }

		if( null != title) {
			Element titleE = ia.newChildElement( "ia:title", XML_NS );
			titleE.setText( title.toString() );
		}

		if( null != description) {
			Element descriptionE = ia.newChildElement( "ia:description", XML_NS );
			descriptionE.setText( title.toString() );
		}

        if( null != width ) {
            Element widthE = ia.newChildElement( "ia:width", XML_NS );
            widthE.setText( width.toString() );
        }

        if( null != height ) {
            Element heightE = ia.newChildElement( "ia:height", XML_NS );
            heightE.setText( height.toString() );
        }
    }

    private Element rootElement( Element parent ) {
        return parent.newChildElement( "ia:imageAttachments", XML_NS );
    }
}
