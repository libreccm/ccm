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
import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter
 */
public class ExpertiseOrganizationForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "expertiseOrganization";

    public ExpertiseOrganizationForm(final ItemSelectionModel itemModel) {
        super("ExpertiseOrganizationForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.organization")));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(GenericOrganizationalUnit.class.
                getName()));
        add(itemSearch);
    }

    @Override
    public void init(final FormSectionEvent fse) throws FormProcessException{
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        Expertise expertise = (Expertise) getItemSelectionModel().getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            expertise.setOrganization((GenericOrganizationalUnit) data.get(ITEM_SEARCH));

            init(fse);
        }
    }
}
