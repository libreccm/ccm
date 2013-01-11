package com.arsdigita.cms.scipublications.importer.ris.converters.utils;

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.importer.report.CollectedVolumeImportReport;
import com.arsdigita.cms.scipublications.importer.report.ProceedingsImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
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
public class RisColVolUtil {

    private final ImporterUtil importerUtil;
    private final boolean pretend;

    public RisColVolUtil(final ImporterUtil importerUtil, final boolean pretend) {
        this.importerUtil = importerUtil;
        this.pretend = pretend;
    }

    public void processCollectedVolume(final RisDataset dataset,
                                       final RisField cvTitleField,
                                       final RisField cvYearField,
                                       final RisField cvEditorsField,
                                       final RisField cvPlaceField,
                                       final RisField cvPublisherField,
                                       final RisField cvEditionField,
                                       final ArticleInCollectedVolume article,
                                       final PublicationImportReport report) {
        final List<String> colVolTitle = dataset.getValues().get(cvTitleField);
        final List<String> colVolYear = dataset.getValues().get(cvYearField);
        final List<String> colVolPlace = dataset.getValues().get(cvPlaceField);
        final List<String> colVolPublisher = dataset.getValues().get(cvPublisherField);
        final List<String> colVolEdition = dataset.getValues().get(cvEditionField);

        final List<String> colVolEditors = dataset.getValues().get(cvEditorsField);
        final List<AuthorData> colVolEditorData = new ArrayList<AuthorData>();

        for (String colVolEditor : colVolEditors) {
            final String[] tokens = colVolEditor.split(",");

            colVolEditorData.add(createAuthorData(tokens));
        }

        if ((colVolTitle != null) && !colVolTitle.isEmpty()) {
            final CollectedVolumeImportReport colVolReport = importerUtil.processCollectedVolume(article,
                                                                                                 colVolTitle.get(0),
                                                                                                 colVolYear.get(0),
                                                                                                 colVolEditorData,
                                                                                                 colVolPublisher.get(0),
                                                                                                 colVolPlace.get(0),
                                                                                                 colVolEdition.get(0),
                                                                                                 pretend);
            report.setCollectedVolume(colVolReport);
        }
    }

    @SuppressWarnings("PMD.LongVariable")
    public void processProceedings(final RisDataset dataset,
                                   final RisField procTitleField,
                                   final RisField procYearField,
                                   final RisField procConfNameField,
                                   final RisField procEditorsField,
                                   final RisField procPublisherField,
                                   final RisField procPlaceField,
                                   final InProceedings inProceedings,
                                   final PublicationImportReport report) {
        final List<String> procTitle = dataset.getValues().get(procTitleField);
        final List<String> procYear = dataset.getValues().get(procYearField);
        final List<String> procConfName = dataset.getValues().get(procConfNameField);
        final List<String> procPublisher = dataset.getValues().get(procPublisherField);
        final List<String> procPlace = dataset.getValues().get(procPlaceField);

        final List<String> procEditors = dataset.getValues().get(procEditorsField);
        final List<AuthorData> procEditorData = new ArrayList<AuthorData>();

        for (String procEditor : procEditors) {
            final String[] tokens = procEditor.split(", ");

            procEditorData.add(createAuthorData(tokens));
        }

        if ((procTitle != null) && !procTitle.isEmpty()) {
            final ProceedingsImportReport procReport = importerUtil.processProceedings(inProceedings,
                                                                                       procTitle.get(0),
                                                                                       procYear.get(0),
                                                                                       procConfName.get(0),
                                                                                       procEditorData,
                                                                                       procPublisher.get(0),
                                                                                       procPlace.get(0),
                                                                                       pretend);
            report.setProceedings(procReport);
        }
    }

    private AuthorData createAuthorData(final String[] tokens) {
        final AuthorData authorData = new AuthorData();

        if (tokens.length == 0) {
            throw new IllegalArgumentException("No author data tokens!");
        }

        if (tokens.length >= 1) {
            authorData.setSurname(tokens[0]);
        }

        if (tokens.length >= 2) {
            authorData.setGivenName(tokens[1]);
        }

        return authorData;
    }

}
