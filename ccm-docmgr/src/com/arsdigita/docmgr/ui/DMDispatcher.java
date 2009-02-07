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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import org.apache.log4j.Category;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.page.BebopMapDispatcher;
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
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.docmgr.File;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.web.Web;

/**
 * Dispatcher for document manager application.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */

public class DMDispatcher extends BebopMapDispatcher implements DMConstants {
    public static final String versionId =
        "$Id: //apps/docmgr/dev/src/com/arsdigita/docmgr/ui/DMDispatcher.java#4 $" +
        "$Author: jparsons $" +
        "$DateTime: 2003/07/17 20:42:41 $";

    private static Category s_log = Category.getInstance
        (DMDispatcher.class.getName());

    /**
     * Default constructor instantiating the URL-page map.
     */
    public DMDispatcher() {
        addPage("", buildDMIndexPage(), true);
        addPage("file", buildFileInfoPage());
    }

    /**
     * Build index page for the document manager,
    */

    private Page buildDMIndexPage() {
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

        p.add(new BrowsePane());
        p.lock();

        return p;
    }


    /**
     * Build page for the administration of one file.
     *   * Implementation according to wireframes at
    */

    private Page buildFileInfoPage() {

        DocmgrBasePage p = new DocmgrBasePage() {
                // need to override this to show the File name
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
                                        (DMUtils.getFile(fid).getName());
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

    public void dispatch(javax.servlet.http.HttpServletRequest req,
                         javax.servlet.http.HttpServletResponse resp,
                         RequestContext ctx)
        throws IOException, javax.servlet.ServletException {

        String url = req.getRequestURI();

        int index = url.lastIndexOf("/download/");

        if (index > 0) {
            String str = req.getParameter(FILE_ID_PARAM.getName());
            if (str != null) {
                BigDecimal id = new BigDecimal(str);

                File file = null;
                try {
                    file = new File(id);
                } catch(DataObjectNotFoundException nfe) {
                    throw new ObjectNotFoundException("The requested file no longer exists.");
                }

              String mimetype = file.getContentType();
                  if (mimetype == null) {
                      mimetype = File.DEFAULT_MIME_TYPE;
                  }


                resp.setContentType(mimetype);

                InputStream is = file.getInputStream();

                byte[] buf = new byte[8192]; // 8k buffer
                OutputStream os = null;

                try {
                    os = resp.getOutputStream();
                    int sz = 0;
                    while ((sz = is.read(buf, 0 , 8192)) != -1) {
                        os.write(buf, 0, sz);
                    }
                } catch (IOException iox) {
                    iox.printStackTrace();
                    throw new RuntimeException(iox.getMessage());
                } finally {
                    try {
                        is.close();
                        os.close();
                    } catch(IOException iox2) { }
                }
            }
        } else {
            super.dispatch(req, resp, ctx);
        }

    }

}
