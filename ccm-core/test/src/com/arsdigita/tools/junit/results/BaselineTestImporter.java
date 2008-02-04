/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.tools.junit.results;

import java.util.Map;

/**
 *  BaselineTestImporter
 *
 *  @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *  @version $Revision: #6 $ $Date Nov 6, 2002 $
 */
public interface BaselineTestImporter {

    /**
     * Imports all the test result files for a particular changelist.
     *
     * @param changelist - The changelist number
     * @return Collection of org.jdom.Document
     */
    Map getTestsForChangelist(String changelist);

    /**
     * Imports all the test result files for the defined regression baseline.
     * Optional. May throw UnsupportedMethodException
     *
     * @return Collection of org.jdom.Document
     */
    Map getBaselineTests();

    /**
     * Returns true if this importer supports test baselines, and one is available.
     *
     * @return true if a baseline is available.
     */
    boolean isBaselineAvailable();
}
