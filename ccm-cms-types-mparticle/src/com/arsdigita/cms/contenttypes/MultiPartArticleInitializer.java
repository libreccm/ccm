/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentSectionConfig;
import com.arsdigita.cms.contenttypes.ui.mparticle.ArticleSectionPanel;
//Unused import
// import com.arsdigita.cms.search.ContentPageMetadataProvider;
import com.arsdigita.kernel.URLService;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.LegacyInitEvent;
import com.arsdigita.search.MetadataProviderRegistry;

/**
 * The MultiPartArticle initializer.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: MultiPartArticleInitializer.java 1500 2007-03-20 09:25:45Z chrisgilbert23 $
 */
public class MultiPartArticleInitializer extends ContentTypeInitializer {

    /**
     * MultiPartArticleInitializer  Constructor
     */
    public MultiPartArticleInitializer() {
        super("ccm-cms-types-mparticle.pdl.mf", MultiPartArticle.BASE_DATA_OBJECT_TYPE);
    }


    public String getTraversalXML() {
        return "WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/MultiPartArticle.xml";
    }

    public String[] getStylesheets() {
        return new String [] {
            "/static/content-types/com/arsdigita/cms/contenttypes/MultiPartArticle.xsl",
            "/static/content-types/com/arsdigita/cms/contenttypes/ArticleSection.xsl"
        };
    }

    // Previously used LegacyInitEvent, allthough no legacy init functionality
    // is used here. Wondering.
    public void init(DomainInitEvent evt) {
        super.init(evt);

        MetadataProviderRegistry.registerAdapter(
            ArticleSection.BASE_DATA_OBJECT_TYPE,
            new ArticleSectionMetadataProvider());

        URLService.registerFinder(
            ArticleSection.BASE_DATA_OBJECT_TYPE,
            new MultiPartArticleSectionURLFinder());

        ContentSectionConfig.registerExtraXMLGenerator(MultiPartArticle.class.getName(),
                                                       new ArticleSectionPanel());
    }
}
