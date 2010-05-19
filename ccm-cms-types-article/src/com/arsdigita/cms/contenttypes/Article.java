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


import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * This content type represents an article.
 *
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 */
public class Article extends com.arsdigita.cms.basetypes.Article {


    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(Article.class);

    /** PDL property name for lead */
    public static final String LEAD = "lead";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.Article";

    public Article() {
        this( BASE_DATA_OBJECT_TYPE );
    }

    public Article( BigDecimal id )
        throws DataObjectNotFoundException {
        this( new OID( BASE_DATA_OBJECT_TYPE, id ) );
    }

    public Article( OID id )
        throws DataObjectNotFoundException {
        super( id );
    }

    public Article( DataObject obj ) {
        super( obj );
    }

    public Article( String type ) {
        super( type );
    }

    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }

    public String getLead() {
        return (String) get( LEAD );
    }

    public void setLead( String lead ) {
        set( LEAD, lead );
    }

    public static final int SUMMARY_LENGTH = 200;
    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getLead(),
                                                             SUMMARY_LENGTH,
                                                             true);
    }

}
