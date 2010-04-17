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

import com.arsdigita.mimetypes.MimeType;

/**
 * Manages the association between publishable content items and
 * templates in a content section.
 *
 * <p>Many sites offer alternative views of the same content item
 * depending on device or browser, or on user preference.  For
 * example, a site may have "plain" and "fancy" versions of its pages.
 * The fancy versions would be the defaults, while the plain versions
 * would be appropriate for users with low-bandwidth connections,
 * older browsers, or a distaste for flashy appurtenances.  In this
 * the case the selection might be made based on a cookie.</p>
 *
 * <p>Another common example is the "printable" version of a page.
 * In this case a query variable might be more appropriate.</p>
 *
 * <p>In general, the process for resolving a template involves two
 * steps:</p>
 *
 * <ol>
 *
 *   <li>The template resolver examines specific properties of the
 *   item, the content section, and/or the request itself and selects
 *   an appropriate <em>context</em>.  A context is simply a token
 *   such as "plain" or "fancy".
 *
 *   <li>Based on the selected context, the template resolver
 *   identifies an appropriate template for the item.  This is a
 *   three-step process: (1) the resolver queries for an association
 *   between the item and a specific template for the selected
 *   context; (2) if no such association exists, the resolver queries
 *   the item's content type for a default template to use in the
 *   selected context; (3) if a default template is not found, return
 *   null (at which point the dispatcher should probably give up and
 *   return a 404 error).
 *
 * </ol>
 *
 * @author Karl Goldstein (karlg@arsdigita.com)
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Revision: #9 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: TemplateManager.java 287 2005-02-22 00:29:02Z sskracic $ 
 **/
public interface TemplateManager {

    /**
     * The default use context for templates
     */
    public static final String PUBLIC_CONTEXT = "public";

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
                            String context);

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
                               String context);

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
                            Template template, String context);


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
                               Template template, String context);

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
    public void addTemplate(ContentSection section, ContentType type, 
                            Template template, String context, 
                            boolean isDefault);


    /**
     * Designate the given template as the default template within its use context.
     * new content items of the given type will use this template by default when
     * they are rendered within the use context. Note that {@link #addTemplate} must
     * first be called to actually add the template to.
     *
     * @param section the content section where the template resides
     * @param type the content type to which the template belongs
     * @param template the template which will be made default
     * @param context the use context in which the template will be made default
     */
    public void setDefaultTemplate(ContentSection section, ContentType type, 
                                   Template template, String context);

    /**
     * Retrieve a template for the item in the given use context.
     *
     * @param item the content item
     * @param context the use context for the template, such as "public" or
     *   "abridged".
     * @return the template assigned to the item in the given context,
     *  or null if no such template is assigned
     */
    public Template getTemplate(ContentItem item, String context);

    /**
     * Retrieve all templates for the content item, along with their
     * use contexts
     *
     * @param item the content item
     * @return a {@link TemplateCollection} of all the templates assigned
     *   to the item
     * @see #getUseContexts
     */
    public TemplateCollection getTemplates(ContentItem item);

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
    public TemplateCollection getUseContexts(ContentItem item);

    /**
     * Retrieve all use context/mime type combinations along with
     * possibly null template for the item in that pair.  This
     * returns one row for each use context/mime type pair.  So, you
     * could get 4 rows if there is public/jsp, public/xsl,
     * alternate/jsp, and alternate/xsl.  This is because there can be
     * one template per context/mime-type pair
     */
    public ItemTemplateCollection getContextsWithTypes(ContentItem item);


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
     * @deprecated Use getDefaultTemplate(ContentSection, ContentType, String, MimeType)
     *
     */
    public Template getDefaultTemplate(ContentSection section, 
                                       ContentType type, String context);


    /**
     * Get the default template for the given section, type and
     * use context
     *
     * @param section the content section to which the template belongs
     * @param type the content type to which the template belongs
     * @param context the use context for the template, such as "public" or
     *   "abridged".
     * @param mimeType The mime type of the template that is desired.
     * @return the default template for the given section, type, context,
     *   and mime type or null if no such template exists
     *
     */
    public Template getDefaultTemplate(ContentSection section, 
                                       ContentType type, String context, 
                                       MimeType mimeType);

    /**
     * Get all the templates within the given section and type
     *
     * @param section the content section
     * @param type the content type
     * @return a collection of templates for the given section, type and
     *   context; an empty collection if there are no such templates
     */
    public TemplateCollection getTemplates(ContentSection section, 
                                           ContentType type);


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
                                           ContentType type, String context);
}
