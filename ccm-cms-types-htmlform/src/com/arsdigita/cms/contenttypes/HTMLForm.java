/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.cms.ContentType;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * This content type represents an article.
 *
 * @version $Revision: #1 $ $Date: 2004/03/05 $
 */
public class HTMLForm extends GenericArticle {

    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(HTMLForm.class);

    /** PDL property name for lead */
    public static final String LEAD = "lead";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
                               = "com.arsdigita.cms.contenttypes.HTMLForm";

    /** Data object type for this domain object (for CMS compatibility) */
    public static final String TYPE
        = BASE_DATA_OBJECT_TYPE;

    public HTMLForm() {
        this( BASE_DATA_OBJECT_TYPE );
    }

    public HTMLForm( BigDecimal id )
        throws DataObjectNotFoundException {
        this( new OID( BASE_DATA_OBJECT_TYPE, id ) );
    }

    public HTMLForm( OID id )
        throws DataObjectNotFoundException {
        super( id );
    }

    public HTMLForm( DataObject obj ) {
        super( obj );
    }

    public HTMLForm( String type ) {
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

}
