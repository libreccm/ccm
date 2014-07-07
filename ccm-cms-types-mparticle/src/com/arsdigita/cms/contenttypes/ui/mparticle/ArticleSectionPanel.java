/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui.mparticle;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.CMSContext;
import com.arsdigita.cms.CMSExcursion;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.ArticleSection;
import com.arsdigita.cms.contenttypes.ArticleSectionCollection;
import com.arsdigita.cms.contenttypes.MultiPartArticle;
import com.arsdigita.cms.dispatcher.XMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * <p>This <code>ContentPanel</code> component fetches
 * the {@link com.arsdigita.cms.dispatcher.XMLGenerator} for the content
 * section.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #7 $ $Date: 2004/08/17 $
 * @version $Id: ArticleSectionPanel.java 2143 2011-01-16 12:26:49Z pboy $
 */
public class ArticleSectionPanel extends SimpleComponent implements
        ExtraXMLGenerator {

    private static final Logger s_log = Logger.getLogger(
            ArticleSectionPanel.class);
    private PageParameter m_page;
    private boolean m_showAllSections = false;
    /**
     * Variable for holding an injected item.
     */
    private ContentItem m_item = null;
    public static final String PAGE_NUMBER_PARAM = "page";

    public ArticleSectionPanel() {
        super();

        m_page = new PageParameter(PAGE_NUMBER_PARAM);
    }

    @Override
    public void register(Page p) {
        super.register(p);

        addGlobalStateParams(p);
    }

    public void addGlobalStateParams(Page p) {
        p.addGlobalStateParam(m_page);
    }

    @Override
    public void setListMode(final boolean listMode) {
        //nothing
    }
    
    /**
     * Try to get the section from the context
     * if there isn't (eg if we are looking at an index
     * item on a category), guess the section from the item
     * @param state
     * @param item
     * @return 
     */
    protected XMLGenerator getXMLGenerator(PageState state, ContentItem item) {
        ContentSection section = null;
        try {
            section = CMS.getContext().getContentSection();
        } catch (Exception e) {
        }
        if (section == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Item id ; " + item.getOID() + " - " + item.
                        getContentSection() + " - " + item);
            }
            section = item.getContentSection();
            CMS.getContext().setContentSection(section);
        }
        return section.getXMLGenerator();
    }

    public void setShowAllSections(boolean showAll) {
        m_showAllSections = showAll;
    }

    protected ContentItem getContentItem(PageState state) {
        CMSContext context = CMS.getContext();

        if (m_item != null) {
            return m_item;
        }

        if (!context.hasContentItem()) {
            return null;
        }
        return context.getContentItem();
    }

    /**
     * This method provides a way for injecting the item to show if the other
     * ways for retrieving the item to show do not work. An example for this
     * is the index/greeting item of a category.
     *
     * @param item The item to inject.
     */
    public void setContentItem(ContentItem item) {
        if (item instanceof ContentBundle) {
            ContentBundle bundle;
            HttpServletRequest request;
            ContentItem resolved;
            bundle = (ContentBundle) item;

            resolved = bundle.getInstance(GlobalizationHelper
                             .getNegotiatedLocale().getLanguage());
            if (resolved == null) {
                resolved = bundle.getPrimaryInstance();
            }

            m_item = resolved;
        } else {
            m_item = item;
        }
    }

    protected ArticleSection[] getSections(ContentItem item,
                                           final PageState state) {
        PageNumber number;
        try {
            number = (PageNumber) state.getValue(m_page);
        } catch (IllegalArgumentException e) {
            // probably viewing an index item on a category,
            // get the parameter from the request and set it
            String value = state.getRequest().getParameter(PAGE_NUMBER_PARAM);
            if (value == null) {
                value = "1";
            }
            number = new PageNumber(value);
            state.setValue(m_page, number);
        }
        if (number == null) {
            number = new PageNumber("1");
        }

        MultiPartArticle mpa = (MultiPartArticle) item;
        if (!number.wantAllSections()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Page number requested is " + number.getPageNumber());
            }
            return getSections(item, state, number.getPageNumber());
        } else {
            ArticleSectionCollection sections = mpa.getSections();
            if (s_log.isDebugEnabled()) {
                s_log.debug("No page number provided");
            }
            ArticleSection[] page = new ArticleSection[(int) sections.size()];
            int i = 0;
            while (sections.next()) {
                page[i] = (ArticleSection) sections.getArticleSection();
                i++;
            }
            return page;
        }
    }

    // Get the section based on position in list of sections
    protected ArticleSection[] getSections(ContentItem item,
                                           PageState state,
                                           Integer number) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting page " + number);
        }

        MultiPartArticle mpa = (MultiPartArticle) item;
        ArticleSectionCollection sections = mpa.getSections();
        int current = 1;
        int desired = number.intValue();

        ArrayList page;
        try {
            // Skip over sections until we hit the desired page
            while (current < desired) {
                if (!sections.next()) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Hit end of section list");
                    }
                    return new ArticleSection[]{};
                }
                ArticleSection section = (ArticleSection) sections.
                        getArticleSection();
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Skipping " + section.getOID());
                }
                if (section.isPageBreak()) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Got page break");
                    }
                    current++;
                }
            }

            // Now capture sections until we hit the next page (or end of sections)
            int subsequent = desired + 1;
            page = new ArrayList();
            while (current < subsequent) {
                if (!sections.next()) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Got end of pages, returning.");
                    }
                    break;
                }
                ArticleSection section = (ArticleSection) sections.
                        getArticleSection();
                page.add(section);
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Keeping section " + section.getOID());
                }
                if (section.isPageBreak()) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Got page break");
                    }
                    current++;
                }
            }
        } finally {
            sections.close();
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("All done " + page.size() + " sections found");
        }

        return (ArticleSection[]) page.toArray(new ArticleSection[page.size()]);
    }

    /**
     * Generates XML that represents a content item.
     *
     * @param state The page state
     * @param parent The parent DOM element
     * @see com.arsdigita.cms.dispatcher.XMLGenerator
     */
    @Override
    public void generateXML(final PageState state,
                            final Element parent) {
        ContentItem item = getContentItem(state);

        if (!isVisible(state) || item == null
            || !(item instanceof MultiPartArticle)) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Skipping generate XML isVisible: " + isVisible(
                        state) + " item "
                            + (item == null ? null : item.getOID()));
            }
            return;
        }

        generateXML(item, parent, state);
    }

    /**
     * Specify the XML for a given content item.
     * @param item
     * @param element
     * @param state 
     */
    @Override
    public void generateXML(ContentItem item, Element element, PageState state) {

        Element content = element.newChildElement("cms:articleSectionPanel",
                                                  CMS.CMS_XML_NS);
        exportAttributes(content);

        XMLGenerator xmlGenerator = getXMLGenerator(state, item);

        ArticleSection sections[] = getSections(item, state);
        for (ArticleSection section : sections) {
            generateSectionXML(state, content, section, xmlGenerator);
        }
    }

    protected void generateSectionXML(final PageState state,
                                      final Element parent,
                                      final ContentItem section,
                                      final XMLGenerator xmlGenerator) {
        CMSExcursion excursion = new CMSExcursion() {

            @Override
            public void excurse() {
                setContentItem(section);
                xmlGenerator.generateXML(state, parent, null);
            }
        };
        try {
            excursion.run();
        } catch (ServletException ex) {
            throw new UncheckedWrapperException("excursion failed", ex);
        } catch (IOException ex) {
            throw new UncheckedWrapperException("excursion failed", ex);
        }
    }

    // A class representing either an Integer number indicating
    // the position in the list of sections, or the string 'all'
    private class PageNumber {

        private final boolean m_all;
        private final Integer m_number;

        public PageNumber(String number)
                throws NumberFormatException {

            if ("all".equals(number.toLowerCase())) {
                m_all = true;
                m_number = null;
            } else {
                m_all = false;
                m_number = new Integer(number);
            }
        }

        public boolean wantAllSections() {
            return m_all;
        }

        public Integer getPageNumber() {
            return m_number;
        }
    }

    // A parameter which is either an Integer number indicating
    // the position in the list of sections, or the string 'all'
    private class PageParameter extends ParameterModel {

        public PageParameter(String name) {
            super(name);
        }

        @Override
        public Object transformValue(HttpServletRequest request)
                throws IllegalArgumentException {
            return transformSingleValue(request);
        }

        @Override
        public Object unmarshal(String encoded)
                throws IllegalArgumentException {

            if (encoded == null || encoded.length() == 0) {
                return null;
            }
            try {
                return new PageNumber(encoded);
            } catch (NumberFormatException e) {
              //e.printStackTrace();
                throw new IllegalArgumentException(getName()
                                                   + " should be a BigDecimal: '"
                                                   + encoded + "'");
            }
        }

        @Override
        public Class getValueClass() {
            return PageNumber.class;
        }
    }
}
