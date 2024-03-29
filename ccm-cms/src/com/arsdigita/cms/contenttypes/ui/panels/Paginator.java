/*
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
package com.arsdigita.cms.contenttypes.ui.panels;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Paginator class for the classes implementing
 * {@link com.arsdigita.cms.contenttypes.ui.GenericOrgaUnitTab}.
 *
 * @author Jens Pelzetter
 */
public class Paginator {

    private static final String PAGE_NUMBER = "pageNumber";
    private static final String PAGE_SIZE = "pageSize";
    private final int pageSize;
    private int pageNumber;
    private final int objectCount;
    private final Logger logger = Logger.getLogger(Paginator.class);

    public Paginator(final HttpServletRequest request,
                     final int objectCount) {
        this(request, objectCount, 30);
    }

    public Paginator(final HttpServletRequest request,
                     final int objectCount,
                     final int pageSize) {
        final String pageNumberStr = request.getParameter(PAGE_NUMBER);
        if (pageNumberStr == null) {
            logger.debug("No pageNumber parameter in request setting page number to 1.");
            pageNumber = 1;
        } else {
            pageNumber = Integer.parseInt(pageNumberStr);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("pageNumber = %d", pageNumber));
        }
        this.objectCount = objectCount;

        if (request.getParameter(PAGE_SIZE) == null) {
            this.pageSize = pageSize;
        } else {
            final String pageSizeStr = request.getParameter(PAGE_SIZE);
            this.pageSize = Integer.parseInt(pageSizeStr);
        }

        normalizePageNumber();
    }

    public void generateXml(final Element parent) {
        final Element paginatorElem = parent.newChildElement(
                "nav:paginator", "http://ccm.redhat.com/navigation");

        final URL requestUrl = Web.getWebContext().getRequestURL();

        final ParameterMap parameters = new ParameterMap();

        if (requestUrl.getParameterMap() != null) {
            final Iterator<?> current = requestUrl.getParameterMap().keySet().
                    iterator();
            while (current.hasNext()) {
                copyParameter(requestUrl, (String) current.next(), parameters);
            }
        }

        paginatorElem.addAttribute("pageParam", PAGE_NUMBER);
        paginatorElem.addAttribute("baseURL", URL.there(requestUrl.getPathInfo(),
                                                        parameters).toString());
        paginatorElem.addAttribute("pageNumber", Integer.toString(pageNumber));
        paginatorElem.addAttribute("pageCount", Integer.toString(getPageCount()));
        paginatorElem.addAttribute("pageSize", Integer.toString(pageSize));
        paginatorElem.addAttribute("objectBegin", Integer.toString(getBegin()));
        paginatorElem.addAttribute("objectEnd", Integer.toString(getEnd()));
        paginatorElem.addAttribute("objectCount", Integer.toString(objectCount));
    }

    public void applyLimits(final DataQuery query) {
        if (logger.isDebugEnabled()) {
            logger.debug("Paginator values: ");
            logger.debug(String.format(" objectCount = %d", objectCount));
            logger.debug(String.format("    begin    = %d", getBegin()));
            logger.debug(String.format("      end    = %d", getEnd()));
            logger.debug(String.format("pageCount    = %d", getPageCount()));
            logger.debug(String.format("    count    = %d", getCount()));
        }

        logger.debug(String.format("Applying limits: %d, %d",
                                   getBegin(),
                                   getEnd()));
        query.setRange(getBegin(), getEnd() + 1);
    }        
    
    public <T> List<T> applyListLimits(final List<T> list, final Class<T> type) {
        int begin = getBegin() - 1;
        if (begin < 0) {
            begin = 0;
        }
        int end = getEnd();
        if (end >= list.size()) {
            end = list.size();
        }
        return list.subList(begin, end);
    }
    
    public List<?> applyListLimits(final List<?> list) {
        int begin = getBegin() - 1;
        if (begin < 0) {
            begin = 0;
        }
        int end = getEnd();
        if (end >= list.size()) {
            end = list.size();
        }
        return list.subList(begin, end);
    }

    public int getPageCount() {
        return (int) Math.ceil((double) objectCount / (double) pageSize);
    }

    private int normalizePageNumber() {
        final int pageCount = getPageCount();

        int num;
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

    public int getBegin() {
        if (pageNumber == 1) {
            return 1;
        } else {
            return ((pageNumber - 1) * pageSize) + 1;
        }
    }

    public int getCount() {
        return Math.min(pageSize, (objectCount - getBegin() + 1));
    }

    public int getEnd() {
        int paginatorEnd = getBegin() + getCount() - 1;
        if (paginatorEnd < 0) {
            paginatorEnd = 0;
        }
        if (paginatorEnd <= getBegin()) {
            paginatorEnd = (getBegin() + 1);
        }
        return paginatorEnd;
    }

    private void copyParameter(final URL requestUrl,
                               final String key,
                               final ParameterMap parameters) {
        if (PAGE_NUMBER.equals(key)) {
            return;
        } else {
            parameters.setParameterValues(key,
                                          requestUrl.getParameterValues(key));
        }
    }
}
