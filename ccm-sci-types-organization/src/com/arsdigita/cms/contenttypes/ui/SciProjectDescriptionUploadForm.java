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
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.globalization.Globalization;
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
 *
 * @author Jens Pelzetter
 */
public class SciProjectDescriptionUploadForm
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
            SciProjectDescriptionUploadForm.class);

    public SciProjectDescriptionUploadForm(ItemSelectionModel itemModel) {
        super("sciprojectUploadDescFrom", new BoxPanel(BoxPanel.VERTICAL));
        m_itemModel = itemModel;
        setMethod(Form.POST);
        setEncType("multipart/form-data");
        m_fileUploadContent = new RequestLocal();
        m_fileUploadContentUsedInso = new RequestLocal();
        addWidgets();
    }

    protected void addWidgets() {
        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.description.upload")));
        m_fileUploadSection = new FileUploadSection(
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.description.upload.mimetype"),
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
        SciProject project = (SciProject) m_itemModel.getSelectedObject(state);

        String uploadContent = (String) m_fileUploadContent.get(state);
        //boolean usedInso = (Boolean) m_fileUploadContentUsedInso.get(state);

        s_log.debug(String.format("Setting project description to: %s",
                                  uploadContent));
        project.setProjectDescription(uploadContent);
        s_log.debug("Saving project.");
        project.save();
    }

    private void validateFileType(MimeType mime, boolean textType)
            throws FormProcessException {

        boolean validType = textType || ((mime instanceof TextMimeType) && ((TextMimeType) mime).
                                         allowINSOConvert().booleanValue());

        if (!validType) {
            throw new FormProcessException("Cannot load " + "files of type " + mime.
                    getMimeType() + " into the article body.");
        } else {
            boolean insoWorks = MimeTypeStatus.getMimeTypeStatus().
                    getInsoFilterWorks().intValue() == 1;

            if (!textType && !insoWorks) {
                // Can't convert.  inso filter is not working.  Give message.
                throw new FormProcessException(
                        "Could not convert to html "
                        + "format because interMedia INSO filter is not installed.");
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
            throw new FormProcessException(
                    ((String) GlobalizationUtil.globalize(
                     "cms.ui.authoring.unable_to_load_file").
                     localize()).concat(e.getMessage()));
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
}
