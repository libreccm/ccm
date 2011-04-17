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


import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.page.BebopMapDispatcher;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.docrepo.File;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.web.Web;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Category;

import java.io.*;
import java.math.BigDecimal;

/**
 * Dispatcher for document repository application.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 * @version $Id: DRDispatcher.java  pboy $
 */

public class DRDispatcher extends BebopMapDispatcher implements DRConstants {

    private static Category s_log = Category.getInstance
            (DRDispatcher.class.getName());

    /**
     * Default constructor instantiating the URL-page map.
     */
    public DRDispatcher() {
        addPage("", buildDMIndexPage(), true);
        addPage("file", buildFileInfoPage());
    }

    /**
     * Build index page for the document repository,
     */

    private Page buildDMIndexPage() {

        Page p = new DocrepoBasePage();

        /**
         * Create main administration tab.
         */
        TabbedPane tb = new TabbedPane();
        tb.setIdAttr("page-body");

        //tb.addTab(WS_BROWSE_TITLE, new BrowsePane());

        /*
        * Disable Repositories tab because
        * Still need to decide what to do with mounting
        * repository, since repository are now application.
        *
        tb.addTab(WS_REPOSITORIES_TITLE, new RepositoryPane());
        */

        p.add(new BrowsePane());
        p.lock();

        return p;
    }


    /**
     * Build page for the administration of one file.
     * (Implementation according to wireframes at)
     */
    private Page buildFileInfoPage() {

        DocrepoBasePage p = new DocrepoBasePage() {
            // need to override this to show the File name
            @Override
            protected void buildTitle() {
                Label title = new Label();
                title.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState state = e.getPageState();
                        Label t = (Label) e.getTarget();
                        BigDecimal fid =
                                (BigDecimal) state.getValue(FILE_ID_PARAM);
                        if (fid!=null) {
                            t.setLabel
                                    (DRUtils.getFile(fid).getName());
                        }
                    }
                });
                setTitle(title);
            }
        };

        /* Temporary fix to sdm #204233, NavBar of Application allows only
        one URL per application, so here we add a Link back to the parent folder
        */
        Label backLinkLabel = GO_BACK_LABEL;
        backLinkLabel.addPrintListener(new PrintListener() {
            public void prepare(PrintEvent e) {
                PageState state = e.getPageState();

                Label t= (Label) e.getTarget();
                String fixed = t.getLabel(e.getPageState());
                String url = Web.getContext().getApplication().getTitle();

                t.setLabel(fixed + " " + url);
            }});
        ActionLink backLink = new ActionLink(backLinkLabel);
        backLink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PageState state = e.getPageState();
                String url = Web.getContext().getApplication().getPath();
                BigDecimal fid = (BigDecimal) state.getValue(FILE_ID_PARAM);

                if (fid != null) {
                    url = url + "?d_id="+fid;
                }
                /*
                BigDecimal pid = null;
                BigDecimal fid = (BigDecimal) state.getValue(FILE_ID_PARAM);
                if (fid!=null) {
                pid = DRUtils.getFile(fid).getParentResource().getID();
                }
                */
                try {
                    DispatcherHelper.sendRedirect(state.getRequest(),
                            state.getResponse(),
                            url);
                } catch (IOException iox) {
                    throw new RuntimeException("Redirect to Application failed"
                            +iox);
                }
            }});
        backLink.setClassAttr("actionLink");
        p.add(backLink);

        // create main File-Info tabs
        TabbedPane tb = new TabbedPane();
        tb.setIdAttr("page-body");

        tb.addTab(FILE_INFO_PROPERTIES_TITLE, new FileInfoPropertiesPane(p));
        tb.addTab(FILE_INFO_HISTORY_TITLE, new FileInfoHistoryPane());

        /*
        * Disable Links tab because we have not
        * decided how to link other KnItems to a document.
        * 01/04/02 Stefan Deusch
        *
        tb.addTab(FILE_INFO_LINKS_TITLE, new FileInfoLinksPane());
        */
        p.add(tb);
        p.lock();

        return p;
    }

    /**
     * convenience wrapper method that allows to register a "" page
     * for an index page, if the isIndex flag is try
     */
    private void addPage(String url, Page p, boolean isIndex) {
        if (isIndex) {
            super.addPage("", p);
        }
        super.addPage(url, p);
    }

    /**
     * 
     * @param req
     * @param resp
     * @param ctx
     * @throws IOException
     * @throws javax.servlet.ServletException
     */
    public void dispatch(javax.servlet.http.HttpServletRequest req,
                         javax.servlet.http.HttpServletResponse resp,
                         RequestContext ctx)
            throws IOException, javax.servlet.ServletException {

        String url = req.getRequestURI();
        int index = url.lastIndexOf("/download/");

        if (index > 0) {
            s_log.debug("Downloading");
            String str = req.getParameter(FILE_ID_PARAM.getName());
            if (str != null) {
                BigDecimal id = new BigDecimal(str);

                File file = null;
                try {
                    file = new File(id);
                } catch(DataObjectNotFoundException nfe) {
                    throw new ObjectNotFoundException(
                              "The requested file no longer exists.");
                }

              //Check to see if current user is allowed to read this file
              file.assertPrivilege(PrivilegeDescriptor.READ);

              String mimetype = file.getContentType();
                  if (mimetype == null) {
                      mimetype = File.DEFAULT_MIME_TYPE;
                  }


                resp.setContentType(mimetype);

                InputStream is;
                final String transaction = req.getParameter("trans_id");
                if (transaction == null || transaction.equals("current")) {
                    is = file.getInputStream();
                } else {
                    is = getFileRevision(transaction);
                }

                sendToOutput(is, resp.getOutputStream());

            }
        } else {
            s_log.debug("dispatching");
            super.dispatch(req, resp, ctx);
        }

    }

    private static void sendToOutput(InputStream is, OutputStream os) {
        byte[] buf = new byte[8192]; // 8k buffer

        try {
            int sz = 0;
            while ((sz = is.read(buf, 0 , 8192)) != -1) {
                os.write(buf, 0, sz);
            }
        } catch (IOException iox) {
            iox.printStackTrace();
            throw new UncheckedWrapperException("IO Error streaming file", iox);
        } finally {
            try {
                is.close();
                os.close();
            } catch(IOException iox2) { }
        }

    }

    private static InputStream getFileRevision(String transaction) {
        BigDecimal transactionID = new BigDecimal(transaction);

        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
                ("com.arsdigita.docrepo.getFileRevisionBlob");
        query.setParameter("transactionID", transactionID);
        InputStream is = null;
        if (query.next()) {
            Object blob = query.get("content");
            is = new ByteArrayInputStream((byte[]) blob);
         }

        return is;
    }

}
