/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.cms.CMSExcursion;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.dispatcher.XMLGenerator;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;

/**
 *
 * @author Jens Pelzetter
 * @version $Id: ArticleSectionXMLGenerator.java 3268 2015-02-25 08:31:00Z jensp
 * $
 */
public class ArticleSectionXMLGenerator implements ExtraXMLGenerator {

    public static final String PAGE_NUMBER_PARAM = "page";
    //private final PageParameter pageParam = new PageParameter(PAGE_NUMBER_PARAM);

    public ArticleSectionXMLGenerator() {
    }

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        final Element content = element.newChildElement(
            "cms:articleSectionPanel",
            CMS.CMS_XML_NS);

        generateToc(item, content, state);

        final XMLGenerator xmlGenerator = getXMLGenerator(state, item);

        final ArticleSection sections[] = getSections(item, state);
        for (int i = 0; i < sections.length; i++) {
            generateSectionXML(state, content, sections[i], xmlGenerator);
        }

        final PageNumber number = getPageNumber(state);
        if (number.wantAllSections()) {
            content.addAttribute("pageNumber", "all");
        } else {
            content
                .addAttribute("pageNumber", number.getPageNumber().toString());
        }
    }

    public void addGlobalStateParams(final Page page) {
    }

    @Override
    public void setListMode(final boolean listMode) {
        //nothing
    }

    protected PageNumber getPageNumber(final PageState state) {
        PageNumber number;

        String value = state.getRequest().getParameter(PAGE_NUMBER_PARAM);
        if (value == null) {
            value = "1";
        }
        number = new PageNumber(value);
        if (number == null) {
            number = new PageNumber("1");
        }

        return number;
    }

    protected void generateToc(
        final ContentItem item, final Element parent, final PageState state
    ) {
        if (!(item instanceof MultiPartArticle)) {
            throw new IllegalArgumentException("Item is not a MultiPartArticle");
        }

        final Element tocElem = parent.newChildElement("toc");

        final MultiPartArticle mparticle = (MultiPartArticle) item;
        final ArticleSectionCollection sections = mparticle.getSections();
        final PageNumber currentPage = getPageNumber(state);
        int sectionNr = 1;
        int pageNr = 1;
        ArticleSection section;
        Element sectionElem;
        while (sections.next()) {
            section = sections.getArticleSection();
            sectionElem = tocElem.newChildElement("section");
            sectionElem.setCDATASection(section.getTitle());
            final String link;
            if (currentPage.getPageNumber() == pageNr) {
                link = String.format("#section-%d", sectionNr);
            } else {
                link = String.format("?page=%d#section-%d", pageNr, sectionNr);
            }
            sectionElem.addAttribute("link", link);
            sectionElem.addAttribute("rank", section.getRank().toString());
            sectionNr++;
            if (section.isPageBreak()) {
                pageNr++;
            }
        }
    }

    protected void generateSectionXML(final PageState state,
                                      final Element parent,
                                      final ContentItem section,
                                      final XMLGenerator xmlGenerator) {
        final CMSExcursion excursion = new CMSExcursion() {

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

    protected ArticleSection[] getSections(final ContentItem item,
                                           final PageState state) {
//        PageNumber number = null;
//
//        String value = state.getRequest().getParameter(PAGE_NUMBER_PARAM);
//        if (value == null) {
//            value = "1";
//        }
//        number = new PageNumber(value);
//        if (number == null) {
//            number = new PageNumber("1");
//        }

        final PageNumber number = getPageNumber(state);

        MultiPartArticle mpa = (MultiPartArticle) item;
        if (!number.wantAllSections()) {
            return getSections(item, state, number.getPageNumber());
        } else {
            ArticleSectionCollection sections = mpa.getSections();
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
    protected ArticleSection[] getSections(final ContentItem item,
                                           final PageState state,
                                           final Integer number) {
        MultiPartArticle mpa = (MultiPartArticle) item;
        ArticleSectionCollection sections = mpa.getSections();
        int current = 1;
        int desired = number.intValue();

        ArrayList page;
        try {
            // Skip over sections until we hit the desired page
            while (current < desired) {
                if (!sections.next()) {

                    return new ArticleSection[]{};
                }
                ArticleSection section = (ArticleSection) sections
                    .getArticleSection();

                if (section.isPageBreak()) {
                    current++;
                }
            }

            // Now capture sections until we hit the next page (or end of sections)
            int subsequent = desired + 1;
            page = new ArrayList();
            while (current < subsequent) {
                if (!sections.next()) {
                    break;
                }
                ArticleSection section = (ArticleSection) sections.
                    getArticleSection();
                page.add(section);
                if (section.isPageBreak()) {
                    current++;
                }
            }
        } finally {
            sections.close();
        }

        return (ArticleSection[]) page.toArray(new ArticleSection[page.size()]);
    }

    /**
     * Try to get the section from the context if there isn't (eg if we are
     * looking at an index item on a category), guess the section from the item
     *
     * @param state
     * @param item
     *
     * @return
     */
    protected XMLGenerator getXMLGenerator(final PageState state,
                                           final ContentItem item) {
        ContentSection section = null;
        try {
            section = CMS.getContext().getContentSection();
        } catch (Exception e) {
        }
        if (section == null) {
            section = item.getContentSection();
            CMS.getContext().setContentSection(section);
        }
        return section.getXMLGenerator();
    }

    // A class representing either an Integer number indicating
    // the position in the list of sections, or the string 'all'
    private class PageNumber {

        private boolean m_all;
        private Integer m_number;

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
    /*private class PageParameter extends ParameterModel {
    
     public PageParameter(String name) {
     super(name);
     }
    
     public Object transformValue(HttpServletRequest request)
     throws IllegalArgumentException {
     return transformSingleValue(request);
     }
    
     public Object unmarshal(String encoded)
     throws IllegalArgumentException {
    
     if (encoded == null || encoded.length() == 0) {
     return null;
     }
     try {
     return new PageNumber(encoded);
     } catch (NumberFormatException e) {
     e.printStackTrace();
     throw new IllegalArgumentException(getName()
     + " should be a BigDecimal: '"
     + encoded + "'");
     }
     }
    
     public Class getValueClass() {
     return PageNumber.class;
     }
     }*/
}
