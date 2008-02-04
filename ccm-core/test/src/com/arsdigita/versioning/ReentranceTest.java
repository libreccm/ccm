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
package com.arsdigita.versioning;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TestTransaction;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * This reproduces the issues reported by Mike Bonnet in
 * https://bugzilla.redhat.com/bugzilla/show_bug.cgi?id=101280
 *
 * <p>This has to live in a separate test file.  If we put in the
 * VersioningEventProcessorTest, some other test would run first and initialize
 * the Event types data objects, thus defeating the purpose of this test. </p>
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-07-30
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/
public class ReentranceTest extends BaseTestCase implements Const {
    // the test does not really fail, but it increases the overall execution of
    // the suite by 3 min on my machine, so I disabled it.
    public final static boolean FAILS = true;

    public ReentranceTest(String name) {
        super(name);
    }

    /**
     * This simply tests whether or not the versioning event processor gets
     * overwhelmed by a long queue of events.
     **/
    public void testGazillionVersionedObjects() {
        final int nObjects = 1000;
        List refs = new ArrayList(nObjects);
        for (int ii=0; ii<nObjects; ii++) {
            DataObject vt1 = Util.newDataObject(VT1);
            vt1.set(NAME, "vt1 " + ii);
            vt1.set(CONTENT, "vt1 content " + ii);
            vt1.set(INT_ATTR, new BigInteger(String.valueOf(ii)));
            // Prevent vt1 from being GC-ed before the loop finishes just in
            // case.
            refs.add(vt1);
        }
        TestTransaction.testCommitTxn
            (SessionManager.getSession().getTransactionContext());
    }
}
