/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;


import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.util.GlobalizationUtil;

import org.apache.log4j.Logger;

/**
 * Form for adding and removing moderation for forums 
 * @author Nobuko Asakai (nasakai@redhat.com)
 */
public class ModerationFormSection extends FormSection {

    private static final Logger s_log = Logger
        .getLogger(ModerationFormSection.class);

    private Label m_statusOn;
    private Label m_statusOff;
    private Submit m_switchOn;
    private Submit m_switchOff;
    private Label m_warning;

    public ModerationFormSection() {
        super(new ColumnPanel(3, true));
        addWidgets();
        addProcessListener(new ModFormProcessListener());
        addInitListener(new ModForumInitListener());
    }

    public void addWidgets() {
        add(new Label(
                GlobalizationUtil.gz("forum.ui.moderate.label")));

        m_statusOn = new Label(
            GlobalizationUtil.gz("forum.ui.moderate.status.on"));
        m_statusOff = new Label(
            GlobalizationUtil.gz("forum.ui.moderate.status.off"));
        add(m_statusOn);
        add(m_statusOff);
        
        m_switchOn = new Submit(
            GlobalizationUtil.gz("forum.ui.moderate.switch.on"));
        m_switchOff = new Submit(
            GlobalizationUtil.gz("forum.ui.moderate.switch.off"));
        add(m_switchOn);
        add(m_switchOff);

        m_warning = new Label(
            GlobalizationUtil.gz("forum.ui.moderate.warning"));
        add(m_warning, ColumnPanel.FULL_WIDTH);
    }


    private void calculateVisibility(PageState state) {
        Forum forum = ForumContext.getContext(state).getForum();
        
        boolean moderated = forum.isModerated();
        
        m_statusOn.setVisible(state, moderated);
        m_statusOff.setVisible(state, !moderated);
        
        m_switchOn.setVisible(state, !moderated);
        m_switchOff.setVisible(state, moderated);
        
        m_warning.setVisible(state, moderated);
    }

    private class ModFormProcessListener implements FormProcessListener {
        public void process(FormSectionEvent event) throws FormProcessException {
            PageState state = event.getPageState();
            Forum forum =  ForumContext.getContext(state).getForum();
            
            if (m_switchOn.isSelected(state)) {
                s_log.debug("Switch on pressed");
                forum.setModerated(true);
            } else if (m_switchOff.isSelected(state)) {
                s_log.debug("Switch off pressed");
                forum.setModerated(false);
            } else {
                s_log.debug("Something else pressed");
            }
            
            calculateVisibility(state);
        }
    }

    private class ModForumInitListener implements FormInitListener {
        public void init(FormSectionEvent event) throws FormProcessException {
            PageState state = event.getPageState();
            calculateVisibility(state);
        }
    }    
}

