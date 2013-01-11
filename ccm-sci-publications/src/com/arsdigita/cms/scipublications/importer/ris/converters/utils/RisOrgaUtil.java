package com.arsdigita.cms.scipublications.importer.ris.converters.utils;

import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.UnPublished;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.util.List;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RisOrgaUtil {

    private final ImporterUtil importerUtil;
    private final boolean pretend;

    public RisOrgaUtil(final ImporterUtil importerUtil, final boolean pretend) {
        this.importerUtil = importerUtil;
        this.pretend = pretend;
    }

    public void processPublisher(final RisDataset dataset, 
                                 final RisField publisherField, 
                                 final RisField placeField, 
                                 final PublicationWithPublisher publication, 
                                 final PublicationImportReport report) {
        final List<String> publisherList = dataset.getValues().get(publisherField);
        final List<String> placeList = dataset.getValues().get(placeField);
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

    public void processOrganization(final RisDataset dataset,
                                    final RisField field,
                                    final boolean pretend,
                                    final UnPublished publication,
                                    final ImporterUtil importerUtil,
                                    final PublicationImportReport report) {
        final List<String> orgaList = dataset.getValues().get(field);

        final String orgaName;
        if ((orgaList == null) || orgaList.isEmpty()) {
            orgaName = null;
        } else {
            orgaName = orgaList.get(0);
        }

        if (orgaName != null) {
            report.addOrgaUnit(importerUtil.processOrganization(publication, orgaName, pretend));
        }
    }

    public void processOrganization(final RisDataset dataset,
                                    final RisField field,
                                    final boolean pretend,
                                    final InternetArticle publication,
                                    final ImporterUtil importerUtil,
                                    final PublicationImportReport report) {
        final List<String> orgaList = dataset.getValues().get(field);

        final String orgaName;
        if ((orgaList == null) || orgaList.isEmpty()) {
            orgaName = null;
        } else {
            orgaName = orgaList.get(0);
        }

        if (orgaName != null) {
            report.addOrgaUnit(importerUtil.processOrganization(publication, orgaName, pretend));
        }
    }

}
