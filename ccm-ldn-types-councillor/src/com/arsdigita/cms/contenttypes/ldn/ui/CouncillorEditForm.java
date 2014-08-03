/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.contenttypes.ldn.ui;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;

import com.arsdigita.cms.contenttypes.ldn.Councillor;

import org.apache.log4j.Logger;

public class CouncillorEditForm extends PersonEditForm {

    private static final Logger s_log =
        Logger.getLogger( CouncillorEditForm.class );

    private ItemSelectionModel m_itemModel;
    private TextField m_position;
    private TextField m_politicalParty;
    private TextField m_areaOfResponsibility;
    private SingleSelect m_ward;
    private TextField m_termOfOffice;
    private CMSDHTMLEditor m_surgeryDetails;

    private CouncillorPropertiesStep m_step;

    public CouncillorEditForm(ItemSelectionModel itemModel) {
        super("councillorEdit", itemModel);
        m_itemModel = itemModel;
    }

    public CouncillorEditForm(ItemSelectionModel itemModel,
                              CouncillorPropertiesStep step) {
        this(itemModel);

        m_step = step;
        addSubmissionListener( this );
    }

    public void addWidgets() {
        super.addWidgets();
	m_surgeryDetails = new CMSDHTMLEditor(Councillor.SURGERY_DETAILS);
    m_surgeryDetails.setRows( 10 );
	add(new Label("Surgery Details:"));
	add(m_surgeryDetails);

        m_position = new TextField(Councillor.POSITION);
        add(new Label("Position"));
        add(m_position);

        m_politicalParty = new TextField(Councillor.POLITICAL_PARTY);
        add(new Label("Political Party"));
        add(m_politicalParty);

        m_ward = new SingleSelect(Councillor.WARD);
		m_ward.addOption( new Option( "Bablake", "Bablake" ) );
		m_ward.addOption( new Option( "Binley and Willenhall", "Binley and Willenhall" ) );
		m_ward.addOption( new Option( "Cheylesmore", "Cheylesmore" ) );
		m_ward.addOption( new Option( "Earlsdon", "Earlsdon" ) );
		m_ward.addOption( new Option( "Foleshill", "Foleshill" ) );
		m_ward.addOption( new Option( "Henley", "Henley" ) );
		m_ward.addOption( new Option( "Holbrook", "Holbrook" ) );
		m_ward.addOption( new Option( "Longford", "Longford" ) );
		m_ward.addOption( new Option( "Lower Stoke", "Lower Stoke" ) );
		m_ward.addOption( new Option( "Radford", "Radford" ) );
		m_ward.addOption( new Option( "Sherbourne", "Sherbourne" ) );
		m_ward.addOption( new Option( "St Michael's", "St Michael's" ) );
		m_ward.addOption( new Option( "Upper Stoke", "Upper Stoke" ) );
		m_ward.addOption( new Option( "Wainbody", "Wainbody" ) );
		m_ward.addOption( new Option( "Westwood", "Westwood" ) );
		m_ward.addOption( new Option( "Whoberley", "Whoberley" ) );
		m_ward.addOption( new Option( "Woodlands", "Woodlands" ) );
		m_ward.addOption( new Option( "Wyken", "Wyken" ) );
        add(new Label("Ward"));
        add(m_ward);

        m_areaOfResponsibility
            = new TextField(Councillor.AREA_OF_RESPONSIBILITY);

        add(new Label("Area of Responsibility"));
        add(m_areaOfResponsibility);

        m_termOfOffice = new TextField(Councillor.TERM_OF_OFFICE);
        add(new Label("Term of Office"));
        add(m_termOfOffice);

    }

    public void initCouncillorEdit(FormSectionEvent event) {
        PageState state = event.getPageState();

        super.initPersonEdit(event);
        if (m_itemModel.isSelected(state)) {
            Councillor councillor
                = (Councillor)m_itemModel.getSelectedObject(state);
            m_position.setValue(state, councillor.getPosition());
            m_politicalParty.setValue(state, councillor.getPoliticalParty());
            m_ward.setValue(state, councillor.getWard());
            m_termOfOffice.setValue(state, councillor.getTermOfOffice());
            m_areaOfResponsibility.setValue(state,
                                             councillor.getAreaOfResponsibility());
	    m_surgeryDetails.setValue(state, councillor.getSurgeryDetails());
        }
    }

    public void init(FormSectionEvent event) throws FormProcessException {
        initCouncillorEdit(event);
    }

    /** Cancels streamlined editing. */
    public void submitted( FormSectionEvent fse ) {
        if (m_step != null &&
            getSaveCancelSection().getCancelButton()
            .isSelected( fse.getPageState())) {
            m_step.cancelStreamlinedCreation(fse.getPageState());
        }
    }

    public Councillor processCouncillorEdit(FormSectionEvent event) {
        if( s_log.isDebugEnabled() ) {
            ACSObjectSelectionModel objectModel =
                (ACSObjectSelectionModel) m_itemModel;

            Class javaClass = objectModel.getJavaClass();
            String objectType = objectModel.getObjectType();

            s_log.debug( "Starting process Councillor with model using: " +
                         javaClass.getName() + " " + objectType );
        }

        PageState state = event.getPageState();
        Councillor councillor = null;

        councillor = (Councillor)super.processPersonEdit(event);
        if (councillor != null
            && getSaveCancelSection().getSaveButton()
            .isSelected(state)) {

            councillor.setPosition((String)m_position.getValue(state));
            councillor.setPoliticalParty((String)m_politicalParty.getValue(state));
            councillor.setWard((String)m_ward.getValue(state));
            councillor.setTermOfOffice((String)m_termOfOffice.getValue(state));
            councillor.setAreaOfResponsibility(
                (String)m_areaOfResponsibility.getValue(state));
            councillor.setSurgeryDetails((String)m_surgeryDetails.getValue(state));

            if( null != m_step )
                m_step.maybeForwardToNextStep(event.getPageState());
        }

        s_log.debug( "Finished process Councillor" );

        return councillor;
    }

    public void process(FormSectionEvent event) throws FormProcessException {
        processCouncillorEdit(event);
    }
}
