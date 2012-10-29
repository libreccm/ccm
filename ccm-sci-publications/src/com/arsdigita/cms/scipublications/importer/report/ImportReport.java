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
public class ImportReport {

    private String importer;
    private boolean pretend;
    private List<PublicationImportReport> publications = new ArrayList<PublicationImportReport>();

    public String getImporter() {
        return importer;
    }

    public void setImporter(final String importer) {
        this.importer = importer;
    }

    public List<PublicationImportReport> getPublications() {
        return Collections.unmodifiableList(publications);
    }

    public void addPublication(final PublicationImportReport publication) {
        publications.add(publication);
    }

    public void setPublications(final List<PublicationImportReport> publications) {
        this.publications = publications;
    }

    public boolean isPretend() {
        return pretend;
    }

    public void setPretend(final boolean pretend) {
        this.pretend = pretend;
    }
        
    @Override
    public String toString() {
        final StringWriter strWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(strWriter);

        writer.printf("Importer.......................: %s\n", importer);
        writer.printf("Number of publications imported: %d\n", publications.size());
        writer.printf("Pretend mode: %b\n", pretend);
        if (pretend) {
            writer.printf("Pretend mode is active. None of the publications in this report have been imported. The "
                    + "report only shows what would be done if the publications are truly imported.\n");
        }
        for(PublicationImportReport publication: publications) {
            for(int i = 0; i < 80; i++) {
                writer.append('-');
            }
            writer.append('\n');
            
            writer.append(publication.toString());
            
            for(int i = 0; i < 80; i++) {
                writer.append('-');
            }
            writer.append('\n');
        }
        
        return strWriter.toString();
    }

}
