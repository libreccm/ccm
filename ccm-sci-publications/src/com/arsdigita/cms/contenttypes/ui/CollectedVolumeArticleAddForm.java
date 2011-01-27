/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class CollectedVolumeArticleAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private static final Logger s_log =
                                Logger.getLogger(
            CollectedVolumeArticleAddForm.class);
    private CollectedVolumePropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "articles";
    private ItemSelectionModel m_itemModel;

    public CollectedVolumeArticleAddForm(ItemSelectionModel itemModel) {
        super("ArticlesAddForm", itemModel);
        m_itemModel = itemModel;
    }

    @Override
    protected void addWidgets() {
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.collected_volume.articles.select_article").
                localize()));
        m_itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(
                ArticleInCollectedVolume.class.getName()));
        add(m_itemSearch);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        CollectedVolume collectedVolume =
                        (CollectedVolume) getItemSelectionModel().
                getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().
              isSelected(state))) {
            collectedVolume.addArticle((ArticleInCollectedVolume) data.get(
                    ITEM_SEARCH));
        }

        init(fse);
    }
}
