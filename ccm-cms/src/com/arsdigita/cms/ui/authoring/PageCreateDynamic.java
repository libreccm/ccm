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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
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
import com.arsdigita.cms.ui.type.AddFileElement;
import com.arsdigita.cms.ui.type.AddImageElement;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;


/**
 * The creation component for user-defined content items.
 *
 * TODO: subclass PageCreate now that this no longer extends MetaForm?
 */

public class PageCreateDynamic extends FormSection
    implements FormInitListener, FormProcessListener, FormValidationListener, 
               FormSubmissionListener, CreationComponent {

    private static Logger s_log =
        Logger.getLogger(PageCreateDynamic.class.getName());

    private ItemSelectionModel m_itemModel;

    private static Class[] s_args = new Class[] {
        ItemSelectionModel.class,
        CreationSelector.class
    };
    private static Class[] s_userDefinedArgs = new Class[] {
        ItemSelectionModel.class,
        CreationSelector.class,
        ContentType.class
    };

    private ContentType m_useContentType  = null;
    private ContentType m_parentType  = null;
    private CreationComponent m_parentComponent  = null;

    public static final String NAME = ContentItem.NAME;
    public static final String TITLE = ContentPage.TITLE;
    public static final String LAUNCH_DATE = ContentPage.LAUNCH_DATE;
    public static final String LANGUAGE = ContentItem.LANGUAGE;

    private CreationSelector m_parent;

    /**
     * The state parameter which specifies the content section
     */
    public static final String SECTION_ID = "sid";

    /**
     * Construct a new PageCreationDynamic component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param parent The {@link CreationSelector} parent. This class should call
     * either the {@link CreationSelector#redirectBack(PageState)} or {@link
     * CreationSelector#editItem(PageState, ContentItem)} methods on the parent
     * eventually
     */
    public PageCreateDynamic(ItemSelectionModel itemModel, CreationSelector parent) {
        this(itemModel, parent, null);
    }
    /**
     * Construct a new PageCreationDynamic component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param parent The {@link CreationSelector} parent. This class should call
     * either the {@link CreationSelector#redirectBack(PageState)} or {@link
     * CreationSelector#editItem(PageState, ContentItem)} methods on the parent
     * eventually
     *
     * @param useContentType The content type to use for choosing dynamic
     *   components (if one UDCT inherits from another, type-specific attributes
     *   will be in separate steps).
     */
    public PageCreateDynamic(ItemSelectionModel itemModel,
                             CreationSelector parent,
                             ContentType useContentType) {
        super();
        m_itemModel = itemModel;
        m_parent = parent;
        m_useContentType = useContentType;
        if (m_useContentType == null) {
            m_useContentType = m_itemModel.getContentType();
        }
        // set parent content type (if one exists)
        ObjectType thisObjectType =
            MetadataRoot.getMetadataRoot().getObjectType
            (m_useContentType.getAssociatedObjectType());
        if (thisObjectType != null) {
            ObjectType parentObjectType = thisObjectType.getSupertype();
            if (parentObjectType != null) {
                try {
                    m_parentType =
                        ContentType.findByAssociatedObjectType
                        (parentObjectType.getQualifiedName());
                } catch (DataObjectNotFoundException e) {
                    // no parent content type
                }
            }
        }
        AuthoringKit parentKit = null;
        if (m_parentType != null) {
            parentKit = m_parentType.getAuthoringKit();
        }
        String parentCreateComponent = null;
        if (parentKit != null) {
            parentCreateComponent = parentKit.getCreateComponent();
        }
        if (parentCreateComponent != null) {
            try {
                Object [] vals;
                Class [] args;
                Class createClass = Class.forName(parentCreateComponent);
                if (parentCreateComponent.equals
                    ("com.arsdigita.cms.ui.authoring.PageCreateDynamic")) {
                    vals = new Object[]{m_itemModel, parent, m_parentType};
                    args = s_userDefinedArgs;
                } else {
                    vals = new Object[]{m_itemModel, parent};
                    args = s_args;
                }
                Constructor constr = createClass.getConstructor(args);
                Component tempComponent = (Component)constr.newInstance(vals);
                if (tempComponent instanceof CreationComponent) {
                    m_parentComponent = (CreationComponent) tempComponent;
                }
            } catch (Exception e) {
                // could not instantiate parent's creation component 
                // -- start from scratch...
            }
        } else {
            m_parentComponent = new PageCreate(m_itemModel, parent);
        }
        if (m_parentComponent != null ) {

            super.add(m_parentComponent);
            // Quasimodo BEGIN
            // see comment for the addWidget method
            // addWidgets();
            // Quasimodo END
            getSaveCancelSection().getSaveButton().setButtonLabel("Create");
            
            //Form internalForm = getForm();
            //internalForm.addInitListener(this);
            addInitListener(this);
            m_parentComponent.removeProcessListener(m_parentComponent);
            addProcessListener(this);
            addValidationListener(this);
            //setMethod(Form.POST);
            //setEncType("multipart/form-data");
        } else {
            add(new Label(GlobalizationUtil.globalize(
                          "cms.ui.authoring.invalid_create_component")));
        }

    }

    /*  *  (no Javadoc here!)
     * Add various widgets to the form. Child classes should override
     * this method to perform all their widget-adding needs
     * adds both the name and titlle widgets and
     * the persistent widgets stored in the persistent form
     * associated with this content item
     *
     * Quasimodo BEGIN
     * ===============
     * disabled to get an ordinary create step. This method will ask
     * for ALL additional fields of a UDCT regardless if it is mandatory or not.
     * So, to prevent this I have modified the UDCT-Widgets to allway be optional
     * as the hardcoded content types and this method becomes obsolete.
     *
    protected void addWidgets() {
        PersistentForm pForm = null;
        try {
            pForm = m_useContentType.getItemForm();
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException(ex);
        }
        FormSection thisSection = new FormSection(new ColumnPanel(3));

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
                    actionSelect.addOption(new Option(AddImageElement.ACTION_NONE,"No Image"));
                    actionSelect.addOption(new Option(AddImageElement.ACTION_UPLOAD,"Upload New Image"));
                    imageUploadSection.add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.image_action")));
                    imageUploadSection.add(actionSelect);

                    FileUploadSection fileUpload = new FileUploadSection("Image&nbsp;Type:", "image",
                                                                         ImageMimeType.MIME_IMAGE_JPEG,
                                                                         thisParameterName+".");
                    //fileUpload.getFileUploadWidget()
                    //    .addValidationListener(new NotNullValidationListener());
                    imageUploadSection.add(fileUpload, ColumnPanel.INSERT);
                    thisSection.add(imageUploadSection);

                } else if ((thisParameterName+".file").equals(thisValue)) {
                    FormSection fileAssetUploadSection =
                        new FormSection(new ColumnPanel(2));
                    SingleSelect actionSelect = new SingleSelect
                        (new StringParameter(thisParameterName + "." + AddFileElement.ACTION));
                    actionSelect.addOption(new Option(AddFileElement.ACTION_NONE,"No File"));
                    actionSelect.addOption(new Option(AddFileElement.ACTION_UPLOAD,"Upload New File"));
                    fileAssetUploadSection.add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.file_action")));
                    fileAssetUploadSection.add(actionSelect);

                    FileUploadSection fileUpload =
                        new FileUploadSection("File&nbsp;Type:", (String) null,
                                              (String) null,
                                              thisParameterName+".");
                    //fileUpload.getFileUploadWidget()
                    //    .addValidationListener(new NotNullValidationListener());
                    fileAssetUploadSection.add(fileUpload, ColumnPanel.INSERT);
                    thisSection.add(fileAssetUploadSection);

                } else {
                    associationContentType = ((Hidden) thisComponent).getDefaultValue();
                    associationTypeParameter = ((Hidden) thisComponent).getParameterModel().getName();
                }
            }
            if (thisComponent instanceof SingleSelect &&
                (((SingleSelect) thisComponent).getParameterModel().getName() + "Type")
                .equals(associationTypeParameter)) {
                try {
                    ((SingleSelect)thisComponent).addPrintListener
                        (new ContentItemSelectPrintListener(associationContentType));
                } catch (TooManyListenersException ex) {
                    // don't add listener
                }
            }
            thisSection.add(thisComponent);
        }
        add(thisSection, ColumnPanel.FULL_WIDTH);
    }
    * Quasimodo END
    */

    /**
     * @return the item selection model used in this form
     */
    public ItemSelectionModel getItemSelectionModel() {
        return m_itemModel;
    }

    /**
     * instanciate and add the save/cancel section for this form
     */
    @Override
    public void addSaveCancelSection() {
        if (m_parentComponent != null) {
            m_parentComponent.addSaveCancelSection();
        }
    }

    /**
     * @return the save/cancel section for this form
     */
    @Override
    public SaveCancelSection getSaveCancelSection() {
        if (m_parentComponent != null) {
            return m_parentComponent.getSaveCancelSection();
        } else {
            return null;
        }
    }

    /**
     * Return the ApplyWorkflowFormSection associated with this CreationComponent.
     *
     * @return the ApplyWorkflowFormSection associated with this CreationComponent.
     */
    @Override
    public ApplyWorkflowFormSection getWorkflowSection() {
        if (m_parentComponent != null) {
            return m_parentComponent.getWorkflowSection();
        } else {
            return null;
        }
    }

    @Override
    public void init(FormSectionEvent e) throws FormProcessException {
        // this is currently a no-op
    }

    /**
     * Submission: If the Cancel button was pressed, hide self and
     * show the display component.
     * 
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void submitted(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();

        if(getSaveCancelSection().getCancelButton().isSelected(state)) {
            m_parent.redirectBack(state);
            throw new FormProcessException( (String) GlobalizationUtil
                            .globalize("cms.ui.authoring.submission_cancelled")
                            .localize());
        }
    }

    /**
     * Validate: ensure name uniqueness.
     * 
     * @param e
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void validate(FormSectionEvent e) throws FormProcessException {
        Folder f = m_parent.getFolder(e.getPageState());
        Assert.exists(f);
        validateNameUniqueness(f, e);
    }

    /**
     * Process: save fields to the database.
     * 
     * @param e
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void process(FormSectionEvent e) throws FormProcessException {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        ContentPage item = createUDItem(state);
        item.setLanguage((String)data.get(LANGUAGE));
        item.setName((String)data.get(NAME));
        item.setTitle((String)data.get(TITLE));
        if (!ContentSection.getConfig().getHideLaunchDate()) {
            item.setLaunchDate((Date)data.get(LAUNCH_DATE));
        }

        m_itemModel.setSelectedKey(state, item.getID());
        m_itemModel.setSelectedObject(state, item);

        //set user-added attributes of this item
        DynamicObjectType dot = new DynamicObjectType (m_useContentType.getAssociatedObjectType());
        ObjectType thisObjType = dot.getObjectType();
        Collection assetsToSave = new ArrayList();
        do {
            Iterator declaredProperties = thisObjType.getDeclaredProperties();
            Property currentProperty = null;
            Object value = null;
            while (declaredProperties.hasNext()){
                currentProperty = (Property) declaredProperties.next();
                try {
                    value = (Object) data.get(currentProperty.getName());

                    if (value != null) {

                        if (currentProperty.isAttribute()) {
                            item.set(currentProperty.getName(), value);
                        } else if (currentProperty.getType().getName().equals("TextAsset")) {
			    TextAsset thisAsset = new TextAsset();
			    thisAsset.setName(item.getName() + "_text_" + thisAsset.getID());
                                //thisAsset.setParent(item);
			    assetsToSave.add(thisAsset);
			    thisAsset.setText((String) value);
			    thisAsset.save();
			    DataObject thisDataObj = SessionManager.getSession().retrieve
				(thisAsset.getOID());
			    item.set(currentProperty.getName(), thisDataObj);
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
                                    file = ((MultipartHttpServletRequest)
                                            state.getRequest())
                                        .getFile(currentPropertyName + "." +
                                                 FileUploadSection.FILE_UPLOAD);
                                    ImageAsset a = new ImageAsset();

                                    // Load the asset from file
                                    try {
                                        a.loadFromFile(fileName,
                                                       file,
                                                       ImageAsset.MIME_JPEG);
                                    } catch (IOException ex) {
                                        s_log.error("Error creating new item", ex);
                                        throw new FormProcessException(ex);
                                    }

                                    // Save everything
                                    a.save();
                                    DataObject thisDataObj =
                                        SessionManager.getSession().retrieve
                                        (a.getOID());
                                    item.set(currentPropertyName, thisDataObj);
                                }
                            } else if (imageAction.equals(AddImageElement.ACTION_DELETE)) {
                                // do nothing, but this should never happen for create form
                            } else if (imageAction.equals
                                       (AddImageElement.ACTION_NONE)) {
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
                                    file = ((MultipartHttpServletRequest)
                                            state.getRequest())
                                        .getFile(currentPropertyName + "." +
                                                 FileUploadSection.FILE_UPLOAD);
                                    FileAsset a = new FileAsset();

                                    // Load the asset from file
                                    try {
                                        a.loadFromFile
                                            (fileName,
                                             file,
                                             FileUploadSection.GUESS_MIME
                                             .equals(mimeType) ?
                                             (String) null : mimeType);
                                    } catch (IOException ex) {
                                        s_log.error("Error creating new item", ex);
                                        throw new FormProcessException(ex);
                                    }

                                    // Save everything
                                    a.save();
                                    DataObject thisDataObj =
                                        SessionManager.getSession().retrieve
                                        (a.getOID());
                                    item.set(currentPropertyName, thisDataObj);
                                }
                            } else if (fileAction.equals(AddFileElement.ACTION_DELETE)) {
                                // do nothing, but this should never happen for create form
                            } else if (fileAction.equals
                                       (AddFileElement.ACTION_NONE)) {
                                // do nothing
                            }
                        }   else {
                            String typeName = currentProperty.getType()
                                .getQualifiedName();
                            try {
                                ContentType assocType =
                                    ContentType.findByAssociatedObjectType(typeName);
                                if (value == null || value.equals("")) {
                                    item.set(currentProperty.getName(), null);
                                } else {
                                    DataObject thisObj = SessionManager.getSession().retrieve
                                        (new OID(typeName, new BigDecimal((String) value)));
                                    item.set(currentProperty.getName(), thisObj);
                                }
                            } catch (DataObjectNotFoundException ex) {
                                // content type not found, don't set it.
                            }
                        }
                    }
                } catch (IllegalArgumentException ex) {
                    // value wasn't found in form model
                }
            }
            thisObjType = thisObjType.getSupertype();
        } while (thisObjType != null &&
                 thisObjType.getQualifiedName() != ContentPage.BASE_DATA_OBJECT_TYPE);

        ContentBundle bundle = new ContentBundle(item);
        bundle.setParent(m_parent.getFolder(state));
        bundle.setContentSection(m_parent.getContentSection(state));

        item.save();
        bundle.save();

        Iterator assetIter = assetsToSave.iterator();
        while (assetIter.hasNext()) {
            TextAsset thisAsset = (TextAsset) assetIter.next();
            thisAsset.setParent(item);
            thisAsset.save();
        }

        ContentSection section = m_parent.getContentSection(state);
        // Apply default workflow
        m_parentComponent.getWorkflowSection().applyWorkflow(state, item);

        // Start edititng the component right away
        m_parent.editItem(state, item);
    }

    /**
     * Ensure that the name of an item is unique within a folder. A "New
     * item" form should call this method in the validation listener.
     *
     * @param parent the folder in which to check
     * @param e the {@link FormSectionEvent} which was passed to the
     *    validation listener
     * @throws FormProcessException if the folder already contains an item
     * with the name the user provided on the input form.
     */
    public void validateNameUniqueness(Folder parent, FormSectionEvent e) 
                throws FormProcessException {
        FormData data = e.getFormData();

        if ( parent.getItem((String) data.get(NAME), false) != null ) {
            throw new FormProcessException(
                      "An Item with his name already exists",
                      GlobalizationUtil.globalize(
                        "cms.ui.authoring.an_item_with_this_name_already_exists")
            );
        }
    }


    /**
     * A utility method that will create a new userdefined content item of the
     * correct object type and tell the selection model to select the new item.
     *
     * @param state the current page state
     * @return the new content item (or a proper subclass thereof)
     * @throws com.arsdigita.bebop.FormProcessException
     * @pre state != null
     * @post return != null
     */
    public ContentPage createUDItem(PageState state)
        throws FormProcessException {

        ItemSelectionModel m = getItemSelectionModel();
        ContentPage item = null;

        // Create new item
        try {
            if (m_useContentType==null) {
                item = (ContentPage) m.createItem();
            } else {
                //create a new item of the correct object type, as defined in
                //the user-defined content type
                Class itemClass = Class.forName(m_useContentType.getClassName());

                Constructor constr = itemClass.getConstructor
                    (new Class[] {String.class});
                item = (ContentPage) constr.newInstance
                    (new Object[] {m_useContentType.getAssociatedObjectType()});
                item.setContentType(m_useContentType);
            }
        } catch (ServletException ex) {
            s_log.error("error creating new item", ex);
            throw new FormProcessException("couldn't create item", ex);
        } catch ( ClassNotFoundException ex) {
            s_log.error("error creating new item", ex);
            throw new FormProcessException("couldn't create item", ex);
        } catch (NoSuchMethodException ex) {
            s_log.error("error creating new item", ex);
            throw new FormProcessException("couldn't create item", ex);
        } catch (InstantiationException ex) {
            s_log.error("error creating new item", ex);
            throw new FormProcessException("couldn't create item", ex);
        } catch (IllegalAccessException ex) {
            s_log.error("programmer's error: cnstr should've been made public",
                        ex);
            throw new FormProcessException("couldn't create item", ex);
        } catch (InvocationTargetException ex) {
           s_log.error("error creating new item", ex);
           throw new FormProcessException("couldn't create item", ex);
        }

        // Make sure the item will be remembered across requests
        if(m.getSelectedKey(state) == null) {
            m.setSelectedObject(state, item);
        }
        return item;
    }

    /**
     * A print listener for content item selection -- a select list of all content
     * items of the given type
     *
     */
    public static class ContentItemSelectPrintListener implements PrintListener {

        private String m_contentTypeStr = null;

        public ContentItemSelectPrintListener(String contentType) {
            m_contentTypeStr = contentType;
        }

        public void prepare(PrintEvent event) {

            SingleSelect t = (SingleSelect) event.getTarget();
            t.clearOptions();
            PageState s = event.getPageState();

            //get the current content section
            ContentSection section = CMS.getContext().getContentSection();
            t.addOption(new Option("","-- select --"));

            ContentType contentType = null;
            try {
                contentType =
                    ContentType.findByAssociatedObjectType(m_contentTypeStr);
                ObjectType contentObjectType =
                    MetadataRoot.getMetadataRoot().getObjectType
                    (contentType.getAssociatedObjectType());
                DataCollection items =
                    SessionManager.getSession().retrieve(contentObjectType);

                while (items.next()) {
                    try {
                        ContentItem item = (ContentItem)
                            DomainObjectFactory.newInstance(items.getDataObject());
                        if (ContentItem.DRAFT.equals(item.getVersion())) {
                            BigDecimal itemID = item.getID();
                            String title = null;
                            if (item instanceof ContentPage) {
                                title = ((ContentPage)item).getTitle();
                            } else {
                                title = item.getName();
                            }
                            t.addOption(new Option(itemID.toString(), title));
                        }
                    } catch (PersistenceException ex) {
                        // object not found, don't add to SingleSelect
                    }
                }
            } catch (DataObjectNotFoundException e) {
                // object not found, no objects will be in select list
            }
        }
    }

    /**
     * Adds a component to this container.
     *
     * @param pc the component to add to this BasicPageForm
     * */
    @Override
    public void add(Component pc) {
        if (m_parentComponent != null) {
            m_parentComponent.add(pc);
        } else {
            super.add(pc);
        }
    }

    /**
     * Adds a component with the specified layout constraints to this
     * container. Layout constraints are defined in each layout container as
     * static ints. Use a bitwise OR to specify multiple constraints.
     *
     * @param pc the component to add to this container
     * @param constraints layout constraints (a bitwise OR of static ints 
     *                    in the particular layout)
     */
    @Override
    public void add(Component pc, int constraints) {
        if (m_parentComponent != null) {
            m_parentComponent.add(pc, constraints);
        } else {
            super.add(pc, constraints);
        }
    }

}
