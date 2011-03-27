package com.arsdigita.themedirector.ui;

import com.arsdigita.themedirector.util.GlobalizationUtil;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.toolbox.ui.FormatStandards;
import com.arsdigita.themedirector.Theme;
import org.apache.log4j.Logger;

/**
 *  This just displays the basic information about a theme
 */
class ThemeProperties extends GridPanel {

    private static final Logger s_log = 
        Logger.getLogger(ThemeProperties.class);

    private ThemeSelectionModel m_model;

    ThemeProperties(ThemeSelectionModel model) {
        super(2);
        m_model = model;

        // it would seem like there should be a way to update all 
        // of these at the same time instead of having a different
        // print listener for each of them.

        Label titleLabel = 
            new Label(GlobalizationUtil.globalize("theme.title"));
        titleLabel.setFontWeight(Label.BOLD);
        add(titleLabel);
        Label header = new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label target = (Label)e.getTarget();
                    PageState state = e.getPageState();
                    Theme theme = m_model.getSelectedTheme(state);
                    target.setLabel(theme.getTitle(), state);
                }
            });
        add(header);

        Label descLabel =
            new Label(GlobalizationUtil.globalize("theme.description"));
        descLabel.setFontWeight(Label.BOLD);
        add(descLabel);
        Label description = new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label target = (Label)e.getTarget();
                    PageState state = e.getPageState();
                    Theme theme = m_model.getSelectedTheme(state);
                    if (theme.getDescription() == null) {
                        target.setLabel("---", state);
                    } else {
                        target.setLabel(theme.getDescription(), state);
                    }
                }
            });
        add(description);
                
        Label urlLabel =new Label(GlobalizationUtil.globalize("theme.url"));
        urlLabel.setFontWeight(Label.BOLD);
        add(urlLabel);
        Label url = new Label(new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label target = (Label)e.getTarget();
                    PageState state = e.getPageState();
                    Theme theme = m_model.getSelectedTheme(state);
                    if (theme.getURL() == null) {
                        target.setLabel("---", state);
                    } else {
                        target.setLabel(theme.getURL(), state);
                    }
                }
            });
        add(url);

        Label lastPubDateLabel = new Label(GlobalizationUtil.globalize("theme.last_published_date"));
        lastPubDateLabel.setFontWeight(Label.BOLD);
        add(lastPubDateLabel);
        Label lastPubDate = new Label(GlobalizationUtil.globalize("theme.not_yet_published_date"));
        lastPubDate.setFontWeight(Label.ITALIC);
        lastPubDate.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label target = (Label)e.getTarget();
                    PageState state = e.getPageState();
                    Theme theme = m_model.getSelectedTheme(state);
                    if (theme.getLastPublishedDate() != null) {
                        target.setLabel(FormatStandards.formatDateTime
                                        (theme.getLastPublishedDate()), state);
                    }
                }
            });
        add(lastPubDate);

        Label lastPubUserLabel = new Label(GlobalizationUtil.globalize("theme.last_published_user"));
        lastPubUserLabel.setFontWeight(Label.BOLD);
        add(lastPubUserLabel);
        Label lastPubUser = new Label(GlobalizationUtil.globalize("theme.not_yet_published_user"));
        lastPubUser.setFontWeight(Label.ITALIC);
        lastPubUser.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent e) {
                    Label target = (Label)e.getTarget();
                    PageState state = e.getPageState();
                    Theme theme = m_model.getSelectedTheme(state);
                    if (theme.getLastPublishedUser() != null) {
                        target.setLabel(theme.getLastPublishedUser().getName(),
                                        state);
                    }
                }
            });
        add(lastPubUser);
    }        
}
