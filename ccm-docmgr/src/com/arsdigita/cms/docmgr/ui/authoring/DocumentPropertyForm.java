package com.arsdigita.cms.docmgr.ui.authoring;


import java.io.File;
import java.io.IOException;

import org.apache.log4j.Category;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;

/**
 * Form to edit the basic properties of an document. This form can be
 * extended to create forms for Document subclasses.
 */
public class DocumentPropertyForm
    extends BasicPageForm
    implements FormProcessListener, FormInitListener {
    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(DocumentPropertyForm.class);

    public static final String DESCRIPTION = "description";
    public static final String FILE = "file";

    private FileFormSection m_fileSection;

    /**
     * Creates a new form to edit the Document object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the
     *    Document to work on
     */
    public DocumentPropertyForm( ItemSelectionModel itemModel ) {
        super( ID, itemModel );
    }

    /**
     * Adds widgets to the form.
     */
    protected void addWidgets() {
        super.addWidgets();

        add( new Label( "Description:" ) );
        ParameterModel descriptionParam
            = new StringParameter( DESCRIPTION );
        //descriptionParam
        //    .addParameterListener( new NotNullValidationListener() );
        TextArea description = new TextArea( descriptionParam );
        description.setCols( 40 );
        description.setRows( 5 );
        add( description );

        m_fileSection = new FileFormSection(FILE, "File:");
        add(m_fileSection);
        add(new Label(""));

    }

    public void validate(FormSectionEvent e) throws FormProcessException {
        FormData d = e.getFormData();
    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init( FormSectionEvent fse ) {
        // Do some initialization hook stuff
        FormData data = fse.getFormData();
        Document document
            = (Document) super.initBasicWidgets( fse );

        data.put( DESCRIPTION,      document.getDescription() );

        m_fileSection.initialize(fse, document.getFile());
    }

    /** Form processing hook. Saves Event object. */
    public void process( FormSectionEvent fse ) throws FormProcessException {
        FormData data = fse.getFormData();

        Document document
            = (Document) super.processBasicWidgets( fse );

        // save only if save button was pressed
        if( document != null
            && getSaveCancelSection().getSaveButton()
            .isSelected( fse.getPageState() ) ) {

            FileAsset file =
                m_fileSection.process(fse, document.getFile());
            document.setFile(file);
            document.setDescription( (String) data.get( DESCRIPTION ) );
            document.save();
        }
        
        
    }
}

class FileFormSection extends FormSection implements FormValidationListener {
    public static final Category s_log = Category.getInstance(FileFormSection.class);

    public static final String FILE_OPTIONS = "_fileOptions";
    public static final String DESCRIPTION = "_description";

    public static final String UPLOAD_OPTION = "upload";
    public static final String DELETE_OPTION = "delete";
    public static final String IGNORE_OPTION = "ignore";

    public FileDisplay m_fileDisplay;
    public String m_file;
    public FileUpload m_upload;

    public FileFormSection(String file, String formLabel) {
        m_file = file;
        m_fileDisplay = new FileDisplay();

        StringParameter fileOptions = new StringParameter(file + FILE_OPTIONS);
        RadioGroup fileGroup = new RadioGroup(fileOptions);
        fileGroup.addOption(new Option(UPLOAD_OPTION, UPLOAD_OPTION));
        fileGroup.addOption(new Option(DELETE_OPTION, DELETE_OPTION));
        fileGroup.addOption(new Option(IGNORE_OPTION, IGNORE_OPTION));
        fileGroup.setDefaultValue(IGNORE_OPTION);

        //if (file == null || file.length() == 0) {
        //    m_fileDisplay.setName("(none)");
        //} else {
        m_fileDisplay.setName(file);
            //}
        Label currentLabel = new Label("Existing " + formLabel);
        add(currentLabel);
        add(m_fileDisplay);

        add(new Label(formLabel + "(uploading will replace existing file)"));
        m_upload = new FileUpload(file);
        add(m_upload);

        add(new Label(""));
        add(fileGroup);

        addValidationListener(this);
    }

    public void initialize(FormSectionEvent e, FileAsset asset) {
        if(asset != null) {
            s_log.debug("asset exists");
        } else {
            //m_fileDisplay.setName(asset.getName());
            m_fileDisplay.setName("");
            s_log.debug("asset is null");
        }
        
        m_fileDisplay.setFileAsset(asset);
    }

    public FileAsset process(FormSectionEvent e, FileAsset asset) throws FormProcessException {
        FormData data = e.getFormData();
        
        String fileOption = (String)data.get(m_file + FILE_OPTIONS);
        if (fileOption.equals(UPLOAD_OPTION)) {
            //
            // upload file
            //
            s_log.debug("uploading file");
            String fileName = (String)(data.get(m_file));
            if (fileName != null && fileName.length() > 0) {
                File fileFile = ((MultipartHttpServletRequest)e.getPageState().getRequest()).getFile(m_file);

                s_log.debug("file: " + fileFile);
                FileAsset a = new FileAsset();
                a.setName("file" + a.getID());
                try {
                    s_log.debug("fileName: " + fileName);
                    a.loadFromFile(fileName, fileFile, "file/pdf");
                    s_log.debug("saving");
                    a.setDescription(fileName);
                    a.save();

                    return a;
                } catch (IOException ex) {
                    throw new FormProcessException(ex);
                }
            } else {
                throw new FormProcessException("File Option was null");
            }
        } else if (fileOption != null && fileOption.equals(DELETE_OPTION)) {
            //
            // delete image
            //
            s_log.debug("deleting file");
            return null;
        } else {
            //
            // ignore
            //
            s_log.debug("ignoring image");
            return asset;
        }
    }

    public void validate(FormSectionEvent e) {
        FormData data = e.getFormData();
        String fileOption = (String)data.get(m_file + FILE_OPTIONS);
        String fileName = (String)(e.getFormData().get(m_file));
        s_log.debug("validate option: " + fileOption);
        s_log.debug("validate filename: " + fileName);
        
        if (fileOption.equals(IGNORE_OPTION)) {
            if (fileName != null && !fileName.equals("")) {
                data.addError("You specified an file when choosing to ignore");
            }
        } else if (fileOption.equals(DELETE_OPTION)) {
            if (fileName != null && !fileName.equals("")) {
                data.addError("You specified an file when choosing to delete");
            }
        } else {
            if (fileName == null || fileName.equals("")) {
                data.addError("You did not specify an file when choosing to upload");
            }
        }
    }

}
