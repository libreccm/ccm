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
package com.arsdigita.docrepo.ui;

import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.docrepo.Repository;
import com.arsdigita.docrepo.File;
//import com.arsdigita.docrepo.ResourceImpl;
import com.arsdigita.docrepo.ResourceImplCollection;
//import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataObject;
//import com.arsdigita.persistence.DataQuery;
//import com.arsdigita.persistence.Session;
//import com.arsdigita.persistence.SessionManager;
import com.arsdigita.web.Application;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.xml.Element;
import org.apache.log4j.Category;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * Portlet showing the n most recently updated documents of a
 * repository .
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 * @version $Id: RecentUpdatedDocsPortlet.java  pboy $
 */
public class RecentUpdatedDocsPortlet extends AppPortlet {

    private static Category s_log = Category.getInstance
        (RecentUpdatedDocsPortlet.class.getName());

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.docrepo.ui.RecentUpdatedDocsPortlet";

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public RecentUpdatedDocsPortlet(DataObject dataObject) {
        super(dataObject);
    }

    @Override
    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new RecentUpdatedDocsPortletRenderer(this);
    }
}

class RecentUpdatedDocsPortletRenderer extends AbstractPortletRenderer
    implements DRConstants {
    private RecentUpdatedDocsPortlet m_portlet;

    public RecentUpdatedDocsPortletRenderer
        (RecentUpdatedDocsPortlet docsPortlet) {

        m_portlet = docsPortlet;
    }


    /**
     * 
     * 
     * @param pageState
     * @param parentElement
     */
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
                (DRUtils.FileSize.formatFileSize(fileSize, pageState));

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
	files.close();
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
