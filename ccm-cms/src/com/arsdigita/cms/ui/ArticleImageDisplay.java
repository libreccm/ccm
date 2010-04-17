/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.Article;
import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;


/**
 * Extends {@link ImageDisplay} to display the first
 * (according to the order attribute) image associated with an {@link Article}
 * The typical usage for this component is
 * <blockquote><pre><code>ArticleImageDisplay d = new ArticleImageDisplay(myItemSelectionModel, false);</code></pre></blockquote>
 * <p>
 *
 * @version $Revision: #8 $ $DateTime: 2004/08/17 23:15:09 $
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @version $Id: ArticleImageDisplay.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ArticleImageDisplay extends ImageDisplay {

    private final ItemSelectionModel m_article;


    /**
     * Construct a new <code>ArticleImageDisplay</code>
     *
     * @param article the {@link ItemSelectionModel} which
     *   supplies the {@link Article}
     *
     * @param assets the {@link ItemSelectionModel} which
     *   supplies the {@link ImageAsset} for the article; it is
     *   the parent's responsibility to register any state parameters
     *   for this model
     */
    public ArticleImageDisplay(ItemSelectionModel article,
                               ItemSelectionModel assets) {
        super(assets);

        m_article = article;
    }

    /**
     * @return The item selection model which supplies the
     *   current article
     */
    public final ItemSelectionModel getArticleSelectionModel() {
        return m_article;
    }

    /**
     * @param state The page state
     * @return the currently selected article
     * @post ( return != null )
     */
    protected Article getArticle(PageState state) {
        Article article = (Article) m_article.getSelectedObject(state);
        Assert.exists(article, "Article");
        return article;
    }

    /**
     * Adds the image caption as an attribute of the DOM element.
     */
    protected void generateImagePropertiesXML(ImageAsset image,
                                              PageState state,
                                              Element element) {

        super.generateImagePropertiesXML(image, state, element);

        Article article =  getArticle(state);
        String caption = article.getCaption(image);
        if ( caption != null ) {
            element.addAttribute("caption", caption);
        }
    }

}
