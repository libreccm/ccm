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

import com.arsdigita.cms.dispatcher.ContentItemDispatcher;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.mimetypes.MimeType;


/**
 * A default implementation of {@link
 * com.arsdigita.cms.TemplateManager template manager}.
 *
 * @author Karl Goldstein (karlg@arsdigita.com)
 * @version $Revision: #12 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class DefaultTemplateManager implements TemplateManager {

    public static final String versionId = "$Id: DefaultTemplateManager.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private static final String SECTION_ID = "sectionID";
    private static final String TYPE_ID = "typeID";
    private static final String USE_CONTEXT = "useContext";
    private static final String NEW_DEFAULT_ID = "newDefaultID";
    private static final String MIME_TYPE_STRING = "mimeTypeString";
    private static final String OP_SET_DEFAULT =
        "com.arsdigita.cms.setDefaultTemplate";
    private static final String OP_SET_DEFAULT_NO_MIME_TYPE =
        "com.arsdigita.cms.setDefaultTemplateNullMimeType";

    private static final DefaultTemplateManager m_instance =
        new DefaultTemplateManager();

    /**
     * Construct a new template manager
     */
    public DefaultTemplateManager() {}

    /**
     * Assign a template to the item in the given context. Overrides
     * any previous template assignment.
     *
     * @param item the content item
     * @param template the template to be assigned
     * @param context the use context for the template, such as "public" or
     *   "abridged".
     */
    public void addTemplate(ContentItem item, Template template, 
                            String context, MimeType mimeType) {
        ItemTemplateMapping m = 
            ItemTemplateMapping.getMapping(item, context, mimeType);
        if(m == null) {
            m = new ItemTemplateMapping();
            m.setContentItem(item);
            m.setUseContext(context);
        }
        m.setTemplate(template);
        m.save();
    }

    public void addTemplate(ContentItem item, Template template, String context) {
        addTemplate(item, template, context, template.getMimeType());
    }

    /**
     * Unassign a template from the item in the given context. Do nothing if the
     * template is not assigned to the item.
     *
     * @param item the content item
     * @param template the template to be unassigned
     * @param context the use context for the template, such as "public" or
     *   "abridged".
     */
    public void removeTemplate(ContentItem item, Template template, 
                               String context) {
        ItemTemplateMapping m = 
            ItemTemplateMapping.getMapping(item, context, template);
        if (m != null) {
            m.delete();
        }
    }

    /**
     * Add a template to the given content type for the given content section.
     * The template could be used to render any item of the content type
     *
     * @param section the content section to which the template will belong
     * @param type the content type to which the template will belong
     * @param template the template to be added
     * @param context the use context for the template, such as "public" or
     *   "abridged".
     * @param isDefault if true, new content items of the given type will
     *   use this template by default when they are rendered within the specified
     *   use context
     */
    public void addTemplate (ContentSection section, ContentType type, 
                             Template template, String context, 
                             boolean isDefault) {
        SectionTemplateMapping m = new SectionTemplateMapping();
        if(template.getParent() == null) {
            template.setParent(section.getTemplatesFolder());
            template.setContentSection(section);
            template.save();
        }
        m.setContentSection(section);
        m.setContentType(type);
        m.setUseContext(context);
        m.setTemplate(template);
        m.setDefault(isDefault ? Boolean.TRUE : Boolean.FALSE);
        m.save();
    }

    /**
     * Designate the given template as the default template within its use context.
     * new content items of the given type will use this template by default when
     * they are rendered within the use context. Note that {@link #addTemplate} must
     * first be called to actually add the template to.
     *
     * @param section the content section where the template resides
     * @param type the content type to which the template belongs
     * @param template the template which will be made default
     * @param useContext the use context in which the template will be made default
     */
    public void setDefaultTemplate (ContentSection section, ContentType type,
                                    Template template, String useContext) {
        Session s = SessionManager.getSession();
        DataOperation op = null;
        if (template.getMimeType() != null) {
            op = s.retrieveDataOperation(OP_SET_DEFAULT);
            op.setParameter(MIME_TYPE_STRING, 
                            template.getMimeType().getMimeType());
        } else {
            op = s.retrieveDataOperation(OP_SET_DEFAULT_NO_MIME_TYPE);
        }
        op.setParameter(SECTION_ID, section.getID());
        op.setParameter(TYPE_ID, type.getID());
        op.setParameter(USE_CONTEXT, useContext);
        op.setParameter(NEW_DEFAULT_ID, template.getID());
        op.execute();

        //update the ContentItemDispatcher cache
        ContentItemDispatcher.cachePut(section, type, template);
    }

    /**
     * Add a template to the given content type for the given content section.
     * The template could be used to render any item of the content type.
     * If the given context for the section contains no templates,
     * the new template will be made the default.
     *
     * @param section the content section to which the template will belong
     * @param type the content type to which the template will belong
     * @param template the template to be added
     * @param context the use context for the template, such as "public" or
     *   "abridged".
     */
    public void addTemplate(ContentSection section, ContentType type,
                            Template template, String context) {

        TemplateCollection c = getTemplates(section, type, context);
        boolean hasNext = c.next();
        if(hasNext) c.close();
        addTemplate(section, type, template, context, !hasNext);
    }

    /**
     * Remove the specified template from the content type in the given
     * context. Do nothing  if no such template is associated.
     *
     * @param section the content section to which the template belongs
     * @param type the content type to which the template belongs
     * @param template the template to be removed
     * @param context the use context for the template, such as "public" or
     *   "abridged".
     */
    public void removeTemplate(ContentSection section, ContentType type,
                               Template template, String context) {
        SectionTemplateMapping m =
            SectionTemplateMapping.getMapping(section, type, template, context);
        if(m!=null)
            m.delete();
    }

    /**
     * Retrieve a template for the item in the given use context.
     *
     * @param item the content item
     * @param context the use context for the template, such as "public" or
     *   "abridged".
     * @return the template assigned to the item in the given context,
     *  or null if no such template is assigned
     */
    public Template getTemplate(ContentItem item, String context) {
        ItemTemplateMapping m = ItemTemplateMapping.getMapping(item, context);
        if(m == null) return null;
        return m.getTemplate();
    }

    /**
     * Retrieve all templates for the content item
     *
     * @param item the content item
     * @return a {@link TemplateCollection} of all the templates assigned
     *   to the item
     */
    public TemplateCollection getTemplates(ContentItem item) {
        return ItemTemplateMapping.getTemplates(item);
    }

    /**
     * Retrieve a collection of all use contexts for the item,
     * along with the templates assigned to each use context (if any).
     * Unlike {@link #getTemplates(ContentItem)}, the collection returned
     * by this method will include all use contexts for an item, even
     * those use contexts to which no template is yet assigned.
     *
     * @param item the content item
     * @return a {@link TemplateCollection} of all the templates assigned
     *   to the item
     */
    public TemplateCollection getUseContexts(ContentItem item) {
        return ItemTemplateMapping.getUseContexts(item);
    }

    /**
     * Retrieve all use context/mime type combinations along with
     * possibly null template for the item in that pair.  This
     * returns one row for each use context/mime type pair.  So, you
     * could get 4 rows if there is public/jsp, public/xsl,
     * alternate/jsp, and alternate/xsl.  This is because there can be
     * one template per context/mime-type pair
     */
    public ItemTemplateCollection getContextsWithTypes(ContentItem item) {
        return ItemTemplateMapping.getContextsWithTypes(item);
    }

    /**
     * Get the default template for the given section, type and
     * use context
     *
     * @param section the content section to which the template belongs
     * @param type the content type to which the template belongs
     * @param context the use context for the template, such as "public" or
     *   "abridged".
     * @return the default template for the given section, type and context,
     *   or null if no such template exists
     *
     */
    public Template getDefaultTemplate(ContentSection section, 
                                       ContentType type, String context) {
        return SectionTemplateMapping.getDefaultTemplate(section, type, context);
    }

    /**
     * Get the default template for the given section, type and
     * use context
     *
     * @param section the content section to which the template belongs
     * @param type the content type to which the template belongs
     * @param context the use context for the template, such as "public" or
     *   "abridged".
     * @return the default template for the given section, type and context,
     *   or null if no such template exists
     *
     */
    public Template getDefaultTemplate(ContentSection section, 
                                       ContentType type, String context, 
                                       MimeType mimeType) {
        return SectionTemplateMapping.getDefaultTemplate 
            (section, type, context, mimeType);
    }

    /**
     * Get all the templates within the given section and type
     *
     * @param section the content section
     * @param type the content type
     * @return a collection of templates for the given section, type and
     *   context; an empty collection if there are no such templates
     */
    public TemplateCollection getTemplates(ContentSection section, 
                                           ContentType type) {
        return SectionTemplateMapping.getTemplates(section, type);
    }

    /**
     * Get all the templates within the given section, type and
     * context
     *
     * @param section the content section
     * @param type the content type
     * @param context the use context
     * @return a collection of templates for the given section, type and
     *   context; an empty collection if there are no such templates
     */
    public TemplateCollection getTemplates(ContentSection section, 
                                           ContentType type, String context) {
        return SectionTemplateMapping.getTemplates(section, type, context);
    }
}
