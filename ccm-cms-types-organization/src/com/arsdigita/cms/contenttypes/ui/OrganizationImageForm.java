/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ui.ImageDisplay;

import com.arsdigita.dispatcher.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;

import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.contenttypes.Organization;
import com.arsdigita.cms.contenttypes.util.OrganizationGlobalizationUtil;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;

import org.apache.log4j.Logger;


/**
 * Form to edit the basic properties of an Organization. This form can be
 * extended to create forms for Organization subclasses.
 *
 * @version $Id: OrganizationImageForm.java 757 2005-09-02 14:12:21Z sskracic $
 */
public class OrganizationImageForm
    extends Form
    implements FormInitListener, FormProcessListener, FormValidationListener,
               FormSubmissionListener {

    private OrganizationImageStep m_step;

    private static final Logger s_log
        = Logger.getLogger(OrganizationImageForm.class);

    /** parameter names */
    public static final String IMAGE = "image";

    /** Name of this form */
    public static final String ID = "Organization_image";

    public static final String IMAGE_OPTIONS = "_imageOptions";

    public static final String UPLOAD_OPTION = "upload";
    public static final String DELETE_OPTION = "delete";
    public static final String IGNORE_OPTION = "ignore";

    public ImageDisplay m_imageDisplay;
    public Label m_currentLabel;
    public Label m_label;
    public FileUpload m_upload;
    public Label m_radioLabel;
    public RadioGroup m_group;

    private ItemSelectionModel m_itemModel;
    private SaveCancelSection m_saveCancelSection;

    /**
     * Creates a new form to edit the Organization object specified
     * by the item selection model passed in.
     * @param itemModel The ItemSelectionModel to use to obtain the 
     *    Organization to work on
     */
    public OrganizationImageForm( ItemSelectionModel itemModel ) {
        this("OrganizationImageForm", itemModel);
    }

    public OrganizationImageForm( String name, ItemSelectionModel itemModel ) {
        this("OrganizationImageForm", itemModel, null);
    }

    public OrganizationImageForm(String name, ItemSelectionModel itemModel, OrganizationImageStep step) {
        super(name);

        m_itemModel = itemModel;
        m_step = step;

        setMethod(Form.POST);
        setEncType("multipart/form-data");

        FormErrorDisplay errorDisplay = new FormErrorDisplay(this);
        errorDisplay.setStateParamsAreRegistered(false);

        add(errorDisplay, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);


        m_imageDisplay = new ImageDisplay(itemModel) {
                protected ImageAsset getImageAsset(PageState state) {
                    ImageAsset image = (ImageAsset) ((Organization) getImageSelectionModel().getSelectedObject(state)).getImage();
                    return image;
                }
            };
        StringParameter imageOptions = new StringParameter(IMAGE + IMAGE_OPTIONS);
        m_group = new RadioGroup(imageOptions);
        m_group.addOption(new Option(UPLOAD_OPTION, UPLOAD_OPTION));
        m_group.addOption(new Option(DELETE_OPTION, DELETE_OPTION));
        m_group.addOption(new Option(IGNORE_OPTION, IGNORE_OPTION));
        m_group.setDefaultValue(IGNORE_OPTION);

        m_currentLabel = new Label(OrganizationGlobalizationUtil.globalize
                                   ("cms.contenttypes.current_image"));
        add(m_currentLabel);
        add(m_imageDisplay);

        m_label = new Label(OrganizationGlobalizationUtil.globalize
                            ("cms.contenttypes.image"));
        add(m_label);
        m_upload = new FileUpload(IMAGE);
        add(m_upload);

        m_radioLabel = new Label("");
        add(m_radioLabel);
        add(m_group);
        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection);

        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);
        addSubmissionListener(this);
    }

    private ItemSelectionModel getItemSelectionModel() {
        return m_itemModel;
    }

    /** Form initialisation hook. Fills widgets with data. */
    public void init( FormSectionEvent fse ) {
        PageState state = fse.getPageState();
        Organization item = (Organization) getItemSelectionModel().getSelectedObject(state);
    }

    /** Form processing hook. Saves Organization object. */
    public void process( FormSectionEvent fse ) throws FormProcessException {
        PageState state = fse.getPageState();
        FormData data = fse.getFormData();
        Organization item = (Organization) getItemSelectionModel().getSelectedObject(state);

        ImageAsset image = null;

        String imageOption = (String)data.get(IMAGE + IMAGE_OPTIONS);
        if (imageOption != null && imageOption.equals(UPLOAD_OPTION)) {
            //
            // upload image
            //
            s_log.debug("uploading image");
            String fileName = (String)(data.get(IMAGE));
            if (fileName != null && fileName.length() > 0) {
                File file = ((MultipartHttpServletRequest)state.getRequest()).getFile(IMAGE);
                s_log.debug("file: " + file);
                ImageAsset a = new ImageAsset();
                a.setName(fileName);
                try {
                    s_log.debug("fileName: " + fileName);

                    a.loadFromFile(fileName, file, ImageAsset.MIME_JPEG);

                    s_log.debug("saving");

                    a.save();

                    image = a;
                } catch (IOException ex) {
                    throw new FormProcessException(ex);
                }
            } else {
                throw new FormProcessException
                    ((String)(OrganizationGlobalizationUtil.globalize
                              ("cms.contenttypes.organization.image_option_null")
                              .localize()));
            }
        } else if (imageOption != null && imageOption.equals(DELETE_OPTION)) {
            //
            // delete image
            //
            s_log.debug("deleting image step");
            image = null;
        } else {
            //
            // ignore 
            //
            s_log.debug("ignoring image step");
            image = item.getImage();
        }
        

        item.setImage(image);

        item.save();
        if (m_step != null) {
            m_step.maybeForwardToNextStep(fse.getPageState());
        }
    }

    public void validate(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        String imageOption = (String)data.get(IMAGE + IMAGE_OPTIONS);
        String fileName = (String)(fse.getFormData().get(IMAGE));
        s_log.debug("validate option: " + imageOption);
        s_log.debug("validate filename: " + fileName);
        
        if (imageOption == null || imageOption.equals(IGNORE_OPTION)) {
            if (fileName != null && !fileName.equals("")) {
                data.addError(IMAGE,
                              (OrganizationGlobalizationUtil.globalize
                               ("cms.contenttypes.organization.chose_image_when_ignoring")));
            }
        } else if (imageOption.equals(DELETE_OPTION)) {
            if (fileName != null && !fileName.equals("")) {
                data.addError(IMAGE,
                              (OrganizationGlobalizationUtil.globalize
                               ("cms.contenttypes.organization.chose_image_when_deleting")));
            }
        } else {
            if (fileName == null || fileName.equals("")) {
                data.addError(IMAGE,
                              (OrganizationGlobalizationUtil.globalize
                               ("cms.contenttypes.organization.chose_image_when_uploading")));
            }
        }
    }

    public void submitted(FormSectionEvent fse) throws FormProcessException {
        if (m_step != null &&
            m_saveCancelSection.getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }
}
