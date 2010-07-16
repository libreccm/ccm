/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.Project;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class ProjectAddChildForm extends  BasicItemForm{

    private static final Logger s_log = Logger.getLogger(ProjectAddChildForm.class);
    private ProjectChildrenPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private SaveCancelSection m_saveCancelSection;
    private final String ITEM_SEARCH = "projectChild";
    private ItemSelectionModel m_itemModel;
    
    public ProjectAddChildForm(ItemSelectionModel itemModel) {
        super("ChildAddForm", itemModel);
    }
    
    @Override
    protected void addWidgets() {
        add(new Label((String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.select_child").localize()));
        this.m_itemSearch = new ItemSearchWidget(ITEM_SEARCH, ContentType.
                findByAssociatedObjectType(
                Project.BASE_DATA_OBJECT_TYPE));
        add(this.m_itemSearch);
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
        GenericOrganizationalUnit parent = (GenericOrganizationalUnit) getItemSelectionModel().
                getSelectedObject(state);

        if (!(this.getSaveCancelSection().getCancelButton().isSelected(state))) {
            parent.addOrgaUnitChildren((GenericOrganizationalUnit) data.get(
                    ITEM_SEARCH));
        }

        init(fse);
    }

}
