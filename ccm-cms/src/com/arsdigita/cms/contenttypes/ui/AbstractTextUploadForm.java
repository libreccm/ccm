/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.mimetypes.MimeTypeStatus;
import com.arsdigita.mimetypes.TextMimeType;
import com.arsdigita.mimetypes.converters.ConvertFormat;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;

/**
 * <p>
 * An abstract base form for uploading Texts. The code of this class has been
 * extracted from {@link TextAssetBody}, the form for editing an TextAsset 
 * (used for example for Article). The TextAsset has the disadvantage of storing
 * its information in a separate table, so that information of a content item
 * is spread over several tables. 
 * </p>
 * <p>
 * To use this form, define a property for your object which has the Java type
 * String and the database type CLOB, like this:
 * </p>
 * <pre>
 * String[0..1] text = ct_yourContenttype.text CLOB
 * </pre>
 * <p>
 * To use this form your have to overwrite three methods:
 * </p>
 * <ul>
 * <li>{@link #getLabelText()}</li>
 * <li>{@link #getMimeTypeLabel()}</li>
 * <li>{@link #setText()}</li>
 * </ul>
 * <li>
 * Of course, you have to add your form to a property step also, and write a
 * simple constructor, which takes an {@link ItemSelectionModel} as parameter
 * and passes it to the constructor of this class.
 * </li>
 *
 *
 * @author Jens Pelzetter
 */
public abstract class AbstractTextUploadForm
        extends Form
        implements FormInitListener,
                   FormProcessListener,
                   FormValidationListener {

    private ItemSelectionModel m_itemModel;
    private FileUploadSection m_fileUploadSection;
    private SaveCancelSection m_saveCancelSection;
    private RequestLocal m_fileUploadContent;
    private RequestLocal m_fileUploadContentUsedInso;
    private Logger s_log = Logger.getLogger(
            AbstractTextUploadForm.class);

    public AbstractTextUploadForm(ItemSelectionModel itemModel) {
        super("sciprojectUploadDescFrom", new BoxPanel(BoxPanel.VERTICAL));
        m_itemModel = itemModel;
        setMethod(Form.POST);
        setEncType("multipart/form-data");
        m_fileUploadContent = new RequestLocal();
        m_fileUploadContentUsedInso = new RequestLocal();
        addWidgets();
    }

    //Abstract methods to overwrite

    /**
     * The return value of this method is used as label for the upload form.
     *
     * @return The label for the upload form.
     */
    public abstract GlobalizedMessage getLabelText();

    /**
     * The return value of this method is used as label for the MimeType field.
     *
     * @return The label for the MimeType field.
     */
    public abstract GlobalizedMessage getMimeTypeLabel();

    /**
     * <p>
     * This method is called to pass the uploaded text to the edited object. In
     * the method, you have to retrieve the current selected object from the
     * <code>itemModel</code> parameter and call the appropriate
     * <code>set</code> of your class, and its save method. An simple example:
     * </p>
     * <pre>
     * @Override
     * public void setText(ItemSelectionModel itemModel,
     *                     PageState state,
     *                     String text) {
     *   YourContentType obj = (YourContentType) itemModel.getSelectedObject(state);
     *   obj.setText(text);
     *   obj.save();
     * }
     * </pre>
     *
     * @param itemModel The {@link ItemSelectionModel} used by the form.
     * @param state The current {@link PageState}.
     * @param text The uploaded text.
     */
    public abstract void setText(ItemSelectionModel itemModel,
                                 PageState state,
                                 String text);

    protected void addWidgets() {
        add(new Label(getLabelText()));
        m_fileUploadSection = new FileUploadSection(
                getMimeTypeLabel(),
                "mime",
                "text/plain");
        m_fileUploadSection.getFileUploadWidget().addValidationListener(
                new NotNullValidationListener());
        m_fileUploadSection.getMimeTypeWidget().setDefaultValue(
                FileUploadSection.GUESS_MIME);
        add(m_fileUploadSection);

        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection);

        add(new FormErrorDisplay(this));

        this.addValidationListener(this);
        this.addProcessListener(this);
    }

    /**
     * @return the save/cancel section for this form
     */
    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    /**
     * Validate file upload
     * @param e
     * @throws FormProcessException
     */
    @Override
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
        m_fileUploadContent.set(state, file_content);
        m_fileUploadContentUsedInso.set(state, used_inso[0]);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        s_log.debug("Processing upload...");
        PageState state = fse.getPageState();
        //File file = m_fileUploadSection.getFile(fse);
        //SciProject project = (SciProject) m_itemModel.getSelectedObject(state);

        String uploadContent = (String) m_fileUploadContent.get(state);
        //boolean usedInso = (Boolean) m_fileUploadContentUsedInso.get(state);

        s_log.debug(String.format("Setting project description to: %s",
                                  uploadContent));
        //project.setProjectDescription(uploadContent);
        s_log.debug("Saving project.");
        //project.save();
        setText(m_itemModel, state, uploadContent);
    }

    private void validateFileType(MimeType mime, boolean textType)
            throws FormProcessException {

        boolean validType = textType || ((mime instanceof TextMimeType) && ((TextMimeType) mime).
                                         allowINSOConvert().booleanValue());

        if (!validType) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "cms.ui.authoring.invalid_file_type"));
        } else {
            boolean insoWorks = MimeTypeStatus.getMimeTypeStatus().
                    getInsoFilterWorks().intValue() == 1;

            if (!textType && !insoWorks) {
                // Can't convert.  inso filter is not working.  Give message.
                throw new FormProcessException(GlobalizationUtil.globalize(
                        "cms.ui.authoring.couldnt_convert_missing_inso"));
            }
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
            throw new FormProcessException(GlobalizationUtil.globalize(
                     "cms.ui.authoring.unable_to_load_file"));
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
                throw new FormProcessException(GlobalizationUtil.globalize(
                        "cms.ui.authoring.couldnt_convert_inso_failed"));
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
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "cms.ui.authoring.html_file_missing_body_tags"));
        }
        return htmlText.substring(bodyStart_v + 1, bodyEnd);
    }
}
