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
        int numberOfPages = 0;
        
        final ArticleSectionCollection sections = article.getSections();
        while(sections.next()) {
            if (sections.getArticleSection().isPageBreak()) {
                numberOfPages++;
            }
        }
        
        if (numberOfPages == 0) {
            numberOfPages = 1;
        }
        
        return Integer.toString(numberOfPages);
    }

    public void addGlobalStateParams(final Page page) {
        //Nothing
    }
    
    public void setListMode(final boolean listMode) {
        //Ignored here
    }
    
}
