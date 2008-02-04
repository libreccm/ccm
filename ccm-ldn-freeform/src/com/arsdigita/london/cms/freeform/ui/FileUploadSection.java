package com.arsdigita.london.cms.freeform.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;

import java.io.File;

/**
 * A form section with one widget to upload a file. We attempt to
 * automatically guess the mime type from the filename.
 *
 * @author <a href="mbryzek@arsdigita.com">Michael Bryzek</a>
 * @version $Id: FileUploadSection.java 753 2005-09-02 13:22:34Z sskracic $ 
 **/
public class FileUploadSection extends FormSection {

    private FileUpload m_fileUploadWidget;
    
    /**
     * The file upload widget
     **/
    public static final String FILE_UPLOAD = "file_upload";
    
    /**
     * Construct a new FileUploadSection
     * 
     * @param panel The panel that is to be used to lay out the
     * components
     *  
     **/
    public FileUploadSection(Container panel) {
        super(panel);

        add(getUploadWidgetLabel());
        m_fileUploadWidget = createFileUploadWidget();
        add(m_fileUploadWidget);
    }


    /**
     * Construct a new FileUploadSection in a ColumnPanel with two columns.
     **/
    public FileUploadSection() {
        this(new ColumnPanel(2, true));
        ColumnPanel panel = (ColumnPanel) getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
    }


    /**
     * Returns the Label to place to the left of the file upload
     * widget.
     **/
    protected Label getUploadWidgetLabel() {
        return new Label("Select file:");
    }

    /**
     * Returns the FileUpload widget to use. If you name your widget
     * something other than FileUploadSection.FILE_UPLOAD, you must
     * also override {@link #getFile(FormSectionEvent)} and {@link
     * #getFileName(FormSectionEvent)}.
     **/
    protected FileUpload getFileUploadWidget() {
        return m_fileUploadWidget;
    }

    /**
     * Creates and returns the FileUpload widget to use. Default
     * implementation uses {@link FileUpload}.
     **/
    protected FileUpload createFileUploadWidget() {
        return new FileUpload(FILE_UPLOAD);
    }

    /**
     * Obtain a File object from the file upload widget. The
     * containing form should call this method in its process
     * listener. Returns null if the file could not be found.
     *
     * @param state The state of the current request.
     **/
    public File getFile(FormSectionEvent e) {
        String fileName = getFileName(e);
    
        if(fileName != null && fileName.length() > 0) {
            return ((MultipartHttpServletRequest) e.getPageState().getRequest())
                .getFile(FILE_UPLOAD);
        }
        return null;
    }

    /**
     * Obtain a filename from the file upload widget. The containing form
     * should call this method in its process listener.
     *
     * @param e The form section event
     */
    public String getFileName(FormSectionEvent e) {
        FormData data = e.getFormData();
        return (String) data.get(FILE_UPLOAD);
    }
    
}
