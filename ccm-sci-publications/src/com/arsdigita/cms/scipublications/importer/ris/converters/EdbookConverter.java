package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.CollectedVolumeBundle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisAuthorUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisFieldUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisOrgaUtil;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class EdbookConverter extends AbstractRisConverter<CollectedVolume, CollectedVolumeBundle> {    

    @Override
    protected CollectedVolume createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new CollectedVolume();
        }
    }

    @Override
    protected CollectedVolumeBundle createBundle(final CollectedVolume publication,
                                                 final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new CollectedVolumeBundle(publication);
        }
    }

    @Override
    protected void processFields(final RisDataset dataset,
                                 final CollectedVolume publication,
                                 final ImporterUtil importerUtil,
                                 final PublicationImportReport importReport,
                                 final boolean pretend) {
        final RisFieldUtil fieldUtil = new RisFieldUtil(pretend);
        final RisAuthorUtil authorUtil = new RisAuthorUtil(importerUtil, pretend);
        final RisOrgaUtil orgaUtil = new RisOrgaUtil(importerUtil, pretend);

        authorUtil.processEditors(dataset, RisField.AU, publication, importReport);

        fieldUtil.processIntField(dataset, RisField.PY, publication, "yearOfPublication", importReport);

        orgaUtil.processPublisher(dataset, RisField.PB, RisField.CY, publication, importReport);

        fieldUtil.processField(dataset, RisField.AB, publication, "abstract", importReport);
        fieldUtil.processField(dataset, RisField.ET, publication, "edition", importReport);
        fieldUtil.processIntField(dataset, RisField.NV, publication, "numberOfVolumes", importReport);
        fieldUtil.processField(dataset, RisField.SN, publication, "isbn", importReport);
        fieldUtil.processIntField(dataset, RisField.SP, publication, "numberOfPages", importReport);
        fieldUtil.processIntField(dataset, RisField.VL, publication, "volume", importReport);
    }

    public RisType getRisType() {
        return RisType.EDBOOK;
    }

}
