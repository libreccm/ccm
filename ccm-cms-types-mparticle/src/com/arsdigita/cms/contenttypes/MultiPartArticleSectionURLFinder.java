/*
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
 * Originally created on 11-May-04
 * 
 * Copyright West Sussex County Council
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.kernel.NoValidURLException;
import com.arsdigita.kernel.URLFinder;
import com.arsdigita.kernel.URLService;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;

/**
 * Implementation of URLFinder for a specific ArticleSection
 * 
 * @author cgyg9330
 *
 */
public class MultiPartArticleSectionURLFinder implements URLFinder {
    public static final String FIND_PAGE_FOR_SECTION_QUERY =
                  "com.arsdigita.cms.contenttypes.PageNumberForArticleSection";
    
    /**
     * 
     * find URL for a multipart article section. URL is of the format
     * content/folder/articlename?page=X where articlename is the multipart
     * article that this section is from and page is the page that the section
     * appears on.
     * 
     * @param oid the OID of the article section
     */
    public String find(OID oid) throws NoValidURLException {
        DataObject dobj = SessionManager.getSession().retrieve(oid);
    	checkObject(dobj, oid);

        ArticleSection thisSection = new ArticleSection(dobj);
        MultiPartArticle article = thisSection.getMPArticle();
        StringBuffer articleURL = new StringBuffer(URLService.locate(article.getOID()));
        
        Integer sectionNumber = thisSection.getRank();
        if (sectionNumber.intValue() != 1) {
            DataQuery pageNumber = SessionManager.getSession().
                                   retrieveQuery(FIND_PAGE_FOR_SECTION_QUERY);
            pageNumber.setParameter("section", thisSection.getID());
            while (pageNumber.next()) {
                // just in case there are existing parameters on the url
                if (articleURL.toString().indexOf("?") != -1) {
                    articleURL.append("&");
                } else {
                    articleURL.append("?");
                }
                articleURL.append("page=");
                articleURL.append(pageNumber.get("pageNumber"));
            }
            
        }
        return articleURL.toString();
    }

    /**
     * We are redirected to the correct page to edit the multipart article, 
     * ignoring the section. This is the current default behaviour for live links
     * to unpublished Multi-part articles.
     */
    public String find(OID oid, String context) throws NoValidURLException {
    	if(!"draft".equals(context)) {
        return find(oid);
    	} else {
			DataObject dobj = SessionManager.getSession().retrieve(oid);
			checkObject(dobj, oid);

			ArticleSection thisSection = new ArticleSection(dobj);
			MultiPartArticle article = thisSection.getMPArticle();

			ContentSection contentSection = thisSection.getContentSection();
			ItemResolver resolver = contentSection.getItemResolver();

			String url = resolver.generateItemURL(null, article, 
                                                              contentSection,
                                                              context);
			final int sep = url.indexOf('?');
			URL destination = null;
        
			if (sep == -1) {
				destination = URL.there(url, null);
			} else {
				final ParameterMap params = ParameterMap.fromString(
                                                            url.substring(sep + 1));
				destination = URL.there(url.substring(0, sep), params);
			}
			return  destination.toString();
    	}
    }

    private static void checkObject(DataObject dobj, OID oid) {
		if (dobj == null) {
			throw new NoValidURLException("No such data object " + oid);
		}

		if (!dobj
			.getObjectType()
			.getQualifiedName()
			.equals(ArticleSection.BASE_DATA_OBJECT_TYPE)) {
			throw new NoValidURLException(
				"Data Object is not a multipart article section "
					+ dobj.getObjectType().getQualifiedName()
					+ " "
					+ oid);
		}
    }
}
