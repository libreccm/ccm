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
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.Article;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.util.GlobalizationUtil;

/**
 * A form for modifying a single image asset attached to the article.
 * This is just a convenience class; children should override
 * the addWidgets, init and process methods.
 */
public abstract class BasicImageForm extends Form
    implements FormInitListener, FormProcessListener {

    public static final String versionId = "$Id: BasicImageForm.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private ItemSelectionModel m_itemModel, m_assetModel;
    private SaveCancelSection m_saveCancelSection;

    public static final String CAPTION = "caption";

    /**
     * Construct a new BasicImageForm
     *
     * @param formName The name for this form
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param assetModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current image asset
     */
    public BasicImageForm(String formName,
                          ItemSelectionModel itemModel, ItemSelectionModel assetModel
                          ) {
        super(formName, new ColumnPanel(2));
        m_itemModel = itemModel;
        m_assetModel = assetModel;

        ColumnPanel panel = (ColumnPanel)getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("2%");

        addWidgets();

        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        addInitListener(this);
        addProcessListener(this);
    }

    /**
     * Add various widgets to the form. Child classes should override
     * this method to perform all their widget-adding needs
     */
    protected void addWidgets() {
        add(new Label(GlobalizationUtil.globalize("cms.ui.authoring.caption")));

        // WAI requires all images to maintain ALT attribute
        // aplaws ticket 14026
        StringParameter captionParam = new StringParameter(CAPTION);
        captionParam.addParameterListener(new NotNullValidationListener());
        TextField captionWidget = new TextField(captionParam);

        add(captionWidget);
    }

    /**
     * Perform form initialization. Children should override this
     * this method to pre-fill the widgets with data, instantiate
     * the content item, etc.
     */
    public abstract void init(FormSectionEvent e) throws FormProcessException;

    /**
     * Process the form. Children should override this method to save
     * the user's changes to the database.
     */
    public abstract void process(FormSectionEvent e) throws FormProcessException;

    /**
     * Helper method to pre-fill the "Caption" textbox. Should be called from
     * the init listener
     *
     * @param e The FormSectionEvent
     */
    public void initCaption(FormSectionEvent e) {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        Article item = getArticle(state);
        ImageAsset asset = getImageAsset(state);

        if(item != null && asset != null) {
            String caption = item.getCaption(asset);
            if(caption != null)
                data.put(CAPTION, caption);
        }
    }

    /**
     * Helper method to process "Caption" textbox. Should be called from
     * the process listener
     *
     * @param e The FormSectionEvent
     */
    public void processCaption(FormSectionEvent e) {
        FormData data = e.getFormData();
        PageState state = e.getPageState();

        Article item = getArticle(state);
        ImageAsset asset = getImageAsset(state);

        if(item != null && asset != null) {
            item.addImage(asset, (String)data.get(CAPTION));
        }
    }

    /**
     * @return the item selection model used in this form
     */
    public ItemSelectionModel getItemSelectionModel() {
        return m_itemModel;
    }

    /**
     * @return the asset selection model used in this form
     */
    public ItemSelectionModel getAssetSelectionModel() {
        return m_assetModel;
    }

    /**
     * @return the save/cancel section for this form
     */
    public SaveCancelSection getSaveCancelSection() {
        return m_saveCancelSection;
    }

    /**
     * @param state The page state
     * @return the currently selected item
     */
    public Article getArticle(PageState state) {
        return (Article)m_itemModel.getSelectedObject(state);
    }

    /**
     * @param state The page state
     * @return the currently selected image asset
     */
    public ImageAsset getImageAsset(PageState state) {
        return (ImageAsset)m_assetModel.getSelectedObject(state);
    }

    /**
     * Set the image asset. This will probably be done in the process
     * listener
     *
     * @param state The page state
     * @param asset The image asset
     */
    public void setImageAsset(PageState state, ImageAsset asset) {
        m_assetModel.setSelectedObject(state, asset);
        // Force the item to reload, since the assets query is cached ?
        // ?
    }

}
