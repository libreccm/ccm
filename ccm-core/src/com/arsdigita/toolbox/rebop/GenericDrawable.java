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

import java.awt.Graphics;

/**
 * Interface for simple drawable objects. Implement this
 * for objects that will be used to visually render
 * a GenericComponent subclass.
 *
 * @author Gavin Doughtie
 * @see AbstractDrawable
 * @see BevelBox
 * @see StyledText
 */

public interface GenericDrawable {
    public static final String versionId = "$Id: GenericDrawable.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * This is the main GenericDrawable method implementors will define.
     * The other methods can typically be inherited from AbstractDrawable.
     * @param g Graphics to draw on
     * @param x horizontal location to begin drawing
     * @param y vertical location to begin drawing
     * @param width width of area to draw in
     * @param height height of area to draw in
     */
    void draw(Graphics g, int x, int y, int width, int height);

    // Offsets are used mainly for button pushdowns

    /**
     * The X and Y offsets are used to offset a drawable from the location
     * where it is told to draw. Offsets are typically used for drawables
     * that are inside components that form the contents of a push-down
     * button.
     * @param xOffset number of horizontal pixels to offset this
     * drawable before drawing it.
     */
    void setXOffset(int xOffset);
    int getXOffset();

    /**
     * @param yOffset number of vertical pixels to offset this
     * @see #setXOffset(int)
     */
    void setYOffset(int yOffset);
    int getYOffset();

}// GenericDrawable
