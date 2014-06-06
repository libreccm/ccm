/*
 * Copyright (C) 2014 Jens Pelzetter
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

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class MultiPartArticleDataXMLGenerator implements ExtraXMLGenerator {

    public MultiPartArticleDataXMLGenerator() {
        //Nothing
    }
    
    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof MultiPartArticle)) {
            throw new IllegalArgumentException("This ExtraXMLGenerator can only process "
                                               + "MultiPartArticleItems.");
        }

        final MultiPartArticle article = (MultiPartArticle) item;

        final Element root = element.newChildElement("cms:mpadata", CMS.CMS_XML_NS);
        
        final Element numberOfPages = root.newChildElement("numberOfPages");
        numberOfPages.setText(calulateNumberOfPages(article));
        
        
    }
    
    private String calulateNumberOfPages(final MultiPartArticle article) {
        
        final ArticleSectionCollection sections = article.getSections();
        
        if (article.getSections().size() == 0) {
                sections.close();
                return "0";
        }
        
        long numberOfPages = 1;
        long index = 0;
        long lastIndex = sections.size() - 1;
        while(sections.next()) {
            index++;
            //The check for last index is necessary because we don't want to count a page break after
            //the last section
            if (sections.getArticleSection().isPageBreak()
                && index < (lastIndex)) {
                numberOfPages++;
            }
        }
        
//        if (numberOfPages == 0) {
//            numberOfPages = 1;
//        }
        
        return Long.toString(numberOfPages);
    }

    public void addGlobalStateParams(final Page page) {
        //Nothing
    }
    
    public void setListMode(final boolean listMode) {
        //Ignored here
    }
    
}
