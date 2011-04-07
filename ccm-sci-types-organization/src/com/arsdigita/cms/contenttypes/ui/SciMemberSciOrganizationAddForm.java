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
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter
 */
public class SciMemberSciOrganizationAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "sciMemberOrganization";

    public SciMemberSciOrganizationAddForm(ItemSelectionModel itemModel) {
        super("sciMemberOrganizationAddForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label(SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.organization.select_organization")));
        itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(SciOrganization.class.getName()));
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
        SciMember member = (SciMember) getItemSelectionModel().getSelectedObject(
                state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            member.addOrganization((SciOrganization) data.get(ITEM_SEARCH));

            init(fse);
        }

    }
}
