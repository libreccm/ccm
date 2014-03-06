/*
 * Copyright (C) 2012 Peter boy (pboy@barkhof.uni-bremen.de
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

package com.arsdigita.cms.docmgr.ui;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.AbstractTransactionListener;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.util.Assert;
import com.arsdigita.versioning.Versions;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.web.Web;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


/**
 * Application servlet for ccm-docmgr's Repository application, the main
 * package application, serves all request made for the application's UI. 
 * 
 * RepositporyServlet is called by BaseApplicationServlet which has determined
 * that RepositoryServlet is associated with a request URL.
 * 
 * The servlet has to be included in servlet container's deployment descriptor,
 * see {@see com.arsdigita.cms.docmgr.Repository#getServletPath()} for details
 * about web.xml record. It is NOT directly referenced by any other class.
 * 
 * It determines whether a <tt>Page</tt> has been registered to the URL and
 * if so passes the request to that page. Otherwise it hands  the request
 * to the TemplateResolver to find an appropriate JSP file.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: RepositoryServlet.java 2161 2012-02-26 00:16:13Z pboy $
 */
public class RepositoryServlet extends BaseApplicationServlet 
                               implements DMConstants         {

    /** Private logger instance to faciliate debugging procedures             */
    private static final Logger s_log = Logger.getLogger(RepositoryServlet.class);

    /** URL (pathinfo) -> Page object mapping. Based on it (and the http
     * request url) the doService method selects a page to display            */
    private final Map m_pages = new HashMap();


    /**
     * Use parent's class initialization extension point to perform additional
     * initialisation tasks. Here: build the UI pages.
     */
    @Override
    public void doInit()  {
        
        addPage( "/", buildRepositoryIndexPage() );
        addPage( "/file", buildFileInfoPage());
        // search is a tab, for now. 
        //addPage("/search", buildSearchPage());
        //addPage("/search/file", buildFileInfoPage());

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
            String str = sreq.getParameter(FILE_ID_PARAM_NAME);
            s_log.debug("Downloading");
            if (str != null) {
                BigDecimal id = new BigDecimal(str);
                s_log.debug("requesting file for id: "+str);
                Document doc = new Document(id);
                sresp.setHeader("Content-Disposition", "attachment; filename=" +
                               URLDecoder.decode(doc.getName()));
                doc.assertPrivilege(PrivilegeDescriptor.READ);

            // if the user has requested an earlier revision, get
            // that revision and serve it
            String param = sreq.getParameter("transID");
            if (param != null) {
                Session ssn = SessionManager.getSession();
                TransactionContext txn = ssn.getTransactionContext();
                txn.addTransactionListener(new AbstractTransactionListener() {
                    @Override
                    public void beforeCommit(TransactionContext txn) {
                        Assert.fail("uncommittable transaction");
                    }
                });

                Kernel.getContext().getTransaction().setCommitRequested(false);

                BigInteger transID = new BigInteger(param);
                Versions.rollback(doc.getOID(), transID);
            }

            FileAsset file = doc.getFile();
            sresp.setContentType( null != file.getMimeType() ? 
                                  file.getMimeType().getMimeType() : "text/plain" );
            OutputStream os = null;

            try {
                os = sresp.getOutputStream();
                file.writeBytes(os);
            } catch (IOException iox) {
                iox.printStackTrace();
                throw new RuntimeException(iox.getMessage());
            } finally {
                try {
                    //is.close();
                    os.close();
                    } catch(IOException iox2) { }
                }
            }

        } else {
            /* No download, show the repository index page  */
            s_log.debug("show repository page");

            String pathInfo = sreq.getPathInfo();
            Assert.exists(pathInfo, "String pathInfo");
            if (pathInfo.length() > 1 && pathInfo.endsWith("/")) {
                /* NOTE: ServletAPI specifies, pathInfo may be empty or will 
                 * start with a '/' character. It currently carries a 
                 * trailing '/' if a "virtual" page, i.e. not a real jsp, but 
                 * result of a servlet mapping. But Application requires url 
                 * NOT to end with a trailing '/' for legacy free applications.*/
                pathInfo = pathInfo.substring(0, pathInfo.length()-1);
            }

            final Page page = (Page) m_pages.get(pathInfo);

            if (page != null) {

                final com.arsdigita.xml.Document doc = page.buildDocument(sreq, 
                                                                          sresp);

                PresentationManager pm = Templating.getPresentationManager();
                pm.servePage(doc, sreq, sresp);

            } else {

                // TODO: Check JSP's first (cf. cms content-section servlet)
                sresp.sendError(404, "No such page for path " + pathInfo);

            }

        }


        if (s_log.isDebugEnabled()) s_log.info("completed doService method");
    }   

    /**
     * Adds one Url-Page mapping to the internal mapping table.
     * 
     * @param pathInfo url stub for a page to display
     * @param page Page object to display
     */
    protected void addPage(final String pathInfo, final Page page) {

        Assert.exists(pathInfo, String.class);
        Assert.exists(page, Page.class);
        // Current Implementation requires pathInfo to start with a leading '/'
        // SUN Servlet API specifies: "PathInfo *may be empty* or will start
        // with a '/' character."
        Assert.isTrue(pathInfo.startsWith("/"), "path starts not with '/'");

        m_pages.put(pathInfo, page);
    }
    

    /**
     * Build index page for the document manager,
     */
    private Page buildRepositoryIndexPage() {

        Page p = new DocmgrBasePage();

        /* Create main administration tab. */
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
     */
    protected Page buildFileInfoPage() {

        final BigDecimalParameter fileIDParam = new 
                                  BigDecimalParameter(FILE_ID_PARAM_NAME);

        DocmgrBasePage p = new DocmgrBasePage(fileIDParam) {
            // need to override this to show the File name
            @Override
            protected void buildTitle() {
                Label title = new Label();
                title.addPrintListener(new 
                      com.arsdigita.bebop.event.PrintListener() {
                    public void prepare(PrintEvent e) {
                        PageState state = e.getPageState();
                        Label t = (Label) e.getTarget();
                        BigDecimal fid =
                                    (BigDecimal) state.getValue(fileIDParam);
                            if (fid!=null) {
                                t.setLabel(DMUtils.getFile(fid).getTitle());
                            }
                        }
                    });
                    setTitle(title);
                }
                
            @Override
                protected void buildContextBar() {
                	FileDimensionalNavbar navbar = new FileDimensionalNavbar(
                                                           new RequestLocal() {
                    @Override
                		protected Object initialValue(PageState state) {
                			BigDecimal id = (BigDecimal) state.getValue(fileIDParam);
                			return new Document(id);
                		}
                	});
                	navbar.setClassAttr("portalNavbar");
                	getHeader().add(navbar);
                }

        };

        // need to add the file parameter to the page
        //BigDecimalParameter FILE_ID_PARAM = new BigDecimalParameter(FILE_ID_PARAM_NAME);
        //p.addGlobalStateParam(fileIDParam);
        
        /* Temporary fix to sdm #204233, NavBar of Application allows only
           one URL per application, so here we add a Link back to the parent folder
        */
        Label backLinkLabel = new Label
            (new GlobalizedMessage("ui.fileinfo.goback.label", BUNDLE_NAME));
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
                BigDecimal fid = (BigDecimal) state.getValue(fileIDParam);

                if (fid != null) {
                    url = url + "?d_id="+fid;
                }
                /*
                  BigDecimal pid = null;
                  BigDecimal fid = (BigDecimal) state.getValue(FILE_ID_PARAM);
                  if (fid!=null) {
                  pid = DMUtils.getFile(fid).getParentResource().getID();
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
        // TODO - comment in
        tb.addTab(FILE_INFO_HISTORY_TITLE, new FileInfoHistoryPane(p));
        
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
     * Build search page for the document manager,
    */
    protected Page buildSearchPage() {
        Page p = new DocmgrBasePage();

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

        p.add(new SearchPane());
        p.lock();

        return p;
    }

}
