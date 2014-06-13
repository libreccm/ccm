/*
* Copyright (C) 2001, 2003 ArsDigita Corporation. All Rights Reserved.
*
* The contents of this file are subject to the ArsDigita Public
* License (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of
* the License at http://www.arsdigita.com/ADPL.txt
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
*
*/

package com.arsdigita.themedirector.util;

import com.arsdigita.themedirector.Theme;
import com.arsdigita.themedirector.ThemeFile;
import com.arsdigita.themedirector.ThemeFileCollection;

import java.io.File;

import org.apache.log4j.Logger;

/**
 *  Class for polling the database to look for new/updated development files
 *  in the ThemeFile table.  
 *
 *  For "development" files, it looks at every file in the db and, if
 *  the timestamp is after the timestamp of the file on the file system (or
 *  there is no file on the file system)
 *  then it writes out the new file.  If the timestamp on the file system
 *  is newer, it ignores the file.
 *
 *
 * @author <a href="mailto:randyg@redhat.com">Randy Graebner</a>
 *
 * @version $Revision: #2 $ $DateTime: 2004/03/17 09:56:37 $
 */
public class ThemeDevelopmentFileManager extends ThemeFileManager {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int hte runtime environment
     *  and set com.arsdigita.themedirector.util.ThemeDevelopmentFileManager=DEBUG
     *  by uncommenting or adding the line.                                      */
    private static Logger s_log = Logger
                                  .getLogger(ThemeDevelopmentFileManager.class);

    // The code in this class borrows heavily from 
    // com.arsdigita.cms.publishToFile.FileManager

    private static ThemeFileManager s_manager;


    /**
     * Constructor just delegates to super class.
     * 
     * @param startupDelay
     * @param pollDelay
     * @param baseDirectory 
     */
    protected ThemeDevelopmentFileManager(int startupDelay, int pollDelay, 
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
     * The thread starts processing after <code>startupDelay</code> seconds. 
     * The db is checked for new/updated
     * files every <code>pollDelay</code> seconds.
     *
     * This will not start up multiple threads...if there is already
     * a thread running, it will return that thread to you.
     *
     * @param startupDelay number of seconds to wait before starting to process
     *                     the file. A startupDelay of 0 means that this is a no-op
     * @param pollDelay    number of seconds to wait between checks if the file
     *                     has any entries.
     * @param baseDirectory
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
        return getBaseDirectory() + DEV_THEMES_BASE_DIR;
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
            ThemeFileUtil.updateDatabaseFiles(themeDir, theme, 
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
