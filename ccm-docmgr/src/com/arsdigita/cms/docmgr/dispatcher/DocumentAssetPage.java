/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.dispatcher;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.util.Assert;


/**
 * A resource handler which streams out a blob from the database.
 *
 * @author Crag Wolfe
 * @version $Revision: #1 $ $DateTime: 2003/08/18 23:54:14 $
 */
public class DocumentAssetPage extends CMSPage {

    private static final Logger s_log =
        Logger.getLogger(DocumentAssetPage.class);

    public final static String ASSET_ID = "asset_id";

    private BigDecimalParameter m_asset_id;


    /**
     * Construct the resource handler
     */
    public DocumentAssetPage() {
        m_asset_id = new BigDecimalParameter(ASSET_ID);
        m_asset_id.addParameterListener(new NotNullValidationListener());
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

        ContentItem item = com.arsdigita.cms.CMS.getContext().getContentItem();
        Document doc = null;
        try {
            doc = (Document) DomainObjectFactory.newInstance(item.getOID());
        } catch (DataObjectNotFoundException e) {
            s_log.error("Data Object Not Found for id " + item.getOID(), e);
            throw new ServletException(e.getMessage());
        }

        Assert.isTrue(doc instanceof Document,
                     "document is not a document" +
                     doc.getID().toString());

        FileAsset docAsset = doc.getFile();
        Assert.exists(docAsset, FileAsset.class);

        // Set the content type of the response to the MIME type of the image.
        MimeType mime = docAsset.getMimeType();
        if ( mime == null ) {
            throw new ServletException
                ("Could not fetch MIME type of document: " +
                 docAsset.getID().toString());
        }
        response.setContentType(mime.getMimeType());
        //DispatcherHelper.cacheForWorld(response);

        // Stream the blob.
        OutputStream out = response.getOutputStream();
        try {
            docAsset.writeBytes(out);
        } finally {
            out.close();
        }
    }
}
