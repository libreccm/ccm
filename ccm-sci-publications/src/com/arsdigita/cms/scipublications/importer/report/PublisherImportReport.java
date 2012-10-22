package com.arsdigita.cms.scipublications.importer.report;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublisherImportReport {

    private String publisherName;
    private String place;
    private boolean created;

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(final String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(final String place) {
        this.place = place;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(final boolean created) {
        this.created = created;
    }

    @Override
    public String toString() {
        if (created) {
            return String.format("Created publisher '%s: %s' and linked publisher to publication.",
                                 place, publisherName);
        } else {
            return String.format("Found publisher '%s: %s' in database and linked publisher to publication.",
                                 place, publisherName);
        }
    }

}
