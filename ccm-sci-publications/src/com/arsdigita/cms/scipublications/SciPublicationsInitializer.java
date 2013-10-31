/*
 * Copyright (c) 2010, 2013 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.scipublications;

import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporters;
import com.arsdigita.cms.scipublications.exporter.bibtex.BibTeXExporter;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.ArticleBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilders;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BookBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.InCollectionBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.InProceedingsBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.MiscBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.ProceedingsBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnPublishedBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.ArticleInCollectedVolumeConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.ArticleInJournalConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.BibTeXConverters;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.CollectedVolumeConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.ExpertiseConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.GreyLiteratureConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.InProceedingsConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.InternetArticleConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.MonographConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.ProceedingsConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.PublicationConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.PublicationWithPublisherConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.ReviewConverter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.WorkingPaperConverter;
import com.arsdigita.cms.scipublications.exporter.ris.RisConverters;
import com.arsdigita.cms.scipublications.exporter.ris.RisExporter;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImporters;
import com.arsdigita.cms.scipublications.importer.bibtex.BibTeXPublicationsImporter;
import com.arsdigita.cms.scipublications.importer.bibtex.converters.ArticleConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.converters.BookConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.converters.BookletConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.converters.InBookConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.converters.InCollectionConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.converters.ManualConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.converters.MasterThesisConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.converters.MiscConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.converters.PhdThesisConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.converters.TechReportConverter;
import com.arsdigita.cms.scipublications.importer.bibtex.converters.UnPublishedConverter;
import com.arsdigita.cms.scipublications.importer.ris.RisImporter;
import com.arsdigita.cms.scipublications.importer.ris.converters.AbstConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.BlogConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.ChapConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.ConfConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.CpaperConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.EbookConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.EchapConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.EdbookConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.EjourConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.ElecConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.EncycConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.GenConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.InprConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.JfullConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.JourConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.MgznConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.RprtConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.ThesConverter;
import com.arsdigita.cms.scipublications.importer.ris.converters.UnpbConverter;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.ui.admin.ApplicationManagers;

/**
 * Initializer for the SciPublications application.
 * 
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciPublicationsInitializer extends CompoundInitializer {

    public SciPublicationsInitializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(new ManifestSource("empty.pdl.mf",
                                                  new NameFilter((DbHelper.
                                                                  getDatabaseSuffix(
                                                                  database)),
                                                                 "pdl"))));
    }

    @Override
    public void init(DomainInitEvent e) {
        super.init(e);

        DomainObjectFactory.registerInstantiator(
                SciPublications.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
            @Override
            public DomainObject doNewInstance(final DataObject dataObject) {
                return new SciPublications(dataObject);
            }

        });
        
        //Register the ApplicationManager implementation for the SciPublications applications
        ApplicationManagers.register(new SciPublicationsAppManager());

        //Register SciPublicationsExporter implementations provided by this module
        SciPublicationsExporters.register(new BibTeXExporter());
        SciPublicationsExporters.register(new RisExporter());

        //Register BibTeXBuilder implementations provided by this module
        BibTeXBuilders.register(new ArticleBuilder());
        BibTeXBuilders.register(new BookBuilder());
        BibTeXBuilders.register(new InCollectionBuilder());
        BibTeXBuilders.register(new InProceedingsBuilder());
        BibTeXBuilders.register(new MiscBuilder());
        BibTeXBuilders.register(new ProceedingsBuilder());
        BibTeXBuilders.register(new UnPublishedBuilder());

        //Register all exporter BibTeXConverter implementations provided by this module
        BibTeXConverters.register(new ArticleInCollectedVolumeConverter());
        BibTeXConverters.register(new ArticleInJournalConverter());
        BibTeXConverters.register(new CollectedVolumeConverter());
        BibTeXConverters.register(new ExpertiseConverter());
        BibTeXConverters.register(new GreyLiteratureConverter());
        BibTeXConverters.register(new InProceedingsConverter());
        BibTeXConverters.register(new InternetArticleConverter());
        BibTeXConverters.register(new MonographConverter());
        BibTeXConverters.register(new ProceedingsConverter());
        BibTeXConverters.register(new PublicationConverter());
        BibTeXConverters.register(new PublicationWithPublisherConverter());
        BibTeXConverters.register(new ReviewConverter());
        BibTeXConverters.register(new WorkingPaperConverter());

        //Register all exporter RisConverter implementations provided by this module
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.ArticleInCollectedVolumeConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.ArticleInJournalConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.CollectedVolumeConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.ExpertiseConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.GreyLiteratureConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.InProceedingsConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.InternetArticleConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.MonographConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.ProceedingsConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.PublicationConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.PublicationWithPublisherConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.ReviewConverter());
        RisConverters.register(new com.arsdigita.cms.scipublications.exporter.ris.WorkingPaperConverter());

        //Register all SciPublicationsImporter implementations provided by this module
        SciPublicationsImporters.register(new BibTeXPublicationsImporter());
        SciPublicationsImporters.register(new RisImporter());

        //Register all importer BibTeXConverter implementations provided by this module
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(new ArticleConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(new BookConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(new BookletConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(new InBookConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(new InCollectionConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(
                new com.arsdigita.cms.scipublications.importer.bibtex.converters.InProceedingsConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(new ManualConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(new MasterThesisConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(new MiscConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(new PhdThesisConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(
                new com.arsdigita.cms.scipublications.importer.bibtex.converters.ProceedingsConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(new TechReportConverter());
        com.arsdigita.cms.scipublications.importer.bibtex.BibTeXConverters.register(new UnPublishedConverter());

        //Register all importer RisConverter implementations provided by this module
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new AbstConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new BlogConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new com.arsdigita.cms.scipublications.importer.ris.converters.BookConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new ChapConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new ConfConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new CpaperConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new EbookConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new EchapConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new EdbookConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new EjourConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new ElecConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new EncycConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new GenConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new InprConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new JfullConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new JourConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new MgznConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new RprtConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new ThesConverter());
        com.arsdigita.cms.scipublications.importer.ris.RisConverters.register(new UnpbConverter());
    }

}
