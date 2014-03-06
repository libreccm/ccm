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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.themedirector.Theme;
import com.arsdigita.themedirector.ThemeDirector;
import com.arsdigita.themedirector.ThemeDirectorConstants;
import com.arsdigita.themedirector.ui.listeners.CancelListener;
import com.arsdigita.themedirector.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Cancellable;
import com.arsdigita.toolbox.ui.SelectionPanel;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.util.TooManyListenersException;
import org.apache.log4j.Logger;

/**
 *  This is the base page for controlling themes in the system.  It
 *  contains a list of the themes on the left side and it manages the state
 *  to show the correct forms/containers on the left
 */
public class ThemeControlPanel extends SelectionPanel implements ThemeDirectorConstants {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int the runtime environment
     *  and set com.arsdigita.templating.ui.ThemeControlPanel=DEBUG 
     *  by uncommenting or adding the line.                                                   */
    private static final Logger LOGGER = Logger.getLogger(ThemeControlPanel.class);
    private final ThemeSelectionModel selectionModel;
    private final Form themeForm;
    private final BigDecimalParameter defaultThemeParam = new BigDecimalParameter("defaultTheme");

    public ThemeControlPanel() {
        super(new Label(GlobalizationUtil.globalize("theme.available_themes")),
              new ThemeListModelBuilder());

        setIntroPane(new Label(GlobalizationUtil.globalize("theme.select_or_create")));

        ((List) getSelector()).setEmptyView(new Label(GlobalizationUtil.globalize("theme.none_available")));

        // add the theme container
        selectionModel = new ThemeSelectionModel(getSelectionModel());
        final ThemeContainer themeContainer = new ThemeContainer(selectionModel, getBody());
        setItemPane(themeContainer);

        // add the "create a theme" form
        themeForm = new ThemeForm("themeForm", selectionModel);
        themeForm.addSubmissionListener(new CancelListener((Cancellable) themeForm, getBody()));
        themeForm.addProcessListener(new FormProcessListener() {
            @Override
            public void process(final FormSectionEvent event) throws FormProcessException {
                resetPane(event.getPageState());
            }

        });

        final ActionLink addThemeLink = new ActionLink(new Label(GlobalizationUtil.globalize("theme.create_new_theme")));
        addThemeLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                selectionModel.setSelectedObject(event.getPageState(), null);
                themeForm.setVisible(event.getPageState(), true);
            }
        });
        getBody().add(themeForm);
        getBody().connect(addThemeLink, themeForm);
        addAction(addThemeLink, ActionGroup.ADD);

        // add the "Download default base styxle" form
        // PB 2012-05: Download in ThemeDownloadServlet doesn't work, commented 
        // out for now. Not useful anyway because it downloads web tree of every
        // package installed, probably filtered by theme file extensions (xsl, 
        // css, etc).
/*        
         getBody().add(new Label(GlobalizationUtil
         .globalize("theme.download_default_base_styles")));        
         Link downloadFilesLink = new Link(new Label(GlobalizationUtil.globalize
         ("theme.download_default_base_styles")), 
         "download/" + ALL_STYLES_ZIP_NAME);
         addAction(downloadFilesLink, ActionGroup.ADD);
         */
        // add the "Select Standard Theme" form
        final Form defaultThemeForm = createDefaultThemeForm();
        addAction(defaultThemeForm);
    }

    private Form createDefaultThemeForm() {

        final Form defaultThemeForm = new Form("defaultThemeForm", new SimpleContainer());
        defaultThemeForm.add(new Label(GlobalizationUtil.globalize("theme.set_default_theme")));

        final SingleSelect themes = new SingleSelect(defaultThemeParam);
        themes.addOption(new Option(null, new Label(GlobalizationUtil.globalize("theme.none"))));
        try {
            themes.addPrintListener(new PrintListener() {
                @Override
                public void prepare(final PrintEvent event) {
                    final SingleSelect target = (SingleSelect) event.getTarget();
                    final DataCollection options = SessionManager.getSession().retrieve(Theme.BASE_DATA_OBJECT_TYPE);
                    options.addNotEqualsFilter(Theme.LAST_PUBLISHED_DATE, null);
                    options.addOrder(Theme.TITLE);
                    while (options.next()) {
                        target.addOption(new Option(options.get(Theme.ID).toString(),
                                                    options.get(Theme.TITLE).toString()));
                    }
                }
            });
        } catch (TooManyListenersException ex) {
            // Don't be stupid
            throw new UncheckedWrapperException(
                      "An impossible, pointless exception occurred", ex);
        }
        defaultThemeForm.add(themes);

        defaultThemeForm.add(new Submit(GlobalizationUtil.globalize("theme.save")));

        defaultThemeForm.addInitListener(new FormInitListener() {
            @Override
            public void init(final FormSectionEvent event) {
                final FormData data = event.getFormData();

                final ThemeDirector app = ThemeDirector.getThemeDirector();
                final Theme theme = app.getDefaultTheme();

                if (null != theme) {
                    data.put(defaultThemeParam.getName(), theme.getID());
                }
            }

        });

        defaultThemeForm.addProcessListener(new FormProcessListener() {
            @Override
            public void process(final FormSectionEvent event) {
                final FormData data = event.getFormData();
                final BigDecimal themeID = (BigDecimal) data.get(
                                           defaultThemeParam.getName());

                Theme theme = null;
                if (null != themeID) {
                    theme = Theme.retrieve(themeID);
                }

                final ThemeDirector app = ThemeDirector.getThemeDirector();
                app.setDefaultTheme(theme);
            }

        });

        return defaultThemeForm;
    }

    private void resetPane(final PageState state) {
        getBody().reset(state);
        if (getSelectionModel().isSelected(state)) {
            LOGGER.debug("The selection model is selected; displaying the item pane");
            getBody().push(state, getItemPane());
        }
    }

}
