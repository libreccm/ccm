package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.importer.report.AuthorImportReport;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisConverter;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.AuthorData;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public abstract class AbstractRisConverter implements RisConverter {

    protected void processAuthors(final RisDataset dataset,
                                  final RisField risField,
                                  final ImporterUtil importerUtil,
                                  final Publication publication,
                                  final PublicationImportReport report,
                                  final boolean pretend) {
        processAuthors(dataset, risField, importerUtil, publication, false, report, pretend);
    }

    protected void processEditors(final RisDataset dataset,
                                  final RisField risField,
                                  final ImporterUtil importerUtil,
                                  final Publication publication,
                                  final PublicationImportReport report,
                                  final boolean pretend) {
        processAuthors(dataset, risField, importerUtil, publication, true, report, pretend);
    }

    private void processAuthors(final RisDataset dataset,
                                final RisField risField,
                                final ImporterUtil importerUtil,
                                final Publication publication,
                                final boolean isEditors,
                                final PublicationImportReport report,
                                final boolean pretend) {
        final List<String> authors = dataset.getValues().get(risField);
        if ((authors != null) && !authors.isEmpty()) {
            for (String authorStr : authors) {
                processAuthorStr(authorStr, isEditors, importerUtil, publication, report, pretend);
            }
        }
    }

    private void processAuthorStr(final String authorStr,
                                  final boolean editor,
                                  final ImporterUtil importerUtil,
                                  final Publication publication,
                                  final PublicationImportReport importReport,
                                  final boolean pretend) {
        final AuthorData authorData = new AuthorData();

        final String[] tokens = authorStr.split(",");
        if (tokens.length == 0) {
            importReport.addMessage(String.format("Failed to parse author string '%s'.", authorStr));
            return;
        }

        if (tokens.length >= 1) {
            authorData.setSurname(tokens[0]);
        }

        if (tokens.length >= 2) {
            authorData.setGivenName(tokens[1]);
        }

        authorData.setEditor(editor);

        final AuthorImportReport authorReport = importerUtil.processAuthor(publication, authorData, pretend);
        importReport.addAuthor(authorReport);
    }

    protected void processPublisher(final RisDataset dataset,
                                    final boolean pretend,
                                    final PublicationWithPublisher publication,
                                    final ImporterUtil importerUtil,
                                    final PublicationImportReport report) {
        final List<String> publisherList = dataset.getValues().get(RisField.PB);
        final List<String> placeList = dataset.getValues().get(RisField.CY);
        final String publisherName;
        if ((publisherList == null) || publisherList.isEmpty()) {
            publisherName = null;
        } else {
            publisherName = publisherList.get(0);
        }

        final String place;
        if ((placeList == null) || placeList.isEmpty()) {
            place = null;
        } else {
            place = placeList.get(0);
        }

        if (publisherName != null) {
            report.setPublisher(importerUtil.processPublisher(publication, place, publisherName, pretend));
        }
    }

    protected void processNumberOfPages(final RisDataset dataset,
                                        final boolean pretend,
                                        final PublicationWithPublisher publication,
                                        final PublicationImportReport report) {
        final List<String> numberOfPages = dataset.getValues().get(RisField.SP);
        if ((numberOfPages != null) && !numberOfPages.isEmpty()) {
            try {
                final int value = Integer.parseInt(numberOfPages.get(0));
                publication.setNumberOfPages(value);
                report.addField(new FieldImportReport("number of pages", numberOfPages.get(0)));
            } catch (NumberFormatException ex) {
                report.addMessage("Failed to parse number of pages");
            }
        }

    }

    protected void processNumberOfVolumes(final RisDataset dataset,
                                          final boolean pretend,
                                          final PublicationWithPublisher publication,
                                          final PublicationImportReport report) {
        final List<String> numberOfVols = dataset.getValues().get(RisField.NV);
        if ((numberOfVols != null) && !numberOfVols.isEmpty()) {
            try {
                final int value = Integer.parseInt(numberOfVols.get(0));
                publication.setNumberOfVolumes(value);
                report.addField(new FieldImportReport("number of volumes", numberOfVols.get(0)));
            } catch (NumberFormatException ex) {
                report.addMessage("Failed to parse number of volumes.");
            }
        }
    }

    protected void processVolume(final RisDataset dataset,
                                 final boolean pretend,
                                 final PublicationWithPublisher publication,
                                 final PublicationImportReport report) {
        final List<String> volume = dataset.getValues().get(RisField.VL);
        if ((volume != null) && !volume.isEmpty()) {
            try {
                final int value = Integer.parseInt(volume.get(0));
                publication.setVolume(value);
                report.addField(new FieldImportReport("volume", volume.get(0)));
            } catch (NumberFormatException ex) {
                report.addMessage("Failed to parse value of field 'volume'.");
            }
        }
    }

    protected void processYear(final RisDataset dataset,
                               final boolean pretend,
                               final Publication publication,
                               final PublicationImportReport report) {
        final String yearStr = dataset.getValues().get(RisField.PY).get(0);
        try {
            final int year = Integer.parseInt(yearStr);
            publication.setYearOfPublication(year);
            report.addField(new FieldImportReport("year", yearStr));
        } catch (NumberFormatException ex) {
            report.addMessage(String.format("Failed to convert year of publication value '%s' from RIS to"
                                            + "integer value. Setting year of publication to 0"));
            publication.setYearOfPublication(0);
        }
    }

}
