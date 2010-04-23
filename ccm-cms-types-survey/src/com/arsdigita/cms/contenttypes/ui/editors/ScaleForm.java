/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui.editors;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.cms.contenttypes.PersistentScale;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentWidget;
import com.arsdigita.formbuilder.ui.editors.WidgetForm;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;

/**
 *
 * @author quasi
 */
public class ScaleForm extends WidgetForm {

    private OptionGroup m_optionGroup;
    private Label m_optionGroupLabel;
    private SingleSelectionModel m_control;
    private RequestLocal m_scale = new RequestLocal();

    public ScaleForm(String name,
            SingleSelectionModel form,
            SingleSelectionModel control) {
        super(name, form, control);

        m_control = control;
    }


    protected PersistentWidget getWidget() {
        return new PersistentScale();
    }

    protected PersistentWidget getWidget(BigDecimal id)
            throws DataObjectNotFoundException {
        return new PersistentScale(id);
    }

    @Override
    protected void addWidgets(FormSection section) {
        super.addWidgets(section);
    }

    @Override
    public void generateXML(PageState state, Element parent) {
        super.generateXML(state, parent);
    }

    @Override
    protected void initWidgets(FormSectionEvent event, PersistentWidget widget)
            throws FormProcessException {
        super.initWidgets(event, widget);
    }

    @Override
    protected void processWidgets(FormSectionEvent event,
            PersistentWidget widget)
            throws FormProcessException {
        super.processWidgets(event, widget);
    }

}
