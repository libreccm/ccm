/*
 * Copyright (C) 2011 Peter boy (pboy@barkhof.uni-bremen.de
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
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.docrepo.File;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Document;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Application servlet for the ccm-docrepo application, serves all request made
 * for the application's UI. 
 * 
 * DRServlet is called by BaseApplicationServlet which has determined that
 * DRServlet is associated with a request URL.
 * 
 * The servlet has to be included in servlet container's deployment descriptor,
 * see {@see com.arsdigita.docrepo.Repository#getServletPath()} for details
 * about web.xml record. It is NOT directly referenced by any other class.
 * 
 * It determines whether a <tt>Page</tt> has been registered to the URL and
 * if so passes the request to that page. Otherwise it hands  the request
 * to the TemplateResolver to find an appropriate JSP file.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: DRServlet.java 2161 2012-02-26 00:16:13Z pboy $
 */
public class DRServlet  extends BaseApplicationServlet 
                                implements DRConstants         {

    /** Private logger instance to faciliate debugging procedures             */
    private static final Logger s_log = Logger.getLogger(DRServlet.class);

    /** URL (pathinfo) -> Page object mapping. Based on it (and the http
     * request url) the doService method selects a page to display            */
    private final Map m_pages = new HashMap();


    /**
     * Use parent's class initialization extension point to perform additional
     * initialisation tasks. Here: build the UI pages.
     */
    @Override
    public void doInit()  {
        if (s_log.isDebugEnabled()) {
            s_log.info("starting RepositoryServlet doInit method ...");
        }

        addPage("/", buildDMIndexPage());
        addPage("/file", buildFileInfoPage());

    }

    /**
     * Implements the (abstract) doService method of BaseApplicationServlet to
     * perform the services.
     * @see com.arsdigita.web.BaseApplicationServlet#doService
     *      (HttpServletRequest, HttpServletResponse, Application)
     */
    protected void doService( HttpServletRequest sreq, 
                              HttpServletResponse sresp, 
                              Application app)
                   throws ServletException, IOException {
        if (s_log.isDebugEnabled()) {
            s_log.info("starting doService method");
        }

        String url = sreq.getRequestURI();
        int index = url.lastIndexOf("/download/");

        if (index > 0) {
            /* Download requested, handle the download */
            s_log.debug("Downloading");
            String str = sreq.getParameter(FILE_ID_PARAM.getName());
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


                sresp.setContentType(mimetype);

                InputStream is;
                final String transaction = sreq.getParameter("trans_id");
                if (transaction == null || transaction.equals("current")) {
                    is = file.getInputStream();
                } else {
                    is = getFileRevision(transaction);
                }

                sendToOutput(is, sresp.getOutputStream());

            }
        } else {
            /* Show the repository page  */
            s_log.debug("show repository page");
            // super.dispatch(req, resp, ctx);

            String pathInfo = sreq.getPathInfo();
            Assert.exists(pathInfo, "String pathInfo");
            if (pathInfo.length() > 1 && pathInfo.endsWith("/")) {
                /* NOTE: ServletAPI specifies, pathInfo may be empty or will 
                 * start with a '/' character. It currently carries a 
                 * trailing '/' if a "virtual" page, i.e. not a real jsp, but 
                 * result of a servlet mapping. But Application requires url 
                 * NOT to end with a trailing '/' for legacy free applications.  */
                pathInfo = pathInfo.substring(0, pathInfo.length()-1);
            }

            final Page page = (Page) m_pages.get(pathInfo);

            if (page != null) {

                final Document doc = page.buildDocument(sreq, sresp);

                PresentationManager pm = Templating.getPresentationManager();
                pm.servePage(doc, sreq, sresp);

            } else {

                sresp.sendError(404, "No such page for path " + pathInfo);

            }
        
        }

    }

    /**
     * Adds one Url-Page mapping to the internal mapping table.
     * 
     * @param pathInfo url stub for a page to display
     * @param page Page object to display
     */
    private void addPage(final String pathInfo, final Page page) {

        Assert.exists(pathInfo, String.class);
        Assert.exists(page, Page.class);
        // Current Implementation requires pathInfo to start with a leading '/'
        // SUN Servlet API specifies: "PathInfo *may be empty* or will start
        // with a '/' character."
        Assert.isTrue(pathInfo.startsWith("/"), "path starts not with '/'");

        m_pages.put(pathInfo, page);
    }
    /**
     * Build index page for the document repository,
     */

    private Page buildDMIndexPage() {

        Page page = new DocrepoBasePage();

        /**
         * Create main administration tab.
         */
        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.setIdAttr("page-body");
        tabbedPane.addTab(WS_BROWSE_TITLE, new BrowsePane());

        /*
        * Disable Repositories tab because
        * Still need to decide what to do with mounting
        * repository, since repository are now application.*/        
        // tabbedPane.addTab(WS_REPOSITORIES_TITLE, new RepositoryPane());

        //p.add(new BrowsePane());
        page.add(tabbedPane);
        page.lock();

        return page;
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
                title.addPrintListener(new com.arsdigita.bebop.event.PrintListener() {
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
                String url = Web.getWebContext().getApplication().getTitle();

                t.setLabel(fixed + " " + url);
            }});
        ActionLink backLink = new ActionLink(backLinkLabel);
        backLink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PageState state = e.getPageState();
                String url = Web.getWebContext().getApplication().getPath();
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
     * 
     * @param is
     * @param os 
     */
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

    /**
     * 
     * @param transaction
     * @return 
     */
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
