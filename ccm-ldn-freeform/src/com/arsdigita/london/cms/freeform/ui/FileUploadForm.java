package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.NotNullValidationListener;

/**
 * Component to display a generic file upload form. Developers are
 * expected to extend this class, Implement the FormProcessListener
 * interface and add additional widgets.
 *
 * @author <a href="mbryzek@arsdigita.com">Michael Bryzek</a>
 * @version $Id: FileUploadForm.java 753 2005-09-02 13:22:34Z sskracic $ 
 **/
public class FileUploadForm extends Form {
        
    private FileUploadSection m_fileUploadSection;
    
    /**
     * Construct a new FileUploadForm
     */
    public FileUploadForm(Container panel) {
        super("FileUploadForm", panel);
        setMethod(Form.POST);
        setEncType("multipart/form-data");
        
        addWidgets();
        addSubmitButton();
    }
    
    /**
     * Adds the submit button to this form.
     **/
    protected void addSubmitButton() {
        add(new Submit("upload", "Upload"));
    }
    
    /**
     * Adds the widgets to this form. The default implementation only
     * adds the fileUploadSection to the form.
     *
     * @see #createFileUploadSection()
     **/
    protected void addWidgets() {
        m_fileUploadSection = createFileUploadSection();
        add(m_fileUploadSection, ColumnPanel.INSERT);
    }

    /**
     * Creates and returns the FileUploadSection to use in the form
     * for the file upload widget. The default implementation uses an
     * instance of {@link FileUploadSection}.
     *
     * @see #createFileUploadSection()
     **/
    protected FileUploadSection createFileUploadSection() {
        m_fileUploadSection = new FileUploadSection();
        m_fileUploadSection.getFileUploadWidget()
            .addValidationListener(new NotNullValidationListener());
        return m_fileUploadSection;
    }
    

    /**
     * Returns the FormSection used for the actual file upload, or
     * null if the form section has not yet been initialized.
     *
     * @see #createFileUploadSection()
     **/
    protected FileUploadSection getFileUploadSection() {
        return m_fileUploadSection;
    }

}
