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

package com.arsdigita.london.navigation.ui.admin;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.navigation.QuickLink;
import com.arsdigita.london.util.ui.parameters.URLParameter;
import java.net.URL;


public class QuickLinkForm extends Form {

    private ACSObjectSelectionModel m_cat;
    private ACSObjectSelectionModel m_link;
    private TextField m_title;
    private TextArea m_desc;
    private TextField m_url;
    private TextField m_icon;
    private CheckboxGroup m_cascade;
    private SaveCancelSection m_buttons;

    // check box value
    private static final String SELECTED = "selected";

    public QuickLinkForm(String name,
                         ACSObjectSelectionModel cat,
                         ACSObjectSelectionModel link) {
        super(name, new SimpleContainer(Navigation.NAV_PREFIX +
                                        ":quickLinkForm",
                                        Navigation.NAV_NS));
        setRedirecting(true);

        m_cat = cat;
        m_link = link;

        addWidgets();

        m_buttons = new SaveCancelSection(new SimpleContainer());
        add(m_buttons);

        addInitListener(new LinkInitListener());
        addProcessListener(new LinkProcessListener());
        addSubmissionListener(new LinkSubmissionListener());
    }

    protected void addWidgets() {
        m_title = new TextField("title");
        m_title.setSize(50);
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.addValidationListener(new StringInRangeValidationListener(1, 300));
        m_title.setMetaDataAttribute("label", "Title");
        add(m_title);

        m_desc = new TextArea("description");
        m_desc.setRows(5);
        m_desc.setCols(50);
        m_desc.addValidationListener(new NotNullValidationListener());
        m_desc.addValidationListener(new StringInRangeValidationListener(0, 4000));
        m_desc.setMetaDataAttribute("label", "Description");
        add(m_desc);

        m_url = new TextField(new URLParameter("url"));
        m_url.setSize(50);
        m_url.addValidationListener(new NotNullValidationListener());
        m_url.addValidationListener(new StringInRangeValidationListener(1, 300));
        m_url.setMetaDataAttribute("label", "URL");
        add(m_url);

        m_icon = new TextField(new URLParameter("icon"));
        m_icon.setSize(50);
        m_icon.addValidationListener(new StringInRangeValidationListener(0, 300));
        m_icon.setMetaDataAttribute("label", "Icon");
        add(m_icon);

        m_cascade = new CheckboxGroup("cascade");
        m_cascade.addOption(new Option(SELECTED, "cascade to subcategories"));
           add(m_cascade);
    }

    private class LinkInitListener implements FormInitListener {
        public void init(FormSectionEvent ev)
            throws FormProcessException {
            PageState state = ev.getPageState();
            QuickLink link = (QuickLink)m_link.getSelectedObject(state);

            if (link == null) {
                m_title.setValue(state, null);
                m_desc.setValue(state, null);
                m_url.setValue(state, null);
                m_icon.setValue(state, null);
                m_cascade.setValue(state, null);
            } else {
                m_title.setValue(state, link.getTitle());
                m_desc.setValue(state, link.getDescription());
                m_url.setValue(state, link.getURL());
                m_icon.setValue(state, link.getIcon());
                if (link.cascade()) {
                    String[] selected = new String[]{SELECTED};
                    m_cascade.setValue(state, selected);
                } else {
                    m_cascade.setValue(state, null);
                }
            }
        }
    }

    private class LinkSubmissionListener implements FormSubmissionListener {
        public void submitted(FormSectionEvent ev)
            throws FormProcessException {
            PageState state = ev.getPageState();

            if (m_buttons.getCancelButton().isSelected(state)) {
                fireCompletionEvent(state);
                throw new FormProcessException("cancelled");
            }
        }
    }

    private class LinkProcessListener implements FormProcessListener {
        public void process(FormSectionEvent ev)
            throws FormProcessException {
            PageState state = ev.getPageState();
            QuickLink link = (QuickLink)m_link.getSelectedObject(state);

            if (link == null) {
                link = QuickLink.create((String)m_title.getValue(state),
                                        (String)m_desc.getValue(state),
                                        (URL)m_url.getValue(state),
                                        (URL)m_icon.getValue(state),
                                        m_cascade.getValue(state) != null);

                Category cat = (Category)m_cat.getSelectedObject(state);
                cat.addChild(link);
            } else {
                link.setTitle((String)m_title.getValue(state));
                link.setDescription((String)m_desc.getValue(state));
                link.setURL((URL)m_url.getValue(state));
                link.setIcon((URL)m_icon.getValue(state));
                link.setCascade(m_cascade.getValue(state) != null);
            }

            fireCompletionEvent(state);
        }
    }
}
