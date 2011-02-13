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

import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.BinaryAsset;
import com.arsdigita.cms.Asset;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.OID;
import com.arsdigita.toolbox.ui.OIDParameter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
 *
 * @version $Id: BaseAsset.java 1967 2009-08-29 21:05:51Z pboy $
 */
class BaseAsset extends ResourceHandlerImpl {

    private static final Logger s_log = Logger.getLogger(BaseAsset.class);

    public final static String ASSET_ID = "asset_id";
    public static final String OID_PARAM = "oid";

    private final static String s_defaultName = "File";

    private static final BigDecimalParameter s_assetId = new BigDecimalParameter(ASSET_ID);
    private static final OIDParameter s_oid = new OIDParameter(OID_PARAM);

    /*
     * jensp 2011-02-11: No need for static initalizer block here. Moved
     * to variable declaration (see above).
     */
    /*static {        
        s_assetId = new BigDecimalParameter(ASSET_ID);
        s_oid = new OIDParameter(OID_PARAM);
        //s_assetId.addParameterListener(new NotNullValidationListener());        
    }*/

    private final boolean m_download;
    private String m_disposition;

    protected BaseAsset(boolean download) {
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
                                   BinaryAsset asset) {
        String filename = asset.getName();
        if (filename == null) { filename = s_defaultName; }


        // quote the file name to deal with any special 
        // characters in the name of the file
        StringBuffer disposition = new StringBuffer(m_disposition);
        disposition.append('"').append(filename).append('"');

        response.setHeader("Content-Disposition", disposition.toString());
    }

    private void setHeaders(HttpServletResponse response,
                              BinaryAsset asset) {
        setFilenameHeader(response, asset);

        Long contentLength = new Long(asset.getSize());
        response.setContentLength(contentLength.intValue());

        MimeType mimeType = asset.getMimeType();

        if (m_download || mimeType == null) {
            // Section 19.5.1 of RFC2616 says this implies download
            // instead of view
            response.setContentType("application/octet-stream");
        } else {
            response.setContentType(mimeType.getMimeType());
        }

        // PDFs need to be cached for a different amount of time to avoid issues with IE6 - see ticket #20266
        if (mimeType != null && mimeType.getMimeType().equals("application/pdf")) {
            DispatcherHelper.cacheForWorld(response,30);
        } else {
            // Default caching for all other types
            DispatcherHelper.cacheForWorld(response);
        }
    }

    private void send(HttpServletResponse response,
                        BinaryAsset asset) throws IOException {
        // Stream the blob.
        OutputStream out = response.getOutputStream();
        try {
            asset.writeBytes(out);
        } finally {
            out.close();
        }
    }

    public final void dispatch(HttpServletRequest request,
                               HttpServletResponse response,
                               RequestContext actx) 
        throws IOException, ServletException {

        // Fetch and validate the asset ID
        OID oid = null;
        BigDecimal assetId = null;
        try {
            oid = (OID)s_oid.transformValue(request);
            assetId = (BigDecimal) s_assetId.transformValue(request);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    e.toString());
            return;
        }
        if ( assetId == null && oid == null ) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "either " + ASSET_ID + " or " + OID_PARAM + " is required.");
            return;
        } else if ( assetId != null && oid != null ) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "either " + ASSET_ID + " or " + OID_PARAM + " is required.");
            return;
        }
        if (oid == null) {
            oid = new OID(Asset.BASE_DATA_OBJECT_TYPE, assetId);
        }

        BinaryAsset asset = null;
        try {
            Asset a = (Asset)
                DomainObjectFactory.newInstance(oid);

            if (a instanceof BinaryAsset) {
                asset = (BinaryAsset) a;
            } else {
                if (s_log.isInfoEnabled()) {
                    s_log.info("Asset " + oid + " is not a BinaryAsset");
                }
            }
            // Not until permissions are properly assigned to assets
            //checkUserAccess(request, response, actx, asset);
        } catch (DataObjectNotFoundException nfe) {
            if (s_log.isInfoEnabled()) {
                s_log.info("no asset with oid " + oid, nfe);
            }
        }

        if (asset == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                               "No asset with ID " + assetId);
            return;
        }

        setHeaders(response, asset);
        send(response, asset);
    }
}
