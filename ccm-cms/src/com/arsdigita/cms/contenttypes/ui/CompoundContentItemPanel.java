/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.CMSContext;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.dispatcher.XMLGenerator;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public abstract class CompoundContentItemPanel
        extends SimpleComponent
        implements ExtraXMLGenerator {

    private static final Logger s_log = Logger.getLogger(
            CompoundContentItemPanel.class);
    /**
     * Item to show (if injected)
     */
    private ContentItem m_item = null;
    /**
     * Constant for the <code>show</code> parameter
     */
    private static final String SHOW_PARAM = "show";
    /**
     * Parameter which indicates which page to show
     */
    protected StringParameter m_show;
    private String showDefault;
    private boolean showOnlyDefault = false;
    private static final String PAGE_NUMBER = "pageNumber";
    /**
     * Parameter for a paginator
     */
    protected IntegerParameter m_pageNumber;
    /**
     * PageSize for the a paginator
     */
    private long m_pageSize = 30;

    public CompoundContentItemPanel() {
        super();

        m_show = new StringParameter(SHOW_PARAM);
        m_pageNumber = new IntegerParameter(PAGE_NUMBER);
    }

    @Override
    public void register(Page p) {
        super.register(p);

        addGlobalStateParams(p);
    }

    @Override
    public void addGlobalStateParams(Page p) {
        p.addGlobalStateParam(m_show);
        p.addGlobalStateParam(m_pageNumber);
    }

    protected XMLGenerator getXMLGenerator(PageState state, ContentItem item) {
        ContentSection section = null;

        try {
            section = CMS.getContext().getContentSection();
        } catch (Exception ex) {
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

    /**
     * Retrieves the the content item to show. If a item was injected via
     * {@link #setContentItem(com.arsdigita.cms.ContentItem)}, this item is
     * used. Otherwise, the item is retrieved from the CMSContext.
     *
     * @param state The current page state
     * @return The content item to show.
     */
    protected ContentItem getContentItem(PageState state) {
        if (m_item == null) {
            CMSContext context = CMS.getContext();

            if (!context.hasContentItem()) {
                return null;
            }
            return context.getContentItem().getLiveVersion();
        } else {
            return m_item;
        }
    }

    /**
     * Injects the content item to show. Very helpful when the CMSContext is not
     * available, for example in index pages.
     *
     * @param item The item to show by this panel.
     */
    public void setContentItem(final ContentItem item) {                
        if (item instanceof ContentBundle) {
            ContentBundle bundle;
            ContentItem resolved = null;

            bundle = (ContentBundle) item;

            resolved = bundle.getInstance(DispatcherHelper.getNegotiatedLocale().
                    getLanguage());

            if (resolved == null) {
                resolved = bundle.getPrimaryInstance();
            }

            m_item = resolved.getLiveVersion();
        } else {
            m_item = item;
        }
    }

    public long getPageSize() {
        return m_pageSize;
    }

    public void setPageSize(final long pageSize) {
        m_pageSize = pageSize;
    }

    protected long getPageCount(final long objectCount) {
        return (long) Math.ceil((double) objectCount / (double) m_pageSize);
    }

    protected long normalizePageNumber(final long pageCount,
                                       final long pageNumber) {
        long num;
        num = pageNumber;
        if (num < 1) {
            num = 1;
        }
        if (num > pageCount) {
            if (pageCount == 0) {
                num = 1;
            } else {
                num = pageCount;
            }
        }

        return num;
    }

    protected long getPaginatorBegin(final long pageNumber) {
        return (pageNumber - 1) * m_pageSize;
    }

    protected long getPaginatorCount(final long begin,
                                     final long objectCount) {
        return Math.min(m_pageSize, (objectCount - begin));
    }

    protected long getPaginatorEnd(final long begin, final long count) {
        long paginatorEnd = begin + count;
        if (paginatorEnd < 0) {
            paginatorEnd = 0;
        }
        return paginatorEnd;
    }
   
    protected void createPaginatorElement(final Element parent,
                                          final long pageNumber,
                                          final long pageCount,
                                          final long begin,
                                          final long end,
                                          final long count,
                                          final long size) {
        Element paginator;
        paginator =
        parent.newChildElement("nav:paginator",
                               "http://ccm.redhat.com/london/navigation");

        URL requestURL = Web.getContext().getRequestURL();

        ParameterMap map = new ParameterMap();

        if (requestURL.getParameterMap() != null) {
            Iterator<?> current = requestURL.getParameterMap().keySet().
                    iterator();
            while (current.hasNext()) {
                String key = (String) current.next();
                if (key.equals("pageNumber")) {
                    continue;
                }
                map.setParameterValues(key, requestURL.getParameterValues(
                        key));
            }
        }

        paginator.addAttribute("pageParam", "pageNumber");
        paginator.addAttribute("baseURL", URL.there(requestURL.getPathInfo(),
                                                    map).toString());
        paginator.addAttribute("pageNumber", Long.toString(pageNumber));
        paginator.addAttribute("pageCount", Long.toString(pageCount));
        paginator.addAttribute("pageSize", Long.toString(m_pageSize));
        paginator.addAttribute("objectBegin", Long.toString(begin + 1));
        paginator.addAttribute("objectEnd", Long.toString(end));
        paginator.addAttribute("objectCount", Long.toString(size));
    }

    /**
     * The default value for the show parameter.
     *
     * @return Default value for the show parameter.
     */
    protected final String getDefaultForShowParam() {
        if ((showDefault == null) || showDefault.isEmpty()) {
            return getDefaultShowParam();
        } else {
            return showDefault;
        }                
    }
    
    public void setDefaultForShowParam(final String showDefault) {
        this.showDefault = showDefault;
    }
    
    protected abstract String getDefaultShowParam();

    public boolean isShowOnlyDefault() {
        return showOnlyDefault;
    }
    
    public void setShowOnlyDefault(final boolean showOnlyDefault) {
        this.showOnlyDefault = showOnlyDefault;
    } 
    
    protected String getShowParam(final PageState state) {
        String show;
        try {
            show = (String) state.getValue(m_show);
        } catch (IllegalArgumentException ex) {
            // probably viewing an index item on a category,
            // get the parameter from the request and set it
            String value = state.getRequest().getParameter(SHOW_PARAM);
            if (value == null) {
                value = getDefaultForShowParam();
            }
            show = value;
            state.setValue(m_show, value);
        }
        if (show == null) {
            show = getDefaultForShowParam();
        }

        return show;
    }
    
    protected String getHttpParam(final String param, final PageState state) {
      final HttpServletRequest request = state.getRequest();
      String value;
      
      value = request.getParameter(param);
      
      return value;
    }

    protected long getPageNumber(final PageState state) {
        int pageNumber = 1;

        s_log.info("Checking page state...");
        if (state == null) {
            s_log.warn("PageState is null!!!");
        }

        try {
            Object value = state.getValue(m_pageNumber);
            if (value == null) {
                pageNumber = 1;
            } else {
                pageNumber = (Integer) value;
            }
        } catch (IllegalArgumentException ex) {
            String value = state.getRequest().getParameter(PAGE_NUMBER);
            if (value != null) {
                try {
                    pageNumber = Integer.parseInt(value);
                } catch (NumberFormatException ex1) {
                    s_log.warn("Invalid page number");
                }
                state.setValue(m_pageNumber, pageNumber);
            }
        }

        return (long) pageNumber;
    }

    /**
     * Returns the class of the item which can be displayed by this panel.
     *
     * @return The class which objects can be displayed using this panel
     */
    protected abstract Class<? extends ContentItem> getAllowedClass();

    protected String getPanelName() {
        return getAllowedClass().getSimpleName();
    }
    
    protected Element generateBaseXML(ContentItem item,
                                      Element parent,
                                      PageState state) {
        Element content = parent.newChildElement(
                String.format("cms:%sData", getPanelName()),
                CMS.CMS_XML_NS);

        exportAttributes(content);

        content.addAttribute("showing", getShowParam(state));

        return content;
    }

    /**
     * This method is called by CCM the start the rendering process.
     * It uses the returned value of {@link #getAllowedClass()} to check if
     * the item is of the correct type.
     *
     * @param state
     * @param parent
     */
    @Override
    public void generateXML(final PageState state, final Element parent) {
        ContentItem item = getContentItem(state);

        boolean isVisible = isVisible(state);
        Class<? extends ContentItem> klass = getAllowedClass();
        
        if (!isVisible(state)
            || (item == null)
            || !(item.getClass().equals(getAllowedClass()))) {
            s_log.warn("Skipping generate XML isVisible: " + isVisible(
                    state) + " item "
                        + (item == null ? null : item.getOID()));
            return;
        }

        if (state == null) {
            s_log.warn("No page state provided!");
        }

        generateXML(item, parent, state);
    }
}
