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

package com.arsdigita.cms.docmgr.ui;

import java.util.Date;

import org.apache.log4j.Category;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.cms.docmgr.DocMgr;
import com.arsdigita.cms.docmgr.LegacyCategoryBrowserApplication;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.UniversalPermissionDescriptor;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.toolbox.ui.DataQueryBuilder;
import com.arsdigita.toolbox.ui.DataTable;
import com.arsdigita.web.Web;

/**
 * Iterates through all the children of the given Category
 */
public class CategoryDocModelBuilder
    implements DataQueryBuilder, DMConstants {

    private ACSObjectSelectionModel m_sel;
    private String m_context;

    private static final String DOC_ID ="docID";
    private static final String DOCS_IN_CAT_QUERY =
        "com.arsdigita.cms.docmgr.docsInCategory";

    protected static Category s_log = Category.getInstance
        (CategoryDocsNavigatorPortlet.class.getName());

    private final DateParameter m_startDateParameter = new DateParameter(START_DATE_PARAM_NAME);
    private final DateParameter m_endDateParameter = new DateParameter(END_DATE_PARAM_NAME);

    public CategoryDocModelBuilder(ACSObjectSelectionModel sel,
                                        String context) {
        m_sel = sel;
        m_context = context;
    }

    public void lock() {
        // lock
    }
    public boolean isLocked() {
        return false;
    }

    public DataQuery makeDataQuery(DataTable t, PageState s) {
        //Category cat = (Category)m_sel.getSelectedObject(s);

        DataQuery dq = SessionManager.getSession().
            retrieveQuery(DOCS_IN_CAT_QUERY);
        dq.setParameter("categoryID",
                        m_sel.getSelectedKey(s));
        dq.setParameter("context", m_context);

        if (Web.getWebContext().getApplication().getDefaultDomainClass()
            .equals(LegacyCategoryBrowserApplication.
                    BASE_DATA_OBJECT_TYPE)) {
            s_log.debug("contains legacy folder id: "+
                        DocMgr.getConfig().getLegacyFolderID());
            dq.addFilter(dq.getFilterFactory().contains
                ("ancestors", 
                 "/"+DocMgr.getConfig().getLegacyFolderID()+"/",
                 false));
        } else {
            s_log.debug("NOT contains legacy folder id");
            dq.addFilter(dq.getFilterFactory().simple
                         ("ancestors not like :ancestors").
                         set("ancestors",
                             "%/"+DocMgr.getConfig().getLegacyFolderID()+"/%"
                             )
                         );
        }

        Date startDate = (Date) m_startDateParameter.transformValue(s.getRequest());
        Date endDate = (Date) m_endDateParameter.transformValue(s.getRequest());
        s_log.debug("makeDataQuery");
        if (startDate != null) {
            dq.addFilter(dq.getFilterFactory().greaterThan("lastModifiedDate",
                                              startDate, false));
            s_log.debug("startDate is "+startDate.toString());
        }
        if (endDate != null) {
            dq.addFilter(dq.getFilterFactory().lessThan("lastModifiedDate",
                                              endDate, false));
            s_log.debug("endDate is "+endDate.toString());
        }


        User u = Web.getWebContext().getUser();
        OID uOID = null;
        if (u == null) {
            uOID = new OID("com.arsdigita.kernel.User", -200);
        } else {
            uOID = u.getOID();
        }
        if (! PermissionService.checkPermission
            (new UniversalPermissionDescriptor(PrivilegeDescriptor.READ,
                                               u))) {
            PermissionService.filterQuery
                (dq, "docID", PrivilegeDescriptor.READ, uOID);
        }

        return dq;
    }

    public String getKeyColumn() {
        return DOC_ID;
    }
}
