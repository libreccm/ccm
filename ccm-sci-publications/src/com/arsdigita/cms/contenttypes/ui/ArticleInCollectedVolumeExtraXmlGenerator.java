/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class ArticleInCollectedVolumeExtraXmlGenerator
        implements ExtraXMLGenerator {

    public void generateXML(final ContentItem item, 
                            final Element element, 
                            final PageState state) {
        if (!(item instanceof ArticleInCollectedVolume)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    ArticleInJournal.class.getName()));
        }
        
        final ArticleInCollectedVolume article = (ArticleInCollectedVolume) item;
        createCollectedVolumeXml(article, element, state);
    }
    
    private void createCollectedVolumeXml(final ArticleInCollectedVolume article,
                                          final Element parent,
                                          final PageState state) {
        final CollectedVolume collectedVolume = article.getCollectedVolume(GlobalizationHelper.getNegotiatedLocale().getLanguage());
        if (collectedVolume != null) {            
            final XmlGenerator generator = new XmlGenerator(collectedVolume);
            generator.setItemElemName("collectedVolume", "");            
            generator.setListMode(true);
            generator.generateXML(state, parent, "");
        }
    }

    public void addGlobalStateParams(final Page p) {
        //nothing 
    }
    
    @Override
    public void setListMode(final boolean listMode) {
        //Nothing
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
