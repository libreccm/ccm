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
import com.arsdigita.london.theme.ThemeConstants;
import com.arsdigita.london.theme.ui.listeners.CancelListener;
import com.arsdigita.london.theme.util.GlobalizationUtil;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
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
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Cancellable;
import com.arsdigita.toolbox.ui.SelectionPanel;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;

import java.math.BigDecimal;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

/**
 *  This is the base page for controlling themes in the system.  It
 *  contains a list of the themes on the left side and it manages the state
 *  to show the correct forms/containers on the left
 */
public class ThemeControlPanel extends SelectionPanel implements ThemeConstants {
    
    private static final Logger s_log = Logger.getLogger(ThemeControlPanel.class);

    private ThemeSelectionModel m_theme;
    private ThemeContainer m_themeContainer;
    private Form m_themeForm;
    private BigDecimalParameter m_defaultThemeParam = new BigDecimalParameter( "defaultTheme" );

    public ThemeControlPanel() {
        super(new Label(GlobalizationUtil.globalize("theme.available_themes")),
              new ThemeListModelBuilder());

        setIntroPane(new Label(GlobalizationUtil.globalize
                               ("theme.select_or_create")));

        ((List)getSelector()).setEmptyView
            (new Label(GlobalizationUtil.globalize("theme.none_available")));
        
        // add the theme container
        m_theme = new ThemeSelectionModel(getSelectionModel());
        m_themeContainer = new ThemeContainer(m_theme, getBody());
        setItemPane(m_themeContainer);

        // add the "create a theme" form
        m_themeForm = new ThemeForm("themeForm", m_theme);
        m_themeForm.addSubmissionListener
            (new CancelListener((Cancellable)m_themeForm, getBody()));
        m_themeForm.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e) 
                    throws FormProcessException {
                    resetPane(e.getPageState());
                }
            });

        ActionLink addThemeLink = 
            new ActionLink(new Label(GlobalizationUtil.globalize
                                     ("theme.create_new_theme")));
        addThemeLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_theme.setSelectedObject(e.getPageState(), null);
                    m_themeForm.setVisible(e.getPageState(), true);
                }
            });
        getBody().add(m_themeForm);
        getBody().connect(addThemeLink, m_themeForm);
        addAction(addThemeLink, ActionGroup.ADD);

        getBody().add(new Label(GlobalizationUtil.globalize("theme.download_default_base_styles")));        

        Link downloadFilesLink = new Link(new Label(GlobalizationUtil.globalize
                               ("theme.download_default_base_styles")), 
                                      "download/" + ALL_STYLES_ZIP_NAME);
        addAction(downloadFilesLink, ActionGroup.ADD);

        Form defaultThemeForm = createDefaultThemeForm();
        addAction( defaultThemeForm );
    }

    private Form createDefaultThemeForm() {

        Form defaultThemeForm = new Form("defaultThemeForm", new SimpleContainer());
        defaultThemeForm.add( new Label( GlobalizationUtil.globalize
                               ( "theme.set_default_theme" ) ) );

        SingleSelect themes = new SingleSelect( m_defaultThemeParam );
        themes.addOption( new Option( null, new Label( GlobalizationUtil.globalize( "theme.none" ) ) ) );
        try {
            themes.addPrintListener( new PrintListener() {
                public void prepare( PrintEvent ev ) {
                    SingleSelect target = (SingleSelect) ev.getTarget();

                    DataCollection options = SessionManager.getSession().retrieve
                        ( Theme.BASE_DATA_OBJECT_TYPE );
                    options.addNotEqualsFilter( Theme.LAST_PUBLISHED_DATE, null );
                    options.addOrder( Theme.TITLE );
                    while( options.next() ) {
                        target.addOption( new Option( options.get( Theme.ID ).toString(),
                                                      options.get( Theme.TITLE ).toString() ) );
                    }
                }
            } );
        } catch( TooManyListenersException ex ) {
            // Don't be stupid
            throw new UncheckedWrapperException( "An impossible, pointless exception occurred", ex );
        }
        defaultThemeForm.add( themes );

        defaultThemeForm.add( new Submit( GlobalizationUtil.globalize( "theme.save" ) ) );

        defaultThemeForm.addInitListener( new FormInitListener() {
            public void init( FormSectionEvent ev ) {
                FormData data = ev.getFormData();

                ThemeApplication app = (ThemeApplication) Web.getContext().getApplication();
                Theme theme = app.getDefaultTheme();

                if( null != theme )
                    data.put( m_defaultThemeParam.getName(), theme.getID() );
            }
        } );

        defaultThemeForm.addProcessListener( new FormProcessListener() {
            public void process( FormSectionEvent ev ) {
                FormData data = ev.getFormData();
                BigDecimal themeID = (BigDecimal) data.get( m_defaultThemeParam.getName() );

                Theme theme = null;
                if( null != themeID ) {
                    theme = Theme.retrieve( themeID );
                }

                ThemeApplication app = (ThemeApplication) Web.getContext().getApplication();
                app.setDefaultTheme( theme );
            }
        } );

        return defaultThemeForm;
    }

    private void resetPane(PageState state) {
        getBody().reset(state);
        if (getSelectionModel().isSelected(state)) {
            s_log.debug("The selection model is selected; displaying " +
                        "the item pane");
            getBody().push(state, getItemPane());
        }
    }
}
