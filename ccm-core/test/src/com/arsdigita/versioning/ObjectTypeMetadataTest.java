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

import com.arsdigita.versioning.ObjectTypeMetadata;
import com.redhat.persistence.pdl.VersioningMetadata;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

/**
 * <code>ObjectTypeMetadata</code> test.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-19
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */
public class ObjectTypeMetadataTest extends TestCase {
    private static final Logger s_log =
        Logger.getLogger(ObjectTypeMetadataTest.class);

    private static final String VT1  = "versioning.metadata.VT1";
    private static final String VT2  = "versioning.metadata.VT2";
    private static final String UT1  = "versioning.metadata.UT1";
    private static final String UT2  = "versioning.metadata.UT2";
    private static final String UT3  = "versioning.metadata.UT3";
    private static final String UT4  = "versioning.metadata.UT4";
    private static final String UT5  = "versioning.metadata.UT5";
    private static final String UT6  = "versioning.metadata.UT6";
    private static final String VUT2 = "versioning.metadata.VUT2";
    private static final String C1   = "versioning.metadata.C1";
    private static final String A1   = "versioning.metadata.A1";
    private static final String VTC3 = "versioning.metadata.VTC3";
    private static final String C2   = "versioning.metadata.C2";

    private static final String UNVER_ATTR = "unverAttr";
    private static final String UT5_ATTR   = "ut5";
    private static final String UT6S_ATTR  = "ut6s";

    public ObjectTypeMetadataTest(String name) {
        super(name);
    }

    public void testParser() {
        VersioningMetadata vmd = VersioningMetadata.getVersioningMetadata();
        assertTrue(VT1 + Const.MARKED, vmd.isMarkedVersioned(VT1));
        assertTrue(VT2 + Const.UNMARKED, !vmd.isMarkedVersioned(VT2));
        assertTrue(UT1 + Const.UNMARKED, !vmd.isMarkedVersioned(UT1));
        assertTrue(UT2 + Const.UNMARKED, !vmd.isMarkedVersioned(UT2));
        assertTrue(UT3 + Const.UNMARKED, !vmd.isMarkedVersioned(UT3));
        assertTrue(UT4 + Const.UNMARKED, !vmd.isMarkedVersioned(UT4));
        assertTrue(VUT2 + Const.MARKED, vmd.isMarkedVersioned(VUT2));
        assertTrue(UNVER_ATTR + Const.UNVERSIONED,
                   vmd.isMarkedUnversioned(VUT2, UNVER_ATTR));
        assertTrue(UT5_ATTR + Const.UNVERSIONED,
                   vmd.isMarkedUnversioned(VUT2, UT5_ATTR));
        assertTrue(A1 + Const.UNMARKED, !vmd.isMarkedVersioned(A1));
        assertTrue(C1 + Const.UNMARKED, !vmd.isMarkedVersioned(C1));
        assertTrue(VTC3 + Const.MARKED, vmd.isMarkedVersioned(VTC3));
        assertTrue(C2 + Const.UNMARKED, !vmd.isMarkedVersioned(C2));
        assertTrue(UT5 + Const.UNMARKED, !vmd.isMarkedVersioned(UT5));
        assertTrue(UT6 + Const.UNMARKED, !vmd.isMarkedVersioned(UT6));
    }

    /**
     * Object types extending a versioned object type are themselves versioned.
     **/
    public void testDependenceGraph() {
        ObjectTypeMetadata otmd = ObjectTypeMetadata.getInstance();
        assertTrue(VT1 + Const.VERSIONED_TYPE, otmd.isVersionedType(VT1));
        assertTrue(VT2 + Const.VERSIONED_TYPE, otmd.isVersionedType(VT2));
        assertTrue(UT1 + Const.RECOVERABLE, otmd.isRecoverable(UT1));
        assertTrue(UT2 + Const.UNREACHABLE, otmd.isUnreachable(UT2));
        assertTrue(UT3 + Const.COVERSIONED_TYPE, otmd.isCoversionedType(UT3));
        assertTrue(UT4 + Const.COVERSIONED_TYPE, otmd.isCoversionedType(UT4));
        assertTrue(VUT2 + Const.VERSIONED_TYPE, otmd.isVersionedType(VUT2));
        assertTrue(C1 + Const.COVERSIONED_TYPE, otmd.isCoversionedType(C1));
        assertTrue(A1 + Const.RECOVERABLE, otmd.isRecoverable(A1));
        assertTrue(VTC3 + Const.VERSIONED_TYPE, otmd.isVersionedType(VTC3));
        assertTrue(C2 + Const.UNREACHABLE, otmd.isUnreachable(C2));
        assertTrue(UT5 + Const.UNREACHABLE, otmd.isUnreachable(UT5));
        assertTrue(UT6 + Const.RECOVERABLE, otmd.isRecoverable(UT6));
    }
}
