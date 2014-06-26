/*
 * Copyright (c) 2014 Jens Pelzetter
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

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import org.apache.log4j.Logger;

/**
 * Used only internally.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsDirectorCollection extends DomainCollection {

    private final static Logger LOGGER = Logger.getLogger(SciPublicationsDirectorCollection.class);
    public static final String ORDER = "directorOrder";
    public static final String LINK_ORDER = "link.directorOrder";

    public SciPublicationsDirectorCollection(final DataCollection dataCollection) {

        super(dataCollection);
        m_dataCollection.addOrder(LINK_ORDER);
    }

    public Integer getDirectorOrder() {
        return (Integer) m_dataCollection.get(LINK_ORDER);
    }

    public void setDirectorOrder(final Integer order) {
        final DataObject link = (DataObject) get("link");
        link.set(ORDER, order);
    }

    public GenericPerson getDirector() {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.newInstance(
            m_dataCollection.getDataObject());
        return (GenericPerson) bundle.getPrimaryInstance();
    }

    public String getSurname() {
        return getDirector().getSurname();
    }

    public String getGivenName() {
        return getDirector().getGivenName();
    }

    public void swapWithNext(final GenericPerson director) {

        int currentPos = 0;
        int currentIndex = 0;
        int nextIndex = 0;

        rewind();
        while (next()) {
            currentPos = getPosition();
            currentIndex = getDirectorOrder();
            if (getDirector().equals(director)) {
                break;
            }
        }

        if (currentPos == 0) {
            throw new IllegalArgumentException(
                String.format("The provided person is not a director "
                                  + "of this movie."));
        }

        if (next()) {
            nextIndex = getDirectorOrder();
        } else {
            throw new IllegalArgumentException(
                String.format("The provided person is the last in the"
                                  + "collection, so threre is no next object to swap with."));
        }

        this.rewind();

        while (getPosition() != currentPos) {
            next();
        }

        setDirectorOrder(nextIndex);
        next();
        setDirectorOrder(currentIndex);
        rewind();

        normalizeOrder();

    }

    public void swapWithPrevious(GenericPerson director) {

        int previousPos = 0;
        int previousIndex = 0;
        int currentPos = 0;
        int currentIndex = 0;

        this.rewind();
        while (next()) {
            currentPos = getPosition();
            currentIndex = getDirectorOrder();
            if (this.getDirector().equals(director)) {
                break;
            }

            previousPos = currentPos;
            previousIndex = currentIndex;
        }

        if (currentPos == 0) {
            throw new IllegalArgumentException(
                String.format("The provided person is not an director "
                                  + "of this movie."));
        }

        if (previousPos == 0) {
            throw new IllegalArgumentException(
                String.format(
                    "The provided author is the first one in this "
                        + "collection, so there is no previous one to switch "
                        + "with."));
        }

        this.rewind();
        while (this.getPosition() != previousPos) {
            this.next();
        }

        this.setDirectorOrder(currentIndex);
        this.next();
        this.setDirectorOrder(previousIndex);
        this.rewind();

        normalizeOrder();
    }

    private void normalizeOrder() {
        this.rewind();

        int i = 1;
        while (this.next()) {
            setDirectorOrder(i);
            i++;
        }
        this.rewind();
    }

}
