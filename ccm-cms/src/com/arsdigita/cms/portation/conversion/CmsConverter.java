/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.portation.conversion;

import com.arsdigita.cms.portation.conversion.contentsection.ContentSectionConversion;
import com.arsdigita.cms.portation.conversion.contentsection.ContentTypeConversion;
import com.arsdigita.cms.portation.conversion.contentsection.FolderConversion;
import com.arsdigita.cms.portation.conversion.lifecycle.LifecycleConversion;
import com.arsdigita.cms.portation.conversion.lifecycle.LifecycleDefinitionConversion;
import com.arsdigita.cms.portation.conversion.lifecycle.PhaseConversion;
import com.arsdigita.cms.portation.conversion.lifecycle.PhaseDefinitionConversion;
import com.arsdigita.portation.AbstractConverter;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 2/12/18
 */
public class CmsConverter extends AbstractConverter {
    private static CmsConverter instance;

    static {
        instance = new CmsConverter();
    }

    /**
     * Getter for the instance of this singleton.
     *
     * @return instance of the singleton
     */
    public static CmsConverter getInstance() {
        return instance;
    }

    /**
     * Method, to start all the different converter classes in a specific
     * order, so that dependencies can only be set, where the objects have
     * already been created.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void startConversions() throws Exception {
        PhaseDefinitionConversion.getInstance().convertAll();
        LifecycleDefinitionConversion.getInstance().convertAll();
        LifecycleConversion.getInstance().convertAll();
        PhaseConversion.getInstance().convertAll();

        FolderConversion.getInstance().convertAll();
        ContentTypeConversion.getInstance().convertAll();
        ContentSectionConversion.getInstance().convertAll();

        final Class c = Class.forName("com.arsdigita.cms.portation" +
                ".conversion.contenttypes.ArticleConversion");
        if (c != null) {
            Method startConversionToNg = c.getDeclaredMethod("convertAll");
            startConversionToNg.invoke(c.newInstance());
        }

        final Class c1 = Class.forName("com.arsdigita.cms.portation" +
                ".conversion.contenttypes.EventConversion");
        if (c1 != null) {
            Method startConversionToNg = c1.getDeclaredMethod("convertAll");
            startConversionToNg.invoke(c1.newInstance());
        }

        final Class c2 = Class.forName("com.arsdigita.cms.portation" +
                ".conversion.contenttypes.MultiPartArticleSectionConversion");
        if (c2 != null) {
            Method startConversionToNg = c2.getDeclaredMethod("convertAll");
            startConversionToNg.invoke(c2.newInstance());
        }

        final Class c3 = Class.forName("com.arsdigita.cms.portation" +
                ".conversion.contenttypes.MultiPartArticleConversion");
        if (c3 != null) {
            Method startConversionToNg = c3.getDeclaredMethod("convertAll");
            startConversionToNg.invoke(c3.newInstance());
        }

        final Class c4 = Class.forName("com.arsdigita.cms.portation" +
                ".conversion.contenttypes.NewsConversion");
        if (c4 != null) {
            Method startConversionToNg = c4.getDeclaredMethod("convertAll");
            startConversionToNg.invoke(c4.newInstance());
        }

        final Class c5 = Class.forName("com.arsdigita.cms.portation" +
                ".conversion.assets.FileAssetConversion");
        if (c5 != null) {
            Method startConversionToNg = c5.getDeclaredMethod("convertAll");
            startConversionToNg.invoke(c5.newInstance());
        }
    }
}
