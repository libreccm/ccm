/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Image;
import com.arsdigita.cms.contenttypes.util.ImageGlobalizationUtil;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.cms.ui.ImageDisplay;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.dispatcher.DispatcherHelper;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Authoring step to edit the simple attributes of the Image content
 * type (and its subclasses). The attributes edited are 'name', 'title',
 * 'article date', 'location', 'lead', and 'article type'.
 * This authoring step replaces
 * the <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 */
public class ImagePropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static String EDIT_SHEET_NAME = "edit";
    public static String UPLOAD_SHEET_NAME = "uploadSheet";
    private final StringParameter m_imageComponentKey;
    private final MapComponentSelectionModel m_imageComponent;
    private final String UPLOAD = "upload";
    private ImageUploadComponent uploadSheet;

    public ImagePropertiesStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        super(itemModel, parent);

        setDefaultEditKey(EDIT_SHEET_NAME);

        BasicPageForm editSheet;

        m_imageComponentKey = new StringParameter("imageComponent");
        ParameterSingleSelectionModel componentModel = new ParameterSingleSelectionModel(m_imageComponentKey);
        m_imageComponent = new MapComponentSelectionModel(componentModel, new HashMap());
        Map selectors = m_imageComponent.getComponentsMap();
        uploadSheet = new ImageUploadComponent();
        uploadSheet.getForm().addProcessListener(new ImageUploadListerner());
        selectors.put(UPLOAD, uploadSheet);

        editSheet = new ImagePropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit", new WorkflowLockedComponentAccess(editSheet, itemModel), editSheet.getSaveCancelSection().getCancelButton());
        add(UPLOAD_SHEET_NAME, "Upload", new WorkflowLockedComponentAccess(uploadSheet, itemModel), uploadSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getImagePropertySheet(itemModel));
    }

    /**
     * Returns a component that displays the properties of the
     * Image specified by the ItemSelectionModel passed in.
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public static Component getImagePropertySheet(final ItemSelectionModel itemModel) {
        SimpleContainer container = new SimpleContainer();

        container.add(new ImageDisplay(null) {

            @Override
            protected ImageAsset getImageAsset(PageState state) {
                try {
                    ImageAsset image = ((Image) itemModel.getSelectedItem(state)).getImage().proportionalResizeToWidth(Image.getConfig().getMaxImageWidth());
                    return image;
                } catch (NullPointerException ex) {
                    return null;
                }
            }
        });

        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.name"), Image.NAME);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.title"), Image.TITLE);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.launch_date"),
                    ContentPage.LAUNCH_DATE,
                    new DomainObjectPropertySheet.AttributeFormatter() {

                        @Override
                        public String format(DomainObject item,
                                String attribute,
                                PageState state) {
                            ContentPage page = (ContentPage) item;
                            if (page.getLaunchDate() != null) {
                                return DateFormat.getDateInstance(DateFormat.LONG).format(page.getLaunchDate());
                            } else {
                                return (String) ImageGlobalizationUtil.globalize("cms.ui.unknown").localize();
                            }
                        }
                    });
        }

        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.width"), Image.WIDTH);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.height"), Image.HEIGHT);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.caption"), Image.CAPTION);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.description"), Image.DESCRIPTION);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.artist"), Image.ARTIST);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.publishDate"), Image.PUBLISHDATE,
                new DomainObjectPropertySheet.AttributeFormatter() {

                    @Override
                    public String format(DomainObject item, String attribute, PageState state) {
                        Image image = (Image) item;
                        if ((image.getPublishDate()) != null) {
                            if (image.getSkipDay().booleanValue() == true || image.getSkipMonth().booleanValue() == true) {
                                String month = "";
                                if (image.getSkipMonth().booleanValue() == false) {
                                    Locale locale = DispatcherHelper.getNegotiatedLocale();

                                    if (locale != null) {

                                        DateFormatSymbols dfs = new DateFormatSymbols(locale);
                                        String[] months = dfs.getMonths();
                                        month = months[image.getPublishDate().getMonth()] + " ";
                                    }
                                }
                                String year = Integer.toString(image.getPublishDate().getYear() + 1900);
                                return month + year;
                            } else {
                                return DateFormat.getDateInstance(DateFormat.LONG).format(image.getPublishDate());
                            }
                        } else {
                            return (String) ImageGlobalizationUtil.globalize("cms.ui.unknown").localize();
                        }
                    }
                });
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.source"), Image.SOURCE);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.media"), Image.MEDIA);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.copyright"), Image.COPYRIGHT);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.site"), Image.SITE);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.license"), Image.LICENSE);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.material"), Image.MATERIAL);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.technique"), Image.TECHNIQUE);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.origin"), Image.ORIGIN);
        sheet.add(ImageGlobalizationUtil.globalize("cms.contenttypes.ui.image.origSize"), Image.ORIGSIZE);

        container.add(sheet);

        return container;
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_imageComponentKey);
    }

    public ContentItem getItem(PageState ps) {
        return getItemSelectionModel().getSelectedItem(ps);
    }

    class ImageUploadListerner implements FormProcessListener {

        @Override
        public void process(FormSectionEvent fse) {
            FormData data = fse.getFormData();
            PageState ps = fse.getPageState();
            Image image = (Image) getItemSelectionModel().getSelectedObject(ps);
            ImageAsset imageAsset = null;
            ImageComponent component = getImageComponent(ps);

            try {
                imageAsset = component.getImage(fse);
            } catch (FormProcessException ex) {
            }

            // save only if save button was pressed
            if (image != null && uploadSheet.getSaveCancelSection().getSaveButton().isSelected(fse.getPageState())) {

                image.setImage(imageAsset);
                image.save();
            }
        }

        private ImageComponent getImageComponent(PageState ps) {
            if (!m_imageComponent.isSelected(ps)) {
                m_imageComponent.setSelectedKey(ps, UPLOAD);
            }

            return (ImageComponent) m_imageComponent.getComponent(ps);
        }
    }

    interface ImageComponent {

        ImageAsset getImage(FormSectionEvent event)
                throws FormProcessException;

        SaveCancelSection getSaveCancelSection();

        Form getForm();
    }

    private class ImageUploadComponent extends Form
            implements ImageComponent {

        private final FileUploadSection m_imageFile;
        private final SaveCancelSection m_saveCancel;

        public ImageUploadComponent() {
            super("imageStepEditUpload", new ColumnPanel(2));

            setEncType("multipart/form-data");

            // Ignoring deprecated constructor.
            m_imageFile = new FileUploadSection("Image Type", "image", ImageAsset.MIME_JPEG);
            m_imageFile.getFileUploadWidget().addValidationListener(new NotNullValidationListener());

            add(m_imageFile, ColumnPanel.FULL_WIDTH);

            m_saveCancel = new SaveCancelSection();
            add(m_saveCancel);

        }

        @Override
        public SaveCancelSection getSaveCancelSection() {
            return m_saveCancel;
        }

        @Override
        public ImageAsset getImage(FormSectionEvent event)
                throws FormProcessException {
            PageState ps = event.getPageState();

            String filename = (String) m_imageFile.getFileName(event);
            File imageFile = m_imageFile.getFile(event);

            try {
                ImageAsset image = new ImageAsset();
                image.loadFromFile(filename, imageFile, ImageAsset.MIME_JPEG);

                return image;
            } catch (IOException ex) {
                throw new FormProcessException(ex.getMessage());
            }
        }

        @Override
        public Form getForm() {
            return this;
        }
    }
}
