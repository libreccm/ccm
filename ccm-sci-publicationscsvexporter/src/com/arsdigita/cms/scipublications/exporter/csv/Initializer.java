/*
 * Copyright (c) 2014 Jens Pelzetter
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
package com.arsdigita.cms.scipublications.exporter.csv;

import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporter;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporters;
import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class Initializer extends CompoundInitializer {

    public Initializer() {
        super();
        
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(new ManifestSource("empty.pdl.mf",
                                                  new NameFilter((DbHelper.getDatabaseSuffix(
                                                                  database)), "pdl"))));
    }
    
    @Override
    public void init(final DomainInitEvent event) {
        super.init(event);
        
        SciPublicationsExporters.register(new CsvExporter());
        
        CsvConverters.register(new ArticleInCollectedVolumeConverter());
        CsvConverters.register(new ArticleInJournalConverter());
        CsvConverters.register(new CollectedVolumeConverter());
        CsvConverters.register(new ExpertiseConverter());
        CsvConverters.register(new GreyLiteratureConverter());
        CsvConverters.register(new InProceedingsConverter());
        CsvConverters.register(new InternetArticleConverter());
        CsvConverters.register(new MonographConverter());
        CsvConverters.register(new ProceedingsConverter());
        CsvConverters.register(new PublicationConverter());
        CsvConverters.register(new PublicationWithPublisherConverter());
        CsvConverters.register(new ReviewConverter());
        CsvConverters.register(new UnPublishedConverter());
        CsvConverters.register(new WorkingPaperConverter());
    }

}
