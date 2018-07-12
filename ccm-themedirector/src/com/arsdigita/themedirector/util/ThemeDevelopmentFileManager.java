/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.themedirector.util;

import com.arsdigita.themedirector.Theme;
import com.arsdigita.themedirector.ThemeFile;
import com.arsdigita.themedirector.ThemeFileCollection;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Class for polling the database to look for new/updated development files
 * in the ThemeFile table.  
 *
 * For "development" files, it looks at every file in the db and, if the
 * timestamp is after the timestamp of the file on the file system (or
 * there is no file on the file system) then it writes out the new file.
 * If the timestamp on the file system is newer, it ignores the file.
 *
 * @author <a href="mailto:randyg@redhat.com">Randy Graebner</a>
 */
public class ThemeDevelopmentFileManager extends ThemeFileManager {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties in the runtime environment and
     *  set com.arsdigita.themedirector.util.ThemeDevelopmentFileManager=DEBUG
     *  by uncommenting or adding the line.                                   */
    private static final Logger s_log = Logger
                                  .getLogger(ThemeDevelopmentFileManager.class);

    // The code in this class borrows heavily from 
    // com.arsdigita.cms.publishToFile.FileManager

    private static ThemeFileManager s_manager;


    /**
     * Constructor just delegates to super class.
     * 
     * Usually Themedirector's Initializer() will setup a background thread to
     * continuously watch for modifications and synchronize. Specifically the
     * Initializer() provides null as baseDirectory parameter because it
     * doesn't know about servlet context and can not determine the directory.
     *
     * 
     * @param startupDelay number of seconds to wait before starting to process
     *                     the file. A startupDelay of 0 means this is a no-op
     * @param pollDelay    number of seconds to wait between checks if the file
     *                     has any entries.
     * @param baseDirectory String with the file system path to document root 
     *                     for the application context ThemeDirector is running.
     *                     (the directory containing WEB-INF subdir)
     *                     May be null! (Specificall if invokel by Initializer!)
     */
    protected ThemeDevelopmentFileManager(int startupDelay, 
                                          int pollDelay, 
                                          String baseDirectory) {
        
        super(s_log,                                   // Injects it's own logger 
              startupDelay, pollDelay, baseDirectory); // to the parent class methods!
    }


    // is there a way to move this code up in to the parent class?
    /**
     * Start watching the files. This method spawns a background thread that
     * looks for changes in files in the database if there is not already a
     * thread that has been spawned.  If there is already a running thread then
     * this is a no-op that returns a reference to the running thread.
     *
     * Specifically it is used by Themedirector's Initializer() to start a
     * continuous background process to synchronize database and filesystem.
     *
     * The thread starts processing after <code>startupDelay</code> seconds. The
     * db is checked for new/updated files every <code>pollDelay</code> seconds.
     *
     * This will not start up multiple threads...if there is already a thread
     * running, it will return that thread to you.
     *
     * @param startupDelay number of seconds to wait before starting to process
     *                     the file. A startupDelay of 0 means this is a no-op
     * @param pollDelay    number of seconds to wait between checks if the file
     *                     has any entries.
     * @param baseDirectory String with the file system path to document root 
     *                     for the application context ThemeDirector is running.
     *                     (the directory containing WEB-INF subdir)
     *                     May be null! (Specificall if invokel by Initializer!)
     * @return 
     */
    public static ThemeFileManager startWatchingFiles(int startupDelay, 
                                                      int pollDelay, 
                                                      String baseDirectory) {
        if (s_manager == null) {
            s_log.info("Starting Theme File Manager Thread with the base " +
                       "directory of " + baseDirectory);
            if ( startupDelay > 0 ) {
                s_manager = new ThemeDevelopmentFileManager(startupDelay, 
                                                            pollDelay, 
                                                            baseDirectory);
                s_manager.setDaemon(true);
                s_manager.setName("theme-dev-files");
                s_manager.start();
            } else {
                s_log.warn("Theme Development File Manager Thread is not starting " +
                           "because the startup delay is <= 0");
            }
        }
        return s_manager;        
    }

    /**
     * This returns the current thread or null if the thread has not
     * yet been started.
     * 
     * @return 
     */
    public static ThemeFileManager getInstance() {
        return s_manager;
    }


    /**
     * This typically returns something like "getBaseDirectory() + PROD_DIR".
     * 
     * @return 
     */
    @Override
    protected String getManagerSpecificDirectory() {
        String devDir = getBaseDirectory() + DEV_THEMES_BASE_DIR;
        s_log.info(devDir + " is the development themes directory used.");
        return devDir;
    }



    // TODO
    // if we run the updateDatabaseFiles every time this runs then
    // it ends up doing an insert pretty much every time.  So,
    // we only place the dev files in the db when the user specifically
    // tells us to by publishing the files or by clicking on the link
    // to place them in the db.  I am leaving this code here since
    // it works if we want the thread to auto-update things for us.
    // if we decide that we definitely do not want the auto-update
    // then we should remove this.
    @Override
    protected void updateTheme(Theme theme) {
        // the first step is to make sure that all files from the theme
        // are in the db.
        String stub = getManagerSpecificDirectory();

        File themeDir = new File(stub + theme.getURL() + "/");
        if (themeDir.exists()) {
            ThemeFileUtil.updateDatabaseFiles(themeDir,
                                              theme, 
                                              themeDir.getAbsolutePath(), 
                                              false,
                                              ThemeFile.DRAFT);
        }

        // the call to super makes sure that the file system has everything
        // from the database.  
        super.updateTheme(theme);
    }


    /**
     * This allows subclasses to filter the collection as appropriate.
     * (e.g. only return "live" files or only "draft" files).
     * @return 
     */
    @Override
    protected ThemeFileCollection getThemeFilesCollection(Theme theme) {
        return theme.getDraftThemeFiles();
    }
}
