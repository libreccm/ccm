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

import org.apache.log4j.Logger;

/**
 * Initializes the GenericArticle content type.
 * Defines the content type specific properties and just uses the super class
 * methods to register the content type with the (transient) content type store
 * (map). This is done by runtimeRuntime startup method which runs the init()
 * methods of all initializers (this one just using the parent implementation).
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ArticleInitializer.java 757 2005-09-02 14:12:21Z sskracic $
 */
public class ArticleInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(ArticleInitializer.class);

    public ArticleInitializer() {
        super("ccm-cms-types-article.pdl.mf", GenericArticle.BASE_DATA_OBJECT_TYPE);
    }

    @Override
    public String[] getStylesheets() {
        return new String[]
            { "/static/content-types/com/arsdigita/cms/contenttypes/Article.xsl" };
    }
}
