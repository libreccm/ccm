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


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.bebop.parameters.URLTokenValidationListener;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.FileAsset;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.cms.ui.SecurityPropertyEditor;
import com.arsdigita.cms.ui.type.AddFileElement;
import com.arsdigita.cms.ui.type.AddImageElement;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.mimetypes.ImageMimeType;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.util.Assert;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.TooManyListenersException;
import org.apache.log4j.Logger;

/**
 * The editing component for user defined items. Consists of a display
 * component which displays the form metadata, and a form which edits the
 * metadata as well as text.
 *
 * @author Xixi D'Moon (xdmoon@arsdigita.com)
 * @version $Revision: #21 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: PageEditDynamic.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class PageEditDynamic extends SecurityPropertyEditor {

    private static Logger s_log =
        Logger.getLogger(PageEditDynamic.class.getName());

    private AuthoringKitWizard m_parent;
    private ItemSelectionModel m_itemModel;
    private ContentType m_originatingType = null;
    private boolean m_isPrimaryStep = true;

    /**
     * Construct a new PageEditDynamic component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param parent The parent wizard which contains the form. The form
     *   may use the wizard's methods, such as stepForward and stepBack,
     *   in its process listener.
     */
    public PageEditDynamic(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this (itemModel, parent, null);
    }
    /**
     * Construct a new PageEditDynamic component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param parent The parent wizard which contains the form. The form
     *   may use the wizard's methods, such as stepForward and stepBack,
     *   in its process listener.
     *
     * @param originatingType The content type to use for choosing dynamic
     *   components (if one UDCT inherits from another, type-specific attributes
     *   will be in separate steps).
     */
    public PageEditDynamic(ItemSelectionModel itemModel, AuthoringKitWizard parent, ContentType originatingType) {
        this(itemModel, parent, originatingType, true);
    }
    /**
     * Construct a new PageEditDynamic component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param parent The parent wizard which contains the form. The form
     *   may use the wizard's methods, such as stepForward and stepBack,
     *   in its process listener.
     *
     * @param originatingType The content type to use for choosing dynamic
     *   components (if one UDCT inherits from another, type-specific attributes
     *   will be in separate steps).
     *
     * @param isPrimaryStep Whether this authoring step is a primary step for the
     *        authoring kit
     */
    public PageEditDynamic(ItemSelectionModel itemModel,
                           AuthoringKitWizard parent,
                           ContentType originatingType,
                           boolean isPrimaryStep) {

        super();
        m_parent = parent;
        m_itemModel = itemModel;
        m_originatingType = originatingType;
        m_isPrimaryStep = isPrimaryStep;
        if (m_originatingType == null) {
            m_originatingType = m_itemModel.getContentType();
        }

        parent.getList().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    showDisplayPane(state);
                }
            });

        //make form used to edit a user-defined item
        PageEditDynamicForm form = new PageEditDynamicForm(itemModel);
        add("edit", "Edit", new WorkflowLockedComponentAccess(form, itemModel),
            form.getSaveCancelSection().getCancelButton());

        //use DomainOfjectPropertySheet to display attribute/value
        //pairs of the item
        DomainObjectPropertySheet sheet =
            new DomainObjectPropertySheet(itemModel);
        //name and title fields as in ContentPage
        addNameTitleFields(sheet);

        DynamicObjectType dot =
            new DynamicObjectType (m_originatingType.getAssociatedObjectType());

        //display non-inherited attributes by iterating through the properties
        //of the dynamic object type associated with this item
        Iterator declaredProperties = dot.getObjectType().getDeclaredProperties();
        Property currentProperty = null;
        while (declaredProperties.hasNext()){
            currentProperty = (Property) declaredProperties.next();
            String propertyPath = currentProperty.getName();
            if (currentProperty.getType().getName().equals("TextAsset")) {
                propertyPath = propertyPath + ".content";
            } else if (currentProperty.getType().getName().equals("ImageAsset")) {
                propertyPath = propertyPath + ".displayName";
            } else if (currentProperty.getType().getName().equals("FileAsset")) {
                propertyPath = propertyPath + ".displayName";
            } else if (!currentProperty.isAttribute()) {
                propertyPath = propertyPath + ".title";
            }
            sheet.add(currentProperty.getName().concat(":"),
                      propertyPath);
        }
        setDisplayComponent(sheet);
    }

    protected void addNameTitleFields(DomainObjectPropertySheet sheet) {
        sheet.add((String) GlobalizationUtil.globalize("cms.ui.authoring.name").localize(),  ContentPage.NAME);
        sheet.add((String) GlobalizationUtil.globalize("cms.ui.authoring.title").localize(),  ContentPage.TITLE);
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            sheet.add("Launch Date:",
                      ContentPage.LAUNCH_DATE,
                      new DomainObjectPropertySheet.AttributeFormatter() {
                          public String format(DomainObject item,
                                               String attribute,
                                               PageState state) {
                              ContentPage page = (ContentPage) item;
                              if(page.getLaunchDate() != null) {
                                  return DateFormat.getDateInstance(DateFormat.LONG)
                                      .format(page.getLaunchDate());
                              } else {
                                  return "<i>unknown</i>";
                              }
                          }
                      });
        }
    }

    /**
     * @return the parent wizard
     */
    public AuthoringKitWizard getParentWizard() {
        return m_parent;
    }

    /**
     * @return The item selection model
     */
    public ItemSelectionModel getItemSelectionModel() {
        return m_itemModel;
    }


    /**
     * A form for editing user defined content items.
     * includes the persistent widgets stored in the content type's
     * persistent form to edit scalar, dynamic attributes
     */
    private class PageEditDynamicForm extends BasicPageForm {

        /**
         * Construct a new PageEditDynamicForm
         *
         * @param itemModel The {@link ItemSelectionModel} which will
         *   be responsible for loading the current item
         *
         */
        public PageEditDynamicForm(ItemSelectionModel itemModel){
            //overrides the parent constructor otherwise the submit/cancel
            //will be located above the persistent widgets
            super(m_originatingType.getName() + "PageEditDynamicForm",
                  new ColumnPanel(3), itemModel);

            m_itemModel = itemModel;

            ColumnPanel panel = (ColumnPanel) getPanel();
            panel.setBorder(false);
            panel.setPadColor("#FFFFFF");
            //panel.setColumnWidth(1, "30%");
            //panel.setColumnWidth(2, "70%");
            //panel.setWidth("2%");

            //add name and title textfield
            addWidgets();
            //add persistent input fields
            addPersistentWidgets(itemModel);
            //add submit buttons
            addSaveCancelSection();

            addInitListener(this);
            addProcessListener(this);
            addValidationListener(this);
        }

        /**
         * Add name and title widgets to the form. required fields
         */
        protected void addWidgets() {

            if (m_isPrimaryStep) {
                add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.name")));
                add(new Label(""));
                TextField nameWidget = new TextField(new TrimmedStringParameter(NAME));
                nameWidget.addValidationListener(new NotNullValidationListener());
                nameWidget.addValidationListener(new URLTokenValidationListener());
                add(nameWidget);

                add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.title")));
                add(new Label(""));
                TextField titleWidget = new TextField(new TrimmedStringParameter(TITLE));
                titleWidget.addValidationListener(new NotNullValidationListener());
                add(titleWidget);

                add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.page_launch_date")));
                add(new Label(""));
                ParameterModel launchDateParam = new DateParameter(LAUNCH_DATE);
                launchDateParam
                        .addParameterListener(new NotNullValidationListener());
                com.arsdigita.bebop.form.Date launchDate
                        = new com.arsdigita.bebop.form.Date(launchDateParam);
                add(launchDate);

            }
        }

        /**
         * loops through the persisten form associated with this
         * item's content type, make the bebop component for each
         * persistent widget and add it to the bebop form
         */
        private void addPersistentWidgets(ItemSelectionModel itemSel) {
            ContentType type = itemSel.getContentType();

            PersistentForm pForm  = m_originatingType.getItemForm();
            DataAssociationCursor components = pForm.getComponents();
            String associationTypeParameter = null;
            String associationContentType = null;
            //add all persistent components to this form
            while (components.next()) {
                PersistentComponent component = (PersistentComponent)
                    DomainObjectFactory.newInstance( components.getDataObject() );

                Component thisComponent = component.createComponent();
                if (thisComponent instanceof Hidden) {
                    String thisParameterName = ((Hidden) thisComponent).getParameterModel().getName();
                    String thisValue = ((Hidden) thisComponent).getDefaultValue();
                    if ((thisParameterName+".image").equals(thisValue)) {
                        FormSection imageUploadSection =
                            new FormSection(new ColumnPanel(2));
                        SingleSelect actionSelect = new SingleSelect
                            (new StringParameter(thisParameterName + "." + AddImageElement.ACTION));
                        actionSelect.addOption(new Option(AddImageElement.ACTION_NONE,"Leave Image Unchanged"));
                        actionSelect.addOption(new Option(AddImageElement.ACTION_UPLOAD,"Upload New Image"));
                        actionSelect.addOption(new Option(AddImageElement.ACTION_DELETE,"Delete Current Image"));
                        imageUploadSection.add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.image_action")));
                        imageUploadSection.add(actionSelect);

                        FileUploadSection fileUpload =
                            new FileUploadSection("Image&nbsp;Type:", "image",
                                                  ImageMimeType.MIME_IMAGE_JPEG,
                                                  thisParameterName+".");
                        //fileUpload.getFileUploadWidget()
                        //    .addValidationListener(new NotNullValidationListener());
                        imageUploadSection.add(fileUpload, ColumnPanel.INSERT);
                        add(imageUploadSection);

                    } else if ((thisParameterName+".file").equals(thisValue)) {
                        FormSection fileAssetUploadSection =
                            new FormSection(new ColumnPanel(2));
                        SingleSelect actionSelect = new SingleSelect
                            (new StringParameter(thisParameterName + "." + AddFileElement.ACTION));
                        actionSelect.addOption(new Option(AddFileElement.ACTION_NONE,"Leave File Unchanged"));
                        actionSelect.addOption(new Option(AddFileElement.ACTION_UPLOAD,"Upload New File"));
                        actionSelect.addOption(new Option(AddFileElement.ACTION_DELETE,"Delete Current File"));
                        fileAssetUploadSection.add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.file_action")));
                        fileAssetUploadSection.add(actionSelect);

                        FileUploadSection fileUpload =
                            new FileUploadSection("File&nbsp;Type:", null,
                                                  null,
                                                  thisParameterName+".");
                        //fileUpload.getFileUploadWidget()
                        //    .addValidationListener(new NotNullValidationListener());
                        fileAssetUploadSection.add(fileUpload, ColumnPanel.INSERT);
                        add(fileAssetUploadSection);

                    } else {
                        associationContentType = thisValue;
                        associationTypeParameter = thisParameterName;
                    }
                }
                if (thisComponent instanceof SingleSelect &&
                    (((SingleSelect) thisComponent).getParameterModel().getName() + "Type")
                    .equals(associationTypeParameter)) {
                    try {
                        ((SingleSelect)thisComponent).addPrintListener
                            (new PageCreateDynamic.ContentItemSelectPrintListener
                             (associationContentType));
                    } catch (TooManyListenersException ex) {
                        // don't add listener
                    }
                }
                add(thisComponent);
            }
        }

        /**
         * Init: load the item and preset the widgets
         */
        public void init(FormSectionEvent e) throws FormProcessException {
            PageState state = e.getPageState();
            FormData data = e.getFormData();
            ContentPage item;
            if (m_isPrimaryStep) {
                item =  super.initBasicWidgets(e);
            } else {
                item = (ContentPage)this.getItemSelectionModel().getSelectedObject(state);
            }
            DynamicObjectType dot =
                new DynamicObjectType(m_originatingType.getAssociatedObjectType());
            if ( item != null ) {
                Iterator declaredProperties = dot.getObjectType().getDeclaredProperties();
                Property currentProperty = null;
                Object value = null;
                while (declaredProperties.hasNext()){

                    currentProperty = (Property) declaredProperties.next();
                    value = item.get(currentProperty.getName());

                    if (value != null &&
                        currentProperty.getType().getName().equals("TextAsset")) {
                        String content = (String) ((DataObject) value).get("content");
                        data.put(currentProperty.getName(), content);
                    } else if (currentProperty.getType().getName().equals("ImageAsset")) {
                        // do nothing for now
                    } else if (currentProperty.getType().getName().equals("FileAsset")) {
                        // do nothing for now
                    } else if (value instanceof DataObject) {
                        BigDecimal id = (BigDecimal) ((DataObject) value).get("id");
                        data.put(currentProperty.getName(), id);
                    } else if (value != null) {
                        data.put(currentProperty.getName(), value);
                    }
                }
            }
        }


        /**
         * Process: save fields to the database
         */
        public void process(FormSectionEvent e) throws FormProcessException {
            PageState state = e.getPageState();
            FormData data = e.getFormData();

            ContentPage item;
            if (m_isPrimaryStep) {
                item = super.processBasicWidgets(e);
            } else {
                item = (ContentPage)this.getItemSelectionModel().getSelectedObject(state);
            }
            DynamicObjectType dot = new DynamicObjectType (m_originatingType.getAssociatedObjectType());
            if ( item != null ) {
                Iterator declaredProperties = dot.getObjectType().getDeclaredProperties();
                Property currentProperty = null;
                Object value = null;

                while (declaredProperties.hasNext()){
                    currentProperty = (Property) declaredProperties.next();
                    value = data.get(currentProperty.getName());

                    if (value != null) {

                        if (currentProperty.isAttribute()) {
                            item.set(currentProperty.getName(), value);
                        } else if (currentProperty.getType().getName().equals("TextAsset")) {
                            TextAsset thisAsset = null;
                            DataObject assetDO = (DataObject) item.get(currentProperty.getName());
                            if (assetDO != null) {
                                thisAsset = new TextAsset(assetDO);
                            }
			    if (thisAsset == null) {
				thisAsset = new TextAsset();
				thisAsset.setName(item.getName() + "_text_" + thisAsset.getID());
				thisAsset.setParent(item);
				thisAsset.setText((String) value);
				thisAsset.save();
				DataObject thisDataObj = SessionManager.getSession().retrieve
				    (thisAsset.getOID());
				item.set(currentProperty.getName(), thisDataObj);
			    } else {
				thisAsset.setText((String) value);
				thisAsset.save();
			    }
                        } else if (currentProperty.getType().getName().equals("ImageAsset")) {
                            String currentPropertyName = currentProperty.getName();
                            String imageAction =
                                (String) data.get(currentPropertyName + "." +
                                                  AddImageElement.ACTION);
                            String fileName =
                                (String)data.get(currentPropertyName + "." +
                                                 FileUploadSection.FILE_UPLOAD);
                            if (imageAction.equals(AddImageElement.ACTION_UPLOAD)) {
                                File file = null;
                                if(fileName != null && fileName.length() > 0) {
                                    file = ((MultipartHttpServletRequest)state.getRequest())
                                        .getFile(currentPropertyName + "." +
                                                 FileUploadSection.FILE_UPLOAD);
                                    ImageAsset a = new ImageAsset();

                                    // Load the asset from file
                                    try {
                                        a.loadFromFile(fileName, file, ImageAsset.MIME_JPEG);
                                    } catch (IOException ex) {
                                        s_log.error("Error processing form", ex);
                                        throw new FormProcessException(ex);
                                    }

                                    // Save everything
                                    a.save();
                                    DataObject thisDataObj = SessionManager.getSession().retrieve
                                        (a.getOID());
                                    item.set(currentPropertyName, thisDataObj);
                                }
                            } else if (imageAction.equals(AddImageElement.ACTION_DELETE)) {
                                item.set(currentPropertyName, null);
                            } else if (imageAction.equals(AddImageElement.ACTION_NONE)) {
                                // do nothing
                            }
                        } else if (currentProperty.getType().getName().equals("FileAsset")) {
                            String currentPropertyName = currentProperty.getName();
                            String fileAction =
                                (String) data.get(currentPropertyName + "." +
                                                  AddFileElement.ACTION);
                            String fileName =
                                (String)data.get(currentPropertyName + "." +
                                                 FileUploadSection.FILE_UPLOAD);
                            String mimeType =
                                (String)data.get(currentPropertyName + "." +
                                                 FileUploadSection.MIME_TYPE);
                            if (fileAction.equals(AddFileElement.ACTION_UPLOAD)) {
                                File file = null;
                                if(fileName != null && fileName.length() > 0) {
                                    file = ((MultipartHttpServletRequest)state.getRequest())
                                        .getFile(currentPropertyName + "." +
                                                 FileUploadSection.FILE_UPLOAD);
                                    FileAsset a = new FileAsset();

                                    // Load the asset from file
                                    try {
                                        a.loadFromFile
                                            (fileName,
                                             file,
                                             FileUploadSection.GUESS_MIME.equals(mimeType) ?
                                             (String) null : mimeType);
                                    } catch (IOException ex) {
                                        s_log.error("Error processing form", ex);
                                        throw new FormProcessException(ex);
                                    }

                                    // Save everything
                                    a.save();
                                    DataObject thisDataObj = SessionManager.getSession().retrieve
                                        (a.getOID());
                                    item.set(currentPropertyName, thisDataObj);
                                }
                            } else if (fileAction.equals(AddFileElement.ACTION_DELETE)) {
                                item.set(currentPropertyName, null);
                            } else if (fileAction.equals(AddFileElement.ACTION_NONE)) {
                                // do nothing
                            }
                        } else {
                            String typeName = currentProperty.getType()
                                .getQualifiedName();
                            try {
                                ContentType assocType =
                                    ContentType.findByAssociatedObjectType(typeName);
                                if (value == null || value.equals("")) {
                                    item.set(currentProperty.getName(), null);
                                } else {
                                    DataObject thisObj = SessionManager.getSession().retrieve
                                        (new OID(typeName, (BigDecimal) value));
                                    item.set(currentProperty.getName(), thisObj);
                                }
                            } catch (DataObjectNotFoundException ex) {
                                // content type not found, don't set it.
                            }
                        }
                    }
                }
                item.save();
            }

        }

        /**
         * same form validation as in PageEdit
         * confirms this was the same item to be edited
         */
        public void validate(FormSectionEvent event) throws FormProcessException {
            super.validate(event);
            
            PageState state = event.getPageState();
            FormData data = event.getFormData();

            ContentItem item =
                (ContentItem) this.getItemSelectionModel().getSelectedObject(state);
            Assert.exists(item);

            if (m_isPrimaryStep) {
                String newName = (String) data.get(BasicPageForm.NAME);
                String oldName = item.getName();

                // Validation passes if the item name is the same.
                if ( !newName.equalsIgnoreCase(oldName) ) {
                    validateNameUniqueness((Folder) item.getParent(), event);
                }
            }

        }

    }
}
