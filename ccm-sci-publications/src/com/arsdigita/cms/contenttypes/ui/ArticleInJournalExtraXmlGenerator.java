package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.Journal;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class ArticleInJournalExtraXmlGenerator implements ExtraXMLGenerator {

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof ArticleInJournal)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    ArticleInJournal.class.getName()));
        }

        final ArticleInJournal article = (ArticleInJournal) item;
        createJournalXml(article, element, state);
    }

    private void createJournalXml(final ArticleInJournal article,
                                  final Element parent,
                                  final PageState state) {
        final Journal journal = article.getJournal(GlobalizationHelper.
                getNegotiatedLocale().getLanguage());
        if (journal != null) {            
            final XmlGenerator generator = new XmlGenerator(journal);
            generator.setListMode(true);
            generator.setItemElemName("journal", "");
            generator.generateXML(state, parent, "");
        }
    }

    public void addGlobalStateParams(final Page p) {
        //nothing
    }
    
    @Override
    public void setListMode(final boolean listMode) {
        //nothing
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
