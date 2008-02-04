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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.ui.Text;
import org.apache.log4j.Logger;

/**
 * Form for turning the noticeboard functionality on/off.
 */
public class NoticeboardFormSection extends FormSection {

    private static final Logger s_log = Logger
        .getLogger(NoticeboardFormSection.class);

    private Label m_statusOn;
    private Label m_statusOff;
    private Submit m_switchOn;
    private Submit m_switchOff;
    private Submit m_changeExpiry;

    private Label m_expiryLabel;
    private TextField m_expiry;

    public NoticeboardFormSection() {
        super(new ColumnPanel(3, true));
        addWidgets();
        addProcessListener(new ModFormProcessListener());
        addInitListener(new ModFormInitListener());
    }

    public void addWidgets() {
        add(new Label(
                Text.gz("forum.ui.noticeboard.label")));

        m_statusOn = new Label(
            Text.gz("forum.ui.noticeboard.status.on"));
        m_statusOff = new Label(
            Text.gz("forum.ui.noticeboard.status.off"));
        add(m_statusOn);
        add(m_statusOff);

        m_switchOn = new Submit(
            Text.gz("forum.ui.noticeboard.switch.on"));
        m_switchOff = new Submit(
            Text.gz("forum.ui.noticeboard.switch.off"));
        add(m_switchOn);
        add(m_switchOff);

        m_expiryLabel = new Label(
            Text.gz("forum.ui.noticeboard.expiry_after"));
        m_expiry = new TextField(new IntegerParameter("expiry"));
        m_changeExpiry = new Submit(
            Text.gz("forum.ui.noticeboard.change_expiry"));
        add(m_expiryLabel);
        add(m_expiry);
        add(m_changeExpiry);
    }


    private void calculateVisibility(PageState state) {
        Forum forum = ForumContext.getContext(state).getForum();

        boolean noticeboard = forum.isNoticeboard();

        m_statusOn.setVisible(state, noticeboard);
        m_statusOff.setVisible(state, !noticeboard);

        m_switchOn.setVisible(state, !noticeboard);
        m_switchOff.setVisible(state, noticeboard);

        m_expiry.setVisible(state, noticeboard);
        m_expiryLabel.setVisible(state, noticeboard);
        m_changeExpiry.setVisible(state, noticeboard);
    }

    private class ModFormProcessListener implements FormProcessListener {
        public void process(FormSectionEvent event) throws FormProcessException {
            PageState state = event.getPageState();
            Forum forum =  ForumContext.getContext(state).getForum();

            if (m_switchOn.isSelected(state)) {
                s_log.debug("Switch on pressed");
                forum.setNoticeboard(true);
            } else if (m_switchOff.isSelected(state)) {
                s_log.debug("Switch off pressed");
                forum.setNoticeboard(false);
            } else if (m_changeExpiry.isSelected(state)) {
                forum.setExpireAfter( ((Integer) m_expiry.getValue(state)).intValue());
            } else {
                s_log.debug("Something else pressed");
            }

            calculateVisibility(state);
        }
    }

    private class ModFormInitListener implements FormInitListener {
        public void init(FormSectionEvent event) throws FormProcessException {
            PageState state = event.getPageState();
            calculateVisibility(state);
            Forum forum =  ForumContext.getContext(state).getForum();
            m_expiry.setValue(state, new Integer(forum.getExpireAfter()));
        }
    }
}

