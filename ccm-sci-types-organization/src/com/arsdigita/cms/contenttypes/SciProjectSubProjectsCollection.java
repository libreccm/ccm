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
public class SciProjectSubProjectsCollection extends DomainCollection {

    public String ORDER = "subProjectOrder";
    public String LINKORDER = "link.subProjectOrder";
    private static final Logger s_log =
                                Logger.getLogger(
            SciProjectSubProjectsCollection.class);

    public SciProjectSubProjectsCollection(DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(LINKORDER);
    }

    public Integer getSubProjectOrder() {
        return (Integer) m_dataCollection.get(LINKORDER);
    }

    public void setSubProjectOrder(Integer order) {
        DataObject link = (DataObject) this.get("link");

        link.set(ORDER, order);
    }

    public void swapWithNext(SciSubProject subproject) {
        int currentPosition = 0;
        int currentIndex = 0;
        int nextIndex = 0;

        s_log.debug("Searching subproject...");
        this.rewind();
        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getSubProjectOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getSubProjectOrder(): %d",
                                      getSubProjectOrder()));
            if (this.getSubProject().equals(subproject)) {
                break;
            }
        }

        if (currentPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided subproject is not "
                    + "part of this collection."));
        }

        if (this.next()) {
            nextIndex = this.getSubProjectOrder();
        } else {
            throw new IllegalArgumentException(
                    "The provided subproject is the last "
                    + "in the collection, so there is no next object "
                    + "to swap with.");
        }

        this.rewind();

        while (this.getPosition() != currentPosition) {
            this.next();
        }

        this.setSubProjectOrder(nextIndex);
        this.next();
        this.setSubProjectOrder(currentIndex);
        this.rewind();
    }

    public void swapWithPrevious(SciSubProject subproject) {
        int previousPosition = 0;
        int previousIndex = 0;
        int currentPosition = 0;
        int currentIndex = 0;

        s_log.debug("Searching subproject...");
        this.rewind();
        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getSubProjectOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getSubProjectOrder(): %d",
                                      getSubProjectOrder()));
            if (this.getSubProject().equals(subproject)) {
                break;
            }

            previousPosition = currentPosition;
            previousIndex = currentIndex;
        }

        if (currentPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided subproject is not "
                    + "part of this collection."));
        }

        if (previousPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided subproject is the first one in this "
                    + "collection, so there is no previous one to switch "
                    + "with."));
        }

        this.rewind();
        while (this.getPosition() != previousPosition) {
            this.next();
        }

        this.setSubProjectOrder(currentIndex);
        this.next();
        this.setSubProjectOrder(previousIndex);
        this.rewind();
    }

    public SciSubProject getSubProject() {
        return new SciProject(m_dataCollection.getDataObject());
    }
}
