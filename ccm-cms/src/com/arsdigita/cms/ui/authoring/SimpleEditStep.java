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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.SecurityPropertyEditor;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.toolbox.ui.ComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A simple implementation of an Authoring Kit editing step.
 * Extends {@link SecurityPropertyEditor} and provides authoring kit
 * integration. See the authoring kit documentation for more
 * info.
 *
 * Child classes should
 * a). call setDisplayComponent()
 * b). call add() zero or more times
 *
 * @author Stanislav Freidin
 * @version $Id: SimpleEditStep.java 2185 2011-06-20 21:16:02Z pboy $
 */
public class SimpleEditStep extends SecurityPropertyEditor 
                            implements AuthoringStepComponent, RequestListener {

    private static final Logger s_log =
        Logger.getLogger( SimpleEditStep.class );

    private AuthoringKitWizard m_parent;
    private ItemSelectionModel m_itemModel;
    private String m_defaultEditKey = null;

    private StringParameter m_streamlinedCreationParam;
    private static final String STREAMLINED = "_streamlined";
    private static final String STREAMLINED_DONE = "1";

    private static List s_additionalDisplayComponents = new ArrayList();

    /**
     * allow additional display components to be added to all implementations
     * of SimpleEditStep. This allows shared optional packages such as notes to 
     * display information on the initial authoring page of all content types without 
     * causing dependencies from ccm-cms.
     * 
     * Any additional components must be added before the edit step is created.
     * An initialiser is a suitable location
     * 
     * @param c
     */
    public static void addAdditionalDisplayComponent(AdditionalDisplayComponent c) {
    	s_additionalDisplayComponents.add(c);
    }
    
    /**
     * Construct a new SimpleEditStep component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *   be responsible for loading the current item
     *
     * @param parent The parent wizard which contains the form.
     *   The component may use the wizard's methods, such as stepForward
     *   and stepBack, in its process listener.
     */
    public SimpleEditStep(ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this(itemModel, parent, "");
    }
    /**
     * Construct a new SimpleEditStep component
     *
     * @param itemModel The {@link ItemSelectionModel} which will
     *                  be responsible for loading the current item
     *
     * @param parent The parent wizard which contains the form. The component
     *               may use the wizard's methods, such as stepForward
     *               and stepBack, in its process listener.
     *
     * @param paramSuffix Additional global parameter name suffix if there are
     *                    multiple SimpleEditStep instances in an authoring kit.
     */
    public SimpleEditStep(ItemSelectionModel itemModel, 
                          AuthoringKitWizard parent, 
                          String paramSuffix) {
        super();
        m_parent = parent;
        m_itemModel = itemModel;

        m_streamlinedCreationParam = 
            new StringParameter(parent.getContentType().getAssociatedObjectType() 
                               + "_properties_done" + paramSuffix);
        
            
        parent.getList().addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    PageState state = e.getPageState();
                    showDisplayPane(state);
                }
            });
        
        Iterator it = s_additionalDisplayComponents.iterator();
        while (it.hasNext()) {
        	
        	AdditionalDisplayComponent component = (AdditionalDisplayComponent)it.next();
        	component.setItemSelectionModel(itemModel);
        	addDisplayComponent(component);
        	
        }        
    }

    /** 
     * Registers global state param for cancelling streamlined
     * creation
     */
    @Override
    public void register(Page p) {
        super.register(p);
        p.addGlobalStateParam(m_streamlinedCreationParam);
        p.addRequestListener(this);
    }

    /**
     * @return the parent wizard
     */
    public AuthoringKitWizard getParentWizard() {
        return m_parent;
    }

    /**
     * @return The item selection model
     */
    public ItemSelectionModel getItemSelectionModel() {
        return m_itemModel;
    }

    /**
     * Forward to the next step if the streamlined creation parameter
     * is turned on _and_  the streamlined_creation global state param
     * is set to 'active'
     *
     * @param state the PageState
     */
    public void maybeForwardToNextStep(PageState state) {
        if (ContentItemPage.isStreamlinedCreationActive(state) &&
            !STREAMLINED_DONE.equals(state.getValue(m_streamlinedCreationParam))) {
            state.setValue(m_streamlinedCreationParam, STREAMLINED_DONE);
            fireCompletionEvent(state);
        }
    }

    /**
     * Cancel streamlined creation for this step if the streamlined
     * creation parameter is turned on _and_ the streamlined_creation
     * global state param is set to 'active'
     *
     * @param state the PageState
     */
    public void cancelStreamlinedCreation(PageState state) {
        if (ContentItemPage.isStreamlinedCreationActive(state)) {
            state.setValue(m_streamlinedCreationParam, STREAMLINED_DONE);
        }
    }
    
    
    public void setDefaultEditKey(String key) {
        m_defaultEditKey = key;
    }

    /**
     * Open the edit component if the streamlined
     * creation parameter is turned on _and_ the streamlined_creation
     * global state param is set to 'active'
     *
     * @param state the PageState
     */
    @Override
    public void pageRequested(RequestEvent e) {
        PageState state = e.getPageState();

        // XXX: This method is called on every page request for every authoring
        // step in every authoring kit. This has in the past revealed a caching
        // side-effect bug, but should in the main be harmless. Except of course
        // for performance.
        // Ideally this method would only be called for a single authoring step
        // on each page load. However, at the stage that this is called,
        // visibility has not been set, and getting the selected authoring kit
        // or component is not straightforward, and would almost certainly
        // involve duplicating code.
        // This need to be rethought.
        //if( !state.isVisibleOnPage( this ) ) return;

        if (m_defaultEditKey != null && m_itemModel.getSelectedItem(state) != null) {
            ComponentAccess ca = (ComponentAccess) getAccessMap().get(m_defaultEditKey);
            
            if (ContentItemPage.isStreamlinedCreationActive(state) &&
                !STREAMLINED_DONE.equals(state.getValue(m_streamlinedCreationParam)) &&
                ca != null && ca.canAccess(state, Utilities.getSecurityManager(state))) {
                showComponent(state, m_defaultEditKey);
            }
        }
        
    }
	/**
     * Public class which implements an AttributeFormatter interface for 
     * boolean values.
     * Its format(...) class returns a string representation for either a
     * false or a true value.
     */
    protected static class LaunchDateAttributeFormatter 
                        implements DomainObjectPropertySheet.AttributeFormatter {

        /**
         * Constructor, does nothing.
         */
        public LaunchDateAttributeFormatter() {
        }

        /**
         * Formatter for the value of a (LaunchDate) attribute.
         * 
         * It currently relays on the prerequisite that the passed in property
         * attribute is in fact a date property. No type checking yet!
         * 
         * Note: the format method has to be executed at each page request. Take
         * care to properly adjust globalization and localization here!
         * 
         * @param obj        Object containing the attribute to format.
         * @param attribute  Name of the attribute to retrieve and format
         * @param state      PageState of the request
         * @return           A String representation of the retrieved boolean
         *                   attribute of the domain object.
         */
        public String format(DomainObject obj, String attribute, PageState state) {
 
            if ( obj != null && obj instanceof ContentPage) {
                
                ContentPage page = (ContentPage) obj;
                Object field = page.get(attribute);

                if( field != null ) {
                    // Note: No type safety here! We relay that it is
                    // attached to a date property!
                    return DateFormat.getDateInstance(
                                         DateFormat.LONG, 
                                         GlobalizationHelper.getNegotiatedLocale()
                                          )
                                     .format(field);
                } else {
                    return (String)GlobalizationUtil
                                   .globalize("cms.ui.unknown")
                                   .localize();
                }
            }

            return (String) GlobalizationUtil
                            .globalize("cms.ui.unknown")
                            .localize();
        }
    }

}
