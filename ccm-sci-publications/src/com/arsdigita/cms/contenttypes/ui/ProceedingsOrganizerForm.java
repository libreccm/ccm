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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter
 */
public class ProceedingsOrganizerForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "departmentOrga";

    public ProceedingsOrganizerForm(final ItemSelectionModel itemModel) {
        super("ProceedingsOrganizerForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.proceedings.organizer")));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(GenericOrganizationalUnit.class.
                getName()));
        add(itemSearch);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        Proceedings proceedings = (Proceedings) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            GenericOrganizationalUnit organizer =
                                      (GenericOrganizationalUnit) data.get(
                    ITEM_SEARCH);
            organizer = (GenericOrganizationalUnit) organizer.getContentBundle().
                    getInstance(proceedings.getLanguage());

            proceedings.setOrganizerOfConference(organizer);

        }

        init(fse);
    }

    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.proceedings.organizer.no_orga_selected"));
            return;
        }

        Proceedings proceedings = (Proceedings) getItemSelectionModel().
                getSelectedObject(state);
        GenericOrganizationalUnit organizer = (GenericOrganizationalUnit) data.
                get(ITEM_SEARCH);
        if (!(organizer.getContentBundle().hasInstance(proceedings.getLanguage()))) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.proceedings.organizer.no_suitable_language_variant"));
            return;
        }
    }
}
