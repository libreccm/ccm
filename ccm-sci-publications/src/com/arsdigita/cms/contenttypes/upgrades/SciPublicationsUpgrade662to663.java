package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.util.cmd.Program;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsUpgrade662to663 extends Program {

    public SciPublicationsUpgrade662to663() {
        super("SciPublicationsUpgrade662to663", "1.0.0", "", true, true);
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {
        new JournalTypeUpgrade().doUpgrade();
        new PublicationBundleUpgrade().doUpgrade();
        new PublicationWithPublisherBundleUpgrade().doUpgrade();
        new ArticleInCollectedVolumeBundleUpgrade().doUpgrade();
        new ArticleInJournalBundleUpgrade().doUpgrade();
        new CollectedVolumeBundleUpgrade().doUpgrade();
        new ExpertiseBundleUpgrade().doUpgrade();
        new InProceedingsBundleUpgrade().doUpgrade();
        new InternetArticleBundleUpgrade().doUpgrade();
        new JournalBundleUpgrade().doUpgrade();
        new ProceedingsBundleUpgrade().doUpgrade();
        new PublisherBundleUpgrade().doUpgrade();
        new SeriesBundleUpgrade().doUpgrade();
        new UnPublishedBundleUpgrade().doUpgrade();
        new CollectedVolumeArticlesAssocUpgrade().doUpgrade();
        new ExpertiseOrdererAssocUpgrade().doUpgrade();
        new ExpertiseOrganizationAssocUpgrade().doUpgrade();
        new InternetArticleOrganizationAssocUpgrade().doUpgrade();
        new JournalArticlesAssocUpgrade().doUpgrade();
        new ProceedingsOrganizerAssocUpgrade().doUpgrade();
        new ProceedingsPapersAssocUpgrade().doUpgrade();
        new PublicationAuthorsAssocUpgrade().doUpgrade();
        new PublicationOrgaUnitAssocUpgrade().doUpgrade();
        new PublicationWithPublisherPublisherAssocUpgrade().doUpgrade();
        new SeriesEditorsAssocUpgrade().doUpgrade();
        new SeriesPublicationsAssocUpgrade().doUpgrade();
        new UnPublishedOrganizationAssocUpgrade().doUpgrade();
    }

}
