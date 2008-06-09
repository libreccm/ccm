/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.dispatcher;


import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.UserDefinedContentItem;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectTraversal;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.domain.SimpleDomainObjectTraversalAdapter;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

import java.util.Iterator;


/**
 * <p>The default <tt>XMLGenerator</tt> implementation.</p>
 *
 * @author Michael Pih
 * @version $Revision: #20 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class SimpleXMLGenerator implements XMLGenerator {

    public static final String versionId = "$Id: SimpleXMLGenerator.java 1651 2007-09-18 10:30:06Z chrisg23 $ by $Author: chrisg23 $, $DateTime: 2004/08/17 23:15:09 $";

    private static Logger s_log =
        Logger.getLogger(SimpleXMLGenerator.class);

    public static final String ADAPTER_CONTEXT = SimpleXMLGenerator.class.getName();

    // Register general purpose adaptor for all content items
    static {
        SimpleDomainObjectTraversalAdapter adapter =
            new SimpleDomainObjectTraversalAdapter();
        adapter.addAssociationProperty("/object/type");
        adapter.addAssociationProperty("/object/categories");

        DomainObjectTraversal.registerAdapter(
            ContentItem.BASE_DATA_OBJECT_TYPE,
            adapter,
            ADAPTER_CONTEXT
        );
    }

    public SimpleXMLGenerator() {}

    /**
     * Generates the XML to render the content panel.
     *
     * @param state  The page state
     * @param parent The parent DOM element
     * @param useContext The use context
     */
    public void generateXML(PageState state, Element parent, String useContext) {

        ContentSection section = CMS.getContext().getContentSection();
        ContentItem item = getContentItem(state);

        s_log.info("Generate XML for item " + item.getOID());

		
		Party currentParty = Kernel.getContext().getParty();
		if (currentParty == null) {
			currentParty = Kernel.getPublicUser();
		}
		// check if current user can edit the current item (nb privilege is granted on draft item, but live item 
		// has draft as its permission context
		//
		// Note that the xml that is generated is only of use if you DO NOT CACHE content pages. 
		// cg. 

		PermissionDescriptor edit = new PermissionDescriptor(PrivilegeDescriptor.get(SecurityManager.CMS_EDIT_ITEM), item, currentParty);
		if (PermissionService.checkPermission(edit)) {
			parent.addAttribute("canEdit", "true");
		}
		PermissionDescriptor publish = new PermissionDescriptor(PrivilegeDescriptor.get(SecurityManager.CMS_PUBLISH), item, currentParty);
		if (PermissionService.checkPermission(publish)) {
			parent.addAttribute("canPublish", "true");
		}
        String className = item.getDefaultDomainClass();

        // Ensure correct subtype of ContentItem is instantiated
        if (!item.getClass().getName().equals(className)) {
            s_log.info("Specializing item");
            try {
                item = (ContentItem)DomainObjectFactory
                    .newInstance(new OID(item.getObjectType().getQualifiedName(),
                                         item.getID()));
            } catch (DataObjectNotFoundException ex) {
                throw new UncheckedWrapperException( 
                    (String)GlobalizationUtil.globalize(
                        "cms.dispatcher.cannot_find_domain_object"
                    ).localize(),  ex);
            }
        }

        // Implementing XMLGenerator directly is now deprecated
        if ( item instanceof XMLGenerator) {
            s_log.info("Item implements XMLGenerator interface");
            XMLGenerator xitem = (XMLGenerator)item;
            xitem.generateXML(state, parent, useContext);

        } else if (className.equals("com.arsdigita.cms.UserDefinedContentItem")) {
            s_log.info("Item is a user defined content item");
            UserDefinedContentItem UDItem = (UserDefinedContentItem)item;
            generateUDItemXML(UDItem, state, parent, useContext);

        } else {
            s_log.info("Item is using DomainObjectXMLRenderer");
 
            // This is the preferred method
            Element content = startElement(useContext);

            DomainObjectXMLRenderer renderer =
                new DomainObjectXMLRenderer(content);

            renderer.setWrapAttributes(true);
            renderer.setWrapRoot(false);
            renderer.setWrapObjects(false);
            renderer.setRevisitFullObject(true);

            renderer.walk(item, ADAPTER_CONTEXT);

            parent.addContent(content);
        }
    }


    /**
     * Fetches the current content item. This method can be overidden to
     * fetch any {@link com.arsdigita.cms.ContentItem}, but by default,
     * it fetches the <code>ContentItem</code> that is set in the page state
     * by the dispatcher.
     *
     * @param state The page state
     * @return A content item
     */
    protected ContentItem getContentItem(PageState state) {
        if (CMS.getContext().hasContentItem()) {
            return CMS.getContext().getContentItem();
        } else {
            CMSPage page = (CMSPage) state.getPage();
            return page.getContentItem(state);
        }
    }

    protected void generateUDItemXML(UserDefinedContentItem UDItem,
                                     PageState state,
                                     Element parent,
                                     String useContext) {

        Element element = startElement(useContext);
        Element additionalAttrs = UDItemElement(useContext);

        element.addAttribute("type", UDItem.getContentType().getLabel());
        element.addAttribute("id", UDItem.getID().toString());
        element.addAttribute("name", UDItem.getName());
        element.addAttribute("title", UDItem.getTitle());
        element.addAttribute("javaClass", UDItem.getContentType().getClassName());

        DynamicObjectType dot = new DynamicObjectType(
            UDItem.getSpecificObjectType()
        );
        Iterator declaredProperties = dot.getObjectType()
            .getDeclaredProperties();
        Property currentProperty = null;
        Object value = null;
        while (declaredProperties.hasNext()) {
            currentProperty = (Property) declaredProperties.next();
            value = (Object) UDItem.get(currentProperty.getName());
            if (value != null) {
                element.addContent(
                    UDItemAttrElement(currentProperty.getName(), 
                                      value.toString()));
            } else {
                element.addContent(
                    UDItemAttrElement(currentProperty.getName(), 
                                      "none specified"));
            }
        }

        //element.addContent(additionalAttrs);
        parent.addContent(element);

    }



    private Element startElement(String useContext) {
        Element element = new Element("cms:item", CMS.CMS_XML_NS);
        if ( useContext != null ) {
            element.addAttribute("useContext", useContext);
        }
        return element;
    }

    private Element UDItemElement(String useContext) {
        Element element = new Element("cms:UDItemAttributes", CMS.CMS_XML_NS);
        /*
          if ( useContext != null ) {
          element.addAttribute("useContext", useContext);
          }
        */
        return element;
    }

    private Element UDItemAttrElement(String name, String value) {
        Element element = new Element("cms:UDItemAttribute", CMS.CMS_XML_NS);
        element.addAttribute("UDItemAttrName", name);
        element.addAttribute("UDItemAttrValue", value);
        return element;
    }

}
