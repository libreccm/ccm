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
import com.arsdigita.docrepo.File;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.web.Web;
import org.apache.log4j.Logger;

//import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;

/**
 * This component shows the meta data of a file with links to
 * administrative actions on it.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
class FileActionPane extends ColumnPanel
    implements DRConstants
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
     * Delete Listener of a file.
     */
    private final class DeleteListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            final File file = getFile(state);
            String parentFolderID = file.getParent().getID().toString();

            KernelExcursion ex = new KernelExcursion() {
                  protected void excurse() {
                     setEffectiveParty(Kernel.getSystemParty());
                       file.delete();
                   }
            };
            ex.run();

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
