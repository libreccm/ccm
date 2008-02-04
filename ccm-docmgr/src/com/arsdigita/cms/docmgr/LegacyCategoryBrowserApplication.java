/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.cms.docmgr;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;

/**
 * browse items by category within a given toplevel (legacy) folder.
 *
 * @author Crag Wolfe
 */

public class LegacyCategoryBrowserApplication extends Application {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.docmgr.LegacyCategoryBrowserApplication";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    private static Logger s_log =
        Logger.getLogger(LegacyCategoryBrowserApplication.class);

    // pdl constants
    private static final String OWNER = "ownerID";
    private static final String ROOT  = "rootID";

    private DocFolder m_root = null;

    /**
     * Retreives a repository from the database usings its OID.
     *
     * @param oid the OID of the repository
     */
    public LegacyCategoryBrowserApplication(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     */
    public LegacyCategoryBrowserApplication(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructs a repository from the underlying data object.
     */
    public LegacyCategoryBrowserApplication(DataObject obj) {
        super(obj);

    }

    /**
     * This is called when the application is created.
     */
    public static LegacyCategoryBrowserApplication create(String urlName,
                                    String title,
                                    Application parent) {
        LegacyCategoryBrowserApplication app = 
            (LegacyCategoryBrowserApplication) Application.createApplication
            (BASE_DATA_OBJECT_TYPE, urlName, title, parent);

        app.save();

        return app;
    }

    /**
     * Sets the display name of the app.
     */
    //private void setName(String name) {
    //    //setTitle(name);
    //    set("name", name);
    //}


}
