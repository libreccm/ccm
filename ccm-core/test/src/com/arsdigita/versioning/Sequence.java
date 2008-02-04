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

import java.math.BigInteger;

/**
 * A sequence generator. Use this instead of a DB-backed sequence to ensure that
 * the same primary key values are used in any two independent runs of the same
 * unit test suite.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-26
 * @version $Revision: #5 $ $DateTime: 2004/08/16 18:10:38 $
 **/
final class Sequence {
    private static BigInteger s_current = BigInteger.ZERO;

    synchronized public static BigInteger next() {
        s_current = s_current.add(BigInteger.ONE);
        return s_current;
    }
}
