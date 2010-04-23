/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui.editors;

/**
 *
 * @author quasi
 */
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.contenttypes.PersistentScale;
import com.arsdigita.formbuilder.PersistentOptionGroup;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.ui.PropertiesEditor;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;

public class ScaleEditor extends PropertiesEditor {

    private Form m_editor;
    private Form m_buttons;
    private Submit m_props;
    private Submit m_done;
    private SingleSelectionModel m_control;
    private SingleSelectionModel m_form;

    public ScaleEditor(String name,
            SingleSelectionModel form,
            SingleSelectionModel control) {

        super(new ScaleForm(name, form, control));


        super.addProcessListener(
                new FormProcessListener() {

                    public void process(FormSectionEvent e)
                            throws FormProcessException {
                        PageState state = e.getPageState();

                        getPropertiesForm().setVisible(state, false);
                        m_editor.setVisible(state, true);
                        m_buttons.setVisible(state, true);
                    }
                });

        m_form = form;
        m_control = control;

        m_editor = new Form("option_editor", new BoxPanel(BoxPanel.VERTICAL));
        m_editor.add(new ScaleOptionEditor(control) {
            @Override
            protected PersistentOptionGroup getOptionGroup(BigDecimal id)
                    throws DataObjectNotFoundException {
                return new PersistentScale(id).getOptionList();
            }
        });
        add(m_editor);

        m_props = new Submit("props", "Properties");
        m_done = new Submit("done", "Done");

        m_buttons = new Form("buttons");
        m_buttons.add(m_props);
        m_buttons.add(m_done);
        add(m_buttons);

    }

    @Override
    public void generateXML(PageState state, Element parent) {
        if (m_control.getSelectedKey(state) == null
                || m_props.isSelected(state)) {
            getPropertiesForm().setVisible(state, true);
            m_buttons.setVisible(state, false);
            m_editor.setVisible(state, false);


        } else {
            getPropertiesForm().setVisible(state, false);
            m_buttons.setVisible(state, true);
            m_editor.setVisible(state, true);


        }

        super.generateXML(state, parent);


    }

    @Override
    public void addProcessListener(FormProcessListener l) {
        super.addProcessListener(l);

        m_buttons.addProcessListener(new PropertiesFormProcessListener(l));


    }

    @Override
    public boolean isComplete(PageState state) {
        return m_done.isSelected(state);

    }
}
