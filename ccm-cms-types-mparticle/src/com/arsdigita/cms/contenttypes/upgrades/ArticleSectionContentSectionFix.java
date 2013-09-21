package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.ArticleSection;
import com.arsdigita.cms.contenttypes.ArticleSectionCollection;
import com.arsdigita.cms.contenttypes.MultiPartArticle;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.cmd.Program;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ArticleSectionContentSectionFix extends Program {

    public ArticleSectionContentSectionFix() {
        super("ArticleSectionContentSectionFix", "1.0.0", "");
    }

    public static void main(final String[] args) {
        new ArticleSectionContentSectionFix().run(args);
    }

    @Override
    protected void doRun(final CommandLine cmdLine) {
        final Session session = SessionManager.getSession();
        final DomainCollection mparticles = new DomainCollection(session.retrieve(
                MultiPartArticle.BASE_DATA_OBJECT_TYPE));
        
        while(mparticles.next()) {
            processMPArticle((MultiPartArticle)mparticles.getDomainObject());
        }
    }
    
    private void processMPArticle(final MultiPartArticle mpa) {
        final ArticleSectionCollection sections = mpa.getSections();
        
        while(sections.next()) {
            final ArticleSection section = sections.getArticleSection();
            section.setContentSection(mpa.getContentSection());
            section.save();
        }
    }

}