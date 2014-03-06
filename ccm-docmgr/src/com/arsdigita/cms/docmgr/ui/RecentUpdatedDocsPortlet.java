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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.arsdigita.bebop.ExternalLink;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.cms.docmgr.DocLink;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.cms.docmgr.Repository;
import com.arsdigita.cms.docmgr.Resource;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portal.apportlet.AppPortlet;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

/**
 * Portlet showing the n most recently updated documents of a
 * portal.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 * @version $Id: RecentUpdatedDocsPortlet.java,v 1.5 2005/12/08 14:46:55 pkopunec Exp $
 */
public class RecentUpdatedDocsPortlet extends AppPortlet {

	public static final String BASE_DATA_OBJECT_TYPE =
                         "com.arsdigita.cms.docmgr.ui.RecentUpdatedDocsPortlet";

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

class RecentUpdatedDocsPortletRenderer extends AbstractPortletRenderer implements DMConstants {
    private RecentUpdatedDocsPortlet m_portlet;

	public RecentUpdatedDocsPortletRenderer(RecentUpdatedDocsPortlet docsPortlet) {

        m_portlet = docsPortlet;
    }

	protected void generateBodyXML(PageState pageState, Element parentElement) {

        Application application  = m_portlet.getParentApplication();

        String fileURL = application.getPath();
        Repository rep = (Repository) application;
        HttpServletRequest req = pageState.getRequest();

		// Table with 6 columns
		String[] tableHeaders = { "File", "Type", "Size", "Author", "Date", "" };

		DataQuery files = SessionManager.getSession().retrieveQuery(
                                  "com.arsdigita.cms.docmgr.ui.RecentUpdatedDocs");
		files.setParameter("ancestors", "%/" + rep.getRoot().getID().toString() + "/%");
		files.setParameter("maxRows", new Integer(10));

		ArrayList tableDataList = new ArrayList();
		SimpleDateFormat dft = new SimpleDateFormat();
		while (files.next()) {
			Object[] tableRow = new Object[6];

			Resource res = (Resource) DomainObjectFactory.newInstance(
                                    new OID((String) files.get("objectType"),
                                                     files.get("docID")));
			Document document = null;
			DocLink docLink = null;
			boolean isExternalLink = false;
			if (res instanceof DocLink) {
				docLink = (DocLink) res;
				isExternalLink = docLink.isExternal();
				if (!isExternalLink) {
					document = docLink.getTarget();
				}
			}
			else {
				document = (Document) res;
			}
			if (isExternalLink) {
				tableRow[0] = new ExternalLink(
                                                  res.getTitle(),
                                                  docLink.getExternalURL());
				tableRow[1] = "Link";
				tableRow[2] = "";
				tableRow[3] = "";
				// Date column
				Date date = docLink.getLastModifiedLocal();
				if (date != null) {
					tableRow[4] = dft.format(date);
				}
				else {
					tableRow[4] = "";
				}
				tableRow[5] = new ExternalLink(
                                                  "download",
                                                  docLink.getExternalURL());
			}
			else {
				// File name column.
				tableRow[0] = new Link(
                                              document.getTitle(),
                                              fileURL + "/?" + FILE_ID_PARAM_NAME +
                                              "=" + document.getID());
				tableRow[1] = document.getPrettyMimeType();

				long fileSize = document.getSize().longValue();
				// File size column
				tableRow[2] = DMUtils.FileSize.formatFileSize(fileSize);
				// Author column
				tableRow[3] = document.getImpliedAuthor();
				// Date column
				Date date = document.getLastModifiedLocal();
				if (date != null) {
					tableRow[4] = dft.format(date);
				}
				else {
					tableRow[4] = "";
				}
				// Download column
				Link link = new Link("Download", fileURL +
                                                     "/download/?" +
                                                     FILE_ID_PARAM_NAME + "=" +
                                                     document.getID().toString());
				//+ resource.getResourceID());
				link.setClassAttr("downloadLink");
				tableRow[5] = link;
        }

			tableDataList.add(tableRow);
		}
		files.close();

		GridPanel panel = new GridPanel(1);
		addResourceLinks(panel, rep);
		if (tableDataList.isEmpty()) {
			panel.add(new Label(REPOSITORY_RECENTDOCS_EMPTY.
                                            localize(req).toString()));
            panel.generateXML(pageState, parentElement);
            return;
        }
		else {
			Object[][] tableData = (Object[][]) tableDataList.
                                                toArray(new Object[0][0]);
			Table table = new Table(tableData, tableHeaders);
        panel.add(table, GridPanel.FULL_WIDTH);
        panel.generateXML(pageState, parentElement);
    }
	}

	private void addResourceLinks(GridPanel panel, Repository rep) {
        User user = Web.getWebContext().getUser();

		if (!PermissionService.checkPermission(new PermissionDescriptor(
                             PrivilegeDescriptor.CREATE, rep, user))) {
			// don't show resource links
            return;
        }
		// new document
		Link addResourceLink = new Link(new Label(ROOT_ADD_RESOURCE_LINK),
                                m_portlet.getParentApplication().getPath() + "?"
				+ ROOT_ADD_DOC_PARAM.getName() + "=t");
        addResourceLink.setClassAttr("actionLink");

		panel.add(addResourceLink, GridPanel.FULL_WIDTH | GridPanel.RIGHT | GridPanel.BOTTOM);

		// new doclink
		addResourceLink = new Link(new Label(
                                      ROOT_ADD_DOCLINK_LINK),
                                      m_portlet.getParentApplication().getPath()
                                      + "?" + PARAM_ROOT_ADD_DOC_LINK + "=");
		addResourceLink.setClassAttr("actionLink");

		panel.add(addResourceLink, GridPanel.FULL_WIDTH | GridPanel.RIGHT | GridPanel.BOTTOM);
    }
}
