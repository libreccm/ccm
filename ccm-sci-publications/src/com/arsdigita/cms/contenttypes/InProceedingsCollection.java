/*
 * Copyright (c) 2010 Jens Pelzetter
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
 *
 * @author Jens Pelzetter
 */
public class InProceedingsCollection extends DomainCollection {

    public static final String LINKORDER = "link.paperOrder";
    public static final String ORDER = "paperOrder";
    private static final Logger s_log =
                                Logger.getLogger(InProceedingsCollection.class);

    public InProceedingsCollection(DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(LINKORDER);
    }

    public Integer getPaperOrder() {
        return (Integer) m_dataCollection.get(LINKORDER);
    }

    public void setPaperOrder(Integer order) {
        DataObject link = (DataObject) this.get("link");

        link.set(ORDER, order);
    }

    public void swapWithNext(InProceedings paper) {
        int currentPosition = 0;
        int currentIndex = 0;
        int nextIndex = 0;

        s_log.debug("Searching paper...");
        this.rewind();
        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getPaperOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getPaperOrder(): %d",
                                      getPaperOrder()));
            if (this.getPaper().equals(paper)) {
                break;
            }
        }

        if (currentPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided article is not "
                    + "part of this collection."));
        }

        if (this.next()) {
            nextIndex = this.getPaperOrder();
        } else {
            throw new IllegalArgumentException(
                    "The provided paper is the last "
                    + "in the collection, so there is no next object "
                    + "to swap with.");
        }

        this.rewind();

        while (this.getPosition() != currentPosition) {
            this.next();
        }

        this.setPaperOrder(nextIndex);
        this.next();
        this.setPaperOrder(currentIndex);
        this.rewind();
    }

    public void swapWithPrevious(InProceedings paper) {
        int previousPosition = 0;
        int previousIndex = 0;
        int currentPosition = 0;
        int currentIndex = 0;

        s_log.debug("Searching paper...");
        this.rewind();
        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getPaperOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getPaperOrder(): %d",
                                      getPaperOrder()));
            if (this.getPaper().equals(paper)) {
                break;
            }

            previousPosition = currentPosition;
            previousIndex = currentIndex;
        }

        if (currentPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided paper is not "
                    + "part of this collection."));
        }

        if (previousPosition == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided paper is the first one in this "
                    + "collection, so there is no previous one to switch "
                    + "with."));
        }

        this.rewind();
        while (this.getPosition() != previousPosition) {
            this.next();
        }

        this.setPaperOrder(currentIndex);
        this.next();
        this.setPaperOrder(previousIndex);
        this.rewind();
    }

    public InProceedings getPaper() {
        //return new InProceedings(m_dataCollection.getDataObject());

        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());

        return (InProceedings) bundle.getPrimaryInstance();
    }

    public InProceedings getPaper(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());

        return (InProceedings) bundle.getInstance(language);
    }
}
