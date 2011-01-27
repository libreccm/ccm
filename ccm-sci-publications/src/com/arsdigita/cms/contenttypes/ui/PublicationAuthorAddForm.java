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
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.parameters.BooleanParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.AuthorshipCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationAuthorAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private static final Logger s_log = Logger.getLogger(
            PublicationAuthorAddForm.class);
    private PublicationPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    //private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "authors";
    private ItemSelectionModel m_itemModel;

    public PublicationAuthorAddForm(ItemSelectionModel itemModel) {
        super("AuthorsEntryForm", itemModel);
        m_itemModel = itemModel;
    }

    @Override
    protected void addWidgets() {
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.selectAuthor").localize()));
        m_itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(GenericPerson.class.
                getName()));
        add(m_itemSearch);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.author.is_editor").localize()));
        ParameterModel isEditorModel = new BooleanParameter(
                AuthorshipCollection.EDITOR);
        RadioGroup isEditorGroup = new RadioGroup(isEditorModel);
        isEditorGroup.addValidationListener(new NotNullValidationListener());
        isEditorGroup.setMetaDataAttribute(
                "label",
                (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.author.is_editor_label").
                localize());
        isEditorGroup.addOption(
                new Option(Boolean.FALSE.toString(),
                           (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.author.is_editor_false").localize()));
        isEditorGroup.addOption(
                new Option(Boolean.TRUE.toString(),
                           (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.author.is_editor_true").localize()));
        add(isEditorGroup);
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
        Publication publication = (Publication) getItemSelectionModel().
                getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().
              isSelected(state))) {
            publication.addAuthor(
                    (GenericPerson) data.get(ITEM_SEARCH),
                    (Boolean) data.get(AuthorshipCollection.EDITOR));
        }

        init(fse);
    }
}
