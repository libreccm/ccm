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

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * An implementation of DomainObjectTraversal that
 * finds all associated binary assets.
 *
 * @version $Id: AssetExtractor.java 773 2005-09-12 14:52:37Z fabrice $
 */
public class AssetExtractor extends DomainObjectTraversal {

    private static final Logger s_log = Logger.getLogger(AssetExtractor.class);

    private List m_content;

    public static final String TEXT_ASSET_DATA_OBJ = "com.arsdigita.cms.TextAsset";
    public static final String FILE_ASSET_DATA_OBJ = "com.arsdigita.cms.FileAsset";

    public AssetExtractor() {
        m_content = new ArrayList();
    }

    public List getContent() {
        return m_content;
    }

    protected void beginObject(DomainObject obj,
                               String path) {

        if (!ContentSection.getConfig().getDisableFileAssetExtraction()) {
            if (obj.getObjectType().isSubtypeOf(FileAsset.BASE_DATA_OBJECT_TYPE)) {
                FileAsset fa = (FileAsset) obj;
            m_content.add( new RawContentProvider( "file", fa.getContent() ) );

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Adding file provider for object " + fa.getOID() );
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

        Object value = DomainServiceInterfaceExposer.get(obj, property.getName());

        if (value != null && property.isRole() && (value instanceof DataObject)) {
                
            ObjectType assetType = ((DataObject)value).getOID().getObjectType();
            String assetTypeName = assetType.getQualifiedName();

            if (assetTypeName.equals(TEXT_ASSET_DATA_OBJ)) {
                // add the value of the text asset to search_content
                TextAsset ta = new TextAsset((DataObject)value);
                String text = ta.getText();
                if (text != null) {
                    text = com.arsdigita.util.StringUtils.htmlToText( text );
                    m_content.add(new RawContentProvider("text", text.getBytes()));
                }
                if( s_log.isDebugEnabled() ) {
                    s_log.debug( "Adding text provider for attribute " +
                                 ta.getOID() );
                }

            } else if (assetTypeName.equals(FILE_ASSET_DATA_OBJ)) {
                if (!ContentSection.getConfig().getDisableFileAssetExtraction()) {
                // add the value of the file asset to search_content
                FileAsset fa = new FileAsset((DataObject)value);
                m_content.add(new RawContentProvider("file", fa.getContent()));
                    if( s_log.isDebugEnabled() ) {
                        s_log.debug("Adding file provider for attribute "+fa.getOID());
                    }
                }

            } else if( s_log.isDebugEnabled() ) {
                s_log.debug( "Don't know what to do with property " +
                             property.getName() + " of type " + assetTypeName );
            }

        } else if( s_log.isDebugEnabled() ) {
            s_log.debug( "Ignoring property " + property.getName() + 
                         " of type " + value.getClass().getName() );
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

        if (!ContentSection.getConfig().getDisableFileAssetExtraction()) {

        DataAssociation association = 
            (DataAssociation)DomainServiceInterfaceExposer.get
            (obj, property.getName());

        if (association != null && 
            association.getObjectType().isSubtypeOf
            (FileAsset.BASE_DATA_OBJECT_TYPE)) {
            while (association.next()) {
                // add the value of the file asset to search_content
                FileAsset fa = new FileAsset(association.getDataObject());
                m_content.add(new RawContentProvider("file", fa.getContent()));

                if( s_log.isDebugEnabled() ) {
                    s_log.debug( "Adding file provider for association " +
                                 fa.getOID() );
                }
            }
        }
    }
    }

    protected void endAssociation(DomainObject obj,
                                  String path,
                                  Property property) {
    }

}
