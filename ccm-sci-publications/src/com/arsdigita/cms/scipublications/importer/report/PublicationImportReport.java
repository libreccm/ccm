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
public class PublicationImportReport {
    
    private String title;
    private String type;
    private boolean alreadyInDatabase;
    private List<FieldImportReport> fields = new ArrayList<FieldImportReport>();
    private List<AuthorImportReport> authors = new ArrayList<AuthorImportReport>();
    private PublisherImportReport publisher;
    private CollectedVolumeImportReport collectedVolume;
    private JournalImportReport journal;
    private ProceedingsImportReport proceedings;
    private List<OrganizationalUnitImportReport> orgaUnits = new ArrayList<OrganizationalUnitImportReport>();
    private boolean successful;
    private List<String> messages = new ArrayList<String>();

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean isAlreadyInDatabase() {
        return alreadyInDatabase;
    }

    public void setAlreadyInDatabase(final boolean alreadyInDatabase) {
        this.alreadyInDatabase = alreadyInDatabase;
    }

    public List<FieldImportReport> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public void addField(final FieldImportReport field) {
        fields.add(field);
    }
    
    public void setFields(final List<FieldImportReport> fields) {
        this.fields = fields;
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

    public PublisherImportReport getPublisher() {
        return publisher;
    }

    public void setPublisher(final PublisherImportReport publisher) {
        this.publisher = publisher;
    }
        
    public CollectedVolumeImportReport getCollectedVolume() {
        return collectedVolume;
    }

    public void setCollectedVolume(final CollectedVolumeImportReport collectedVolume) {
        this.collectedVolume = collectedVolume;
    }

    public JournalImportReport getJournal() {
        return journal;
    }

    public void setJournal(final JournalImportReport journal) {
        this.journal = journal;
    }

    public ProceedingsImportReport getProceedings() {
        return proceedings;
    }

    public void setProceedings(final ProceedingsImportReport proceedings) {
        this.proceedings = proceedings;
    }

    public List<OrganizationalUnitImportReport> getOrgaUnits() {
        return Collections.unmodifiableList(orgaUnits);
    }

    public void addOrgaUnit(final OrganizationalUnitImportReport orgaUnit) {
        orgaUnits.add(orgaUnit);
    }
    
    public void setOrgaUnits(final List<OrganizationalUnitImportReport> orgaUnits) {
        this.orgaUnits = orgaUnits;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(final boolean successful) {
        this.successful = successful;
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(final String message) {
        messages.add(message);
    }
    
    public void setMessage(final List<String> messages) {
        this.messages = messages;
    }
            
    @Override
    public String toString() {
        final StringWriter strWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(strWriter);
        
        writer.printf("%24s: %s\n", "title", title);
        writer.printf("%24s: %s\n", "type", type);
        writer.printf("%24s: %b\n", successful);
        if (!successful) {
            writer.printf("Import failed. Messages from importer:\n ");
            for(String message : messages) {
                writer.printf("%s\n", message);
            }
            return strWriter.toString();
        }
        writer.printf("%24s: %b\n", "Already in database", alreadyInDatabase);
        writer.printf("Authors:");
        for(AuthorImportReport author: authors) {
            writer.printf("%s\n", author.toString());
        }
        if (publisher != null) {
            writer.printf("%s\n", publisher.toString());
        }
        if (collectedVolume != null) {
            writer.printf("Collected volume:\n%s\n", collectedVolume.toString());
        }
        if (journal != null) {
            writer.printf("Journal: %s\n", journal.toString());
        }
        if (proceedings != null) {
            writer.printf("Proceedings:\n%s\n", proceedings.toString());
        }
        for(FieldImportReport field : fields) {
            writer.printf("%s\n", field.toString());
        }
        for(OrganizationalUnitImportReport orgaUnit : orgaUnits) {
            writer.printf("%s\n", orgaUnit.toString());
        }
                
        writer.printf("Messages:\n");
        for(String message : messages) {
            writer.printf("%s\n", message);
        }
        
        return strWriter.toString();
    }
    
    
}
