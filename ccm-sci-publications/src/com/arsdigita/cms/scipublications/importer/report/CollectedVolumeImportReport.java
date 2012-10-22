package com.arsdigita.cms.scipublications.importer.report;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class CollectedVolumeImportReport {
    
    private String collectedVolumeTitle;
    private List<AuthorImportReport> authors = new ArrayList<AuthorImportReport>();
    private boolean created;

    public String getCollectedVolumeTitle() {
        return collectedVolumeTitle;
    }

    public void setCollectedVolumeTitle(final String collectedVolumeTitle) {
        this.collectedVolumeTitle = collectedVolumeTitle;
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
                                    
            for(int i = 0; i < 40; i++) {
                writer.append("- ");
            }
            
            writer.printf("Created collected volume '%s' and linked it with publication.\n");
            writer.print("Author of collected volume:\n");            
            for(AuthorImportReport author : authors) {
                writer.printf("%s\n", author.toString());
            }
            
            for(int i = 0; i < 40; i++) {
                writer.append("- ");
            }
            
            return strWriter.toString();
        } else {
            return String.format("Found collected volume '%s' and linked it with publications.", collectedVolumeTitle);
        }        
    }
    
}
