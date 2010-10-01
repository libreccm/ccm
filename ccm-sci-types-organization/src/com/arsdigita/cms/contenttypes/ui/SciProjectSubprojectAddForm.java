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
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;

/**
 *
 * @author Jens Pelzetter
 */
public class SciProjectSubprojectAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "projects";

    public SciProjectSubprojectAddForm(ItemSelectionModel itemModel) {
        super("SubprojectAddForm", itemModel);
    }

    @Override
    protected void addWidgets() {
        add(new Label((String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.select_subproject").localize()));
        m_itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(
                SciProject.class.getName()));
        add(m_itemSearch);
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
        SciProject project = (SciProject) getItemSelectionModel().
                getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().
              isSelected(state))) {
            project.addSubProject((SciProject) data.get(ITEM_SEARCH));
        }

        init(fse);
    }
}
