/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.DateTime;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.DateTimeParameter;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.contentassets.DublinCoreES;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.london.terms.Domain;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 
 * 
 */
public abstract class DublinCoreFormSection extends FormSection
                                            implements FormInitListener, 
                                                       FormProcessListener {

    /** A logger instance to assist debugging.                                */
    private static final Logger s_log = 
                                Logger.getLogger(DublinCoreFormSection.class);


    // private TextField m_contributor;  //nevertheless, contrib is a db field

    // Originally coverage(Spatial) is a controlled list, therefore Widget here
    private Widget m_coverage;

    private TextArea m_creator; //default value pulled in frim config, edited
                                // value persisted in database

    private TextField m_dateValid;

    private TextArea m_description;

    // private TextField m_format;
    // private TextField m_identifier;
    // private TextField m_language;
    private TextArea m_publisher;

    /* Relation: Related resource. Recommended to identify by a string 
     * conforming to a formal identification system.                          */ 
    // private TextField m_relation;
    /** Rights: Information about rights held in and over the resource.
     *  Statement about various property rights.                              */
    private TextArea m_rights;

    /* Related resource from which the resource is derived. Recommended to 
     * identify by a string conforming to a formal identification system.     */
    // private TextField m_source;

    /** Topic of the resource, typically represented by keywords or 
     *  classiofication codes.                                                */
    // private TextField m_subject;
    private TextField m_keywords;

    /* Given name, not persisted, retrieved from associated content item.     */
    // private TextField m_title;

    /* * Nature or genre, recommended to use a controlled vocabulary          */
    // private TextField m_type;

    /** Flag whether the discription item should be editable after retrieving
     *  its value from the associated content item description (abstract)     */
    private boolean editableDescription;
    
    private Submit m_cancel;


    /**
     * Constructor creates the form elements.
     * 
     * @param editableDescription whether the discription item should be editable
     */
    public DublinCoreFormSection(boolean editableDescription) {
        this.editableDescription = editableDescription;


        m_dateValid = new TextField(new StringParameter("dateValid"));
        m_dateValid.addValidationListener(new StringLengthValidationListener(
                100));

        m_creator = new TextArea(new StringParameter("creatorOwner"));
        m_creator.addValidationListener(new 
                       StringLengthValidationListener(300));
        m_creator.setCols(50);
        m_creator.setRows(3);

        m_description = new TextArea(new StringParameter("description"));
        if (editableDescription) {
            m_description.addValidationListener(
                          new StringLengthValidationListener(4000));
            m_description.setCols(50);
            m_description.setRows(10);
            
        } else {
            m_description.setReadOnly(); 
        }

        m_publisher = new TextArea(new StringParameter("publisher"));
        m_publisher.addValidationListener(new 
                    StringLengthValidationListener(4000));
        m_publisher.setCols(50);
        m_publisher.setRows(5);

        m_rights = new TextArea(new StringParameter("rights"));
        m_rights.addValidationListener(new StringLengthValidationListener(4000));
        m_rights.setCols(50);
        m_rights.setRows(10);

        m_keywords = new TextField(new TrimmedStringParameter("keywords"));
        m_keywords.addValidationListener(new StringLengthValidationListener(
                                         4000));
        m_keywords.addValidationListener(new DublinCoreKeywordsValidationListener());
        m_keywords.setHint("Enter a list of keywords, separated with commas");
        m_keywords.setSize(50);



        add(new Label("Coverage:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_coverage);



        add(new Label("Date (valid):", Label.BOLD), ColumnPanel.RIGHT);
        add(m_dateValid);

        add(new Label("Creator (owner):", Label.BOLD), ColumnPanel.RIGHT);
        add(m_creator);

        add(new Label("Description:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_description);

        add(new Label("Publisher:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_publisher);

        add(new Label("Rights:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_rights);

        add(new Label("Keywords:", Label.BOLD), ColumnPanel.RIGHT);
        add(m_keywords);


        SaveCancelSection saveCancel = new SaveCancelSection();
        m_cancel = saveCancel.getCancelButton();
        add(saveCancel, ColumnPanel.FULL_WIDTH);

        addInitListener(this);
        addProcessListener(this);
    }

    /**
     * 
     * @return 
     */
    public Submit getCancelButton() {
        return m_cancel;
    }
    
    /**
     * 
     * @param name
     * @param key
     * @return 
     */
    protected Widget createControlledList(String name, String key) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Creating controlled list with " + name + " key "
                        + key);
        }
        Domain domain = null;
        if (key != null) {
            try {
                domain = Domain.retrieve(key);
            } catch (DataObjectNotFoundException ex) {
                s_log.warn("Cannot find controlled list key " + key
                           + " for field " + name);
                // nada
            }
        }

        if (domain == null) {
            TextField widget = new TextField(name);
            widget.setSize(40);
            return widget;
        } else {
            DublinCoreControlledList widget = new DublinCoreControlledList(name, domain);
            return widget;
        }
    }

    /**
     * 
     * @param fse
     * @throws FormProcessException 
     */
    public void init(FormSectionEvent fse) throws FormProcessException {

        PageState state = fse.getPageState();
        ContentItem item = getSelectedItem(state);
        if (item == null) {
            return;
        }

        DublinCoreES dcItem = DublinCoreES.findByOwner(item);
        if (dcItem == null) {
            m_creator.setValue(state, DublinCoreES.getConfig()
                    .getOwnerDefault());
            m_publisher.setValue(state, DublinCoreES.getConfig()
                    .getPublisherDefault());
            m_rights.setValue(state, DublinCoreES.getConfig()
                    .getRightsDefault());
            m_description.setValue(state, getInitialDescription(item));
            return;
        }

        m_coverage.setValue(state, dcItem.getCoverage());

        m_creator.setValue(state,
                           dcItem.getCreator() == null ?  
                               DublinCoreES.getConfig().getOwnerDefault() : 
                               dcItem.getCreator()
                          );

        m_publisher.setValue(state,
                dcItem.getPublisher() == null ? DublinCoreES.getConfig()
                        .getPublisherDefault() : dcItem.getPublisher());
        m_rights.setValue(state, dcItem.getRights() == null ? DublinCoreES
                .getConfig().getRightsDefault() : dcItem.getRights());
   //   m_keywords.setValue(state, dcItem.getKeywords());
        if (editableDescription) {
            m_description.setValue(state, dcItem.getDescription());
        } else {
            m_description.setValue(state, getInitialDescription(item));
        }
   //   if (DublinCoreES.getConfig().getUseCCNPortalMetadata()) {
   //       m_ccn_portal_instance
   //               .setValue(state, dcItem.getCcnPortalInstance());
   //   }
    }

    /**
     * 
     * @param fse
     * @throws FormProcessException 
     */
    public void process(FormSectionEvent fse) throws FormProcessException {

        PageState state = fse.getPageState();
        ContentItem item = getSelectedItem(state);
        if (item == null) {
            return;
        }
        DublinCoreES dcItem = DublinCoreES.findByOwner(item);
        if (dcItem == null) {
            dcItem = DublinCoreES.create(item);
        }


        dcItem.setCoverage((String) m_coverage.getValue(state));
        dcItem.setDate((String) m_dateValid.getValue(state));

        dcItem.setCreator((String) m_creator.getValue(state));

        dcItem.setPublisher((String) m_publisher.getValue(state));
        dcItem.setRights((String) m_rights.getValue(state));

        // dcItem.setKeywords((String) m_keywords.getValue(state));

        if (editableDescription) {
            saveDescription((String) m_description.getValue(state), item, dcItem);
        }


        dcItem.save();
    }

    /**
     * 
     * @param state
     * @return 
     */
    protected abstract ContentItem getSelectedItem(PageState state);
    
    /**
     * 
     * @param item
     * @return 
     */
    protected String getInitialDescription(ContentItem item) {
        return "";
    }
    
    /**
     * 
     * @param description
     * @param item
     * @param dcItem 
     */
    protected void saveDescription(String description, ContentItem item, DublinCoreES dcItem ) {
        dcItem.setDescription(description);
    }

}
