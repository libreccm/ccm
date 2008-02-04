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
package com.arsdigita.formbuilder.util;


import com.arsdigita.formbuilder.PersistentComponent;
import com.arsdigita.formbuilder.PersistentContainer;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataOperation;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;


/**
 * This class is used internally by PersistentFormSection and PersistentOptionGroup
 * to manage the associations to the Component children of these containers.
 *
 * @author Peter Marklund
 * @version $Id: PersistentContainerHelper.java 317 2005-03-11 19:04:37Z mbooth $
 *
 */
public class PersistentContainerHelper
    implements PersistentContainer {

    public static final String versionId = "$Id: PersistentContainerHelper.java 317 2005-03-11 19:04:37Z mbooth $ by $Author: mbooth $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(PersistentContainerHelper.class);

    private PersistentComponent m_component;

    // We cache the number of components to avoid querying the
    // database about this number everytime a component is added
    // A value of -1 indicates that the field is not initialized
    // We are using lazy initialization here
    private int m_numberOfComponents = -1;

    public PersistentContainerHelper(PersistentComponent component) {

        m_component = component;
    }

    // *** Public API

    /**
     * Add a component after the already added components (in the last position).
     * If this domain object has not been saved (with save()) before you invoke this method
     * it will be saved by this method. The component will not be selected.
     */
    public void addComponent(PersistentComponent component) {

        addComponent(component, getNumberOfComponents() + 1, false);
    }

    /**
     * Add a component after the already added components (in the last position).
     * If this domain object has not been saved (with save()) before you invoke this method
     * it will be saved by this method.
     */
    public void addComponent(PersistentComponent component, boolean selected) {

        addComponent(component, getNumberOfComponents() + 1, selected);
    }

    /**
     * Add a child component of the container.
     * If this domain object has not been saved (with save()) before you invoke this method
     * it will be saved by this method. The component will not be selected.
     *
     * @param position The count of this component starting with 1 (i.e. if it's
     *        the third component to be added to the container this
     *        value would be 3)
     */
    public void addComponent(PersistentComponent component,
                             int position) {

        addComponent(component, position, false);
    }

    /**
     * Add a child component of the container.
     * If this domain object has not been saved (with save()) before you invoke this method
     * it will be saved by this method.
     *
     * @param position The count of this component starting with 1 (i.e. if it's
     *        the third component to be added to the container this
     *        value would be 3)
     */
    public void addComponent(PersistentComponent component,
                             int position,
                             boolean isSelected) {

        // We need the container and the component to be saved for referential
        // integrity in the database
        if (m_component.isNew()) {
            m_component.save();
        }

        if (component.isNew()) {
            component.save();
        }

        assertPositionInAddRange(position);

        // Add the component
        executeAddComponent(component.getID(), position, isSelected);

        // Increment the component counter
        incrementNumberOfComponents();
    }

    /**
     * Remove a component from the container.
     * If this domain object has not been saved (with save()) before you invoke this method
     * it will be saved by this method.
     */
    public void removeComponent(PersistentComponent component) {

        // We need the container to be saved for referential integrity in the database
        if (m_component.isNew()) {
            m_component.save();
        }
        if (component.isNew()) {
            throw new IllegalArgumentException("Trying to remove factory with id " + component.getID() +
                                               " that has not been saved. A factory must have been first added " +
                                               " and saved before it can be removed");
        }

        // Remove the component
        executeRemoveComponent(component.getID());

        // Decrement the component counter
        decrementNumberOfComponents();
    }

    /**
     * Move component to new position.
     *
     * @param toPosition The position to move the component to. Positions start with 1.
     */
    public void moveComponent(PersistentComponent component,
                              int toPosition) {

        removeComponent(component);

        addComponent(component, toPosition);
    }

    public void clearComponents() {

        executeClearComponents();
    }

    public void setComponentSelected(PersistentComponent component,
                                     boolean selected) {

        executeSetComponentSelected(component.getID(), selected);
    }

    /**
     * Return all children components in order
     */
    public DataAssociationCursor getComponents() {
        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Getting components for " + m_component.getOID() );
        }

        DataAssociationCursor cursor =
            ((DataAssociation) DomainServiceInterfaceExposer.get
            ( m_component, "component" )).cursor();
        cursor.addOrder( "link.orderNumber" );

        return cursor;
    }

    // *** Internal Helper Methods
    /**
     * Map a component with a certain position to the container.
     */
    protected void executeAddComponent(BigDecimal componentID,
                                       int position,
                                       boolean isSelected) {

        Session session = SessionManager.getSession();

        // First update the order numbers
        DataOperation operation =
            session.retrieveDataOperation("com.arsdigita.formbuilder.UpdateOrderBeforeAdd");
        operation.setParameter("containerID", m_component.getID());
        operation.setParameter("orderNumber", new Integer(position));
        operation.execute();

        // Add the component
        operation = session.retrieveDataOperation("com.arsdigita.formbuilder.AddComponent");
        operation.setParameter("containerID", m_component.getID());
        operation.setParameter("componentID", componentID);
        operation.setParameter("orderNumber", new Integer(position));
        operation.setParameter("isSelected", new Boolean(isSelected));
        operation.execute();
    }

    /**
     * Remove a component-container mapping. Note that this does not remove the component
     */
    protected void executeRemoveComponent(BigDecimal componentID) {
        Session session = SessionManager.getSession();

        // Update the order numbers first
        DataOperation operation =
            session.retrieveDataOperation("com.arsdigita.formbuilder.UpdateOrderBeforeRemove");
        operation.setParameter("containerID", m_component.getID());
        operation.setParameter("componentID", componentID);
        operation.execute();

        // Remove the component
        operation = session.retrieveDataOperation("com.arsdigita.formbuilder.RemoveComponent");
        operation.setParameter("containerID", m_component.getID());
        operation.setParameter("componentID", componentID);
        operation.execute();
    }

    /**
     * Remove all component associations from the container
     */
    protected void executeClearComponents() {

        Session session = SessionManager.getSession();
        DataOperation operation =
            session.retrieveDataOperation("com.arsdigita.formbuilder.ClearComponents");

        operation.setParameter("containerID", m_component.getID());

        operation.execute();
    }

    protected void executeSetComponentSelected(BigDecimal componentID, boolean selected) {

        Session session = SessionManager.getSession();
        DataOperation operation =
            session.retrieveDataOperation("com.arsdigita.formbuilder.SetComponentSelected");

        operation.setParameter("containerID", m_component.getID());
        operation.setParameter("componentID", componentID);
        operation.setParameter("isSelected", new Boolean(selected));
        operation.execute();

    }

    /**
     * Assert that the position is valid for adding a component at.
     */
    protected void assertPositionInAddRange(int position) {
        // +2 not +1, since component numbers start at '1', and we
        // need to be able to add after the last component.
        assertPositionInRange(position, getNumberOfComponents() + 2);
    }

    /**
     * Assert that the position is within the current range of component positions.
     */
    protected void assertPositionInCurrentRange(int position) {

        assertPositionInRange(position, getNumberOfComponents());
    }

    /**
     * Assert that the position is between 1 and the given upper limit
     */
    protected void assertPositionInRange(int position, int upperLimit) {

        try {
            FormBuilderUtil.assertArgumentInRange(position, 1, upperLimit);

        } catch (Exception e) {

            throw new IllegalArgumentException("position " + Integer.toString(position) +
                                               " provided to " + this.toString() + " is invalid" +
                                               ", should be between 1 and " +
                                               Integer.toString(upperLimit));
        }
    }

    protected int getNumberOfComponents() {

        // Initialize the cached number if this has not been done
        if (m_numberOfComponents == -1) {

            // Get number from database and cache it
            Session session = SessionManager.getSession();
            DataQuery query =
                session.retrieveQuery("com.arsdigita.formbuilder.NumberOfComponents");
            query.setParameter("containerID", m_component.getID());
            query.next();
            m_numberOfComponents =
                ((Integer)query.get("numberOfComponents")).intValue();
            query.close();
        }

        return m_numberOfComponents;
    }

    protected void incrementNumberOfComponents() {

        // Initialize the number if this has not been done
        if (m_numberOfComponents == -1) {
            getNumberOfComponents();
        }

        // Increment the number
        ++m_numberOfComponents;
    }

    protected void decrementNumberOfComponents() {

        // Initialize the number if this has not been done
        if (m_numberOfComponents == -1) {
            getNumberOfComponents();
        }

        // Decrement the counter
        --m_numberOfComponents;
    }

    public Iterator getComponentsIter() {
        class DACIterator implements Iterator {
            boolean m_hasNext = false;
            Object m_obj = null;
            DataAssociationCursor m_cursor;

            DACIterator(DataAssociationCursor cursor) {
                m_cursor = cursor;
                iterate();
            }

            public boolean hasNext() {
                return m_hasNext;
            }

            public Object next() {
                if (!m_hasNext) throw new NoSuchElementException();

                Object next = m_obj;
                iterate();
                return next;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public void iterate() {
                m_hasNext = m_cursor.next();
                if (m_hasNext) {
                    m_obj = DomainObjectFactory.newInstance(m_cursor.getDataObject());
                } else {
                    m_obj = null;
                }
            }
        };

        return new DACIterator(getComponents());
    }
}
