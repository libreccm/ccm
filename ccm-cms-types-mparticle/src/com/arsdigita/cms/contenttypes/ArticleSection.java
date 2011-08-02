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

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * Represents a section within a MultiPartArticle
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @version $Id: ArticleSection.java 2099 2010-04-17 15:35:14Z pboy $
 */
public class ArticleSection extends ContentPage {
    private static final Logger s_log = Logger.getLogger(ArticleSection.class);

    /** attributes names */
    public static final String TEXT     = "text";
    public static final String IMAGE    = "image";
    public static final String RANK     = "rank";
    public static final String PAGE_BREAK = "pageBreak";
    public static final String MP_ARTICLE = "mparticle";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.contenttypes.ArticleSection";

    /** Default constructor. */
    public ArticleSection() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor.  Retrieves an object instance with the given id.
     *
     * @param id the id of the object to retrieve
     */
    public ArticleSection(BigDecimal id) 
        throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor.  Retrieves an object instance with the given OID.
     *
     * @param id the id of the object to retrieve
     */
    public ArticleSection(OID id) 
        throws DataObjectNotFoundException {
        super(id);
    }

    /**
     * Constructor.  Creates an ArticleSection domain object using the
     * object data given.
     *
     * @param obj the object data to use
     */
    public ArticleSection(DataObject obj) {
        super(obj);
    }


    /** Constructor.  Construct an object with the given type. */
    public ArticleSection(String type) {
        super(type);
    }


    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /** Accessor. Get this item's rank in the set of ArticleSections */
        
    public Integer getRank() {
        return (Integer)get(RANK);
    }

    /** Mutator. Set this item's rank.*/

    public void setRank(Integer rank) {
        set(RANK, rank);
    } 

    public MultiPartArticle getMPArticle() {
        DataObject obj = (DataObject) get( MP_ARTICLE );
        return (MultiPartArticle) DomainObjectFactory.newInstance( obj );
    }

    /** Accessor. Get the text associated with this item. */
    public TextAsset getText() {
        if ( get(TEXT) == null ) {
            return null;
        }
        return new TextAsset((DataObject)get(TEXT));
    }

    /** Mutator.. Set the text associated with this item. */
    public void setText(TextAsset text) {
        setAssociation(TEXT, text);
    }

    /** Accessor. Get the image associated with this item. */
    public ImageAsset getImage() {
        if ( get(IMAGE) == null ) {
            return null;
        }
        return new ImageAsset((DataObject)get(IMAGE));
    }

    /** Mutator. Set the image associated with this item. */
    public void setImage(ImageAsset image) {
        setAssociation(IMAGE, image);
    }

    public void initialize() {
        super.initialize();
                                                                                
        if (isNew()) {
            set(PAGE_BREAK, Boolean.FALSE);
        }
    }

    public boolean isPageBreak() {
        return (Boolean.TRUE.equals(get(PAGE_BREAK)));
    }

    public void setPageBreak(boolean val) {
        set(PAGE_BREAK, new Boolean(val));
    }

    /**
     * Depending on config parameter, either return the title
     * of the section 
     * 
     * OR
     * 
     * return the title of the section at the top of the 
     * page on which the current section appears unless
     * the whole multipart article appears on one page, 
     * in which case null is returned.
     */
    public String getPageTitle() {
        
        if (MultiPartArticle.getConfig().useSectionTitle()) {
        	return getTitle();
        }
        
        s_log.debug("retrieve pageTitle for section " + getTitle() + " ranked " + getRank());
        MultiPartArticle parent = getMPArticle();
        
        // is this a single page article? either page break on last section, 
        // or no page breaks
        
        
        // boolean argument means order by rank ascending - no argument
	// currently means ascending, but specify here in case that 
	// changes
        ArticleSectionCollection sections = parent.getSections(true);
        sections.addEqualsFilter(PAGE_BREAK, Boolean.TRUE);
        Integer firstPageBreak = null;
        if (sections.next()) {
            firstPageBreak = sections.getArticleSection().getRank();
            s_log.debug("first page break in this article is in section ranked " + firstPageBreak);
        }
        sections.close();
        int lastSection = parent.getMaxRank();
        s_log.debug("last section of article is ranked " + lastSection);
        
        if (firstPageBreak == null || firstPageBreak.intValue() == lastSection) {
            s_log.debug("this is a single page article");
            return null;
        } else {
            s_log.debug("this article has more than one page");
        }
        
        // okay - this article has more than one page - lets find the page break
        // before this section and then the section following that page break
        // boolean argument means order by rank descending
        
        sections = parent.getSections(false);
        sections.addEqualsFilter(PAGE_BREAK, Boolean.TRUE);
        sections.addFilter(
        sections.getFilterFactory().lessThan(RANK, getRank(), true));
            
        Integer topOfPageRank = new Integer(1);
        if (sections.next()) {
            topOfPageRank = new Integer(sections.getArticleSection().getRank().intValue() + 1);
            s_log.debug("Found top of page rank: " 
                + topOfPageRank.intValue());
        } else {
            // If no page breaks before this section then we must be on 
            // page one.
            s_log.debug("This section is on first page.");
        }
        sections.close();
        
        // Get 'clean' 
        sections = parent.getSections(false);
        sections.addEqualsFilter(RANK, topOfPageRank);
       	String sectionTitle= null;
        while (sections.next()) {
            sectionTitle =  sections.getArticleSection().getTitle();
            s_log.debug("Found page/section title: " + sectionTitle);
        }
        
        return sectionTitle;
    }
    
    /**
    * As sections don't have their own summary, return the parent's search
    * summary.
    */
    public String getSearchSummary() {
    	if (MultiPartArticle.getConfig().useParentSummary()) {
    		MultiPartArticle parent = getMPArticle();
    		return parent.getSearchSummary();
    	} else {
    		return super.getSearchSummary();
    	}
    }

    protected void afterSave() {
        super.afterSave();

        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Saved section " + getOID() );
        }

        // Would prefer to do this in beforeSave(), but getMPArticle() returns
        // null
        if( null == getLanguage() ) {
            setLanguage( getMPArticle().getLanguage() );
        }

        PermissionService.setContext( this, getMPArticle() );
    }


    /**
     * This overrides the method on ContentItem, the API of which
     * says that this method can return a null eg. if the method is
     * called on an Article's ImageAsset.
     *
     * However there seems to be a problem with ArticleSections returning
     * null when they shouldn't. If that happens we are going to look up the
     * parent which we check is a MultiPartArticle and call getContentSection
     * on that.
     *
     * @return The content section to which this item belongs
     */
     public ContentSection getContentSection() {
         ContentSection ct = super.getContentSection();
         if (ct != null) {
             return ct;
         } else {
             ACSObject parent = getParent();
             if (parent != null && parent instanceof MultiPartArticle) {
                 ct = ((ContentItem)parent).getContentSection();
                 return ct;
             }
         }
         // this will be picked up by ContentItem.beforeSave()
         // where upon setDefaultContentSection will be called..
         return null;
     }
}
