/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentSubDepartmentsCollection extends DomainCollection {

    public String ORDER = "subDepartmentOrder";
    public String LINKORDER = "link.subDepartmentOrder";
    private static final Logger s_log =
            Logger.getLogger(SciDepartmentSubDepartmentsCollection.class);

    public SciDepartmentSubDepartmentsCollection(DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(LINKORDER);
    }

    public Integer getSubDepartmentOrder() {
        return (Integer) m_dataCollection.get(LINKORDER);
    }

    public void setSubDepartmentOrder(Integer order) {
        DataObject link = (DataObject) this.get("link");

        link.set(ORDER, order);
    }

    public void swapWithNext(SciDepartment department) {
        int currentPosition = 0;
        int currentIndex = 0;
        int nextIndex = 0;

        s_log.debug("Searching department...");
        this.rewind();
        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getSubDepartmentOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getDepartmentOrder(): %d",
                                      getSubDepartmentOrder()));
            if (this.getSubDepartment().equals(department)) {
                break;
            }
        }

        if (currentPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided department is not "
                    + "part of this collection."));
        }

        if (this.next()) {
            nextIndex = this.getSubDepartmentOrder();
        } else {
            throw new IllegalArgumentException(
                    "The provided department is the last "
                    + "in the collection, so there is no next object "
                    + "to swap with.");
        }

        this.rewind();

        while (this.getPosition() != currentPosition) {
            this.next();
        }

        this.setSubDepartmentOrder(nextIndex);
        this.next();
        this.setSubDepartmentOrder(currentIndex);
        this.rewind();
    }

    public void swapWithPrevious(SciDepartment department) {
        int previousPosition = 0;
        int previousIndex = 0;
        int currentPosition = 0;
        int currentIndex = 0;

        s_log.debug("Searching department...");
        this.rewind();
        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getSubDepartmentOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getDepartmentOrder(): %d",
                                      getSubDepartmentOrder()));
            if (this.getSubDepartment().equals(department)) {
                break;
            }

            previousPosition = currentPosition;
            previousIndex = currentIndex;
        }

        if (currentPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided department is not "
                    + "part of this collection."));
        }

        if (previousPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided department is the first one in this "
                    + "collection, so there is no previous one to switch "
                    + "with."));
        }

        this.rewind();
        while (this.getPosition() != previousPosition) {
            this.next();
        }

        this.setSubDepartmentOrder(currentIndex);
        this.next();
        this.setSubDepartmentOrder(previousIndex);
        this.rewind();
    }

    public SciDepartment getSubDepartment() {
        return new SciDepartment(m_dataCollection.getDataObject());
    }
}
