/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Revision:
 * Method : addWidgets()
 * Description : Message Label added for textarea dimensions.
 *               NullValidationListener disabled for width and height of the control to make the attributes
 *              optional.
 * Method : processWidgets()
 * Description : Set the default value of width and height as 0 which would be replaced by the XSL ont the frontend
 */

package com.arsdigita.formbuilder.ui.editors;

import com.arsdigita.formbuilder.util.GlobalizationUtil ; 

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.NumberInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.formbuilder.PersistentTextArea;
import com.arsdigita.formbuilder.PersistentWidget;

import java.math.BigDecimal;

public class TextAreaForm extends WidgetLabelForm {
    private TextField m_width;
    private TextField m_height;
    private TextArea m_value;

    public TextAreaForm(String name,
                        SingleSelectionModel form,
                        SingleSelectionModel control) {
        super(name, form, control);
    }

    protected void addWidgets(FormSection section) {
        super.addWidgets(section);

        m_width = new TextField(new IntegerParameter("width"));
        m_width.setSize(5);
        //m_width.addValidationListener(new NotNullValidationListener());
        //m_width.addValidationListener(new NumberInRangeValidationListener(1, 70));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.textareamsg")), ColumnPanel.RIGHT);
        section.add(new Label(""));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.width")), ColumnPanel.RIGHT);
        section.add(m_width);

        m_height = new TextField(new IntegerParameter("height"));
        m_height.setSize(5);
        //m_height.addValidationListener(new NotNullValidationListener());
        m_height.addValidationListener(new NumberInRangeValidationListener(1, 70));
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.height")), ColumnPanel.RIGHT);
        section.add(m_height);

        m_value = new TextArea(new StringParameter("value"));
        m_value.setCols(50);
        m_value.setRows(20);
        section.add(new Label(GlobalizationUtil.globalize("formbuilder.ui.editors.default_value")), ColumnPanel.RIGHT);
        section.add(m_value);
    }

    protected PersistentWidget getWidget() {
        return new PersistentTextArea();
    }

    protected PersistentWidget getWidget(BigDecimal id)
        throws DataObjectNotFoundException {

        return new PersistentTextArea(id);
    }

    protected void initWidgets(FormSectionEvent e,
                               PersistentWidget w)
        throws FormProcessException {
        super.initWidgets(e, w);

        PersistentTextArea widget = (PersistentTextArea)w;

        PageState state = e.getPageState();

        if (widget == null) {
            m_width.setValue(state, "");
            m_height.setValue(state, "");
            m_value.setValue(state, "");
        } 
        else {
            if(widget.getCols()==0) {
                m_width.setValue(state, "");
            }
            else{
                m_width.setValue(state, new Integer(widget.getCols()));
            }
            if(widget.getRows()==0) {
                m_height.setValue(state, "");
            }
            else{
                m_height.setValue(state, new Integer(widget.getRows()));
            }           
            m_value.setValue(state, widget.getDefaultValue());
        }
    }

    protected void processWidgets(FormSectionEvent e,
                                  PersistentWidget w)
        throws FormProcessException {
        super.processWidgets(e, w);
        //set the default width and height to 0
        PersistentTextArea widget = (PersistentTextArea)w;

        FormData data = e.getFormData();
        Integer width = new Integer(0);
        Integer height = new Integer(0);
        if(data.get("width")!=null) {
            width = (Integer)data.get("width");            
        }
        if(data.get("height")!=null) {
            height = (Integer)data.get("height");            
        }
        
        String value = (String)data.get("value");
        widget.setCols(width.intValue());
        widget.setRows(height.intValue());        
        widget.setDefaultValue(value);
    }
}
