/*
 * Copyright (C) 2010 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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

import com.arsdigita.cms.dispatcher.StreamAsset;
import com.arsdigita.cms.dispatcher.StreamImage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;

import com.arsdigita.web.URL;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

//  WORK IN PROGRESS !!
//  Currentliy just a minimal implementation ready to switch cms-service from an
//  old style package-type application to a new style legacy compatible
//  application based an classes web.Application etc.

/**
 * Application domain class for the CMS Service application, a CMS module which
 * is used by the Content Management System as a store for global resources
 * and assets.
 *
 * @author pb
 * @version $Id: Service.java $
 */
public class Service extends Application {

    /** Logger instance for debugging  */
    private static final Logger s_log = Logger.getLogger(Service.class);

    // pdl stuff (constants)
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.Service";

    // general constants
    public static final String PRIMARY_URL_STUB = "cms-service";
 // public static final String DISPATCHER_CLASS =
 //                            "com.arsdigita.cms.dispatcher.ServiceDispatcher";
    // Service has no direct user interface, therefore no styesheet
    // public final static String STYLESHEET =
    //                         "/packages/content-section/xsl/content-center.xsl";

    /**
     * Constructor retrieving service from the database usings its OID.
     *
     * @param oid the OID of the service (cms-service)
     * @throws DataObjectNotFoundException
     */
    public Service(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor retrieving the contained <code>DataObject</code> from the
     * persistent storage mechanism with an <code>OID</code> specified by id.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     */
    public Service(BigDecimal key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    /**
     * Constructs a service domain object from the underlying data object.
     */
    public Service(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Getter to retrieve the base database object type name
     *
     * @return base data aoject type as String
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    public static String getURL() {
        return "/" + PRIMARY_URL_STUB + "/";
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
        StringBuilder buf = new StringBuilder(Service.getURL() );
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
        StringBuilder buf = new StringBuilder(Service.getURL() );
        buf.append("stream/image/?");
        buf.append(StreamImage.IMAGE_ID).append("=").append(asset.getID());
        return buf.toString();
    }

    /**
     * The URL to log out.
     * @return The logout URL
     */
    public static String getLogoutURL() {
        StringBuilder buf = new StringBuilder(Service.getURL() );
        buf.append("logout");
        return buf.toString();
    }

    /**
     * This is called when the application is created.
     */
    public static Service create(String urlName,
                                 String title,
                                 Application parent) {

        Service app = (Service) Application.createApplication
                                (BASE_DATA_OBJECT_TYPE, urlName, title, parent);

        app.save();

        return app;
    }

    @Override
    public String getServletPath() {
        return URL.SERVLET_DIR + "/cms-service";
    }


}
