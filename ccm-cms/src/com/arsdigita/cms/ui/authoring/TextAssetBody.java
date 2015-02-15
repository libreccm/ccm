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
package com.arsdigita.cms.ui.authoring;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.Asset;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.CMSConfig;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.cms.ui.SecurityPropertyEditor;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.mimetypes.MimeTypeStatus;
import com.arsdigita.mimetypes.TextMimeType;
import com.arsdigita.mimetypes.converters.ConvertFormat;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * Displays the mime-type and the body of a single {@link TextAsset}. Maintains
 * a form or uploading files into the text body of the asset, and a form for
 * editing the text of the asset.
 * <p>
 * Unlike most other authoring components, this component does not require
 * the asset to exist. If the asset does not exist (i.e., if
 * <code>!m_assetModel.isSelected(state)</code>), the upload and editing
 * forms will create a new asset and set it in the model by calling
 * <code>setSelectedObject</code> on the asset selection model. Child
 * classes should override the {@link #createTextAsset(PageState)} method
 * in to create a valid text asset.
 * <p>
 * This component is used primarily in {@link GenericArticleBody} and
 * {@link com.arsdigita.cms.ui.templates.TemplateBody}
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: TextAssetBody.java 2167 2011-06-19 21:12:12Z pboy $
 */
public abstract class TextAssetBody extends SecurityPropertyEditor
        implements Resettable, AuthoringStepComponent, RequestListener {

    private static Logger s_log =
                          Logger.getLogger(TextAssetBody.class);
    private ItemSelectionModel m_assetModel;
    public static final String FILE_UPLOAD = "file";
    public static final String TEXT_ENTRY = "text";
    private StringParameter m_streamlinedCreationParam;
    private static final String STREAMLINED = "_streamlined";
    private static final String STREAMLINED_DONE = "1";
    private static final CMSConfig s_config = new CMSConfig();

    static {
        s_log.debug("Static initializer is starting...");
        s_config.load();
        s_log.debug("Static initializer finished.");
    }

    /**
     * Construct a new GenericArticleBody component
     *
     * @param assetModel The {@link ItemSelectionModel} which will
     *   be responsible for maintaining the current asset
     */
    public TextAssetBody(ItemSelectionModel assetModel) {
        this(assetModel, null);
    }

    /**
     * Construct a new GenericArticleBody component
     *
     * @param assetModel The {@link ItemSelectionModel} which will
     *   be responsible for maintaining the current asset
     * @param parent The parent wizard which contains the form. The form
     *   may use the wizard's methods, such as stepForward and stepBack,
     *   in its process listener.
     */
    public TextAssetBody(ItemSelectionModel assetModel,
                         AuthoringKitWizard parent) {
        super();
        m_assetModel = assetModel;

        m_streamlinedCreationParam =
        new StringParameter(parent == null ? "item" : parent.getContentType().
                getAssociatedObjectType() + "_body_done");

        if (!s_config.getHideTextAssetUploadFile()) {
            PageFileForm f = getPageFileForm();
            addFileWidgets(f);
            add(FILE_UPLOAD, 
            GlobalizationUtil.globalize("cms.ui.upload"), 
                    f,
                f.getSaveCancelSection().getCancelButton());
        }

        PageTextForm t = new PageTextForm();
        addTextWidgets(t);
        add(TEXT_ENTRY, 
            GlobalizationUtil.globalize("cms.ui.edit"), 
            t,
            t.getSaveCancelSection().getCancelButton());

        // Specify full path to properties of the text asset
        DomainObjectPropertySheet sheet = getBodyPropertySheet(assetModel);
        sheet.add(GlobalizationUtil.globalize("cms.ui.authoring.text_type"),
                  Asset.MIME_TYPE + "." + MimeType.LABEL);
        sheet.add(GlobalizationUtil.globalize("cms.ui.authoring.body"), 
                  TextAsset.CONTENT);

        setDisplayComponent(sheet);

        getDisplayPane().setClassAttr("invertedPropertyDisplay");

    }

    protected DomainObjectPropertySheet getBodyPropertySheet(
            ItemSelectionModel assetModel) {
        return new TextAssetBodyPropertySheet(assetModel);
    }

    /**
     * Adds the options for the mime type select widget of
     * <code>GenericArticleForm</code> and sets the default mime type.
     **/
    protected void setMimeTypeOptions(SingleSelect mimeSelect) {
        FileUploadSection.addMimeOptions(mimeSelect, "text");
        mimeSelect.setOptionSelected("text/html");
    }

    /**
     * Get the current text asset, if any
     * @param state represents the current request
     */
    public TextAsset getTextAsset(PageState state) {
        return (TextAsset) m_assetModel.getSelectedObject(state);
    }

    /**
     * Reset this component to its original state
     *
     * @param s the current page state
     */
    public void reset(PageState s) {
        showDisplayPane(s);
    }

    // Create a text asset if it does not exist.
    // This should probably be a method in GenericArticle ?
    protected TextAsset createOrGetTextAsset(ItemSelectionModel assetModel,
                                             PageState s) {
        // Get the text asset or create a new one
        TextAsset t = getTextAsset(s);

        if (t == null) {
            t = createTextAsset(s);
            assetModel.setSelectedObject(s, t);
        }

        return t;
    }

    /**
     * Create a brand new <code>TextAsset</code>. Child classes
     * should override this method to do the right thing. The
     * default implementation creates a parent-less <code>TextAsset</code>
     * with a unique name.
     *
     * @param s the current page state
     * @return a valid <code>TextAsset</code>
     */
    protected abstract TextAsset createTextAsset(PageState s);

    /**
     * Set additional parameters of a brand new text asset, such as the
     * parent ID, after the asset has been successfully uploaded
     *
     * @param s the current page state
     * @param a the new <code>TextAsset</code>
     */
    protected abstract void updateTextAsset(PageState s, TextAsset a);

    /**
     * Return the <code>ItemSelectionModel</code> which will be used
     * to maintain the current text asset
     */
    public ItemSelectionModel getAssetSelectionModel() {
        return m_assetModel;
    }

    /**
     * Forward to the next step if the streamlined creation parameter
     * is turned on _and_  the streamlined_creation global state param
     * is set to 'active'
     *
     * @param state the PageState
     */
    protected void maybeForwardToNextStep(PageState state) {
        if (ContentItemPage.isStreamlinedCreationActive(state) && !STREAMLINED_DONE.
                equals(state.getValue(m_streamlinedCreationParam))) {
            state.setValue(m_streamlinedCreationParam, STREAMLINED_DONE);
            fireCompletionEvent(state);
        }
    }

    /**
     * Cancel streamlined creation for this step if the streamlined
     * creation parameter is turned on _and_ the streamlined_creation
     * global state param is set to 'active'
     *
     * @param state the PageState
     */
    protected void cancelStreamlinedCreation(PageState state) {
        if (ContentItemPage.isStreamlinedCreationActive(state)) {
            state.setValue(m_streamlinedCreationParam, STREAMLINED_DONE);
        }
    }

    /**
     * Open the edit component if the streamlined
     * creation parameter is turned on _and_ the streamlined_creation
     * global state param is set to 'active'
     *
     * @param state the PageState
     */
    public void pageRequested(RequestEvent e) {
        PageState state = e.getPageState();
        //if (getTextAsset(state) != null) {
        //    ComponentAccess ca = (ComponentAccess) getAccessMap().get(TEXT_ENTRY);

        if (ContentItemPage.isStreamlinedCreationActive(state) && !STREAMLINED_DONE.
                equals(state.getValue(m_streamlinedCreationParam))) {
            showComponent(state, TEXT_ENTRY);
        }
        //}

    }

    /**
     *  This is the form that is used to upload files.  This method can
     *  be used so that a subclass can use their own subclass of PageFileForm.
     */
    protected PageFileForm getPageFileForm() {
        return new PageFileForm();
    }

    /**
     * A form for editing TextAsset items. Displays a "file upload"
     * widget, auto-guesses mime type
     */
    public class PageFileForm extends Form
            implements FormProcessListener, FormValidationListener {

        private SaveCancelSection m_saveCancelSection;
        private FileUploadSection m_fileUploadSection;
        // Variables saved by validate for processing
        private RequestLocal m_file_upload_content;
        private RequestLocal m_file_upload_usedINSO;
        /**
         * The text entry widget
         */
        public static final String TEXT_ENTRY = "text_entry";

        /**
         * Construct a new PageFileForm
         */
        public PageFileForm() {
            super("PageFileUpload", new BoxPanel(BoxPanel.VERTICAL));
            setMethod(Form.POST);
            setEncType("multipart/form-data");
        }

        protected String getFileUploadContent(PageState state) {
            return (String) m_file_upload_content.get(state);
        }

        /**
         * Make sure that files of this type can be uploaded
         */
        private void validateFileType(MimeType mime, boolean textType)
                throws FormProcessException {

            boolean validType = textType || ((mime instanceof TextMimeType) && ((TextMimeType) mime).
                                             allowINSOConvert().booleanValue());

            if (!validType) {
                throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.authoring.invalid_file_type"));
            }

            boolean insoWorks = MimeTypeStatus.getMimeTypeStatus().
                    getInsoFilterWorks().intValue() == 1;

            if (!textType && !insoWorks) {
                // Can't convert.  inso filter is not working.  Give message.
                throw new FormProcessException(
                        "Could not convert to html "
                        + "format because interMedia INSO filter is not installed.");
            }
        }

        /**
         * read in the content of the file (in bytes).
         */
        private byte[] readFileBytes(File file) throws FormProcessException {
            byte[] file_bytes;
            try {
                FileInputStream fs = new FileInputStream(file);
                file_bytes = new byte[fs.available()];
                fs.read(file_bytes);
                fs.close();
            } catch (Exception e) {
                throw new FormProcessException((String) GlobalizationUtil.
                        globalize("cms.ui.authoring.unable_to_load_file").
                        localize() + e.getMessage());
            }
            return file_bytes;
        }

        /**
         * Convert bytes to String, possibly using INSO filter to convert to
         * HTML type
         */
        private String convertBytes(byte[] file_bytes, boolean text_type,
                                    boolean[] used_inso)
                throws FormProcessException {
            String file_content;
            // If mime type is not text type, try to convert to html
            if (!text_type) {
                file_content = ConvertFormat.toHTML(file_bytes);
                if (file_content != null) {
                    // Converted successfully, flag type should be html
                    used_inso[0] = true;
                } else {
                    throw new FormProcessException(
                            "Could not convert to html format. "
                            + "interMedia INSO filter conversion failed.");
                }
            } else {
                // Text type, no need to convert
                String enc = Globalization.getDefaultCharset(Kernel.getContext().
                        getLocale());
                try {
                    file_content = new String(file_bytes, enc);
                } catch (UnsupportedEncodingException ex) {
                    throw new UncheckedWrapperException("cannot convert to encoding "
                                                        + enc, ex);
                }
                used_inso[0] = false;
            }
            return file_content;
        }

        /**
         * Extract the contents of the html Body tag.
         * (Done to prevent base and other header tags
         * from interfering with page display).
         */
        private String extractHTMLBody(String htmlText)
                throws FormProcessException {
            String lc = htmlText.toLowerCase();
            int bodyStart = lc.indexOf("<body");
            int bodyStart_v = lc.indexOf(">", bodyStart);
            int bodyEnd = lc.indexOf("</body>", bodyStart_v);
            if (bodyStart == -1 || bodyEnd == -1) {
                final String errMsg =
                             "The file (which should be type "
                             + "HTML) is missing the <body> or </body> tag.";
                throw new FormProcessException(errMsg);
            }
            return htmlText.substring(bodyStart_v + 1, bodyEnd);
        }

        /**
         * Validate file upload
         */
        public void validate(FormSectionEvent e) throws FormProcessException {

            MimeType mime = m_fileUploadSection.getMimeType(e);
            boolean textType = mime.getPrefix().equals(TextMimeType.TEXT_PREFIX);

            validateFileType(mime, textType);

            // Convert the file to HTML, if possible
            File file = m_fileUploadSection.getFile(e);
            byte[] file_bytes = readFileBytes(file);
            boolean[] used_inso = new boolean[1];
            String file_content = convertBytes(file_bytes, textType, used_inso);

            if (TextMimeType.MIME_TEXT_HTML.equals(mime.getMimeType())) {
                file_content = extractHTMLBody(file_content);
            }

            PageState state = e.getPageState();
            m_file_upload_content.set(state, file_content);
            m_file_upload_usedINSO.set(state, new Boolean(used_inso[0]));
        }

        /**
         * Process file upload.  Must be validated first.
         */
        public void process(FormSectionEvent e) throws FormProcessException {
            FormData data = e.getFormData();
            PageState state = e.getPageState();
            // Get the text asset or create a new one
            TextAsset t = createOrGetTextAsset(m_assetModel, state);
            File file = m_fileUploadSection.getFile(e);

            // Get info created during validation
            String upload_content = (String) m_file_upload_content.get(state);
            boolean usedINSO = ((Boolean) m_file_upload_usedINSO.get(state)).
                    booleanValue();

            // Set the mime type
            MimeType mime = m_fileUploadSection.getMimeType(e);
            if (usedINSO) {
                mime = MimeType.loadMimeType("text/html");
            }
            if (mime != null) {
                t.setMimeType(mime);
            }

            // Save the uploaded content
            t.setText(upload_content);


            //t.setName(fileName); // ???
            file = null;

            // Save everything
            updateTextAsset(state, t);
            if (t.isNew() || t.isModified()) {
                t.save();
            }

        }

        /**
         * @return the save/cancel section for this form
         */
        public SaveCancelSection getSaveCancelSection() {
            return m_saveCancelSection;
        }

        /**
         * @return the save/cancel section for this form
         */
        public FileUploadSection getFileUploadSection() {
            return m_fileUploadSection;
        }
    }

    /**
     * A form for editing the body of the text.
     */
    public class PageTextForm extends Form
            implements FormInitListener, FormProcessListener,
                       FormSubmissionListener {

        private SingleSelect m_mimeWidget;
        private Label m_mimeLabel;
        private TextArea m_textWidget;
        private SaveCancelSection m_saveCancelSection;
        /**
         * The text entry widget
         */
        public static final String TEXT_ENTRY = "text_entry";
        /**
         * The mime type widget
         */
        public static final String MIME_TYPE = "mime_type";

        /**
         * Construct a new PageTextForm
         *
         * @param assetModel The {@link ItemSelectionModel} which will
         *   be responsible for loading the current asset
         *
         */
        public PageTextForm() {
            super("PageTextForm", new ColumnPanel(2));
            setMethod(Form.POST);
            setEncType("multipart/form-data");
        }

        // These are here so that TemplateBody can set them.
        public final void setMimeWidget(final SingleSelect widget) {
            m_mimeWidget = widget;
        }

        public final void setTextWidget(final TextArea widget) {
            m_textWidget = widget;
        }

        public final void setSaveCancel(final SaveCancelSection widget) {
            m_saveCancelSection = widget;
        }

        // Init: load the item and preset the textarea
        public void init(FormSectionEvent e) throws FormProcessException {
            // Ok, we need to stream out the text somehow, but for now
            // we just fake it
            FormData data = e.getFormData();
            PageState state = e.getPageState();


            TextAsset t = (TextAsset) m_assetModel.getSelectedObject(state);
            if (t != null) {
                data.put(TEXT_ENTRY, t.getText());
                MimeType m = t.getMimeType();
                if (m != null) {
                    data.put(MIME_TYPE, m.getMimeType());
                }
            }
        }

        /** Cancels streamlined editing. */
        public void submitted(FormSectionEvent e) {
            if (getSaveCancelSection().getCancelButton().isSelected(e.
                    getPageState())) {
                TextAssetBody.this.cancelStreamlinedCreation(e.getPageState());
            }
        }

        // process: update the mime type and content
        public void process(FormSectionEvent e) throws FormProcessException {

            FormData data = e.getFormData();
            PageState state = e.getPageState();
            TextAsset t = createOrGetTextAsset(m_assetModel, state);

            // Set the mime type
            MimeType m = MimeType.loadMimeType((String) data.get(MIME_TYPE));
            t.setMimeType(m);

            // Get the string and normalize it
            String text = (String) data.get(TEXT_ENTRY);

            if (text == null) {
                text = "";
            }


            t.setText((String) data.get(TEXT_ENTRY));
            // Save everything
            updateTextAsset(state, t);
            if (t.isNew() || t.isModified()) {
                t.save();
            }

            TextAssetBody.this.maybeForwardToNextStep(e.getPageState());
        }

        /**
         * @return the save/cancel section for this form
         */
        public SaveCancelSection getSaveCancelSection() {
            return m_saveCancelSection;
        }
    }

    protected String getDefaultMimeType() {
        return "text/plain";
    }

    /* overridable method to put together the PageFileForm Component */
    protected void addFileWidgets(PageFileForm c) {

        c.m_fileUploadSection =
        new FileUploadSection("Text Type:", "text", getDefaultMimeType());
        c.m_fileUploadSection.getFileUploadWidget().addValidationListener(
                new NotNullValidationListener());
        // Default to -guess- because want to use file extension to determine type.
        c.m_fileUploadSection.getMimeTypeWidget().setDefaultValue(
                FileUploadSection.GUESS_MIME);
        c.add(c.m_fileUploadSection);

        c.m_saveCancelSection = new SaveCancelSection();
        c.add(c.m_saveCancelSection);

        // add FormErrorDisplay component to display any error message
        FormErrorDisplay fe = new FormErrorDisplay(c);
        c.add(fe);

        c.addValidationListener(c);
        c.addProcessListener(c);  // process called from validationListener

        // Storage for file upload information
        c.m_file_upload_content = new RequestLocal();
        c.m_file_upload_usedINSO = new RequestLocal();
    }


    /* overridable method to put together the PageTextForm Component */
    protected void addTextWidgets(PageTextForm c) {

        ColumnPanel panel = (ColumnPanel) c.getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        c.add(new Label(
                GlobalizationUtil.globalize("cms.ui.authoring.text_type")));
        c.m_mimeWidget = new SingleSelect(PageTextForm.MIME_TYPE);
        c.m_mimeWidget.setClassAttr("displayOneOptionAsLabel");
        setMimeTypeOptions(c.m_mimeWidget);
        c.add(c.m_mimeWidget, ColumnPanel.LEFT);

        c.add(new Label(GlobalizationUtil.globalize(
                "cms.ui.authoring.edit_body_text")),
              ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);

        c.m_textWidget = new CMSDHTMLEditor(PageTextForm.TEXT_ENTRY);
        c.m_textWidget.setRows(25);
        c.m_textWidget.setCols(40);
        // cg - sets FCKEditor size - closer to actual published page 
        // width, and similar size as htmlarea.
        // could be configurable - unset means default 100% x 400px
        c.m_textWidget.setMetaDataAttribute("width", "575");
        c.m_textWidget.setMetaDataAttribute("height", "500");
        c.m_textWidget.setWrap(CMSDHTMLEditor.SOFT);
        c.add(c.m_textWidget, ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);

        c.m_saveCancelSection = new SaveCancelSection();
        c.add(c.m_saveCancelSection, ColumnPanel.FULL_WIDTH);

        // optionally, we clear the text of MSWord tags every time
        // the text is submitted/saved
        if (ContentSection.getConfig().getSaveTextCleansWordTags()) {
            c.m_saveCancelSection.getSaveButton().setOnClick("wordClean_"
                                                             + PageTextForm.TEXT_ENTRY
                                                             + "();");
        }

        c.addInitListener(c);
        c.addProcessListener(c);

    }

    /** 
     * Registers globa state param for cancelling streamlined
     * creation
     */
    public void register(Page p) {
        super.register(p);
        p.addGlobalStateParam(m_streamlinedCreationParam);
        p.addRequestListener(this);
    }
}
