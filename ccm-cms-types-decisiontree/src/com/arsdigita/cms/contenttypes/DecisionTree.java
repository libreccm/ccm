/*
 * Copyright (C) 2007 Red Hat Inc. All Rights Reserved.
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

import java.math.BigDecimal;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.cms.dispatcher.XMLGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.xml.Element;

/**
 * The Decision Tree content type.
 *
 * This content type has been contributed by Camden.
 * 
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTree extends ContentPage implements XMLGenerator {

    public static final Logger s_log = Logger.getLogger(DecisionTree.class);

    public static final String ADAPTER_CONTEXT = SimpleXMLGenerator.class.getName();
    
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.DecisionTree";
    
    public static final String CANCEL_URL        = "cancelURL";
    public static final String PARAM_RETURN_URL  = "return_url";
    public static final String FIRST_SECTION     = "firstSection";
    public static final String SECTIONS          = "sections";
    public static final String PARAM_SECTION_ID  = "section_id";
    public static final String PARAM_SECTION_OID = "section_oid";
    public static final String BUTTON_CANCEL     = "cancel";
    public static final String BUTTON_NEXT       = "next";

    public DecisionTree() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public DecisionTree(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public DecisionTree(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public DecisionTree(DataObject obj) {
        super(obj);
    }

    public DecisionTree(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this item. Child classes
     * should override this method to return the correct value.
     */
    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public String getCancelURL() {
    	return (String) get(CANCEL_URL);
    }
    
    public void setCancelURL(String value) {
    	set(CANCEL_URL, value);
    }
    
    public DecisionTreeSection getFirstSection() {
    	DataObject dataObject = (DataObject) get(FIRST_SECTION);
        if (dataObject == null) { return null; }
        return new DecisionTreeSection(dataObject);
    }
    
    public void setFirstSection(DecisionTreeSection value) {
    	setAssociation(FIRST_SECTION, value);
    }
    
    /**
     * Add the specified TreeSection to this object. If there are no other sections,
     * mark this one as "first", which makes it appear automatically when the 
     * content item is viewed.
     *
     * @param section the TreeSection to add
     */
    public void addSection(DecisionTreeSection section) {
    	DecisionTreeSectionCollection sections = getSections();
    	boolean isFirst = sections.isEmpty();
        add(SECTIONS, section);
        if (isFirst)
        	setFirstSection(section);
    }

    public DecisionTreeSectionCollection getSections() {
    	DecisionTreeSectionCollection sections = 
                new DecisionTreeSectionCollection((DataCollection)get(SECTIONS));
    	sections.addOrder(TITLE);
        return sections;
    }

    /**
     * Remove the given TreeSection from this object.
     *
     * @param section the TreeSection to remove
     */
    public void removeSection(DecisionTreeSection section) {
        section.delete();
    }

    public DecisionTreeSectionOptionCollection getOptions() {
    	Session ssn = SessionManager.getSession();
    	DataCollection collection = ssn.retrieve(DecisionTreeSectionOption
                                                 .BASE_DATA_OBJECT_TYPE);
    	
    	DecisionTreeSectionOptionCollection options = new DecisionTreeSectionOptionCollection(collection);
    	options.addEqualsFilter("treeSection.tree.id", getID());
    	options.addOrder("treeSection.title, rank, label");
    	return options;
    }

    public DecisionTreeOptionTargetCollection getTargets() {
    	Session ssn = SessionManager.getSession();
    	DataCollection collection = ssn.retrieve(DecisionTreeOptionTarget
                                                 .BASE_DATA_OBJECT_TYPE);
    	
    	DecisionTreeOptionTargetCollection options = new 
                DecisionTreeOptionTargetCollection(collection);
    	options.addEqualsFilter("matchOption.treeSection.tree.id", getID());
    	options.addOrder("matchOption.treeSection.title, matchOption.rank, matchOption.label");
    	return options;
    }
    
    /**
     * Given a URL parameter name, determine if that parameter should be included
     * in the HTTP request for the next section.
     * 
     * @param parameterName the name of the URL parameter to check
     * @return true if the parameter is to be included in the next section request
     */
    public static boolean preserveParameter(String parameterName) {
    	return (!PARAM_SECTION_ID.equals(parameterName) &&
    			!PARAM_SECTION_OID.equals(parameterName) &&
    			!PARAM_RETURN_URL.equals(parameterName) &&
    			!BUTTON_CANCEL.equals(parameterName) &&
    			!BUTTON_NEXT.equals(parameterName) &&
    			!BaseApplicationServlet.APPLICATION_ID_PARAMETER.equals(parameterName));
    }
    
    /**
     * Generate the XML for a DecisionTree. In addition to what SimpleXMLGenerator does,
     * this also stores information about the current request in an XML element.
     */
	public void generateXML(PageState state, Element parent, String useContext) {
		ContentItem item = CMS.getContext().getContentItem();

        Element content = new Element("cms:item", CMS.CMS_XML_NS);
        if (useContext != null)
        	content.addAttribute("useContext", useContext);

        Element customInfo = new Element("customInfo");
        customInfo.addAttribute("currentURL", state.getRequestURI());
        content.addContent(customInfo);

        // Add the URL parameters to the XML output, so we can include
        // them has hidden form fields.
        HttpServletRequest request = state.getRequest();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
        	String name = (String) parameterNames.nextElement();
        	
        	// Don't include the section_id parameter - that's
        	// not to be passed from section to section.
        	if (!preserveParameter(name))
        		continue;
        	
        	Element parameters = new Element("parameters");
        	parameters.addAttribute("name", name);
        	parameters.addAttribute("value", request.getParameter(name));
        	
        	content.addContent(parameters);
        }
        
        DomainObjectXMLRenderer renderer =
            new DomainObjectXMLRenderer(content);

        renderer.setWrapAttributes(true);
        renderer.setWrapRoot(false);
        renderer.setWrapObjects(false);
        renderer.setRevisitFullObject(true);

        renderer.walk(item, ADAPTER_CONTEXT);

        parent.addContent(content);
	}    

	/**
	 * Override ContentItem.copyProperty so that firstSection is handled
	 * like a component.
	 */
	public boolean copyProperty(CustomCopy source, Property property,
			ItemCopier copier) {
		String attribute = property.getName();
		if (FIRST_SECTION.equals(attribute)) {
			// We don't copy the FIRST_SECTION property here, because it's not marked
			// as a component, and would cause a PublishedLink to be created, which
			// results in performance problems during publication. Instead, we will 
			// set the FIRST_SECTION property as part of the copying of the SECTIONS 
			// property, see below.
			return true;
		}
		
		if (SECTIONS.equals(attribute)) {
			DecisionTree srcTree = (DecisionTree)source;

			// We make sure that all objects we need below exist by copying
			// the TreeSections associated with the source DecisionTree.
			// The copier will store the OIDs of the copies, and won't
			// recreate them when it does its own thing.
			DecisionTreeSectionCollection srcSections = srcTree.getSections();
			while (srcSections.next())
				copier.copy(source, this, srcSections.getSection(), property);
			
			DecisionTreeSection srcFirstSection = srcTree.getFirstSection(); 
			if (srcFirstSection != null) {
				// At this point, all associated TreeSections have been copied,
				// so we can be sure that ItemCopier.getCopy() will return the
				// copied TreeSection object.
				setFirstSection((DecisionTreeSection)copier.getCopy(srcFirstSection.getOID()));
			}
			
			// Next, we go through all OptionTargets in the source object 
			// and set the TARGET_SECTION property of the corresponding
			// destination DecisionTreeOptionTarget. Like FIRST_SECTION, the TARGET_SECTION
			// property was skipped when the OptionTargets were copied.
			DecisionTreeOptionTargetCollection srcTargets = srcTree.getTargets();
			while (srcTargets.next()) {
				DecisionTreeOptionTarget srcTarget = srcTargets.getTarget();
				DecisionTreeSection srcSection = srcTarget.getTargetSection();

				if (srcSection == null) continue;
				
				DecisionTreeOptionTarget dstTarget = (DecisionTreeOptionTarget)copier.getCopy(srcTarget.getOID());
				dstTarget.setTargetSection((DecisionTreeSection)copier.getCopy(srcSection.getOID()));
			}
		}
		
		return super.copyProperty(source, property, copier);
	}
}
