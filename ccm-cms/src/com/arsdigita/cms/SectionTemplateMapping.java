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
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.mimetypes.MimeType;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * Represents a mapping from (content section + content type) to a
 * template.  This class is is package scope since it is part of the
 * internal templating implementation.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: SectionTemplateMapping.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SectionTemplateMapping extends TemplateMapping {

    public static final String versionId = "$Id: SectionTemplateMapping.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.SectionTemplateMapping";

    public static final String SECTION      = "section";
    public static final String CONTENT_TYPE = "contentType";

    private static final Logger logger = Logger.getLogger(SectionTemplateMapping.class);

    // Default constructor
    public SectionTemplateMapping() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    // OID constructor
    public SectionTemplateMapping(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    // ID constructor
    public SectionTemplateMapping(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    // DataObject constructor
    public SectionTemplateMapping(DataObject obj) {
        super(obj);
    }

    // Subtype constructor
    public SectionTemplateMapping(String type) {
        super(type);

        setDefault(Boolean.FALSE);
        setUseContext(TemplateManager.PUBLIC_CONTEXT);
    }

    public final ContentSection getContentSection() {
        return (ContentSection) DomainObjectFactory.newInstance
            ((DataObject)get(SECTION));
    }

    public final void setContentSection(ContentSection sec) {
        Assert.exists(sec);
        setAssociation(SECTION, sec);
    }

    public ACSObject getParent() {
        return getContentType();
    }

    public final ContentType getContentType() {
        return (ContentType) DomainObjectFactory.newInstance
            ((DataObject) get(CONTENT_TYPE));
    }

    public final void setContentType(ContentType t) {
        Assert.exists(t);
        setAssociation(CONTENT_TYPE, t);
    }

    /**
     * Determine if the template will be the default within its
     * context
     */
    public Boolean isDefault() {
        return (Boolean)get(IS_DEFAULT);
    }

    /**
     * Set whether the template will be the default within its
     * context
     */
    public void setDefault(Boolean b) {
        Assert.exists(b);
        set(IS_DEFAULT, b);
    }

    /**
     * Load the specified mapping; return null if no such mapping
     * exists
     * @deprecated use getMapping(ContentSection section, ContentType type, Template template, String useContext, MimeType mimeType)
     */
    protected static SectionTemplateMapping getMapping(ContentSection section, ContentType type,
                                                       Template template, String useContext
                                                       ) {
        return getMapping(section, type, template, useContext, 
                          MimeType.loadMimeType(Template.JSP_MIME_TYPE));
    }

    /**
     * Load the specified mapping; return null if no such mapping
     * exists
     */
    protected static SectionTemplateMapping getMapping
        (ContentSection section, ContentType type,
         Template template, String useContext, MimeType mimeType) {
        SectionTemplateCollection c = getTemplates(section, type, useContext);
        c.addEqualsFilter(TEMPLATE + "." + ACSObject.ID, template.getID());
        String mimeString = null;
        if (mimeType != null) {
            mimeString = mimeType.getMimeType();
        }
        if(!c.next()) return null;
        SectionTemplateMapping m = (SectionTemplateMapping)c.getDomainObject();
        Assert.isTrue(!c.next());
        c.close();
        return m;
    }

    /**
     * Get the default template for the given use context and mime type
     */
    protected static Template getDefaultTemplate 
        (ContentSection section, ContentType type, String useContext, 
         MimeType mimeType) {
        SectionTemplateCollection c = 
            getDefaultTemplates(section, type, useContext);
        String mimeString = null;
        if (mimeType != null) {
            mimeString = mimeType.getMimeType();
        }
        c.addEqualsFilter(TemplateCollection.TEMPLATE + "." + Template.MIME_TYPE + "." + MimeType.MIME_TYPE, mimeString);
        if(!c.next()) return null;
        SectionTemplateMapping m = (SectionTemplateMapping)c.getDomainObject();
        // FIXME: There HAS to be a better way to enforce uniqueness here...
        Assert.isTrue(!c.next());
        c.close();
        return m.getTemplate();
    }

    /**
     * Get the default template for the given use context
     * @deprecated use getDefaultTemplates with the MimeType or use the
     * collection since there can be one default per mime type per context
     */
    protected static Template getDefaultTemplate(ContentSection section, 
                                                 ContentType type, 
                                                 String useContext) {
        SectionTemplateCollection c = getTemplates(section, type, useContext);
        c.addEqualsFilter(IS_DEFAULT, new Boolean(true));
        if(!c.next()) return null;
        SectionTemplateMapping m = (SectionTemplateMapping)c.getDomainObject();
        // FIXME: There HAS to be a better way to enforce uniqueness here...
        Assert.isTrue(!c.next());
        c.close();
        return m.getTemplate();
    }

    /**
     * Get the default template for the given use context
     */
    protected static SectionTemplateCollection getDefaultTemplates 
        (ContentSection section, ContentType type, String useContext) {
        SectionTemplateCollection c = getTemplates(section, type, useContext);
        c.addEqualsFilter(IS_DEFAULT, new Boolean(true));
        return c;
    }

    /**
     * Retrieve all templates for the given content, type, and use
     * context
     */
    protected static SectionTemplateCollection getTemplates
        (ContentSection section, ContentType type, String useContext) {
        SectionTemplateCollection c = getTemplates(section, type);
        c.addEqualsFilter(USE_CONTEXT, useContext);
        return c;
    }

    /**
     * Retrieve all templates for the given content section and type,
     * along with their use context
     */
    protected static SectionTemplateCollection getTemplates
        (ContentSection section, ContentType type) {
        SectionTemplateCollection c = getTemplates(section);
        c.addEqualsFilter(CONTENT_TYPE + "." + ACSObject.ID, type.getID());
        return c;
    }

    /**
     * Retrieve all templates for the given content section, and all
     * types within it, along with their use context
     */
    protected static SectionTemplateCollection getTemplates(ContentSection section) {
        DataCollection da = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        SectionTemplateCollection c = new SectionTemplateCollection(da);
        c.addEqualsFilter(SECTION + "." + ACSObject.ID, section.getID());
        c.addOrder(CONTENT_TYPE + "." + ContentType.LABEL);
        c.addOrder(USE_CONTEXT);
        c.addOrder(TEMPLATE + "." + ContentItem.NAME);
        return c;
    }

}
