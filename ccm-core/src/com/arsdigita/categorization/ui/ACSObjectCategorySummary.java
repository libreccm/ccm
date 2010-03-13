/*
 * Copyright (C) 2007 Chris Gilbert
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
package com.arsdigita.categorization.ui;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.categorization.CategorizedObject;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.categorization.RootCategoryCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;
/**
 * abstract class for displaying the categories assigned to an object under one or 
 * more root nodes. Subclasses should retrieve the object to be assigned and 
 * supply the logic to retrieve root categories
 * @author chris.gilbert@westsussex.gov.uk
 * 
 * abstracted from ItemCategorySummary in ccm-cms
 *
 */
public abstract class ACSObjectCategorySummary extends SimpleComponent {

    private static final Logger s_log = Logger.getLogger(ACSObjectCategorySummary.class);

    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_ADD_JS = "addJS";

    private Map m_listeners = new HashMap();

    public ACSObjectCategorySummary() {
        registerAction(ACTION_DELETE,
                       new DeleteActionListener());
    }

    public void registerAction(String name,
                                  ActionListener listener) {
        m_listeners.put(name, listener);
    }

    public void respond(PageState state) throws ServletException {
        super.respond(state);

        Assert.isTrue(canEdit(state), "User can edit object");

        String name = state.getControlEventName();
        ActionListener listener = (ActionListener)m_listeners.get(name);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Got event " + name + " listener " + listener);
        }

        if (listener != null) {
            listener.actionPerformed(new ActionEvent(this, state));
        }
    }

	/**
	 * default behaviour is to check for edit access on the current resource.
	 * as defined by getCategorizedObject.
	 * This can be overridden by subclasses, if for instance a specific 
	 * privilege should be checked
	 * @param state
	 * @return
	 */
    protected boolean canEdit(PageState state) {
    	
    	Party party = Kernel.getContext().getParty();
    	if (party == null) {
    		party = Kernel.getPublicUser();
    	}
    	
    	PermissionDescriptor edit = new PermissionDescriptor(PrivilegeDescriptor.EDIT, getObject(state), party);
        return PermissionService.checkPermission(edit);
    }
    
    

	
	protected abstract ACSObject getObject(PageState state);
	
	protected abstract String getXMLPrefix();
	
	protected abstract String getXMLNameSpace();
	
	protected abstract RootCategoryCollection getRootCategories(PageState state);
	
	
    public void generateXML(PageState state,
                            Element parent) {
        
        boolean canEdit = canEdit(state);
		
        Element content = parent.newChildElement(getXMLPrefix() + ":categoryStepSummary",
                                                 getXMLNameSpace());
        exportAttributes(content);

        Element rootCats = content.newChildElement(getXMLPrefix() + ":categoryRoots",
		getXMLNameSpace());

        RootCategoryCollection roots = getRootCategories(state);
        while (roots.next()) {
            Element root = rootCats.newChildElement(getXMLPrefix() + ":categoryRoot",
			getXMLNameSpace());

            root.addAttribute("name", roots.getName());
            root.addAttribute("description", roots.getDescription());
            root.addAttribute("context", roots.getUseContext());

            if (canEdit) {
                state.setControlEvent(this,
                                      ACTION_ADD,
                                      roots.getCategory().getID().toString());
                try {
                    root.addAttribute("addAction", XML.format(state.stateAsURL()));
                } catch (IOException ex) {
                    throw new UncheckedWrapperException("cannot generate URL", ex);
                }
                state.clearControlEvent();
                state.setControlEvent(this,
                                      ACTION_ADD_JS,
                                      roots.getCategory().getID().toString());
                try {
                    root.addAttribute("addJSAction", XML.format(state.stateAsURL()));
                } catch (IOException ex) {
                    throw new UncheckedWrapperException("cannot generate URL", ex);
                }
                state.clearControlEvent();
            }
        }

        Element itemCats = content.newChildElement(getXMLPrefix() + ":itemCategories",
		getXMLNameSpace());
        CategoryCollection cats = new CategorizedObject(getObject(state)).getParents();
        while (cats.next()) {
            Category cat = (Category)cats.getCategory();


            // This is lame, but there is no way to get full path
            // and thus avoid this DOS attack using categorization API
            StringBuffer path = new StringBuffer();
            CategoryCollection parents = cat.getDefaultAscendants();
            parents.addOrder(Category.DEFAULT_ANCESTORS);

            while (parents.next()) {
                Category par = (Category)parents.getCategory();
                if (path.length() != 0) {
                    path.append(" -> ");
                }
                path.append(par.getName());
            }

            Element el = itemCats.newChildElement(getXMLPrefix() + ":itemCategory",
			getXMLNameSpace());
            el.addAttribute("name", cat.getName());
            el.addAttribute("description", cat.getDescription());
            el.addAttribute("path", XML.format(path));

            if (canEdit) {
                state.setControlEvent(this,
                                      ACTION_DELETE,
                                      cat.getID().toString());
                try {
                    el.addAttribute("deleteAction", XML.format(state.stateAsURL()));
                } catch (IOException ex) {
                    throw new UncheckedWrapperException("cannot generate URL", ex);
                }
                state.clearControlEvent();
            }
        }
    }

    private class DeleteActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PageState state = e.getPageState();
            String value = state.getControlEventValue();

            Category cat = (Category)DomainObjectFactory
                .newInstance(new OID(Category.BASE_DATA_OBJECT_TYPE,
                                     new BigDecimal(value)));
            ACSObject object = getObject(state);
            
            if (s_log.isDebugEnabled()) {
                s_log.debug("Removing category " + cat + " from "  + object
                            );
            }
            cat.removeChild(object);

            state.clearControlEvent();
            throw new RedirectSignal(state.toURL(), true);
        }
    }
}
