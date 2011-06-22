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
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter
 */
public class UnPublishedOrganizationForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "unPublishedOrga";

    public UnPublishedOrganizationForm(final ItemSelectionModel itemModel) {
        super("UnPublishedOrganizationForm", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.organization").localize()));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                                          ContentType.findByAssociatedObjectType(
                GenericOrganizationalUnit.class.getName()));
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
        UnPublished unPublished = (UnPublished) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            GenericOrganizationalUnit orga = (GenericOrganizationalUnit) data.
                    get(ITEM_SEARCH);
            orga = (GenericOrganizationalUnit) orga.getContentBundle().
                    getInstance(unPublished.getLanguage());

            unPublished.setOrganization(orga);

        }

        init(fse);
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.unpublished.organization.no_orga_selected"));
            return;
        }

        UnPublished unPublished = (UnPublished) getItemSelectionModel().
                getSelectedObject(state);
        GenericOrganizationalUnit orga = (GenericOrganizationalUnit) data.get(
                ITEM_SEARCH);
        if (!(orga.getContentBundle().hasInstance(unPublished.getLanguage()))) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.unpublished.organization.no_suitable_language_variant"));
            return;
        }
    }
}
