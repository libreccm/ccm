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
package com.arsdigita.cms.search;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectTraversal;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.search.ContentProvider;
import com.arsdigita.search.ContentType;
import com.arsdigita.search.converter.ConversionException;
import com.arsdigita.search.converter.Converter;
import com.arsdigita.search.converter.ConverterRegistry;
import com.arsdigita.util.StringUtils;

import org.apache.log4j.Logger;

public class TextContentProvider implements ContentProvider {

    private DomainObject m_obj;
    private String m_context;
    private String m_adapterContext;

    public TextContentProvider(String context,
                               DomainObject obj,
                               String adapterContext) {
        m_context = context;
        m_obj = obj;
        m_adapterContext = adapterContext;
    }

    public String getContext() {
        return m_context;
    }

    public ContentType getType() {
        return ContentType.TEXT;
    }

    public byte[] getBytes() {
        
        // Long term, this should be uncommented and the conversion
        // should be moved in to the com.arsdigita.search package.
        // But, for now, this is faster/easier
        //DomainObjectTextRenderer renderer =
        //    new DomainObjectTextRenderer();
        TextRenderer renderer = new TextRenderer();

        renderer.walk(m_obj, m_adapterContext);

        String text = renderer.getText();
        return text.getBytes();
    }

    /**
     * An implementation of the traversal code that converts FileAssets in
     * to the appropriate text
     */
    public class TextRenderer extends DomainObjectTraversal {
        
        private Logger s_log = Logger.getLogger(TextRenderer.class);
        private StringBuffer m_text;

        /**
         */
        public TextRenderer() {
            m_text = new StringBuffer("");
        }

        public String getText() {
            return m_text.toString();
        }

        protected void beginObject(DomainObject obj,
                                   String path) {

            if (!ContentSection.getConfig().getDisableFileAssetExtraction()) {
                if (obj.getObjectType().isSubtypeOf(FileAsset.BASE_DATA_OBJECT_TYPE)) {
                    FileAsset fa = (FileAsset) obj;
            appendFileAsset( fa );

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Adding file asset object " + fa.getOID() );
                    }
            }
        }
        }

        protected void endObject(DomainObject obj,
                                 String path) {
        }

        protected void revisitObject(DomainObject obj,
                                     String path) {
        }

        protected void handleAttribute(DomainObject obj,
                                       String path,
                                       Property property) {

            Object value = 
                DomainServiceInterfaceExposer.get(obj, property.getName());

            if (value != null &&
                property.isRole() && 
                (value instanceof DataObject)) {
                
                ObjectType assetType = ((DataObject)value).getOID().getObjectType();
                String assetTypeName = assetType.getQualifiedName();

                if (assetTypeName.equals(TextAsset.BASE_DATA_OBJECT_TYPE)) {
                    if( s_log.isDebugEnabled() ) {
                        s_log.debug( "Adding text asset attribute " +
                                     property.getName() + " for " +
                                     obj.getOID() );
                    }
                    
                    appendTextAsset(new TextAsset((DataObject)value));

                } else if (assetTypeName.equals(FileAsset.BASE_DATA_OBJECT_TYPE)) {
                    if (!ContentSection.getConfig().getDisableFileAssetExtraction()) {
                    if( s_log.isDebugEnabled() ) {
                        s_log.debug( "Adding file asset attribute " +
                                     property.getName() + " for " +
                                     obj.getOID() );
                    }

                    FileAsset fa = new FileAsset((DataObject)value);
                    appendFileAsset(fa);
                }
                }

            } else {
                if (value != null && !property.isRole()) {
                    m_text.append(" " + value);
                }
            }
        }

        protected void beginRole(DomainObject obj,
                                 String path,
                                 Property property) {
        }

        protected void endRole(DomainObject obj,
                               String path,
                               Property property) {
        }


        protected void beginAssociation(DomainObject obj,
                                        String path,
                                        Property property) {
            DataAssociation association = 
                (DataAssociation)DomainServiceInterfaceExposer.get
                (obj, property.getName());
            
            if (association != null && 
                association.getObjectType().isSubtypeOf
                (FileAsset.BASE_DATA_OBJECT_TYPE)) {

                if (!ContentSection.getConfig().getDisableFileAssetExtraction()) {
                    while (association.next()) {
                        if( s_log.isDebugEnabled() ) {
                            s_log.debug( "Adding file asset association " +
                                     property.getName() + " for " +
                                     obj.getOID() );
                        }
                        if (obj.getObjectType().isSubtypeOf(ContentPage.BASE_DATA_OBJECT_TYPE)) {
                            ContentPage cp = (ContentPage) obj;
                            if (cp.indexAssetsWithPage()) {
                                appendFileAsset(new FileAsset(association.getDataObject()));
                            }
                        }
                    }
                }

            } else if (association != null && 
                association.getObjectType().isSubtypeOf
                (TextAsset.BASE_DATA_OBJECT_TYPE)) {
                while (association.next()) {
                    if( s_log.isDebugEnabled() ) {
                        s_log.debug( "Adding text asset association " +
                                     property.getName() + " for " +
                                     obj.getOID() );
                    }

                    appendTextAsset(new TextAsset(association.getDataObject()));
                }
            }
        }
        
        protected void endAssociation(DomainObject obj,
                                      String path,
                                      Property property) {
        }

        private void appendTextAsset(TextAsset asset) {
            String content = asset.getText();
            if (content != null) {
                m_text.append(" " + StringUtils.htmlToText(content));
            }
        }

        private void appendFileAsset(FileAsset asset) {
            Converter converter = 
                ConverterRegistry.getConverter(asset.getMimeType());
            if (converter != null) {
                if( s_log.isDebugEnabled() ) {
                    s_log.debug( "Converting " + asset.getOID() + " using " +
                                 converter.getClass().getName() );
                }

                try {
                    String converted = converter.convertDocument
                        ( asset.getContent() );

                    if( s_log.isDebugEnabled() ) {
                        s_log.debug( "Converted file is: " + converted );
                    }

                    m_text.append( " " ).append( converted );
                } catch (ConversionException e) {
                    s_log.error("Error converting FileAsset " + asset.getOID()+
                                " with MimeType " + asset.getMimeType(), e);
                }
            } else {
                s_log.debug("Skipping FileAsset " + asset.getOID() +
                            " with MimeType " + asset.getMimeType());
            }
        }
    }
}
