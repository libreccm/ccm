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
package com.arsdigita.cms.contentassets;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;

import org.apache.log4j.Logger;

/**
 *  Configuration object for the DublinCore asset.
 */
public class DublinCoreConfig extends AbstractConfig {

    /** A logger instance to assist debugging.  */
    private static final Logger s_log = Logger.getLogger(DublinCoreConfig.class);
    /** Singelton config object.  */
    private static DublinCoreConfig s_conf;

    /**
     * Gain a DublinCoreConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized DublinCoreConfig instanceOf() {
        if (s_conf == null) {
            s_conf = new DublinCoreConfig();
            s_conf.load();
        }

        return s_conf;
    }

    // ///////////////////////////////////////////////////////////////////////
    //
    // set of configuration parameters
    /** Default Audience Domain Key preset in the authoring step ui           */
    private Parameter m_audience = new StringParameter(
            "com.arsdigita.cms.contentassets.dublincore.audience_domain",
            Parameter.OPTIONAL,
            null);
    /** Default Coverage Domain Key preset in the authoring step ui           */
    private Parameter m_coverageSpatial = new StringParameter(
            "com.arsdigita.cms.contentassets.dublincore.coverage_spatial_domain",
            Parameter.OPTIONAL,
            null);
    /** Default Units Domain Key preset in the authoring step ui              */
    private Parameter m_coverageUnit = new StringParameter(
            "com.arsdigita.cms.contentassets.dublincore.coverage_units_domain",
            Parameter.OPTIONAL,
            null);
    /** Default Default Owner preset in the authoring step ui                 */
    private Parameter m_owner = new StringParameter(
            "com.arsdigita.cms.contentassets.dublincore.owner_default",
            Parameter.OPTIONAL,
            null);
    /** Default Default Owner Contact preset in the authoring step ui         */
    private Parameter m_owner_contact = new StringParameter(
            "com.arsdigita.cms.contentassets.dublincore.owner_contact_default",
            Parameter.OPTIONAL,
            null);
    /** Default Rights string preset in the authoring step ui                 */
    private Parameter m_rights = new StringParameter(
            "com.arsdigita.cms.contentassets.dublincore.rights_default",
            Parameter.OPTIONAL,
            null);
    /** Default Publisher string preset in the authoring step ui              */
    private Parameter m_publisher = new StringParameter(
            "com.arsdigita.cms.contentassets.dublincore.publisher_default",
            Parameter.OPTIONAL,
            null);
    /** Default value wether to metadata should include CCN.PortalInclude     */
    private Parameter m_use_ccn_portal = new BooleanParameter(
            "com.arsdigita.cms.contentassets.dublincore.use_ccn_portal_default",
            Parameter.OPTIONAL,
            Boolean.FALSE);
    /** Default Related Items subject domain  preset in the authoring step ui */
    private Parameter m_relatedItemsSubjectDomain = new StringParameter(
            "com.arsdigita.cms.contentassets.dublincore.related_items_subject_domain",
            Parameter.OPTIONAL,
            null);
    private Parameter m_assetStepSortKey =
                      new IntegerParameter(
            "com.arsdigita.cms.contentassets.dublincore.asset_step_sortkey",
            Parameter.OPTIONAL,
            3);

    /**
     * Constructor just registers and loads the parameter.
     */
    public DublinCoreConfig() {

        register(m_audience);
        register(m_coverageSpatial);
        register(m_coverageUnit);
        register(m_owner);
        register(m_owner_contact);
        register(m_rights);
        register(m_publisher);
        register(m_use_ccn_portal);
        register(m_relatedItemsSubjectDomain);
        register(m_assetStepSortKey);

        loadInfo();
    }

    /**
     * 
     * @return 
     */
    public String getAudienceDomain() {
        return (String) get(m_audience);
    }

    public String getCoverageSpatialDomain() {
        return (String) get(m_coverageSpatial);
    }

    public String getCoverageUnitDomain() {
        return (String) get(m_coverageUnit);
    }

    public String getOwnerDefault() {
        return (String) get(m_owner);
    }

    public String getRightsDefault() {
        return (String) get(m_rights);
    }

    public String getPublisherDefault() {
        return (String) get(m_publisher);
    }

    public boolean getUseCCNPortalMetadata() {
        return ((Boolean) get(m_use_ccn_portal)).booleanValue();
    }

    public String getRelatedItemsSubjectDomain() {
        return (String) get(m_relatedItemsSubjectDomain);
    }

    public String getOwnerContactDefault() {
        return (String) get(m_owner_contact);
    }
    
    public Integer getAssetStepSortKey() {
        return (Integer) get(m_assetStepSortKey);
    }

    // Only for test suites
    void setRelatedItemsSubjectDomain(String domain) {
        set(m_relatedItemsSubjectDomain, domain);
    }

}
