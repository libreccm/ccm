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
package com.arsdigita.util;

/**
 * Almost the same as java.awt.Dimension
 *
 * @author Koalamann (konerman@tzi.de)
 * @version $Date: 2015/10/28 $
 */
public class Dimension {

    /**
     * The width dimension; negative values can be used.
     */
    public int width;

    /**
     * The height dimension; negative values can be used.
     */
    public int height;

    /**
     * Creates an instance of <code>Dimension</code> with a width of zero and a
     * height of zero.
     */
    public Dimension() {
        this(0, 0);
    }

    /**
     * Constructs a <code>Dimension</code> and initializes it to the specified
     * width and specified height.
     *
     * @param width the specified width
     * @param height the specified height
     */
    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
