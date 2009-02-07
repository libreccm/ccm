/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.bebop.PageState;
import com.arsdigita.caching.CacheTable;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.CategoryItemsQuery;
import com.arsdigita.cms.CategoryTemplateCollection;
import com.arsdigita.cms.CategoryTemplateMapping;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.TemplateManager;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;

import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * Resolves items to URLs and URLs to items with category-based URLs for 
 * multiple language variants.
 *
 * Created Mon Jan 20 14:30:03 2003.
 *
 * @author <a href="mailto:sseago@redhat.com">Scott Seago</a>
 * @version $Id: CategoryItemResolverImpl.java 1795 2009-02-07 10:47:32Z pboy $
 */
public class CategoryItemResolverImpl extends MultilingualItemResolver
    implements CategoryItemResolver, TemplateResolver {

    private static final Logger s_log = Logger.getLogger
        (CategoryItemResolverImpl.class);

    public static final String CATEGORIES_PREFIX = "categories";

    private TemplateResolver m_templateResolver;

    //cache the content items
    private static CacheTable s_itemURLCache =
        new CacheTable("CategoryItemResolverImplItemURLCache");

    /**
     * Gets the category for the current request (if set by
     *    getItem(section, url, context)
     *
     * @param request The current request
     *
     * @return the Category for the current request
     */
    public Category getCategory(HttpServletRequest request)
    {
        String url = request.getRequestURI();
        ItemURLInfo itemURLInfo = (ItemURLInfo)s_itemURLCache.get(url);
        if (itemURLInfo == null)
            return null;

        return itemURLInfo.category;
    }

    /**
     * Gets the category path for the current request (if set by
     *    getItem(section, url, context)
     *
     * @param request The current request
     *
     * @return the Category path for the current request
     */
    public Category[] getCategoryPath(HttpServletRequest request)
    {
        String url = request.getRequestURI();
        ItemURLInfo itemURLInfo = (ItemURLInfo)s_itemURLCache.get(url);
        if (itemURLInfo == null)
            return null;

        return itemURLInfo.categoryPath;
    }

    /**
     * Whether the current request is an index item request (i.e. the
     * item name is not specified in the URL)
     *
     * @param request The current request
     *
     * @return Whether the current request is an index item request
     */
    public boolean isIndexRequest (HttpServletRequest request)
    {
        String url = request.getRequestURI();
        ItemURLInfo itemURLInfo = (ItemURLInfo)s_itemURLCache.get(url);
        if (itemURLInfo == null)
            return false;

        return itemURLInfo.isIndex;
    }

    public CategoryItemResolverImpl () {
        super();
        m_templateResolver = new CategoryTemplateResolver();
    }


    /**
     * Returns a content item based on section, url, and use context.
     *
     * @param section The current content section
     * @param url The section-relative URL
     * @param context The use context,
     * e.g. <code>ContentItem.LIVE</code>,
     * <code>CMSDispatcher.PREVIEW</code> or
     * <code>ContentItem.DRAFT</code>.  See {@link
     * #getCurrentContext}.
     * @return The content item, or null if no such item exists
     */
    @Override
     public ContentItem getItem(final ContentSection section,
                                String url,
                                final String context) {
         if (s_log.isDebugEnabled()) {
             s_log.debug("Resolving the item in content section " + section +
                         " at URL '" + url + "' for context " + context);
         }

         Assert.exists(section, "ContentSection section");
         Assert.exists(url, "String url");
         Assert.exists(context, "String context");
         url = stripTemplateFromURL(url);

         if ( ContentItem.DRAFT.equals(context) ||
              !url.startsWith("/" + CATEGORIES_PREFIX) ) {

             return super.getItem(section, url, context);
         }
         String categoryURL = url.substring(("/"+CATEGORIES_PREFIX).length());
         Category root = section.getRootCategory();
         Assert.exists(root);


         String file = null;
         String path = "";
         if (!"".equals(categoryURL)) {
                 int index = categoryURL.lastIndexOf("/");
                 if (index < 0) {
                     file=categoryURL;
                 }
                 file = categoryURL.substring(index+1);
                 if (file.endsWith(".jsp")) {
                     file = file.substring(0, file.length() - 4);
                 }
                 path = categoryURL.substring(0, index);
         }
         s_log.debug("Path is " + path);
         s_log.debug("File is " + file);
         // note that with the ItemResolver implementation, it's
         // possible that file is actually a category (index request)
         // rather than an item name, since trailing "/" and ".jsp"
         // are pulled off by the Servlet.


         //TO DO: Refactor this such that two calls to
         //getChildrenByURL are not needed or find a way to
         //distinguish /foo/bar.jsp from /foo/bar/ in the first place.
         // Get list of categories in the path.
         Category[] cats = null;

         cats = root.getChildrenByURL (path + "/" + file);
         if (cats == null) {
             // final element wasn't category. assume an item.
             // Really object identity? Don't think so.
             // if (path == "") {
             if (path.equals("")) {
                 cats = new Category[1];
                 cats[0] = root;
             } else {
                 cats = root.getChildrenByURL (path);
             }
         } else {
             file = null;
         }
         if (cats == null) {
             return null;
         }

         Assert.isTrue (cats.length >= 1);
         Category cat = cats[cats.length-1];
         s_log.debug ("Category is " + cat.getDisplayName());
         String lang = null;
         boolean isIndex = false;
         ContentItem item = null;
         if (file != null) {                // It's a content item.
             s_log.debug ("Look for item " + file);
             String[] nameAndLang = getNameAndLangFromURLFrag(file);
             String name = nameAndLang[0];
             lang = nameAndLang[1];

             CategoryItemsQuery items = CategoryItemsQuery.retrieve(cat,context);
             items.addEqualsFilter(CategoryItemsQuery.NAME, name);

             try {
                 if (items.next()) {
                     item = (ContentItem)DomainObjectFactory
                         .newInstance(new OID(items.getObjectType(),
                                              items.getItemID()));
                 }
             } catch (DataObjectNotFoundException ex) {
                 // returning null if item can't be instantiated
             }
             items.close();

         } else {                        // It's an index page.
             s_log.debug ("Look for index page");
             isIndex = true;
             ACSObject index = cat.getIndexObject();
             if (index != null && index instanceof ContentItem) {
                 item = (ContentItem)index;
                 if (! item.getVersion().equals(ContentItem.LIVE)) {
                     item = item.getLiveVersion ();

                 }
             } else {
                 item = null;
             }
         }
         ContentItem returnItem =  getItemFromLangAndBundle(lang,item);
         if (returnItem != null) {
             HttpServletRequest request = Web.getRequest();
             itemURLCachePut(request.getRequestURI(),
                             new ItemURLInfo(cat, cats, isIndex));
         }
         return returnItem;


     }

    private static synchronized void itemURLCachePut(String url,
                                                     ItemURLInfo info) {
        s_itemURLCache.put(url, info);
    }

    /**
     * Generates a URL for a content item.
     *
     * @param itemId The item ID
     * @param name The name of the content page
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param category the Category to use as the context for
     * generating the URL
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL(PageState state,
                                  BigDecimal itemId,
                                  String name,
                                  ContentSection section,
                                  String context,
                                  Category category){

        return generateItemURL(state, itemId, name, section,
                               context, (String) null, category);
    }

    /**
     * Generates a URL for a content item.
     *
     * @param itemId The item ID
     * @param name The name of the content page
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     * @param category the Category to use as the context for
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL (PageState state,
                                   BigDecimal itemId,
                                   String name,
                                   ContentSection section,
                                   String context,
                                   String templateContext,
                                   Category category) {

        if (s_log.isDebugEnabled()) {
            s_log.debug("Generating an item URL for item id " + itemId +
                        ", section " + section + ", and context '" +
                        context + "' with name '" + name +
                        "' in category '" + category + "'");
        }

        Assert.exists(itemId,  "BigDecimal itemId");
        Assert.exists(context, "Sring context");
        Assert.exists(section, "ContentSection section");

        if (ContentItem.DRAFT.equals(context)) {
            // No template context here.
            // CategoryItemResolver doesn't change resolution of
            // back-end URLs
            return generateDraftURL(section, itemId);
        } else if (CMSDispatcher.PREVIEW.equals(context)) {
            ContentItem item = (ContentItem)DomainObjectFactory
                .newInstance(new OID(ContentItem.BASE_DATA_OBJECT_TYPE,
                                     itemId));

            return generatePreviewURL(section, item, templateContext, category);
        } else if (ContentItem.LIVE.equals(context)) {
            ContentItem item = (ContentItem)DomainObjectFactory
                .newInstance(new OID(ContentItem.BASE_DATA_OBJECT_TYPE,
                                     itemId));

            if (Assert.isEnabled()) {
                Assert.exists(item, "item");
                Assert.isTrue(ContentItem.LIVE.equals(item.getVersion()),
                              "Generating " + ContentItem.LIVE + " " +
                              "URL; this item must be the live version");
            }

            return generateLiveURL(section, item, templateContext, category);
        } else {
            throw new IllegalArgumentException
                ("Unknown context '" + context + "'");
        }
    }

    /**
     * Generates a URL for a content item.
     *
     * @param item The item
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param category the Category to use as the context for
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL (PageState state,
                                   ContentItem item,
                                   ContentSection section,
                                   String context,
                                   Category category) {

        return generateItemURL(state, item, section, context,
                               (String) null, category);
    }

    /**
     * Generates a URL for a content item.
     *
     * @param item The item
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     * @param category the Category to use as the context for
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL (PageState state,
                                   ContentItem item,
                                   ContentSection section,
                                   String context,
                                   String templateContext,
                                   Category category) {

        if (s_log.isDebugEnabled()) {
            s_log.debug("Generating an item URL for item " + item +
                        ", section " + section + ", and context " +
                        context + " in category " + category);
        }

        Assert.exists(item, "ContentItem item");
        Assert.exists(context, "String context");

        if (section == null) {
            section = item.getContentSection();
        }

        if (ContentItem.DRAFT.equals(context)) {
            if (Assert.isEnabled()) {
                Assert.isTrue(ContentItem.DRAFT.equals(item.getVersion()),
                              "Generating " + ContentItem.DRAFT +
                              " url: item must be draft version");
            }
            // CategoryItemResolver doesn't change resolution of
            // back-end URLs
            return generateDraftURL(section, item.getID());
        } else if (CMSDispatcher.PREVIEW.equals(context)) {
            return generatePreviewURL(section, item, templateContext, category);
        } else if (ContentItem.LIVE.equals(context)) {
            if (Assert.isEnabled()) {
                Assert.isTrue(ContentItem.LIVE.equals(item.getVersion()),
                              "Generating " + ContentItem.LIVE +
                              " url: item must be live version");
            }

            return generateLiveURL(section, item, templateContext, category);
        } else {
            throw new RuntimeException("Unknown context " + context);
        }
    }


    /**
     * Generates a URL for a content item.
     *
     * @param itemId The item ID
     * @param name The name of the content page
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or
     * "admin"
     * @return The URL of the item
     * @see #getCurrentContext
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final BigDecimal itemId,
                                  final String name,
                                  final ContentSection section,
                                  final String context) {
        return generateItemURL(state, itemId, name, section, context,
                               (String) null);
    }

    /**
     * Generates a URL for a content item.
     *
     * @param itemId The item ID
     * @param name The name of the content page
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or
     * "admin"
     * @param templateContext the context for the URL, such as
     * "public"
     * @return The URL of the item
     * @see #getCurrentContext
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final BigDecimal itemId,
                                  final String name,
                                  final ContentSection section,
                                  final String context,
                                  final String templateContext) {

        return generateItemURL(state, itemId, name, section, context,
                               templateContext, (Category) null);
    }

    /**
     * Generates a URL for a content item.
     *
     * @param item The item
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or
     * "admin"
     * @return The URL of the item
     * @see #getCurrentContext
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final ContentItem item,
                                  final ContentSection section,
                                  final String context) {

        return generateItemURL(state, item, section, context, (String) null);
    }

    /**
     * Generates a URL for a content item.
     *
     * @param item The item
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or
     * "admin"
     * @param templateContext the context for the URL, such as
     * "public"
     * @return The URL of the item
     * @see #getCurrentContext
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final ContentItem item,
                                  ContentSection section,
                                  final String context,
                                  final String templateContext) {

        return generateItemURL(state, item, section, context, templateContext,
                               (Category) null);
    }

    /**
     * Generate a <em>language-independent</em> URL to the
     * <code>item</code> in the given section.<p> When a client
     * retrieves this URL, the URL is resolved to point to a specific
     * language instance of the item referenced here, i.e. this URL
     * will be resolved to a <em>language-specific</em> URL
     * internally.
     *
     * @param section the <code>ContentSection</code> that contains this item
     * @param item <code>ContentItem</code> for which a URL should be
     *  constructed.
     * @param templateContext template context; will be ignored if <code>null</code>
     * @param category the Category to use for URL generation
     *
     * @return a <em>language-independent</em> URL to the
     * <code>item</code> in the given <code>section</code>, which will
     * be presented within the given <code>templateContext</code>
     */
    protected String generateLiveURL(final ContentSection section,
                                     final ContentItem item,
                                     final String templateContext,
                                     final Category category) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Generating live URL for item " + item + " in " +
                        "section " + section);
        }

        Category urlCategory = getURLCategory(item,category);
        // Use passed-in category, if item is in it, else use default
        // category, else fall back to folder-based URL
        if (urlCategory == null) {
            return super.generateLiveURL(section, item, templateContext);
        }
        /*
         * URL = URL of section + templateContext + category path to the ContentBundle
         * which contains the item
         */
        final StringBuffer url = new StringBuffer(400);
        //url.append(section.getURL());
        url.append(section.getPath()).append("/");

        /*
         * add template context, if one is given
         */
        // This is breaking URL's...not sure why it's here. XXX
        // this is needed for multiple template support

        if ((templateContext != null && templateContext.length() > 0)) {
            url.append(TEMPLATE_CONTEXT_PREFIX).append(templateContext);
            url.append("/");
        }
        url.append(CATEGORIES_PREFIX + "/");

        // Try to retrieve the bundle.
        final ContentItem bundle = (ContentItem) item.getParent();

        /*
         * It would be nice if we had a ContentPage here, which
         * supports the getContentBundle() method, but unfortunately
         * the API sucks and there is no real distinction between mere
         * ContentItems and top-level items, so we have to use this
         * hack.  TODO: add sanity check that bundle is actually a
         * ContentItem.
         */
        if (bundle != null && bundle instanceof ContentBundle) {
            s_log.debug("Found a bundle; building its file name");

            final String fname =
                urlCategory.getQualifiedURL("/",false) +"/" + bundle.getName();

            if (s_log.isDebugEnabled()) {
                s_log.debug("Appending the bundle's file name '" +
                            fname + "'");
            }

            url.append(fname);

        } else {
            s_log.debug("No bundle found; using the item's path directly");

            url.append(urlCategory.getQualifiedURL("/",false));
            url.append("/").append(item.getName());
        }

        final String language = item.getLanguage();

        if (language == null) {
            s_log.debug("The item has no language; omitting the " +
                        "language encoding");
        } else {
            s_log.debug("Encoding the language of the item passed in, '" +
                        language + "'");

            url.append(".").append(language);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Generated live URL " + url.toString());
        }

        return url.toString();
    }

    /**
     * Generate a URL which can be used to preview the
     * <code>item</code>, using the given
     * <code>templateContext</code>.<p> Only a specific language
     * instance can be previewed, meaning there <em>no</em> language
     * negotiation is involved when a request is made to a URL that
     * has been generated by this method.
     *
     * @param section The <code>ContentSection</code> which contains
     * the <code>item</code>
     * @param item The <code>ContentItem</code> for which a URL should
     * be generated.
     * @param templateContext the context that determines which
     * template should render the item when it is previewed; ignored
     * if the argument given here is <code>null</code>
     * @param category the Category to use for URL generation
     * @return a URL which can be used to preview the given
     * <code>item</code>
     */
    protected String generatePreviewURL(ContentSection section,
                                        ContentItem item,
                                        String templateContext,
                                        Category category) {
        Assert.exists(section, "ContentSection section");
        Assert.exists(item, "ContentItem item");

        Category urlCategory = getURLCategory(item,category);
        // Use passed-in category, if item is in it, else use default
        // category, else fall back to folder-based URL
        if (urlCategory == null) {
            return super.generatePreviewURL(section, item, templateContext);
        }

        // Items are previewed individually, without language
        // negotiation.

        final StringBuffer url = new StringBuffer(400);
        url.append(section.getPath());
        url.append("/");
        url.append(CMSDispatcher.PREVIEW);
        url.append("/");
        if ((templateContext != null && templateContext.length() >= 0)) {
            url.append(TEMPLATE_CONTEXT_PREFIX).append(templateContext);
            url.append("/");
        }
        url.append(CATEGORIES_PREFIX).append("/");

        // Try to retrieve the bundle.
        ContentItem bundle = (ContentItem) item.getParent();

        /* It would be nice if we had a ContentPage here, which
         * supports the getContentBundle() method, but unfortunately
         * the API sucks and there is no real distinction between mere
         * ContentItems and top-level items, so we have to use this
         * hack.  TODO: add sanity check that bundle is actually a
         * ContentItem.
         */
        if (bundle != null && bundle instanceof ContentBundle) {
            s_log.debug("Found a bundle; using its path");

            url.append(urlCategory.getQualifiedURL("/",false));
            url.append("/").append(bundle.getName());
        } else {
            s_log.debug("No bundle found; using the item's path directly");

            url.append(urlCategory.getQualifiedURL("/",false));
            url.append("/").append(item.getName());
        }

        final String language = item.getLanguage();

        if (language == null) {
            s_log.debug("The item has no language; omitting the " +
                        "language encoding");
        } else {
            s_log.debug("Encoding the language of the item passed in, '" +
                        language + "'");

            url.append(".").append(language);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Generated preview URL " + url.toString());
        }

        return url.toString();
    }

    private Category getURLCategory(ContentItem item, Category urlCat) {
        CategoryCollection categories = item.getCategoryCollection();
        Category firstCat = null;
        Category filteredCat = null;
        while( filteredCat == null && categories.next() &&
               !(firstCat !=null && urlCat == null)) {

            Category cat = categories.getCategory();
            if (firstCat == null) {
                firstCat = cat;
            }
            if (cat.equals(urlCat)) {
                filteredCat = cat;
            }
        }
        categories.close();
        return (filteredCat != null) ? filteredCat : firstCat;
    }


    public String getTemplate(ContentSection section,
                                ContentItem item,
                                HttpServletRequest request) {
        s_log.debug("getTemplate called");
        return m_templateResolver.getTemplate(section, item, request);
    }

    public String getTemplateXSLPath(Template template) {
        return m_templateResolver.getTemplateXSLPath(template);
    }

    public String getTemplatePath(Template template) {
        return m_templateResolver.getTemplatePath(template);
    }

    /*
     * Java doesn't support multiple inheritance, so we don't inherit these two
     * methods from AbstractTemplateResolver
     */

    /* (non-Javadoc)
     * @see
     * com.arsdigita.cms.dispatcher.TemplateResolver#setTemplateContext(java.lang.String,
     * javax.servlet.http.HttpServletRequest)
     */
    public void setTemplateContext(String sTemplateContext, HttpServletRequest request) {
        if (sTemplateContext != null) {
            request.setAttribute("templateContext", sTemplateContext);
        }
    }

    /* (non-Javadoc)
     * @see
     * com.arsdigita.cms.dispatcher.TemplateResolver#getTemplateContext(javax.servlet.http.HttpServletRequest)
     */
    public String getTemplateContext(HttpServletRequest request) {
        String context = (String) request.getAttribute("templateContext");
        if (context == null) {
            context = TemplateManager.PUBLIC_CONTEXT;
        }
        return context;
    }

    protected class CategoryTemplateResolver extends DefaultTemplateResolver {

        /**
         * Returns the template associated with the item (if any)
         */
        @Override
        protected String getItemTemplate(ContentSection section,
                                         ContentItem item,
                                         HttpServletRequest request) {
            String templatePath = super.getItemTemplate(section, item, request);
            if (templatePath == null) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("No item template, looking for category template");
                }

                Category[] path = getCategoryPath(request);
                Template template = null;
                if (path != null) {
                    ContentType type = item.getContentType();
                    String context = super.getTemplateContext(request);
                    for (int i = path.length -1 ; i >= 0 ; i--) {
                        s_log.debug("Check cat " + path[i].getURL());

                        CategoryTemplateCollection templates =
                            CategoryTemplateMapping.getTemplates
                            ((Category)path[i], type, context);

                        if (templates.next()) {
                            template = templates.getTemplate();
                            s_log.debug("Found template for cat " +
                                        template.getID());
                            templates.close();
                            break;
                        }
                    }
                }
                templatePath = (template == null) ?
                    null : getTemplateFilename(template, section, item, request);
            }
            return templatePath;
        }

    }

    private static class ItemURLInfo {
        Category category;
        Category[] categoryPath;
        boolean isIndex;

        ItemURLInfo(Category category,
                    Category[] categoryPath,
                    boolean isIndex) {

            this.category = category;
            this.categoryPath = categoryPath;
            this.isIndex = isIndex;
        }

        private String encodeAsString() {
            StringBuffer sb = new StringBuffer();
            sb.append(category.getID()).append(" / ");

            for (int i = 0; i < categoryPath.length; i++) {
                sb.append(categoryPath[i].getID()).append(" ");
            }
            sb.append(isIndex);
            return sb.toString();
        }

        public boolean equals(ItemURLInfo obj) {
            return encodeAsString().equals(obj.encodeAsString());
        }

        @Override
        public int hashCode() {
            return encodeAsString().hashCode();
        }
    }
}
