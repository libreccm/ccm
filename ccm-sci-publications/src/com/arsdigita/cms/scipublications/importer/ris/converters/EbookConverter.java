package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.contenttypes.PublicationWithPublisherBundle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisAuthorUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisFieldUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisOrgaUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisSeriesUtil;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 * Converter for the RIS type {@code EBOOK} to the SciPublications type {@link Monograph}.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class EbookConverter extends AbstractRisConverter<Monograph, PublicationWithPublisherBundle> {

    @Override
    protected Monograph createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new Monograph();
        }
    }

    @Override
    protected PublicationWithPublisherBundle createBundle(final Monograph publication,
                                                          final boolean pretend) {
        return new PublicationWithPublisherBundle(publication);
    }

    @Override
    protected void processFields(final RisDataset dataset,
                                 final Monograph publication, final ImporterUtil importerUtil,
                                 final PublicationImportReport importReport,
                                 final boolean pretend) {
        final RisFieldUtil fieldUtil = new RisFieldUtil(pretend);
        final RisAuthorUtil authorUtil = new RisAuthorUtil(importerUtil, pretend);
        final RisOrgaUtil orgaUtil = new RisOrgaUtil(importerUtil, pretend);
        final RisSeriesUtil seriesUtil = new RisSeriesUtil(importerUtil, pretend);

        fieldUtil.processIntField(dataset, RisField.PY, publication, "yearOfPublication", importReport);

        authorUtil.processAuthors(dataset, RisField.AU, publication, importReport);
        authorUtil.processEditors(dataset, RisField.A3, publication, importReport);

        orgaUtil.processPublisher(dataset, RisField.PB, RisField.CY, publication, importReport);

        fieldUtil.processField(dataset, RisField.AB, publication, "abstract", importReport);

        fieldUtil.processField(dataset, RisField.ET, publication, "edition", importReport);

        fieldUtil.processIsbn(dataset, RisField.SN, publication, importReport);

        fieldUtil.processIntField(dataset, RisField.SP, publication, "numberOfPages", importReport);

        fieldUtil.processIntField(dataset, RisField.VL, publication, "volume", importReport);

        seriesUtil.processSeries(dataset, RisField.T2, publication, importReport);
    }

    public RisType getRisType() {
        return RisType.EBOOK;
    }

}
