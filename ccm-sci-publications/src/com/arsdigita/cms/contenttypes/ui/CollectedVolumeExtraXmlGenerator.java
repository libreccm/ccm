package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolumeCollection;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class CollectedVolumeExtraXmlGenerator implements ExtraXMLGenerator {

    private boolean listMode = false;

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof CollectedVolume)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    CollectedVolume.class.getName()));
        }

        final CollectedVolume collectedVolume = (CollectedVolume) item;
        if (!listMode) {
            createArticlesXml(collectedVolume, element, state);
        }
    }

    private void createArticlesXml(final CollectedVolume collectedVolume,
                                   final Element parent,
                                   final PageState state) {
        final ArticleInCollectedVolumeCollection articles = collectedVolume.
                getArticles();
        if ((articles == null) || articles.isEmpty()) {
            return;
        }

        final Element articlesElem = parent.newChildElement("articles");
        while (articles.next()) {
            createArticleXml(articles.getArticle(GlobalizationHelper.
                    getNegotiatedLocale().getLanguage()),
                             articles.getArticleOrder(),
                             articlesElem,
                             state);

        }
    }

    private void createArticleXml(final ArticleInCollectedVolume article,
                                  final Integer order,
                                  final Element articlesElem,
                                  final PageState state) {
        final XmlGenerator generator = new XmlGenerator(article);
        generator.setItemElemName("article", "");
        generator.addItemAttribute("order", order.toString());
        generator.generateXML(state, articlesElem, "");
    }

    public void addGlobalStateParams(final Page page) {
        //Nothing
    }

    @Override
    public void setListMode(final boolean listMode) {
        this.listMode = listMode;
    }

    private class XmlGenerator extends SimpleXMLGenerator {

        private final ContentItem item;

        public XmlGenerator(final ContentItem item) {
            super();
            this.item = item;
        }

        @Override
        protected ContentItem getContentItem(final PageState state) {
            return item;
        }
    }
}
