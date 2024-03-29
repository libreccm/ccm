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
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ImageAssetCollection;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;

/**
 * Builds an {@link ImageBrowserModel} by selecting all images which match the
 * given keyword. The keyword is supplied by the
 * <code>getSelectedKey</code> method of a
 * <code>SingleSelectionModel</code>. If the keyword is empty or null, the
 * builder will return an {@link EmptyImageBrowserModel}
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @version $Id: DefaultImageBrowserModelBuilder.java 1940 2009-05-29 07:15:05Z
 * terry $
 */
public class DefaultImageBrowserModelBuilder extends LockableImpl
        implements ImageBrowserModelBuilder, PaginationModelBuilder {

    private SingleSelectionModel m_keywordModel;
    private static ImageBrowserModel EMPTY_MODEL = new EmptyImageBrowserModel();
    private ImageBrowser m_imageBrowser;
    private RequestLocal m_size;
    private String m_key;
    private RequestLocal m_imageColl;
    private String m_context;

    /**
     * Construct a new DefaultImageBrowserModelBuilder
     *
     * @param keywordModel The SingleSelectionModel whose getSelectedKey(state)
     * method returns a string keyword
     * @param context the context for the retrieved items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     */
    public DefaultImageBrowserModelBuilder(
            SingleSelectionModel keywordModel, String context) {
        super();
        m_keywordModel = keywordModel;
        m_context = context;
        m_size = new RequestLocal();
        m_key = "";
        m_imageColl = new RequestLocal();
    }

    /**
     * Construct a new DefaultImageBrowserModelBuilder
     *
     * @param keywordModel The SingleSelectionModel whose getSelectedKey(state)
     * method returns a string keyword
     */
    public DefaultImageBrowserModelBuilder(SingleSelectionModel keywordModel) {
        this(keywordModel, ContentItem.DRAFT);
    }

    public ImageAssetCollection getImageCollectionByKeyword(PageState state) {
        String key = (String) m_keywordModel.getSelectedKey(state);
        if (!m_key.equals(key)) {
            m_key = key;

        }
        return ReusableImageAsset.getReusableImagesByKeyword(key, m_context);
    }

    /**
     * Construct an ImageBrowserModel for the current request
     */
    public ImageBrowserModel makeModel(ImageBrowser browser, PageState s) {
        //String key = (String)m_keywordModel.getSelectedKey(s);

        // pass through key even if null -- null key will return all rows in m_context.
        //ImageAssetCollection c = ReusableImageAsset.getReusableImagesByKeyword(key, m_context);
        return new DefaultImageBrowserModel((ImageAssetCollection) m_imageColl.get(s));
    }

    /**
     * @return the keyword selection model
     */
    public SingleSelectionModel getKeywordModel() {
        return m_keywordModel;
    }

    /**
     * @param context the new context for the items. Should be
     *   {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     */
    public void setContext(String context) {
        Assert.isUnlocked(this);
        m_context = context;
    }

    public int getTotalSize(Paginator paginator, PageState state) {

        int size = 0;

        if (m_size.get(state) == null) {

            String key = (String) m_keywordModel.getSelectedKey(state);

            //HACK: (by Quasimodo)
            // So, there is something broken. The selectedPage has to be resettet to
            // 1, if the keyword changes and therefor the imageCollection changes its
            // size.
            // Also, setting the imaheCollection range in this method seems to be 
            // wrong, because it changes the funtionality from a getter method to
            // something more complex.
            if (m_key == null || (key != null && !m_key.equalsIgnoreCase(key))) {
                paginator.setSelectedPageNum(state, 1);
                m_key = key;
            }
            // End hack
            
            ImageAssetCollection imageCollection = ReusableImageAsset.getReusableImagesByKeyword(key, m_context);
            if (imageCollection != null) {
                size = (int) imageCollection.size();
            }
            
            m_size.set(state, size);

            // This should not be done here.
            imageCollection.setRange(new Integer(paginator.getFirst(state)),
                    new Integer(paginator.getLast(state) + 1));
            m_imageColl.set(state, imageCollection);

        } else {
            size = ((Integer) m_size.get(state)).intValue();
        }

        return size;
    }

    public void setImageBrowser(ImageBrowser ib) {
        m_imageBrowser = ib;
    }

    /**
     * Indicates whether the paginator should be visible, based on the
     * visibility of the image browser itself.
     *
     * @return true if image browser is visible, or if the associated image
     * browser is unknown.
     */
    public boolean isVisible(PageState state) {
        return (m_imageBrowser != null) ? m_imageBrowser.isVisible(state) : true;
    }
}
