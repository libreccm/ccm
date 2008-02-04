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

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.docmgr.File;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;

/**
 * This component shows the meta data of a file with links to
 * administrative actions on it.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
class FileActionPane extends ColumnPanel
    implements DMConstants
{

    private FileInfoPropertiesPane m_parent;
    private RequestLocal m_fileData;
    private ActionLink m_newVersion;
    private Link m_download;
    private ActionLink m_email;
    private ActionLink m_delete;

    private static final Logger s_log = Logger.getLogger(FileActionPane.class);
    /**
     * Constructor
     */

    FileActionPane(FileInfoPropertiesPane parent) {
        super(1);

        m_parent = parent;

        m_fileData = new RequestLocal() {
                protected Object initialValue(PageState state) {
                    BigDecimal id = (BigDecimal) state.getValue(FILE_ID_PARAM);
                    File file = null;
                    try {
                        file = new File(id);
                    } catch(DataObjectNotFoundException nfe) {
                        throw new ObjectNotFoundException("The requested file no longer exists.");
                    }
                    return file;
                }
            };


        m_newVersion    = addActionLink(FILE_NEW_VERSION_LINK);

        PrintListener printListener = new PrintListener() {
                public void prepare(PrintEvent e) {
                    Link l = (Link) e.getTarget();
                    PageState state = e.getPageState();
                    File f = getFile(state);
                    l.setTarget("download/" + f.getName() + "?" +
                                FILE_ID_PARAM.getName() + "=" + f.getID());
                }
            };

        m_download = new Link(new Label(FILE_DOWNLOAD_LINK),
                              printListener);
        m_download.setClassAttr("actionLink");
        add(m_download);

        m_email         = addActionLink(FILE_SEND_COLLEAGUE_LINK);
        m_delete        = addActionLink(FILE_DELETE_LINK);

        m_newVersion.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_parent.displayUploadForm(e.getPageState());
                }
            });


        m_email.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_parent.displaySendColleagueForm(e.getPageState());

                }
            });

        m_delete.addActionListener(new DeleteListener());
        m_delete.setConfirmation(FILE_DELETE_CONFIRM.localize().toString());
    }


    private ActionLink addActionLink(GlobalizedMessage msg) {
        ActionLink ln = new ActionLink(new Label(msg));
        ln.setClassAttr("actionLink");
        this.add(ln);
        return ln;
    }

    /**
     * return File initialized in RequestLocal
     */
    private File getFile(PageState s) {
        return (File)m_fileData.get(s);
    }

    /**
     * Download action handler for each page instance. Currently, the
     * Servlet response is switched to the Mime-type of the file,
     * and served. If the downloading browser understands the mime-type,
     * e.g. a jpeg image, it is shown in the browser.
     */
    private final class DownloadListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();

            HttpServletResponse resp = state.getResponse();
            File file = getFile(state);
            String mimeType = file.getContentType();
            if (mimeType == null) {
                mimeType = File.DEFAULT_MIME_TYPE;
            }

            // set output stream to corresponding Mime type
            resp.setContentType(mimeType);

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
                throw new RuntimeException(iox.getMessage());
            } finally {
                try {
                    is.close();
                    os.close();
                } catch(IOException iox2) {
                    // empty
                }
            }
        }
    }

    /**
     * Delete Listener of a file.
     */
    private final class DeleteListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            File file = getFile(state);
            String parentFolderID = file.getParent().getID().toString();
            file.delete();

            try {
                String appURI = getRedirectURI(state);

                DispatcherHelper
                    .sendRedirect(state.getRequest(),
                                  state.getResponse(),
                                  appURI+"?"+SEL_FOLDER_ID_PARAM.getName()+"="+
                                  parentFolderID);
            } catch(IOException iox) {
                throw new UncheckedWrapperException(iox);
            }
        }

        private String getRedirectURI(PageState state) {
            String appURI = state.getRequestURI();
            s_log.debug("Original app URI: " + appURI);
            int idx = appURI.indexOf("/file/");
            appURI = appURI.substring(0, idx);

            final String servletPath = Web.getConfig().getDispatcherServletPath();
            if (appURI.startsWith(servletPath)) {
                appURI = appURI.substring(servletPath.length());
            }


            s_log.debug("New URI: " + appURI);
            return appURI;
        }
    }

}
