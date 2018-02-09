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
package com.arsdigita.london.terms.portation.modules;

import com.arsdigita.london.terms.portation.conversion.NgCoreCollection;
import com.arsdigita.london.terms.portation.modules.core.categorization.DomainOwnershipMarshaller;
import com.arsdigita.london.terms.portation.modules.core.core.ResourceTypeMarshaller;
import com.arsdigita.london.terms.portation.modules.core.categorization.DomainMarshaller;
import com.arsdigita.london.terms.portation.modules.core.web.CcmApplicationMarshaller;
import com.arsdigita.portation.AbstractExporter;
import com.arsdigita.portation.Format;
import com.arsdigita.portation.modules.CoreExporter;

import java.util.ArrayList;

/**
 * Helper to implement the specifics for the exportation. Makes source code
 * in the cli-tool shorter and more readable.
 *
 * Their exists no direct usage of this class. It is used via reflections in
 * the ccm-core package to start export of these classes.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 7/28/17
 */
public class LdnTermsExporter extends AbstractExporter {

    private static LdnTermsExporter instance;

    static {
        instance = new LdnTermsExporter();
    }

    /**
     * Getter for the instance of the singleton.
     *
     * @return instance of this singleton
     */
    public static LdnTermsExporter getInstance() {
        return instance;
    }

    @Override
    public void startMarshaller() {
        ResourceTypeMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        CcmApplicationMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        DomainMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
        DomainOwnershipMarshaller.getInstance().
                marshallAll(format, pathName, indentation);
    }
}
