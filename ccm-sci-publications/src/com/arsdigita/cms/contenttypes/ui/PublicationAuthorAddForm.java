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
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.AuthorshipCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationAuthorAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener,
                   FormSubmissionListener {

    private static final Logger s_log = Logger.getLogger(
            PublicationAuthorAddForm.class);
    private PublicationPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "authors";
    private ItemSelectionModel m_itemModel;
    private SimpleEditStep editStep;
    private Label selectedAuthorLabel;
    private static final String ISEDITOR = "isEditor";
    private CheckboxGroup isEditor;

    public PublicationAuthorAddForm(ItemSelectionModel itemModel,
                                    SimpleEditStep editStep) {
        super("AuthorsEntryForm", itemModel);
        m_itemModel = itemModel;
        this.editStep = editStep;
        addSubmissionListener(this);
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

        selectedAuthorLabel = new Label("");
        add(selectedAuthorLabel);

        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.author.is_editor").localize()));
        isEditor = new CheckboxGroup("isEditorGroup");
        isEditor.addOption(new Option(ISEDITOR, ""));
        add(isEditor);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();

        GenericPerson author;
        Boolean editor;

        author = ((PublicationAuthorsPropertyStep) editStep).getSelectedAuthor();
        editor = ((PublicationAuthorsPropertyStep) editStep).
                isSelectedAuthorEditor();

        if (author == null) {
            s_log.warn("No author selected.");

            m_itemSearch.setVisible(state, true);
            selectedAuthorLabel.setVisible(state, false);
        } else {
            s_log.warn(String.format("Author is here: %s", author.getFullName()));

            data.put(ITEM_SEARCH, author);
            //data.put(AuthorshipCollection.EDITOR, editor);
            if ((editor != null) && editor) {
                isEditor.setValue(state, new String[]{ISEDITOR});
            } else {
                isEditor.setValue(state, null);
            }

            m_itemSearch.setVisible(state, false);
            selectedAuthorLabel.setLabel(author.getFullName(), state);
            selectedAuthorLabel.setVisible(state, true);
        }

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        Publication publication = (Publication) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            GenericPerson author;
            author = ((PublicationAuthorsPropertyStep) editStep).
                    getSelectedAuthor();

            Boolean editor;

            if (isEditor.getValue(state) == null) {
                editor = Boolean.FALSE;
            } else {
                editor = Boolean.TRUE;
            }

            if (author == null) {
                GenericPerson authorToAdd =
                              (GenericPerson) data.get(ITEM_SEARCH);
                authorToAdd = (GenericPerson) authorToAdd.getContentBundle().
                        getInstance(publication.getLanguage());

                publication.addAuthor(authorToAdd, editor);
            } else {
                AuthorshipCollection authors;

                authors = publication.getAuthors();

                while (authors.next()) {
                    if (authors.getAuthor().equals(author)) {
                        break;
                    }
                }

                authors.setEditor(editor);

                ((PublicationAuthorsPropertyStep) editStep).setSelectedAuthor(
                        null);
                ((PublicationAuthorsPropertyStep) editStep).
                        setSelectedAuthorEditor(null);

                authors.close();
            }
        }

        init(fse);
    }

    @Override
    public void submitted(FormSectionEvent fse) throws FormProcessException {
        if (getSaveCancelSection().getCancelButton().isSelected(
                fse.getPageState())) {
            ((PublicationAuthorsPropertyStep) editStep).setSelectedAuthor(
                    null);
            ((PublicationAuthorsPropertyStep) editStep).setSelectedAuthorEditor(
                    null);

            init(fse);
        }
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();
        boolean editing = false; //Are we editing the association

        if ((((PublicationAuthorsPropertyStep) editStep).getSelectedAuthor()
             == null)
            && (data.get(ITEM_SEARCH) == null)) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.authors.selectAuthor.no_author_selected"));
            return;
        }

        Publication publication = (Publication) getItemSelectionModel().
                getSelectedObject(state);
        GenericPerson author = (GenericPerson) data.get(ITEM_SEARCH);
        if (author == null) {
            author = ((PublicationAuthorsPropertyStep) editStep).
                    getSelectedAuthor();
            editing = true;
        }
        if (!(author.getContentBundle().hasInstance(publication.getLanguage(), true))) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.authors.selectAuthor.no_suitable_language_variant"));
            return;
        }

        if (!editing) {
            author = (GenericPerson) author.getContentBundle().getInstance(publication.
                    getLanguage());
            AuthorshipCollection authors = publication.getAuthors();
            authors.addFilter(
                    String.format("id = %s", author.getID().toString()));
            if (authors.size() > 0) {
                data.addError(PublicationGlobalizationUtil.globalize(
                        "publications.ui.authors.selectAuthor.already_added"));
            }

            authors.close();
        }
    }
}
