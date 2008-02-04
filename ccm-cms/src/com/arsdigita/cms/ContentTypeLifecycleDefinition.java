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
 *
 */
package com.arsdigita.cms;

import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;

/**
 * This class associates {@link com.arsdigita.cms.ContentSection
 * content sections} and {@link com.arsdigita.cms.ContentType content
 * types} with particular publication lifecycles.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #9 $ $Date: 2004/08/17 $
 */
public class ContentTypeLifecycleDefinition extends DomainObject {

    public static final String versionId = "$Id: ContentTypeLifecycleDefinition.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";


    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.ContentTypeLifecycleDefinition";

    protected static final String SECTION_ID = "sectionId";
    protected static final String CONTENT_TYPE_ID = "contentTypeId";
    protected static final String LIFECYCLE_DEFINITION_ID = "lifecycleDefinitionId";


    protected ContentTypeLifecycleDefinition() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    protected ContentTypeLifecycleDefinition(OID oid)
        throws DataObjectNotFoundException {
        super(oid);
    }

    protected ContentTypeLifecycleDefinition(DataObject obj) {
        super(obj);
    }


    protected BigDecimal getContentSectionID() {
        return (BigDecimal) get(SECTION_ID);
    }

    protected void setContentSection(ContentSection section) {
        set(SECTION_ID, section.getID());
    }

    protected BigDecimal getContentTypeID() {
        return (BigDecimal) get(CONTENT_TYPE_ID);
    }

    protected void setContentType(ContentType type) {
        set(CONTENT_TYPE_ID, type.getID());
    }

    protected BigDecimal getLifecycleDefinitionID() {
        return (BigDecimal) get(LIFECYCLE_DEFINITION_ID);
    }

    protected void setLifecycleDefinition(LifecycleDefinition definition) {
        set(LIFECYCLE_DEFINITION_ID, definition.getID());
    }

    /**
     * Get the default associated lifecycle definition for a content type in a
     * particular content section
     */
    public static LifecycleDefinition getLifecycleDefinition(ContentSection section,
                                                             ContentType type) {
        try {
            OID oid = new OID(BASE_DATA_OBJECT_TYPE);
            oid.set(SECTION_ID, section.getID());
            oid.set(CONTENT_TYPE_ID, type.getID());

            ContentTypeLifecycleDefinition assn =
                new ContentTypeLifecycleDefinition(oid);
            BigDecimal lifecycleDefinitionID = assn.getLifecycleDefinitionID();

            return new LifecycleDefinition
                (new OID(LifecycleDefinition.BASE_DATA_OBJECT_TYPE,
                         lifecycleDefinitionID));

        } catch (DataObjectNotFoundException e) {
            return null;
        }

    }

    /**
     * Associated a default lifecycle definition for a content type in a
     * particular content section.  If this association already exists, the
     * previous association will be updated.
     * @return true is association is added, false if updated
     */
    public static boolean updateLifecycleDefinition(ContentSection section,
                                                    ContentType type,
                                                    LifecycleDefinition lifecycle) {

        try {
            OID oid = new OID(BASE_DATA_OBJECT_TYPE);
            oid.set(SECTION_ID, section.getID());
            oid.set(CONTENT_TYPE_ID, type.getID());

            ContentTypeLifecycleDefinition assn =
                new ContentTypeLifecycleDefinition(oid);
            assn.setLifecycleDefinition(lifecycle);
            assn.save();
            return false;

        } catch (DataObjectNotFoundException e) {
            //this association does not exist
            ContentTypeLifecycleDefinition assn =
                new ContentTypeLifecycleDefinition();
            assn.setContentSection(section);
            assn.setContentType(type);
            assn.setLifecycleDefinition(lifecycle);
            assn.save();
            return true;
        }
    }



    /**
     * Remove the default lifecycle definition association for a content type in
     * a particular content section.
     * @return true if association is deleted, false otherwise
     */
    public static boolean removeLifecycleDefinition(ContentSection section,
                                                    ContentType type) {
        try {
            OID oid = new OID(BASE_DATA_OBJECT_TYPE);
            oid.set(SECTION_ID, section.getID());
            oid.set(CONTENT_TYPE_ID, type.getID());

            ContentTypeLifecycleDefinition assn =
                new ContentTypeLifecycleDefinition(oid);
            assn.delete();
            return true;
        } catch (DataObjectNotFoundException e) {
            //don't do anything since it does not exist
            return false;
        }

    }

}
