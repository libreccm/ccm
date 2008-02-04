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
package com.arsdigita.toolbox.rebop;

/**
 * Partial implementation of the GenericDrawable interface so
 * derived classes don't have to implement the Offset methods.
 * @author Gavin Doughtie
 */
public abstract class AbstractDrawable implements GenericDrawable {
    public static final String versionId = "$Id: AbstractDrawable.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    // These offsets control how far the GenericDrawable
    // will be drawn past the passed-in x and y
    private int m_xOffset = 0;
    private int m_yOffset = 0;

    public void setXOffset(int xOffset) {
        m_xOffset = xOffset;
    }

    public int getXOffset() {
        return m_xOffset;
    }

    public int getYOffset() {
        return m_yOffset;
    }

    public void setYOffset(int yOffset) {
        m_yOffset = yOffset;
    }

}// AbstractDrawable
