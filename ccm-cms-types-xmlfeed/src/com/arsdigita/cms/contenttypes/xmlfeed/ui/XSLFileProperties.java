/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Copyright (C) 2003 Runtime Collective
 */

package com.arsdigita.cms.contenttypes.xmlfeed.ui;

import com.arsdigita.cms.contenttypes.xmlfeed.XMLFeed;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SaveCancelSection;

import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;

import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;

import com.arsdigita.bebop.parameters.NotNullValidationListener;

import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.mimetypes.MimeType;

import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.cms.ui.SecurityPropertyEditor;

import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

import com.arsdigita.templating.XSLTemplate;
import com.arsdigita.templating.Templating;

import com.arsdigita.util.UncheckedWrapperException;


import java.io.File;
import java.io.IOException;


import java.net.URL;
import java.net.MalformedURLException;


import org.apache.log4j.Logger;


/**
 * <p>
 * This class represents the authoring step for the
 * {@link XmlFeed XmlFeed} that allows you to associate the correct XSL file to
 * allow the feed to be transformed into HTML.
 * </p>
 *
 * <p>
 * The authoring kit is setup in
 * {@link com.arsdigita.aplaws.xmlfeed.XmlFeedInitializer XmlFeedInitializer}
 * </p>
 *
 * @author <a href="mailto:miles@runtime-collective.com">Miles Barr</a>
 * @version $Id: XSLFileProperties.java 755 2005-09-02 13:42:47Z sskracic $
 *
 * @see com.arsdigita.aplaws.xmlfeed.XmlFeedInitializer
 */
public final class XSLFileProperties extends SecurityPropertyEditor
    implements Resettable {

    /** The logger object for this class. */
    private static Logger s_log = Logger.getLogger(XSLFileProperties.class);

    // ===== Constants ======================================================= //
    public static final String versionId =
            "$Id: XSLFileProperties.java 755 2005-09-02 13:42:47Z sskracic $";

    public static final String MIME_TYPE = "application/xsl+xml";
    public static final String FILE_UPLOAD = FileUploadSection.FILE_UPLOAD;

    // ===== Fields ========================================================== //
    private AuthoringKitWizard m_parent;
    private ItemSelectionModel m_itemModel;


    // ===== Constructors ==================================================== //

    /**
     * Construct a new XSLFileProperties component.
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for maintaining the current asset
     */
    public XSLFileProperties(ItemSelectionModel itemModel,
                             AuthoringKitWizard parent) {
        m_itemModel = itemModel;
        m_parent    = parent;
      
        // Set up the form that actually allows the uploading of the XSL
        XSLFilePropertiesForm form = new XSLFilePropertiesForm(itemModel);
        add(FILE_UPLOAD, "Upload the XSL file", 
            new WorkflowLockedComponentAccess(form, itemModel),
            form.getSaveCancelSection().getCancelButton());

        // Specify full path to properties of the XSL file.
        DomainObjectPropertySheet sheet =
            new DomainObjectPropertySheet(itemModel);
        sheet.add("XSL File", XMLFeed.XSL_FILE + ".name");
      
        setDisplayComponent(sheet);
      
        getDisplayPane().setClassAttr("invertedPropertyDisplay");
    }




    // ===== State Methods==================================================== //

    /**
     * Reset this component to its original state.
     *
     * @param state The current page state.
     */
    public void reset(PageState state) {
        showDisplayPane(state);
    }

    // ===== File Handler Methods ============================================ //

    /**
     * Gets the current file asset, if any.
     *
     * @param state The current page state.
     * @return The current file asset.
     */
    public FileAsset getFileAsset(PageState state) {
        return (FileAsset)m_itemModel.getSelectedObject(state);
    }


    /**
     * Create a new <code>FileAsset</code>.
     *
     * @param state The current page state.
     * @return A new <code>FileAsset</code>.
     */
    private FileAsset createFileAsset() {
        FileAsset file = new FileAsset();

        file.setMimeType(MimeType.loadMimeType(MIME_TYPE));

        return file;
    }


    // ===== Inner Form ====================================================== //

    /**
     * This form allows the editing of the URL attribute of the XmlFeed content
     * type.
     */
    private class XSLFilePropertiesForm extends Form
        implements FormProcessListener, FormValidationListener {

        private final ItemSelectionModel m_itemModel;

        private FileUploadSection m_fileUploadSection;
        private SaveCancelSection m_saveCancelSection;


        /**
         * Construct a new XSLFilePropertiesForm.
         *
         * @param itemModel The {@link ItemSelectionModel} which will
         *   be responsible for loading the current item
         */
        public XSLFilePropertiesForm(ItemSelectionModel itemModel) {
            super("XSLFileUpload", new ColumnPanel(2));

            // Set the proper form type to allow file uploads.
            setMethod(Form.POST);
            setEncType("multipart/form-data");

            // Set up the file upload widget.
            m_fileUploadSection =
                new FileUploadSection("File Type","application", MIME_TYPE);
            m_fileUploadSection.getFileUploadWidget()
                .addValidationListener(new NotNullValidationListener());

            setMimeTypeOptions(m_fileUploadSection.getMimeTypeWidget());

            add(m_fileUploadSection);

            m_itemModel = itemModel;

            m_saveCancelSection = new SaveCancelSection();
            add(m_saveCancelSection);

            // Set up the error display.
            FormErrorDisplay errorDisplay = new FormErrorDisplay(this);
            add(errorDisplay);

            ColumnPanel panel = (ColumnPanel) getPanel();
            panel.setBorder(false);
            panel.setPadColor("#FFFFFF");
            panel.setColumnWidth(1, "20%");
            panel.setColumnWidth(2, "80%");
            panel.setWidth("2%");

            // Set up the listeners.
            addProcessListener(this);
            addValidationListener(this);
        }


        /**
         * Adds the options for the mime type select widget of
         * <code>TextPageForm</code> and sets the default mime type.
         */
        private void setMimeTypeOptions(SingleSelect select) {
            // Remove all the existing mime types and add the XSL one.
            select.clearOptions();
            select.addOption(new Option(MIME_TYPE, "XSL File"));
            select.setOptionSelected(MIME_TYPE);
        }

        /**
         * @return The save/cancel section for this form.
         */
        public SaveCancelSection getSaveCancelSection() {
            return m_saveCancelSection;
        }

        /**
         * @return The file upload section for this form.
         */
        public FileUploadSection getFileUploadSection() {
            return m_fileUploadSection;
        }


        /**
         * Process the form. Retrieves the files from the upload widget and then
         * sets the internal <code>FileAsset</code> object of the
         * <code>XmlFeed</code> content item.
         */
        public void process(FormSectionEvent e) throws FormProcessException {
            FormData data   = e.getFormData();
            PageState state = e.getPageState();
            XMLFeed item    = (XMLFeed) m_itemModel.getSelectedObject(state);

            FileAsset fileAsset = item.getXSLFile();
            if (null == fileAsset) {
                fileAsset = createFileAsset();
                item.setXSLFile(fileAsset);
            }

            File file = m_fileUploadSection.getFile(e);
            String fileName = m_fileUploadSection.getFileName(e);

            fileAsset.setMimeType(m_fileUploadSection.getMimeType(e));

            try {
                fileAsset.loadFromFile(fileName, file, MIME_TYPE);
            } catch (IOException ex) {
                throw new FormProcessException("cannot load XSL file", ex);
            }

            fileAsset.setName(fileName);
            item.save();
        }


        /**
         * Validates the form, ensuring the value set of the URL is correct.
         *
         * @param event the {@link FormSectionEvent} which was passed to the
         *    validation listener
         * @throws FormProcessException If the url is not set or it does not take
         *   the form of a standard url.
         */
        public void validate(FormSectionEvent e) throws FormProcessException {
            FormData data = e.getFormData();

            // Verify the file is an XML document.
            File file = m_fileUploadSection.getFile(e);
            try {
                URL url = new URL("file://" + file.getAbsolutePath());
                XSLTemplate xsl = Templating.getTemplate(url);
            } catch (UncheckedWrapperException ex) {
                s_log.error("cannot instantiate XSL file", ex);
                data.addError("Cannot verify the file is valid, please try again.");
            } catch (MalformedURLException ex) {
                throw new UncheckedWrapperException("bad filename " + file, ex);
            }

            // Make sure the mime type is correct. At the moment this is always true
            // because we have to set the mime type as there is no current standard
            // and the system would not assign the correct one.
            MimeType mime = m_fileUploadSection.getMimeType(e);
            if (mime != null) {
                boolean isCorrectType = MIME_TYPE.equals(mime.getPrefix());

                if (!isCorrectType) {
                    data.addError("File is not the correct type.");
                }
            }
        }
    }
}
