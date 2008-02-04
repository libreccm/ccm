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

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;


/**
 * This content type represents a FAQItem.
 *
 * @version $Revision: #7 $ $Date: 2004/08/17 $
 */
public class FAQItem extends ContentPage {

    /** PDL property name for question */
    public static final String QUESTION = "question";
    /** PDL property name for answer */
    public static final String ANSWER = "answer";
    /** PDL property for name of section */
    public static final String SECTION_NAME = "sectionName";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.FAQItem";

    public FAQItem() {
        this( BASE_DATA_OBJECT_TYPE );
    }

    public FAQItem( BigDecimal id )
        throws DataObjectNotFoundException {
        this( new OID( BASE_DATA_OBJECT_TYPE, id ) );
    }

    public FAQItem( OID id )
        throws DataObjectNotFoundException {
        super( id );
    }

    public FAQItem( DataObject obj ) {
        super( obj );
    }

    public FAQItem( String type ) {
        super( type );
    }

    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }


    /* accessors *****************************************************/
    public String getQuestion() {
        return (String) get( QUESTION );
    }

    public void setQuestion( String question ) {
        set( QUESTION, question );
    }

    public String getSectionName() {
        return (String) get( SECTION_NAME);
    }

    public void setSectionName( String sn) {
        set( SECTION_NAME, sn );
    }

    public String getAnswer() {
      return (String) get( ANSWER );
  }

  public void setAnswer( String answer ) {
      set( ANSWER, answer );
  }

    // Search stuff to allow the content type to be searchable
    public String getSearchSummary() {
        return getQuestion();
    }

}
