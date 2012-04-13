package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class InProceedingsExtraXmlGenerator implements ExtraXMLGenerator {

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof InProceedings)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    InProceedings.class.getName()));
        }

        final InProceedings inProceedings = (InProceedings) item;
        createProceedingsXml(inProceedings, element, state);
    }

    private void createProceedingsXml(final InProceedings inProceedings,
                                      final Element parent,
                                      final PageState state) {
        final Proceedings proceedings =
                          inProceedings.getProceedings(GlobalizationHelper.
                getNegotiatedLocale().getLanguage());
        if (proceedings != null) {            
            final XmlGenerator generator = new XmlGenerator(proceedings);
            generator.setItemElemName("proceedings", "");
            generator.generateXML(state, parent, "");
        }
    }

    public void addGlobalStateParams(final Page page) {
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
