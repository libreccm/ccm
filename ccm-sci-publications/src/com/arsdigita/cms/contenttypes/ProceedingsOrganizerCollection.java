package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;

import org.apache.log4j.Logger;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ProceedingsOrganizerCollection extends DomainCollection {

    public static final String LINKORDER = "link.organizerOrder";
    public static final String ORDER = "organizerOrder";
    private static final Logger s_log =
                                Logger.getLogger(ProceedingsOrganizerCollection.class);

    public ProceedingsOrganizerCollection(final DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(LINKORDER);
    }

    public Integer getOrganizerOrder() {
        return (Integer) m_dataCollection.get(LINKORDER);
    }

    public void setOrganizerOrder(final Integer order) {
        final DataObject link = (DataObject) get("link");

        link.set(ORDER, order);
    }

    public void swapWithNext(final GenericOrganizationalUnit organizer) {
        int currentPosition = 0;
        int currentIndex = 0;
        int nextIndex = 0;

        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getOrganizerOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getOrganizerOrder(): %d",
                                      getOrganizerOrder()));
            if (this.getOrganizer().equals(organizer)) {
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
            nextIndex = this.getOrganizerOrder();
        } else {
            throw new IllegalArgumentException(
                "The provided organizer is the last "
                    + "in the collection, so there is no next object "
                    + "to swap with.");
        }

        this.rewind();

        while (this.getPosition() != currentPosition) {
            this.next();
        }

        this.setOrganizerOrder(nextIndex);
        this.next();
        this.setOrganizerOrder(currentIndex);
        this.rewind();
    }

    public void swapWithPrevious(final GenericOrganizationalUnit organizer) {
        int previousPosition = 0;
        int previousIndex = 0;
        int currentPosition = 0;
        int currentIndex = 0;

        s_log.debug("Searching organizer...");
        this.rewind();
        while (this.next()) {
            currentPosition = this.getPosition();
            currentIndex = this.getOrganizerOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPosition,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getOrganizerOrder(): %d",
                                      getOrganizerOrder()));
            if (this.getOrganizer().equals(organizer)) {
                break;
            }

            previousPosition = currentPosition;
            previousIndex = currentIndex;
        }

        if (currentPosition == 0) {
            throw new IllegalArgumentException(
                String.format(
                    "The provided organizer is not "
                        + "part of this collection."));
        }

        if (previousPosition == 0) {
            throw new IllegalArgumentException(
                String.format(
                    "The provided organizer is the first one in this "
                        + "collection, so there is no previous one to switch "
                        + "with."));
        }

        this.rewind();
        while (this.getPosition() != previousPosition) {
            this.next();
        }

        this.setOrganizerOrder(currentIndex);
        this.next();
        this.setOrganizerOrder(previousIndex);
        this.rewind();
    }

    public GenericOrganizationalUnit getOrganizer() {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory
            .newInstance(m_dataCollection.getDataObject());
        
        return (GenericOrganizationalUnit) bundle.getPrimaryInstance();
    }
    
    public GenericOrganizationalUnit getOrganizer(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory
            .newInstance(m_dataCollection.getDataObject());
        
        return (GenericOrganizationalUnit) bundle.getInstance(language);
    }

}
