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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.docmgr.File;
import com.arsdigita.docmgr.Folder;
import com.arsdigita.docmgr.Repository;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.User;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

/**
 * Public static helper methods and classes for recurring tasks
 * in Document Manager UI.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
public class DMUtils implements DMConstants {
    public static final String versionId =
        "$Id: //apps/docmgr/dev/src/com/arsdigita/docmgr/ui/DMUtils.java#3 $" +
        "$Author: jorris $" +
        "$DateTime: 2003/05/23 13:52:45 $";

    private static final Logger s_log = Logger.getLogger(DMUtils.class);

    /**
     * Gets or sets the root folder id and returns the root Folder
     */
    public static Folder getRootFolder(PageState state) {
        // Because user-profile is also making use of the doc
        // repository for storing user portraits, the current
        // application could be something other than a repository.
        // So, we'll assume that if the current application is not a
        // repository, it is a user-profile application.

        Repository currentRepository = null;

        // Check if it is a repository.
        Application app = Web.getContext().getApplication();
        String sAppType = app.getApplicationType().getApplicationObjectType();

        // Unfortunately, I have to hard-code these constants in here
        // because ant forces me to build docs before user-profile.
        String sUserProfileBaseObjectType = "com.arsdigita.userprofile.UserProfile";
        String sUserProfileRepoPath = "/cw-admin/user-profile/repository/";

        if (sAppType.compareTo(sUserProfileBaseObjectType) == 0) {
            // This is a user-profile app, so get the repository for
            // the user-profile app.
            currentRepository = (Repository)Application.retrieveApplicationForPath(sUserProfileRepoPath);
        } else {
            // Normal repository
            currentRepository = (Repository) Web.getContext().getApplication();
        }

        Folder root = currentRepository.getRoot();
        setRoot(state, root);
        return root;
    }

    /**
     * Get the selected folder in the tree node or the root tree id
     */
    public static BigDecimal getSelFolderOrRootID(PageState state, TreeNode n) {
        String idS = (String) n.getKey();

        if (idS != null) {
            return new BigDecimal(idS);
        }

        // No folder is selected. Get root folder.
        Folder root = getRootFolder(state);
        return root.getID();
    }

    /**
     * Change global state parameter of root folder.
     */
    public static void setRoot(PageState state, Folder root) {
        state.setValue(ROOTFOLDER_ID_PARAM, root.getID());
    }


    /**
     * Get User object from request context.
     */
    public static User getUser(PageState state) {
        User user = Web.getContext().getUser();

        if (user == null) {
            // User should be authenticated.
            throw new RuntimeException("User not logged in");
        }

        return user;
    }

    /**
     * Wrapper to get the selected folder in a tree. Works only if
     * we are already showing an expanded tree.
     */
    public static BigDecimal getSelectedFolderID(PageState state, Tree t) {
        String id = (String) t.getSelectedKey(state);

        if (id != null) {
            return new BigDecimal(id);
        }

        throw new RuntimeException("No tree node selected");
    }

    /**
     * Attempts to load a file by ID.
     */
    public static File getFile(BigDecimal id) {
        try {
            return new File(id);
        } catch(DataObjectNotFoundException nfe) {
            throw new ObjectNotFoundException("The requested file no longer exists.");
        }
    }

    /**
     * Get the selected folder in the tree
     */
    public static String getFolderName(PageState state, Tree tree) {
        BigDecimal id = getSelectedFolderID(state, tree);
        Folder folder = null;

        try {
            folder = new Folder(id);
        } catch(DataObjectNotFoundException nfe) {
            throw new UncheckedWrapperException("Folder not found", nfe);
        }

        return folder.getName();
    }

    /**
     * Extract the true filename from the uploaded filepath name
     * taking OS-specific file-separator into account The considered
     * OS'es are Windows, Mac, and Linux/Unixes.  The default case is
     * Linux/Unix.
     */
    public static String extractFileName(String rawName, PageState state) {
        // Get the page request header and read the User-agent

        HttpServletRequest request = state.getRequest();
        String userAgent = request.getHeader("User-Agent").toUpperCase();

        char separator;

        // Micro$oft browser
        if (userAgent.indexOf("WINDOWS") != -1) {
            separator = '\\';
        }

        // MacIntosh browser
        else if (userAgent.indexOf("MAC") != -1) {
            separator = ':';
        }

        // Default, the nerds who use Linux
        else {
            separator = '/';
        }

        int idx = rawName.lastIndexOf(separator);

        if (idx != -1) {
            return rawName.substring(idx + 1);
        } else {
            return rawName;
        }
    }

    /**
     * Class to describe and format file sizes in request specific locales
     */
    public static class FileSize {
        /**
         * Chooses the units for the file such that the file size is
         * greater or equal unity in the smallest units possible.
         */
        public static String formatFileSize(long n,  PageState state) {
            Locale locale = Kernel.getContext().getLocale();

            NumberFormat nf = NumberFormat.getNumberInstance(locale);
            nf.setMaximumFractionDigits(0);

            // Try finding a good matching going from small to large
            // files, assuming that most files will be small.

            double size = (double) n;

            if (size < 1000) {
                return nf.format(size) + " B";
            } else if ((size = byteToKilo(n)) < 1000) {
                return nf.format(size) + " KB";
            }

            // Must be one MB or larger.  Change pricision and keep
            // checking.

            nf.setMaximumFractionDigits(2);

            if ((size = byteToMega(n)) < 1000) {
                return nf.format(size) + " MB";
            } else {
                return nf.format(size) + " GB";
            }
        }

        /**
         * Wrapper for converting from BigDecimal to long file size
         */
        public static String formatFileSize(BigDecimal size, PageState state) {
            return formatFileSize(size.longValue(), state);
        }

        /*
         * Converstion methods
         */
        private static double byteToKilo(long size) {
            return ((double) size) / 1024.;
        }

        private static double byteToMega(long size) {
            return ((double) size) / 1048576.;
        }
    }

    /**
     * Class to render java.util.Date in request specific locale
     * and pattern.
     */
    public static class DateFormat {
        /**
         * Default Date format should look like this
         * 12/19/01 07:21 PM
         */
        public static String format(Date date) {
            return format(date, "MM/dd/yy hh:mm a");
        }

        /**
         * This allows to pass in any formatting strings for the date
         */
        public static String format(Date date, String fmt) {
            SimpleDateFormat formatter =
                new SimpleDateFormat(fmt, Kernel.getContext().getLocale());
            return formatter.format(date);
        }
    }
}
