/*
 * Copyright (c) 2014 Jens Pelzetter
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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.SciPublicationsAboutGlobalizationUtil;
import com.arsdigita.cms.contentassets.SciPublicationsAboutService;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsAboutDiscussingForm extends BasicItemForm implements FormProcessListener,
                                                                                 FormInitListener {

    private ItemSearchWidget itemSearch;
    private final static String ITEM_SEARCH = "discussingPublications";
    private final ItemSelectionModel itemModel;

    public SciPublicationsAboutDiscussingForm(final ItemSelectionModel itemModel) {

        super("SciPublicationsAboutDiscussingForm", itemModel);
        this.itemModel = itemModel;
    }

    @Override
    public void addWidgets() {

        itemSearch = new ItemSearchWidget(
            ITEM_SEARCH,
            ContentType.findByAssociatedObjectType(Publication.class.getName()));
        itemSearch.setLabel(SciPublicationsAboutGlobalizationUtil.globalize(
            "com.arsdigita.cms.contentassets.about.discusses.publication.select_discussing"));
        add(itemSearch);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

        setVisible(event.getPageState(), true);

    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final Publication discussed = (Publication) itemModel.getSelectedObject(state);
        final SciPublicationsAboutService service = new SciPublicationsAboutService();

        if (!(getSaveCancelSection().getCancelButton().isSelected(state))) {
            final Publication discussing = (Publication) data.get(ITEM_SEARCH);
            service.addDiscussingPublication(discussed, discussing);
        }

        init(event);
    }

    @Override
    public void validate(final FormSectionEvent event) throws FormProcessException {

        final FormData data = event.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(SciPublicationsAboutGlobalizationUtil.globalize(
                "com.arsdigita.cms.contentassets.about.discussing.publication.none_selected"));
        }
    }

}
