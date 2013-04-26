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
import com.arsdigita.cms.CachedImage;
import com.arsdigita.cms.ImageAsset;
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
 * This class can use a special image cache to speed up image dispatching. Also,
 * during dispatch this class will create server-side resized images depending
 * on the URL parameter. Resizing is done by ImageScalr. The image cache can be
 * activated and configured by com.arsdigita.cms.image_cache.* parameters.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Sören Bernstein <sbernstein@zes.uni-bremen.de>
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
        if (CMS.getConfig().getImageCacheEnabled()) {
            s_imageCache = new CacheTable("BaseImageCache",
                    CMS.getConfig().getImageCacheMaxAge(),
                    CMS.getConfig().getImageCacheMaxSize());
        }
    }
    private final boolean IMAGE_CACHE_PREFETCH = CMS.getConfig().getImageCachePrefetchEnabled();
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
     * Sets RFC2183 governed Content-Disposition header to supply filename to
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
     * @param request  The servlet request object
     * @param response the servlet response object
     * @param actx     The request context
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
        String maxWidthParam = request.getParameter("maxWidth");
        String maxHeightParam = request.getParameter("maxHeight");

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
        if (maxWidthParam != null && maxHeightParam != null) {
            try {

                // Set width if supplied by URL parameter
                if (!maxWidthParam.isEmpty() && maxWidthParam.matches("^[0-9]*$")) {
                    resizeParam += "&maxWidth=" + maxWidthParam;
                }
            } catch (NumberFormatException numberEx) {
                s_log.warn("maxWidth parameter invalid " + maxWidthParam);
            }

            try {

                // Set height if supplied by URL parameter
                if (!maxHeightParam.isEmpty() && maxHeightParam.matches("^[0-9]*$")) {
                    resizeParam += "&maxHeight=" + maxHeightParam;
                }

            } catch (NumberFormatException numberEx) {
                s_log.warn("maxHeight parameter invalid " + maxHeightParam);
            }
        }
        // Now, we have all information we need to proceed

        // Get the image
        cachedImage = this.getImage(response, oid, resizeParam);
        if (cachedImage == null) {
            // ok, something is really weird now. Can't find image with this oid. Bailing out.
            return;
        }

        setHeaders(response, cachedImage);
        send(response, cachedImage);
    }

    private CachedImage getImage(HttpServletResponse response, OID oid, String resizeParam) throws IOException {

        CachedImage cachedImage = null;

        // Test for cache
        if (s_imageCache != null) {

            // Image cache is enabled, try to fetch images from cache
            cachedImage = getImageFromCache(response, oid, resizeParam);

        } else {

            // Image cache is disabled
            // Get the original image from db
            cachedImage = getImageFromDB(response, oid);
            if (cachedImage != null && !resizeParam.isEmpty()) {
                cachedImage = new CachedImage(cachedImage, resizeParam);
            }
        }

        return cachedImage;
    }

    /**
     * Fetches the {@link CachedImage} from the image cache. If tge object
     * could not be found in the cache, this method falls back to
     * {@link #getImageFromDB(javax.servlet.http.HttpServletResponse, com.arsdigita.persistence.OID)}.
     * This method will also store the CachedImage in the image cache for future
     * use.
     *
     * @param response The HttpServletResponse
     * @param oid the {@link OID} of the wanted object
     * @param resizeParam the resize paramters of the wanted object
     * @return the wanted {@link CachedImage} in the correct size or null, if the object could not be found
     * @throws IOException
     */
    private CachedImage getImageFromCache(HttpServletResponse response, OID oid, String resizeParam) throws IOException {
        CachedImage cachedImage;

        cachedImage = (CachedImage) s_imageCache.get(oid.toString() + resizeParam);

        // If we coundn't find the specific version
        if (cachedImage == null) {

            // If we were looking for a resized version
            if (!resizeParam.isEmpty()) {

                // try to find the original version in the cache by recursion
                cachedImage = this.getImageFromCache(response, oid, "");

                if (cachedImage != null) {
                    cachedImage = new CachedImage(cachedImage, resizeParam);
                    s_imageCache.put(oid.toString() + resizeParam, cachedImage);
                }
            } else {

                // look for the original version in the database
                cachedImage = getImageFromDB(response, oid);

                // If we found the image, put it into the image cache
                if (cachedImage != null && IMAGE_CACHE_PREFETCH) {
                    s_imageCache.put(oid.toString(), cachedImage);
                }
            }
        }
        return cachedImage;
    }

    /**
     * Fetches the {@link ImageAsset} with the supplied {@link OID} from the database
     * and converts it to a {@CachedImage}.
     *
     * @param response the HttpServletResponse
     * @param oid the {@link OID} to the ImageAsset
     * @return the ImageAsset with the oid as CachedImage or null, if not found
     * @throws IOException
     */
    private CachedImage getImageFromDB(HttpServletResponse response, OID oid) throws IOException {

        ImageAsset imageAsset = null;

        s_log.info(oid.toString() + " is not in imageCache. Fetching from database");

        // Try to get the Asset from database and test for ImageAsset
        try {
            Asset a = (Asset) DomainObjectFactory.newInstance(oid);

            // Make sure we have an ImageAsset
            if (a instanceof ImageAsset) {
                imageAsset = (ImageAsset) a;
            } else {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Asset " + oid + " is not an ImageAsset");
                }
            }
        } catch (DataObjectNotFoundException nfe) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "no ImageAsset with oid " + oid);
            return null;
        }

        return new CachedImage(imageAsset);
    }
}
