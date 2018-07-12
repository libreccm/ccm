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
import com.arsdigita.themedirector.ThemeCollection;
import com.arsdigita.themedirector.ThemeDirectorConstants;
import com.arsdigita.themedirector.ThemeFileCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.CCMResourceManager;
//import com.arsdigita.themedirector.dispatcher.InternalThemePrefixerServlet;
//import com.arsdigita.web.Web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Class providing client classes (as FileManager for development and published
 * themes) with base methods for polling the database to look for new/updated
 * files in the ThemeFile table.
 *
 * For "published" files, It goes through each Theme and looks at the last time
 * it was published. If the last time published > last time thread was run then
 * it examines those files. When the thread runs for the first time, it examines
 * all themes.
 *
 * For "development" files, it looks at every file in the db and, if the
 * timestamp is after the timestamp of the file on the file system (or there is
 * no file on the file system) then it writes out the new file. If the timestamp
 * on the file system is newer, it ignores the file.
 *
 * @author <a href="mailto:randyg@redhat.com">Randy Graebner</a>
 * @version $Revision: #2 $ $DateTime: 2004/01/30 17:24:49 $
 */
public abstract class ThemeFileManager extends Thread
    implements ThemeDirectorConstants {

    /**
     * Internal logger instance to faciliate debugging. Carries over the logger
     * instance from the client child which actually does the work.
     */
    private final Logger m_log;

    // The code in this class borrows heavily from
    // com.arsdigita.cms.publishToFile.FileManager
    /**
     * Following true if should keep watching file.
     */
    private boolean m_keepWatchingFiles = true;

    // Parameters involved in processing the file and their default values.
    // Set to values other than default by calling methods from an initializer.
    private final int m_startupDelay;
    private final int m_pollDelay;
    /**
     * Full path of the web application's base directory ("document root")
     */
    private String m_baseDirectory = null;

    // the m_ignoreInterrupt allows us to use the "interrupt" command to
    // break out of a "sleep" but to continue "running"
    protected boolean m_ignoreInterrupt = false;

    private Date m_lastRunDate;

    /**
     * Constructor initializes internal properties.
     *
     * @param log
     * @param startupDelay
     * @param pollDelay
     * @param baseDirectory
     */
    protected ThemeFileManager(Logger log,
                               int startupDelay,
                               int pollDelay,
                               String baseDirectory) {
        m_log = log;
        m_startupDelay = startupDelay;
        m_pollDelay = pollDelay;
        m_lastRunDate = null;
        m_baseDirectory = baseDirectory;
        m_keepWatchingFiles = true;
    }

    /**
     * Stop watching and processing the file. The background thread that
     * processes the file will terminate after this method has been called.
     * Termination is not immediate, since the file may be in the middle of
     * processing a block of entries.
     */
    public void stopWatchingFiles() {
        m_keepWatchingFiles = false;
    }

    protected boolean keepWatchingFiles() {
        return m_keepWatchingFiles;
    }

    protected Date getLastRunDate() {
        return m_lastRunDate;
    }

    /**
     * Watch file for entries to process. The main routine that starts file
     * processing.
     */
    @Override
    public void run() {
        m_log.info("Start polling file in " + m_startupDelay + "s.");
        if (m_lastRunDate == null) {
            // only do the startup delay the first time this is run.
            sleepSeconds(m_startupDelay);
        }
        m_log.info("Polling file every " + m_pollDelay + "s.");
        while ((sleepSeconds(m_pollDelay) || m_ignoreInterrupt)
                   && m_keepWatchingFiles) {
            // Get the last run date before we do anything,
            // so we can be sure that we do not miss any themes
            // published while we run. But only store it after
            // we have processed all themes, because it will be
            // used in ThemePublishedFileManager.updateTheme().
            Date lastRunDate = new Date();

            TransactionContext txn = SessionManager.getSession()
                .getTransactionContext();

            try {
                boolean startedTransaction = false;
                if (!txn.inTxn()) {
                    txn.beginTxn();
                    startedTransaction = true;
                }
                ThemeCollection collection = ThemeCollection.getAllThemes();
                while (collection.next()) {
                    updateTheme(collection.getTheme());
                }
                if (startedTransaction) {
                    txn.commitTxn();
                }

                m_lastRunDate = lastRunDate;
            } catch (Throwable e) {
                m_log.warn("Ignoring uncaught exception", e);
            } finally {
                if (txn.inTxn()) {
                    txn.abortTxn();
                    m_log.info("Aborting transaction");
                }
            }
            // we only ignore one interrupt at most
            m_ignoreInterrupt = false;
        }
    }

    /**
     * This allows an outside piece of code to force an automatic update instead
     * of making it wait for the thread to wake up.
     */
    public void updateAllThemesNow() {
        // this call to interrupt should kill the current "run" that is
        // occurring and break out of the sleep
        m_ignoreInterrupt = true;
        interrupt();
    }

    /**
     * This allows an outside piece of code to force an automatic update on a
     * single theme instead of making it wait for the thread to wake up.
     *
     * @param theme
     */
    public void updateThemeNow(Theme theme) {
        updateTheme(theme);
    }

    /**
     * This returns the base directory of the web applications ("document root")
     * used to construct the file system location when writing out files.
     *
     * @return Full path of the web application context directory
     */
    protected String getBaseDirectory() {

        if (m_baseDirectory == null) {
            // Because the constructor sets the base directory this should
            // never happen, but just in case ....

            // ThemeDirector may execute in a different web application context
            // as core oder CMS. 
            // Old non-standard-compliant code had been removed, so currently
            // ThemeManager has to be installed into the same context as core.
            // To determine the actual context we may ask Themedirector servlet.
            // Something like
            // ServletContext themeCtx = InternalThemePrefixerServlet
            //                        .getThemedirectorContext();
            // We have to ensure the Servlet is initialized.
            // ServletContext themeCtx = Web.getServletContext();
            // m_baseDirectory = themeCtx.getRealPath("/");
            m_baseDirectory = CCMResourceManager.getBaseDirectory().getPath();
        }

        return m_baseDirectory;
    }

    /**
     * This typically returns something like "getBaseDirectory() + PUB_DIR".
     *
     * @return
     */
    protected abstract String getManagerSpecificDirectory();

    /**
     * This allows subclasses to filter the collection as appropriate. (e.g.
     * only return "live" files or only "draft" files).
     *
     * @param theme
     *
     * @return
     */
    protected abstract ThemeFileCollection getThemeFilesCollection(Theme theme);

    /*
      TODO:
      1. provide a link to allow the user to force a sync of draft files
      When the user syncs the draft files, do we tell all servers to
      insert in to the db and then tell all servers to sync with the db?
      Maybe outline this in the "known problems" which can be solved
      by only developing on one machine.

      2. provide a link to allow the user to force a sync of prod files

      Issues:
      If someone is working on server A and someone else on server B, this
      code can override someone's work.  For instance, if designer 1
      places a file "foo.xsl" on server A at 1:00PM and then designer 2
      places a file with the same name (foo.xsl) on server B at 2:00PM,
      this code will assume that the file from server B is more recent and
      thus propagate that file to server A, overwriting the file uploaded by
      desginer 1.  This can be avoided by only allowing one server to
      be the designated development server.

      In order for files to correctly propagate, all of the servers need to
      have clocks that are close to synchronized (at the very least, they
      must be set for the same time zone).

     */
    /**
     * This looks at all of the files in the db for the passed in theme and
     * makes sure that this servers file system has all of the updated files.
     *
     * @param theme
     */
    protected void updateTheme(Theme theme) {
        String stub = getManagerSpecificDirectory();
        // sync the files that have the correct dates
        m_log.debug("Looking at theme " + theme.getURL());

        ThemeFileCollection files = getThemeFilesCollection(theme);
        if (files == null) {
            return;
        }

        while (files.next()) {
            File temp = new File(stub + theme.getURL() + "/" + files
                .getFilePath());
            // if the file timestamp in the db is newer than the file
            // on the FS then write it out (or delete).
            if (new Date(temp.lastModified())
                .before(files.getLastModifiedDate())) {
                if (files.isDeleted()) {
                    if (temp.exists()) {
                        boolean success = temp.delete();
                        if (success) {
                            m_log.info("Deletion of " + temp + " succeeded");
                        } else {
                            m_log.error("Deletion of " + temp + " failed");
                        }
                    }
                    continue;
                }
                // now, we need to write out the file and
                // log any errors we encounter
                FileOutputStream out = null;
                try {
                    // make sure all of the directories exist
                    temp.getParentFile().mkdirs();
                    m_log.debug("Writing file " + temp.getAbsolutePath());

                    byte[] content = files.getThemeFile().getContent();
                    out = new FileOutputStream(temp);
                    out.write(content);
                    temp.setLastModified(files.getLastModifiedDate().getTime());
                } catch (IOException e) {
                    m_log.error("Error writing file " + temp.getAbsolutePath(),
                                e);
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        m_log.error("Could not close output stream", e);
                    }
                }
            }
        }
    }

    /**
     * *
     * Sleep for n seconds.
     *
     * @param n
     *
     * @return 
     **
     */
    protected boolean sleepSeconds(long n) {
        try {
            sleep(n * 1000);
            return true;
        } catch (InterruptedException e) {
            m_log.info("Waiting was interrupted.");
            return false;
        }
    }

}
