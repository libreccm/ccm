package com.arsdigita.cms.scipublications.importer.report;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ProceedingsImportReport {

    private String proceedingsTitle;
    private String conference;
    private Date dateFromOfConference;
    private Date dateToOfConference;
    private List<AuthorImportReport> authors = new ArrayList<AuthorImportReport>();
    private boolean created;

    public String getProceedingsTitle() {
        return proceedingsTitle;
    }

    public void setProceedingsTitle(final String proceedingsTitle) {
        this.proceedingsTitle = proceedingsTitle;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(final String conference) {
        this.conference = conference;
    }

    public Date getDateFromOfConference() {
        return dateFromOfConference;
    }

    public void setDateFromOfConference(final Date dateFromOfConference) {
        this.dateFromOfConference = dateFromOfConference;
    }

    public Date getDateToOfConference() {
        return dateToOfConference;
    }

    public void setDateToOfConference(final Date dateToOfConference) {
        this.dateToOfConference = dateToOfConference;
    }

    public List<AuthorImportReport> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    public void addAuthor(final AuthorImportReport author) {
        authors.add(author);
    }

    public void setAuthors(final List<AuthorImportReport> authors) {
        this.authors = authors;
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
            final StringWriter strWriter = new StringWriter();
            final PrintWriter writer = new PrintWriter(strWriter);
                        
            for (int i = 0; i < 40; i++) {
                writer.append("- ");
            }

            writer.printf("Created proceedings '%s' and linked them with publication.");
            writer.printf("Conference.............: %s\n", conference);
            writer.printf("Date from of conference: %s\n", dateToOfConference.toString());
            writer.printf("Date to of conference..: %s\n", dateToOfConference.toString());
            writer.print("Authors:\n");
            for(AuthorImportReport author : authors) {
                writer.printf("%s\n", author.toString());
            }
                        
            for (int i = 0; i < 40; i++) {
                writer.append("- ");
            }

            return strWriter.toString();
        } else {
            return String.format("Found procceedings '%s' in database and linked them with publications.",
                                 proceedingsTitle);
        }
    }

}
