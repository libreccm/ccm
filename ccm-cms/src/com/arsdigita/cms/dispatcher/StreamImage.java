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
import com.arsdigita.cms.contenttypes.GenericArticle;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ImageAssetCollection;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;
import com.arsdigita.versioning.Transaction;

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
 * @version $Id: StreamImage.java 1571 2007-04-20 15:57:54Z apevec $
 */
public class StreamImage extends ResourceHandlerImpl {

    public static final String IMAGE_ID = "image_id";
    public static final String OID_PARAM = "oid";

    // the transactionID and objectID allow us to rollback to a specific
    // version of an image.  If we only have a transactionID and
    // the item is its own master then we can also roll it back
    public static final String TRANSACTION_ID = "transID";
    public static final String OBJECT_ID = "objectID";

    private BigDecimalParameter m_imageId;
    private OIDParameter m_oid;
    private BigDecimalParameter m_transactionID;
    private BigDecimalParameter m_objectID;

    private static final Logger s_log =
        Logger.getLogger(StreamImage.class);

    /**
     * Construct the resource handler
     */
    public StreamImage() {
        m_imageId = new BigDecimalParameter(IMAGE_ID);
        m_oid = new OIDParameter(OID_PARAM);
        m_transactionID = new BigDecimalParameter(TRANSACTION_ID);
        m_objectID = new BigDecimalParameter(OBJECT_ID);
    }

    /**
     * Streams an image from the database.
     *
     * @param request The servlet request object
     * @param response the servlet response object
     * @param actx The request context
     */
    public void dispatch(HttpServletRequest request,
                         HttpServletResponse response,
                         RequestContext actx)
        throws IOException, ServletException {

        // Fetch and validate the image ID
        OID oid = null;
        BigDecimal imageId = null;
        BigDecimal transactionID = null;
        BigDecimal objectID = null;
        try {
            oid = (OID)m_oid.transformValue(request);
            imageId = (BigDecimal) m_imageId.transformValue(request);
            transactionID =
                (BigDecimal) m_transactionID.transformValue(request);
            objectID =
                (BigDecimal) m_objectID.transformValue(request);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    e.toString());
            return;
        }
        if ( imageId == null && oid == null ) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "either " + IMAGE_ID + " or " + OID_PARAM + " is required.");
            return;
        } else if ( imageId != null && oid != null ) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "either " + IMAGE_ID + " or " + OID_PARAM + " is required.");
            return;
        }
        if (oid == null) {
            oid = new OID(ImageAsset.BASE_DATA_OBJECT_TYPE, imageId);
        }
        Transaction transaction = null;
        GenericArticle article = null;
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
        String mimeType = null;

        if (article == null) {
            try {
                image = (ImageAsset) DomainObjectFactory.newInstance(oid);

            } catch (DataObjectNotFoundException nfe) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "no ImageAsset with oid " + oid);
                return;
            }

        } else {
            ImageAssetCollection col = article.getImages();
            col.addEqualsFilter(ACSObject.ID, imageId);
            if (col.next()) {
                image = col.getImage();
                col.close();
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "failed to retrieve ImageAsset " + imageId);
                return;
            }

        }


        if ( image.getMimeType() == null ) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "MIME type not found for ImageAsset " + imageId);
        }

        if ("live".equals(image.getVersion())) {
            DispatcherHelper.cacheForWorld(response);
        } else {
            DispatcherHelper.cacheDisable( response );
        }

        // Not until permissions are properly assigned to assets
        //checkUserAccess(request, response, actx, image);

        response.setContentType(image.getMimeType().getMimeType());
        // Stream the blob.
        OutputStream out = response.getOutputStream();
        image.writeBytes(out);
        out.close();
    }
}
