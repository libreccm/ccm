package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
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
public class PublicationAuthorAddForm extends BasicItemForm {

    private static final Logger s_log = Logger.getLogger(
            PublicationAuthorAddForm.class);
    private PublicationPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
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
                "publications.ui.authors.is_editor").localize()));
        ParameterModel isEditorModel = new BooleanParameter(
                AuthorshipCollection.EDITOR);
        RadioGroup isEditorGroup = new RadioGroup(isEditorModel);
        isEditorGroup.addValidationListener(new NotNullValidationListener());
        isEditorGroup.setMetaDataAttribute(
                "label",
                (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.is_editor_label").
                localize());
        isEditorGroup.addOption(
                new Option(Boolean.FALSE.toString(),
                           (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.is_editor_false").localize()));
        isEditorGroup.addOption(
                new Option(Boolean.TRUE.toString(),
                           (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.is_editor_true").localize()));
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

        if (!(this.getSaveCancelSection().getCancelButton().isSelected(state))) {
            publication.addAuthor(
                    (GenericPerson) data.get(ITEM_SEARCH),
                    (Boolean) data.get(AuthorshipCollection.EDITOR));
        }

        init(fse);
    }
}
