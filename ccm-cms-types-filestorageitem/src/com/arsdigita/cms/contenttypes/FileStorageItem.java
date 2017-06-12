/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.FileAsset;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * This content type represents a FileStorageItem.
 *
 * @version $Revision: #7 $ $Date: 2004/08/17 $
 */
public class FileStorageItem extends ContentPage {

    private final static Logger s_log = Logger.getLogger(FileStorageItem.class);

    /** PDL property name for description */
    public static final String DESCRIPTION = "description";
    /** PDL property name for file */
    public static final String FILE = "file";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.FileStorageItem";

    public static final int DESCRIPTION_LENGTH = 500;

    public FileStorageItem() {
        this( BASE_DATA_OBJECT_TYPE );
    }

    public FileStorageItem( BigDecimal id )
        throws DataObjectNotFoundException {
        this( new OID( BASE_DATA_OBJECT_TYPE, id ) );
    }

    public FileStorageItem( OID id )
        throws DataObjectNotFoundException {
        super( id );
    }

    public FileStorageItem( DataObject obj ) {
        super( obj );
    }

    public FileStorageItem( String type ) {
        super( type );
    }

    @Override
    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }


    /* accessors *****************************************************/
    @Override
    public String getDescription() {
        return (String) get( DESCRIPTION );
    }

    @Override
    public void setDescription( String description ) {
        set( DESCRIPTION, description );
    }

    // Search stuff to allow the content type to be searchable
    public static final int SUMMARY_LENGTH = 200;
    @Override
    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getDescription(),
                                                             SUMMARY_LENGTH,
                                                             true);
    }

    public FileAsset getFile() {
        DataObject file = (DataObject) get(FILE);
        if (file != null) {
            return new FileAsset(file);
        } else {
            return null;
        }
    }

    public void setFile(FileAsset file) {
        setAssociation(FILE, file);
    }
    /**
     * Instruct search framework to include file contents in the same record
     * as the other parts of the page (eg title, metadata etc). See
     * indexAssetsWithPage in com.arsdigita.cms.ContentPage
     *
     * @return 
     */
    @Override
     public boolean indexAssetsWithPage() {
         return true;
     }
                                                  
}
