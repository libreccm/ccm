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
 *
 */
package com.arsdigita.shortcuts.ui;

import java.math.BigDecimal;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.shortcuts.ShortcutUtil;
import com.arsdigita.shortcuts.Shortcut;
import com.arsdigita.shortcuts.util.GlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.log4j.Category;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.perl.MalformedPerl5PatternException;

public class ShortcutForm extends Form {

    private static final Category log = Category.getInstance(ShortcutForm.class
            .getName());

    private ACSObjectSelectionModel m_selected_shortcut;

    private TextField m_url;

    private TextField m_redirect;

    private final Submit m_submit;

    public ShortcutForm(ACSObjectSelectionModel selected_shortcut) {
        super("ShortcutForm");
        m_selected_shortcut = selected_shortcut;

        TrimmedStringParameter urlKeyParameter = new TrimmedStringParameter(
                "url");
        urlKeyParameter.addParameterListener(new NotEmptyValidationListener());
        m_url = new TextField(urlKeyParameter);

        TrimmedStringParameter redirectParameter = new TrimmedStringParameter(
                "redirect");
        redirectParameter
                .addParameterListener(new NotEmptyValidationListener());

        m_redirect = new TextField(redirectParameter);

        urlKeyParameter.addParameterListener(new ParameterListener() {
            @Override
            public void validate(ParameterEvent e) throws FormProcessException {
                ParameterData data = e.getParameterData();

                String key = (String) data.getValue();
                if (key == null) {
                    return; // Something else will handle
                }				// this

                Perl5Util perl = new Perl5Util();
                try {
                    if (!perl.match("/^(\\/[-a-zA-Z0-9_.]+)+\\/?$/", key)) {
                        data.addError(GlobalizationUtil.globalize(
                                "shortcuts.ui.invalid_key_descr"));
                        throw new FormProcessException(
                                "Invalid key",
                                GlobalizationUtil.globalize("shortcuts.ui.invalid_key")
                        );
                    }
                } catch (MalformedPerl5PatternException ex) {
                    throw new UncheckedWrapperException("bad regex", ex);
                }
            }
        });

        redirectParameter.addParameterListener(new ParameterListener() {
            @Override
            public void validate(ParameterEvent e) throws FormProcessException {
                ParameterData data = e.getParameterData();

                String url = (String) data.getValue();
                if (url == null) {
                    return; // Something else will handle
                }				// this

                url = url.toLowerCase();

                // Absolute local url is ok
                if (url.startsWith("/")) {
                    return;
                }

                // Fully qualified url is ok
                if (url.startsWith("http://")) {
                    return;
                }

                // And secure ones too
                if (url.startsWith("https://")) {
                    return;
                }

                data
                        .addError("You must enter an absolute path "
                                + "(starting with '/') or a fully "
                                + "qualified URL (starting with 'http://' or 'https://')");
                throw new FormProcessException("invalid URL");
            }
        });

        add(new Label("URL Key:"));
        add(m_url);
        add(new Label("Redirect:"));
        add(m_redirect);

        m_submit = new Submit("Save Shortcut");
        add(m_submit);

        addInitListener(new ShortcutInitListener());
        addProcessListener(new ShortcutFormProcessListener());
        addValidationListener(new ShortcutFormValidationListener());
    }

    private class ShortcutInitListener implements FormInitListener {

        @Override
        public void init(FormSectionEvent ev) throws FormProcessException {
            PageState state = ev.getPageState();
            BigDecimal shortcutKey = (BigDecimal) m_selected_shortcut
                    .getSelectedKey(state);
            if (shortcutKey == null) {
                log.debug("init form for empty shortcut");
                m_url.setValue(state, null);
                m_redirect.setValue(state, null);
            } else {
                log.debug("init form for shortcut " + shortcutKey);
                Shortcut shortcut = new Shortcut(shortcutKey);
                m_url.setValue(state, shortcut.getUrlKey());
                m_redirect.setValue(state, shortcut.getRedirect());
            }
        }
    }

    private class ShortcutFormValidationListener implements
            FormValidationListener {

        @Override
        public void validate(FormSectionEvent e) throws FormProcessException {

            PageState state = e.getPageState();

            // get currently edited shortcut
            BigDecimal shortcutKey = (BigDecimal) m_selected_shortcut
                    .getSelectedKey(state);

            String key = ShortcutUtil.cleanURLKey((String) m_url
                    .getValue(state));

            if (shortcutKey == null) {
                String target = ShortcutUtil.getTarget(key);
                if (target != null) {
                    m_url.addError(GlobalizationUtil.globalize(
                            "shortcuts.ui.key_already_exists"));
                    throw new FormProcessException(
                            "duplicate key",
                            GlobalizationUtil.globalize("shortcuts.ui.duplicate_key")
                    );
                }
            }

            int index = key.indexOf("/", 2);
            String base = key.substring(0, index + 1);
        }
    }

    private class ShortcutFormProcessListener implements FormProcessListener {

        @Override
        public void process(FormSectionEvent e) throws FormProcessException {

            PageState state = e.getPageState();

            BigDecimal shortcutKey = (BigDecimal) m_selected_shortcut.getSelectedKey(state);

            String url = ShortcutUtil.cleanURLKey((String) m_url.getValue(state));
            String redirect = (String) m_redirect.getValue(state);

            if (shortcutKey == null) {
                log.info("save new shortcut " + url);
                Shortcut shortcut = Shortcut.create(url, redirect);
                shortcut.save();
            } else {
                log.info("save updated shortcut " + shortcutKey + " " + url);
                Shortcut shortcut = new Shortcut(shortcutKey);
                shortcut.setUrlKey(url);
                shortcut.setRedirect(redirect);
                shortcut.save();
            }

            ShortcutUtil.repopulateShortcuts();

            m_redirect.setValue(state, "");
            m_url.setValue(state, "");
            m_selected_shortcut.clearSelection(state);
        }
    }

}
