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
package com.arsdigita.cms;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.util.LanguageUtil;
import com.arsdigita.domain.AbstractDomainObjectObserver;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectObserver;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 * A bundle of content items of different languages.  A bundle ties
 * the various language instances of an item together and provides
 * methods to access them.
 *
 * @author Shashin Shinde
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ContentBundle.java 2273 2012-01-20 00:21:50Z pboy $
 */
public class ContentBundle extends ContentItem {

    private static final Logger s_log = Logger.getLogger(ContentBundle.class);
    private static DomainObjectObserver s_instancesObserver =
            new AbstractDomainObjectObserver() {

                @Override
                public void add(DomainObject dom, String name,
                        DataObject dobj) {
                    if (INSTANCES.equals(name)) {
                        if (dobj != null) {
                            PermissionService.setContext(dobj.getOID(), dom.getOID());
                        }
                    }
                }
            };
    /**
     * The base data object type of a bundle
     */
    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.cms.ContentBundle";
    /**
     * The primary instances association
     */
    public static final String INSTANCES = "instances";
    /**
     * The association to AtoZ aliases
     */
    public static final String ATOZ_ALIASING_PROVIDERS = "atozAliasingProviders";
    /**
     * The default language property
     */
    public static final String DEFAULT_LANGUAGE = "defaultLanguage";
    private boolean m_wasNew = false;

    /**
     * Returns the data object type for this bundle.
     */
    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Creates a new bundle.
     *
     * @param primary The primary language instance of this bundle
     */
    public ContentBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        super.setName(primary.getName());
    }

    /**
     * Retrieves a bundle.
     *
     * @param oid the <code>OID</code> of the bundle to retrieve
     */
    public ContentBundle(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Retrieves a bundle.
     *
     * @param id the <code>BigDecimal</code> id of the bundle to
     * retrieve
     */
    public ContentBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Retrieves or creates a bundle using the <code>DataObject</code>
     * argument.
     *
     * @param object the <code>DataObject</code> to use in creating or
     * retrieving the bundle
     */
    public ContentBundle(final DataObject object) {
        super(object);
    }

    /**
     * Creates a bundle.
     *
     * @param type the <code>String</code> data object type with which
     * to create a new bundle
     */
    public ContentBundle(final String type) {
        super(type);
    }

    @Override
    protected ContentItem makeCopy() {
        final ContentBundle newItem = (ContentBundle) super.makeCopy();

        final WorkflowTemplate template =
                ContentTypeWorkflowTemplate.getWorkflowTemplate(newItem.getContentSection(), newItem.getContentType());

        if (template != null) {
            s_log.debug("Setting up new workflow template");
            ItemCollection instances = getInstances();
            while (instances.next()) {
                ContentItem instance = instances.getContentItem();
                s_log.debug("Item id is: " + instance.getID());
                final Workflow workflow = template.instantiateNewWorkflow();
                workflow.setObjectID(instance.getID());
                workflow.start(Web.getContext().getUser());
                workflow.save();

            }
        }

        return newItem;
    }

    /**
     * Gets the default language of the bundle.
     */
    public final String getDefaultLanguage() {
        return (String) get(DEFAULT_LANGUAGE);
    }

    /**
     * Sets the default language of the bundle.
     */
    public final void setDefaultLanguage(final String language) {
        if (Assert.isEnabled()) {
            Assert.exists(language, String.class);
            Assert.isTrue(language.length() == 2,
                    language + " is not an ISO639 language code");
        }

        set(DEFAULT_LANGUAGE, language);
    }

    /**
     * Adds a language instance to this bundle.  This method will fail
     * if the bundle already contains a different instance for the
     * same language.
     *
     * Note that in order to set the primary instance you must call
     * this method and {@link #setDefaultLanguage(String)} as well.
     *
     * @param instance the new language instance
     * @see #setDefaultLanguage(String)
     * @pre instance != null
     * @post this.equals(instance.getParent())
     */
    public void addInstance(final ContentItem instance) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding " + instance + " to bundle " + this);
        }

        if (Assert.isEnabled()) {
            Assert.exists(instance, ContentItem.class);
            Assert.isFalse(hasInstance(instance.getLanguage()),
                    "The bundle already contains an instance "
                    + "for the language " + instance.getLanguage());
        }

        instance.setParent(this);
        instance.setContentSection(getContentSection());

        if (Assert.isEnabled()) {
            Assert.isEqual(this, instance.getParent());
        }
    }

    /**
     * Removes a language instance from the bundle.  This method will
     * fail if <code>instance</code> is the primary instance.
     *
     * Note that the language instance is not deleted by this
     * operation; it is just removed from the Bundle. Users of this
     * method have to take care to properly dispose of this instance!
     *
     * @param instance The language instance to remove
     * @pre instance != null
     * @post instance.getParent() == null
     */
    public void removeInstance(final ContentItem instance) {
        if (Assert.isEnabled()) {
            Assert.exists(instance, ContentItem.class);
            Assert.isEqual(this, instance.getParent());
            Assert.isNotEqual(instance, getPrimaryInstance());
        }

        instance.setParent(null);

        if (Assert.isEnabled()) {
            Assert.isTrue(instance.getParent() == null);
        }
    }

    /**
     * Gets the primary instance of this bundle.
     *
     * @return the language instance of this item which is marked as
     * the primary instance
     * @see #addInstance(ContentItem)
     */
    public final ContentItem getPrimaryInstance() {
        return getInstance(getDefaultLanguage());
    }

    /**
     * Produces a collection containing all language instances in this
     * bundle.
     *
     * @return a collection of language instances
     */
    public final ItemCollection getInstances() {
        return new ItemCollection(instances());
    }

    public final ContentItem getInstance(final Locale locale) {
        return this.getInstance(locale.getLanguage(), false);
    }

    public final ContentItem getInstance(final Locale locale, boolean allowLanguageIndependent) {
        return this.getInstance(locale.getLanguage(), allowLanguageIndependent);
    }

    /**
     * Returns a language instance for <code>language</code> or
     * <code>null</code> if no such instance exists.
     *
     * This method does <strong>not</strong> do language negotiation,
     * it only returns an exact match for the given Locale or
     * <code>null</code> if no such match is found.
     * 
     * It will try to return a language independent version of the 
     * content item, if there is one and {@code allowLanguageIndependent}
     * is true.
     * 
     * @param language the language for which to get an instance
     * @return the instance of this item which exactly matches the
     * <em>language</em> part of the Locale <code>l</code>
     * @see #negotiate
     * @pre language != null
     */
    public final ContentItem getInstance(final String language) {
        return this.getInstance(language, Kernel.getConfig().languageIndependentItems());
    }

    public final ContentItem getInstance(final String language, boolean allowLanguageIndependent) {
        if (Assert.isEnabled()) {
            Assert.exists(language, String.class);
            Assert.isTrue(language.length() == 2,
                    language + " does not look like a valid language "
                    + "code");
        }

        // The data object to return
        ContentItem contentItem = null;

        // Try to get the content item in the exact language
        DataAssociationCursor instances = instances();
        instances.addEqualsFilter(LANGUAGE, language);

        if (instances.next()) {
            contentItem = (ContentItem) DomainObjectFactory.newInstance(instances.getDataObject());
        }

        instances.close();

        // Try to get a language independent version of the content item,
        // if we couldn't find an exact match and language independent
        // content items are acceptable.
        if (contentItem == null && allowLanguageIndependent == true) {
            contentItem = this.getInstance(GlobalizationHelper.LANG_INDEPENDENT, false);
        }

        return contentItem;
    }

    /**
     * Tells whether <code>instance</code> is present in the bundle.
     *
     * @param instance the language instance to look for
     * @return <code>true</code> if the instance is in the bundle
     */
    public final boolean hasInstance(final ContentItem instance) {
        Assert.exists(instance, ContentItem.class);

        final DataAssociationCursor instances = instances();
        instances.addEqualsFilter(ID, instance.getID());

        return !instances.isEmpty();
    }

    /**
     * Utility method to check if this bundle already contains an
     * instance for the given <code>language</code>.
     *
     * @param language an ISO639 2-letter language code
     * @return <code>true</code> if this <code>ContentBundle</code>
     * contains an instance for the language given as an argument
     * @see ContentItem#getLanguage()
     */
    public final boolean hasInstance(final String language) {
        return this.hasInstance(language, false);
    }

    public final boolean hasInstance(final String language, boolean allowLanguageIndependent) {
        if (Assert.isEnabled()) {
            Assert.exists(language, String.class);
            Assert.isTrue(language.length() == 2,
                    language + " is not an ISO639 language code");
        }

        final DataAssociationCursor instances = instances();

        // If allowLanguageIndependent == false (default case), only search 
        // for an exact language match
        if (allowLanguageIndependent == false) {
            instances.addEqualsFilter(LANGUAGE, language);
        } // Else, search also for language independent version
        else {
            /*FilterFactory ff = instances.getFilterFactory();
            instances.addFilter(
                    ff.or().addFilter(ff.equals(LANGUAGE, language)).
                    addFilter(ff.equals(LANGUAGE, "--")));*/
            instances.addFilter(String.format("(%s = '%s' or %s = '%s')",
                                              LANGUAGE,
                                              language,
                                              LANGUAGE,
                                              GlobalizationHelper.LANG_INDEPENDENT));
        }

        return !instances.isEmpty();
    }

    /**
     * List all languages in which this item is available, i.e. the
     * language codes of all instances in this bundle.
     *
     * @return A <code>Collection</code> of language 2-letter codes in
     * which this item is available
     */
    public final Collection<String> getLanguages() {
        // XXX For LIVE bundles, there might be several PENDING
        // instances with the same language. Maybe we should filter
        // these out and return only one?

        final ItemCollection items = getInstances();

        final Collection list = new ArrayList();

        while (items.next()) {
            list.add(items.getLanguage());
        }

        items.close();

        if (Assert.isEnabled()) {
            Assert.isTrue(!list.isEmpty() || getInstances().isEmpty());
        }

        return list;
    }

    /**
     * Negotiate the right language instance for this bundle and return it.
     *
     * @param locales the acceptable locales for the language
     * instance, in <em>decreasing</em> importance
     * @return the negotiated language instance or <code>null</code>
     * if there is no language instance for any of the locales in
     * <code>locales</code>
     * @pre locales != null
     * @deprecated Locale negotiation takes place in 
     *  {@link com.arsdigita.globalization.GlobalizationHelper}.
     *  Use {@link #getInstance(java.lang.String)} instead.
     */
    // Quasimodo:
    // Is this method ever used? Netbeans couldn't find anything.
    @Deprecated
    public ContentItem negotiate(Locale[] locales) {
        Assert.exists(locales);
        String supportedLanguages = LanguageUtil.getSupportedLanguages();
        DataAssociationCursor instancesCursor = instances();
        DataObject dataObject = null;
        int bestMatch = 0;
        DataObject matchingInstance = null;
        String language = null;
        while (instancesCursor.next()) {
            dataObject = instancesCursor.getDataObject();
            language = (String) dataObject.get(LANGUAGE);

            // If language is not one of the supported languages, skip this entry
            if (!supportedLanguages.contains(language)) {
                continue;
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug("negotiate: language= " + language);
            }

            if (language != null) {
                // If the current object is languange independent and no better
                // match is already found, match it with the lowest priority
                if (language.equals("--") && matchingInstance == null) {
                    bestMatch = locales.length;
                    matchingInstance = dataObject;
                } else {
                    // In any other case
                    for (int i = 0; i < locales.length; i++) {
                        if (language.equals(locales[i].getLanguage())) {
                            if (i < bestMatch || matchingInstance == null) {
                                bestMatch = i;
                                matchingInstance = dataObject;
                                if (s_log.isDebugEnabled()) {
                                    s_log.debug("negotiate: "
                                            + "bestMatch= " + i
                                            + ", language= " + language);
                                }
                            } // else other match with less preferred language found
                        }
                    } // end for
                }
            } // end if
            if (bestMatch == 0 && matchingInstance != null) {
                s_log.debug("negotiate: best possible match found, exiting");
                break;       // exit loop when best match is found
            }
        }
        instancesCursor.close();
        if (matchingInstance != null) {
            return (ContentItem) DomainObjectFactory.newInstance(matchingInstance);
        } else {
            s_log.info("negotiate: no match found!");
            return null;
        }
    }

    /**
     * Negotiate the right language instance for this bundle and return it.
     *
     * @param locales the acceptable locales for the language instance, in
     *  <em>decreasing</em> importance. This parameter has to be an
     * <code>Enumeration</code> of <code>Locale</code> objects.
     * @return the negotiated language instance or <code>null</code> if there
     *  is no language instance for any of the locales in <code>locales</code>.
     * @pre locales != null
     * @deprecated Locale negotiation takes place in 
     *  {@link com.arsdigita.globalization.GlobalizationHelper}.
     *  Use {@link #getInstance(java.lang.String)} instead.
     */
    @Deprecated
    public ContentItem negotiate(Enumeration locales) {
        String supportedLanguages = LanguageUtil.getSupportedLanguages();

        Assert.exists(locales);
        /* copy "locales" enumeration, since we have to iterate
         * over it several times
         */
        Locale loc = null;
        List languageCodes = new ArrayList();
        for (int i = 0; locales.hasMoreElements(); i++) {
            loc = (Locale) locales.nextElement();

            // Quasimodo:
            // Only add languages to the List which are supported by cms
            if (supportedLanguages.contains(loc.getLanguage())) {
                languageCodes.add(loc.getLanguage());
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug("negotiate: pref " + i + ": " + loc.getLanguage());
            }
        }

        // Add unspecified language for language independent objects
        if (supportedLanguages.contains("--")) {
            languageCodes.add("--");
        }

        final DataAssociationCursor instances = instances();

        DataObject dataObject = null;
        int bestMatch = 0;
        DataObject match = null;
        String language = null;

        while (instances.next()) {
            dataObject = instances.getDataObject();
            language = (String) dataObject.get(LANGUAGE);

            if (s_log.isDebugEnabled()) {
                s_log.debug("negotiate: language= " + language);
            }

            if (language != null) {
                for (int i = 0; i < languageCodes.size(); i++) {
                    if (language.equals((String) languageCodes.get(i))) {
                        if (i < bestMatch || match == null) {
                            bestMatch = i;
                            match = dataObject;
                            if (s_log.isDebugEnabled()) {
                                s_log.debug("negotiate: "
                                        + "bestMatch= " + i
                                        + ", language= " + language);
                            }
                        } // else other match with less preferred language found
                    }
                } // end for
            } // end if

            if (bestMatch == 0 && match != null) {
                s_log.debug("negotiate: best possible match found, exiting");
                break;       // exit loop when best match is found
            }
        }

        instances.close();

        return (ContentItem) DomainObjectFactory.newInstance(match);
    }

    // Methods from item that bundle overrides
    @Override
    protected void beforeSave() {
        super.beforeSave();

        final ContentItem primary = getPrimaryInstance();

        Assert.exists(getContentType(), ContentType.class);

        if (primary != null) {
            primary.setContentSection(getContentSection());
        }
    }

    @Override
    protected boolean canPublishToFS() {
        return false;
    }

    @Override
    protected void publishToFS() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContentItem publish(final LifecycleDefinition definition,
            final Date start) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Lifecycle getLifecycle() {
        // Bundles do not have lifecycles.

        return null;
    }

    @Override
    public void setLifecycle(final Lifecycle lifecycle) {
        // I'd like to do the following, but VersionCopier calls
        // setLifecycle.
        //throw new UnsupportedOperationException();
    }

    /**
     * Ignore the <code>INSTANCES</code> property for
     * <code>ItemCopier.VERSION_COPY</code>.
     *
     * @param source the source CustomCopy item
     * @param property the property to copy
     * @param copier a temporary class that is able to copy a child item
     *   correctly.
     * @return true if the property was copied; false to indicate
     *   that regular metadata-driven methods should be used
     *   to copy the property.
     */
    @Override
    public boolean copyProperty(final CustomCopy source,
            final Property property,
            final ItemCopier copier) {
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            if (INSTANCES.equals(property.getName())) {
                return true;
            } else if (ATOZ_ALIASING_PROVIDERS.equals(property.getName())) {
                return true;
            }
        }

        return super.copyProperty(source, property, copier);
    }

    @Override
    public boolean copyServices(final ContentItem source) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Copying services on bundle " + getName() + " "
                    + getID() + " using source " + source.getID());
        }

        // Copy categories
        CategoryCollection categories = source.getCategoryCollection();
        while (categories.next()) {
            final Category category = categories.getCategory();
            
            category.addChild(this, categories.getSortKey());
            category.save(); // XXX remove me
        }
        categories.close();

        return true;
    }

    @Override
    protected void initialize() {
        super.initialize();
        addObserver(s_instancesObserver);

        m_wasNew = isNew();
    }

    @Override
    protected void afterSave() {
        if (m_wasNew) {
            getPrimaryInstance().setContentSection(getContentSection());
        }

        super.afterSave();
    }

    // Utility methods
    private DataAssociationCursor instances() {
        final DataAssociationCursor cursor =
                ((DataAssociation) super.get(INSTANCES)).cursor();

        return cursor;
    }

    private DataAssociationCursor instances(final String version) {
        final DataAssociationCursor cursor = instances();

        cursor.addEqualsFilter(VERSION, version);

        return cursor;
    }
}
