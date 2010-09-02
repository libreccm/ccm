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
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class AuthorshipCollection extends DomainCollection {

    private final static Logger s_log =
                                Logger.getLogger(AuthorshipCollection.class);
    public static final String ORDER = "authorOrder";
    public static final String LINKORDER = "link.authorOrder";
    public static final String EDITOR = "editor";
    public static final String LINKEDITOR = "link.editor";

    public AuthorshipCollection(
            DataCollection dataCollection) {
        super(dataCollection);
        m_dataCollection.addOrder(LINKORDER);
    }

    public Integer getAuthorshipOrder() {
        return (Integer) m_dataCollection.get(LINKORDER);
    }

    public void setAuthorshipOrder(Integer order) {
        DataObject link = (DataObject) this.get("link");

        link.set(ORDER, order);
    }

    public Boolean isEditor() {
        return (Boolean) m_dataCollection.get(LINKEDITOR);
    }

    public void setEditor(Boolean editor) {
        DataObject link = (DataObject) this.get("link");

        link.set(EDITOR, editor);
    }

    public void swapWithNext(GenericPerson author) {
        int currentPos = 0;
        int currentIndex = 0;
        int nextIndex = 0;

        s_log.debug("Searching author...");
        this.rewind();
        while (this.next()) {
            currentPos = this.getPosition();
            currentIndex = this.getAuthorshipOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPos,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getAuthorshipOrder(): %d",
                                      getAuthorshipOrder()));
            if (this.getAuthor().equals(author)) {
                break;
            }
        }

        if (currentPos == 0) {
            throw new IllegalArgumentException(
                    String.format("The provided person is not an author "
                                  + "of this publication."));
        }

        if (this.next()) {
            nextIndex = this.getAuthorshipOrder();
        } else {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided person is the last in the"
                    + "collection, so threre is no next object to swap with."));
        }

        this.rewind();

        while (this.getPosition() != currentPos) {
            this.next();
        }

        this.setAuthorshipOrder(nextIndex);
        this.next();
        this.setAuthorshipOrder(currentIndex);
        this.rewind();
    }

    public void swapWithPrevious(GenericPerson author) {
        int previousPos = 0;
        int previousIndex = 0;
        int currentPos = 0;
        int currentIndex = 0;

        s_log.debug("Searching author...");
        this.rewind();
        while (this.next()) {
            currentPos = this.getPosition();
            currentIndex = this.getAuthorshipOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPos,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getAuthorshipOrder(): %d",
                                      getAuthorshipOrder()));
            if (this.getAuthor().equals(author)) {
                break;
            }

            previousPos = currentPos;
            previousIndex = currentIndex;
        }

        if (currentPos == 0) {
            throw new IllegalArgumentException(
                    String.format("The provided person is not an author "
                                  + "of this publication."));
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

        this.setAuthorshipOrder(currentIndex);
        this.next();
        this.setAuthorshipOrder(previousIndex);
        this.rewind();
    }

    public GenericPerson getAuthor() {
        return new GenericPerson(m_dataCollection.getDataObject());
    }
}
