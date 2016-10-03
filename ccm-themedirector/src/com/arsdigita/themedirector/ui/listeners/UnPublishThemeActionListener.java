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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.subsite.Subsite;
import com.arsdigita.themedirector.*;
import com.arsdigita.themedirector.ui.ThemeSelectionModel;
import com.arsdigita.themedirector.util.ThemeFileUtil;
import com.arsdigita.themedirector.util.WhiteListFilenameFilter;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.SessionManager;
import static com.arsdigita.themedirector.ThemeDirectorConstants.DEV_THEMES_BASE_DIR;
import static com.arsdigita.themedirector.ThemeDirectorConstants.PROD_THEMES_BASE_DIR;
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
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 * This unpublishes the theme and delete the files. This action means that the
 * user wants to delete the themes . This is done by deleting all the files from
 * the themes/publishedDir directory into the published directory.
 *
 * @author Konermann;
 */
public class UnPublishThemeActionListener implements ThemeDirectorConstants,
        ActionListener {

    private final ThemeSelectionModel m_model;

    /**
     * Constructor, just stores the ThemeSelectionModel.
     *
     * @param model the ThemeSelectionModel
     */
    public UnPublishThemeActionListener(ThemeSelectionModel model) {
        m_model = model;
    }

    /**
     *
     * @param e
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void actionPerformed(ActionEvent e) {
//        File defaultThemeDirectory = new File(
//                                    Web.getServletContext().getRealPath("/")
//                                    + ThemeDirector.getConfig().getDefaultThemePath());
        String defaultThemeDirectory = ThemeDirector.getConfig().getDefaultThemePath();
        Theme theme = m_model.getSelectedTheme(e.getPageState());
        String themeURL = theme.getURL();

        //check if the selected theme is the defaulttheme
        final ThemeDirector app = ThemeDirector.getThemeDirector();
        final Theme defaulttheme = app.getDefaultTheme();

        if (theme.equals(defaulttheme)) { //theme==defaulttheme

            //return error. User has to change default theme first
            // throw new FormProcessException("das zu löschende Theme ist das default Theme.");
        } else {

//            (2) Löschen der Themenfiles (und des Verzeichnisses) aus themes/publishedDir 
//            für jeden Application Servers, die zum Publizieren Theme gehören 
//            (die Dateien der development Version bleiben bestehen) löschen
//            (dazu gibt es bereits Routinen im package themedirectorUtil in den Manager
//               Klassen, die Du wiederverwenden kannst
//
//            (3) Löschen der entsprechenden Files in der DB. Auch dazu gibt es Routinen in den Manager Klassen
        
//        ApproveThemeActionListener.java published ein Theme. Dies muss rückgängig gemacht werden.
   
}

    }
}
