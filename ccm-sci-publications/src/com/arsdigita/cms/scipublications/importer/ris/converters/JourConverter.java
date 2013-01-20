package com.arsdigita.cms.scipublications.importer.ris.converters;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.ArticleInJournalBundle;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.ris.RisDataset;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisAuthorUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisFieldUtil;
import com.arsdigita.cms.scipublications.importer.ris.converters.utils.RisJournalUtil;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class JourConverter extends AbstractRisConverter<ArticleInJournal, ArticleInJournalBundle> {  

    @Override
    protected ArticleInJournal createPublication(final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new ArticleInJournal();
        }
    }

    @Override
    protected String getTypeName() {
        return ArticleInJournal.class.getName();
    }
    
    @Override
    protected ArticleInJournalBundle createBundle(final ArticleInJournal publication, final boolean pretend) {
        if (pretend) {
            return null;
        } else {
            return new ArticleInJournalBundle(publication);
        }
    }

    @Override
    protected void processFields(final RisDataset dataset,
                                 final ArticleInJournal publication, final ImporterUtil importerUtil,
                                 final PublicationImportReport importReport,
                                 final boolean pretend) {
        final RisFieldUtil fieldUtil = new RisFieldUtil(pretend);
        final RisAuthorUtil authorUtil = new RisAuthorUtil(importerUtil, pretend);
        final RisJournalUtil journalUtil = new RisJournalUtil(importerUtil, pretend);

        fieldUtil.processTitle(dataset, publication, importReport);

        fieldUtil.processIntField(dataset, RisField.PY, publication, "yearOfPublication", importReport);

        authorUtil.processAuthors(dataset, RisField.AU, publication, importReport);

        fieldUtil.processField(dataset, RisField.AB, publication, "abstract", importReport);

        journalUtil.processJournal(dataset, RisField.T2, publication, importReport);

        fieldUtil.processField(dataset, RisField.M1, publication, "issue", importReport);

        fieldUtil.processPages(dataset, RisField.SP, publication, importReport);

        fieldUtil.processIntField(dataset, RisField.VL, publication, "volume", importReport);

        fieldUtil.processIntField(dataset, RisField.M2, publication, "pagesFrom", importReport);
    }

    @Override
    public RisType getRisType() {
        return RisType.JOUR;
    }

}
