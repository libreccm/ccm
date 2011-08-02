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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.BinaryAsset;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.SecurityManager;
//import com.arsdigita.cms.installer.ServiceInstaller;
// import com.arsdigita.cms.installer.WorkspaceInstaller;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelContext;
import com.arsdigita.kernel.PackageInstance;
import com.arsdigita.kernel.PackageInstanceCollection;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.SiteNodeCollection;
import com.arsdigita.kernel.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class provides many utility functions for the Content Management
 * System.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: Utilities.java 2140 2011-01-16 12:04:20Z pboy $
 */
public class Utilities {

//  public final static String CMS_WORKSPACE = WorkspaceInstaller.PACKAGE_KEY;
//  public final static String CMS_SERVICE = ServiceInstaller.PACKAGE_KEY;

    // Used for caching util lookups
    private static HashMap m_cache = new HashMap();

    private static Date s_lastSectionRefresh = null;
    private static Map s_sectionRefreshTimes =
        Collections.synchronizedMap(new HashMap());

    public static final Logger LOG = Logger.getLogger(Utilities.class);

    /**
     * Fetch the location of the CMS Workspace package. Caches the result.
     * @return The URL of the CMS Workspace package
     */
    public static String getWorkspaceURL() {
        String url = (String) m_cache.get(CMS.WORKSPACE_PACKAGE_KEY);
        if ( url == null ) {
            url = getSingletonPackageURL(CMS.WORKSPACE_PACKAGE_KEY);
            m_cache.put(CMS.WORKSPACE_PACKAGE_KEY, url);
        }
        return url;
    }

    /**
     * Fetch the location of the CMS Services package. Caches the result.
     * @return The URL of the CMS Services package
     */
    public static String getServiceURL() {
        String url = (String) m_cache.get(CMS.SERVICE_PACKAGE_KEY);
        if ( url == null ) {
	    // chris.gilbert@westsussex.gov.uk
            // We don't want application context in this url, especially when 
            // it gets cached in a static variable - if I have a 
            // file that is maintained by a non cms application eg 
            // forum, then I can end up with a url that doesn't work
            // and so breaks file links everywhere
            url = getSingletonPackageURLSansContext(CMS.SERVICE_PACKAGE_KEY);
            m_cache.put(CMS.SERVICE_PACKAGE_KEY, url);
        }

        return url;
    }

    /**
     * The URL to log out.
     * @return The logout URL
     */
    public static String getLogoutURL() {
        StringBuffer buf = new StringBuffer(getServiceURL());
        buf.append("logout");
        return buf.toString();
    }

    /**
     * Constuct a URL which serves a binary asset.
     *
     * @param asset  The binary asset
     * @return the URL which will serve the specified binary asset
     */
    public static String getAssetURL(BinaryAsset asset) {
        return getAssetURL(asset.getID());
    }

    /**
     * Constuct a URL which serves a binary asset.
     *
     * @param assetId  The asset ID
     * @return the URL which will serve the specified binary asset
     */
    public static String getAssetURL(BigDecimal assetId) {
        StringBuffer buf = new StringBuffer(getServiceURL());
        buf.append("stream/asset?");
        buf.append(StreamAsset.ASSET_ID).append("=").append(assetId);
        return buf.toString();
    }



    /**
     * Constuct a URL which serves an image.
     *
     * @param asset  The image asset whose image is to be served
     * @return the URL which will serve the specified image asset
     */
    public static String getImageURL(ImageAsset asset) {
        StringBuffer buf = new StringBuffer(getServiceURL());
        buf.append("stream/image/?");
        buf.append(StreamImage.IMAGE_ID).append("=").append(asset.getID());
        return buf.toString();
    }



    /**
     * Constuct an oid for the image.  This is used when publishing
     * to the file system.
     *
     * @param asset  The image asset whose image is to be served
     * @return the oid for the specified image asset
     */
    public static String getImageOID(ImageAsset asset) {
        String oid = asset.getOID().toString();
        return oid;
    }


    public static String getGlobalAssetsURL() {
        return getWebappContext();
    }


    /**
     * Fetches the URL of a mounted instance of a package.
     * @param key The package key
     * @return The URL where the package is mounted
     */
    private static String getSingletonPackageURL(String key) {
        PackageType type = null;
        type = PackageType.findByKey(key);

        PackageInstanceCollection instances = type.getInstances();
        PackageInstance instance = null;
        if ( !instances.next() ) {
            instances.close();
            throw new RuntimeException(
                                       "Failed to locate an instance of the singleton package: " + key);
        } else {
            instance = instances.getPackageInstance();
            instances.close();
        }

        SiteNodeCollection nodes = instance.getMountPoints();
        SiteNode node = null;
        if ( !nodes.next() ) {
            nodes.close();
            throw new RuntimeException(
                                       "Failed to locate a mountpoint for the singleton package: " + key);
        } else {
            node = nodes.getSiteNode();
            nodes.close();
        }

        StringBuffer url = new StringBuffer();
        String context = getWebappContext();
        if (context != null) {
            url.append(context);
        }
        url.append(SiteNode.getRootSiteNode().getURL())
            .append(node.getURL().substring(1));

        LOG.debug("Single package url for " + key + " is " + url);

        return url.toString();
    }

    private static String getSingletonPackageURLSansContext(String key) {
	PackageType type = null;
	type = PackageType.findByKey(key);
	PackageInstanceCollection instances = type.getInstances();
	PackageInstance instance = null;
	if ( !instances.next() ) {
	    instances.close();
	    throw new RuntimeException(
				   "Failed to locate an instance of the singleton package: " + key);
	} else {
	    instance = instances.getPackageInstance();
	    instances.close();
	}

	SiteNodeCollection nodes = instance.getMountPoints();
	SiteNode node = null;
	if ( !nodes.next() ) {
	    nodes.close();
	    throw new RuntimeException(
				   "Failed to locate a mountpoint for the singleton package: " + key);
	} else {
	    node = nodes.getSiteNode();
	    nodes.close();
	}

	StringBuffer url = new StringBuffer();
			
	url.append(SiteNode.getRootSiteNode().getURL())
		.append(node.getURL().substring(1));

	LOG.debug("Single package url for " + key + " is " + url);

	return url.toString();
    }


    /**
     * Fetch the context path of the request. This is typically "/".
     *
     * @return The webapp context path
     */
    public static String getWebappContext() {
        return DispatcherHelper.getWebappContext();
    }


    /**
     * Check for the last refresh on authoring kits or content types in
     * a section.
     **/
    public static synchronized Date
        getLastSectionRefresh(ContentSection section) {

        // cache by URL string instead of by section object to avoid
        // holding the reference.

        String sectionURL = section.getURL();

        Date lastModified = (Date) s_sectionRefreshTimes.get(sectionURL);
        if (lastModified == null) {
            lastModified = new Date();
            s_lastSectionRefresh = lastModified;
            s_sectionRefreshTimes.put(sectionURL, lastModified);
        }

        return lastModified;
    }

    /**
     * Check for the last refresh on authoring kits or content types in
     * any section.
     **/
    public static Date getLastSectionRefresh() {

        // instantiate last refresh lazily to ensure that first result is
        // predictable.

        if (s_lastSectionRefresh == null) {
            s_lastSectionRefresh = new Date();
        }
        return s_lastSectionRefresh;
    }

    /**
     * Force the authoring UI to reload. This should be done every time an
     * authoring kit or a content type are updated.
     */
    public static void refreshItemUI(PageState state) {
        // Drop the authoring kit UI to force it to refresh
        // THE URL SHOULD NOT BE HARDCODED !

        ContentSection section = CMS.getContext().getContentSection();

        // OLD APPROACH: used in conjunction with CMSDispatcher.  This
        // shouldn't do any harm even if CMSDispatcher is not being used.
        CMSDispatcher.releaseResource(section, "admin/item");
        ContentCenterDispatcher.releaseResource("");
        ContentCenterDispatcher.releaseResource("index");
        refreshAdminUI(state);

        // NEW APPROACH: used in conjunction with
        // ContentSectionDispatcher.  cache by URL string instead of by
        // section object to avoid holding the reference.  This shouldn't
        // do any harm even if ContentSectionDispatcher is not being used.
        s_lastSectionRefresh = new Date();
        s_sectionRefreshTimes.put(section.getURL(), s_lastSectionRefresh);
    }

    /**
     * Force the authoring UI to reload. This should be done every time an
     * authoring kit or a content type are updated.
     */
    public static void refreshAdminUI(PageState state) {
        // Drop the admin UI to force it to refresh
        // THE URL SHOULD NOT BE HARDCODED !

        ContentSection section = CMS.getContext().getContentSection();

        CMSDispatcher.releaseResource(section, "admin");
        CMSDispatcher.releaseResource(section, "admin/index");
        CMSDispatcher.releaseResource(section, "");
        ContentCenterDispatcher.releaseResource("");
        ContentCenterDispatcher.releaseResource("index");
    }

    /**
     * Add the "pragma: no-cache" header to the HTTP response to make sure
     * the browser does not cache tha page
     *
     * @param response The HTTP response
     * @deprecated use com.arsdigita.dispatcher.DispatcherHelper.cacheDisable(HttpServletResponse)
     */
    public static void disableBrowserCache(HttpServletResponse response) {
        response.addHeader("pragma", "no-cache");
    }


    /**
     * Fetches the currently logged in user.
     *
     * @param request The HTTP request
     * @return The currently logged-in user, or null if there is none
     * @deprecated use {@link KernelContext#getParty()}
     */
    public static User getCurrentUser(HttpServletRequest request) {
        KernelContext kernelContext = Kernel.getContext();
        if ( kernelContext.getParty() instanceof User ) {
            return (User) kernelContext.getParty();
        } else {
            return null;
        }

    }

    /**
     * Fetch the security manager.
     *
     * @param state The page state
     * @return The SecurityManager for the content section
     */
    public static SecurityManager getSecurityManager(PageState state) {
        ContentSection section = CMS.getContext().getContentSection();

        return new SecurityManager(section);
    }

}
