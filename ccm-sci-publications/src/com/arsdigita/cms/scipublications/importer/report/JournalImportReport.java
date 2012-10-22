package com.arsdigita.cms.scipublications.importer.report;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class JournalImportReport {

    private String journalTitle;
    private boolean created;

    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(final String journalTitle) {
        this.journalTitle = journalTitle;
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
            return String.format("Created journal '%s' and linked it to publication.");
        } else {
            return String.format("Found journal '%s' in database and linked it to publication.");
        }
    }

}
