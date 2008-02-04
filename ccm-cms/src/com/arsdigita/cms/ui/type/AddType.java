/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.cms.ui.type;


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeLifecycleDefinition;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.TooManyListenersException;


/**
 * This class contains a form component to add a content type
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #15 $ $Date: 2004/08/17 $
 */
public class AddType extends CMSForm
    implements PrintListener, FormProcessListener, FormInitListener {

    public static final String versionId = "$Id: AddType.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    private final static Logger s_log =
        Logger.getLogger(AddType.class.getName());

    protected Hidden m_id;
    protected TextField m_label, m_objectType, m_className;
    protected TextArea m_description;
    protected SingleSelect m_lifecycleSelect;
    protected Submit m_submit;
    protected Submit m_cancel;


    public AddType() {
        super("ContentType");

        m_id = new Hidden(new BigDecimalParameter("id"));
        add(m_id);
        m_id.addValidationListener(new NotNullValidationListener());

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.label")));
        m_label = new TextField(new StringParameter("label"));
        m_label.addValidationListener(new NotNullValidationListener());
        m_label.setSize(40);
        m_label.setMaxLength(1000);
        add(m_label);

        add(new Label(GlobalizationUtil.globalize("cms.ui.description")));
        m_description = new TextArea(new StringParameter("description"));
        m_description.addValidationListener(new StringLengthValidationListener(4000));
        m_description.setCols(40);
        m_description.setRows(5);
        m_description.setWrap(TextArea.SOFT);
        add(m_description);

        // per our ui specs list #35
        // flattop: code put back in.... will be fixed by Xixi
        add(new Label(GlobalizationUtil.globalize("cms.ui.type.object_type")));
        m_objectType = new TextField(new StringParameter("objectType"));
        m_objectType.addValidationListener(new NotNullValidationListener());
        m_objectType.setSize(40);
        m_objectType.setMaxLength(1000);
        add(m_objectType);

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.class_name")));
        m_className = new TextField(new StringParameter("className"));
        m_className.setSize(40);
        m_className.setMaxLength(1000);
        add(m_className);

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.lifecycle")));
        m_lifecycleSelect = new SingleSelect(new BigDecimalParameter("lifecycle"));
        try {
            m_lifecycleSelect.addPrintListener(this);
        } catch (TooManyListenersException e) {
            s_log.warn("Too many listeners", e);
            throw new UncheckedWrapperException(e);
        }
        add(m_lifecycleSelect);

        SimpleContainer s = new SimpleContainer();
        m_submit = new Submit("submit");
        m_submit.setButtonLabel("Save");
        s.add(m_submit);
        m_cancel = new Submit("cancel");
        m_cancel.setButtonLabel("Cancel");
        s.add(m_cancel);
        add(s, ColumnPanel.FULL_WIDTH|ColumnPanel.CENTER);

        addProcessListener(this);
        addInitListener(this);
        addSubmissionListener(new TypeSecurityListener());
    }

    public boolean isCancelled(PageState state) {
        return m_cancel.isSelected(state);
    }

    /**
     * Print listener to generate the select widget for the list of
     * lifecyle definitions
     */
    public void prepare(PrintEvent event) {

        SingleSelect t = (SingleSelect) event.getTarget();

        //get the current content section
        ContentSection section = CMS.getContext().getContentSection();

        t.addOption(new Option("","-- select --"));

        LifecycleDefinitionCollection cycles = section.getLifecycleDefinitions();
        while (cycles.next()) {
            LifecycleDefinition cycle = cycles.getLifecycleDefinition();
            t.addOption(new Option(cycle.getID().toString(), cycle.getLabel()));
        }
    }


    /**
     * Processes the form
     */
    public void process(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        ContentSection section = CMS.getContext().getContentSection();

        FormData data = e.getFormData();
        BigDecimal key = (BigDecimal) data.get(m_id.getName());
        String label = (String) data.get(m_label.getName());
        String description = (String) data.get(m_description.getName());
        String objectType = (String) data.get(m_objectType.getName());
        String className = (String) data.get(m_className.getName());

        BigDecimal lifecycleID = (BigDecimal) data.get(m_lifecycleSelect.getName());

        ContentType contentType;
        boolean isNew = false;

        //check if the object already exists for double click protection
        try {
            contentType = new ContentType(key);
        } catch (DataObjectNotFoundException ex) {
            contentType = new ContentType(SessionManager.getSession().create
                                          (new OID(ContentType.BASE_DATA_OBJECT_TYPE, key)));
            isNew = true;
        }

        contentType.setLabel(label);
        contentType.setDescription(description);
        contentType.setAssociatedObjectType(objectType);
        contentType.setClassName(className);
        contentType.save();

        if (isNew) {
            updateContentTypeAssociation(section, contentType);
        }

        //associated a default lifecycle
        try {
            if (lifecycleID != null) {
                LifecycleDefinition lifecycle = new LifecycleDefinition(lifecycleID);
                ContentTypeLifecycleDefinition.updateLifecycleDefinition(section,
                                                                         contentType, lifecycle);
            } else {
                //remove the association
                ContentTypeLifecycleDefinition.removeLifecycleDefinition(section,
                                                                         contentType);
            }
        } catch (DataObjectNotFoundException ex) {
            //just ignore this since the lifecycle definition does not exist
            // no association
        }

        Utilities.refreshItemUI(state);

    }

    public void init(FormSectionEvent e) {
        FormData data = e.getFormData();
        try {
            if (data.get(m_id.getName()) == null) {
                data.put(m_id.getName(), Sequences.getNextValue());
            }
        } catch (SQLException s) {
            s_log.warn("Error retrieving sequence value", s);
            data.addError("Could not generate Sequence ID " + s.getMessage());
        }
    }

    protected void updateContentTypeAssociation(ContentSection section,
                                                ContentType type) {
        section.addContentType(type);
        section.save();
    }

    public Object getObjectKey(PageState s) {
        BigDecimal id = (BigDecimal) getFormData(s).get(m_id.getName());
        return id;
    }

}
