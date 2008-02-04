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

package com.arsdigita.docmgr.ui;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Category;

import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.docmgr.File;
import com.arsdigita.docmgr.Repository;
import com.arsdigita.docmgr.ResourceImplCollection;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.web.Application;
import com.arsdigita.xml.Element;

/**
 * Portlet showing the n most recently updated documents of a
 * portal.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
public class RecentUpdatedDocsPortlet extends AppPortlet {
    public static final String versionId =
        "$Id: //apps/docmgr/dev/src/com/arsdigita/docmgr/ui/RecentUpdatedDocsPortlet.java#6 $" +
        "$Author: jparsons $" +
        "$DateTime: 2003/07/11 17:45:09 $";

    private static Category s_log = Category.getInstance
        (RecentUpdatedDocsPortlet.class.getName());

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.docs.ui.RecentUpdatedDocsPortlet";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public RecentUpdatedDocsPortlet(DataObject dataObject) {
        super(dataObject);
    }

    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new RecentUpdatedDocsPortletRenderer(this);
    }
}

class RecentUpdatedDocsPortletRenderer extends AbstractPortletRenderer
    implements DMConstants {
    private RecentUpdatedDocsPortlet m_portlet;

    public RecentUpdatedDocsPortletRenderer
        (RecentUpdatedDocsPortlet docsPortlet) {

        m_portlet = docsPortlet;
    }


    protected void generateBodyXML(PageState pageState,
                                   Element parentElement) {

        Application application  = m_portlet.getParentApplication();

        String fileURL = application.getPath();
        Repository rep = (Repository) application;
        HttpServletRequest req = pageState.getRequest();

        ResourceImplCollection files = 
          Repository.getRecentlyModifiedDocuments(rep);


        GridPanel panel;

        // Table with 5 columns
        String[] tableHeaders = {
            "File",
            "Type",
            "Size",
            "Author",
            "Date",
            ""
        };


        // Determine number of rows
        int size = 0;
        int maxDocs = (int)files.size();
        int maxCount = 10;
        if ( maxCount < maxDocs ) {
            size = maxCount;
        } else {
            size = maxDocs;
        }

        // No documents.
        if (size == 0 ) {
            panel = new GridPanel(1);
            addResourceLink(panel);
            panel.add
                (new Label(REPOSITORY_RECENTDOCS_EMPTY.localize(req).toString()));
            panel.generateXML(pageState, parentElement);
            return;
        }

        // We have documents.  Present them in the table.
        panel = new GridPanel(1);
        addResourceLink(panel);
        Object tableData[][] = new Object[size][6];
        for ( int i = 0; i < size && files.next(); i++) {
            File resource = (File)files.getDomainObject();
            // File name column.
            tableData[i][0] = new Link((String) resource.getName(),
                                       fileURL+"/file?" +FILE_ID_PARAM.getName() +
                                       "=" + resource.getResourceID());

            // File type column
            tableData[i][1] = 
             new Label((String) resource.getPrettyContentType());

            long fileSize = ((BigDecimal)resource.getSize()).longValue();

            // File size column
            tableData[i][2] = new Label
                (DMUtils.FileSize.formatFileSize(fileSize, pageState));

            // Author column
            User user = resource.getLastModifiedUser();
            String author = null;
            if (null != user) {
                    author = user.getPersonName().toString();
            }

            if (null == author) {
                author = "Unknown";
            }

            tableData[i][3] = new Label(author);

            // Date column
            java.util.Date date = resource.getLastModifiedDate();
            SimpleDateFormat dft = new SimpleDateFormat();
            String textdate = dft.format(date);
            tableData[i][4] = new Label(textdate);

            // Download column
            Link link = new Link("Download",
                                 fileURL + "/download/" + resource.getName() + "?" +
                                 FILE_ID_PARAM.getName() + "=" 
                                 + resource.getResourceID());
            link.setClassAttr("downloadLink");
            tableData[i][5] = link;
        }
        Table table = new Table(tableData, tableHeaders);
        panel.add(table, GridPanel.FULL_WIDTH);
        panel.generateXML(pageState, parentElement);
    }


    private void addResourceLink(GridPanel panel) {
        Link addResourceLink =
            new Link(new Label(ROOT_ADD_RESOURCE_LINK),
                     m_portlet.getParentApplication().getPath() +
                     "?"+ROOT_ADD_DOC_PARAM.getName()+"="+"t");
        addResourceLink.setClassAttr("actionLink");

        panel.add(addResourceLink,
                  GridPanel.FULL_WIDTH | GridPanel.RIGHT | GridPanel.BOTTOM);
    }
}
