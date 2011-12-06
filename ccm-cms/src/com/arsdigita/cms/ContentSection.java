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

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.PageResolver;
import com.arsdigita.cms.dispatcher.Resource;
import com.arsdigita.cms.dispatcher.ResourceMapping;
import com.arsdigita.cms.dispatcher.ResourceType;
import com.arsdigita.cms.dispatcher.TemplateResolver;
import com.arsdigita.cms.dispatcher.XMLGenerator;
// import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.Locale;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.StringTokenizer;

/**
 * <p>A content section represents a collection of content that is
 * managed as a unit. Content sections typically correspond to major
 * branches of the public site map. For example, a general news site
 * might have content sections for World, National, Regional, Science
 * and Technology stories. Each content section has its own production
 * and deployment environment, including the following:</p>
 *
 * <ol>
 *
 *   <li><p>It can have its own administration roles, including
 *   managers, producers, editors and designers.</p></li>
 *
 *   <li><p>It is associated with one or more specific {@link
 *   com.arsdigita.cms.ContentType content types}. For example, the
 *   "Press" section is associated with Press Releases.</p></li>
 *
 *   <li><p>It can have its own default workflows and
 *   lifecycles.</p></li>
 *
 *   <li><p>In addition to the content pages themselves, it can have
 *   any number of top-level pages for browsing, searching and any
 *   other desired purpose.</p></li>
 *
 * </ol>
 *
 * <p>By default, each content section is associated with exactly one
 * {@link com.arsdigita.kernel.PackageInstance package instance} and
 * can be mounted at exactly one node in the site map.</p>
 *
 * @author <a href="mailto:pihman@arsdigita.com">Michael Pih</a>
 * @author <a href="mailto:flattop@arsdigita.com">Jack Chung</a>
 * @version $Revision: #37 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: ContentSection.java 2209 2011-06-22 07:59:10Z pboy $
 */
public class ContentSection extends Application {

    private static final Logger s_log = Logger.getLogger(ContentSection.class);
    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.cms.ContentSection";
    public static final String PACKAGE_TYPE = "content-section";
//     public final static String STYLESHEET = "/packages/content-section/xsl/cms.xsl";
    protected static final String ID = "id";
    protected static final String PACKAGE = "package";
    protected static final String NAME = "label";
    protected static final String ROOT_FOLDER = "rootFolder";
    protected static final String TEMPLATES_FOLDER = "templatesFolder";
    protected static final String STAFF_GROUP = "staffGroup";
    protected static final String VIEWERS_GROUP = "viewersGroup";
    protected static final String DEFAULT_LOCALE = "defaultLocale";
    protected static final String LOCALES = "locales";
    protected static final String PAGE_RESOLVER_CLASS = "pageResolverClass";
    protected static final String ITEM_RESOLVER_CLASS = "itemResolverClass";
    protected static final String TEMPLATE_RESOLVER_CLASS = "templateResolverClass";
    protected static final String XML_GENERATOR_CLASS = "xmlGeneratorClass";
    protected static final String CONTENT_TYPES = "associatedContentTypes";
    protected static final String CREATABLE_CONTENT_TYPES =
            "creatableContentTypes";
    protected static final String CONTENT_TYPES_NOT_ASSOC =
            "notAssociatedContentTypes";
    protected static final String LIFECYCLE_DEFINITIONS =
            "associatedLifecycleDefinitions";
    protected static final String WF_TEMPLATES = "associatedWorkflowTemplates";
    private final static String ITEM_QUERY = "com.arsdigita.cms.ItemsInSection";
    private final static String SECTION_ID = "sectionId";
    private static final CMSConfig s_config = new CMSConfig();

    static {
        s_log.debug("Static initializer starting...");
        s_config.load();
        s_log.debug("Static initializer finished...");
    }
    // Cached properties
    PageResolver m_pageResolver = null;
    ItemResolver m_itemResolver = null;
    TemplateResolver m_templateResolver = null;
    XMLGenerator m_xmlGenerator = null;

//    public ContentSection() {
//        super(BASE_DATA_OBJECT_TYPE);
//    }
//
    /**
     * Constructor re-creating a content section object by retrieving its data
     * object by OID
     * 
     * @param oid
     * @throws DataObjectNotFoundException
     */
    public ContentSection(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor re-creating a content section object from its data object.
     *
     * @param oid
     * @throws DataObjectNotFoundException
     */
    public ContentSection(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor re-creating a content section object by retrieving its data
     * Object by ID
     *
     * @param oid
     * @throws DataObjectNotFoundException
     */
    public ContentSection(BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public static CMSConfig getConfig() {
        return s_config;
    }

    /**
     * @return the base PDL object type for this section. Child classes should
     *  override this method to return the correct value
     */
    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Fetches a property of the content section.
     * Publicized the getter for metadata forms.
     *
     * @param key The name of the attribute
     * @return The value of the attribute
     */
    @Override
    public Object get(String key) {
        return super.get(key);
    }

    /**
     * Sets a property of the content section.
     * Publicized the setter for metadata forms.
     *
     * @param key The name of the attribute
     * @param value The value of the attribute
     */
    @Override
    public void set(String key, Object value) {
        super.set(key, value);
    }

    /**
     * Sets the content section of the root folder to this section.
     */
    @Override
    protected void afterSave() {
        super.afterSave();
        // Set the root folder's content section.
        Folder root = getRootFolder();
        root.setContentSection(this);
        root.save();
    }

    /**
     * Fetch the name of the content section.
     *
     * @return The name of the content section
     */
    public String getName() {
        //return (String) get(NAME);
        return getTitle();
    }

    /**
     * Returns the title of the content section.
     *
     * @return A title
     */
    @Override
    public String getDisplayName() {
        return getName();
    }

    /**
     * Set the name of the content section
     *
     * @param name The name
     */
    public void setName(String name) {
        set(NAME, name);
    }

//  Left-over from content section as old-style application based on kernel
//  PackageType and Sitenode instead on new style web.Application.  
//  Retained for reference purpose until the packages Workspace and Service are
//  migrated to new style application as well.
//  /**
//   * Get the package instance for this content section. Each section is
//   * associated with exactly one package instance.
//   *
//   * @return the package instance associated with this content section.
//   * @post return != null
//   */
//    public PackageInstance getPackageInstance() {
//        DataObject pkg = (DataObject) get(PACKAGE);
//        Assert.exists(pkg, "package instance");
//        return new PackageInstance(pkg);
//    }
//
//  /**
//   * Set the package instance for this content section.
//   *
//   * @param pkg The package instance
//   * @pre ( pkg != null )
//   */
//    protected void setPackageInstance(PackageInstance pkg) {
//        Assert.exists(pkg, "package instance");
//        setAssociation(PACKAGE, pkg);
//    }
//
//  /**
//   * Fetch the site node on which the content section is mounted.
//   * A content section should be mounted on exactly one site node. If it
//   * is mounted on more than one site node, only the first site node will
//   * be returned.
//   *
//   * @return The site node
//   */
//    public SiteNode getSiteNode() {
//        return getPackageInstance().getDefaultMountPoint();
//    }
    /**
     * Finds the location of the content section.
     *
     * @return The URL of the site node on which the content section is mounted.
     *   This URL includes the webapp context path.
     */
    public String getURL() {
        String sURL = null;

        //see if there is a request
        final HttpServletRequest sreq = Web.getRequest();
        if (sreq != null) {
            // If we're running in the scope of a request , generate a URL using the
            // request, since it will set the scheme correctly and add
            // any missing global parameters.
            //
            // No, we do not want any parameters here, because existing CMS code
            // expects getURL() to return path only!
            sURL = URL.there(sreq, this, "/", null).toString();
        } else {
            sURL = URL.there(this, "/", null).toString();
        }

        return sURL;
    }

    /**
     * Gets the full path of the content section.
     *
     * @return returns the path of this application including the dispatcher path.
     *         The path does not end in a slash.  Does not return null
     */
    public String getFullPath() {
        return URL.getDispatcherPath() + getPath();
    }

//  Left over, see above
//    public final String getPath() {
//        final String path = getSiteNode().getURL();
//
//        if (Assert.isEnabled()) {
//            Assert.isTrue(path.endsWith("/"));
//        }
//
//        return path.substring(0, path.length() - 1);
//    }
    /**
     * Get the folder in which all draft items are contained, directly or
     * indirectly. This folder will in general contain different kinds of
     * content items and other folders. The root folder for live items can be
     * obtained by calling {@link ContentItem#getLiveVersion} on the folder
     * returned by this method.
     *
     * @post return != null
     * @return the root folder for draft items in this content section.
     */
    public Folder getRootFolder() {
        DataObject folder = (DataObject) get(ROOT_FOLDER);
        Assert.exists(folder, "root folder");
        return new Folder(folder);
    }

    /**
     * Set the root folder for this content section.
     *
     * @param root The root folder
     */
    public void setRootFolder(Folder root) {
        Assert.exists(root, "root folder");

        // Update the content section of the old and new root folders.
        // This is necessary because the content section is used to determine
        // which items are a part of the site map (folder hierarchy) of a
        // content section.
        //
        // MP: This only works if the root folder is changed and it contains no
        //     sub-items/folders. The next step is to recursively update items
        //     under the root folder when fetching "all items in a section" is
        //     implemented.
        if (!isNew()) {
            Folder oldRoot = getRootFolder();
            oldRoot.setContentSection(null);
            oldRoot.save();
        }

        setAssociation(ROOT_FOLDER, root);
    }

    /**
     * Get the folder in which all templates for this section are contained.
     *
     * @post return != null
     * @return the root folder for all templates within this section
     */
    public Folder getTemplatesFolder() {
        DataObject folder = (DataObject) get(TEMPLATES_FOLDER);
        if (folder == null) {
            return null;
        } else {
            return new Folder(folder);
        }
    }

    /**
     * Set the templates folder for this content section
     *
     * @param folder the folder where all templates for this section will
     *   be stored
     */
    public void setTemplatesFolder(Folder folder) {
        setAssociation(TEMPLATES_FOLDER, folder);
    }

    /**
     * Fetch the root category for the content section.
     * It will search for a root category matching the
     * current URL, then progressively strip off bits
     * of the url until the root site node.
     *
     * @deprecated use {@link Category#getRootForObject} instead
     *
     * @return The root category
     * @post ( return != null )
     */
    public Category getRootCategory() {
        Category category = Category.getRootForObject(this);

        Assert.exists(category, "root category");

        return category;
    }

    /**
     * Set the root category for this content section.
     *
     * @deprecated use {@link Category#setRootForObject} instead
     *
     * @param root The root category
     * @pre  ( root != null )
     */
    public void setRootCategory(Category root) {
        Assert.exists(root, "root category");
        Category.setRootForObject(this, root);
    }

    /**
     * Fetch the staff group for this content section.
     *
     * @return The staff group
     * @post ( return != null )
     */
    public Group getStaffGroup() {
        DataObject group = (DataObject) get(STAFF_GROUP);
        Assert.exists(group, "staff group");
        return new Group(group);
    }

    /**
     * Set the staff group for this content section.
     *
     * @param group The staff group
     * @pre ( group != null )
     */
    public void setStaffGroup(Group group) {
        Assert.exists(group, "staff group");
        setAssociation(STAFF_GROUP, group);
    }

    /**
     * Fetch the viewers group for this content section.
     *
     * @return The viewers group
     * @post ( return != null )
     */
    public Group getViewersGroup() {
        DataObject group = (DataObject) get(VIEWERS_GROUP);
        Assert.exists(group, "viewers group");
        return new Group(group);
    }

    /**
     * Set the viewers group for this content section.
     *
     * @param group The viewers group
     * @pre ( group != null )
     */
    public void setViewersGroup(Group group) {
        Assert.exists(group, "viewers group");
        setAssociation(VIEWERS_GROUP, group);
    }

    /**
     * Get the class name of the {link @com.arsdigita.cms.dispatcher.PageResolver}.
     *
     * @return The class name
     * @post ( return != null )
     */
    public String getPageResolverClassName() {
        String prc = (String) get(PAGE_RESOLVER_CLASS);
        Assert.exists(prc, "Page Resolver class");
        return prc;
    }

    /**
     * Get the page resolver for this content section. The page resolver is
     * used to resolve URLs to
     * {@link com.arsdigita.cms.dispatcher.ResourceHandler server resources}.
     *
     * @return The page resolver
     */
    public PageResolver getPageResolver() {

        if (m_pageResolver == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("The page resolver hasn't been loaded yet; "
                        + "loading it now");
            }

            try {
                final Class prc = Class.forName(getPageResolverClassName());
                m_pageResolver = (PageResolver) prc.newInstance();
                m_pageResolver.setContentSectionID(getID());
            } catch (ClassNotFoundException cnfe) {
                throw new UncheckedWrapperException(cnfe);
            } catch (InstantiationException ie) {
                throw new UncheckedWrapperException(ie);
            } catch (IllegalAccessException iae) {
                throw new UncheckedWrapperException(iae);
            }
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Returning page resolver " + m_pageResolver);
        }

        return m_pageResolver;
    }

    /**
     * Set the page resolver for this content section.
     *
     * @param className The class name
     */
    public void setPageResolverClassName(String className) {
        set(PAGE_RESOLVER_CLASS, className);
        m_pageResolver = null;
    }

    /**
     * Get the class name of the {link @com.arsdigita.cms.dispatcher.ItemResolver}.
     *
     * @return The class name
     * @post ( return != null )
     */
    public String getItemResolverClassName() {
        String irc = (String) get(ITEM_RESOLVER_CLASS);
        Assert.exists(irc, "Content Item Resolver class");
        s_log.debug("Content Item Resolver Class is " + irc);
        return irc;
    }

    /**
     * Get the item resolver for this content section. The item
     * resolver is used to resolve URLs to {@link
     * com.arsdigita.cms.ContentItem content items}.
     *
     * @return The item resolver
     */
    public ItemResolver getItemResolver() {
        if (m_itemResolver == null) {
            try {
                final Class irc = Class.forName(getItemResolverClassName());
                m_itemResolver = (ItemResolver) irc.newInstance();
            } catch (ClassNotFoundException cnfe) {
                throw new UncheckedWrapperException(cnfe);
            } catch (InstantiationException ie) {
                throw new UncheckedWrapperException(ie);
            } catch (IllegalAccessException iae) {
                throw new UncheckedWrapperException(iae);
            }
        }

        return m_itemResolver;
    }

    /**
     * Set the item resolver for this content section.
     *
     * @param className The class name
     */
    public void setItemResolverClass(String className) {
        set(ITEM_RESOLVER_CLASS, className);
        m_itemResolver = null;
    }

    /**
     * Get the class name of the {link @com.arsdigita.cms.dispatcher.TemplateResolver}.
     *
     * @return The class name
     * @post ( return != null )
     */
    public String getTemplateResolverClassName() {
        String trc = (String) get(TEMPLATE_RESOLVER_CLASS);
        Assert.exists(trc, "Template Resolver class");
        return trc;
    }

    /**
     * Returns the template resolver for this content section.
     *
     * @return The name of a class that implements
     * <code>com.arsdigita.cms.dispatcher.TemplateResolver</code>.
     */
    public TemplateResolver getTemplateResolver() {
        if (m_templateResolver == null) {
            try {
                Class trc = Class.forName(getTemplateResolverClassName());
                m_templateResolver = (TemplateResolver) trc.newInstance();
            } catch (ClassNotFoundException cnfe) {
                throw new UncheckedWrapperException(cnfe);
            } catch (InstantiationException ie) {
                throw new UncheckedWrapperException(ie);
            } catch (IllegalAccessException iae) {
                throw new UncheckedWrapperException(iae);
            }
        }

        return m_templateResolver;
    }

    /**
     * Sets the template resolver for this content section.
     *
     * @param className The name of a class that implements
     * <code>com.arsdigita.cms.dispatcher.TemplateResolver</code>.
     **/
    public void setTemplateResolverClass(String className) {
        set(TEMPLATE_RESOLVER_CLASS, className);
        m_templateResolver = null;
    }

    /**
     * Get the class name of the {link @com.arsdigita.cms.dispatcher.XMLGenerator}.
     *
     * @return The class name
     */
    public String getXMLGeneratorClassName() {
        String xgc = (String) get(XML_GENERATOR_CLASS);
        Assert.exists(xgc, "XML Generator class");
        return xgc;
    }

    /**
     * Get the XML generator for this content section. The XML generator is
     * used to transform content items into a DOM element.
     *
     * @return The XML generator
     */
    public XMLGenerator getXMLGenerator() {
        if (m_xmlGenerator == null) {
            try {
                Class xgc = Class.forName(getXMLGeneratorClassName());
                m_xmlGenerator = (XMLGenerator) xgc.newInstance();
            } catch (ClassNotFoundException cnfe) {
                throw new UncheckedWrapperException(cnfe);
            } catch (InstantiationException ie) {
                throw new UncheckedWrapperException(ie);
            } catch (IllegalAccessException iae) {
                throw new UncheckedWrapperException(iae);
            }
        }

        return m_xmlGenerator;
    }

    /**
     * Set the XML generator for this content section.
     *
     * @param className The class name
     */
    public void setXMLGeneratorClass(String className) {
        set(XML_GENERATOR_CLASS, className);
        m_xmlGenerator = null;
    }

    //////////////////////////////
    //
    // Globalization.
    //
    /**
     * Gets the default Locale. This is used for translating or creating
     * content if no locale is specified.
     *
     * @return The default locale for a content section, possibly null
     */
    public Locale getDefaultLocale() {
        DataObject obj = (DataObject) get(DEFAULT_LOCALE);
        if (obj == null) {
            return null;
        } else {
            return new Locale(obj);
        }
    }

    /**
     * Sets the default locale for a content section. Only a locale that is
     * registered to this section can be set as the default locale for this
     * section. If no locale is passed in, unset the default locale, if it
     * exists.
     *
     * @param locale The locale. If null, unset the default locale.
     * @pre ( locale in getLocales() || locale == null )
     */
    public void setDefaultLocale(Locale locale) {
        setAssociation(DEFAULT_LOCALE, locale);
    }

    /**
     * Returns a collection of Locales associated with this content section.
     * Each locale represents options for translating content items in this
     * section.
     *
     * @return A collection of locales registered to this content section
     * @post ( return != null )
     */
    public SectionLocaleCollection getLocales() {
        DataAssociation da = (DataAssociation) get(LOCALES);
        return new SectionLocaleCollection(da);
    }

    /**
     * Register a locale with this content section.
     *
     * @param locale The locale
     * @pre ( locale != null )
     */
    public void addLocale(Locale locale) {
        addLocale(locale, false);
    }

    /**
     * Register a locale with this content section. The locale may be
     * set as the default locale for this content section.
     *
     * @param locale The locale
     * @param isDefault A flag, if true, which indicates that this locale
     *    should be the default locale for this content section.
     * @pre ( locale != null )
     */
    public void addLocale(Locale locale, boolean isDefault) {
        DataAssociation da = (DataAssociation) get(LOCALES);
        locale.addToAssociation(da);

        if (isDefault) {
            setDefaultLocale(locale);
        }
    }

    /**
     * Unregister a locale from the content section.
     *
     * @param locale
     * @pre ( locale != null )
     */
    public void removeLocale(Locale locale) {
        DataAssociation da = (DataAssociation) get(LOCALES);
        locale.removeFromAssociation(da);
    }

    //////////////////////////////
    //
    // Content types.
    //
    /**
     * Get all user-defined content types registered to the content section.
     *
     * @return A ContentTypeCollection of registered content types
     */
    public ContentTypeCollection getContentTypes() {
        return getContentTypes(false);
    }

    public ContentTypeCollection getContentTypes(boolean hidden) {
        DataAssociation da = (DataAssociation) get(CONTENT_TYPES);
        ContentTypeCollection types = new ContentTypeCollection(da);
        // Filter out internal content types.
        types.addFilter("mode != 'I'");
        if (!hidden) {
            types.addFilter("mode != 'H'");
        }
        return types;
    }

    public ContentTypeCollection getDescendantsOfContentType(ContentType ct) {
        ContentTypeCollection ctc = getContentTypes();

        // The Filter Factory
        FilterFactory ff = ctc.getFilterFactory();

        // Create an or-filter
        CompoundFilter or = ff.or();

        // The content type must be either of the requested type
        or.addFilter(ff.equals(ContentType.ID, ct.getID()));

        // Or must be a descendant of the requested type
        try {
            StringTokenizer strTok = new StringTokenizer(ct.getDescendants(), "/");
            while (strTok.hasMoreElements()) {
                or.addFilter(ff.equals(ContentType.ID, (String) strTok.nextElement()));
            }
        } catch (Exception ex) {
            // WTF? The selected content type does not exist in the table???
            s_log.error("WTF? The selected content type does not exist in the table???");
        }

        ctc.addFilter(or);
        return ctc;
    }

    /**
     * Get all user-defined content types registered to the content section
     * that can be created.
     *
     * @return A ContentTypeCollection of content types that are
     * 1) registered to the content section
     * 2) user-defined
     * 3) possess a non-empty creation component in its AuthoringKit.
     */
    public ContentTypeCollection getCreatableContentTypes() {
        return getCreatableContentTypes(false);
    }

    public ContentTypeCollection getCreatableContentTypes(boolean hidden) {
        DataAssociation da = (DataAssociation) get(CREATABLE_CONTENT_TYPES);
        ContentTypeCollection types = new ContentTypeCollection(da);
        // Filter out internal content types.
        types.addFilter("mode != 'I'");
        if (!hidden) {
            types.addFilter("mode != 'H'");
        }
        return types;
    }

    /**
     * Register a content type to the content section. If the content type is
     * already registered to the content section, nothing is done.
     *
     * @param type The content type
     */
    public void addContentType(ContentType type) {
        if (!hasContentType(type)) {
            DataAssociation da = (DataAssociation) get(CONTENT_TYPES);
            type.addToAssociation(da);
        }
    }

    /**
     * Unregister a content type from the content section.
     *
     * @param type The content type
     */
    public void removeContentType(ContentType type) {
        DataAssociation da = (DataAssociation) get(CONTENT_TYPES);
        type.removeFromAssociation(da);
    }

    /**
     * Return <code>true</code> if the content type is registered with this
     * content section.
     *
     * @param type the type to cjeck for
     * @return <code>true</code> if the content type is registered with this
     * content section.
     */
    private boolean hasContentType(ContentType type) {
        DataAssociation da = (DataAssociation) get(CONTENT_TYPES);
        DataAssociationCursor cursor = da.cursor();
        cursor.addEqualsFilter(ID, type.getID());
        return (cursor.size() > 0);
    }

    /**
     * Return the user-defined content types that are not registered to
     * this content section.
     *
     * @return A ContentTypeCollection of content types not registered
     * to the content section
     */
    public ContentTypeCollection getNotAssociatedContentTypes() {
        DataAssociation da = (DataAssociation) get(CONTENT_TYPES_NOT_ASSOC);
        ContentTypeCollection types = new ContentTypeCollection(da);
        // Filter out internal content types.
        types.addFilter("mode != 'I'");
        return types;
    }

    //////////////////////////////
    //
    // Lifecycle definitions.
    //
    /**
     * Get all lifecycle definitions registered to the content section.
     *
     * @return a LifecycleDefinitionCollection or registered
     * lifecycle definition.
     */
    public LifecycleDefinitionCollection getLifecycleDefinitions() {
        return new LifecycleDefinitionCollection(getLifecycleDefinitionsAssociation());
    }

    /**
     * Register a lifecycle definition to the content section.
     *
     * @param definition The lifecycle definition
     */
    public void addLifecycleDefinition(LifecycleDefinition definition) {
        definition.addToAssociation(getLifecycleDefinitionsAssociation());
    }

    /**
     * Unregister a lifecycle definition from the content section.
     *
     * @param definition The lifecycle definition
     */
    public void removeLifecycleDefinition(LifecycleDefinition definition) {
        definition.removeFromAssociation(getLifecycleDefinitionsAssociation());
    }

    private DataAssociation getLifecycleDefinitionsAssociation() {
        return (DataAssociation) get(LIFECYCLE_DEFINITIONS);
    }

    //////////////////////////////
    //
    // Workflow templates.
    //
    /**
     * Get all workflow templates registered to the content section.
     *
     * @return a TaskCollection of workflow templates.
     */
    public TaskCollection getWorkflowTemplates() {
        TaskCollection tasks = new TaskCollection(getWorkflowTemplatesAssociation());
        tasks.addOrder("label asc");
        return tasks;
    }

    /**
     * Register a workflow template to the content section.
     *
     * @param template The workflow template
     */
    public void addWorkflowTemplate(WorkflowTemplate template) {
        template.addToAssociation(getWorkflowTemplatesAssociation());
    }

    /**
     * Unregister a workflow template from the content section.
     *
     * @param template The workflow template
     */
    public void removeWorkflowTemplate(WorkflowTemplate template) {
        template.removeFromAssociation(getWorkflowTemplatesAssociation());
    }

    private DataAssociation getWorkflowTemplatesAssociation() {
        return (DataAssociation) get(WF_TEMPLATES);
    }

    //////////////////////////////
    //
    // Finding a content section.
    //
    /**
     * Looks up the section given the SiteNode.
     *
     * @param node The site node
     * @return The content section
     * @pre ( node != null )
     * @post ( return != null )
     */
    public static ContentSection getSectionFromNode(SiteNode node)
            throws DataObjectNotFoundException {

        return (ContentSection) retrieveApplicationForSiteNode(node);

//        BigDecimal sectionId = null;
//
//        PackageInstance pkg = node.getPackageInstance();
//        if ( pkg == null ) {
//            throw new DataObjectNotFoundException(
//                  "No package instance for node_id=" + node.getID().toString());
//        }
//
//        return getSectionFromPackage(pkg);
    }

//  /**
//   * Looks up the section given the PackageInstance.
//   *
//   * @param pkg The package instance
//   * @return The content section ID
//   * @pre ( pkg != null )
//   * @post ( return != null )
//   */
//    public static ContentSection getSectionFromPackage(PackageInstance pkg)
//        throws DataObjectNotFoundException {
//
//        ContentSection section = null;
//
//        final String query = "com.arsdigita.cms.getSectionFromPackage";
//        DataQuery dq = SessionManager.getSession().retrieveQuery(query);
//        dq.setParameter("packageId", pkg.getID());
//        if ( dq.next() ) {
//            DataObject dobj = (DataObject) dq.get("section");
//            if ( dobj != null ) {
//                section = (ContentSection) DomainObjectFactory.newInstance(dobj);
//            }
//            dq.close();
//        } else {
//            throw new DataObjectNotFoundException(
//                      "Failed to fetch a content section for the current package " +
//                      "instance. [package_id =" + pkg.getID().toString() + "]");
//        }
//        return section;
//    }
    /**
     * Get the content section for an item.
     *
     * @deprecated use {@link ContentItem#getContentSection} instead
     *
     * @pre item != null
     * @post return != null
     * @param item A content item
     * @return The content section of an item
     */
    public static ContentSection getContentSection(ContentItem item)
            throws DataObjectNotFoundException {

        return item.getContentSection();
    }

    /**
     * Get the content section for a folder.
     *
     * @deprecated use {@link ContentItem#getContentSection} instead
     * @pre item != null
     * @post return != null
     * @param folder A content folder
     * @return The content section of the folder
     */
    public static ContentSection getContentSection(Folder folder)
            throws DataObjectNotFoundException {

        return folder.getContentSection();
    }

    /**
     * Retrieve all content sections in the system.
     *
     * @return A collection of content sections
     */
    public static ContentSectionCollection getAllSections() {
        DataCollection da = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        return new ContentSectionCollection(da);
    }

    /**
     * Creates a content section of the given name using default values and
     * returns it.
     *
     * @param name Name of the content section
     * @return ContentSection
     */
    public static ContentSection create(final String name) {

        Folder folder = createRootFolder(name);
        Category category = createRootCategory(name);
        Group staff = createStaffGroup(name);

        // Some default classes for a content section.
        String prc = "com.arsdigita.cms.dispatcher.SimplePageResolver";
        String irc = "com.arsdigita.cms.dispatcher.MultilingualItemResolver";
        String xgc = "com.arsdigita.cms.dispatcher.SimpleXMLGenerator";
        String trc = "com.arsdigita.cms.dispatcher.DefaultTemplateResolver";

        ContentSection section = ContentSection.create(name,
                                                       folder,
                                                       category,
                                                       staff,
                                                       prc,
                                                       irc,
                                                       xgc,
                                                       trc);

        // Set the default context on the root folder to
        // the content section
        PermissionService.setContext(folder.getOID(), section.getOID());
        createDefaultResources(section);

        return section;
    }

    /**
     * Create a new content section. This method is called automatically when a
     * CMS package instance is created.
     *
     * @param name The package instance
     * @param folder The root folder
     * @param category The root category
     * @param staff The staff group
     * @param prc The page resolver class name
     * @param irc The item resolver class name
     * @param xgc The XML generator class name
     * @return The new content section
     */
    public static ContentSection create(String name,
                                        Folder folder,
                                        Category category,
                                        Group staff,
                                        String prc,
                                        String irc,
                                        String xgc) {
        /** Set default as template resolver class name */
        String trc = "com.arsdigita.cms.dispatcher.DefaultTemplateResolver";
        return ContentSection.create(
                                     name,
                                     folder,
                                     category,
                                     staff,
                                     prc,
                                     irc,
                                     xgc,
                                     trc);
    }

    /**
     * Create a new content section. This method is called automatically when a
     * CMS package instance is created.
     *
     * @param name The package instance
     * @param folder The root folder
     * @param category The root category
     * @param staff The staff group
     * @param prc The page resolver class name
     * @param irc The item resolver class name
     * @param xgc The XML generator class name
     * @param trc The template resolver class name
     * @return The new content section
     */
    public static ContentSection create(String name,
                                        Folder folder,
                                        Category category,
                                        Group staff,
                                        String prc,
                                        String irc,
                                        String xgc,
                                        String trc) {

        // This could be moved out of here and into the Installer
        // (passing it into a modified version of create)
        Group viewers = new Group();
        viewers.setName(name + " Viewers");
        viewers.save();

        // Create template root folder.
        Folder templates = new Folder();
        templates.setName("templates");
        templates.setLabel((String) GlobalizationUtil.globalize(
                "cms.templates").localize());
        templates.save();

        //create and initialize the content section application
        ContentSection section = (ContentSection) Application
                                 .createApplication(BASE_DATA_OBJECT_TYPE
                                                    , name, name, null);
        section.initialize(name,
                           folder,
                           category,
                           staff,
                           prc,
                           irc,
                           xgc,
                           trc,
                           templates,
                           viewers);

        return section;
    }

    /**
     * Creates and maps default resources to the content section.
     *
     * @param section The content section
     *
     * MP: create resource types.
     * MP: use the resources API.
     * MP: only create resources once.
     */
    protected static void createDefaultResources(ContentSection section) {

        // XML resources
        ResourceType rt = ResourceType.findResourceType("xml");
        Resource r = rt.createInstance("com.arsdigita.cms.ui.ContentSectionPage");
        r.save();
        ResourceMapping rm = r.createInstance(section, "admin");
        rm.save();
        rm = r.createInstance(section, "admin/index");
        rm.save();

        // XXX What's up with this?  The class doesn't exist anymore.
        //r = rt.createInstance("com.arsdigita.cms.user.ItemIndexPage");
        //r.save();
        //rm = r.createInstance(section, "index");
        //rm.save();

        r = rt.createInstance("com.arsdigita.cms.ui.ContentItemPage");
        r.save();
        rm = r.createInstance(section, "admin/item");
        rm.save();

    }

    /**
     * Creates the root folder for a content section.
     *
     * @param name The name of the content section
     * @return The root folder
     */
    protected static Folder createRootFolder(String name) {
        Folder root = new Folder();
        root.setName("/");
        root.setLabel((String) GlobalizationUtil.globalize(
                "cms.installer.root_folder").localize());
        root.save();
        return root;
    }

    /**
     * Creates the root category for a content section.
     *
     * @param name The name of the content section
     * @return The root category
     */
    protected static Category createRootCategory(String name) {
        Category root = new Category("/", "Root Category");
        root.save();
        return root;
    }

    /**
     * Creates default staff group and associated default roles for a
     * content section.
     *
     * @param name The name of the content section
     * @return The staff group
     */
    private static Group createStaffGroup(String name) {
        Group staff = new Group();
        staff.setName(name + " Administration");
        staff.save();
        return staff;
    }

    /**
     * Initialize a newly created content section.
     *
     * @param name The package instance name
     * @param folder The root folder
     * @param category The root category
     * @param staff The staff group
     * @param prc The page resolver class name
     * @param irc The item resolver class name
     * @param xgc The XML generator class name
     * @param trc The template resolver class name
     * @return The new content section
     */
    public ContentSection initialize(
            String name,
            Folder folder,
            Category category,
            Group staff,
            String prc,
            String irc,
            String xgc,
            String trc,
            Folder templates,
            Group viewers) {

        setName(name);
        //setPackageInstance(pkg);
        setRootFolder(folder);
        setRootCategory(category);
        setStaffGroup(staff);
        setPageResolverClassName(prc);
        setItemResolverClass(irc);
        setXMLGeneratorClass(xgc);
        setTemplateResolverClass(trc);
        setTemplatesFolder(templates);
        setViewersGroup(viewers);
        save();

        return this;
    }

    /**
     * Fetches the child items of this section. An item is defined to be "in"
     * a content section if it can be found directly in the folder hierarchy
     * (site map) of the content section. The returned collection
     * provides methods to filter by various criteria, for example by name or
     * by whether items are folders or not.
     */
    public Folder.ItemCollection getItems() {
        DataQuery dq = SessionManager.getSession().retrieveQuery(ITEM_QUERY);
        dq.setParameter(SECTION_ID, getID());
        return new Folder.ItemCollection(dq);
    }

    @Override
    public String getServletPath() {
        return URL.SERVLET_DIR + "/content-section";
    }

}
