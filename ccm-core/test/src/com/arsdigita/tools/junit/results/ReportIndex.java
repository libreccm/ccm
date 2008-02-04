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

import org.jdom.Element;

/**
 *  ReportIndex
 *
 *  @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *  @version $Revision: #8 $ $Date Nov 6, 2002 $
 */
public class ReportIndex extends Element {
    public ReportIndex(String previousChangelist, String currentChangelist, String databaseType) {
        super("junit_index");
        setAttribute("previous_changelist", previousChangelist);
        setAttribute("current_changelist", currentChangelist);
        setAttribute("database_type", databaseType);
        setAttribute("changes", "0");
        setAttribute("warnings", "0");
    }

    public void addResult(ResultDiff diff) {
        Element elem = new Element("test");
        elem.setAttribute("name", diff.getTestName());


        elem.setAttribute("tests", Integer.toString(diff.getCurrent().getTestCount()));
        int testDelta = diff.getCurrent().getTestCount() - diff.getPrevious().getTestCount();
        elem.setAttribute("test_delta", Integer.toString(testDelta));

        elem.setAttribute("failures", Integer.toString(diff.getCurrent().getFailureCount()));
        int failureDelta = diff.getCurrent().getFailureCount() - diff.getPrevious().getFailureCount();
        elem.setAttribute("failure_delta", Integer.toString(failureDelta));

        elem.setAttribute("errors", Integer.toString(diff.getCurrent().getErrorCount()));
        int errorDelta = diff.getCurrent().getErrorCount() - diff.getPrevious().getErrorCount();
        elem.setAttribute("error_delta", Integer.toString(errorDelta));

        elem.setAttribute("new_tests", Integer.toString(diff.newTestCount()));
        elem.setAttribute("missing_tests", Integer.toString(diff.missingTestCount()));

        final boolean warningsExist = testDelta < 0 || failureDelta > 0 || errorDelta > 0 || diff.missingTestCount() > 0;

        elem.setAttribute("warning", "" + warningsExist);
		
        final boolean changesExist = !(testDelta == 0 &&
                failureDelta == 0 && errorDelta == 0 && diff.missingTestCount() == 0 && diff.newTestCount() == 0);

        if (changesExist) {
            incrementChangeCount();
        } 
        
        if(warningsExist) {
            incrementWarningCount();
        }

        addContent(elem);
    }

    public void incrementWarningCount() {
        int warnings = Integer.parseInt(getAttributeValue("warnings"));
        warnings++;
        setAttribute("warnings", Integer.toString(warnings));
    }

    public void incrementChangeCount() {
        int changes = Integer.parseInt(getAttributeValue("changes"));
        changes++;
        setAttribute("changes", Integer.toString(changes));
    }
}
