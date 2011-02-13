/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

/**
 * CMS content type that represents a multi-part article.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @version $Id: MultiPartArticle.java 1500 2007-03-20 09:25:45Z chrisgilbert23 $
 */
public class MultiPartArticle extends ContentPage {

    private static final Logger s_log = Logger.getLogger(MultiPartArticle.class);

    /** PDL property names */
    public static final String SUMMARY      = "summary";
    public static final String SECTIONS     = "sections";


    /** rank direction changes */
    public static final int    UP           = 1;
    public static final int    DOWN         = 2;


    /** data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.contenttypes.MultiPartArticle";

    /** named query parameters */
    public static final String RANK    = "rank";
    public static final String ARTICLE = "article";
    public static final String SECTION = "section";
    
    private static MultiPartArticleConfig s_config = new MultiPartArticleConfig();

    static {
        s_log.debug("Static initalizer starting...");
	s_config.load();
        s_log.debug("Static initalizer finished.");
    }
    
    public static MultiPartArticleConfig getConfig() {
	return s_config;
    }

    /** Default constructor. */
    public MultiPartArticle() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. Retrieves an object instance with the given id.
     *
     * @param id the id of the object to retrieve
     */
    public MultiPartArticle( BigDecimal id )
        throws DataObjectNotFoundException
    {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. Retrieves an object instance with the given OID.
     *
     * @param id the object id of the object to retrieve
     */
    public MultiPartArticle( OID id )
        throws DataObjectNotFoundException
    {
        super(id);
    }

    /**
     * Constructor. Create a MultiPartArticle domain object using the
     * object data given.
     *
     * @param obj the object data to use
     */
    public MultiPartArticle( DataObject obj ) {
        super(obj);
    }

    /** Constructor. */
    public MultiPartArticle( String type ) {
        super(type);
    }

    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /** Accessor. Get the summary for this MultiPartArticle. */
    public String getSummary() {
        return (String)get(SUMMARY);
    }

    /** Accessor. Set the summary for this MultiPartArticle. */
    public void setSummary( String summary ) {
        set(SUMMARY, summary);
    }

    /**
     * Add the specified ArticleSection to this object.  Sets the rank
     * of the association to be after all the other ArticleSections.
     *
     * @param section the ArticleSection to add
     */
    public void addSection( ArticleSection section ) {
        addSection(section, getMaxRank() + 1);
    }

    /**
     * Add the specified ArticleSection to this object. Sets the rank
     * of the association to the given value.
     *
     * @param section the ArticleSection to add
     * @param rank the rank of the ArticleSection in the association.
     */
    public void addSection( ArticleSection section, Integer rank ) {
        s_log.info("adding section:" + section.getName() + 
                 " with rank " + rank.toString());
        section.setRank(rank);
        add(SECTIONS,section);
    }

    public void addSection( ArticleSection section, int rank ) {
        addSection(section, new Integer(rank));
    }

    public ArticleSectionCollection getSections(boolean asc) {
        DataAssociationCursor dac = ((DataAssociation) get(SECTIONS)).cursor();
        String direction = asc ? " asc" : " desc";
        dac.addOrder(RANK + direction);
        return new ArticleSectionCollection(dac);
    }

    /**
     * Get the collection of sections.
     */
    public ArticleSectionCollection getSections() {
        return getSections(true);
    }

    /**
     * Remove the given ArticleSection from this object. Updates
     * the ranks of the remaining sections.
     *
     * @param section the ArticleSection to remove
     */
    public void removeSection( ArticleSection section ) {
        changeSectionRank(section, getMaxRank());
        section.delete();
    }

    /**
     * Change the rank of a given ArticleSection in this object.
     */
    public void changeSectionRank(BigDecimal sectionID, int rank) {
        ArticleSection target = (ArticleSection)
            DomainObjectFactory.newInstance
            (new OID(ArticleSection.BASE_DATA_OBJECT_TYPE, sectionID));

        changeSectionRank(target, rank);
    }

    public void changeSectionRank(BigDecimal sectionID, BigDecimal dest ) {
        int rank = getRank(dest);
        changeSectionRank(sectionID, rank);
    }

    public void changeSectionRank(ArticleSection section, BigDecimal dest) {
        int rank = getRank(dest);
        changeSectionRank(section, rank);
    }


    protected int getRank(BigDecimal sectionID) {
        try {
            ArticleSection section = new ArticleSection(sectionID);
            return section.getRank().intValue();
        } catch (DataObjectNotFoundException e) {
            throw new com.arsdigita.util.UncheckedWrapperException(e);
        }
    }


    /**
     * Change the rank of the sections with the given id within this object.
     * Sets the section rank to that given, and moves all other section ranks
     * as appropriate. If the new rank is greater than the current rank,
     * sections in between will be moved to a numerically lower rank. If the
     * new rank is less than the current rank than sections in between will be
     * moved to a higher rank.
     *
     * @param source the section to reorder
     * @param rank the new rank for the section. This must be between 1 and
     * the max section rank inclusively.
     */
    public void changeSectionRank(ArticleSection source, int destRank) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("*** changeSectionRank, section ID = " + source.getID()
                      + "destRank = "  + destRank);
        }

        Integer r = source.getRank();
        if (r == null) {
            throw new IllegalStateException(source + " has null rank");
        }

        int curRank = r.intValue();

        ArticleSectionCollection coll = getSections(true);
        if (curRank > destRank) {
            coll.setRange(new Integer(destRank), new Integer(curRank));
            int rank = destRank;
            while (coll.next()) {
                ArticleSection cur = coll.getArticleSection();
                cur.setRank(new Integer(rank + 1));
                rank++;
            }
            source.setRank(new Integer(destRank));
        } else if (curRank < destRank) {
            coll.setRange(new Integer(curRank + 1), new Integer(destRank + 1));
            int rank = curRank + 1;
            while (coll.next()) {
                ArticleSection cur = coll.getArticleSection();
                cur.setRank(new Integer(rank - 1));
                rank++;
            }
            source.setRank(new Integer(destRank));
        }
        coll.close();
    }


    /**
     * Returns the highest section rank.
     */
    protected int getMaxRank() {
        int rank;

        ArticleSectionCollection coll = getSections(false);
        if (coll.next()) {
            rank = coll.getArticleSection().getRank().intValue();
            coll.close();
        } else {
            rank = 0;
        }

        return rank;
    }

    public static final int SUMMARY_LENGTH = 200;
    public String getSearchSummary() {
        final String summary = getSummary();

        if (summary == null) {
            return "";
        } else {
            return com.arsdigita.util.StringUtils.truncateString
                (summary, SUMMARY_LENGTH, true);
        }
    }
    
  
}
