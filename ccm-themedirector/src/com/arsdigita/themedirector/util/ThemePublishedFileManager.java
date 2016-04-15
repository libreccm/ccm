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
import com.arsdigita.themedirector.ThemeFileCollection;
import com.arsdigita.templating.Templating;
import java.io.File;
import org.apache.log4j.Logger;


/**
 *  Class for polling the database to look for new/updated published files in
 *  the ThemeFile table.
 *
 *  For "published" files, It goes through each Theme and looks at the
 *  last time it was published.  If the last time published &gt; last
 *  time thread was run then it examines those files.  When the thread
 *  runs for the first time, it examines all themes.
 *
 * @author <a href="mailto:randyg@redhat.com">Randy Graebner</a>
 * @version $Revision: #2 $ $DateTime: 2004/03/17 09:56:37 $
 */
public class ThemePublishedFileManager extends ThemeFileManager {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int hte runtime environment
     *  and set com.arsdigita.themedirector.util.ThemePublishedFileManager=DEBUG
     *  by uncommenting or adding the line.                                   */
    private static Logger s_log = Logger.getLogger(
                                  ThemePublishedFileManager.class);

    // The code in this class borrows heavily from
    // com.arsdigita.cms.publishToFile.FileManager

    static private ThemeFileManager s_manager = null;


    /**
     * Constructor just delegates to super class.
     * 
     * Usually Themedirector's Initializer() will setup a background thread to
     * continuously watch for modifications and synchronize. Specifically the
     * Initializer() provides null as baseDirectory parameter because it
     * doesn't know about servlet context and can not determine the directory.
     * 
     * @param startupDelay number of seconds to wait before starting to process
     *                     the file. A startupDelay of 0 means this is a no-op
     * @param pollDelay    number of seconds to wait between checks if the file
     *                     has any entries.
     * @param baseDirectory String with the path to theme files base directory
     *                     (the directory containing devel and pub subdirs)
     *                     May be null! (Most likely if invokel by Initializer!)
     */
    protected ThemePublishedFileManager(int startupDelay, 
                                        int pollDelay,
                                        String baseDirectory) {
        super(s_log, startupDelay, pollDelay, baseDirectory);
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
     * @param baseDirectory String with the path to theme files base directory
     *                     (the directory containing devel and pub subdirs)
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
                s_manager = new ThemePublishedFileManager(startupDelay,
                                                          pollDelay,
                                                          baseDirectory);
                s_manager.setDaemon(true);
                s_manager.setName("theme-pub-files");
                s_manager.start();
            } else {
                s_log.warn("Theme Published File Manager Thread is not starting " +
                           "because the startup delay is <= 0");
            }
        }
        return s_manager;
    }

    /**
     * This returns the current thread or null if the thread has not yet
     * been started.
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
        return getBaseDirectory() + PROD_THEMES_BASE_DIR;
    }


    /**
     * This looks at all of the files in the db for the passed in theme and
     * makes sure that this servers file system has all of the updated files.
     * If the thread has run since the theme was last published then this is 
     * a no-op
     */
    @Override
    protected void updateTheme(Theme theme) {
        if (getLastRunDate() == null || m_ignoreInterrupt ||
            !(new File(getManagerSpecificDirectory() + theme.getURL())).exists() ||
            (theme.getLastPublishedDate() != null &&
            getLastRunDate().before(theme.getLastPublishedDate()))) {
            super.updateTheme(theme);
            // purge the templating cache  -- we want newly published themes
            // to be visible immediately, and not after a server restart.
            Templating.purgeTemplates();
        }
    }


    /**
     * This allows subclasses to filter the collection as appropriate.
     * (e.g. only return "live" files or only "draft" files).
     * 
     * @return 
     */
    @Override
    protected ThemeFileCollection getThemeFilesCollection(Theme theme) {
        return theme.getPublishedThemeFiles();
    }
}
