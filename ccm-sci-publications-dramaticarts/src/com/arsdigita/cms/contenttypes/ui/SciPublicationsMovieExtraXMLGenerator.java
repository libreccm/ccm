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
import com.arsdigita.cms.contenttypes.SciPublicationsMovie;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsMovieExtraXMLGenerator implements ExtraXMLGenerator {

    private boolean listMode;

    @Override
    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (item instanceof SciPublicationsMovie) {
            final SciPublicationsMovie movie = (SciPublicationsMovie) item;
            
            if (movie.getDirector() != null) {
                final XmlGenerator generator = new XmlGenerator(movie.getDirector());
                generator.setItemElemName("director", "");
                generator.setListMode(true);
                generator.generateXML(state, element, "");
            }
            
            if (movie.getProductionCompany() != null) {
                final XmlGenerator generator = new XmlGenerator(movie.getProductionCompany());
                generator.setItemElemName("productionCompany", "");
                generator.setListMode(true);
                generator.generateXML(state, element, "");
            }
            
        } else {
            throw new IllegalArgumentException(
                "This ExtraXMLGenerator can only process item of type SciPublicationsMovie");
        }
    }

    public void addGlobalStateParams(final Page page) {
        //nothing                
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
