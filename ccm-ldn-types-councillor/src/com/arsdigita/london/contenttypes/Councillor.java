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

package com.arsdigita.london.contenttypes;


import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * This content type represents an event.
 *
 * @version $Revision: #3 $ $Date: 2004/04/08 $
 **/
public class Councillor extends Person {

    private final static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(Councillor.class);

    /** PDL property name for event date */
    public static final String POSITION = "position";
    public static final String POLITICAL_PARTY = "politicalParty";
    public static final String WARD = "ward";
    public static final String AREA_OF_RESPONSIBILITY = "areaOfResponsibility";
    public static final String TERM_OF_OFFICE = "termOfOffice";
    public static final String SURGERY_DETAILS = "surgeryDetails";
    public static final String BASE_DATA_OBJECT_TYPE 
        = "com.arsdigita.coventry.cms.contenttypes.Councillor";

    public Councillor() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public Councillor(OID id) {
        super(id);
    }

    public Councillor(DataObject obj) {
        super(obj);
    }

    public Councillor(String type) {
        super(type);
    }

    // Accessors
    public String getPosition() {
        return (String)get(POSITION);
    }

    public void setPosition(String position) {
        set(POSITION, position);
    }
    
    public String getPoliticalParty() {
        return (String) get(POLITICAL_PARTY);
    }

    public void setPoliticalParty(String politicalParty) {
        set(POLITICAL_PARTY, politicalParty);
    }

    public String getWard() {
        return (String) get(WARD);
    }
    
    public void setWard(String ward) {
        set(WARD, ward);
    }

    public String getAreaOfResponsibility() {
        return (String) get(AREA_OF_RESPONSIBILITY);
    }
    
    public void setAreaOfResponsibility(String areasOfResponsibility) {
        set(AREA_OF_RESPONSIBILITY, areasOfResponsibility);
    }
    
    public String getTermOfOffice() {
        return (String)get(TERM_OF_OFFICE);
    }

    public void setTermOfOffice(String termOfOffice) {
        set(TERM_OF_OFFICE, termOfOffice);
    }

    public String getSurgeryDetails() {
	return (String) get(SURGERY_DETAILS);
    }
    
    public void setSurgeryDetails(String surgeryDetails) {
	set(SURGERY_DETAILS, surgeryDetails);
    }
    
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }
}
