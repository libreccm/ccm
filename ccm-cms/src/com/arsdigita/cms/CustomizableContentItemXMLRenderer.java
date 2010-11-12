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
package com.arsdigita.cms;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectTraversalAdapter;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * A renderer which allows some customization of the rendering of
 * data associations form the using component and therefore from a JSP file.
 *
 * @author Jens Pelzetter
 */
public class CustomizableContentItemXMLRenderer
        extends DomainObjectXMLRenderer {

    private static final Logger s_log = Logger.getLogger(
            CustomizableContentItemXMLRenderer.class);
    private int m_pageSize = 20;
    private String m_order;
    private List<String> m_filters;    

    public CustomizableContentItemXMLRenderer(Element root) {
        super(root);
        m_filters = new ArrayList<String>();
    }

    public void setPageSize(int pageSize) {
        m_pageSize = pageSize;
    }

    /**
     * Set an order for the rendered data association(s).
     *
     * @param order
     */
    public void setOrder(String order) {
        m_order = order;
    }

    /**
     * Sets some filters for the rendered data associations.
     *
     * @param filters
     */
    public void setFilters(List<String> filters) {
        m_filters = filters;
    }

    /**
     * Adds a filter to use for the rendered data associations.
     *
     * @param filter
     */
    public void addFilter(String filter) {
        m_filters.add(filter);
    }

    /**
     * Public variant of the
     * {@link DomainObjectTraversal#walk(DomainObject, String, DomainObjectTraversalAdapter)}
     * method. This allows it to pass an customized
     * {@link DomainObjectTraversalAdapter}  to the renderer which is than used
     * instead of the registered <code>TraversalAdapter</code>.
     *
     * @param dobj
     * @param context
     * @param adapter
     */
    @Override
    public void walk(final DomainObject dobj,
                     final String context,
                     final DomainObjectTraversalAdapter adapter) {
        super.walk(dobj, context, adapter);
    }

    /**
     * Copied from {@link ContentItemXMLRenderer}.
     *
     * @param adapter
     * @param obj
     * @param path
     * @param context
     * @param linkObject
     */
    @Override
    protected void walk(final DomainObjectTraversalAdapter adapter,
                        final DomainObject obj,
                        final String path,
                        final String context,
                        final DomainObject linkObject) {

        DomainObject nObj = obj;

        if (nObj instanceof ContentBundle) {

            nObj = ((ContentBundle) obj).negotiate(DispatcherHelper.getRequest().
                    getLocales());
        }


        super.walk(adapter, nObj, path, context, linkObject);
    }

    /**
     * Renders data associations using the provided page size to create a
     * paginator (if the page size is greater than 0, set the page size to 0 or
     * less to disable the paginator) and the provided order and filters to
     * render data associations.
     *
     * @param adapter
     * @param obj
     * @param path
     * @param context
     * @param prop
     * @param propName
     * @param propValue
     */
    @Override
    protected void walkDataAssociations(DomainObjectTraversalAdapter adapter,
                                        DomainObject obj,
                                        String path,
                                        String context,
                                        Property prop,
                                        String propName,
                                        Object propValue) {
        s_log.debug(String.format("%s is a DataAssociation", prop.getName()));

        if (m_pageSize <= 0) {
            s_log.debug("pageSize is 0 or less, not using Paginator.");
            super.walkDataAssociations(adapter,
                                       obj,
                                       path,
                                       context,
                                       prop,
                                       propName,
                                       propValue);
        } else {
            s_log.debug(String.format("Rendering DataAssociation using a "
                                      + "paginator with a "
                                      + "page size of %d", m_pageSize));

            beginAssociation(obj, path, prop);

            DataAssociationCursor daCursor = ((DataAssociation) propValue).
                    getDataAssociationCursor();

            if (daCursor.size() == 0) {
                s_log.debug("No items to show, finishing assoication and "
                            + "returning.");
                endAssociation(obj, path, prop);
                return;
            }

            s_log.debug("Walking daCursor...");
            s_log.debug(String.format("daCursor.size() = %d", daCursor.size()));

            //Add filters and order
            if (m_order != null) {
                daCursor.addOrder(m_order);
            }
            for (String filter : m_filters) {
                daCursor.addFilter(filter);
            }

            //Build paginator

            //Get pageNumber from request URL
            URL requestUrl = Web.getContext().getRequestURL();
            String pageNumberValue = requestUrl.getParameter("pageNumber");
            long pageNumber;
            if (pageNumberValue == null) {
                pageNumber = 1;
            } else {
                pageNumber = Long.parseLong(pageNumberValue);
            }

            long objectCount = daCursor.size();
            int pageCount = (int) Math.ceil((double) objectCount
                                            / (double) m_pageSize);

            if (pageNumber < 1) {
                pageNumber = 1;
            }

            if (pageNumber > pageCount) {
                if (pageCount == 0) {
                    pageNumber = 1;
                } else {
                    pageNumber = pageCount;
                }
            }

            long begin = ((pageNumber - 1) * m_pageSize);
            int count = (int) Math.min(m_pageSize, (objectCount - begin));
            long end = begin + count;

            s_log.debug("Calculted the following values for the paginator:");
            s_log.debug(String.format("pageNumber  = %d", pageNumber));
            s_log.debug(String.format("objectCount = %d", objectCount));
            s_log.debug(String.format("pageCount   = %d", pageCount));
            s_log.debug(String.format("begin       = %d", begin));
            s_log.debug(String.format("count       = %d", count));
            s_log.debug(String.format("end         = %d", end));

            if (count != 0) {
                daCursor.setRange(new Integer((int) begin + 1),
                                  new Integer((int) end + 1));
            }

            /*Element paginator = newElement(getCurrentElement(),
            "nav:paginator");*/
            Element paginator =
                    new Element("nav:paginator",
                                "http://ccm.redhat.com/london/navigation");
            getCurrentElement().addContent(paginator);

            ParameterMap map = new ParameterMap();

            if (requestUrl.getParameterMap() != null) {
                Iterator current = requestUrl.getParameterMap().keySet().
                        iterator();
                while (current.hasNext()) {
                    String key = (String) current.next();
                    if (key.equals("pageNumber")) {
                        continue;
                    }
                    map.setParameterValues(key, requestUrl.getParameterValues(
                            key));
                }
            }

            paginator.addAttribute("pageParam", "pageNumber");
            paginator.addAttribute("baseURL",
                                   URL.there(requestUrl.getPathInfo(),
                                             map).toString());
            paginator.addAttribute("pageNumber",
                                   new Long(pageNumber).toString());
            paginator.addAttribute("pageCount",
                                   new Long(pageCount).toString());
            paginator.addAttribute("pageSize", new Long(m_pageSize).toString());
            paginator.addAttribute("objectBegin", new Long(begin + 1).toString());
            paginator.addAttribute("objectEnd", new Long(end).toString());
            paginator.addAttribute("objectCount",
                                   new Long(objectCount).toString());

            int index = 0;
            while (daCursor.next()) {
                DataObject dobj = daCursor.getDataObject();
                ACSObject object = null;
                object = (ACSObject) DomainObjectFactory.newInstance(dobj);

                //Element item = newElement(paginator, "item");
                //Element item = newElement(paginator, propName);
                //appendToPath(path, "paginator");
                //Element itemPath = newElement(item, "path");
                //itemPath.setText(getStableURL(dobj, object));

                //generateItemXML(item, dobj, object, index);
                DataObject link = daCursor.getLink();
                DomainObject linkObj = null;
                if (link != null) {
                    linkObj = new LinkDomainObject(link);
                }
                walk(adapter,
                     DomainObjectFactory.newInstance(daCursor.getDataObject()),
                     appendToPath(path, propName),
                     context,
                     linkObj);

                index++;
            }

            //endPaginator();
            endAssociation(obj, path, prop);
        }
    }

    protected String getStableURL(DataObject dobj, ACSObject obj) {
        OID oid = new OID((String) dobj.get(ACSObject.OBJECT_TYPE),
                          dobj.get(ACSObject.ID));

        return String.format("/redirect/?oid=%s", oid.toString());
    }

    /*protected void generateItemXML(Element item,
                                   DataObject dobj,
                                   ACSObject obj,
                                   int index) {
        if (obj != null) {
            ContentItemXMLRenderer renderer = new ContentItemXMLRenderer(item);
            renderer.setRevisitFullObject(false);
            renderer.setWrapAttributes(true);
            renderer.setWrapRoot(false);
            renderer.setWrapObjects(false);
            renderer.walk(obj, SimpleXMLGenerator.ADAPTER_CONTEXT);
        }
    }*/

    /*protected Element beginPaginator() {
    Element element = newElement(getCurrentElement(), "nav:paginator");
    getElementStack().push(getCurrentElement());
    setCurrentElement(element);
    return element;
    }

    protected Element endPaginator() {
    setCurrentElement((Element) getElementStack().pop());
    return getCurrentElement();
    }*/
}
