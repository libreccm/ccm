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
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ContentCenter;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.util.LanguageUtil;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Resolves items to URLs and URLs to items for multiple language variants.
 *
 * Created Mon Jan 20 14:30:03 2003.
 *
 * @author <a href="mailto:mhanisch@redhat.com">Michael Hanisch</a>
 * @version $Id: MultilingualItemResolver.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class MultilingualItemResolver extends AbstractItemResolver implements
    ItemResolver {

    private static final Logger s_log = Logger.getLogger(
        MultilingualItemResolver.class);

    private static MasterPage s_masterP = null;
    private static final String ADMIN_PREFIX = "admin";

    /**
     * The string identifying an item's ID in the query string of a URL.
     */
    protected static final String ITEM_ID = "item_id";

    /**
     * The separator used in URL query strings; should be either "&" or ";".
     */
    protected static final String SEPARATOR = "&";

    public MultilingualItemResolver() {
        s_log.debug("Undergoing creation");
    }

    /**
     * Returns a content item based on section, url, and use context.
     *
     * @param section The current content section
     * @param url     The section-relative URL
     * @param context The use context, e.g. <code>ContentItem.LIVE</code>,
     *                <code>CMSDispatcher.PREVIEW</code> or
     *                <code>ContentItem.DRAFT</code>. See {@link
     * #getCurrentContext}.
     *
     * @return The content item, or null if no such item exists
     */
    @Override
    public ContentItem getItem(final ContentSection section,
                               String url,
                               final String context) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Resolving the item in content section " + section
                            + " at URL '" + url + "' for context " + context);
        }

        Assert.exists(section, "ContentSection section");
        Assert.exists(url, "String url");
        Assert.exists(context, "String context");

        Folder rootFolder = section.getRootFolder();
        url = stripTemplateFromURL(url);

        // nothing to do, if root folder is null
        if (rootFolder == null) {
            s_log.debug("The root folder is null; returning no item");
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Using root folder " + rootFolder);
            }

            if (ContentItem.LIVE.equals(context)) {
                s_log.debug("The use context is 'live'");

                // We allow for returning null, so the root folder may
                // not be live.
                //Assert.isTrue(rootFolder.isLive(),
                //    "live context - root folder of secion must be live");
                // If the context is 'live', we need the live item.
                rootFolder = (Folder) rootFolder.getLiveVersion();

                if (rootFolder == null) {
                    s_log.debug("The live version of the root folder is "
                                    + "null; returning no item");
                } else {
                    s_log.debug("The root folder has a live version; "
                                    + "recursing");

                    final String prefix = section.getURL() + rootFolder
                        .getPath();

                    if (url.startsWith(prefix)) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("The URL starts with prefix '" + prefix
                                            + "'; removing it");
                        }

                        url = url.substring(prefix.length());
                    }

                    final ContentItem item = getItemFromLiveURL(url, rootFolder);

                    if (s_log.isDebugEnabled()) {
                        s_log
                            .debug("Resolved URL '" + url + "' to item " + item);
                    }

                    return item;
                }
            } else if (ContentItem.DRAFT.equals(context)) {
                s_log.debug("The use context is 'draft'");

                // For 'draft' items, 'generateUrl()' method returns
                // URL like this
                // '/acs/wcms/admin/item.jsp?item_id=10201&set_tab=1'
                // Check if URL contains any match of string
                // 'item_id', then try to instanciate item_id value
                // and return FIXME: Please hack this if there is
                // more graceful solution. [aavetyan]
                if (Assert.isEnabled()) {
                    Assert.isTrue(url.indexOf(ITEM_ID) >= 0,
                                  "url must contain parameter " + ITEM_ID);
                }

                final ContentItem item = getItemFromDraftURL(url);

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Resolved URL '" + url + "' to item " + item);
                }

                return item;
            } else if (CMSDispatcher.PREVIEW.equals(context)) {
                s_log.debug("The use context is 'preview'");

                final String prefix = CMSDispatcher.PREVIEW + "/";

                if (url.startsWith(prefix)) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("The URL starts with prefix '" + prefix
                                        + "'; removing it");
                    }

                    url = url.substring(prefix.length());
                }

                final ContentItem item = getItemFromLiveURL(url, rootFolder);

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Resolved URL '" + url + "' to item " + item);
                }

                return item;
            } else {
                throw new IllegalArgumentException(
                    "Invalid item resolver context " + context);
            }
        }

        s_log.debug("No item resolved; returning null");

        return null;
    }

    /**
     * Fetches the current context based on the page state.
     *
     * @param state the current page state
     *
     * @return the context of the current URL, such as
     *         <code>ContentItem.LIVE</code> or <code>ContentItem.DRAFT</code>
     *
     * @see ContentItem#LIVE
     * @see ContentItem#DRAFT
     */
    @Override
    public String getCurrentContext(final PageState state) {
        s_log.debug("Getting the current context");

        // XXX need to use Web.getWebContext().getRequestURL() here.
        String url = state.getRequest().getRequestURI();

        final ContentSection section = CMS.getContext().getContentSection();

        // If this page is associated with a content section,
        // transform the URL so that it is relative to the content
        // section site node.
        if (section != null) {
            final String sectionURL = section.getURL();

            if (url.startsWith(sectionURL)) {
                url = url.substring(sectionURL.length());
            }
        }

        // Remove any template-specific URL components (will only work
        // if they're first in the URL at this point; verify). XXX but
        // we don't actually verify?
        url = stripTemplateFromURL(url);

        // Determine if we are under the admin UI.
        if (url.startsWith(ADMIN_PREFIX) || url.startsWith(ContentCenter
            .getURL())) {
            return ContentItem.DRAFT;
        } else {
            return ContentItem.LIVE;
        }
    }

    /**
     * Generates a URL for a content item.
     *
     * @param itemId  The item ID
     * @param name    The name of the content page
     * @param state   The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     *
     * @return The URL of the item
     *
     * @see #getCurrentContext
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final BigDecimal itemId,
                                  final String name,
                                  final ContentSection section,
                                  final String context) {
        return generateItemURL(state, itemId, name, section, context, null);
    }

    /**
     * Generates a URL for a content item.
     *
     * @param itemId          The item ID
     * @param name            The name of the content page
     * @param state           The page state
     * @param section         the content section to which the item belongs
     * @param context         the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     *
     * @return The URL of the item
     *
     * @see #getCurrentContext
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final BigDecimal itemId,
                                  final String name,
                                  final ContentSection section,
                                  final String context,
                                  final String templateContext) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Generating an item URL for item id " + itemId
                            + ", section " + section + ", and context '"
                        + context
                            + "' with name '" + name + "'");
        }

        Assert.exists(itemId, "BigDecimal itemId");
        Assert.exists(context, "String context");
        Assert.exists(section, "ContentSection section");

        if (ContentItem.DRAFT.equals(context)) {
            // No template context here.
            return generateDraftURL(section, itemId);
        } else if (CMSDispatcher.PREVIEW.equals(context)) {
            ContentItem item = new ContentItem(itemId);

            return generatePreviewURL(section, item, templateContext);
        } else if (ContentItem.LIVE.equals(context)) {
            ContentItem item = new ContentItem(itemId);

            if (Assert.isEnabled()) {
                Assert.exists(item, "item");
                Assert.isTrue(ContentItem.LIVE.equals(item.getVersion()),
                              "Generating " + ContentItem.LIVE + " "
                                  + "URL; this item must be the live version");
            }

            return generateLiveURL(section, item, templateContext);
        } else {
            throw new IllegalArgumentException("Unknown context '" + context
                                                   + "'");
        }
    }

    /**
     * Generates a URL for a content item.
     *
     * @param item    The item
     * @param state   The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     *
     * @return The URL of the item
     *
     * @see #getCurrentContext
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final ContentItem item,
                                  final ContentSection section,
                                  final String context) {
        return generateItemURL(state, item, section, context, null);
    }

    /**
     * Generates a URL for a content item.
     *
     * @param item            The item
     * @param state           The page state
     * @param section         the content section to which the item belongs
     * @param context         the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     *
     * @return The URL of the item
     *
     * @see #getCurrentContext
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final ContentItem item,
                                  ContentSection section,
                                  final String context,
                                  final String templateContext) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Generating an item URL for item " + item + ", section "
                            + section + ", and context " + context);
        }

        Assert.exists(item, "ContentItem item");
        Assert.exists(context, "String context");

        if (section == null) {
            section = item.getContentSection();
        }

        if (ContentItem.DRAFT.equals(context)) {
            if (Assert.isEnabled()) {
                Assert.isTrue(ContentItem.DRAFT.equals(item.getVersion()),
                              "Generating " + ContentItem.DRAFT
                                  + " url: item must be draft version");
            }

            return generateDraftURL(section, item.getID());
        } else if (CMSDispatcher.PREVIEW.equals(context)) {
            return generatePreviewURL(section, item, templateContext);
        } else if (ContentItem.LIVE.equals(context)) {
            if (Assert.isEnabled()) {
                Assert.isTrue(ContentItem.LIVE.equals(item.getVersion()),
                              "Generating " + ContentItem.LIVE
                                  + " url: item must be live version");
            }

            return generateLiveURL(section, item, templateContext);
        } else {
            throw new RuntimeException("Unknown context " + context);
        }
    }

    /**
     * Returns a master page based on page state (and content section).
     *
     * @param item    The content item
     * @param request The HTTP request
     *
     * @return The master page
     *
     * @throws javax.servlet.ServletException
     */
    @Override
    public CMSPage getMasterPage(final ContentItem item,
                                 final HttpServletRequest request)
        throws ServletException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting the master page for item " + item);
        }

        // taken from SimpleItemResolver
        if (s_masterP == null) {
            s_masterP = new MasterPage();
            s_masterP.init();
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Returning master page " + s_masterP);
        }

        return s_masterP;
    }

    /**
     * Returns content item's draft version URL
     *
     * @param section The content section to which the item belongs
     * @param itemId  The content item's ID
     *
     * @return generated URL string
     */
    protected String generateDraftURL(final ContentSection section,
                                      final BigDecimal itemId) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Generating draft URL for item ID " + itemId
                            + " and section " + section);
        }

        if (Assert.isEnabled()) {
            Assert.isTrue(section != null && itemId != null,
                          "get draft url: neither secion nor item "
                              + "can be null");
        }

        final String url = ContentItemPage.getItemURL(section.getPath() + "/",
                                                      itemId,
                                                      ContentItemPage.AUTHORING_TAB);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Generated draft URL " + url);
        }

        return url;
    }

    /**
     * Generate a <em>language-independent</em> URL to the <code>item</code> in
     * the given section.<p>
     * When a client retrieves this URL, the URL is resolved to point to a
     * specific language instance of the item referenced here, i.e. this URL
     * will be resolved to a <em>language-specific</em> URL internally.
     *
     * @param section         the <code>ContentSection</code> that contains this
     *                        item
     * @param item            <code>ContentItem</code> for which a URL should be
     *                        constructed.
     * @param templateContext template context; will be ignored if
     *                        <code>null</code>
     *
     * @return a <em>language-independent</em> URL to the <code>item</code> in
     *         the given <code>section</code>, which will be presented within
     *         the given <code>templateContext</code>
     */
    protected String generateLiveURL(final ContentSection section,
                                     final ContentItem item,
                                     final String templateContext) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Generating live URL for item " + item + " in "
                            + "section " + section);
        }

        /*
         * URL = URL of section + templateContext + path to the ContentBundle
         * which contains the item
         */
        final StringBuffer url = new StringBuffer(400);
        //url.append(section.getURL());
        url.append(section.getPath()).append("/");

        /*
         * add template context, if one is given
         */
        // This is breaking URL's...not sure why it's here. XXX
        // this should work with the appropriate logic. trying again.
        if (!(templateContext == null || templateContext.length() == 0)) {
            url.append(TEMPLATE_CONTEXT_PREFIX).append(templateContext).append(
                "/");
        }

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

            final String fname = bundle.getPath();

            if (s_log.isDebugEnabled()) {
                s_log.debug("Appending the bundle's file name '" + fname + "'");
            }

            url.append(fname);

        } else {
            s_log.debug("No bundle found; using the item's path directly");

            url.append(item.getPath());
        }

        if (CMSConfig.getInstanceOf().getUseLanguageExtension()) {
            // This will append the language to the url, which will in turn render
            // the language negotiation inoperative
            final String language = item.getLanguage();

            if (language == null) {
                s_log.debug("The item has no language; omitting the "
                                + "language encoding");
            } else {
                s_log.debug("Encoding the language of the item passed in, '"
                                + language + "'");

                url.append(".").append(language);
            }
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Generated live URL " + url.toString());
        }

        return url.toString();
    }

    /**
     * Generate a URL which can be used to preview the <code>item</code>, using
     * the given <code>templateContext</code>.<p>
     * Only a specific language instance can be previewed, meaning there
     * <em>no</em> language negotiation is involved when a request is made to a
     * URL that has been generated by this method.
     *
     * @param section         The <code>ContentSection</code> which contains the
     *                        <code>item</code>
     * @param item            The <code>ContentItem</code> for which a URL
     *                        should be generated.
     * @param templateContext the context that determines which template should
     *                        render the item when it is previewed; ignored if
     *                        the argument given here is <code>null</code>
     *
     * @return a URL which can be used to preview the given <code>item</code>
     */
    protected String generatePreviewURL(ContentSection section,
                                        ContentItem item,
                                        String templateContext) {
        Assert.exists(section, "ContentSection section");
        Assert.exists(item, "ContentItem item");

        final StringBuffer url = new StringBuffer(100);
        url.append(section.getPath());
        url.append("/");
        url.append(CMSDispatcher.PREVIEW);
        url.append("/");
        /*
         * add template context, if one is given
         */
        // This is breaking URL's...not sure why it's here. XXX
        // this should work with the appropriate logic. trying again.
        if (!(templateContext == null || templateContext.length() == 0)) {
            url.append(TEMPLATE_CONTEXT_PREFIX).append(templateContext).append(
                "/");
        }

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

            url.append(bundle.getPath());
        } else {
            s_log.debug("No bundle found; using the item's path directly");

            url.append(item.getPath());
        }

        final String language = item.getLanguage();

        if (language == null) {
            s_log.debug("The item has no language; omitting the "
                            + "language encoding");
        } else {
            s_log.debug("Encoding the language of the item passed in, '"
                            + language + "'");

            url.append(".").append(language);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Generated preview URL " + url.toString());
        }

        return url.toString();
    }

    /**
     * Retrieves <code>ITEM_ID</code> parameter from URL and instantiates item
     * according to this ID.
     *
     * @param url URL that indicates which item should be retrieved; must
     *            contain the <code>ITEM_ID</code> parameter
     *
     * @return the <code>ContentItem</code> the given <code>url</code> points
     *         to, or <code>null</code> if no ID has been found in the
     *         <code>url</code>
     */
    protected ContentItem getItemFromDraftURL(final String url) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Looking up the item from draft URL " + url);
        }

        int pos = url.indexOf(ITEM_ID);

        // XXX this is wrong: here we abort on not finding the
        // parameter; below we return null.
        if (Assert.isEnabled()) {
            Assert.isTrue(pos >= 0,
                          "Draft URL must contain parameter " + ITEM_ID);
        }

        String item_id = url.substring(pos); // item_id == ITEM_ID=.... ?

        pos = item_id.indexOf("="); // should be exactly after the ITEM_ID string

        if (pos != ITEM_ID.length()) {
            // item_id seems to be something like ITEM_IDFOO=

            s_log.debug("No suitable item_id parameter found; returning null");

            return null;        // no ID found
        }

        pos++;                  // skip the "="

        // ID is the string between the equal (at pos) and the next separator
        int i = item_id.indexOf(SEPARATOR);
        item_id = item_id.substring(pos, Math.min(i, item_id.length() - 1));

        if (s_log.isDebugEnabled()) {
            s_log.debug("Looking up item using item ID " + item_id);
        }

        OID oid = new OID(ContentItem.BASE_DATA_OBJECT_TYPE, new BigDecimal(
                          item_id));
        final ContentItem item = (ContentItem) DomainObjectFactory.newInstance(
            oid);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Returning item " + item);
        }

        return item;
    }

    /**
     * Returns a content item based on URL relative to the root folder.
     *
     * @param url          The content item url
     * @param parentFolder The parent folder object, url must be relevant to it
     *
     * @return The Content Item instance, it can also be either Bundle or Folder
     *         objects, depending on URL and file language extension
     */
    protected ContentItem getItemFromLiveURL(String url,
                                             Folder parentFolder) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Resolving the item for live URL " + url
                            + " and parent folder " + parentFolder);
        }

        if (parentFolder == null || url == null || url.equals("")) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("The url is null or parent folder was null "
                                + "or something else is wrong, so just return "
                                + "the parent folder");
            }

            return parentFolder;
        }

        int len = url.length();
        int index = url.indexOf('/');

        if (index >= 0) {
            s_log.debug("The URL starts with a slash; paring off the first "
                            + "URL element and recursing");

            // If we got first slash (index == 0), ignore it and go
            // on, sample '/foo/bar/item.html.en', in next recursion
            // will have deal with 'foo' folder.
            String name = index > 0 ? url.substring(0, index) : "";
            parentFolder = "".equals(name) ? parentFolder
                               : (Folder) parentFolder.getItem(URLEncoder
                    .encode(name), true);
            url = index + 1 < len ? url.substring(index + 1) : "";

            return getItemFromLiveURL(url, parentFolder);
        } else {
            s_log.debug("Found a file element in the URL");

            String[] nameAndLang = getNameAndLangFromURLFrag(url);
            String name = nameAndLang[0];
            String lang = nameAndLang[1];

            ContentItem item = parentFolder.getItem(URLEncoder.encode(name),
                                                    false);
            return getItemFromLangAndBundle(lang, item);
        }
    }

    /**
     * Returns an array containing the the item's name and lang based on the URL
     * fragment.
     *
     * @return a two-element string array, the first element containing the
     *         bundle name, and the second element containing the lang string
     */
    protected String[] getNameAndLangFromURLFrag(String url) {
        String name = null;
        String lang = null;

        /*
         * Try to find out if there's an extension with the language code
         * 1 Get a list of all "extensions", i.e. parts of the url
         *   which are separated by colons
         * 2 If one or more extensions have been found, compare them against
         *   the list of known language codes
         * 2a if a match is found, this language is used to retrieve an instance
         *    from a bundle
         * 2b if no match is found
         */
        final ArrayList exts = new ArrayList(5);
        final StringTokenizer tok = new StringTokenizer(url, ".");

        while (tok.hasMoreTokens()) {
            exts.add(tok.nextToken());
        }

        if (exts.size() > 0) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Found some file extensions to look at; they "
                                + "are " + exts);
            }

            /*
             * We have found at least one extension, so we can
             * proceed.  Now try to find out if one of the
             * extensions looks like a language code (we only
             * support 2-letter language codes!).
             * If so, use this as the language to look for.
             */
 /*
             * First element is the NAME of the item, not an extension!
             */
            name = (String) exts.get(0);
            String ext = null;
            Collection supportedLangs = LanguageUtil.getSupportedLanguages2LA();
            Iterator supportedLangIt = null;

            for (int i = 1; i < exts.size(); i++) {
                ext = (String) exts.get(i);

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Examining extension " + ext);
                }

                /*
                 * Loop through all extensions, but discard the
                 * first one, which is the name of the item.
                 */
                if (ext != null && ext.length() == 2) {
                    /* Only check extensions consisting of 2
                     * characters.
                     *
                     * Compare current extension with known
                     * languages; if it matches, we've found the
                     * language we should use!
                     */
                    supportedLangIt = supportedLangs.iterator();
                    while (supportedLangIt.hasNext()) {
                        if (ext.equals(supportedLangIt.next())) {
                            lang = ext;
                            if (s_log.isDebugEnabled()) {
                                s_log.debug("Found a match; using "
                                                + "language " + lang);
                            }
                            break;
                        }
                    }
                } else {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Discarding extension " + ext + "; "
                                        + "it is too short");
                    }
                }
            }
        } else {
            s_log.debug("The file has no extensions; no language was "
                            + "encoded");
            name = url;     // no extension, so we just have a name here
            lang = null;    // no extension, so we cannot guess the language
        }

        if (Assert.isEnabled()) {
            Assert.exists(name, "String name");
            Assert.exists(lang == null || lang.length() == 2);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("File name resolved to " + name);
            s_log.debug("File language resolved to " + lang);
        }
        String[] returnArray = new String[2];
        returnArray[0] = name;
        returnArray[1] = lang;
        return returnArray;
    }

    /**
     * Finds a language instance of a content item given the bundle, name, and
     * lang string
     *
     * @param lang The lang string from the URL
     * @param item The content bundle
     *
     * @return The negotiated lang instance for the current request.
     */
    protected ContentItem getItemFromLangAndBundle(String lang, ContentItem item) {
        if (item != null && item instanceof ContentBundle) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Found content bundle " + item);
            }
            if (lang == null) {
                s_log.debug("The URL has no language encoded in it; "
                                + "negotiating the language");
                // There is no language, so we get the negotiated locale and call
                // this method again with a proper language
                return this.getItemFromLangAndBundle(GlobalizationHelper
                    .getNegotiatedLocale().getLanguage(), item);
            } else {
                s_log.debug("The URL is encoded with a langauge; "
                                + "fetching the appropriate item from "
                                + "the bundle");
                /*
                 * So the request contains a language code as an
                 * extension of the "name" ==>go ahead and try to
                 * find the item from its ContentBundle.  Fail if
                 * the bundle does not contain an instance for the
                 * given language.
                 */

                final ContentItem resolved = ((ContentBundle) item).getInstance(
                    lang);

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Resolved URL to item " + resolved);
                }
                return resolved;
            }
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("I expected to get a content bundle; I got " + item);
            }

            /*
             * We expected something like a Bundle, but it seems
             * like we got something completely different...  just
             * return this crap and let other people's code deal
             * with it ;-).
             *
             * NOTE: This should never happen :-)
             */
            return item;    // might be null
        }
    }

}
