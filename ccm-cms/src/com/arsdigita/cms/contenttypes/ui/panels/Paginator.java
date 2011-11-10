package com.arsdigita.cms.contenttypes.ui.panels;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Paginator class for the classes implementing 
 * {@link com.arsdigita.cms.contenttypes.ui.GenericOrgaUnitTab}.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class Paginator {

    private static final String PAGE_NUMBER = "pageNumber";
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
            logger.debug("No pageNumber parameter in request setting page number"
                         + " to 1.");
            pageNumber = 1;
        } else {
            pageNumber = Integer.parseInt(pageNumberStr);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("pageNumber = %d", pageNumber));
        }
        this.objectCount = objectCount;
        this.pageSize = pageSize;

        normalizePageNumber();
    }

    public void generateXml(final Element parent) {
        final Element paginatorElem = parent.newChildElement(
                "nav:paginator", "http://ccm.redhat.com/navigation");

        final URL requestUrl = Web.getContext().getRequestURL();

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
        query.setRange(getBegin(), getEnd());
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

    private int getBegin() {
        if (pageNumber == 1) {
            return 1;
        } else {
            return (pageNumber - 1) * pageSize;
        }
    }

    private int getCount() {
        return Math.min(pageSize, (objectCount - getBegin()));
    }

    private int getEnd() {
        int paginatorEnd = getBegin() + getCount();
        if (paginatorEnd < 0) {
            paginatorEnd = 0;
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
