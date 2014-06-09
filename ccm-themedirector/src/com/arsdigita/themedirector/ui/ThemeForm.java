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

package com.arsdigita.themedirector.ui;

import com.arsdigita.themedirector.Theme;
// import com.arsdigita.london.theme.ThemeDirector;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.GlobalizedParameterListener;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.Label;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
//import com.arsdigita.util.IO;
import com.arsdigita.themedirector.ThemeDirectorConstants;
import com.arsdigita.themedirector.ThemeDirector;
import com.arsdigita.themedirector.util.GlobalizationUtil;
//import com.arsdigita.themedirector.util.ManifestReader;
import com.arsdigita.subsite.Subsite;
import com.arsdigita.subsite.Site;
import com.arsdigita.toolbox.ui.Cancellable;
import com.arsdigita.web.Web;

import java.io.File;
//import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStream;
import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.filefilter.DirectoryFileFilter;

import org.apache.log4j.Logger;



/**
 *  This is a class used to create themes and edit its basic properties
 *
 *  @author Randy Graenber &lt;randyg@redhat.com&gt;
 */
public class ThemeForm extends Form implements Cancellable, ThemeDirectorConstants {
    
    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int hte runtime environment
     *  and set com.arsdigita.themedirector.ui.ThemeForm=DEBUG by uncommenting 
     *  or adding the line.                                                   */
    private static final Logger s_log = Logger.getLogger(ThemeForm.class);

    private final ThemeSelectionModel m_theme;

    /* Holds the theme's title.                                               */
    private final TextField m_title;

    /* Holds the theme's description.                                         */
    private final TextArea m_description;

    /* Holds the theme's URL.                                                 */
    private final TextField m_url;

    /* Instance of the form's Save/Cancel buttons.                            */
    private final SaveCancelSection m_buttons;

    /**
     * Constructor creats the input form to create a new theme or edit an
     * existing one.
     * 
     * @param name
     * @param theme 
     */
    public ThemeForm(String name,
                     ThemeSelectionModel theme) {
        super(name, new GridPanel(2));
        setClassAttr("simpleThemeForm");
        setRedirecting(true);
        
        m_theme = theme;  // Initialize ThemeSelectionModel

        // Add the Title input field
        add(new Label(GlobalizationUtil.globalize("theme.title")));
        m_title = new TextField(new StringParameter("title"));
        // Experimental. We are migrating the Label if a widget as part of the
        // widgets's xml properties.
        m_title.setLabel(GlobalizationUtil.globalize("theme.title"));
        m_title.addValidationListener(new NotEmptyValidationListener());
        m_title.setHint(GlobalizationUtil.globalize("theme.title_hint"));
        m_title.setSize(40);
        add(m_title);

        add(new Label(GlobalizationUtil.globalize("theme.description")));
        m_description = new TextArea(new StringParameter("description"));
        // Experimental, see above
        m_description.setLabel(GlobalizationUtil.globalize("theme.description"));
        m_description.setCols(40);
        m_description.setRows(4);
        m_description.setHint(GlobalizationUtil
                              .globalize("theme.description_hint"));
        add(m_description);

        add(new Label(GlobalizationUtil.globalize("theme.url")));
        m_url = new TextField(new StringParameter("url"));
        // Experimental, see above
        m_url.setLabel(GlobalizationUtil.globalize("theme.url"));
        m_url.addValidationListener(new NotEmptyValidationListener());
        m_url.addValidationListener(new URLFormValidationListener());
        m_url.addValidationListener(new UniqueURLValidationListener());
        m_url.setSize(40);
        m_url.setHint(GlobalizationUtil.globalize("theme.url_hint"));
        add(m_url);
        
        m_buttons = new SaveCancelSection();
        m_buttons.getSaveButton().setHint(GlobalizationUtil
                                          .globalize("theme.save_button_hint"));
        m_buttons.getCancelButton().setHint(GlobalizationUtil
                                          .globalize("theme.cancel_button_hint"));
        add(m_buttons);
        
        addSubmissionListener(new ThemeSubmissionListener());
        addProcessListener(new ThemeProcessListener());
        addInitListener(new ThemeInitListener());
        // Form wide validation listener commented out temporarily. All validation
        // is done using a parameterValidationListener on each input component.
        // A form wide validation listener may be introduced later to display
        // an error summerize text and advise what to do.
        //addValidationListener(new ThemeValidationListener());  
    }

    /**
     * Processed if this form is cancelled.
     * 
     * @param s
     * @return 
     */
    @Override
    public boolean isCancelled(PageState s) {
        return m_buttons.getCancelButton().isSelected(s);
    }

    /**
     * 
     */
    private class ThemeSubmissionListener implements FormSubmissionListener {

        /**
         * 
         * @param e
         * @throws FormProcessException 
         */
        @Override
        public void submitted(FormSectionEvent e) 
                    throws FormProcessException {
            PageState state = e.getPageState();
            
            if (m_buttons.getCancelButton().isSelected(state)) {
                throw new FormProcessException(
                      "cancel pressed",
                      GlobalizationUtil.globalize("theme.cancel_button_hint")
                );
            }
        }
    }

    /**
     * Initializes the theme form with appropriate values if theme already
     * exists.
     */
    private class ThemeInitListener implements FormInitListener {
        @Override
        public void init(FormSectionEvent e) 
            throws FormProcessException {
            PageState state = e.getPageState();
            
            Theme theme = (Theme)m_theme.getSelectedTheme(state);
            if (theme == null) {
                m_title.setValue(state, null);
                m_description.setValue(state, null);
                m_url.setValue(state, null);
            } else {
                m_title.setValue(state, theme.getTitle());
                m_description.setValue(state, theme.getDescription());
                m_url.setValue(state, theme.getURL());
            }
        }
    }

    /**
     * ProcessListener class to act upon the themedirector form input (after
     * successful input validation if any). It's process method is the entry
     * point.
     */
    private class ThemeProcessListener implements FormProcessListener {

        /**
         * Process the form input data. The data are first stored into the
         * database and than the file system is synced if required. In case of
         * a new theme the default theme files (if existent) are copied. If for
         * an existing theme the name (url) has changed, the filesystem 
         * directories are modified accordingly.
         * 
         * @param e
         * @throws FormProcessException 
         */
        @Override
        public void process(FormSectionEvent e) 
                    throws FormProcessException {

            PageState state = e.getPageState();

            Theme theme = m_theme.getSelectedTheme(state);
            String oldURL = null;
            String newURL = null;
            if (theme == null) {
                /* We handle a new (created) theme. No previous values exist.*/
                newURL = (String)m_url.getValue(state);
                theme = new Theme((String)m_title.getValue(state), 
                                  (String)m_description.getValue(state), 
                                  newURL);
            } else {
                oldURL = theme.getURL();
                newURL = (String)m_url.getValue(state);
                theme.setTitle((String)m_title.getValue(state));
                theme.setDescription((String)m_description.getValue(state));
                theme.setURL(newURL);
            }

            // only add to the theme if it is published
            if (theme.getLastPublishedUser() != null) {
                Subsite.getConfig().addTheme(theme.getURL(), theme.getTitle());
            }

            if (!newURL.equals(oldURL) && oldURL != null) {
                Subsite.getConfig().removeTheme(oldURL);
            }

            theme.save();  // save theme to database
            m_theme.setSelectedObject(state, theme);

            // now that the db part is done, we do the file IO
            File newDirectory = null;
            File oldDirectory = null;
            try {
                // Determine the WebAppRoot should be something like this:
                // /var/ccm-devel/web/<username>/<projectname>/webapps/libreccm;
                File currentRoot = new File(Web.getServletContext().getRealPath("/"));
                
                newDirectory = new File(currentRoot, DEV_THEMES_BASE_DIR + 
                                        newURL);
                if (newDirectory.exists() && !newURL.equals(oldURL)) {
                    // this means there is a file in the file system but not in
                    // the database this should never happen because "validate"
                    // should catch it.
                    throw new UncheckedWrapperException
                        ("The file " + newDirectory.getName() + " already " +
                         "exists in the file system but not in the " +
                         "database.  Please contact your system " +
                         "administrator to fix the problem");
                }

                if (oldURL != null) {
                    oldDirectory = new File(currentRoot, DEV_THEMES_BASE_DIR + 
                                            oldURL);
                }

                if ( oldURL == null || !oldDirectory.exists()) {
                    // we make sure that the base directory exists and then we
                    // copy the files over.  
                    File baseDirectory = new File(currentRoot, 
                                                  DEV_THEMES_BASE_DIR);
                    if (!baseDirectory.exists()) {
                        baseDirectory.mkdirs();
                    }

                    copyDefaultTheme(newDirectory,null);
                //  Old way should be removed as soon as thorough testing the
                //  new method has been completed.
                //  copyDefaultFiles(newDirectory);

                    if (oldDirectory != null && !oldDirectory.exists()) {
                        s_log.warn("We were asked to move files from " +
                                   oldDirectory + " to " + newDirectory +
                                   " but the old directory did not exist so " +
                                   " we just created a new directory.");
                    }
                } else if (!oldURL.equals(newURL)) {
                    boolean devSuccess = oldDirectory.renameTo(newDirectory);

                    // we also need to rename the prod directory
                    File prodDir = 
                        new File(currentRoot, PROD_THEMES_BASE_DIR + oldURL);
                    boolean prodSuccess = true;
                    if (prodDir.exists()) {
                        prodSuccess = prodDir.renameTo
                            (new File(currentRoot, PROD_THEMES_BASE_DIR + newURL));
                    }
                    
                    if (!devSuccess) {
                        s_log.fatal("Error renaming " + oldDirectory + " to " +
                                    newDirectory);
                    } else if (!prodSuccess) {
                        s_log.fatal("Error renaming " + prodDir + " to " +
                                    new File(currentRoot, 
                                             PROD_THEMES_BASE_DIR + newURL));
                    } else {
                        // now, we need to update the sites so that point at
                        // the old directory...we need them to point to the
                        // new directory
                        DataCollection collection = 
                                         SessionManager
                                         .getSession()
                                         .retrieve(Site.BASE_DATA_OBJECT_TYPE);
                        collection.addEqualsFilter(Site.STYLE_DIRECTORY,
                                                   oldURL);
                        while (collection.next()) {
                            Site site = new Site(collection.getDataObject());
                            site.setStyleDirectory(newURL);
                        }
                    }
                }
            } catch (IOException ex) {
                String message = "Error moving/creating files using newURL " +
                    newDirectory + " and oldURL " + oldURL;
                s_log.error(message, ex);
                throw new UncheckedWrapperException(message, ex);
            }
        }
    }

    /**
     * Copies a complete directory containing the default theme to a newly
     * created theme's directory without any filtering or other processing.
     * It assumes, that the source directory contains a complete and working
     * set of theme files.
     * 
     * Developer's note: Why not move into private class ThemeProcessListener
     * where it is used (exclusivly).
     * 
     * @param newThemeDirectory     specifies the target directory. Must not
     *                              be null.
     * @param defaultThemeDirectory Directory containing a complete set of
     *                              theme files intended as default theme.
     *                              If null the default theme directory is
     *                              retrieved from ThemeDirector config
     * 
     * @throws IOException 
     */
    private void copyDefaultTheme(File newThemeDirectory, 
                                  File defaultThemeDirectory) throws IOException {
        if (defaultThemeDirectory == null) {
            defaultThemeDirectory = new File(
                                    Web.getServletContext().getRealPath("/")
                                    + ThemeDirector.getConfig().getDefaultThemePath());
        }
            
        FileUtils.copyDirectory(defaultThemeDirectory, 
                                newThemeDirectory);
    }


    /**
     * Themedirector-specific parameter validation listener, verifies that the
     * URL parameter value contains only valid characters.
         * Helper method to checks the form of the url...specifically, we are
         * only allowing [A-Z,a-z,0-9,_,-].
     */
    private class  URLFormValidationListener 
                   extends GlobalizedParameterListener {
        
        /**
         * Default Constructor setting a predefined label as error message.
         */
        public URLFormValidationListener() {
            setError(GlobalizationUtil.globalize(
                     "theme.form.url_can_contain_only_characters")
            );
        }

        /**
         * Validate Method required and used to validate input.
         *
         * @param e ParameterEvent containing the data
         */
        @Override
        public void validate(ParameterEvent e) {

            ParameterData data = e.getParameterData();
            Object value = data.getValue();
            PageState state = e.getPageState();
            String url = (String)m_url.getValue(state);

            if (value != null) {
                String urlString = value.toString().trim();

                for (int i = 0; i < urlString.length(); i++) {
                    char c = urlString.charAt(i);
                    if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')
                            || ('0' <= c && c <= '9') || c == '_' || c == '-')) {
                        // urlString contains illegal character(s)
                        // adds an error message to the widget object.
                        data.addError(getError());
                        return;
                    }
                }
                // non-empty value, just return
                return;
            }
            // Empty or no illegal character found, just  return
            return;

        }  // end method validatge
    }  // end private class URLFormValidationListener


    /**
     * Themedirector-specific parameter validation listener, verifies that the
     * URL parameter is unique.
     */
    private class  UniqueURLValidationListener 
                   extends GlobalizedParameterListener {
        
        /**
         * Default Constructor setting a predefined label as error message.
         */
        public UniqueURLValidationListener() {
            setError(GlobalizationUtil.globalize(
                     "theme.form.url_already_exists")
            );
        }

        /**
         * Validate Method required and used to validate input.
         *
         * @param e ParameterEvent containing the data
         * @pre URL parameter is not empty and contains valid characters only
         *      as ensured by previously invoked ValidationListeners.
         */
        @Override
        public void validate(ParameterEvent e) {

            ParameterData data = e.getParameterData();
            Object value = data.getValue();
            PageState state = e.getPageState();
            String url = (String)m_url.getValue(state);

            if (value != null) {
                String urlString = value.toString().trim();

                DataCollection collection = SessionManager.getSession()
                                            .retrieve(Theme.BASE_DATA_OBJECT_TYPE);
                collection.addEqualsFilter("lower(" + Theme.URL + ")", 
                                           urlString.toLowerCase());
                Theme currentTheme = (Theme)m_theme.getSelectedObject(state);
                if (currentTheme != null) {
                    collection.addNotEqualsFilter(Theme.ID, currentTheme.getID());
                }

                if ( collection.size() > 0) {
                    // urlString is not unique but already exists.
                    // adds an error message to the widget object.
                    data.addError(getError());
                    return;
                }

                // unique value, just return
                return;
            }
            // null or any other condition not able to handle here. Just  return

        }  // end method validate

    }  // end private class UniqueURLValidationListener


    /**
     * ValidationListener class to check the themedirector's form input data.
     * It's validate method is the entry point and executed when submitting
     * the form.
     */
    private class ThemeValidationListener implements FormValidationListener {

        /**
         * Does the actual validation.
         * 
         * @param fse
         * @throws FormProcessException 
         */
        @Override
        public void validate(FormSectionEvent fse) 
                    throws FormProcessException {

            PageState state = fse.getPageState();


            // Nothing to do at the Moment
            // We should add something like that:
            // - acquire a formdata object
            // - iterate ober the formdata widgets and check for an 
            //   added error message
            // - Add the  error to an error list
            // - If at least on error has been found, throw a FormProcessException
            //   With a summerizing text and a list of errors found.
            // Text from a FPE is by default shown just below the action buttons
            // (Save / Cancel)
            

            // in case of an error in any of the input form widgets,
            // throw a FPE in give a summerizing message and advice.
            // throw new FormProcessException
            //             ("[String]",
            //              GlobalizationUtil.globalize("key",errorDetails)
            //             );
            //    }


        } 

    }  // end ThemeValidationListener




     // ///////////////////////////////////////////////////////////////////////
     // Outdated CODE
     // Temporarily retained for easy reference until migration is complete.
     // ///////////////////////////////////////////////////////////////////////


    /**
     * Copies the default theme files to the new directory using a
     * Manifest file to determine the files to copy.
     * 
     * NOTE:
     * Old way should be removed as soon as thorough testing the
     * new method has been completed.
     * 
     * 
     * @param newDirectory specifies the target directory
     * @throws IOException 
     */
/*
    private void copyDefaultFiles(File newDirectory) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream
            (ThemeDirector.getConfig().getDefaultThemeManifest());

        if (is == null) {
            throw new IllegalStateException
                ("no such resource: " + 
                 ThemeDirector.getConfig().getDefaultThemeManifest());
        }
        
        if ( !newDirectory.mkdirs() ) {
        	throw new UncheckedWrapperException("Cannot create theme directory "
                                                +newDirectory.getAbsolutePath());
        }

        ManifestReader reader = 
            new FileWriterManifestReader(is, newDirectory);
        reader.processFile();
    }
*/

    /**
     * 
     * NOTE: Part of outdated way to copy a default theme. Copying is now done
     * without Manifest file. Class chould be completely removed as soon as
     * QA testing is completed.
     */
/*
    private class FileWriterManifestReader extends ManifestReader {
        private final File m_newDirectory;
        private final String m_directoryFilter;

         FileWriterManifestReader(InputStream stream, File newDirectory) {
            super(stream);
            m_newDirectory = newDirectory;

            m_directoryFilter = ThemeDirector.getConfig().getDefaultThemePath();
        }

        @Override
        public void processManifestFileLine(InputStream is,
                                            String filePath,
                                            boolean isStyleFile) {
            if (!isStyleFile || (m_directoryFilter != null && !filePath.startsWith(m_directoryFilter))) {
                return;
            }

            try {
                // If this is not in the base directory then we must
                // first create its directory before creating the file
                String newFileName = filePath;
                if (m_directoryFilter != null) {
                    newFileName = filePath.substring(m_directoryFilter.length());
                }
                if (newFileName.indexOf("/") > -1) {
                    String newDir = newFileName.substring
                        (0, newFileName.lastIndexOf("/"));
                    File dir = new File(m_newDirectory, newDir);
                    dir.mkdirs();
                }
                
                // we can finally create and write the file
                File newFile = new File(m_newDirectory, newFileName);
                newFile.createNewFile();
                
                FileOutputStream os = new FileOutputStream(newFile);
                IO.copy(is, os);
                is.close();
                os.close();
            } catch (IOException e) {
                throw new UncheckedWrapperException(
                    "Error reading from " + filePath + 
                    " or writing to " + m_newDirectory.getAbsolutePath(), e);
            }
        } 
    }
*/
}
