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

import com.arsdigita.themedirector.ui.listeners.ApproveThemeActionListener;
import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.themedirector.Theme;
import com.arsdigita.themedirector.ThemeDirectorConstants;
import com.arsdigita.themedirector.dispatcher.ThemeDownloadServlet;
import com.arsdigita.themedirector.util.GlobalizationUtil;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.ModalPanel;
import com.arsdigita.web.RedirectSignal;

import org.apache.log4j.Logger;

/**
 *  This displays information about a single theme
 *
 *  @author Randy Graebner &lt;randyg@redhat.com&gt;
 */
public class ThemeContainer extends SimpleContainer 
                            implements ThemeDirectorConstants {
    
    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int the runtime environment
     *  and set com.arsdigita.themedirector.ui.ThemeContainer=DEBUG 
     *  by uncommenting or adding the line.                                  */
    private static final Logger s_log = 
                                Logger.getLogger(ThemeContainer.class);

    private ThemeSelectionModel m_model;
    private ThemeForm m_editForm;
    private ThemeValidationPanel m_validationPanel;
    private SimpleContainer m_mainPanel;

    /**
     * Constructor.
     * 
     * @param model
     * @param parent
     */
    public ThemeContainer(ThemeSelectionModel model, final ModalPanel parent) {
        super();
        m_model = model;
        
        m_mainPanel = new GridPanel(1);

        add(new ThemeProperties(m_model));

        ActionLink editFormLink = addEditForm(m_mainPanel, parent);

        ActionGroup group = new ActionGroup();
        m_mainPanel.add(group);
        group.addAction(editFormLink, ActionGroup.EDIT);

        Link downloadFiles = 
            new Link(new Label(GlobalizationUtil.globalize
                               ("theme.download_dev_theme_files")), 
                     new PrintListener() {
                         public void prepare(PrintEvent e) {
                             Link link = (Link)e.getTarget();
                             PageState state = e.getPageState();
                             Theme theme = m_model.getSelectedTheme(state);
                             link.setTarget
                                 ("download/" + ThemeDownloadServlet
                                  .DEVELOPMENT_PREFIX + theme.getURL() +
                                  ".zip?" + ThemeDownloadServlet.THEME_ID + 
                                  "=" + theme.getID());
                         }
                     });
        group.addAction(downloadFiles, ActionGroup.EDIT);

        Link downloadProdFiles = new ProductionFilesDownloadLink(m_model);
        group.addAction(downloadProdFiles, ActionGroup.EDIT);


        ActionLink validateThemesLink = 
            new ActionLink(new Label(GlobalizationUtil.globalize("theme.validate_themes")));
        m_validationPanel = new ThemeValidationPanel(m_model, m_mainPanel);
        add(m_validationPanel);

        validateThemesLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_validationPanel.setVisible(e.getPageState(), true);
                    m_validationPanel.validateStylesheets(e.getPageState());
                    m_mainPanel.setVisible(e.getPageState(), false);
                }
            });
        group.addAction(validateThemesLink, ActionGroup.EDIT);


        ActionLink previewLink = new ActionLink(new Label
                           (GlobalizationUtil.globalize("theme.preview_themes")));

        previewLink.addActionListener(new PreviewThemeActionListener(m_model) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    boolean areValid = 
                        m_validationPanel.validateStylesheets(state);
                    if (areValid) {
                        super.actionPerformed(e);
                    } else {
                        m_validationPanel.setVisible(state, true);
                            m_mainPanel.setVisible(state, false);
                    }
                }
            });
        group.addAction(previewLink, ActionGroup.EDIT);


        ActionLink approveThemeLink = 
            new ActionLink
            (new Label(new PrintListener() {
                    public void prepare(PrintEvent e) {
                        Label target = (Label)e.getTarget();
                        PageState state = e.getPageState();
                        Theme theme = m_model.getSelectedTheme(state);
                        if (theme != null && 
                            theme.getLastPublishedDate() != null) {
                            target.setLabel(GlobalizationUtil.globalize
                                            ("theme.reapprove_themes").localize().toString(), state);
                        } else {
                            target.setLabel(GlobalizationUtil.globalize
                                            ("theme.approve_themes").localize().toString(), state);
                        }
                    }
                }));

        approveThemeLink.addActionListener
            (new ApproveThemeActionListener(m_model) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        PageState state = e.getPageState();
                        boolean areValid = 
                            m_validationPanel.validateStylesheets(state);
                        if (areValid) {
                            super.actionPerformed(e);
                        } else {
                            m_validationPanel.setVisible(state, true);
                            m_mainPanel.setVisible(state, false);
                        }
                    }
                });
        group.addAction(approveThemeLink, ActionGroup.ADD);

        m_mainPanel.add(new ThemeFilesList(m_model));
        add(m_mainPanel);
    }



    /**
     *  This adds the form to edit the properties of the panel. 
     */
    private ActionLink addEditForm(SimpleContainer panel, ModalPanel parent) {
        m_editForm = new ThemeForm("editTheme", m_model);
        panel.add(m_editForm);
        final ActionLink editFormLink = 
            new ActionLink(new Label
                           (GlobalizationUtil.globalize("theme.edit")));
        editFormLink.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_editForm.setVisible(e.getPageState(), true);
                    editFormLink.setVisible(e.getPageState(), false);
                }
            });
        m_editForm.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e) 
                    throws FormProcessException {
                    PageState state = e.getPageState();
                    editFormLink.setVisible(state, true);
                    m_editForm.setVisible(state, false);
                }
            });
        m_editForm.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent e) 
                    throws FormProcessException {
                    PageState state = e.getPageState();
                    if (m_editForm.isCancelled(state)) {
                        editFormLink.setVisible(state, true);
                        m_editForm.setVisible(state, false);
                        throw new FormProcessException("cancelled");
                    }
                }
            });
        return editFormLink;
    }


    /**
     *
     * @param page
     */
    @Override
    public void register(Page page) {
        page.setVisibleDefault(m_editForm, false);
        page.setVisibleDefault(m_validationPanel, false);
    }

    /**
     * 
     */
    private class ProductionFilesDownloadLink extends Link {
        
        /**
         * Constructor
         * @param model 
         */
        ProductionFilesDownloadLink(final ThemeSelectionModel model) {
            super(new Label(GlobalizationUtil.globalize
                            ("theme.download_prod_theme_files")),
                  new PrintListener() {
                      public void prepare(PrintEvent e) {
                          Link link = (Link)e.getTarget();
                          PageState state = e.getPageState();
                          Theme theme = model.getSelectedTheme(state);
                          link.setTarget
                              ("download/" + ThemeDownloadServlet
                               .PUBLISHED_PREFIX + theme.getURL() +
                               ".zip?" + ThemeDownloadServlet.THEME_ID + 
                               "=" + theme.getID());
                      }
                  });
        }

        /**
         * 
         * @param state
         * @return 
         */
        @Override
        public boolean isVisible(PageState state) {
            return super.isVisible(state) &&
                m_model.getSelectedTheme(state)
                .getLastPublishedDate() != null;
        }
    }


    /**
     *  This action class verifies that the stylesheets are valid
     *  and then redirects the user so that they can preview the site.
     */
    private class PreviewThemeActionListener implements ActionListener {
        private ThemeSelectionModel m_themeModel;
        PreviewThemeActionListener(ThemeSelectionModel model) {
            m_themeModel = model;
        }
        public void actionPerformed(ActionEvent e) {
            // the action currently does not do anything but it should
            // verify the stylesheets

            throw new RedirectSignal
                (PREVIEW_PREFIX + "/" + 
                 m_themeModel.getSelectedTheme(e.getPageState())
                 .getURL(), false);
        }
    }
}
