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
 */

package com.arsdigita.london.cms.dublin;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.BooleanParameter;

public class DublinCoreConfig extends AbstractConfig {
    
    private Parameter m_audience;
    private Parameter m_coverageSpatial;
    private Parameter m_coverageUnit;
    private Parameter m_owner;
    private Parameter m_owner_contact;
    private Parameter m_rights;
    private Parameter m_publisher;
    private Parameter m_use_ccn_portal;

    private Parameter m_relatedItemsSubjectDomain;

    public DublinCoreConfig() {
        m_audience = new StringParameter(
            "com.arsdigita.london.cms.dublin.audience_domain",
            Parameter.OPTIONAL,
            null);
        m_coverageSpatial = new StringParameter(
            "com.arsdigita.london.cms.dublin.coverage_spatial_domain",
            Parameter.OPTIONAL,
            null);
        m_coverageUnit = new StringParameter(
            "com.arsdigita.london.cms.dublin.coverage_units_domain",
            Parameter.OPTIONAL,
            null);
        
        m_owner = new StringParameter(
            "com.arsdigita.london.cms.dublin.owner_default",
            Parameter.OPTIONAL,
            null);
        m_rights = new StringParameter(
            "com.arsdigita.london.cms.dublin.rights_default",
            Parameter.OPTIONAL,
            null);
        m_publisher = new StringParameter(
            "com.arsdigita.london.cms.dublin.publisher_default",
            Parameter.OPTIONAL,
            null);
        
        m_use_ccn_portal = new BooleanParameter(
            "com.arsdigita.london.cms.dublin.use_ccn_portal_default",
            Parameter.OPTIONAL,
            Boolean.FALSE);
        
        m_relatedItemsSubjectDomain = new StringParameter(
            "com.arsdigita.london.cms.dublin.related_items_subject_domain",
            Parameter.REQUIRED,
            "LGCL");

	m_owner_contact = new StringParameter(
					      "com.arsdigita.london.cms.dublin.owner_contact_default",
					      Parameter.OPTIONAL,
					      null);
					      
        register(m_audience);
        register(m_coverageSpatial);
        register(m_coverageUnit);
        
        register(m_owner);
	register(m_owner_contact);
        register(m_rights);
        register(m_publisher);
        register(m_use_ccn_portal);

        register(m_relatedItemsSubjectDomain);

        loadInfo();
    }
    
    public String getAudienceDomain() {
        return (String)get(m_audience);
    }
    
    public String getCoverageSpatialDomain() {
        return (String)get(m_coverageSpatial);
    }
    
    public String getCoverageUnitDomain() {
        return (String)get(m_coverageUnit);
    }
    
    public String getOwnerDefault() {
        return (String)get(m_owner);
    }

    public String getRightsDefault() {
        return (String)get(m_rights);
    }

    public String getPublisherDefault() {
        return (String)get(m_publisher);
    }

    public boolean getUseCCNPortalMetadata() {
        return ((Boolean)get(m_use_ccn_portal)).booleanValue();
    }

    public String getRelatedItemsSubjectDomain() {
        return (String)get(m_relatedItemsSubjectDomain);
    }

    public String getOwnerContactDefault() {
	return (String)get(m_owner_contact);
    }

    // Only for test suites
    void setRelatedItemsSubjectDomain(String domain) {
        set(m_relatedItemsSubjectDomain, domain);
    }
}
