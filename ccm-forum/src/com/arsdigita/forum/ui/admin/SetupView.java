/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
 *
 */
package com.arsdigita.forum.ui.admin;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.ui.Constants;
import com.arsdigita.forum.util.GlobalizationUtil;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

/**
 * Form that allows forum admin to set options 
 * that apply to this forum instance
 */
public class SetupView  extends Form
                        implements FormInitListener, FormSubmissionListener,
                                   FormProcessListener, Constants {

    private static final Logger s_log = Logger.getLogger(SetupView.class);
    // values for checkboxes
    private static final String MODERATED = "moderated";
    private static final String PUBLIC = "public";
    private static final String NOTICEBOARD = "noticeboard";
    private static final String ALLOW_FILES = "filesAllowed";
    private static final String ALLOW_IMAGES = "imagesallowed";
    private static final String AUTOSUBSCRIBE_THREAD_STARTERS = "autosubscribe";
    private static final String NO_CATEGORY_POSTS_ALLOWED = "nocategory";
    private static final String ANONYMOUS_POSTS_ALLOWED = "anonymous";

    private CheckboxGroup m_settings;
    private TextField m_expiry;
    private SaveCancelSection m_saveCancel;
    private TextArea m_introduction;
    private TextField m_title;

    /**
     * Constructor to create the setup panel.
     */
    public SetupView() {
        super("setupForm", new SimpleContainer("forum:setup", FORUM_XML_NS));

        // preliminary step 1: Create a group of options to determine various
        // properties of this forum
        m_settings = new CheckboxGroup("settings");

        m_settings.addOption(new Option(MODERATED,
                                        new Label(GlobalizationUtil.gz(
                                            "forum.ui.settings.moderated"))
                                        ));
        m_settings.addOption(new Option(PUBLIC,
                                        new Label( GlobalizationUtil.gz(
                                            "forum.ui.settings.public"))
                                        ));
        m_settings.addOption(new Option(NOTICEBOARD,
                                        new Label(GlobalizationUtil.gz(
                                            "forum.ui.settings.noticeboard"))
                                        ));
        m_settings.addOption(new Option(ALLOW_FILES,
                                        new Label(GlobalizationUtil.gz(
                                            "forum.ui.settings.allowFiles"))
                                        ));
        m_settings.addOption(new Option(ALLOW_IMAGES,
                                        new Label(GlobalizationUtil.gz(
                                            "forum.ui.settings.allowImages"))
                                       ));
        m_settings.addOption(new Option(AUTOSUBSCRIBE_THREAD_STARTERS,
                                        new Label(GlobalizationUtil.gz(
                                            "forum.ui.settings.autosubscribe"))
                                       ));

        m_settings.addOption(new Option(NO_CATEGORY_POSTS_ALLOWED,
                                        new Label(GlobalizationUtil.gz(
                                            "forum.ui.settings.noCategoryPosts"))
                                       ));
        m_settings.addOption(new Option(ANONYMOUS_POSTS_ALLOWED,
                                        new Label(GlobalizationUtil.gz(
                                            "forum.ui.settings.anonymousPosts"))
                                       ));

        // preliminary step 2: Create a widget to determin the expiration limit
        // for the forum GUI
        m_expiry = new TextField(new IntegerParameter("expiry"));
        m_expiry.setMetaDataAttribute("label", (String) GlobalizationUtil.gz(
                "forum.ui.noticeboard.expiry_after").localize());

        // preliminary step 3: Create a Save - Discard widget for the pane
        m_saveCancel = new SaveCancelSection();
        m_saveCancel.getSaveButton().setButtonLabel(GlobalizationUtil.gz(
                "forum.ui.settings.save"));
        
        // ////////////////////////////////////////////////////////////////////
        // Create the pane
        // ////////////////////////////////////////////////////////////////////

        /* Introductory text fiel                                            */
        m_introduction = new TextArea("introduction", 8, 60, TextArea.SOFT);
        m_introduction.addValidationListener(
                           new StringInRangeValidationListener(0, 4000, 
                                   GlobalizationUtil.gz(
                                   "forum.ui.validation.introduction_too_long")
                           ));
        m_introduction.setMetaDataAttribute("label", (String) GlobalizationUtil.gz(
                "forum.ui.settings.introduction").localize());

        m_title = new TextField("title");
        m_title.setMetaDataAttribute("label", (String) GlobalizationUtil.gz(
                "forum.ui.settings.title").localize());
        m_title.setSize(70);

        add(m_title);
        add(m_introduction);
        add(m_settings);
        add(m_expiry);
        add(m_saveCancel);

        addInitListener(this);
        addSubmissionListener(this);
        addProcessListener(this);
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        Forum forum = ForumContext.getContext(state).getForum();
        Set settingsSet = new HashSet();
        if (forum.isModerated()) {
            settingsSet.add(MODERATED);
        }
        if (forum.isNoticeboard()) {
            settingsSet.add(NOTICEBOARD);
        }
        if (forum.allowFileAttachments()) {
            settingsSet.add(ALLOW_FILES);
        }
        if (forum.allowImageUploads()) {
            settingsSet.add(ALLOW_IMAGES);
        }
        if (forum.autoSubscribeThreadStarter()) {
            settingsSet.add(AUTOSUBSCRIBE_THREAD_STARTERS);
        }
        if (forum.noCategoryPostsAllowed()) {
            settingsSet.add(NO_CATEGORY_POSTS_ALLOWED);
        }
        if (forum.anonymousPostsAllowed()) {
            settingsSet.add(ANONYMOUS_POSTS_ALLOWED);
        }
        m_settings.setValue(state, settingsSet.toArray());

        m_expiry.setValue(state, new Integer(forum.getExpireAfter()));

        m_introduction.setValue(state, forum.getIntroduction());

        m_title.setValue(state, forum.getTitle());

    }

    public void submitted(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();

        if (m_saveCancel.getCancelButton().isSelected(state)) {
            s_log.debug("cancelled");
            throw new FormProcessException("cancelled");
        }
    }

    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        Forum forum = ForumContext.getContext(state).getForum();
        String[] settingsArray = (String[]) m_settings.getValue(state);
        List settings = Collections.EMPTY_LIST;
        if (settingsArray != null) {

            settings = Arrays.asList(settingsArray);
        }
        forum.setModerated(settings.contains(MODERATED));
        forum.setPublic(settings.contains(PUBLIC));
        forum.setNoticeboard(settings.contains(NOTICEBOARD));
        // could remove any existing files but i don't think that would be desirable
        forum.setAllowFileAttachments(settings.contains(ALLOW_FILES));
        forum.setAllowImageUploads(settings.contains(ALLOW_IMAGES));
        forum.setAutoSubscribeThreadCreator(settings.contains(
                AUTOSUBSCRIBE_THREAD_STARTERS));
        forum.setNoCategoryPostsAllowed(settings.contains(
                NO_CATEGORY_POSTS_ALLOWED));
        forum.setAnonymousPostsAllowed(
                settings.contains(ANONYMOUS_POSTS_ALLOWED));

        forum.setTitle((String) m_title.getValue(state));
        forum.setIntroduction((String) m_introduction.getValue(state));
        Integer expiry = (m_expiry.getValue(state) == null) ? new Integer(0) : (Integer) m_expiry.
                getValue(state);
        int newExpiry = expiry.intValue();
        if (forum.getExpireAfter() != newExpiry) {
            forum.setExpireAfter(newExpiry);
        }

    }
}
