/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.cms.dublin;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.Assert;
import com.arsdigita.kernel.ACSObject;
import java.util.Date;
import org.apache.log4j.Logger;

/***
 *
 * DublinCoreItem
 *
 * @author slater@arsdigita.com
 * @version $Id: DublinCoreItem.java 1111 2006-04-18 13:57:35Z apevec $
 *
 * This object provides DublinCore functionality
 *
 * There are a bunch of scalar fields
 * The only non-scalar field is "Subject", which we handle as if
 * it were a category.
 *
 * Here is a database of all the fields handled
 * (from http://dublincore.org/documents/dces/)
 *
 * Contributor
 * Coverage
 * CreationDate
 * Creator
 * Description
 * Format
 * Identifier
 * Language
 * Publisher
 * Relation
 * Rights
 * Source
 * Subject
 * Title
 * Type
 *
 *
 **/
public class DublinCoreItem extends ContentItem {

    private static final Logger logger = Logger.getLogger(DublinCoreItem.class);
    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.london.cms.dublin.DublinCoreItem";
    
    private static final DublinCoreConfig s_config = new DublinCoreConfig();
    static {
        logger.debug("Static initalizer starting...");
        s_config.load();
        logger.debug("Static initalizer finished.");
    }
    
    public static final DublinCoreConfig getConfig() {
        return s_config;
    }

    // DataObject field constants

    /* Constant for the content item this dc object is owned by (associated with) */
    public static final String DCMI_OWNER = "dcOwner";

    public static final String DC_AUDIENCE = "dcAudience";
    public static final String DC_CONTRIBUTOR = "dcContributor";

    public static final String DC_COVERAGE = "dcCoverage";
    public static final String DC_COVERAGE_POSTCODE = "dcCoveragePostcode";
    public static final String DC_COVERAGE_SPATIAL_REF = "dcCoverageSpatialRef";
    public static final String DC_COVERAGE_UNIT = "dcCoverageUnit";
    public static final String DC_TEMPORAL_BEGIN = "dcTemporalBegin";
    public static final String DC_TEMPORAL_END = "dcTemporalEnd";

    public static final String DC_CREATOR_OWNER = "dcCreatorOwner";
    public static final String DC_CREATOR_CONTACT = "dcCreatorContact";

    public static final String DC_DESCRIPTION = "dcDescription";
    public static final String DC_IDENTIFIER = "dcIdentifier";

    public static final String DC_PRESERVATION = "dcPreservation";
    public static final String DC_PUBLISHER = "dcPublisher";
    public static final String DC_RELATION = "dcRelation";
    public static final String DC_RIGHTS = "dcRights";
    public static final String DC_SOURCE = "dcSource";

    public static final String DC_CCN_PORTAL_INSTANCE = "dcCcnPortalInstance";

    public static final String DC_DATE_VALID = "dcDateValid";
    public static final String DC_DISPOSAL_REVIEW = "dcDisposalReview";
    public static final String DC_LANGUAGE = "dcLanguage";

    public static final String DC_KEYWORDS = "dcKeywords";
    
    // Constructors

    /**
     * Default constructor. This creates a new content page.
     **/
    protected DublinCoreItem() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public DublinCoreItem(OID oid) {
        super(oid);
    }

    public DublinCoreItem(DataObject obj) {
        super(obj);
    }
    
    public DublinCoreItem(String type) {
        super(type);
    }


    /**
     * Create a new empty Dublin Core meta data object and associate it with
     * the given content item.
     * 
     * @param  owner content item object to associate the new DCES object with
     * @return the new DCMI object, associated with the given content item 
     *         object but otherwise empty.
     */
    public static DublinCoreItem create(ContentItem owner) {
        DublinCoreItem item = new DublinCoreItem();
        item.setOwner(owner);
        item.setName(owner.getName() + "-dublin-metadata");
        return item;
    }
    
    
    /**
     * Retrieve a Dublin Core meta data object by its owner (a content item)
     * from the database and instantiate a new Dublin Core object.
     * 
     * @param owner the content item object for which we look for meta data 
     * @return a DublinCoreItem object
     */
    public static DublinCoreItem findByOwner(ContentItem owner) {
        DataCollection items = SessionManager.getSession()
            .retrieve(BASE_DATA_OBJECT_TYPE);
        items.addEqualsFilter(DCMI_OWNER + "." + ACSObject.ID, 
                              owner.getID());
        
        if (items.next()) {
            DataObject obj = items.getDataObject();
            items.close();
            return (DublinCoreItem)DomainObjectFactory.newInstance(obj);
        }
        return null;
    }

    protected void setOwner(ContentItem owner) {
        setAssociation(DCMI_OWNER, owner);
    }
    
    public ContentItem getOwner() {
        DataObject dobj = (DataObject)get(DCMI_OWNER);
        Assert.exists(dobj, DataObject.class);
        return (ContentItem)DomainObjectFactory.newInstance(dobj);
    } 

    /* Audience */
    public String getAudience() {
        return (String)get(DC_AUDIENCE);
    }

    public void setAudience(String audience) {
        set(DC_AUDIENCE, audience);
    }


    /* Contributor */
    public String getContributor() {
        return (String)get(DC_CONTRIBUTOR);
    }

    public void setContributor(String contributor) {
        set(DC_CONTRIBUTOR, contributor);
    }

    /*Coverage*/
    public String getCoverage() {
        return (String)get(DC_COVERAGE);
    }

    public void setCoverage(String coverage) {
        set(DC_COVERAGE, coverage);
    }
  

    /*Coveragepostcode*/
    public String getCoveragePostcode() {
        return (String)get(DC_COVERAGE_POSTCODE);
    }

    public void setCoveragePostcode(String coveragepostcode) {
        set(DC_COVERAGE_POSTCODE, coveragepostcode);
    }
  

    /*Coveragespatialref*/
    public String getCoverageSpatialRef() {
        return (String)get(DC_COVERAGE_SPATIAL_REF);
    }

    public void setCoverageSpatialRef(String coverageSpatialRef) {
        set(DC_COVERAGE_SPATIAL_REF, coverageSpatialRef);
    }
  

    /*Coverageunit*/
    public String getCoverageUnit() {
        return (String)get(DC_COVERAGE_UNIT);
    }

    public void setCoverageUnit(String coverageunit) {
        set(DC_COVERAGE_UNIT, coverageunit);
    }
  
    /*Temporalbegin*/
    public Date getTemporalBegin() {
        return (Date)get(DC_TEMPORAL_BEGIN);
    }

    public void setTemporalBegin(Date temporalbegin) {
        set(DC_TEMPORAL_BEGIN, temporalbegin);
    }
  

    /*Temporalend*/
    public Date getTemporalEnd() {
        return (Date)get(DC_TEMPORAL_END);
    }

    public void setTemporalEnd(Date temporalend) {
        set(DC_TEMPORAL_END, temporalend);
    }
  

    /*DateValid*/
    public String getDateValid() {
        return (String)get(DC_DATE_VALID);
    }

    public void setDateValid(String dateValid) {
        set(DC_DATE_VALID, dateValid);
    }


    /*Disposal Review*/
    public String getDisposalReview() {
        return (String)get(DC_DISPOSAL_REVIEW);
    }

    public void setDisposalReview(String disposalReview) {
        set(DC_DISPOSAL_REVIEW, disposalReview);
    }


    /*Language*/
    public String getLanguage() {
        return getOwner().getLanguage();
    }

    public void setLanguage(String language) {
        throw new UnsupportedOperationException(
            "Metadata language is no longer set explicitly. " + 
            "Language is pulled from underlying ContentItem");
        //set(DC_LANGUAGE, language);
    }


    /*Creator*/
    public String getCreatorOwner() {
        return (String)get(DC_CREATOR_OWNER);
    }

    public void setCreatorOwner(String creator) {
        set(DC_CREATOR_OWNER, creator);
    }

    /*Creator*/
    public String getCreatorContact() {
        return (String)get(DC_CREATOR_CONTACT);
    }

    public void setCreatorContact(String creator) {
        set(DC_CREATOR_CONTACT, creator);
    }


    /*Description*/
    public String getDescription() {
        return (String)get(DC_DESCRIPTION);
    }

    public void setDescription(String description) {
        set(DC_DESCRIPTION, description);
    }


    /*Identifier*/
    public String getIdentifier() {
        return (String)get(DC_IDENTIFIER);
    }

    public void setIdentifier(String identifier) {
        set(DC_IDENTIFIER, identifier);
    }

    /*Preservation*/
    public String getPreservation() {
        return (String)get(DC_PRESERVATION);
    }

    public void setPreservation(String preservation) {
        set(DC_PRESERVATION, preservation);
    }


    /*Publisher*/
    public String getPublisher() {
        return (String)get(DC_PUBLISHER);
    }

    public void setPublisher(String publisher) {
        set(DC_PUBLISHER, publisher);
    }

    /*CCN Portal Instance */
    public String getCcnPortalInstance() {
        return (String)get(DC_CCN_PORTAL_INSTANCE);
    }

    public void setCcnPortalInstance(String ccnPortalInstance) {
        set(DC_CCN_PORTAL_INSTANCE, ccnPortalInstance);
    }


    /*Relation*/
    public String getRelation() {
        return (String)get(DC_RELATION);
    }

    public void setRelation(String relation) {
        set(DC_RELATION, relation);
    }


    /*Rights*/
    public String getRights() {
        return (String)get(DC_RIGHTS);
    }

    public void setRights(String rights) {
        set(DC_RIGHTS, rights);
    }

    /*Source*/
    public String getSource() {
        return (String)get(DC_SOURCE);
    }

    public void setSource(String source) {
        set(DC_SOURCE, source);
    }


    public void beforeSave() {
        super.beforeSave();
        if (get(DC_LANGUAGE) == null) {
            set(DC_LANGUAGE, getOwner().getLanguage() );
        }
    }
    

    public void setKeywords(String keywords) {
        set(DC_KEYWORDS, keywords);
    }

    public String getKeywords() {
        return (String)get(DC_KEYWORDS);
    }
}
