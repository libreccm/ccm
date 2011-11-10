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
package com.arsdigita.navigation.ui;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomizableContentItemXMLRenderer;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObjectTraversal;
import com.arsdigita.domain.DomainObjectTraversalAdapter;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * <p>
 * This is a more customizable variant of the {@link GreetingItem} component. 
 * It uses the {@link CustomizableContentItemXMLRenderer} for rendering the 
 * item.
 * </p>
 * <p>
 * This component provides an method to access the traversal adapter which is
 * used to control which properties of the greeting item are rendered and which
 * are not put into the XML output. You can create an completely new
 * adapter, or copy the one which is registered for the rendered item.
 * </p>
 * <p>
 * The manipulations are done in a JSP file. An example:
 * </p>
 * <pre>
 *  &lt;define:component name="greetingItem"
 *                       classname="com.arsdigita.navigation.ui.CustomizableGreetingItem"/&gt;
 *     &lt;jsp:scriptlet&gt;
 *        ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).loadTraversalAdapter("com.arsdigita.cms.contenttypes.SciOrganization",
 *                          SimpleXMLGenerator.ADAPTER_CONTEXT);
 *      ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).getTraversalAdapter().clearAssociationProperties();
 *      ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).getTraversalAdapter().addAssociationProperty("/object/persons");
 *      ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).setOrder("surname asc, givenname asc");
 *
 *       ((com.arsdigita.navigation.ui.CustomizableGreetingItem) greetingItem).setPageSize(5);
 *      &lt;/jsp:scriptlet&gt;
 * </pre>
 * <p>
 * As you might notice, you have to know the type of the greeting item to use 
 * the {@link #loadTraversalAdapter(String, String)} method if you want to
 * copy the traversal adapter.
 * </p>
 *
 * @see SimpleDomainObjectTraversalAdapter
 * @see CustomizableContentItemXMLRenderer
 *
 * @author Jens Pelzetter
 */
public class CustomizableGreetingItem extends GreetingItem {

    private static final Logger s_log = Logger.getLogger(
            CustomizableGreetingItem.class);
    private SimpleDomainObjectTraversalAdapter adapter;
    private int m_pageSize = 20;
    private String m_order;
    private List<String> m_filters;

    public CustomizableGreetingItem() {
        super();
        adapter = new SimpleDomainObjectTraversalAdapter();
        m_filters = new ArrayList<String>();

        /*adapter.setAssociationRule(
        SimpleDomainObjectTraversalAdapter.RULE_INCLUDE);
        adapter.setAttributeRule(SimpleDomainObjectTraversalAdapter.RULE_INCLUDE);
        adapter.addAttributeProperty("/object/title");
        adapter.addAttributeProperty("/object/name");
        adapter.addAttributeProperty("/object/addendum");
        adapter.addAttributeProperty("/object/organizationShortDescription");
        adapter.addAttributeProperty("/object/organizationDescription");
        adapter.addAssociationProperty("/object/persons");*/

        //loadTraversalAdapter("com.arsdigita.cms.contenttypes.SciOrganization",
        //                   SimpleXMLGenerator.ADAPTER_CONTEXT);

        //adapter.removeAssociationProperty("/object/persons");
    }

    /**
     * Set the page size to use for rendering association properties. Set to 0
     * or less to deactivated the paginator.
     *
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        m_pageSize = pageSize;
    }

    /**
     * Set an order for the rendered data associations.
     *
     * @param order
     */
    public void setOrder(String order) {
        m_order = order;
    }

    /**
     * Add a filter for the rendered data associations.
     *
     * @param filter
     */
    public void addFilter(String filter) {
        m_filters.add(filter);
    }

    /**
     * Copies the traversal adapter for <code>type</type> and
     * <code>context</code>. Caution: Do not manipulate the parent adapters!
     *
     * @param type
     * @param context
     */
    public void loadTraversalAdapter(String type, String context) {
        ObjectType objType;
        DomainObjectTraversalAdapter dotAdapter;

        s_log.debug(String.format("Loading traversal adapter for type '%s' "
                                  + "and context '%s'",
                                  type,
                                  context));
        objType = SessionManager.getMetadataRoot().getObjectType(type);
        dotAdapter = DomainObjectTraversal.findAdapter(objType, context);

        if (dotAdapter instanceof SimpleDomainObjectTraversalAdapter) {
            SimpleDomainObjectTraversalAdapter sourceAdapter;

            sourceAdapter = (SimpleDomainObjectTraversalAdapter) dotAdapter;

            //adapter = (SimpleDomainObjectTraversalAdapter) dotAdapter;

            adapter = new SimpleDomainObjectTraversalAdapter(sourceAdapter.
                    getParent());
            adapter.setAttributeRule(sourceAdapter.getAttributeRule());
            adapter.setAssociationRule(sourceAdapter.getAssociationRule());
            for (Object prop : sourceAdapter.getAttributeProperties()) {
                adapter.addAttributeProperty((String) prop);
            }

            for (Object prop : sourceAdapter.getAssociationProperties()) {
                adapter.addAssociationProperty((String) prop);
            }
        } else {
            s_log.warn(String.format("Registered traversal adapter for type "
                                     + "'%s' and context '%s' is not an instance "
                                     + "of SimpleDomainObjectTraversalAdapter "
                                     + "and therefore not "
                                     + "usable with this class. ",
                                     type,
                                     context));
        }

    }

    public SimpleDomainObjectTraversalAdapter getTraversalAdapter() {
        return adapter;
    }

    /**
     * Creates the XML for the GreetingItem using
     * {@link CustomizableContentItemXMLRenderer}. The adapter and the other
     * parameters for rendering are passed to the renderer.
     *
     * @param parent
     * @param item
     */
    @Override
    protected void generateGreetingItemXml(Element parent, ContentItem item) {
        CustomizableContentItemXMLRenderer renderer =
                                           new CustomizableContentItemXMLRenderer(
                parent);
        renderer.setWrapAttributes(true);
        renderer.setWrapRoot(false);
        renderer.setWrapObjects(false);
        
        renderer.setPageSize(m_pageSize);
        renderer.setOrder(m_order);
        renderer.setFilters(m_filters);
        
        renderer.walk(item, SimpleXMLGenerator.ADAPTER_CONTEXT, adapter);
        //renderer.walk(item, SimpleXMLGenerator.ADAPTER_CONTEXT);


    }
}
