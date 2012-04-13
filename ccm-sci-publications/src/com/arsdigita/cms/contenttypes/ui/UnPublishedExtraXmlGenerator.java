package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class UnPublishedExtraXmlGenerator implements ExtraXMLGenerator {

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof UnPublished)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    UnPublished.class.getName()));
        }

        final UnPublished unPublished = (UnPublished) item;
        createOrganizationXml(unPublished, element, state);
    }

    private void createOrganizationXml(final UnPublished unPublished,
                                       final Element parent,
                                       final PageState state) {
        final GenericOrganizationalUnit orga =
                                        unPublished.getOrganization(GlobalizationHelper.
                getNegotiatedLocale().getLanguage());
        if (orga != null) {            
            final XmlGenerator generator = new XmlGenerator(orga);
            generator.setItemElemName("organization", "");
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
