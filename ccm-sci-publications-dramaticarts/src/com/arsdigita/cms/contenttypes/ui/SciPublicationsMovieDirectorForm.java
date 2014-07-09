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
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.SciPublicationsDramaticArtsGlobalisationUtil;
import com.arsdigita.cms.contenttypes.SciPublicationsMovie;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsMovieDirectorForm extends BasicItemForm implements FormInitListener,
                                                                               FormProcessListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "setDirector";

    public SciPublicationsMovieDirectorForm(final ItemSelectionModel itemModel) {
        super("SciPublicationsMovieSetDirector", itemModel);
    }

    @Override
    public void addWidgets() {

        final SciPublicationsDramaticArtsGlobalisationUtil globalisationUtil
              = new SciPublicationsDramaticArtsGlobalisationUtil();

        itemSearch = new ItemSearchWidget(
            ITEM_SEARCH, ContentType.findByAssociatedObjectType(
                GenericPerson.BASE_DATA_OBJECT_TYPE));
        itemSearch.setEditAfterCreate(false);
        itemSearch.setLabel(globalisationUtil.globalize(
                            "publications.dramaticarts.ui.movie.director"));
        add(itemSearch);

    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();

        final SciPublicationsMovie movie = (SciPublicationsMovie) getItemSelectionModel()
            .getSelectedObject(state);

        if (getSaveCancelSection().getSaveButton().isSelected(state)) {
            final GenericPerson director = (GenericPerson) data.get(ITEM_SEARCH);
            movie.setDirector(director);
            itemSearch.publishCreatedItem(data, director);
        }

        init(event);
    }

    @Override
    public void validate(final FormSectionEvent event) throws FormProcessException {
        //final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final SciPublicationsDramaticArtsGlobalisationUtil globalisationUtil
              = new SciPublicationsDramaticArtsGlobalisationUtil();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(globalisationUtil.globalize(
                "publications.dramaticarts.ui.movie.director.no_director_selected"));
        }
        

    }

}
