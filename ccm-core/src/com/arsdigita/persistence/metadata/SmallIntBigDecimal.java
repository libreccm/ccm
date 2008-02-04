/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence.metadata;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * SmallIntBigDecimal
 *
 *  This is an implementation of BigDecimal for values <= 32 bits. This is
 *  because Integer.toString(int) is signifigantly faster than BigInteger.toSring().
 *  All BigDecimals in Persistence are really integer values, so the conversion is
 *  valid.
 *
 *  This class is intended to be used within MetadataRoot when instantiating
 *  values from the database.
 *
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 *
 */
class SmallIntBigDecimal extends BigDecimal {

    SmallIntBigDecimal(BigInteger val) {
        super(val);
        if( val.bitLength() >= 32 ) {
            throw new IllegalArgumentException("Value " + super.toString() + " too large!");
        }
    }

    public String toString() {
        return Integer.toString(toBigInteger().intValue());
    }
}
