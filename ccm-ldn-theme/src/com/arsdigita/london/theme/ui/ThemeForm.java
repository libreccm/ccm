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

package com.arsdigita.london.theme.ui;

import com.arsdigita.london.theme.Theme;
import com.arsdigita.london.theme.ThemeApplication;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.Label;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.IO;
import com.arsdigita.london.theme.ThemeConstants;
import com.arsdigita.london.theme.ThemeApplication;
import com.arsdigita.london.theme.util.GlobalizationUtil;
import com.arsdigita.london.theme.util.ManifestReader;
import com.arsdigita.london.subsite.Subsite;
import com.arsdigita.london.subsite.Site;
import com.arsdigita.toolbox.ui.Cancellable;
import com.arsdigita.web.Web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;



/**
 *  This is a class used to create themes and edit its basic properties
 *
 *  @author Randy Graenber &lt;randyg@redhat.com&gt;
 */
public class ThemeForm extends Form implements Cancellable, ThemeConstants {
    
    private static final Logger s_log = Logger.getLogger(ThemeForm.class);

    private ThemeSelectionModel m_theme;
    private TextField m_title;
    private TextArea m_description;
    private TextField m_url;
    private SaveCancelSection m_buttons;

    public ThemeForm(String name,
                     ThemeSelectionModel theme) {
        super(name, new GridPanel(2));
        setClassAttr("simpleThemeForm");
        setRedirecting(true);
        
        m_theme = theme;

        add(new Label(GlobalizationUtil.globalize("theme.title")));
        m_title = new TextField(new StringParameter("title"));
        m_title.addValidationListener(new NotEmptyValidationListener());
        m_title.setHint("Enter the title of the theme, up to 80 characters");
        m_title.setSize(40);
        add(m_title);

        add(new Label(GlobalizationUtil.globalize("theme.description")));
        m_description = new TextArea(new StringParameter("description"));
        m_description.setCols(40);
        m_description.setRows(4);
        m_description.setHint(
            "Enter a short description for the theme, up to 4000 characters"
        );
        add(m_description);

        add(new Label(GlobalizationUtil.globalize("theme.url")));
        m_url = new TextField(new StringParameter("url"));
        m_url.addValidationListener(new NotEmptyValidationListener());
        m_title.setSize(40);
        m_url.setHint(
            "Enter the url for the theme, eg 'holiday'"
        );
        add(m_url);
        
        m_buttons = new SaveCancelSection();
        m_buttons.getSaveButton().setHint("Save the details in the form");
        m_buttons.getCancelButton().setHint("Abort changes & reset the form");
        add(m_buttons);
        
        addSubmissionListener(new ThemeSubmissionListener());
        addProcessListener(new ThemeProcessListener());
        addInitListener(new ThemeInitListener());
        addValidationListener(new ThemeValidationListener());
    }

    // if this form is cancelled
    public boolean isCancelled(PageState s) {
        return m_buttons.getCancelButton().isSelected(s);
    }

    private class ThemeSubmissionListener implements FormSubmissionListener {
        public void submitted(FormSectionEvent e) 
            throws FormProcessException {
            PageState state = e.getPageState();
            
            if (m_buttons.getCancelButton().isSelected(state)) {
                throw new FormProcessException("cancel pressed");
            }
        }
    }

    private class ThemeInitListener implements FormInitListener {
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

    private class ThemeProcessListener implements FormProcessListener {
        public void process(FormSectionEvent e) 
            throws FormProcessException {
            PageState state = e.getPageState();

            Theme theme = m_theme.getSelectedTheme(state);
            String oldURL = null;
            String newURL = null;
            if (theme == null) {
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

            // only add the theme if it is published
            if (theme.getLastPublishedUser() != null) {
                Subsite.getConfig().addTheme(theme.getURL(), theme.getTitle());
            }

            if (!newURL.equals(oldURL) && oldURL != null) {
                Subsite.getConfig().removeTheme(oldURL);
            }

            theme.save();
            m_theme.setSelectedObject(state, theme);

            // now that the db part is done, we do the file IO
            File newDirectory = null;
            File oldDirectory = null;
            try {
                // The WebAppRoot should be something like this:
                // /var/ccm-devel/web/<username>/<projectname>/webapps/ccm-ldn-theme;
                File currentRoot = new File(Web.getServletContext().getRealPath("/"));
                
                newDirectory = new File(currentRoot, DEV_THEMES_BASE_DIR + 
                                        newURL);
                if (newDirectory.exists() && !newURL.equals(oldURL)) {
                    // this means there is a file in the file system
                    // but not in the database 
                    // this should never happen because "validate" should
                    // catch it.
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

                if (oldURL == null || !oldDirectory.exists()) {
                    // we make sure that the base directory exists and
                    // then we copy the files over.  
                    File baseDirectory = new File(currentRoot, 
                                                  DEV_THEMES_BASE_DIR);
                    if (!baseDirectory.exists()) {
                        baseDirectory.mkdirs();
                    }

                    copyDefaultFiles(newDirectory);

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
                            SessionManager.getSession().retrieve
                            (Site.BASE_DATA_OBJECT_TYPE);
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

    private class ThemeValidationListener implements FormValidationListener {
        public void validate(FormSectionEvent e) 
            throws FormProcessException {
            PageState state = e.getPageState();
            String url = (String)m_url.getValue(state);
            validateURLForm(state, url);
            validateURLUniqueness(state, url);

            // now, validate that the URL does not already exist if this
            // is actually a "new" and not an "edit"
            Theme theme = m_theme.getSelectedTheme(state);
            if (theme == null) {
                File currentRoot = new File(Web.getServletContext().getRealPath("/"));
                File newDirectory = new File(currentRoot, DEV_THEMES_BASE_DIR +
                                             url);
                if (newDirectory.exists()) {
                    throw new FormProcessException
                        ("A file with the name " + url + " already exists " +
                         "in the file system.  Contact your system " +
                         "administrator if you think this is an error.");
                }
            }
        }
    }

    /**
     *  This checks the form of the url...specifically, we are only allowing
     *  [A-Z,a-z,0-9,_,-].
     */
    public void validateURLForm(PageState state, String url) 
        throws FormProcessException {
        
        //Obviously, this is not at all globalized and should
        //only be used with English text....however, since we are dealing
        // with a string that will be in the file system and will not
        // be seen by the end user, this should be fine.
        for (int i = 0; i < url.length(); i++) {
            char c = url.charAt(i);
            if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ||
                  ('0' <= c && c <= '9') || c == '_' || c == '-')) {
                throw new FormProcessException
                    ("The URL can only contain A-Z, a-z, 0-9, _, and -.");
            }
        }
    }



    /**
     *  This makes sure no other theme has the same URL
     */
    public void validateURLUniqueness(PageState state, String url) 
        throws FormProcessException {
        
        if ( url != null ) {
            DataCollection collection = SessionManager.getSession()
                .retrieve(Theme.BASE_DATA_OBJECT_TYPE);
            collection.addEqualsFilter("lower(" + Theme.URL + ")", 
                                       url.toLowerCase());
            Theme currentTheme = (Theme)m_theme.getSelectedObject(state);
            if (currentTheme != null) {
                collection.addNotEqualsFilter(Theme.ID, currentTheme.getID());
            }

            if ( collection.size() > 0) {
                throw new FormProcessException
                    ("A theme with this url already exists");
            }
        }            
    }


    /**
     *  This copies the default theme files to the new directory that
     *  is specified by the pass in File
     */
    private void copyDefaultFiles(File newDirectory) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream
            (ThemeApplication.getConfig().getDefaultThemeManifest());

        if (is == null) {
            throw new IllegalStateException
                ("no such resource: " + 
                 ThemeApplication.getConfig().getDefaultThemeManifest());
        }
        
        if ( !newDirectory.mkdirs() ) {
        	throw new UncheckedWrapperException("Cannot create theme directory "+newDirectory.getAbsolutePath());
        }

        ManifestReader reader = 
            new FileWriterManifestReader(is, newDirectory);
        reader.processFile();
    }


    private class FileWriterManifestReader extends ManifestReader {
        private File m_newDirectory;
        private String m_directoryFilter;

        FileWriterManifestReader(InputStream stream, File newDirectory) {
            super(stream);
            m_newDirectory = newDirectory;

            m_directoryFilter = ThemeApplication.getConfig().getDefaultThemePath();
        }

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
}
