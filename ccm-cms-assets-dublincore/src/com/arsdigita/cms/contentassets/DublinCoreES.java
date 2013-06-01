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

package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.Assert;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Domain class represents a Dublin Core Element Set (DCES) version 1.1 of core
 * meta data items. For detailend information and a description of each item 
 * see http://dublincore.org/documents/dces/ (version 2012). 
 * 
 * It manages the persistence of the items by providing the getter/setter methods
 * for each. The UI classes, specifically DublinCoreFormSection and 
 * DublinCoreSummary, use it to retrieve and save the information.
 *
 * There are a bunch of scalar fields. The only non-scalar field is "Subject", 
 * which we handle as if it were a category.
 *
 * Here is a database of all the fields handled
 * <dl>
 * <dt>Contributor</dt><dd>
 *     An entity responsible for making <i>contributions</i> to the resource. 
 *     Examples include a person, an organization, or a service. </dd>
 * <dt>Coverage</dt><dd>
 *     The spatial or temporal topic of the resource, the spatial applicability 
 *     of the resource, or the jurisdiction under which the resource is relevant.
 *     Recommended best practice is to use a <i>controlled vocabulary</i> such
 *     as the Thesaurus of Geographic Names [TGN].</dd>
 * <dt>Creator</dt><dd>
 *     An entity primarily responsible for making the resource.Examples of a 
 *     Creator include a person, an organization, or a service. Typically, the 
 *     name of a Creator should be used to indicate the entity.</dd> 
 * <dt>Date</dt><dd>
 *     A point or period of time associated with an event in the lifecycle of 
 *     the resource. Date may be used to express temporal information at any 
 *     level of granularity. Recommended best practice is to use an encoding 
 *     scheme, such as the W3CDTF profile of ISO 8601 [W3CDTF].<br/>
 *     Used here as date of creation, or of (last) (re-)publication. </dd>
 * <dt>Description</dt><dd>
 *     An account of the resource. Description may include but is not limited 
 *     to: an abstract, a table of contents, a graphical representation, or a 
 *     free-text account of the resource.<br />
 *     Automatically pulled from the associated content item  and may be
 *     configured as editible or read only.</dd>
 * <dt>Format</dt><dd>
 *     The file format, physical medium, or dimensions of the resource. Examples 
 *     of dimensions include size and duration. Recommended best practice is to 
 *     use a controlled vocabulary, e.g. list of Internet Media Types [MIME].<br/>
 *     Automatically pulled in from the type of associated content item.</dd>
 * <dt>Identifier</dt><dd>
 *     An unambiguous reference to the resource within a given context. 
 *     Recommended best practice is to identify the resource by means of a 
 *     string conforming to a formal identification system. </dd>
 * <dt>Language</dt><dd>
 *     The language of the resource. Recommended best practice is to use a 
 *     controlled vocabulary such as RFC 4646 [RFC4646].<br />
 *     Automatically pulled in from the associated content type.</dd>
 * <dt>Publisher</dt><dd>
 *     An entity responsible for making the resource available. Examples of a 
 *     Publisher include a person, an organization, or a service. Typically, the 
 *     name of a Publisher should be used to indicate the entity.</dd>
 * <dt>Relation</dt><dd>
 *     A related resource. Recommended best practice is to identify the related 
 *     resource by means of a string conforming to a formal identification 
 *     system. </dd>
 * <dt>Rights</dt><dd>
 *     Information about rights held in and over the resource. Typically, rights 
 *     information includes a statement about various property rights associated 
 *     with the resource, including intellectual property rights.</dd> 
 * <dt>Source</dt><dd>
 *     A related resource from which the described resource is derived. The 
 *     described resource may be derived from the related resource in whole or 
 *     in part. Recommended best practice is to identify the related resource 
 *     by means of a string conforming to a formal identification system.</dd>
 * <dt>Subject</dt><dd>
 *     The topic of the resource. Typically, the subject will be represented 
 *     using <i>keywords, key phrases, or classification codes</i>. Recommended 
 *     best practice is to use a controlled vocabulary.</dd>
 * <dt>Title</dt><dd>
 *     A name given to the resource. Typically, a Title will be a name by which 
 *     the resource is formally known.<br />
 *     NOT PERSISTED in database but retrieved as title (display name) from 
 *     content item.</dd>
 * <dt>Type</dt><dd>
 *     The nature or genre of the resource. Recommended best practice is to use 
 *     a controlled vocabulary such as the DCMI Type Vocabulary [DCMITYPE]. To 
 *     describe the file format, physical medium, or dimensions of the resource, 
 *     use the Format element.</dd>
 * </dl>
 * It may serve as a base for a more complex and larger set of "DCMI Metadata 
 * Terms" whereas all those sets include the base element set items.
 *
 * @author slater@arsdigita.com
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: DublinCoreES.java 1111 2006-04-18 13:57:35Z apevec $
 */
public class DublinCoreES extends ContentItem {

    /** A logger instance to assist debugging.                                */
    private static final Logger logger = Logger.getLogger(DublinCoreES.class);
    /** PDL Stuff - Base object                                               */
    public static final String BASE_DATA_OBJECT_TYPE = 
                               "com.arsdigita.cms.contentassets.DublinCoreES";
    
    /** Config object containing various parameter    */
    private static final DublinCoreConfig s_config = DublinCoreConfig.instanceOf();
        
    /** Provide other classes with the config object                          */
    public static final DublinCoreConfig getConfig() {
        return s_config;
    }

    // ///////////////////////////////////////////////////////////////////////
    //
    // DataObject constants

    /* Constant for Assoziation betweem DCES and content item                 */
    public static final String DCES_OWNER = "dcesOwner";

    /* Constant for Contributor dc item data field                            */
    public static final String DC_CONTRIBUTOR = "dcContributor";
    /* Constant for Coverage dc item data field                               */
    public static final String DC_COVERAGE = "dcCoverage";
    /* Constant for Creator dc item data field                                */
    public static final String DC_CREATOR = "dcCreator";
    public static final String DC_DATE = "dcDate";
    public static final String DC_DESCRIPTION = "dcDescription";
    public static final String DC_FORMAT = "dcFormat";
    public static final String DC_IDENTIFIER = "dcIdentifier";
    public static final String DC_LANGUAGE = "dcLanguage";
    public static final String DC_PUBLISHER = "dcPublisher";
    public static final String DC_RELATION = "dcRelation";
    public static final String DC_RIGHTS = "dcRights";
    public static final String DC_SOURCE = "dcSource";
    public static final String DC_SUBJECT = "dcSubject";
    /* DCES element item title automatically retrieved from associated
     * content item title (display name), not persisted here!            
    public static final String DC_TITLE = "";                                 */
    public static final String DC_TYPE = "dcType";


    // ///////////////////////////////////////////////////////////////////////
    //
    // Constructors section

    /**
     * Default constructor. This creates a new content page.
     **/
    protected DublinCoreES() {
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
    public DublinCoreES(OID oid) {
        super(oid);
    }

    public DublinCoreES(DataObject obj) {
        super(obj);
    }
    
    public DublinCoreES(String type) {
        super(type);
    }


    /**
     * Create a new empty DCES object and associate it with the given content
     * item.
     * 
     * @param  ownerItem content item object to associate the new DCES object with
     * @return the new DCES object, associated with the given content item 
     *         object but otherwise empty.
     */
    public static DublinCoreES create(ContentItem ownerItem) {
        DublinCoreES dces = new DublinCoreES();
        dces.setOwner(ownerItem);
        dces.setName(ownerItem.getName() + "-dublin-metadata");
        return dces;
    }
    
    
    /**
     * Retrieve a DCES by its owner (a content item) from the database and
     * instantiate a new DCES object.
     * 
     * @param ownerItem the content item object for which we look for meta data 
     * @return a DCES object
     */
    public static DublinCoreES findByOwner(ContentItem ownerItem) {
        DataCollection items = SessionManager.getSession()
                                             .retrieve(BASE_DATA_OBJECT_TYPE);
        items.addEqualsFilter(DCES_OWNER + "." + ACSObject.ID, 
                              ownerItem.getID());
        
        if (items.next()) {
            DataObject obj = items.getDataObject();
            items.close();
            return (DublinCoreES)DomainObjectFactory.newInstance(obj);
        }
        return null;
    }


    // ///////////////////////////////////////////////////////////////////////
    //
    // The getters / setters section

    /**
     * Associate this DCES with the content item it provides Dublin Core meta
     * data for. 
     * @param owner 
     */
    protected void setOwner(ContentItem ownerItem) {
        setAssociation(DCES_OWNER, ownerItem);
    }
    
    /**
     * Retrieve the content item this DCES provides meta data for.
     * 
     * @return The contentItem this DCES provides meta data for.  
     */
    public ContentItem getOwner() {
        DataObject dobj = (DataObject)get(DCES_OWNER);
        Assert.exists(dobj, DataObject.class);
        return (ContentItem)DomainObjectFactory.newInstance(dobj);
    } 


    /** 
     * Retrieve the Contributor metadata item. A contriburor is an entity
     * responsible for making contributions to the resource. Examples include 
     * a person, an organization, or a service. 
     */
    public String getContributor() {
        return (String)get(DC_CONTRIBUTOR);
    }

    /**
     * Set and store the Contributor metadata item. A contriburor is an entity
     * responsible for making contributions to the resource. Examples include 
     * a person, an organization, or a service. 
     * 
     * @param contributor 
     */
    public void setContributor(String contributor) {
        set(DC_CONTRIBUTOR, contributor);
    }

    /** 
     * Retrieve the Coverage metadata item. 
     * It is the spatial or temporal topic of the resource, the spatial applicability 
     * of the resource, or the jurisdiction under which the resource is relevant.
     * Spatial topic and spatial applicability may be a named place or a location 
     * specified by its geographic coordinates. Temporal topic may be a named 
     * period, date, or date range. A jurisdiction may be a named administrative 
     * entity or a geographic place to which the resource applies. Recommended 
     * best practice is to use a controlled vocabulary such as the Thesaurus of 
     * Geographic Names [TGN].
     */
    public String getCoverage() {
        return (String)get(DC_COVERAGE);
    }

    /**
     * Set and store the Coverage metadata item. 
     * It is the spatial or temporal topic of the resource, the spatial applicability 
     * of the resource, or the jurisdiction under which the resource is relevant.
     * For more details @see getCoverage()
     * 
     * @param coverage 
     */
    public void setCoverage(String coverage) {
        set(DC_COVERAGE, coverage);
    }


    /*Creator*/
    public String getCreator() {
        return (String)get(DC_CREATOR);
    }

    public void setCreator(String creator) {
        set(DC_CREATOR, creator);
    }
  

  

    /*Date*/
    public String getDate() {
        return (String)get(DC_DATE);
    }

    public void setDate(String date) {
        set(DC_DATE, date);
    }

    /*Language*/
    @Override
    public String getLanguage() {
        return getOwner().getLanguage();
    }

    @Override
    public void setLanguage(String language) {
        throw new UnsupportedOperationException(
            "Metadata language is no longer set explicitly. " + 
            "Language is pulled from underlying ContentItem");
        //set(DC_LANGUAGE, language);
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

    /*Publisher*/
    public String getPublisher() {
        return (String)get(DC_PUBLISHER);
    }

    public void setPublisher(String publisher) {
        set(DC_PUBLISHER, publisher);
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


    /**
     * 
     */
    @Override
    public void beforeSave() {
        super.beforeSave();
        if (get(DC_LANGUAGE) == null) {
            set(DC_LANGUAGE, getOwner().getLanguage() );
        }
    }
}
