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
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationGenericOrganizationalsUnitCollection;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationGenericOrganizationalUnitAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final static String ITEM_SEARCH = "organizationalunits";

    public PublicationGenericOrganizationalUnitAddForm(
            final ItemSelectionModel itemModel) {
        super("PublicationGenericOrganizationalUnitAddForm", itemModel);
    }

    @Override
    public void addWidgets() {
        add(new Label((String) PublicationGlobalizationUtil.globalize(
                "publications.ui.orgaunit.select").localize()));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(GenericOrganizationalUnit.class.
                getName()));
        itemSearch.setDisableCreatePane(true);
        add(itemSearch);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        final FormData data = fse.getFormData();
        final PageState state = fse.getPageState();

        final Publication publication = (Publication) getItemSelectionModel().
                getSelectedObject(state);

        if ((publication != null)
            && (getSaveCancelSection().getSaveButton().isSelected(state))) {
            GenericOrganizationalUnit orgaunit =
                                      (GenericOrganizationalUnit) data.get(
                    ITEM_SEARCH);
            orgaunit = (GenericOrganizationalUnit) orgaunit.getContentBundle().
                    getInstance(publication.getLanguage(), true);
            
            publication.addOrganizationalUnit(orgaunit);
        }

        init(fse);
    }

    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.orgaunit.select.nothing"));
            return;
        }

        final Publication publication = (Publication) getItemSelectionModel().
                getSelectedObject(state);
        GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) data.
                get(ITEM_SEARCH);
        if (!(orgaunit.getContentBundle().hasInstance(publication.getLanguage(),
                                                      Kernel.getConfig().
              languageIndependentItems()))) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.orgaunit.no_suitable_language_variant"));
            return;
        }

        orgaunit = (GenericOrganizationalUnit) orgaunit.getContentBundle().
                getInstance(publication.getLanguage(), true);
        final PublicationGenericOrganizationalsUnitCollection orgaunits =
                                                              publication.
                getOrganizationalUnits();
        orgaunits.addFilter(
                String.format("id = %s", orgaunit.getID().toString()));
        if (orgaunits.size() > 0) {
            data.addError(PublicationGlobalizationUtil.globalize(
                    "publications.ui.orgaunit.already_added"));
        }

        orgaunits.close();
    }
}
