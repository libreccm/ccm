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
package com.arsdigita.tools.junit.extensions;

import com.arsdigita.tools.junit.extensions.PerfTiming;
import com.arsdigita.tools.junit.extensions.TimedTestRecord;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.File;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 * <P> TestSuite used to run performance tests using the {@link TimedTestRecord}
 * test decorator. Only actualy TestCases should be added to this suite,
 * <i>not</i> TestSuites or TestDecorators.</P>
 *
 * <P> The PerformanceSuite stores runtime data relating to each of it's tests
 * in an xml file. The data from previous runs is used as a basis for the
 * timout value of each subsequent run. Therefore tests that take significantly
 * longer than they used to are failed.</P>
 *
 * @see TimedTestRecord
 * @see PerfTiming
 */

public class PerformanceSuite extends PackageTestSuite {
    private PerfTiming m_perfTiming;

    public PerformanceSuite() {
        super();
        m_perfTiming = new PerfTiming();

        try {
            File file = getTimingFile();
            m_perfTiming.load(file);
        } catch (Exception e) {
            throw new UncheckedWrapperException(
                    "Error loading performance file", e);
        }
    }

    /*
     * Performance timing results will be associated with a specific
     * test suite. All performace tests run by the suite wil be stored.
     * Timing results file will be same name as suite.
     */
    private File getTimingFile() {
        String path = System.getProperty("test.base.dir") + "/"
                      + getClass().getName().replace('.','/')
                      + "Timing.xml";
        File file = new File(path);
        return file;
    }


    /* Create the TimedTestRecord when tests are added to the suite. */
    public void addTest(Test test) {
        Test timedTest = new TimedTestRecord((TestCase)test, m_perfTiming);
        super.addTest(timedTest);   //To change body of overriden
    }
}
