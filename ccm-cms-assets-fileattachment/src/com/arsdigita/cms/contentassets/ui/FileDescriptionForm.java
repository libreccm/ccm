/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringInRangeValidationListener;
import com.arsdigita.cms.contentassets.FileAttachment;
import com.arsdigita.cms.contentassets.FileAttachmentConfig;
import com.arsdigita.cms.contentassets.FileAttachmentGlobalize;
import com.arsdigita.cms.contentassets.util.FileAttachmentGlobalizationUtil;

/**
 * Form to edit the description of a file attachment. File description edit
 * form, default class for
 * <code>com.arsdigita.cms.contentassets.file_edit_form</code> configuration
 * parameter. Edits property Asset.description
 */
public class FileDescriptionForm extends FormSection implements
        FormInitListener, FormProcessListener, FormValidationListener,
        FormSubmissionListener {

    private static final FileAttachmentConfig s_config = FileAttachmentConfig
            .instanceOf();

    //private TextArea m_title;
    private TextField m_title;
    private TextArea m_descriptionDHTML;
    private TextArea m_description;
    private FileAttachmentSelectionModel m_fileModel;
    private Submit m_cancel;
    private Label titleLabel;
    private Label descLabel;

    /**
     * Creates a new form to edit the FileAttachment description by the item
     * selection model passed in.
     *
     * @param file The FileAttachmentSelectionModel to use to obtain the
     * FileAttachment to work on
     */
    public FileDescriptionForm(FileAttachmentSelectionModel file) {
        super(new ColumnPanel(2));
        m_fileModel = file;

        addWidgets();
        SaveCancelSection saveCancel = new SaveCancelSection();
        m_cancel = saveCancel.getCancelButton();
        add(saveCancel, ColumnPanel.FULL_WIDTH);

        addInitListener(this);
        addSubmissionListener(this);
        addValidationListener(this);
        addProcessListener(this);
    }

    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {

        //m_title = new DHTMLEditor("title");
        m_title = new TextField("title");
        m_title.setSize(32);
        titleLabel = new Label(FileAttachmentGlobalizationUtil
                .globalize("cms.contentassets.file_attachment.title"));
        add(titleLabel);
        add(m_title);

        m_descriptionDHTML = new DHTMLEditor("description");
//        m_descriptionDHTML.addValidationListener(new NotNullValidationListener());
        m_descriptionDHTML.addValidationListener(new StringInRangeValidationListener(
                0,
                s_config.getFileAttachmentDescriptionMaxLength(),
                FileAttachmentGlobalize.DescriptionTooLong(s_config.getFileAttachmentDescriptionMaxLength())));

        add(new Label(FileAttachmentGlobalizationUtil
                .globalize("cms.contentassets.file_attachment.caption_or_description")));

        add(m_descriptionDHTML);

        m_description = new TextArea("desc");
        descLabel = new Label(FileAttachmentGlobalizationUtil
                .globalize("cms.contentassets.file_attachment.desc"));
        add(descLabel);
        add(m_description);

    }

    /**
     * Submission listener. Handles cancel events.
     *
     * @param e the FormSectionEvent
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void submitted(FormSectionEvent e) throws FormProcessException {
        if (m_cancel.isSelected(e.getPageState())) {
            m_fileModel.clearSelection(e.getPageState());
            init(e);
            throw new FormProcessException(FileAttachmentGlobalizationUtil.globalize(
                    "cms.contentassets.file_attachment.cancelled"));
        }
    }

    /**
     * Validation listener.
     *
     * @param event the FormSectionEvent
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void validate(FormSectionEvent event) throws FormProcessException {
        PageState state = event.getPageState();
        FileAttachment file = m_fileModel.getSelectedFileAttachment(state);
        // test if the user made an input

        int inputlength = 0;
        if (file.getAdditionalInfo() != null && file.getAdditionalInfo().equals("caption")) {
            String title = (String) m_title.getValue(state);
            String desc = (String) m_descriptionDHTML.getValue(state);

            inputlength = title.length() + desc.length();
        } else {
            inputlength = ((String) m_description.getValue(state)).length();
        }

        if (inputlength <= 0) {
            throw new FormProcessException(FileAttachmentGlobalizationUtil
                    .globalize(
                            "cms.contentassets.file_attachment.input_mandatory"));
        }
    }

    /**
     * Init listener.
     *
     * @param fse the FormSectionEvent
     */
    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        if (m_fileModel.isSelected(state)) {
            setVisible(state, true);
            FileAttachment file = m_fileModel.getSelectedFileAttachment(state);

            if (file.getAdditionalInfo() != null && file.getAdditionalInfo().equals("caption")) {
                m_description.setVisible(state, false);
                descLabel.setVisible(state, false);
                m_descriptionDHTML.setValue(state, file.getDescription());
                m_descriptionDHTML.setVisible(state, true);
                String name = file.getName();
                m_title.setVisible(state, true);
                titleLabel.setVisible(state, true);
                if (name != null && name.equals("iscaption")) {
                    m_title.setValue(state, null);
                } else {
                    m_title.setValue(state, name);
                }
            } else {
                m_title.setVisible(state, false);
                titleLabel.setVisible(state, false);
                m_descriptionDHTML.setVisible(state, false);
                m_description.setVisible(state, true);
                descLabel.setVisible(state, true);
                m_description.setValue(state, file.getDescription());

            }
        } else {
            setVisible(state, false);
        }
    }

    /**
     * Process listener. Edits the file description.
     *
     * @param fse the FormSectionEvent
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        // save only if save button was pressed
        if (m_cancel.isSelected(state)) {
            // cancel button is selected
            m_fileModel.clearSelection(state);
        } else {
            if (m_fileModel.isSelected(state)) {
                FileAttachment file = m_fileModel
                        .getSelectedFileAttachment(state);

                if (file.getAdditionalInfo() != null && file.getAdditionalInfo().equals("caption")) {
                    file.setDescription((String) m_descriptionDHTML.getValue(state));
                    if (m_title.getValue(state) != null) {
                        file.setName((String) m_title.getValue(state));
                    } else {
                        file.setName("iscaption");
                    }
                } else {
                    file.setDescription((String) m_description.getValue(state));
                }
            }
            m_fileModel.clearSelection(state);
            init(fse);
        }
    }
}
