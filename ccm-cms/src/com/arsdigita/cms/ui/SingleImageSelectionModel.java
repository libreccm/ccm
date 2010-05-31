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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.contenttypes.GenericArticle;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ImageAssetCollection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.domain.DomainObject;


/**
 * Selects a single image for an article.
 *
 * @see com.arsdigita.cms.ui.authoring.ArticleImage
 * @see com.arsdigita.cms.ui.ArticleImageDisplay
 *
 * @version $Id: SingleImageSelectionModel.java 287 2005-02-22 00:29:02Z sskracic $
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 */

public class SingleImageSelectionModel extends ItemSelectionModel {

    private ItemSelectionModel m_articleModel;

    /**
     * Construct a new <code>SingleImageSelectionModel</code>
     *
     * @param javaClass the Java class name of the {@link ImageAsset} subclass
     *   that this model deals with
     * @param objetcType the PDL object type of the {@link ImageAsset} subclass
     *   that this model deals with
     * @param param the {@link BigDecimalParameter} where the image ID will
     *   be stored
     * @param articleModel the {@link ItemSelectionModel} which will supply
     *   the current article
     */
    public SingleImageSelectionModel(
                                     String javaClass, String objectType, BigDecimalParameter param,
                                     ItemSelectionModel articleModel
                                     ) {
        this(javaClass, objectType, new ParameterSingleSelectionModel(param),
             articleModel);
    }

    /**
     * Construct a new <code>SingleImageSelectionModel</code>
     *
     * @param javaClass the Java class name of the {@link ImageAsset} subclass
     *   that this model deals with
     * @param objetcType the PDL object type of the {@link ImageAsset} subclass
     *   that this model deals with
     * @param imageModel the {@link SingleSelectionModel} which will store the
     *   image ID
     * @param articleModel the {@link ItemSelectionModel} which will supply
     *   the current article
     */
    public SingleImageSelectionModel(
                                     String javaClass, String objectType, SingleSelectionModel imageModel,
                                     ItemSelectionModel articleModel
                                     ) {
        super(javaClass, objectType, imageModel);
        m_articleModel = articleModel;
    }

    /**
     * Construct a new <code>SingleImageSelectionModel</code>
     *
     * @param imageModel the {@link SingleSelectionModel} which will store the
     *   image ID
     * @param articleModel the {@link ItemSelectionModel} which will supply
     *   the current article
     */
    public SingleImageSelectionModel(
                                     SingleSelectionModel imageModel, ItemSelectionModel articleModel
                                     ) {
        this(ImageAsset.class.getName(), ImageAsset.BASE_DATA_OBJECT_TYPE,
             imageModel, articleModel);
    }

    /**
     * Construct a new <code>SingleImageSelectionModel</code>
     *
     * @param param the {@link BigDecimalParameter} where the image ID will
     *   be stored
     *
     * @param articleModel the {@link ItemSelectionModel} which will supply
     *   the current article
     */
    public SingleImageSelectionModel(
                                     BigDecimalParameter param, ItemSelectionModel articleModel
                                     ) {
        this(ImageAsset.class.getName(), ImageAsset.BASE_DATA_OBJECT_TYPE,
             param, articleModel);
    }

    // Load the first asset for the article, if neccessary.
    private void checkAsset(PageState state) {
        if ( !isInitialized(state) ) {
            // Load the object from the item.
            com.arsdigita.cms.ContentItem temp =
                (com.arsdigita.cms.ContentItem)m_articleModel.getSelectedObject(state);
            GenericArticle item = null;
            if ( temp != null ) {
                item =
                    (GenericArticle) com.arsdigita.cms.ACSObjectFactory.castContentItem(temp);
            }
            if ( item != null ) {
                ImageAssetCollection images = item.getImages();
                if ( images.next() ) {
                    setSelectedObject(state, images.getImage());
                    images.close();
                }
            }
        }
    }

    /**
     * Get the currently selected image. If no image is selected,
     * select the first image for the article.
     */
    public DomainObject getSelectedObject(PageState state) {
        checkAsset(state);
        return super.getSelectedObject(state);
    }

    /**
     * Get the id of the currently selected image
     */
    public Object getSelectedKey(PageState state) {
        checkAsset(state);
        return super.getSelectedKey(state);
    }

    /**
     * @return the {@link ItemSelectionModel} which supplies the article
     */
    public ItemSelectionModel getArticleSelectionModel() {
        return m_articleModel;
    }
}
