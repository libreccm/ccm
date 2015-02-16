/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.kernel;

import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.ui.ResourceConfigFormSection;
import com.arsdigita.kernel.ui.BasicResourceConfigFormSection;
import com.arsdigita.kernel.ui.ResourceConfigComponent;
import com.arsdigita.toolbox.ui.SecurityContainer;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.FormProcessException;
import org.apache.log4j.Logger;

/**
 * XXX JAVADOC XXX
 *
 * @see com.arsdigita.kernel.ResourceType
 * @see ResourceConfigFormSection
 * @see com.arsdigita.kernel.Resource
 * @author Justin Ross
 * @version $Id: ResourceTypeConfig.java 1942 2009-05-29 07:53:23Z terry $
 */
public class ResourceTypeConfig {

    /** The logging object for this class. */
    private static final Logger s_log = Logger.getLogger
        (ResourceTypeConfig.class);

    /**
     * optionally set - prevents create/modify form being rendered unless user
     * has this privilege on the appropriate object (parent application in the
     * case of create, application in the case of modify
     */
    private PrivilegeDescriptor m_createModifyPrivilege = null;
    /**
     * optionally set - allows components that list applications to filter on the
     * basis of this privilege - see com.arsdigita.london.portal.ui.PersistantPortal
     * and com.arsdigita.london.portal.ui.ApplicationSelector
     */
    private PrivilegeDescriptor m_viewPrivilege = null;
	
    /**
     * For use in generating default config components when Resource authors
     * don't specify their own. 
     * Should only be used by PortletType!
     */
    protected ResourceTypeConfig() {
        // Empty
    }

    /**
     * 
     * @param resourceObjectType
     */
    public ResourceTypeConfig(String resourceObjectType) {
        this(resourceObjectType, null, null);
    }

    /**
     * constructor that allows the resource create and modify forms to be
     * conditionally displayed according to the accessPrivilege.
     *
     * On creation, a permission check is carried out on the parent resource while
     * on modification, a permission check is carried out on the resource being modified.
     *
     *
     * @param resourceObjectType
     * @param accessPrivilege
     */
    public ResourceTypeConfig(String resourceObjectType, 
                              PrivilegeDescriptor createModifyPrivilege,
                              PrivilegeDescriptor viewPrivilege) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering " + this + " to object type " +
                        resourceObjectType);
        }
        m_createModifyPrivilege = createModifyPrivilege;
        m_viewPrivilege = viewPrivilege;
        
        s_log.debug("create/modify privilege is " + m_createModifyPrivilege +
                    ". View privilege is " + m_viewPrivilege);
        
        

        ResourceType.registerResourceTypeConfig
            (resourceObjectType, this);
    }

    /**
     * 
     * @param resType
     * @param parentResRL
     * @return
     */
    public ResourceConfigFormSection getCreateFormSection
            (final ResourceType resType, final RequestLocal parentResRL) {
        final BasicResourceConfigFormSection config =
            new BasicResourceConfigFormSection(resType, parentResRL);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Returning default create form section " + config);
        }

        return config;
    }

    /**
     * 
     * @param application
     * @return
     */
    public ResourceConfigFormSection getModifyFormSection
            (final RequestLocal application) {
        final BasicResourceConfigFormSection config =
            new BasicResourceConfigFormSection(application);

        return config;
    }

    /**
     * Retrieves the component for creating an instance of a ResourceType.
     * The component should fire a completion event when it has finished
     * processing.
     * @see com.arsdigita.bebop.Completable#fireCompletionEvent(PageState)
     */
    public ResourceConfigComponent getCreateComponent
            (final ResourceType resType, final RequestLocal parentResRL) {
        final ResourceConfigFormSection section =
            getCreateFormSection(resType, parentResRL);

        return new ResourceConfigWrapperComponent(section, parentResRL);
    }

    /**
     * Retrieves the component for modifying an instance of a ResourceType.
     * The component should fire a completion event when it has finished
     * processing.
     * @see com.arsdigita.bebop.Completable#fireCompletionEvent(PageState)
     */
    public ResourceConfigComponent getModifyComponent
            (final RequestLocal resource) {
        final ResourceConfigFormSection section =
            getModifyFormSection(resource);

        return new ResourceConfigWrapperComponent(section, resource);
    }

    /**
     * 
     * @param resource
     */
    public void configureResource(Resource resource) {
        // Empty
    }

    /**
     * Retrieve privilege required for user to see an instance of the resource type. 
     * Privilege may be specified in constructor, or this method may be overridden.
     * 
     * Privilege must be specified by overriding this method if the privilege is
     * retrieved with  PrivilegeDescriptor.get, which relies on a map populated
     * during legacy init event and so may not be populated when the
     * ResourceTypeConfig is created.
     * 
     * If no privilege specified, null is returned
     * @return
     */
    public PrivilegeDescriptor getViewPrivilege() {
    	return m_viewPrivilege;
    }

    /**
     * Retrieve privilege required for user to create or modify an instance of
     * the resource type.
     * Privilege may be specified in constructor, or this method may be overridden.
     *
     * Privilege must be specified by overriding this method if the privilege is
     * retrieved with PrivilegeDescriptor.get, which relies on a map populated
     * during legacy init event and so may not be populated when the
     * ResourceTypeConfig is created.
     *
     * If privilege is specified, view/modify form may not be displayed if user
     * has insufficient privileges.
     *
     * @return
     */
    public PrivilegeDescriptor getCreateModifyPrivilege() {
        return m_createModifyPrivilege;
    }
    

    /**
     * 
     */
    private class ResourceConfigWrapperComponent
            extends ResourceConfigComponent {

        // on creation, check privilege against parent resource. On modification,
        // check privilege against resource
        private RequestLocal m_accessCheckRes;
        private ResourceConfigFormSection m_section;
        private SaveCancelSection m_buttons;

        public ResourceConfigWrapperComponent
                (ResourceConfigFormSection section, RequestLocal accessCheckResRL) {
            m_accessCheckRes = accessCheckResRL;
            m_section = section;
            m_buttons = new SaveCancelSection();

            Form form = new Form("wrapper");
            form.setRedirecting(true);
            form.add(m_section);
            form.add(m_buttons);

            form.addSubmissionListener(new FormSubmissionListener() {
                    public void submitted(FormSectionEvent e)
                        throws FormProcessException {
                        PageState state = e.getPageState();

                        if (m_buttons.getCancelButton().isSelected(state)) {
                            fireCompletionEvent(state);
                            throw new FormProcessException(KernelGlobalizationUtil.globalization("kernel.cancelled"));
                        }
                    }
                });
            form.addProcessListener(new FormProcessListener() {
                    public void process(FormSectionEvent e)
                        throws FormProcessException {
                        PageState state = e.getPageState();
                        fireCompletionEvent(state);
                    }
                });
            if (m_createModifyPrivilege != null && m_accessCheckRes != null) {
                    s_log.debug("" +
                        "creating resource create/modify wrapper form with access check");
                    SecurityContainer sc = new SecurityContainer(form) {
                        protected boolean canAccess(Party party, PageState state) {
                            Resource resource = (Resource)m_accessCheckRes.get(state);
                            s_log.debug("check permission on " + resource +
                                        " for " + party.getPrimaryEmail().getEmailAddress());
                            PermissionDescriptor access =
                                new PermissionDescriptor(m_createModifyPrivilege,
                                                         resource, party);
                            return PermissionService.checkPermission(access);
                            }};
                    add(sc);
            } else {
                s_log.debug(
                    "creating resource create/modify wrapper form without access check");

                add(form);
            }
        }

        /**
         * 
         * @param state
         * @return
         */
        public Resource createResource(PageState state) {
            Resource resource = null;

            // when either save is selected, or nothing is selected
            // (e.g. when pressing enter in IE)        
            if (m_buttons.getSaveButton().isSelected(state)
                || !m_buttons.getCancelButton().isSelected(state)) {
                resource = m_section.createResource(state);
            }
            return resource;
        }

        public void modifyResource(PageState state) {
            // when either save is selected, or nothing is selected
            // (e.g. when pressing enter in IE)
            if (m_buttons.getSaveButton().isSelected(state)
                || !m_buttons.getCancelButton().isSelected(state)) {
                m_section.modifyResource(state);
            }
        }
        
    }
}
