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


import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.Article;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ImageAssetCollection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.ArticleImageDisplay;
import com.arsdigita.cms.ui.CMSContainer;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.ImageChooser;
import com.arsdigita.cms.ui.SecurityPropertyEditor;
import com.arsdigita.cms.ui.SingleImageSelectionModel;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.ui.workflow.WorkflowLockedContainer;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;


/**
 * Display the image associated with the article (if any),
 * and present UI controls for associating a new image.
 * This component contains two {@link PropertyEditor}
 * instances: one instance that shows up when the article has
 * no associated image, and another instance that shows up when
 * the article has at least one image.
 * <p>
 * This class actually contains four inner classes:
 * <ul>
 *  <li>ImageUploadForm: a form for uploading a new image</li>
 *  <li>ImagePropertiesForm: a form for editing width/height/caption</li>
 *  <li>ArticleImagePane: an {@link com.arsdigita.cms.ui.ItemPropertySheet}
 *  which displays the properties of an image</li>
 *  <li>ArticleImageChooser: a {@link SimpleComponent} that contains
 *    an {@link ImageChooser} and an <code>ImageSelectionForm</code> (which
 *    is another inner class). The <code>ArticleImageChooser</code>
 *    uses the {@link ImageChooser} to select an image, then shows the
 *    form in order to assign a caption to the image and complete the
 *    image selection process.
 *  </li>
 * </ul>
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #20 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class ArticleImage extends SimpleContainer implements AuthoringStepComponent {

    public static final String versionId = "$Id: ArticleImage.java 754 2005-09-02 13:26:17Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private static final String IMAGE_ASSET = "image_asset";

    private static Logger s_log = Logger.getLogger(ArticleImage.class);

    private final ItemSelectionModel m_article;
    private final AuthoringKitWizard m_parent;
    private SingleImageSelectionModel m_assets;
    private BigDecimalParameter m_assetParam;

    private ArticleWithoutImage m_noImagePane;
    private ArticleImageChooser m_noImageChooser;

    private ArticleWithImage m_imagePane;
    private ArticleImageChooser m_imageChooser;

    private StringParameter m_streamlinedCreationParam;
    private static final String STREAMLINED = "_streamlined";
    private static final String STREAMLINED_DONE = "1";


    public ArticleImage(ItemSelectionModel itemModel,
                        AuthoringKitWizard parent) {
        super();

        m_parent = parent;
        m_article = itemModel;

        m_streamlinedCreationParam = 
            new StringParameter(parent.getContentType().getAssociatedObjectType() + "_image_done");

        // Add the asset selection model
        m_assetParam = new BigDecimalParameter(IMAGE_ASSET);
        m_assets = new SingleImageSelectionModel(m_assetParam, m_article);


        // This panel will be shown if there is no image selected.
        m_noImagePane = new ArticleWithoutImage(m_article, m_assets);
        m_noImageChooser = m_noImagePane.getChooser();
        final ImageSelectionForm noImageForm = m_noImageChooser.getImageSelectionForm();
        noImageForm.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent event)
                    throws FormProcessException {
                    PageState state = event.getPageState();
                    m_imagePane.showDisplayPane(state);
                    maybeForwardToNextStep(state);
                }
            });
        noImageForm.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent event) throws FormProcessException {
                    PageState state = event.getPageState();
                    if (noImageForm.getSaveCancelSection().getCancelButton()
                        .isSelected( state)) {
                        cancelStreamlinedCreation(state);
                    }
                }
            });
        add(m_noImagePane);

        m_imagePane = new ArticleWithImage(m_article, m_assets);
        m_imageChooser = m_imagePane.getChooser();
        final ImageSelectionForm imageForm = m_imageChooser.getImageSelectionForm();
        imageForm.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent event)
                    throws FormProcessException {
                    PageState state = event.getPageState();
                    m_imagePane.showDisplayPane(state);
                    maybeForwardToNextStep(state);
                }
            });


        // Reset state of all sub-components
        m_imagePane.getList().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    m_imageChooser.deselectImage(state);
                    m_imageChooser.getImageChooser().clearKeyword(state);
                    m_noImageChooser.deselectImage(state);
                    m_noImageChooser.getImageChooser().clearKeyword(state);
                }
            });
        imageForm.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent event) throws FormProcessException {
                    PageState state = event.getPageState();
                    if (imageForm.getSaveCancelSection().getCancelButton()
                        .isSelected( state)) {
                        cancelStreamlinedCreation(state);
                    }
                }
            });

        add(m_imagePane);

        // Reset the editing when this component becomes visible
        parent.getList().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    PageState state = event.getPageState();
		    // get the image fresh from the db each time this component displays;
		    // otherwise rollback doesn't appear to work on this pane
		    Article art = (Article)(m_article.getSelectedObject(state));
		    ImageAssetCollection col = art.getImages();
		    ImageAsset img = null;
		    if (col.next()) {
			img = col.getImage();
		    }
                    col.close();
		    m_assets.setSelectedObject(state, img);
                    m_imagePane.showDisplayPane(state);
                    m_imageChooser.deselectImage(state);
                    m_noImageChooser.deselectImage(state);
                }
            });
    }

    // Create the image chooser
    private ArticleImageChooser addChooser(SecurityPropertyEditor e) {
        ArticleImageChooser c = new ArticleImageChooser(m_article, m_assets);
        c.getImageSelectionForm().addProcessListener(
                                                     new FormProcessListener() {
                                                         public void process(FormSectionEvent param_e) throws FormProcessException {
                                                             m_imagePane.showDisplayPane(param_e.getPageState());
                                                         }
                                                     });
        ImageChooser ch = c.getImageChooser();
        e.addCancelListener(ch.getForm(), ch.getFormCancelButton());
        e.addComponent("browse", "Select existing image",
                       new WorkflowLockedComponentAccess(c,m_article));

        return c;
    }

    // Register the image id
    public void register(Page p) {
        p.addComponentStateParam(this, m_assetParam);
        p.addGlobalStateParam(m_streamlinedCreationParam);
    }

    // Generate XML: Show either the image pane or the no image pane
    public void generateXML(PageState state, Element parent) {
        if(m_assets.getSelectedObject(state) == null) {
            m_noImagePane.generateXML(state, parent);
        } else {
            m_imagePane.generateXML(state, parent);
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
        return m_article;
    }


    /**
     * Forward to the next step if the streamlined creation parameter
     * is turned on _and_  the streamlined_creation global state param
     * is set to 'active'
     *
     * @param state the PageState
     */
    public void maybeForwardToNextStep(PageState state) {
        if (ContentItemPage.isStreamlinedCreationActive(state) &&
            !STREAMLINED_DONE.equals(state.getValue(m_streamlinedCreationParam))) {
            state.setValue(m_streamlinedCreationParam, STREAMLINED_DONE);
            fireCompletionEvent(state);
        }
    }

    /**
     * Cancel streamlined creation for this step if the streamlined
     * creation parameter is turned on _and_ the streamlined_creation
     * global state param is set to 'active'
     *
     * @param state the PageState
     */
    public void cancelStreamlinedCreation(PageState state) {
        if (ContentItemPage.isStreamlinedCreationActive(state)) {
            state.setValue(m_streamlinedCreationParam, STREAMLINED_DONE);
        }
    }

    private class ArticleWithoutImage extends SecurityPropertyEditor {

        private final ItemSelectionModel m_articleWithoutImage;
        private final ItemSelectionModel m_assetsWithoutImage;

        private ImageUploadForm m_uploadFormWithoutImage;
        private ArticleImageChooser m_chooserWithoutImage;

        public ArticleWithoutImage(ItemSelectionModel article,
                                   ItemSelectionModel assets) {
            super();

            m_articleWithoutImage = article;
            m_assetsWithoutImage = assets;

            Label l = new Label(GlobalizationUtil.globalize("cms.ui.authoring.this_article_does_not_have_an_image"));
            l.setFontWeight(Label.ITALIC);
            setDisplayComponent(l);

            m_uploadFormWithoutImage = new ImageUploadForm(m_articleWithoutImage, 
                                                           m_assetsWithoutImage, 
                                                           ArticleImage.this);
            add("upload", "Upload a new image",
                new WorkflowLockedComponentAccess(m_uploadFormWithoutImage, m_articleWithoutImage),
                m_uploadFormWithoutImage.getSaveCancelSection().getCancelButton());


            m_chooserWithoutImage = new ArticleImageChooser(m_articleWithoutImage, m_assetsWithoutImage);
            ImageChooser imgChooser = m_chooserWithoutImage.getImageChooser();
            addCancelListener(imgChooser.getForm(),
                              imgChooser.getFormCancelButton());
            addComponent("browse", "Select existing image",
                         new WorkflowLockedComponentAccess(m_chooserWithoutImage, m_articleWithoutImage));
        }

        public ArticleImageChooser getChooser() {
            return m_chooserWithoutImage;
        }
    }


    private class ArticleWithImage extends SecurityPropertyEditor {

        private final ItemSelectionModel m_articleWithImage;
        private final ItemSelectionModel m_assetsWithImage;

        private CMSContainer m_display;

        private ActionLink m_editLink;
        private ActionLink m_uploadLink;
        private ActionLink m_selectLink;
        private ActionLink m_removeLink;

        private ImageUploadForm m_uploadFormWithImage;
        private ImagePropertiesForm m_editFormWithImage;
        private ArticleImageChooser m_chooserWithImage;

        public ArticleWithImage(ItemSelectionModel article,
                                ItemSelectionModel assets) {
            super();

            m_articleWithImage = article;
            m_assetsWithImage = assets;

            // This panel will be shown if there is an image.
            m_display = new CMSContainer();
            m_display.add(new ArticleImageDisplay(m_articleWithImage, m_assetsWithImage));

            // The "edit caption" link.
            m_editLink = new ActionLink( (String) GlobalizationUtil.globalize("cms.ui.authoring.edit_caption").localize());
            m_editLink.setClassAttr("actionLink");
            m_editLink.setIdAttr("edit_link");
            m_display.add(new WorkflowLockedContainer(m_editLink, m_articleWithImage));

            // The "upload image" link.
            m_uploadLink = new ActionLink( (String) GlobalizationUtil.globalize("cms.ui.authoring.upload_a_new_image").localize());
            m_uploadLink.setClassAttr("actionLink");
            m_uploadLink.setIdAttr("upload_link");
            m_display.add(new WorkflowLockedContainer(m_uploadLink, m_articleWithImage));

            // The "select a new image" link.
            m_selectLink = new ActionLink( (String) GlobalizationUtil.globalize("cms.ui.authoring.select_an_existing_image").localize());
            m_selectLink.setClassAttr("actionLink");
            m_selectLink.setIdAttr("select_link");
            m_display.add(new WorkflowLockedContainer(m_selectLink, m_articleWithImage));

            // The "remove image" link.
            m_removeLink = new ActionLink( (String) GlobalizationUtil.globalize("cms.ui.authoring.remove_image").localize());
            m_removeLink.setClassAttr("actionLink");
            m_removeLink.setIdAttr("remove_image_link");
            m_removeLink.setConfirmation("Are you sure you wish to " +
                                         "remove this image from the article?");
            m_removeLink.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        PageState state = event.getPageState();
                        ImageAsset img = getImageAsset(state);
                        Article articleTarget = getArticle(state);
                        articleTarget.removeImage(img);
			articleTarget.save();
                        m_assetsWithImage.setSelectedObject(state, null);
			m_imageChooser.deselectImage(state);
			m_imageChooser.getImageChooser().clearKeyword(state);
			m_noImageChooser.deselectImage(state);
			m_noImageChooser.getImageChooser().clearKeyword(state);
			m_noImagePane.showDisplayPane(event.getPageState());
                        Utilities.disableBrowserCache(state.getResponse());
                    }
                });
            m_display.add(new WorkflowLockedContainer(m_removeLink, m_articleWithImage));
            setDisplayComponent(m_display);

            // The edit form.
            m_editFormWithImage = new ImagePropertiesForm(m_articleWithImage, m_assetsWithImage);
            addComponent("edit",
                         new WorkflowLockedComponentAccess(m_editFormWithImage, m_articleWithImage));
            addListeners(m_editFormWithImage,
                         m_editFormWithImage.getSaveCancelSection().getCancelButton());
            addVisibilityListener(m_editLink, "edit");

            // The upload form.
            m_uploadFormWithImage = new ImageUploadForm(m_articleWithImage, 
                                                        m_assetsWithImage,
                                                        ArticleImage.this);
            addComponent("upload",
                         new WorkflowLockedComponentAccess(m_uploadFormWithImage, m_articleWithImage));
            addListeners(m_uploadFormWithImage,
                         m_uploadFormWithImage.getSaveCancelSection().getCancelButton());
            addVisibilityListener(m_uploadLink, "upload");

            // The select form.
            m_chooserWithImage = new ArticleImageChooser(m_articleWithImage, m_assetsWithImage);
            ImageChooser imgChooser = m_chooserWithImage.getImageChooser();
            addCancelListener(imgChooser.getForm(),
                              imgChooser.getFormCancelButton());
            addComponent("browse",
                         new WorkflowLockedComponentAccess(m_chooserWithImage, m_articleWithImage));
            addVisibilityListener(m_selectLink, "browse");
        }

        public ArticleImageChooser getChooser() {
            return m_chooserWithImage;
        }

        private ImageAsset getImageAsset(PageState state) {
            ImageAsset image = (ImageAsset) m_assetsWithImage.getSelectedObject(state);
            Assert.assertNotNull(image, "Image asset");
            return image;
        }

        private Article getArticle(PageState state) {
            Article article = (Article) m_articleWithImage.getSelectedObject(state);
            Assert.assertNotNull(article, "Article");
            return article;
        }

    }



}
