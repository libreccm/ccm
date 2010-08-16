/*
 * Copyright (c) 2010 Jens Pelzetter,
 for the Center of Social Politics of the University of Bremen
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
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class EditshipCollection extends DomainCollection {

    public static final String LINK_FROM = "link.from";
    public static final String LINK_TO = "link.to";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String LINKORDER = "link.editor_order";
    public static final String ORDER = "editor_order";
    private final static Logger s_log =
                                Logger.getLogger(EditshipCollection.class);

    public EditshipCollection(DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(LINKORDER);
    }

    public Date getFrom() {
        return (Date) m_dataCollection.get(LINK_FROM);
    }

    public void setFrom(Date from) {
        DataObject link = (DataObject) this.get("link");

        link.set(FROM, from);
    }

    public Date getTo() {
        return (Date) m_dataCollection.get(LINK_TO);
    }

    public void setTo(Date to) {
        DataObject link = (DataObject) this.get("link");

        link.set(TO, to);
    }

    /**
     * Gets the value of the order attribute for the current association.
     * 
     * @return Order value of the current item.
     */
    public Integer getEditorOrder() {
        return (Integer) m_dataCollection.get(LINKORDER);
    }

    /**
     * Sets the value of the order attribute of the current association.
     * 
     * @param order The new value for the order.
     * 
     */
    public void setEditorOrder(Integer order) {
        DataObject link = (DataObject) this.get("link");

        link.set(ORDER, order);
    }

    /**
     * Swaps the item {@code child} with the next one in the collection.
     *
     * @param editor The item to swap with the next one.
     * @throws IllegalArgumentException Thrown if the items provided is
     * not part of this collection, or if the item is the last one in the
     * collection.
     */
    public void swapWithNext(GenericPerson editor) {
        int currentPosition = 0;
        int currentIndex = 0;
        int nextIndex = 0;

        s_log.debug("Search editor...");
        this.rewind();
        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getEditorOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getEditorOrder(): %d",
                                      getEditorOrder()));
            if (this.getEditor().equals(editor)) {
                break;
            }
        }

        if (currentPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided person is not "
                    + "part of this collection."));
        }

        if (this.next()) {
            nextIndex = this.getEditorOrder();
        } else {
            throw new IllegalArgumentException(
                    "The provided person is the last "
                    + "in the collection, so there is no next object "
                    + "to swap with.");
        }

        this.rewind();

        while (this.getPosition() != currentPosition) {
            this.next();
        }

        this.setEditorOrder(nextIndex);
        this.next();
        this.setEditorOrder(currentIndex);
        this.rewind();
    }

    /**
     * Swaps the item {@code editor} with the previous one in the collection.
     *
     * @param editor The person to swap with the previous one.
     * @throws IllegalArgumentException Thrown if the person provided is
     * not part of this collection, or if the person is the first one in the
     * collection.
     */
    public void swapWithPrevious(GenericPerson editor) {
        int previousPosition = 0;
        int previousIndex = 0;
        int currentPosition = 0;
        int currentIndex = 0;

        s_log.debug("Searching editor...");
        this.rewind();
        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getEditorOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getEditorOrder(): %d",
                                      getEditorOrder()));
            if (this.getEditor().equals(editor)) {
                break;
            }

            previousPosition = currentPosition;
            previousIndex = currentIndex;
        }

        if (currentPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided person is not "
                    + "part of this collection."));
        }

          if (previousPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided person is the first one in this "
                    + "collection, so there is no previous one to switch "
                    + "with."));
        }

        this.rewind();
        while (this.getPosition() != previousPosition) {
            this.next();
        }

        this.setEditorOrder(currentIndex);
        this.next();
        this.setEditorOrder(previousIndex);
        this.rewind();
    }

    public GenericPerson getEditor() {
        return new GenericPerson(m_dataCollection.getDataObject());
    }
}
