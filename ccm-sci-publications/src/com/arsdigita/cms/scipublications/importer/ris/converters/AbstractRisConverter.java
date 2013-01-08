package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public abstract class AbstractRisConverter implements RisConverter {

    protected void processField(final RisDataset dataset,
                                final RisField field,
                                final Publication publication,
                                final String targetField,
                                final PublicationImportReport report,
                                final boolean pretend) {
        final List<String> values = dataset.getValues().get(field);
        if ((values != null) && !values.isEmpty()) {
            publication.set(targetField, values.get(0));
            report.addField(new FieldImportReport(targetField, values.get(0)));
        }
    }

    protected void processIntField(final RisDataset dataset,
                                   final RisField field,
                                   final Publication publication,
                                   final String targetField,
                                   final PublicationImportReport report,
                                   final boolean pretend) {
        final List<String> values = dataset.getValues().get(field);
        if ((values != null) && !values.isEmpty()) {
            final String valueStr = values.get(0);
            try {
                final int value = Integer.parseInt(valueStr);
                publication.set(targetField, value);
                publication.set(targetField, valueStr);
            } catch (NumberFormatException ex) {
                report.addMessage(String.format("Failed to parse value of field '%s' into an integer for dataset "
                                                + "starting on line %d.",
                                                field,
                                                dataset.getFirstLine()));
            }
        }
    }

    protected void processTitle(final RisDataset dataset,
                                final Publication publication,
                                final PublicationImportReport report,
                                final boolean pretend) {
        publication.setTitle(dataset.getValues().get(RisField.TI).get(0));
        publication.setTitle(dataset.getValues().get(RisField.TI).get(0));
    }

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
                processAuthorStr(authorStr,
                                 isEditors,
                                 importerUtil,
                                 publication,
                                 report,
                                 dataset.getFirstLine(),
                                 pretend);
            }
        }
    }

    private void processAuthorStr(final String authorStr,
                                  final boolean editor,
                                  final ImporterUtil importerUtil,
                                  final Publication publication,
                                  final PublicationImportReport importReport,
                                  final int firstLine,
                                  final boolean pretend) {
        final AuthorData authorData = new AuthorData();

        final String[] tokens = authorStr.split(",");
        if (tokens.length == 0) {
            importReport.addMessage(String.format("Failed to parse author string '%s' at dataset starting at line %d.",
                                                  authorStr, firstLine));
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
                report.addMessage(String.format("Failed to parse number of pages at dataset starting at line %d",
                                                dataset.getFirstLine()));
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
                report.addMessage(String.format("Failed to parse number of volumes at dataset starting at line %d.",
                                                dataset.getFirstLine()));
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
                report.addMessage(String.format("Failed to parse value of field 'volume' on dataset starting "
                                                + "at line %d.", dataset.getFirstLine()));
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
                                            + "integer value on dataset starting at line %d. Setting year of "
                                            + "publication to 0", dataset.getFirstLine()));
            publication.setYearOfPublication(0);
        }
    }

    protected void processSeries(final RisDataset dataset,
                                 final RisField field,
                                 final Publication publication,
                                 final ImporterUtil importerUtil,
                                 final boolean pretend,
                                 final PublicationImportReport report) {
        final List<String> series = dataset.getValues().get(field);
        if ((series != null) && !series.isEmpty()) {
            report.setSeries(importerUtil.processSeries(publication, series.get(0), pretend));
        }
    }

    protected void processJournal(final RisDataset dataset,
                                  final RisField field,
                                  final ArticleInJournal article,
                                  final ImporterUtil importerUtil,
                                  final boolean pretend,
                                  final PublicationImportReport report) {
        final List<String> journal = dataset.getValues().get(field);
        if ((journal != null) && !journal.isEmpty()) {
            report.setJournal(importerUtil.processJournal(article, journal.get(0), pretend));
        }
    }

    private AuthorData createAuthorData(final String[] tokens) {
        final AuthorData authorData = new AuthorData();
        
        if (tokens.length == 0) {
            throw new IllegalArgumentException("No author data tokens!");
        } 
        
        if(tokens.length >= 1) {
            authorData.setSurname(tokens[0]);
        }
        
        if (tokens.length >= 2) {
            authorData.setGivenName(tokens[1]);
        }
                
        return authorData;
    }
    
    protected void processCollectedVolume(final RisDataset dataset,
                                          final RisField cvTitleField,
                                          final RisField cvYearField,
                                          final RisField cvEditorsField,
                                          final RisField cvPlaceField,
                                          final RisField cvPublisherField,
                                          final RisField cvEditionField,
                                          final ArticleInCollectedVolume article,
                                          final ImporterUtil importerUtil,
                                          final boolean pretend,
                                          final PublicationImportReport report) {
        final List<String> colVolTitle = dataset.getValues().get(cvTitleField);
        final List<String> colVolYear = dataset.getValues().get(cvYearField);
        final List<String> colVolPlace = dataset.getValues().get(cvPlaceField);
        final List<String> colVolPublisher = dataset.getValues().get(cvPublisherField);
        final List<String> colVolEdition = dataset.getValues().get(cvEditionField);
        
        final List<String> colVolEditors = dataset.getValues().get(cvEditorsField);
        final List<AuthorData> colVolEditorData = new ArrayList<AuthorData>();
        
        for(String collVolEditor : colVolEditors) {
            final String[] tokens = collVolEditor.split(",");
            
            colVolEditorData.add(createAuthorData(tokens));
        }
        
        if ((colVolTitle != null) && !colVolTitle.isEmpty()) {
            report.setCollectedVolume(importerUtil.processCollectedVolume(article, 
                                                                          colVolTitle.get(0), 
                                                                          colVolYear.get(0), 
                                                                          colVolEditorData, 
                                                                          colVolPublisher.get(0), 
                                                                          colVolPlace.get(0), 
                                                                          colVolEdition.get(0),
                                                                          pretend));
        }
    }

    protected void processPages(final RisDataset dataset,
                                final RisField field,
                                final Publication publication,
                                final boolean pretend,
                                final PublicationImportReport report) {
        final List<String> values = dataset.getValues().get(field);
        final String pages = values.get(0);

        final String[] tokens = pages.split("-");
        if (tokens.length == 2) {
            try {
                final int pagesFrom = Integer.parseInt(tokens[0]);
                final int pagesTo = Integer.parseInt(tokens[1]);

                publication.set("pagesFrom", pagesFrom);
                publication.set("pagesTo", pagesTo);

                report.addField(new FieldImportReport("pagesFrom", Integer.toString(pagesFrom)));
                report.addField(new FieldImportReport("pagesTo", Integer.toString(pagesTo)));
            } catch (NumberFormatException ex) {
                report.addMessage(String.format("Failed to parse pages value in dataset starting at line %d. "
                                                + "On of the values given is not an integer.",
                                                dataset.getFirstLine()));
            }
        } else if (tokens.length == 1) {
            try {
                final int pagesFrom = Integer.parseInt(tokens[0]);

                publication.set("pagesFrom", pagesFrom);

                report.addField(new FieldImportReport("pagesFrom", Integer.toString(pagesFrom)));
            } catch (NumberFormatException ex) {
                report.addMessage(String.format("Failed to parse pages value in dataset starting at line %d. "
                                                + "Value is not an integer.",
                                                dataset.getFirstLine()));
            }
        } else if (tokens.length > 2) {
            report.addMessage(String.format("Failed to parse pages value in dataset starting at line %d. "
                                            + "Invalid format",
                                            dataset.getFirstLine()));
        }
    }
}
