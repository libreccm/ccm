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


import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.ArticleImageAssociation;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.ImageBrowser;
import com.arsdigita.cms.ui.ImageChooser;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.OID;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * A component that can assign an existing image to an article.
 * Consists of an ImageChooser and an ImageSelectionForm
 *
 * @author Stanislav Freidin
 * @author Michael Pih
 * @version $Revision: #12 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class ArticleImageChooser extends SimpleContainer {

    public static final String CHOSEN_IMAGE = "chim";

    public static final String versionId = "$Id: ArticleImageChooser.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private static Logger s_log =
        Logger.getLogger(ArticleImageChooser.class);

    private final ItemSelectionModel m_assets;
    private final ItemSelectionModel m_choiceModel;

    private ImageChooser m_chooser;
    private ImageSelectionForm m_form;

    private BigDecimalParameter m_chosenImage;


    /**
     * Construct a new ArticleImageChooser
     */
    public ArticleImageChooser(ItemSelectionModel itemModel,
                               ItemSelectionModel assetModel) {
        super();
        m_assets = assetModel;

        m_chooser = new ImageChooser();
        add(m_chooser);

        // Show the form when an image is clicked
        m_chooser.addImageActionListener(new ImageBrowser.LinkActionListener() {
                public void linkClicked(PageState state, BigDecimal imageId) {
                    s_log.debug("SELECTING: " + imageId);
                    selectImage(state, imageId);
                }
                public void deleteClicked(PageState state, BigDecimal imageId) {
                    s_log.debug("DELETING: " + imageId);
                    deleteImage(state, imageId);
                }
            });

        // Clone the asset model, since we don't want to contaminate
        // the original data in case the user decides to cancel the
        // form submission
        m_chosenImage = new BigDecimalParameter(CHOSEN_IMAGE);

        ContentType t = assetModel.getContentType();
        if ( t == null ) {
            m_choiceModel =
                new ItemSelectionModel(assetModel.getJavaClass().getName(),
                                       assetModel.getObjectType(),
                                       m_chosenImage);
        } else {
            m_choiceModel =
                new ItemSelectionModel(assetModel.getContentType(), m_chosenImage);
        }

        m_form = new ImageSelectionForm(itemModel, m_choiceModel);

        // Hide the form again when the user clicks Cancel
        m_form.addSubmissionListener(new FormSubmissionListener() {
                public void submitted(FormSectionEvent e) throws FormProcessException {
                    PageState state = e.getPageState();
                    if(m_form.getSaveCancelSection().getCancelButton().isSelected(state)) {
                        deselectImage(state);
                        throw new FormProcessException( (String) GlobalizationUtil.globalize("cms.ui.authoring.submission_cancelled").localize());
                    }
                }
            });

        // Propagate the image selection on success
        m_form.addProcessListener(new FormProcessListener() {
                public void process(FormSectionEvent e) throws FormProcessException {
                    PageState state = e.getPageState();
                    m_assets.setSelectedObject(state,
                                               m_choiceModel.getSelectedObject(state));
                }
            });

        add(m_form);
    }

    // Hide the form by default, register the "chosen image" parameter
    public void register(Page p) {
        p.addComponentStateParam(this, m_chosenImage);
        p.setVisibleDefault(m_form, false);
    }

    /**
     * @return the image selection form
     */
    public ImageSelectionForm getImageSelectionForm() {
        return m_form;
    }

    /**
     * @return the image chooser
     */
    public ImageChooser getImageChooser() {
        return m_chooser;
    }

    /**
     * Select the specified image and show the form
     */
    public void selectImage(PageState s, BigDecimal imageId) {
        s.setVisible(m_form, true);
        s.setVisible(m_chooser, false);
        m_choiceModel.setSelectedKey(s, imageId);
    }

    /**
     * Select the specified image and show the form
     */
    public void deleteImage(PageState s, BigDecimal imageId) {
        SecurityManager sm = Utilities.getSecurityManager(s);
        if (sm.canAccess(s.getRequest(),SecurityManager.DELETE_IMAGES) ) {
            try {
                ImageAsset asset = (ImageAsset) DomainObjectFactory.newInstance
                    (new OID(ImageAsset.BASE_DATA_OBJECT_TYPE,imageId));
                if (!ArticleImageAssociation.imageHasAssociation(asset)) {
                    asset.setLive(null);
                    ItemCollection pendingVersions = asset.getPendingVersions();
                    while(pendingVersions.next()) {
                        ContentItem item = pendingVersions.getContentItem();
                        asset.removePendingVersion(item);
                    }
                    asset.delete();

                }
            } catch (DataObjectNotFoundException e) {
                // can't find asset, don't delete
            }

        }
    }

    /**
     * Deselect the image and hide the form
     */
    public void deselectImage(PageState s) {
        s.setVisible(m_form, false);
        s.setVisible(m_chooser, true);
        m_choiceModel.clearSelection(s);
        m_chooser.clearSelection(s);
    }

}
