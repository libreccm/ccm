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
import com.arsdigita.cms.Asset;
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
    // the transactionID and objectID allow us to rollback to a specific
    // version of an image.  If we only have a transactionID and
    // the item is its own master then we can also roll it back
//    public static final String TRANSACTION_ID = "transID";
//    public static final String OBJECT_ID = "objectID";
    private BigDecimalParameter m_imageId;
    private OIDParameter m_oid;
//    private BigDecimalParameter m_transactionID;
//    private BigDecimalParameter m_objectID;
    private final boolean m_download;
    private String m_disposition;
    private static final Logger s_log =
            Logger.getLogger(BaseImage.class);

    /**
     * Construct the resource handler
     */
    public BaseImage(boolean download) {
        m_imageId = new BigDecimalParameter(IMAGE_ID);
        m_oid = new OIDParameter(OID_PARAM);
//        m_transactionID = new BigDecimalParameter(TRANSACTION_ID);
//        m_objectID = new BigDecimalParameter(OBJECT_ID);

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
            ImageAsset image) {
        String filename = image.getName();
        if (filename == null) {
            filename = s_defaultName;
        }

        // quote the file name to deal with any special
        // characters in the name of the file
        StringBuilder disposition = new StringBuilder(m_disposition);
        disposition.append('"').append(filename).append('"');

        response.setHeader("Content-Disposition", disposition.toString());
    }

    private void setHeaders(HttpServletResponse response,
            ImageAsset image) {
        setFilenameHeader(response, image);

        Long contentLength = new Long(image.getSize());
        response.setContentLength(contentLength.intValue());

        MimeType mimeType = image.getMimeType();

        if (m_download || mimeType == null) {
            // Section 19.5.1 of RFC2616 says this implies download
            // instead of view
            response.setContentType("application/octet-stream");
        } else {
            response.setContentType(mimeType.getMimeType());
        }

        // Default caching for all other types
        if ("live".equals(image.getVersion())) {
            DispatcherHelper.cacheForWorld(response);
        } else {
            DispatcherHelper.cacheDisable(response);
        }
    }

    private void send(HttpServletResponse response,
            ImageAsset image) throws IOException {
        // Stream the blob.
        OutputStream out = response.getOutputStream();
        try {
            image.writeBytes(out);
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

        // Fetch and validate the image ID
        OID oid = null;
        BigDecimal imageId = null;
//        BigDecimal transactionID = null;
//        BigDecimal objectID = null;
        try {
            oid = (OID) m_oid.transformValue(request);
            imageId = (BigDecimal) m_imageId.transformValue(request);
//            transactionID =
//                (BigDecimal) m_transactionID.transformValue(request);
//            objectID =
//                (BigDecimal) m_objectID.transformValue(request);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    e.toString());
            return;
        }
        if ((imageId == null && oid == null) || (imageId != null && oid != null)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "either " + IMAGE_ID + " or " + OID_PARAM + " is required.");
            return;
        }
        if (oid == null) {
            oid = new OID(ImageAsset.BASE_DATA_OBJECT_TYPE, imageId);
        }

//        Transaction transaction = null;
//        GenericArticle article = null;
        // XXX: add back rollback
        /*if (transactionID != null) {
        try {
        transaction =
        new Transaction(transactionID);
        // we have a transaction so let's see if we have an article
        if (objectID != null) {
        article = new GenericArticle(objectID);
        article.rollBackTo(transaction);
        }
        } catch (DataObjectNotFoundException e) {
        s_log.warn("Unable to locate transaction " + transactionID);
        // this is non-critical so we just continue
        }
        }*/

        ImageAsset image = null;
//        if (article == null) {
        try {
            Asset a = (Asset) DomainObjectFactory.newInstance(oid);

            if (a instanceof ImageAsset) {
                image = (ImageAsset) a;
            } else {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Asset " + oid + " is not an ImageAsset");
                }
            }
        } catch (DataObjectNotFoundException nfe) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "no ImageAsset with oid " + oid);
            return;
        }
//        }

//        if (image.getMimeType() == null) {
//            response.sendError(HttpServletResponse.SC_NOT_FOUND,
//                    "MIME type not found for ImageAsset " + imageId);
//        }

        // Not until permissions are properly assigned to assets
        //checkUserAccess(request, response, actx, image);

//        response.setContentType(image.getMimeType().getMimeType());

/* Quasimodo: on demand resizing of images
        int width;
        int height;
        
        width = Integer.parseInt(request.getParameter("width"));
        height = Integer.parseInt(request.getParameter("height"));
        
        if(width || height) {
            
        }
*/
        
        setHeaders(response, image);
        send(response, image);
    }
}
