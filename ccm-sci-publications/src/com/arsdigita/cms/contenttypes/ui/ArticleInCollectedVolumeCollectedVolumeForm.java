/*
 * Copyright (c) 2010 Jens Pelzetter
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
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.PublicationsConfig;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

/**
 * Form for adding an association between an ArticleInCollectedVolume and a
 * CollectedVolume.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ArticleInCollectedVolumeCollectedVolumeForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "collectedVolume";
    private final static PublicationsConfig config = new PublicationsConfig();

    static {
        config.load();
    }

    public ArticleInCollectedVolumeCollectedVolumeForm(
            ItemSelectionModel itemModel) {
        super("ArticleInCollectedVolumeCollectedVolume", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label(
                PublicationGlobalizationUtil.globalize(
                "publications.ui.articleInCollectedVolume.selectCollectedVolume")));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                                          ContentType.findByAssociatedObjectType(
                CollectedVolume.class.getName()));
        if ((config.getDefaultCollectedVolumesFolder() != null) && (config.getDefaultCollectedVolumesFolder() != 0)) {
            itemSearch.setDefaultCreationFolder(new Folder(new BigDecimal(config.getDefaultCollectedVolumesFolder())));
        }
        add(itemSearch);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        ArticleInCollectedVolume article =
                                 (ArticleInCollectedVolume) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            CollectedVolume collectedVolume = (CollectedVolume) data.get(
                    ITEM_SEARCH);
            collectedVolume =
            (CollectedVolume) collectedVolume.getContentBundle().getInstance(
                    article.getLanguage());

            article.setCollectedVolume(collectedVolume);
            itemSearch.publishCreatedItem(data, collectedVolume);
        }

        init(fse);
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.articleInCollectedVolume.selectCollectedVolume.no_collected_volume_selected"));
            return;
        }

        ArticleInCollectedVolume article =
                                 (ArticleInCollectedVolume) getItemSelectionModel().
                getSelectedObject(state);

        CollectedVolume collectedVolume =
                        (CollectedVolume) data.get(ITEM_SEARCH);

        if (!(collectedVolume.getContentBundle().hasInstance(
              article.getLanguage(),
              Kernel.getConfig().
              languageIndependentItems()))) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.articleInCollectedVolume.selectCollectedVolume.no_suitable_language_variant"));
        }
    }

}
