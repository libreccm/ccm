/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.formbuilder;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.formbuilder.FormItem;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.formbuilder.ui.ProcessListenerEditor;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;

public class FormActions extends ProcessListenerEditor {

    private Form m_lrForm;
    private RadioGroup m_localRemote;
    private Label m_remoteLabel;
    private TextField m_remoteUrl;
    private Submit m_switch;

    private Label m_urlValue;
    private Container m_urlEditLink;

    private static final Logger s_log =
        Logger.getLogger(FormActions.class);

    public FormActions(ItemSelectionModel model,
                       AuthoringKitWizard parent) {
        super("forms-cms",
              new FormSingleSelectionModel(model));
    }

    private FormItem getFormItem(PageState ps) {
        return (FormItem) ((FormSingleSelectionModel) m_form).getItemModel().getSelectedItem(ps);
    }


    protected void addComponents() {
        m_lrForm = new Form("locateRemoteForm", new ColumnPanel(2));

        ColumnPanel panel = (ColumnPanel) m_lrForm.getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        m_lrForm.add(new Label("Form mode:"));

        m_localRemote = new RadioGroup("remote");
        Option local = new Option(Boolean.FALSE.toString(), "Local");
        Option remote = new Option(Boolean.TRUE.toString(), "Remote");
        m_localRemote.addOption(local);
        m_localRemote.addOption(remote);
        m_localRemote.setLayout(RadioGroup.VERTICAL);
        m_lrForm.add(m_localRemote);

        m_remoteLabel = new Label("Remote URL");
        m_lrForm.add(m_remoteLabel);

        m_urlEditLink = new SimpleContainer();
        m_urlValue = new Label();
        m_urlValue.setOutputEscaping(false);
        m_urlEditLink.add(m_urlValue);
        m_urlEditLink.add(new Label("&nbsp;", false));
        ActionLink edit = new ActionLink("edit");
        edit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    m_urlEditLink.setVisible(state, false);
                    m_remoteUrl.setVisible(state, true);
                }
              });
        m_urlEditLink.add(edit);

        m_lrForm.add(m_urlEditLink);

        m_remoteUrl = new TextField("remoteUrl");
        m_lrForm.add(m_remoteUrl);

        m_switch = new Submit("submit", "Submit");
        m_lrForm.add(m_switch);

        m_lrForm.add(new Label());

        m_lrForm.add(new Label("&nbsp;", false), ColumnPanel.FULL_WIDTH);


        m_lrForm.addInitListener(new FormInitListener() {
                public void init(FormSectionEvent e) {
                    initWidgets(e);
                }
              });

        m_lrForm.addValidationListener(new FormValidationListener() {
                public void validate(FormSectionEvent e) {
                    PageState state = e.getPageState();
                    FormData data = e.getFormData();
                    String value = data.getString(m_remoteUrl.getName());
                    if (m_remoteUrl.isVisible(state)  &&
                            value != null  &&  value.length() > 0) {
                        try {
                            new URL(value);
                        } catch (MalformedURLException ex) {
                            data.addError(m_remoteUrl.getName(), "Please enter a valid URL");
                        }
                    }
                }
              });

        m_lrForm.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e) {
                    PageState state = e.getPageState();
                    if (m_switch.isSelected(state)) {
                        processWidgets(e);
                    }
                }
              });

        add(m_lrForm);
        super.addComponents();
    }

    public void register(Page page) {
        super.register(page);
        page.setVisibleDefault(m_remoteUrl, false);
    }

    protected void processWidgets(FormSectionEvent e) {
        s_log.debug("processWidgets");
        PageState state = e.getPageState();
        FormItem item = getFormItem(state);
        item.setRemote(Boolean.TRUE.toString().equals(m_localRemote.getValue(state)));
        if (m_remoteUrl.isVisible(state)) {
            item.setRemoteURL( (String) m_remoteUrl.getValue(state));
            m_urlEditLink.setVisible(state, true);
            m_remoteUrl.setVisible(state, false);
        }
        m_action.clearSelection(state);
        m_newAction.getSelection().clearSelection(state);
        initWidgets(e);
    }

    protected void initWidgets(FormSectionEvent e) {
        s_log.debug("initWidgets");
        PageState state = e.getPageState();
        FormItem item = getFormItem(state);
        m_localRemote.setValue(state, String.valueOf(item.isRemote()));
        // action_id being set denotes we're somewhere in edit/delete
        // action form.  Don't mess with visibility of forms then.
        BigDecimal action_id = (BigDecimal) m_action.getSelectedKey(state);
        s_log.debug("selected action: " + action_id);
        // type_id being set denotes we're somewhere in add
        // action form.  Don't mess with visibility of forms then.
        BigDecimal type_id = (BigDecimal) m_newAction.getSelection().getSelectedKey(state);
        s_log.debug("selected type: " + type_id);
        if (action_id == null  &&  type_id == null) {
            if (item.isRemote()) {
                m_urlValue.setLabel(item.getRemoteURL(), state);
                m_remoteUrl.setValue(state, item.getRemoteURL());
                // If this widget is visible, that means we've just
                // clicked on 'edit' action link.  Stop further
                // processing because visibility is already
                // sorted out in ActionListener.
                if (m_remoteUrl.isVisible(state)) {
                    return;
                }
                m_remoteLabel.setVisible(state, true);
                m_urlEditLink.setVisible(state, true);
                m_remoteUrl.setVisible(state, false);
                m_list_actions.setVisible(state, false);
                m_edit_action.setVisible(state, false);
            } else {
                m_remoteLabel.setVisible(state, false);
                m_remoteUrl.setVisible(state, false);
                m_urlEditLink.setVisible(state, false);
                m_list_actions.setVisible(state, true);
                m_edit_action.setVisible(state, false);
            }
        }
    }

}
