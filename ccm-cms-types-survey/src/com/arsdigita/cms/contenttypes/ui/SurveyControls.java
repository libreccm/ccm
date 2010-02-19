package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.formbuilder.FormSectionItem;
import com.arsdigita.cms.formbuilder.FormSectionWrapper;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.formbuilder.FormSectionModelBuilder;
import com.arsdigita.cms.ui.workflow.WorkflowLockedContainer;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.ui.ControlEditor;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;

public class SurveyControls extends ControlEditor {

    private ItemSelectionModel m_itemModel;

    public SurveyControls(ItemSelectionModel item, AuthoringKitWizard parent) {
        super("forms-cms", new SurveySingleSelectionModel(item), true);

        m_itemModel = item;

        setFormSectionModelBuilder(new FormSectionModelBuilder(item));
    }

    @Override
    protected void addEditableComponent(Container container, Component child) {
        WorkflowLockedContainer lock =
                new WorkflowLockedContainer(((SurveySingleSelectionModel) getFormModel()).getItemModel());
        lock.add(child);
        super.addEditableComponent(container, lock);
    }

    @Override
    protected PersistentComponent getFormSection(PageState state, BigDecimal sectionID) {
        FormSectionItem section = null;
        try {
            section = (FormSectionItem) DomainObjectFactory.newInstance(new OID(FormSectionItem.BASE_DATA_OBJECT_TYPE, sectionID));
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException("cannot load section", ex);
        }

        FormSectionWrapper wrapper = FormSectionWrapper.create(section, ContentItem.DRAFT);

        return wrapper;
    }

    @Override
    protected boolean addItemEditObserver(PageState state) {
        return Utilities.getSecurityManager(state).canAccess(
                state.getRequest(),
                SecurityManager.EDIT_ITEM,
                (ContentItem) m_itemModel.getSelectedObject(state));
    }
}
