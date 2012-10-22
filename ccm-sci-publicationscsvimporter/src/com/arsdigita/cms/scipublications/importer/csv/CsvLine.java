package com.arsdigita.cms.scipublications.importer.csv;

import java.util.Arrays;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class CsvLine {

    private final String[] cols;
    private final int lineNumber;

    public CsvLine(final String[] cols, final int lineNumber) {
        if (cols.length != 30) {
            throw new IllegalArgumentException(String.format("Unexpected number of columns. Expected 30 columns, "
                                                             + "but provided array has %d entries.", cols.length));
        }
        this.cols = Arrays.copyOf(cols, cols.length);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
    
    public String getTitle() {
        return cols[0].trim();
    }

    public String getYear() {
        return cols[1].trim();
    }

    public String getAuthors() {
        return cols[2].trim();
    }
    
    public String getType() {
        return cols[3].trim();
    }
    
    public String getAbstract() {
        return cols[4].trim();
    }
    
    public String getMisc() {
        return cols[5].trim();
    }
    
    public String getReviewed() {
        return cols[6].trim();
    }
    
    public String getDepartment() {
        return cols[7].trim();
    }
    
    public String getPlace() {
        return cols[8].trim();
    }
    
    public String getPublisher() {
        return cols[9].trim();
    }
    
    public String getVolume() {
        return cols[10].trim();
    }
    
    public String getNumberOfVolumes() {
        return cols[11].trim();
    }
    
    public String getNumberOfPages() {
        return cols[12].trim();
    }
    
    public String getEdition() {
        return cols[13].trim();
    }
    
    public String getPageFrom() {
        return cols[14].trim();
    }
    
    public String getPageTo() {
        return cols[15].trim();
    }
    
    public String getChapter() {
        return cols[16].trim();
    }
    
    public String getIssue() {
        return cols[17].trim();
    }
    
    public String getPublicationDate() {
        return cols[18].trim();
    }
    
    public String getJournal() {
        return cols[19].trim();
    }
    
    public String getCollectedVolume() {
        return cols[20].trim();
    }
    
    public String getCollectedVolumeAuthors() {
        return cols[21].trim();
    }
    
    public String getIsbn() {
        return cols[22].trim();
    }
    
    public String getIssn() {
        return cols[23].trim();
    }
    
    public String getLastAccess() {
        return cols[24].trim();
    }
    
    public String getUrl() {
        return cols[25].trim();
    }
    
    public String getUrn() {
        return cols[26].trim();
    }
    
    public String getDoi() {
        return cols[27].trim();
    }
    
    public String getConference() {
        return cols[28].trim();
    }
    
    public String getScope() {
        return cols[29].trim();
    }
}
