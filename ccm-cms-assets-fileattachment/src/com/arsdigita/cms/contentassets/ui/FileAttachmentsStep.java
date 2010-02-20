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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.FileAttachment;
import com.arsdigita.cms.ui.CMSContainer;
import com.arsdigita.cms.ui.SecurityPropertyEditor;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.ui.workflow.WorkflowLockedContainer;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.Classes;

/**
 * Authoring step for file attachments
 *
 * @author Scott Seago (sseago@redhat.com)
 * @version $Revision: #2 $ $DateTime: 2004/03/30 18:21:14 $
 * @version $Id: FileAttachmentsStep.java 1116 2006-04-20 13:55:17Z apevec $
 */
public class FileAttachmentsStep extends SecurityPropertyEditor {

    private final ItemSelectionModel m_item;
    private final AuthoringKitWizard m_parent;

    private CMSContainer m_display;
    private FileAttachmentsTable m_fileList;
    private FileAttachmentUpload m_uploadForm;

    private BigDecimalParameter m_fileParam = new BigDecimalParameter("fa");
    private FileAttachmentSelectionModel m_fileModel =
                    new FileAttachmentSelectionModel(m_fileParam);
    private Submit m_cancel;
    private Form m_dcForm;

    /**
     *
     * @param itemModel
     * @param parent
     */
    public FileAttachmentsStep(ItemSelectionModel itemModel,
                               AuthoringKitWizard parent) {
        m_parent = parent;
        m_item = itemModel;

        m_fileList = new FileAttachmentsTable(m_item, m_fileModel);
        m_display = new CMSContainer();
        //Main label
        Label mainLabel = new Label("This item does not have any associated files.");
        mainLabel.setFontWeight(Label.ITALIC);
        mainLabel.addPrintListener( new PrintListener() {
                public void prepare(PrintEvent event) {
                    PageState state = event.getPageState();
                    ContentItem item =  (ContentItem) m_item.getSelectedObject(state);
                    if (item != null) {
                        DataCollection files = FileAttachment.getAttachments(item);
                        Label mainTarget = (Label) event.getTarget();
                        if (files.isEmpty()) {
                            mainTarget.setLabel(
                                "This item does not have any associated files.");
                        } else {
                            mainTarget.setLabel("");
                        }
                    }
                }
            });
        m_display.add(mainLabel);
        m_display.add(m_fileList);
        setDisplayComponent(m_display);

        // The upload form.
        m_uploadForm = new FileAttachmentUpload(m_item);
        add("upload", "Upload a new file",
            new WorkflowLockedComponentAccess(m_uploadForm, m_item),
            m_uploadForm.getSaveCancelSection().getCancelButton());

        // File asset metadata form. 
        Form form = new Form("faEdit");
        Class editFormClass = FileAttachment.getConfig().getEditFormClass();
        FormSection editForm = (FormSection)
            Classes.newInstance(editFormClass,
                                new Class[] { FileAttachmentSelectionModel.class },
                                new Object[] { m_fileModel });
        form.add(editForm);

        WorkflowLockedContainer edit = new WorkflowLockedContainer(m_item);
        edit.add(form);
        add(edit);

        // Reset the editing when this component becomes visible
        m_parent.getList().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    PageState state = event.getPageState();
                    showDisplayPane(state);
                }
            });
    }

    /**
     * @return the parent wizard
     */
    public AuthoringKitWizard getParentWizard() {
        return m_parent;
    }

    /**
     * @return The item selection model
     */
    public ItemSelectionModel getItemSelectionModel() {
        return m_item;
    }

}
