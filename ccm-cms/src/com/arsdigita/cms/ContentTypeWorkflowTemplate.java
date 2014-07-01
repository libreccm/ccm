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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * This class models a three-way association that represents the
 * default {@link com.arsdigita.workflow.simple.WorkflowTemplate
 * workflow template} registered to a {@link
 * com.arsdigita.cms.ContentType content type} within a {@link
 * com.arsdigita.cms.ContentSection content section}.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: ContentTypeWorkflowTemplate.java 1942 2009-05-29 07:53:23Z terry $
 */
public class ContentTypeWorkflowTemplate extends DomainObject {

    private static final Logger s_log = Logger.getLogger
        (ContentTypeWorkflowTemplate.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.ContentTypeWorkflowTemplate";

    protected static final String SECTION_ID = "sectionId";
    protected static final String CONTENT_TYPE_ID = "contentTypeId";
    protected static final String WF_TEMPLATE_ID = "workflowTemplateId";


    protected ContentTypeWorkflowTemplate() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    protected ContentTypeWorkflowTemplate(OID oid)
        throws DataObjectNotFoundException {
        super(oid);
    }

    protected ContentTypeWorkflowTemplate(DataObject obj) {
        super(obj);
    }

    protected BigDecimal getContentSectionID() {
        return (BigDecimal) get(SECTION_ID);
    }

    protected BigDecimal getContentTypeID() {
        return (BigDecimal) get(CONTENT_TYPE_ID);
    }

    protected BigDecimal getWorkflowTemplateID() {
        return (BigDecimal) get(WF_TEMPLATE_ID);
    }

    protected void setContentSection(ContentSection section) {
        set(SECTION_ID, section.getID());
    }

    protected void setContentType(ContentType type) {
        set(CONTENT_TYPE_ID, type.getID());
    }

    protected void setWorkflowTemplate(WorkflowTemplate template) {
        set(WF_TEMPLATE_ID, template.getID());
    }

    /**
     * Get the default associated workflow template for a content type in a
     * particular content section.
     *
     * @param section The content section
     * @param type The content type
     * @return The default workflow template, null if there is none.
     */
    public static WorkflowTemplate getWorkflowTemplate
            (final ContentSection section, final ContentType type) {
        if (Assert.isEnabled()) {
            Assert.exists(section, ContentSection.class);
            Assert.exists(type, ContentType.class);
        }

        try {
            OID oid = new OID(BASE_DATA_OBJECT_TYPE);
            oid.set(SECTION_ID, section.getID());
            oid.set(CONTENT_TYPE_ID, type.getID());

            final ContentTypeWorkflowTemplate assn =
                new ContentTypeWorkflowTemplate(oid);

            final BigDecimal id = assn.getWorkflowTemplateID();

            return new WorkflowTemplate
                (new OID(WorkflowTemplate.BASE_DATA_OBJECT_TYPE, id));
        } catch (DataObjectNotFoundException e) {
            s_log.debug("There is no default workflow template for CT " +
                    type.getName() +
                    " in section " +
                    section.getName());
            return null;
        }
    }


    /**
     * Associate a default workflow template for a content type in a
     * particular content section.  If this association already exists, the
     * previous association will be updated.
     *
     * @param section The content section
     * @param type The content type
     * @param template The workflow template
     * @return true is association is added, false if updated
     */
    public static boolean updateWorkflowTemplate(ContentSection section,
                                                 ContentType type,
                                                 WorkflowTemplate template) {
        try {
            OID oid = new OID(BASE_DATA_OBJECT_TYPE);
            oid.set(SECTION_ID, section.getID());
            oid.set(CONTENT_TYPE_ID, type.getID());

            ContentTypeWorkflowTemplate assn = new ContentTypeWorkflowTemplate(oid);
            assn.setWorkflowTemplate(template);
            assn.save();
            return false;

        } catch (DataObjectNotFoundException e) {
            // The association does not exist.
            ContentTypeWorkflowTemplate assn = new ContentTypeWorkflowTemplate();
            assn.setContentSection(section);
            assn.setContentType(type);
            assn.setWorkflowTemplate(template);
            assn.save();
            return true;
        }
    }

    /**
     * Remove the default workflow template association for a content type in
     * a particular content section.
     *
     * @param section The content section
     * @param type The content type
     * @return true if association is deleted, false otherwise
     */
    public static boolean removeWorkflowTemplate(ContentSection section,
                                                 ContentType type) {
        try {
            OID oid = new OID(BASE_DATA_OBJECT_TYPE);
            oid.set(SECTION_ID, section.getID());
            oid.set(CONTENT_TYPE_ID, type.getID());

            ContentTypeWorkflowTemplate assn = new ContentTypeWorkflowTemplate(oid);
            assn.delete();
            return true;
        } catch (DataObjectNotFoundException e) {
            // There is no default workflow template. Do nothing.
            return false;
        }
    }

}
