package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.ProceedingsBundle;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class ProceedingsImporter extends AbstractPublicationWithPublisherImporter<Proceedings> {

    public ProceedingsImporter(final CsvLine data, final PublicationImportReport report, final boolean pretend) {
        super(data, report, pretend);
    }

    @Override
    public Proceedings importPublication() {
        final Proceedings proceedings = super.importPublication();
        final CsvLine data = getData();
        final PublicationImportReport report = getReport();

        if ((data.getConference() != null) && !data.getConference().isEmpty()) {
            if (!isPretend()) {
                proceedings.setNameOfConference(data.getConference());
            }
            report.addField(new FieldImportReport("Name of conference", data.getConference()));
        }

        return proceedings;
    }

    @Override
    protected Proceedings createPublication() {
        if (isPretend()) {
            return null;
        } else {
            return new Proceedings();
        }
    }

    @Override
    protected PublicationBundle createBundle(final Proceedings proceedings) {
        if (isPretend()) {
            return null;
        } else {
            return new ProceedingsBundle(proceedings);
        }
    }

}
