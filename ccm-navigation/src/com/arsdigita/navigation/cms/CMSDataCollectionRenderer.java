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
 */
package com.arsdigita.navigation.cms;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentItemXMLRenderer;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.navigation.DataCollectionRenderer;
import com.arsdigita.navigation.Navigation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.xml.Element;

public class CMSDataCollectionRenderer extends DataCollectionRenderer {
    
    private boolean useExtraXml = true;
    
    public CMSDataCollectionRenderer() {
        addAttribute("masterVersion.id");
    }

    public boolean getUseExtraXml() {
        return useExtraXml;
    }
    
    public void setUseExtraXml(final boolean useExtraXml) {
        this.useExtraXml = useExtraXml;
    }
    
    protected String getStableURL(DataObject dobj,
                                  ACSObject obj) {
        if (obj == null) {
            obj = (ACSObject) DomainObjectFactory.newInstance(dobj);
        }
        if (obj instanceof ContentItem) {
            OID oid = ((ContentItem) obj).getDraftVersion().getOID();
            return Navigation.redirectURL(oid);
        }
        return super.getStableURL(dobj, obj);
    }

    @Override
    protected void generateItemXML(Element item,
                                   DataObject dobj,
                                   ACSObject obj,
                                   int index) {
        if (obj != null) {            
            ContentItemXMLRenderer renderer = new ContentItemXMLRenderer(item);
            renderer.setRevisitFullObject(false);
            renderer.setWrapAttributes(true);
            renderer.setWrapRoot(false);
            renderer.setWrapObjects(false);
            //renderer.walk(obj, SimpleXMLGenerator.ADAPTER_CONTEXT);
            /* jensp 2011-01-03: 
             * I needed the option to use different traversal adapters for
             * the object in the detail view and the list view. It is now 
             * possible to set the adapter context used from a JSP template, 
             * using DataCollectionRenderer#setSpecializeObjectsContext(String).
             */          
            renderer.walk(obj, getSpecializeObjectsContext());            
            
            if ((obj instanceof ContentItem) && useExtraXml) {
                final ContentItem contentItem = (ContentItem) obj;
                
                for(ExtraXMLGenerator generator : contentItem.getExtraListXMLGenerators()) {
                    generator.setListMode(true);
                    generator.generateXML(contentItem, item, null);
                }
                 
            }                       
        }
    }
}
