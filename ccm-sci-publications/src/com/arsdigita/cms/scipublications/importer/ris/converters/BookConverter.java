package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.AuthorImportReport;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisConverter;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.AuthorData;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.util.List;

/**
 * Converter for the RIS type {@code BOOK} to the SciPublications type {@link Monograph}.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class BookConverter implements RisConverter {

    public PublicationImportReport convert(final RisDataset dataset,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();
        report.setType(Monograph.BASE_DATA_OBJECT_TYPE);

        final Monograph monograph = new Monograph();

        monograph.setTitle(dataset.getValues().get(RisField.TI).get(0));
        report.setTitle(dataset.getValues().get(RisField.TI).get(0));

        final String yearStr = dataset.getValues().get(RisField.PY).get(0);
        try {
            final int year = Integer.parseInt(yearStr);
            monograph.setYearOfPublication(year);
            report.addField(new FieldImportReport("year", yearStr));
        } catch (NumberFormatException ex) {
            report.addMessage(String.format("Failed to convert year of publication value '%s' from RIS to"
                                            + "integer value. Setting year of publication to 0"));
            monograph.setYearOfPublication(0);
        }

        final List<String> authors = dataset.getValues().get(RisField.AU);
        if ((authors != null) && !authors.isEmpty()) {
            for (String authorStr : authors) {
                processAuthorStr(authorStr, false, importerUtil, monograph, report, pretend);
            }
        }

        final List<String> editors = dataset.getValues().get(RisField.A3);
        if ((editors != null) && !editors.isEmpty()) {
            for (String editorStr : editors) {
                processAuthorStr(editorStr, true, importerUtil, monograph, report, pretend);
            }
        }

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
            report.setPublisher(importerUtil.processPublisher(monograph, place, publisherName, pretend));
        }

        final List<String> abstractList = dataset.getValues().get(RisField.AB);
        if ((abstractList != null) && (!abstractList.isEmpty())) {
            monograph.setAbstract(abstractList.get(0));
            report.addField(new FieldImportReport("abstract", abstractList.get(0)));
        }

        final List<String> edition = dataset.getValues().get(RisField.ET);
        if ((edition != null) && !edition.isEmpty()) {
            monograph.setEdition(edition.get(0));
            report.addField(new FieldImportReport("edition", edition.get(0)));
        }

        final List<String> numberOfVols = dataset.getValues().get(RisField.NV);
        if ((numberOfVols != null) && !numberOfVols.isEmpty()) {
            try {
                final int value = Integer.parseInt(numberOfVols.get(0));
                monograph.setNumberOfVolumes(value);
                report.addField(new FieldImportReport("number of volumes", numberOfVols.get(0)));
            } catch (NumberFormatException ex) {
                report.addMessage("Failed to parse number of volumes.");
            }
        }
        
        final List<String> isbn = dataset.getValues().get(RisField.SN);
        if ((isbn != null) && !isbn.isEmpty()) {
            monograph.setISBN(isbn.get(0));
            report.addField(new FieldImportReport("isbn", isbn.get(0)));
        }
        
        final List<String> numberOfPages = dataset.getValues().get(RisField.SP);
        if ((numberOfPages != null) && !numberOfPages.isEmpty()) {
            try {
                final int value = Integer.parseInt(numberOfPages.get(0));
                monograph.setNumberOfPages(value);
                report.addField(new FieldImportReport("number of pages", numberOfPages.get(0)));                
            } catch(NumberFormatException ex) {
                report.addMessage("Failed to parse number of pages");
            }
        }
        
        final List<String> volume = dataset.getValues().get(RisField.VL);
        if ((volume != null) && !volume.isEmpty()) {
            try {
                final int value = Integer.parseInt(volume.get(0));
                monograph.setVolume(value);
                report.addField(new FieldImportReport("volume", volume.get(0)));                
            } catch(NumberFormatException ex) {
                report.addMessage("Failed to parse value of field 'volume'.");                
            }
        }
        
        final List<String> series = dataset.getValues().get(RisField.T2);
        if ((series != null) && !series.isEmpty()) {
            report.setSeries(importerUtil.processSeries(monograph, series.get(0), pretend));
        }

        return report;
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

    public RisType getRisType() {
        return RisType.BOOK;
    }

}
