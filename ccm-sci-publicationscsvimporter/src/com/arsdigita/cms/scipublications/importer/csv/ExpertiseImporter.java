package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class ExpertiseImporter extends AbstractPublicationImporter<Expertise> {

    protected ExpertiseImporter(final CsvLine data, final PublicationImportReport report, final boolean pretend) {
        super(data, report, pretend);
    }

    @Override
    protected Expertise importPublication() {
        final Expertise expertise = super.importPublication();
        final CsvLine data = getData();
        final PublicationImportReport report = getReport();

        if ((data.getPlace() != null) && !data.getPlace().isEmpty()) {
            if (!isPretend()) {
                expertise.setPlace(data.getPlace());
            }
            report.addField(new FieldImportReport("Place", data.getPlace()));
        }

        processNumberOfPages(expertise);

        return expertise;
    }

    private void processNumberOfPages(final Expertise publication) {
        if ((getData().getNumberOfPages() != null) && !getData().getNumberOfPages().isEmpty()) {
            try {
                final int volume = Integer.parseInt(getData().getNumberOfPages());
                if (!isPretend()) {
                    publication.setNumberOfPages(volume);
                }
                getReport().addField(new FieldImportReport("Number of pages", getData().getNumberOfPages()));
            } catch (NumberFormatException ex) {
                getReport().addMessage(String.format("Failed to parse numberOfPages data in line %d.",
                                                     getData().getLineNumber()));
            }
        }
    }

    @Override
    protected Expertise createPublication() {
        if (isPretend()) {
            return null;
        } else {
            return new Expertise();
        }
    }

    @Override
    protected PublicationBundle createBundle(final Expertise expertise) {
        if (isPretend()) {
            return null;
        } else {
            return new PublicationBundle(expertise);
        }
    }

}
