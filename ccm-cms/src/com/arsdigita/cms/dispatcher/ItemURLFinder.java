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

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.kernel.URLFinderNotFoundException;
import com.arsdigita.kernel.URLService;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Implementation of {@link com.arsdigita.kernel.URLFinder} for content types,
 * necessary for ccm dispatcher to find a concrete content item for an url
 * provided by a client request.
 *
 * Specifically it is a helper class for
 * {@link com.arsdigita.web.OIDRedirectServlet} to map an OID to an URL.
 *
 */
public class ItemURLFinder implements URLFinder {

    private static final Logger s_log = Logger.getLogger(ItemURLFinder.class);

    /**
     *
     * @param oid
     * @param context publication status ['live'|'draft']
     * @return
     * @throws NoValidURLException
     */
    @Override
    public String find(OID oid, String context) throws NoValidURLException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Locating " + oid + " in " + context);
        }

        ContentItem item;
        try {
            item = (ContentItem) DomainObjectFactory
                    .newInstance(oid);
        } catch (DataObjectNotFoundException ex) {
            throw new NoValidURLException(
                    "cannot instantiate item " + oid
                    + " message: " + ex.getMessage());
        }

        if (ContentItem.LIVE.equals(context)) {
            if (item.isLive()) {
                if (!item.isLiveVersion()) {
                    item = item.getLiveVersion();
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Switched to live version " + item);
                    }
                }
            } else {
                s_log.debug("Item was not live");
                throw new NoValidURLException(
                        "item " + oid + " is not live");
            }
        }

        return find(item, context);
    }

    /**
     *
     * @param oid
     * @return
     * @throws NoValidURLException
     */
    @Override
    public String find(OID oid) throws NoValidURLException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Locating " + oid);
        }

        ContentItem item;
        try {
            item = (ContentItem) DomainObjectFactory
                    .newInstance(oid);
        } catch (DataObjectNotFoundException ex) {
            throw new NoValidURLException(
                    "cannot instantiate item " + oid
                    + " message: " + ex.getMessage());
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Item version is " + item.getVersion());
        }

        // Revert to the behavior before change #41315.
        // Clients relied on the behavior that links with no context
        // defaulted to the live version (if one existed). Changing
        // that behavior broke a lot of links in static content that couldn't
        // easily be updated.
        // This change restores the old behavior. We don't get a regression of
        // bz 116226 (which bz 41315 was intended to fix) because the CMS
        // search.xsl has been updated to append "&context=draft" to the search
        // results.  The CMS DHTML editor has also been updated to append
        // generated links with "&context=live". If at some point in the future
        // all unqualified links have been removed, then this fix could be
        // removed as well.
        if (item.isLive() && !item.isLiveVersion()) {
            item = item.getLiveVersion();
            if (s_log.isDebugEnabled()) {
                s_log.debug("Switched to live version " + item);
            }
        }

        return find(item, item.getVersion());
    }

    /**
     *
     * @param item
     * @param context publication status ['live'|'draft']
     * @return
     * @throws NoValidURLException
     */
    private String find(ContentItem item, String context)
            throws NoValidURLException {

        ContentSection section = item.getContentSection();
        ItemResolver resolver = section.getItemResolver();

        // If the ContentItem is an index object for a Category, redirect
        // to the URL for that Category instead of the item.
        // in fact don't do that if we have content='draft', in which case
        // always send to the admin screen (that's for results of the admin
        // search)

        if (!ContentItem.DRAFT.equals(context)) {  // LIVE context
            ACSObject parent = item.getParent();
            ContentBundle bundle = null;
            if (parent instanceof ContentBundle) {
                bundle = (ContentBundle) ((ContentBundle) parent).getDraftVersion();
            }
            if (bundle != null) {
                List<DataObject> categories = getCategories(bundle);

                /* For all associated categories, try to get a url. Stop at 
                 * first successful try.*/
                for(DataObject dobj:categories) {
                    String url;
                    Category indexCat = (Category) DomainObjectFactory.
                            newInstance(dobj);
                    try {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug(item + " is a Category index item. "
                                    + "Resolving URL for " + indexCat);
                        }
                        url = URLService.locate(indexCat.getOID(), context);
                    } catch (URLFinderNotFoundException ufnfe) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Could not find URLFinder for " + indexCat
                                    + ", continuing with URL resolution for " + item,
                                    ufnfe);
                        }
                        continue;
                    } catch (NoValidURLException nvue) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Could not find valid URL for " + indexCat
                                    + ", continuing with URL resolution for " + item,
                                    nvue);
                        }
                        continue;
                    }
                    return url;

                }
            }
        } else { // DRAFT context
            // public users get 404 when item gets unpublished
            // if com.arsdigita.cms.unpublished_not_found=true
            if (ContentSection.getConfig().isUnpublishedNotFound()
                    && !Web.getUserContext().isLoggedIn()) {
                throw new NoValidURLException("user must be logged-in to get draft");
            } else {
                // force the switch to draft version at this point
                // otherwise resolver below breaks with:
                // java.lang.IllegalStateException: Generating draft url:
                // item must be draft version
                if (!item.isDraftVersion()) {
                    item = item.getDraftVersion();
                    s_log.debug("switching to draft version");
                }
            }
        }

        String url = resolver.generateItemURL(null, item, section, context);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Resolved " + item + " in " + context + " to " + url);
        }

        final int sep = url.indexOf('?');
        URL destination = null;

        if (sep == -1) {
            destination = URL.there(url, null);
        } else {
            final ParameterMap params = ParameterMap.fromString(url.substring(sep + 1));

            destination = URL.there(url.substring(0, sep), params);
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("After munging, destination is " + destination);
        }

        return destination.toString();
    }

    /**
     * Get all categories for a content bundle, where the content bundle is an
     * index item.
     *
     * @param bundle The content bundle to test for
     * @return a list of associated categories
     */
    protected List<DataObject> getCategories(ContentBundle bundle) {
        List<DataObject> catList = new ArrayList<DataObject>();
        DataAssociationCursor categories =
                ((DataAssociation) DomainServiceInterfaceExposer.
                get(bundle, Category.CATEGORIES)).cursor();
        categories.addEqualsFilter("link." + Category.IS_INDEX, Boolean.TRUE);

        while(categories.next()) {
            catList.add(categories.getDataObject());
        }
        categories.close();
        
        return catList;
    }
}
