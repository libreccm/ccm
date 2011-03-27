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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.themedirector.Theme;
import com.arsdigita.themedirector.ThemeDirectorConstants;
import com.arsdigita.themedirector.ThemeFile;
import com.arsdigita.themedirector.ThemeFileCollection;
import com.arsdigita.themedirector.ui.listeners.FileRemovalRequestListener;
import com.arsdigita.themedirector.util.GlobalizationUtil;
import com.arsdigita.themedirector.util.WhiteListFilenameFilter;
import com.arsdigita.toolbox.ui.FormatStandards;
import com.arsdigita.util.Files;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 *  This generates the xml to show all of the files for a given theme.
 *
 *  @author Randy Graebner &lt;randyg@redhat.com&gt;
 */
class ThemeFilesList extends SimpleContainer implements ThemeDirectorConstants {

    private static final Logger s_log =
        Logger.getLogger(ThemeFilesList.class);

    private final static String XML_FOLDER = THEME_XML_PREFIX + "folder";
    private final static String XML_FILE = THEME_XML_PREFIX + "file";

    private ThemeSelectionModel m_model;

    ThemeFilesList(ThemeSelectionModel model) {
        super("theme:fileList", XML_NS);
        m_model = model;
        Label heading = new Label(GlobalizationUtil.globalize("theme.files_list_dev"));
        heading.setClassAttr("heading");
        add(heading);
    }

    public void register(Page page) {
        page.addRequestListener(new FileRemovalRequestListener());
    }

    public void generateChildrenXML(PageState state, Element root) {
        super.generateChildrenXML(state, root);
        File currentRoot = new File(Web.getServletContext().getRealPath("/"));
        Theme theme = m_model.getSelectedTheme(state);
        File themes = new File(currentRoot, DEV_THEMES_BASE_DIR +
                               theme.getURL());

        Element folder = root.newChildElement(XML_FOLDER, XML_NS);
        folder.addAttribute("name", theme.getURL());
        folder.addAttribute("depth", "");

        HashMap themeFiles = new HashMap();
        ThemeFileCollection collection = theme.getDraftThemeFiles();
        if (collection != null) {
            while (collection.next()) {
                ThemeFile tfile = collection.getThemeFile();
                String tfilePath = themes.getAbsolutePath() + File.separator + tfile.getFilePath();
                themeFiles.put(tfilePath, tfile);
            }
        }

        generateFolderXML(state, themes, folder, themeFiles, "-");
    }

    private void generateFolderXML(PageState state,
                                   File baseDirectory,
                                   Element folderElement,
                                   Map themeFiles,
                                   String depth) {
        File tempFile = null;
        String[] list = baseDirectory.list();
        Element childElement = null;
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                tempFile = new File(baseDirectory, list[i]);
                if (tempFile.isDirectory()) {
                    childElement = folderElement.newChildElement(XML_FOLDER, XML_NS);
                    generateFolderXML(state, tempFile, childElement, themeFiles, depth+"-");
                } else {
                    childElement = folderElement.newChildElement(XML_FILE, XML_NS);
                    childElement.addAttribute("inWhiteList",
                                              String.valueOf(WhiteListFilenameFilter.inWhiteList(list[i])));
                    ThemeFile dbtFile = (ThemeFile) themeFiles.get(tempFile.getAbsolutePath());
                    childElement.addAttribute("inDatabase", String.valueOf(dbtFile != null));
                    if (dbtFile != null) {
                        childElement.addAttribute("fileID", dbtFile.getID().toString());
                        boolean scheduledForRemoval = dbtFile.isDeleted()
                            &&  dbtFile.getLastModifiedDate().after(new Date(tempFile.lastModified()));
                        childElement.addAttribute("isDeleted", String.valueOf(scheduledForRemoval));
                        if (!scheduledForRemoval) {
                            childElement.addAttribute("removeURL", state.toURL() + "&fileID=" + dbtFile.getID());
                        }
                    }
                }
                childElement.addAttribute
                    ("lastModified",
                     FormatStandards.formatDateTime(new Date(tempFile.lastModified())));
                childElement.addAttribute("size", Files.getPrettySize(tempFile));
                childElement.addAttribute("name", tempFile.getName());
                childElement.addAttribute("depth", depth);
            }
        }
    }
}
