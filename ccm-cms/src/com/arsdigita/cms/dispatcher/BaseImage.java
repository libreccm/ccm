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

import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.caching.CacheTable;
import com.arsdigita.cms.Asset;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.CachedImage;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * A resource handler which streams out a blob from the database.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #20 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: BaseImage.java 1571 2007-04-20 15:57:54Z apevec $
 */
public class BaseImage extends ResourceHandlerImpl {

    public static final String IMAGE_ID = "image_id";
    public static final String OID_PARAM = "oid";
    private final static String s_defaultName = "Image";
    private BigDecimalParameter m_imageId;
    private OIDParameter m_oid;
    private final boolean m_download;
    private String m_disposition;
    // ImageCache
    private static CacheTable s_imageCache = null;

    static {
        if (CMS.getConfig().getImageCacheEnable()) {
            s_imageCache = new CacheTable("BaseImageCache",
                    CMS.getConfig().getImageCacheMaxAge(),
                    CMS.getConfig().getImageCacheMaxSize());
        }
    }
    private static final Logger s_log = Logger.getLogger(BaseImage.class);

    /**
     * Construct the resource handler
     */
    public BaseImage(boolean download) {
        m_imageId = new BigDecimalParameter(IMAGE_ID);
        m_oid = new OIDParameter(OID_PARAM);

        m_download = download;
        if (m_download) {
            m_disposition = "attachment; filename=";
        } else {
            m_disposition = "inline; filename=";
        }
    }

    /**
     * Sets RFC2183 governed Contnet-Disposition header to supply filename to
     * client. See section 19.5.1 of RFC2616 for interpretation of
     * Content-Disposition in HTTP.
     */
    protected void setFilenameHeader(HttpServletResponse response,
            CachedImage cachedImage) {
        String filename = cachedImage.getName();
        if (filename == null) {
            filename = s_defaultName;
        }

        // quote the file name to deal with any special
        // characters in the name of the file
        StringBuilder disposition = new StringBuilder(m_disposition);
        disposition.append('"').append(filename).append('"');

        response.setHeader("Content-Disposition", disposition.toString());
    }

    private void setHeaders(HttpServletResponse response, CachedImage cachedImage) {
        setFilenameHeader(response, cachedImage);

        response.setContentLength(cachedImage.getSize());

        MimeType mimeType = cachedImage.getMimeType();

        if (m_download || mimeType == null) {
            // Section 19.5.1 of RFC2616 says this implies download
            // instead of view
            response.setContentType("application/octet-stream");
        } else {
            response.setContentType(mimeType.getMimeType());
        }

        // Default caching for all other types
        if ("live".equals(cachedImage.getVersion())) {
            DispatcherHelper.cacheForWorld(response);
        } else {
            DispatcherHelper.cacheDisable(response);
        }
    }

    private void send(HttpServletResponse response, CachedImage cachedImage) throws IOException {

        // Stream the blob.
        OutputStream out = response.getOutputStream();
        try {
            cachedImage.writeBytes(out);
        } finally {
            out.close();
        }
    }

    /**
     * Streams an image from the database.
     *
     * @param request The servlet request object
     * @param response the servlet response object
     * @param actx The request context
     */
    @Override
    public void dispatch(HttpServletRequest request,
            HttpServletResponse response,
            RequestContext actx)
            throws IOException, ServletException {

        OID oid = null;
        BigDecimal imageId = null;
        CachedImage cachedImage = null;
        String resizeParam = "";

        // Get URL parameters
        String widthParam = request.getParameter("width");
        String heightParam = request.getParameter("height");

        // Need the OID, but can work with imageId
        try {
            // Try to get OID and imageId, there should only be one not both
            oid = (OID) m_oid.transformValue(request);
            imageId = (BigDecimal) m_imageId.transformValue(request);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    e.toString());
            return;
        }
        // We can't handle both OID and imageId at the same time
        if ((imageId == null && oid == null) || (imageId != null && oid != null)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "either " + IMAGE_ID + " or " + OID_PARAM + " is required.");
            return;
        }
        // If the OID is still null
        if (oid == null) {
            // Get the OID from the imageID
            oid = new OID(ImageAsset.BASE_DATA_OBJECT_TYPE, imageId);
        }
        // Finally, we have a valid OID

        // Process URL parameter
        if (widthParam != null && heightParam != null) {
            try {

                // Set width
                if (!widthParam.isEmpty() && widthParam.matches("^[0-9]*$")) {
                    resizeParam += "&width=" + widthParam;
                }
            } catch (NumberFormatException numberEx) {
                s_log.warn("width parameter invalid " + widthParam);
            }

            try {

                // Set height
                if (!heightParam.isEmpty() && heightParam.matches("^[0-9]*$")) {
                    resizeParam += "&height=" + heightParam;
                }

            } catch (NumberFormatException numberEx) {
                s_log.warn("height parameter invalid " + heightParam);
            }
        }
        // Now, we have all information we need to proceed

        if (!resizeParam.isEmpty()) {

            // Try to get the CachedImage with the OID from the imageCache
            cachedImage = (CachedImage) s_imageCache.get(oid.toString() + resizeParam);

            // If cachedImage is still null, the resized version of this oid is 
            // not in the cache. So, we try to find the original version to 
            // avoid unnesseccary database access
            if (cachedImage == null) {

                // Get the original version
                cachedImage = (CachedImage) s_imageCache.get(oid.toString());

                // If cachedImage is still null, it is not in the imageCache
                if (cachedImage == null) {

                    // Get it from the database
                    cachedImage = this.getImageAssetFromDB(response, oid);

                    // If cachedImage is still null, we can't find the oid in the DB either
                    // There is something broken. Bail out.
                    if (cachedImage == null) {
                        return;
                    }

                    // Put the CachedImage into the imageCache
                    s_imageCache.put(oid.toString(), cachedImage);
                }

                // Create a resized version of the cachedImage 
                cachedImage = new CachedImage(cachedImage, resizeParam);

                // Put the CacheImageAsset into the imageCache
                s_imageCache.put(oid.toString() + resizeParam, cachedImage);
            }

        } else {

            // Try to get the CachedImage with the OID from the imageCache
            cachedImage = (CachedImage) (s_imageCache.get(oid.toString()));

            // If cachedImage is still null, it is not in the imageCache
            if (cachedImage == null) {

                // Get it from the database
                cachedImage = this.getImageAssetFromDB(response, oid);

                // If cachedImage is still null, we can't find the oid in the DB either
                // There is something broken. Bail out.
                if (cachedImage == null) {
                    return;
                }
            }

            // Put the CacheImageAsset into the imageCache
            s_imageCache.put(oid.toString(), cachedImage);
        }

        setHeaders(response, cachedImage);
        send(response, cachedImage);
    }

    private CachedImage getCachedImage(HttpServletResponse response, OID oid, String resizeParam) throws IOException {

        CachedImage cachedImage = null;

        if (s_imageCache != null) {
            cachedImage = (CachedImage) s_imageCache.get(oid.toString() + resizeParam);

            if (cachedImage == null) {

                if (!resizeParam.isEmpty()) {
                    cachedImage = (CachedImage) s_imageCache.get(oid.toString());

                    // Create a resized version of the cachedImage 
                    cachedImage = new CachedImage(cachedImage, resizeParam);

                    // Put the CacheImageAsset into the imageCache
                    s_imageCache.put(oid.toString() + resizeParam, cachedImage);
                }
            }
        } else {
            cachedImage = getImageAssetFromDB(response, oid);

            if (cachedImage != null && !resizeParam.isEmpty()) {
                cachedImage = new CachedImage(cachedImage, resizeParam);
            }
        }

        return cachedImage;
    }

    private CachedImage getImageAssetFromDB(HttpServletResponse response, OID oid) throws IOException {

        ImageAsset imageAsset = null;

        s_log.info(oid.toString() + " is not in imageCache. Fetching from database");

        // Try to get the Asset from database and test for ImageAsset
        try {
            Asset a = (Asset) DomainObjectFactory.newInstance(oid);

            if (a instanceof ImageAsset) {
                imageAsset = (ImageAsset) a;
            } else {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Asset " + oid + " is not an ImageAsset");
                }
            }
        } catch (DataObjectNotFoundException nfe) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "no ImageAsset with oid " + oid);
            return null;
        }

        return new CachedImage(imageAsset);
    }
}
