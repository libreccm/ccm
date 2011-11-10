/**
 * This is a specialised version of com.arsdigita.cms.ui.authoring.CategoryWidget
 * which adds an isEmpty attribute to the category elements.
 */
package com.arsdigita.navigation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.TemplateContext;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;
import com.arsdigita.london.terms.Domain;

public class CategoryWidget extends com.arsdigita.cms.ui.authoring.CategoryWidget {

	public CategoryWidget(String name,BigDecimalParameter root,StringParameter mode) {
		super(name, root, mode);
	}
	
    public void generateWidget(String name, Element parent) {
    	BigDecimal theRoot = getCategoryForName(name);
    	
    	Element widget = parent.newChildElement("cms:categoryWidget",CMS.CMS_XML_NS);
        exportAttributes(widget);

        widget.addAttribute("name", getName());

        Set ids = new HashSet();
    
        Category root = (Category)DomainObjectFactory.newInstance(
            new OID(Category.BASE_DATA_OBJECT_TYPE,theRoot)
        );

        CategoryCollection cats = root.getDescendants();
        cats.addEqualsFilter("parents.link.relationType", "child");
        cats.addPath("parents.link.sortKey");
        cats.addPath("parents.id");
        
        Map children = new HashMap();
        while (cats.next()) {
            Category cat = cats.getCategory();
            BigDecimal parentID = (BigDecimal)cats.get("parents.id");
            
            List childList = (List)children.get(parentID);
            if (childList == null) {
                childList = new ArrayList();
                children.put(parentID, childList);
            }
            
            childList.add(new CategorySortKeyPair(cat,(BigDecimal)cats.get("parents.link.sortKey")));
        }
        
        generateCategory(widget, null, root, null, ids, children);
    }

    public void generateCategory(Element parent,
            String path,
            Category cat,
            BigDecimal sortKey,
            Set selected,
            Map children) {
    	Element el = new Element("cms:category",CMS.CMS_XML_NS);

    	el.addAttribute("id", XML.format(cat.getID()));
    	el.addAttribute("name", cat.getName());
    	el.addAttribute("description", cat.getDescription());
    	el.addAttribute("isSelected", selected.contains(cat.getID()) ? "1" : "0");
    	el.addAttribute("isAbstract", cat.isAbstract() ? "1" : "0");
    	el.addAttribute("isEnabled", cat.isEnabled() ? "1" : "0");

    	// Find out if this category is empty or not...
    	ACSObjectCollection result = new ACSObjectCollection(SessionManager.getSession().retrieve(ContentItem.BASE_DATA_OBJECT_TYPE));
    	result.addEqualsFilter("categories.roTransParents."+ID, cat.getID());
    	result.addEqualsFilter("version", "live");
    	el.addAttribute("isEmpty",(int)result.size()==0 ? "1" : "0");
    	if (sortKey != null) {
    		el.addAttribute("sortKey", sortKey.toString());
    	}

    	String fullname = path == null ? "/" : path + " > " + cat.getName();
    	el.addAttribute("fullname", fullname);

    	parent.addContent(el);

    	List c = (List)children.get(cat.getID());
    	if (c != null) {
    		Iterator i = c.iterator();
    		while (i.hasNext()) {
    			CategorySortKeyPair pair = (CategorySortKeyPair) i.next();
    			Category child = pair.getCategory();
    			BigDecimal childSortKey = pair.getSortKey();
    			generateCategory(el, fullname, child,childSortKey, selected, children);
    		}
    	}        
    }

    private class CategorySortKeyPair {
        private Category m_category;
        private BigDecimal m_sortKey;

        public CategorySortKeyPair(Category category, BigDecimal sortKey) {
            m_category = category;
            m_sortKey = sortKey;
        }
        public Category getCategory() {
            return m_category;
        }
        public BigDecimal getSortKey() {
            return m_sortKey;
        }
    }
    
    private static BigDecimal getCategoryForName(String name) {
    	
    	if(false) {
    		return new BigDecimal(52870);
    	}
    	
        DataCollection objs = SessionManager.getSession().retrieve(Domain.BASE_DATA_OBJECT_TYPE);
        objs.addEqualsFilter("title", name);
        String dispatcherContext = null;
        TemplateContext tc = Navigation.getContext().getTemplateContext();
        if (tc != null) {
            dispatcherContext = tc.getContext();
        }
        objs.addEqualsFilter("model.ownerUseContext.useContext", dispatcherContext);

        DomainCollection domains = new DomainCollection(objs);
        if (domains.next()) {
            Category cat = null;
            cat = ((Domain) domains.getDomainObject()).getModel();
        	return cat.getID();
        } else {
        	return new BigDecimal(-1);
        }
    }
}
