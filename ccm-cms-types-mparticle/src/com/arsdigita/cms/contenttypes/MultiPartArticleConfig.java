/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.EnumerationParameter;
import com.arsdigita.util.parameter.Parameter;

/**
 * A set of configuration parameters for multipart articles.
 *
 * @author Chris Gilbert &lt;chris.gilbert@westsussex.gov.uk&gt;
 */
public class MultiPartArticleConfig extends AbstractConfig {
    public final static String versionId =
        "$Id: MultiPartArticleConfig.java,v 1.3 2006/03/28 07:40:17 cgyg9330 Exp $" +
        "$Author: cgyg9330 $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    public static final String SHORT_TITLE = "short";
    public static final String PAGE_TOP_TITLE = "page_top";
    public static final String SECTION_TITLE = "section";
    
    
    private EnumerationParameter m_searchResultFormat;
    

    
    public MultiPartArticleConfig() {
		m_searchResultFormat =  new EnumerationParameter("com.arsdigita.cms.types.mparticle.search.result_format", Parameter.REQUIRED,
	            SHORT_TITLE);
		m_searchResultFormat.put(SHORT_TITLE, SHORT_TITLE); 
		m_searchResultFormat.put(PAGE_TOP_TITLE, PAGE_TOP_TITLE);
		m_searchResultFormat.put(SECTION_TITLE, SECTION_TITLE);
		register(m_searchResultFormat);
	    loadInfo();
    }

    /**
     * Used to determine the format of search results for multipart
     * article sections.
     * 
     * value short retails existing behaviour - section title is 
     * used and there is no summary
     * 
     * value page_top uses the title of the section at the top of the page 
     * (where several sections are used without page break) 
     * preceded by the parent article title, and uses parent
     * summary as a summary (if entire article appears on one page, 
     * the parent title is used as title on its own.
     * 
     * value section uses the section title preceded by the parent 
     * title, and uses parent summary as a summary
     * 
     * page_top is appropriate if you use multiple sections on a single 
     * page purely to display several images - in this case, the 
     * page should appear as if it were a single page.
     * 
     * short is included for legacy implementations 
     * 
     * Note - any changes to config parameter ONLY AFFECT 
     * search results for MP Articles created after the change.
     * To apply changes to existing articles, change config
     * parameter, then delete search content where object type is 
     * 'com.arsdigita.cms.contenttypes.ArticleSection' and then
     * ccm-run com.arsdigita.london.search.Reindexer --object-type
     * com.arsdigita.cms.contenttypes.ArticleSection
     * on the command line
     * 
     * @return
     */
    public String getSearchResultFormat() {
        return (String)get(m_searchResultFormat);
    }
    
    public boolean useParentSummary () {
    	return !SHORT_TITLE.equals(getSearchResultFormat());
    }
    
    public boolean includeParentTitle () {
    	return !SHORT_TITLE.equals(getSearchResultFormat());
    }

    public boolean useSectionTitle() {
    	return !PAGE_TOP_TITLE.equals(getSearchResultFormat());
    }
    
   
}
