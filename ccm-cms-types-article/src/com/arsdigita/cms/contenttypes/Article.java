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
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This content type represents an article.
 *
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 */
public class Article extends GenericArticle {


    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(GenericArticle.class);

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

    @Override
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
    @Override
    public String getSearchSummary() {
        return com.arsdigita.util.StringUtils.truncateString(getLead(),
                                                             SUMMARY_LENGTH,
                                                             true);
    }

    /**
     * Retrieves all objects of this type stored in the database. Very
     * necessary for exporting all entities of the current work environment.
     *
     * @return List of all objects
     */
    public static List<Article> getAllObjects() {
        List<Article> objectList = new ArrayList<>();

        final Session session = SessionManager.getSession();
        DomainCollection collection = new DomainCollection(session.retrieve(
                Article.BASE_DATA_OBJECT_TYPE));

        while (collection.next()) {
            Article object = (Article) collection
                    .getDomainObject();
            if (object != null) {
                objectList.add(object);
            }
        }

        collection.close();
        return objectList;
    }
}
