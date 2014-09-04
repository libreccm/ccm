/*
 * Copyright (C) 2008 Permeance Technologies Ptd Ltd. All Rights Reserved.
 *
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
 */
package com.arsdigita.portalworkspace.ui.portlet;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.HTMLColourCodeValidationListener;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.portal.PortletConfigFormSection;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.portalworkspace.portlet.FlashPortlet;
import com.arsdigita.portal.Portlet;

/**
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class FlashPortletEditor extends PortletConfigFormSection {

    private TextField m_backgroundColour;

    private TextField m_detectKey;

    private TextField m_file;

    private TextField m_height;

    private TextArea m_parameters;

    private SingleSelect m_quality;

    private TextField m_redirectUrl;

    private TextArea m_variables;

    private TextField m_version;

    private TextField m_width;

    private TextField m_xiRedirectUrl;

    public FlashPortletEditor(ResourceType resType, RequestLocal parentAppRL) {
        super(resType, parentAppRL);
    }

    public FlashPortletEditor(RequestLocal application) {
        super(application);
    }

    protected void addWidgets() {
        super.addWidgets();

        m_file = this.addTextField(FlashPortlet.SWF_FILE, "SWF file", 64, 2048, true);
        m_width = this.addTextField(FlashPortlet.WIDTH, "Width", 8, 8, true);
        m_height = this.addTextField(FlashPortlet.HEIGHT, "Height", 8, 8, true);
        m_version = this.addTextField(FlashPortlet.VERSION, "Minimum Flash version", 8, 8, true);
        m_backgroundColour = this.addTextField(FlashPortlet.BACKGROUND_COLOUR, "Background colour",
                                               7, 7, true);
        m_backgroundColour.addValidationListener(new HTMLColourCodeValidationListener());

        m_quality = this.addSingleSelect(FlashPortlet.QUALITY, "Quality", false);
        m_quality.addOption(new Option("low"));
        m_quality.addOption(new Option("autolow"));
        m_quality.addOption(new Option("autohigh"));
        m_quality.addOption(new Option("medium"));
        m_quality.addOption(new Option("high"));
        m_quality.addOption(new Option("best"));

        m_parameters = this.addTextArea(FlashPortlet.PARAMETERS, "Parameters", 4, 64, false);
        m_variables = this.addTextArea(FlashPortlet.VARIABLES, "Variables", 4, 64, false);

        m_detectKey = this.addTextField(FlashPortlet.DETECT_KEY, "Detect key", 32, 32, false);
        m_redirectUrl = this.
                addTextField(FlashPortlet.REDIRECT_URL, "Redirect URL", 64, 2048, false);
        m_xiRedirectUrl = this.addTextField(FlashPortlet.XI_REDIRECT_URL, "XI Redirect URL", 64,
                                            2048, false);

        add(new Link("What do these fields mean?", "http://blog.deconcept.com/swfobject/"),
            ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);
    }

    protected void initWidgets(PageState state, Portlet portlet) throws FormProcessException {
        super.initWidgets(state, portlet);

        if (portlet != null) {
            FlashPortlet flashPortlet = (FlashPortlet) portlet;

            // Load the form from the portlet
            m_backgroundColour.setValue(state, flashPortlet.getBackgroundColour());
            m_detectKey.setValue(state, flashPortlet.getDetectKey());
            m_file.setValue(state, flashPortlet.getFile());
            m_height.setValue(state, flashPortlet.getHeight());
            m_parameters.setValue(state, flashPortlet.getParameters());

            m_redirectUrl.setValue(state, flashPortlet.getRedirectUrl());
            m_variables.setValue(state, flashPortlet.getVariables());
            m_version.setValue(state, flashPortlet.getVersion());
            m_width.setValue(state, flashPortlet.getWidth());
            m_xiRedirectUrl.setValue(state, flashPortlet.getXiRedirectUrl());
            m_quality.setValue(state, flashPortlet.getQuality());
        } else {
            // Set defaults
            m_backgroundColour.setValue(state, "#ffffff");
            m_file.setValue(state,
                            "http://www.adobe.com/support/flashplayer/ts/documents/tn_15507/flashplayerversion.swf");
            m_width.setValue(state, "100%");
            m_height.setValue(state, "100%");
            m_version.setValue(state, "8.0");
            m_quality.setValue(state, "high");
            m_parameters.setValue(state, "mode=transparent\nplay=false\nloop=false\n");
        }
    }

    protected void processWidgets(PageState state, Portlet portlet) throws FormProcessException {
        super.processWidgets(state, portlet);

        FlashPortlet flashPortlet = (FlashPortlet) portlet;

        flashPortlet.setBackgroundColour((String) m_backgroundColour.getValue(state));
        flashPortlet.setDetectKey((String) m_detectKey.getValue(state));
        flashPortlet.setFile((String) m_file.getValue(state));
        flashPortlet.setHeight((String) m_height.getValue(state));
        flashPortlet.setParameters((String) m_parameters.getValue(state));
        flashPortlet.setQuality((String) m_quality.getValue(state));
        flashPortlet.setRedirectUrl((String) m_redirectUrl.getValue(state));
        flashPortlet.setVariables((String) m_variables.getValue(state));
        flashPortlet.setVersion((String) m_version.getValue(state));
        flashPortlet.setWidth((String) m_width.getValue(state));
        flashPortlet.setXiRedirectUrl((String) m_xiRedirectUrl.getValue(state));
    }

    /**
     * Add a new text field.
     *
     * @param name the name of the parameter
     * @param label the label to be displayed
     * @param size the visible size of the text field
     * @param maxLength the maximum length of text that can be entered
     * @param mandatory denotes whether this text field requires a value
     * @return the text field
     */
    private TextField addTextField(String name, String label, int size, int maxLength,
                                   boolean mandatory) {
        TextField field = new TextField(new StringParameter(name));
        field.setSize(size);
        field.setMaxLength(maxLength);

        // Add validation
        if (mandatory) {
            field.addValidationListener(new NotEmptyValidationListener());
        }

        // Add to the form
        add(mandatory ? new Label(label + ":", Label.BOLD) : new Label(label + ":"),
            ColumnPanel.RIGHT);
        add(field);
        return field;
    }

    /**
     * Add a new text area.
     *
     * @param name the name of the parameter
     * @param label the label to be displayed
     * @param rows the visible rows
     * @param cols the visible cols
     * @param mandatory denotes whether this text field requires a value
     * @return the text area
     */
    private TextArea addTextArea(String name, String label, int rows, int cols, boolean mandatory) {
        TextArea field = new TextArea(new StringParameter(name), rows, cols, TextArea.SOFT);

        // Add validation
        if (mandatory) {
            field.addValidationListener(new NotEmptyValidationListener());
        }

        // Add to the form
        add(mandatory ? new Label(label + ":", Label.BOLD) : new Label(label + ":"),
            ColumnPanel.RIGHT);
        add(field);
        return field;
    }

    /**
     * Add a new single select.
     *
     * @param name the name of the parameter
     * @param label the label to be displayed
     * @param mandatory denotes whether this text field requires a value
     * @return the text area
     */
    private SingleSelect addSingleSelect(String name, String label, boolean mandatory) {
        SingleSelect field = new SingleSelect(new StringParameter(name));

        // Add validation
        if (mandatory) {
            field.addValidationListener(new NotEmptyValidationListener());
        }

        // Add to the form
        add(mandatory ? new Label(label + ":", Label.BOLD) : new Label(label + ":"),
            ColumnPanel.RIGHT);
        add(field);
        return field;
    }
}
