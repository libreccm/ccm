package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.AbstractSingleSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Survey;
import com.arsdigita.cms.util.GlobalizationUtil;

public class SurveySingleSelectionModel extends AbstractSingleSelectionModel {

    private ItemSelectionModel m_item;

    public SurveySingleSelectionModel(ItemSelectionModel item) {
        m_item = item;
    }

    public ItemSelectionModel getItemModel() {
        return m_item;
    }

    @Override
    public boolean isSelected(PageState state) {
        return m_item.isSelected(state);
    }

    public Object getSelectedKey(PageState state) {
        Survey item = (Survey) m_item.getSelectedObject(state);
        return item.getForm().getID();
    }

    public void setSelectedKey(PageState state, Object key) {
        throw new RuntimeException((String) GlobalizationUtil.globalize("cms.contenttypes.ui.survey.oh_no_you_dont").localize());
    }

    @Override
    public void clearSelection(PageState state) {
        throw new RuntimeException((String) GlobalizationUtil.globalize("cms.contenttypes.ui.survey.oh_no_you_dont").localize());
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        throw new RuntimeException((String) GlobalizationUtil.globalize("cms.contenttypes.ui.survey.oh_no_you_dont").localize());
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        throw new RuntimeException((String) GlobalizationUtil.globalize("cms.contenttypes.ui.survey.oh_no_you_dont").localize());
    }

    public ParameterModel getStateParameter() {
        throw new RuntimeException((String) GlobalizationUtil.globalize("cms.contenttypes.ui.survey.oh_no_you_dont").localize());
    }
}
