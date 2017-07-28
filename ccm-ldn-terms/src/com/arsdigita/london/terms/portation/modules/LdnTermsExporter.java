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
import com.arsdigita.london.terms.portation.modules.core.categorization.DomainMarshaller;
import com.arsdigita.portation.AbstractExporter;
import com.arsdigita.portation.Format;

import java.util.ArrayList;

/**
 * Helper to implement the specifics for the exportation. Makes source code
 * in the cli-tool shorter and more readable.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 7/28/17
 */
public class LdnTermsExporter extends AbstractExporter {

    public static void exportUsers() {
        System.out.printf("\tExporting domains...");
        DomainMarshaller domainMarshaller = new DomainMarshaller();
        domainMarshaller
                .prepare(Format.XML, pathName, "domains", indentation);
        domainMarshaller
                .exportList(new ArrayList<>(NgCoreCollection.domains.values()));
        System.out.printf("\t\tdone.\n");
    }

}
