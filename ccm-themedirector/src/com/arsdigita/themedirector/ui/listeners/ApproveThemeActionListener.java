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
 */
package com.arsdigita.themedirector.ui.listeners;

import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.subsite.Subsite;
import com.arsdigita.themedirector.Theme;
import com.arsdigita.themedirector.ThemeDirectorConstants;
import com.arsdigita.themedirector.ThemeFile;
import com.arsdigita.themedirector.ThemeObserver;
import com.arsdigita.themedirector.ui.ThemeSelectionModel;
import com.arsdigita.themedirector.util.ThemeFileUtil;
import com.arsdigita.themedirector.util.WhiteListFilenameFilter;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Files;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/**
 * This approves the theme and pushes it to the production file location. This
 * action means that the user wants to approve the themes and push them live.
 * This is done by copying the files from the devel directory into the published
 * directory.
 *
 * @author Randy Graebner &lt;randyg@redhat.com&gt;
 */
public class ApproveThemeActionListener implements ThemeDirectorConstants,
                                                   ActionListener {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by
     * editing /WEB-INF/conf/log4j.properties int the runtime environment and
     * set
     * com.arsdigita.themedirector.ui.listeners.ApproveThemeActionListener=DEBUG
     * by uncommenting or adding the line.
     */
    private static final Logger LOGGER = Logger.getLogger(
        ApproveThemeActionListener.class);

    private final ThemeSelectionModel selectionModel;

    /**
     * Constructor, just stores the ThemeSelectionModel.
     *
     * @param selectionModel the ThemeSelectionModel
     */
    public ApproveThemeActionListener(
        final ThemeSelectionModel selectionModel) {

        this.selectionModel = selectionModel;
    }

    /**
     *
     * @param event
     */
    @Override
    public void actionPerformed(final ActionEvent event) {

        // First, we rename the current production directory so that if there
        // is an exception, we can try to move it back in to place
        final Theme theme = selectionModel
            .getSelectedTheme(event.getPageState());

        final File currentRoot = new File(
            Web.getServletContext().getRealPath("/"));

        final File newProd = new File(currentRoot, PROD_THEMES_BASE_DIR);
        final File devDir = new File(currentRoot,
                                     DEV_THEMES_BASE_DIR + theme.getURL());

        ThemeFileUtil.prepareThemeDirectory(devDir.getAbsolutePath());

        final File oldProd = new File(currentRoot,
                                      PROD_THEMES_BASE_DIR + theme.getURL());
        final File backupFile = new File(oldProd.getAbsolutePath() + ".bak");

        if (oldProd.exists()) {
            oldProd.renameTo(backupFile);
        }

        oldProd.mkdirs();
        // now, we copy the "dev" directory to the "prd" directory

        try {
            // sort filepaths to be copied alphabetically, so that the
            // directory is always created before the files contained therein
            final Set<String> filesToCopy = new TreeSet<>(
                Arrays.asList(Files
                    .listFilesInTree(devDir,
                                     new WhiteListFilenameFilter())));

            for (String path : filesToCopy) {

                final File src = new File(devDir, path);

                if (src.isDirectory()) {
                    LOGGER.debug("creating directory " + path);
                    new File(newProd, theme.getURL() + File.separator + path)
                        .mkdirs();
                } else {
                    LOGGER.debug("Copying file " + path);
                    Files.copy(src,
                               new File(newProd,
                                        theme.getURL()
                                            + File.separator
                                            + path));
                }
            }

            // assuming that went well, we need to move the devDir in to the
            // database as the latest "live" files but before we do that, we
            // copy is_deleted flag from development to published files
            final DataOperation op = SessionManager
                .getSession()
                .retrieveDataOperation(
                    "com.arsdigita.themedirector.bulkFileUpdate");
            op.setParameter("themeID", theme.getID());
            op.setParameter("timestamp", new Date());
            op.execute();
            ThemeFileUtil.updateDatabaseFiles(devDir,
                                              theme,
                                              devDir.getAbsolutePath(),
                                              true,
                                              ThemeFile.LIVE);

            // since we are publishing, we also want to share the DEV files
            ThemeFileUtil.updateDatabaseFiles(devDir,
                                              theme,
                                              devDir.getAbsolutePath(),
                                              true,
                                              ThemeFile.DRAFT);

            // add the observer to make sure that it syncs with other
            // servers in the cluster
            theme.addObserver(new ThemeObserver());

        } catch (IOException ex) {
            final String errorMsg = "There was an error moving files from "
                                        + devDir.getAbsolutePath()
                                        + " to "
                                        + newProd.getAbsolutePath();
            LOGGER.error(errorMsg, ex);
            // delete the directory and move the oldProd back
            Files.delete(new File(newProd + theme.getURL()));
            if (oldProd.exists()) {
                oldProd.renameTo(new File(currentRoot,
                                          PROD_THEMES_BASE_DIR + theme.getURL()
                ));
            }
            throw new UncheckedWrapperException(errorMsg, ex);
        }

        // finally, since there were no errors, we can update our
        // theme object and delete our "old" production directory
        if (oldProd.exists()) {
            Files.delete(backupFile);
        }
        theme.setLastPublishedDate(new Date());
        theme.setLastPublishedUser(Kernel.getContext().getParty());
        // add it to the subsite drop down list
        Subsite.getConfig().addTheme(theme.getURL(), theme.getTitle());
    }

}
