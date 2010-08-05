/*
 * Copyright (c) 2010 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import org.apache.log4j.Logger;

/**
 * Collection class for the childs of a GenericOrganizationalUnit.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitChildrenCollection extends DomainCollection {

    public static final String ORDER = "link.orgaunit_children_order";
    public static final String CHILDREN_ORDER = "orgaunit_children_order";
    private final static Logger s_log = Logger.getLogger(
            GenericOrganizationalUnitChildrenCollection.class);

    public GenericOrganizationalUnitChildrenCollection(
            DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(ORDER);
    }

    /**
     * Gets the value of the order attribute of the current association.
     *
     * @return Order value of the current item.
     */
    public Integer getChildrenOrder() {
        return (Integer) m_dataCollection.get(ORDER);
    }

    /**
     * Sets the order attribute for the current association.
     *
     * @param order The new value for the order.
     */
    public void setChildrenOrder(Integer order) {
        DataObject link = (DataObject) this.get("link");

        link.set(CHILDREN_ORDER, order);
    }

    /**
     * Swaps the item {@code child} with the next one in the collection.
     *
     * @param child The child to swap with the next one.
     * @throws IllegalArgumentException Thrown if the child object provided is
     * not part of this collection, or if the child is the last one in the
     * collection.
     */
    public void swapWithNext(GenericOrganizationalUnit child) {
        int currentPos = 0;
        int currentIndex = 0;
        int nextIndex = 0;        

        s_log.debug("Searching child...");
        this.rewind();
        while (this.next()) {
            currentPos = this.getPosition();
            currentIndex = this.getChildrenOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPos,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getChildrenOrder(): %d",
                                      getChildrenOrder()));
            if (this.getOrgaUnitChild().equals(child)) {
                break;
            }
        }

        if (currentPos == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided organizational unit is not "
                    + "part of this collection."));
        }

        if (this.next()) {
            nextIndex = this.getChildrenOrder();
        } else {
            throw new IllegalArgumentException(
                    "The provided organizational unit is the last "
                    + "in the collection, so there is no next object "
                    + "to swap with.");
        }

        this.rewind();

        while (this.getPosition() != currentPos) {
            this.next();
        }

        this.setChildrenOrder(nextIndex);
        this.next();
        this.setChildrenOrder(currentIndex);
        this.rewind();
    }

    /**
     * Swaps the item {@code child} with the previous one in the collection.
     *
     * @param child The child to swap with the previous one.
     * @throws IllegalArgumentException Thrown if the child object provided is
     * not part of this collection, or if the child is the first one in the
     * collection.
     */
    public void swapWithPrevious(GenericOrganizationalUnit child) {
        int previousPos = 0;
        int previousIndex = 0;
        int currentPos = 0;
        int currentIndex = 0;

        s_log.debug("Searching child...");
        this.rewind();
        while (this.next()) {
            currentPos = this.getPosition();
            currentIndex = this.getChildrenOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPos,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getChildrenOrder(): %d",
                                      getChildrenOrder()));
            if (this.getOrgaUnitChild().equals(child)) {
                break;
            }

            previousPos = currentPos;
            previousIndex = currentIndex;
        }

        if (currentPos == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided organizational unit is not "
                    + "part of this collection."));
        }

        if (previousPos == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided organizational unit is the first one in this "
                    + "collection, so there is no previous one to switch "
                    + "with."));
        }

        this.rewind();
        while (this.getPosition() != previousPos) {
            this.next();
        }

        this.setChildrenOrder(currentIndex);
        this.next();
        this.setChildrenOrder(previousIndex);
        this.rewind();
    }

    /**
     * Retrieves the current child.
     *
     * @return The GenericOrganizationalUnit at the cursors current position.
     */
    public GenericOrganizationalUnit getOrgaUnitChild() {
        return new GenericOrganizationalUnit(m_dataCollection.getDataObject());
    }
}
