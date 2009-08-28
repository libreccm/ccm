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
package com.arsdigita.cms.ui.templates;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.Table;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.Template;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.TextAssetBodyLabelCellRenderer;
import com.arsdigita.cms.ui.authoring.TextAssetBodyPropertySheet;
import com.arsdigita.cms.ui.authoring.TextAssetBody;
import com.arsdigita.cms.ui.authoring.WorkflowLockedComponentAccess;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.xml.Document;
import org.apache.log4j.Logger;
import java.util.Map;
import java.util.Iterator;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Edits the body of a template
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: TemplateBody.java 754 2005-09-02 13:26:17Z sskracic $
 */
public class TemplateBody extends TextAssetBody {
    public static final String versionId =
        "$Id: TemplateBody.java 754 2005-09-02 13:26:17Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger(TemplateBody.class);

    private AuthoringKitWizard m_parent;

    /**
     * Construct a new TemplateBody component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current template
     *
     * @param parent The parent wizard which contains the form. The form
     *   may use the wizard's methods, such as stepForward and stepBack,
     *   in its process listener.
     */
    public TemplateBody(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel);
        m_parent = parent;

        // Reset the component when it is hidden
        parent.getList().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    reset(state);
                }
            });

        // Set the right component access on the forms -
        // FIXME: Update this for templating permissions !
        Component f = getComponent(FILE_UPLOAD);
        if (f != null) {
            setComponentAccess(FILE_UPLOAD,
                               new WorkflowLockedComponentAccess(f, itemModel));
        }
        Component t = getComponent(TEXT_ENTRY);
        setComponentAccess(TEXT_ENTRY,
                           new WorkflowLockedComponentAccess(t, itemModel));
    }

    protected DomainObjectPropertySheet getBodyPropertySheet(ItemSelectionModel assetModel) {
        TextAssetBodyPropertySheet sheet =
            new TextAssetBodyPropertySheet(assetModel);
        sheet.getColumn(1).setCellRenderer(new TemplateLabelCellRenderer());
        return sheet;
    }

    /**
     * Create a new text asset and associate it with the current item
     *
     * @param s the current page state
     * @return a valid TextAsset
     */
    protected TextAsset createTextAsset(PageState s) {
        throw new UnsupportedOperationException();
    }

    /**
     * Set additional parameters of a brand new text asset, such as the
     * parent ID, after the asset has been successfully uploaded
     *
     * @param s the current page state
     * @param a the new <code>TextAsset</code>
     */
    protected void updateTextAsset(PageState s, TextAsset a) {
        // Do nothing
    }

    /**
     * Adds the options for the mime type select widget 
     **/
    protected void setMimeTypeOptions(SingleSelect mimeSelect) {
        Map mimeTypes  = Template.SUPPORTED_MIME_TYPES;
        Iterator keys = mimeTypes.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            mimeSelect.addOption
                (new Option(key, 
                            new Label((GlobalizedMessage)mimeTypes.get(key))));
        }
    }

    /* overridable method to put together the PageTextForm Component */
    protected void addTextWidgets(PageTextForm c) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding text widgets to " + c);
        }

        ColumnPanel panel = (ColumnPanel)c.getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        c.add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.text_type")));

        final SingleSelect mime = new SingleSelect(PageTextForm.MIME_TYPE);
        c.add(mime, ColumnPanel.LEFT);
        c.setMimeWidget(mime);
        setMimeTypeOptions(mime);

        mime.setClassAttr("displayOneOptionAsLabel");

        c.add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.edit_body_text")),
              ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);

        final TextArea text = new TextArea(PageTextForm.TEXT_ENTRY);
        c.add(text, ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);
        c.setTextWidget(text);

        text.setRows(10);
        text.setCols(50);
        text.setWrap(TextArea.SOFT);

        final SaveCancelSection saveCancel = new SaveCancelSection();
        c.add(saveCancel);
        c.setSaveCancel(saveCancel);

        c.addInitListener(c);
        c.addProcessListener(c);
    }

    /* overridable method to put together the PageFileForm Component */
    protected void addFileWidgets(PageFileForm c) {
        super.addFileWidgets(c);
        FileUploadSection uploadSection = c.getFileUploadSection();
        SingleSelect mimeWidget = uploadSection.getMimeTypeWidget();
        mimeWidget.clearOptions();
        setMimeTypeOptions(mimeWidget);
        mimeWidget.setDefaultValue(FileUploadSection.GUESS_MIME);
        mimeWidget.addOption
            (new Option(FileUploadSection.GUESS_MIME, new Label
                        (GlobalizationUtil.globalize
                         ("cms.ui.authoring.file_upload.auto_detect"))));
    }

    /**
     *  This is the form that is used to upload files.  This method can
     *  be used so that a subclass can use their own subclass of PageFileForm.
     */
    protected PageFileForm getPageFileForm() {
        return new TemplateFileForm();
    }
    
    protected String getDefaultMimeType() {
        return Template.JSP_MIME_TYPE;
    }

    protected class TemplateFileForm extends PageFileForm {

        /**
         * Validate file upload
         */
        public void validate(FormSectionEvent e) throws FormProcessException {
            super.validate(e);
            // the mime type has to be one of the specified allowable
            // mime types.

            FileUploadSection section = getFileUploadSection();
            MimeType mime = section.getMimeType(e);

            String mimeType = mime.getMimeType();
            if (!Template.SUPPORTED_MIME_TYPES.keySet().contains(mimeType)) {
                throw new FormProcessException
                    ("The mime type " + mimeType + "is not one of the " +
                     "supported Template Mime Types");
            }

            if (Template.XSL_MIME_TYPE.equals(mimeType)) {
                String content = getFileUploadContent(e.getPageState());
                try {
                    new Document(content);
                } catch (Exception ex) {
                    throw new FormProcessException
                        ("The uploaded file is not properly formatted XML: " +
                         ex.getMessage());
                }
            }
        }
    }

    protected class TemplateLabelCellRenderer extends TextAssetBodyLabelCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            Label label = null;
            if (MIME_TYPE_KEY.equals(key) && 
                value instanceof TextAsset) {
                MimeType type = ((TextAsset)value).getMimeType();
                if (type != null) {
                    GlobalizedMessage mimeTypeMessage = 
                        (GlobalizedMessage)Template.SUPPORTED_MIME_TYPES.get
                        (type.getMimeType());
                    if (mimeTypeMessage != null) {
                        return new Label(mimeTypeMessage, false);
                    } 
                }
            } 
            return super.getComponent(table, state, value, isSelected, 
                                      key, row, column);
        }
    }

}
