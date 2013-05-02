package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class InternetArticleExtraXmlGenerator implements ExtraXMLGenerator {

    private boolean listMode = false;

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof InternetArticle)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    InternetArticle.class.getName()));
        }

        final InternetArticle internetArticle = (InternetArticle) item;
        createOrganizationXml(internetArticle, element, state);
    }

    public void addGlobalStateParams(final Page page) {
        //nothing
    }

    @Override
    public void setListMode(final boolean listMode) {
        this.listMode = true;
    }

    private void createOrganizationXml(final InternetArticle internetArticle,
                                       final Element parent,
                                       final PageState state) {
        final GenericOrganizationalUnit orga =
                                        internetArticle.getOrganization(GlobalizationHelper.
                getNegotiatedLocale().getLanguage());
        if (orga != null) {
            final XmlGenerator generator = new XmlGenerator(orga);
            generator.setItemElemName("organization", "");
            generator.setListMode(listMode);
            generator.generateXML(state, parent, "");
        }
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
