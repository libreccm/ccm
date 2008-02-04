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
 *  FileTestImporter
 *
 *  @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *  @version $Revision: #6 $ $Date Nov 6, 2002 $
 */
public class FileTestImporter implements BaselineTestImporter {


    public Map getTestsForChangelist(String changelist) {
        ResultFileSetLoader loader = new ResultFileSetLoader();
        String archiveDir = System.getProperty("junit.result.archive");
        if (!archiveDir.endsWith("/")) {
            archiveDir += "/";
        }

        Map tests = loader.loadResultFiles(archiveDir + changelist);

        return tests;
    }

    public Map getBaselineTests() {
        throw new UnsupportedOperationException(getClass() + " does not support baselines. Use getTestsForChangelist");
    }

    public boolean isBaselineAvailable() {
        return false;
    }

}
