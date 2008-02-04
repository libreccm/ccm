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
package com.arsdigita.formbuilder;

import com.arsdigita.formbuilder.util.PersistentContainerHelper;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * This domain object manages persistence of a Bebop FormSection.
 *
 * @author Peter Marklund
 * @version $Id: PersistentFormSection.java 738 2005-09-01 12:36:52Z sskracic $
 *
 */
public class PersistentFormSection extends PersistentComponent
    implements PersistentContainer, CompoundComponent {

    public static final String versionId = "$Id: PersistentFormSection.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(PersistentFormSection.class.getName());

    // We delegate the association with the child components to this
    // object
    private PersistentContainerHelper m_container =
        new PersistentContainerHelper(this);

    private Container m_form_container;

    // Executes before each component is aded to the form section
    ComponentAddObserver m_componentAddObserver;

    /**
     * BASE_DATA_OBJECT_TYPE represents the full name of the
     * underlying DataObject of this class.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.formbuilder.FormSection";

    ArrayList m_listeners;

    // *** Constructors -------------

    /**
     * Constructor that creates a new form domain object that
     * can be saved to the database later on.
     */
    public PersistentFormSection() {

        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * To be used by sub classes only.
     */
    public PersistentFormSection(String objectType) {

        super(objectType);
    }

    public PersistentFormSection(ObjectType type) {
        super(type);
    }

    public PersistentFormSection(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor that retrieves an existing form domain object
     * from the database.
     *
     * @param id The object id of the form domain object to retrieve
     */
    public PersistentFormSection(BigDecimal id)
        throws DataObjectNotFoundException {

        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * For sub classes to use.
     */
    protected PersistentFormSection(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }


    // *** Public Methods

    /**
     * I am overriding this method to delete all associations with
     * components before the form is deleted. The components themselves are not deleted.
     */
    /*
    protected void beforeDelete() {
        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Deleting children of " + getOID() );
        }

        DataAssociationCursor components = m_container.getComponents();
        while( components.next() ) {
            DomainObject obj =
                DomainObjectFactory.newInstance( components.getDataObject() );

            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Deleting child of " + getOID() + ": " +
                             obj.getOID() + "(" + obj.getClass().getName() +
                             ")" );
            }
            obj.delete();
        }

        //m_container.clearComponents();
        super.beforeDelete();
    }
    */

    /**
     * Add a component after the already added components (in the last position).
     * If this domain object has not been saved (with save()) before you invoke this method
     * it will be saved by this method.
     */
    public void addComponent(PersistentComponent componentFactory) {

        // Delegate to the Container helper
        m_container.addComponent(componentFactory);
    }

    /**
     * Add a child component of the form.
     * If this domain object has not been saved (with save()) before you invoke this method
     * it will be saved by this method.
     *
     * @param position The count of this component starting with 1 (i.e. if it's
     *        the third component to be added to the form this
     *        value would be 3)
     */
    public void addComponent(PersistentComponent componentFactory,
                             int position) {

        // Delegate to the Container helper
        m_container.addComponent(componentFactory, position);
    }

    /**
     * Remove a component from the form.
     * If this domain object has not been saved (with save()) before you invoke this method
     * it will be saved by this method.
     */
    public void removeComponent(PersistentComponent componentFactory) {

        // Delegate to the Container helper
        m_container.removeComponent(componentFactory);
    }

    /**
     * Move component to new position.
     *
     * @param toPosition The position to move the component to. Positions start with 1.
     */
    public void moveComponent(PersistentComponent componentFactory,
                              int toPosition) {

        // Delegate to the Container helper
        m_container.moveComponent(componentFactory, toPosition);
    }

    /**
     * Delete all component associations from this container
     */
    public void clearComponents() {
        m_container.clearComponents();
    }

    /**
     * Create a Bebop FormSection using the persistent information in this form section domain object.
     *
     */
    public Component createComponent() {

        FormSection formSection;
        if (m_form_container == null) {
            formSection = new FormSection();
        } else {
            formSection = new FormSection(m_form_container);
        }

        // Add the process listener
        addProcessListeners(formSection);

        // Add the components
        addComponents(formSection);

        return formSection;
    }

    /**
     * Return all child components 
     */
    public DataAssociationCursor getComponents() {

        // Delegate to the Container helper
        return m_container.getComponents();
    }

    /**
     * Return an Iterator over all child components
     */
    public Iterator getComponentsIter() {
        return m_container.getComponentsIter();
    }

    // *** Internal Helper Methods

    /**
     * Instantiate a process listener with the persisted class name and add
     * it to the Bebop Form.
     */
    protected void addProcessListeners(FormSection formSection) {
        Iterator listeners = getProcessListeners();

        while(listeners.hasNext()) {
            PersistentProcessListener l = (PersistentProcessListener)listeners.next();

            formSection.addProcessListener(l.createProcessListener());
        }
    }

    /**
     * Add all child components to the FormSection.
     */
    protected void addComponents(FormSection formSection) {

        DataAssociationCursor components = m_container.getComponents();
        if (components.isEmpty() ) {
        	components.close();
        	return;
        }
        
        // Loop over the child components
        int componentCounter = 1;
        
        while (components.next()) {
            // Fetch the next component from the list
            PersistentComponent persistentComponent = (PersistentComponent)
                DomainObjectFactory.newInstance( components.getDataObject() );

            // Add the component to the form

            // Fire the component add observer
            if (m_componentAddObserver != null) {
                m_componentAddObserver.beforeAddingComponent(formSection, persistentComponent, componentCounter);
            }

            Component component = persistentComponent.createComponent();

            // Give the observer an opportunity to modify the component that we add to the form
            if (m_componentAddObserver != null) {
                m_componentAddObserver.addingComponent(persistentComponent, componentCounter, component);
            }

            formSection.add(component);

            // Fire the component add observer
            if (m_componentAddObserver != null) {
                m_componentAddObserver.afterAddingComponent(formSection, persistentComponent, componentCounter);
            }

            ++componentCounter;
        }
    	//Close it to avoid exceptions.
        components.close();
    }

    public void setComponentAddObserver(ComponentAddObserver observer) {

        m_componentAddObserver = observer;
    }

    private void retrieveListeners() {
        m_listeners = new ArrayList();
        
        DataAssociation listeners = (DataAssociation)get("listeners");
        DataAssociationCursor cursor = listeners.cursor();
        cursor.addOrder("link.position asc");
        
        while (cursor.next()) {
            PersistentProcessListener l = (PersistentProcessListener)DomainObjectFactory.newInstance(cursor.getDataObject());
            m_listeners.add(l);
        }
    }

    // *** Attribute Methods

    public void addProcessListener(PersistentProcessListener listener) {
        if (listener.isNew()) {
            listener.save();
        }

        if (m_listeners == null)
            retrieveListeners();

        int position = m_listeners.size();
        
        DataObject link = add("listeners", listener);
        link.set("position", new BigDecimal(position));
        m_listeners.add(listener);        
    }

    public void removeProcessListener(PersistentProcessListener listener) {
        if (m_listeners == null)
            retrieveListeners();

        int position = m_listeners.indexOf(listener);
        
        remove("listeners", listener);
        m_listeners.remove(position);

        if( s_log.isDebugEnabled() )
            s_log.debug( "Removing process listener at position " + position );
        
        DataAssociation listeners = (DataAssociation)get("listeners");
        DataAssociationCursor cursor = listeners.cursor();
        cursor.addOrder("link.position asc");
        
        while (cursor.next()) {
            DataObject link = cursor.getLink();
            BigDecimal current = (BigDecimal)link.get("position");
            if( s_log.isDebugEnabled() ) {
                s_log.debug( "Position " + current );
            }

            if (current.intValue() > position) {
                link.set("position", new BigDecimal(current.intValue() - 1));
            }
        }
    }

    public void clearProcessListeners() {
        if (m_listeners == null)
            retrieveListeners();

        clear("listeners");
        
        Iterator i = m_listeners.iterator();
        while (i.hasNext()) {
            PersistentProcessListener l = (PersistentProcessListener)i.next();
            l.delete();
        }
        m_listeners = null;
    }

    public Iterator getProcessListeners() {
        if (m_listeners == null)
            retrieveListeners();

        return m_listeners.iterator();
    }

    /**
     * Set the container that the persistent form will use
     * (for example a ColumnPanel with a certain number of columns).
     * This property is not yet persisted.
     */
    public void setFormContainer(Container container) {
        m_form_container = container;
    }

    /**
     * Get the container that the persistent form will use
     * (for example a ColumnPanel with a certain number of columns).
     * This property is not yet persisted.
     */
    public Container getFormContainer() {
        return m_form_container;
    }

    public boolean isEditable() {
        return false;
    }
}
