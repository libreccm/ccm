package com.arsdigita.cms.scipublications.importer.report;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SeriesImportReport {
    
    private String seriesTitle;
    private boolean created;

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(final String seriesTitle) {
        this.seriesTitle = seriesTitle;
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
            return String.format("Created series '%s' and linked series to publication.", seriesTitle);
        } else {
            return String.format("Found series '%s' in database and linked series to publication.", seriesTitle);
        }
    }
    
    
    
}
