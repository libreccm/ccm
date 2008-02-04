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

import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.util.UncheckedWrapperException;
import com.clarkware.junitperf.TimedTest;
import junit.extensions.TestDecorator;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * <P>This test decorator is used to decorate <code>TestCases</code> only.
 * It records how long the tests took to execute. If the test had been 
 * run previously it uses the previous 'time to completion' as a basis for
 * the timeout. I.e. if the test took 'significantly' longer than before, 
 * it will fail. {@link PerfTiming}'s <code>variance</code> field for further
 * details.</P>
 *
 * @see PerfTiming
 * @see TestCaseDescriptor
 *
 * @author <a href="mailto:aahmed@redhat.com"> Aizaz Ahmed </a>
 */
public class TimedTestRecord extends TestDecorator {

    private PerfTiming pTime;
    
    public TimedTestRecord ( TestCase test, PerfTiming perfTiming ) {
        super ( test );
        pTime = perfTiming;
    }
    
    public TimedTestRecord ( TestCase test ) {
        super ( test );
        try {
            pTime = new PerfTiming();
            pTime.load();
        } catch (Exception e) {
            throw new UncheckedWrapperException(
                            "Error loading performance file", e);
        }
    }

    public void run(final TestResult result) {

        new KernelExcursion() {
            protected void excurse() {
                setParty(PermissionDecorator.getAdminUser());

                runTimingTest(result);

            }
        }.run();

    }

    private void runTimingTest(TestResult result) {
        try {
            /* get the previous recorded time, new timeout value */
            TestCaseDescriptor tdesc = pTime.getDescriptor ( fTest );
            long timeout = tdesc.getFastestWithVar();

            /*
             * we need to record the time ourselves as well,
             * unfortunately TimedTest does not give us access to the
             * beginning time
             */
            TimedTest timedTest = new TimedTest ( fTest, timeout );
            long beginTime = System.currentTimeMillis();
            timedTest.run ( result );
            long elapsed = System.currentTimeMillis() - beginTime;

            /* update the records only if an error did not occur */
            if  ( result.wasSuccessful () ) {
                if ( elapsed < tdesc.getFastest() ) {
                    tdesc.setFastest ( elapsed );
                    pTime.update ( tdesc );
                }
            } else {
            }

        } catch ( Exception e ) {
            /* an unexpected exception */
            result.addError ( fTest, new Error ( e.toString() ) );
        }
    }
}
