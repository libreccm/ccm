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
package com.arsdigita.bebop;

import com.arsdigita.bebop.util.Color;
import com.arsdigita.bebop.util.Size;

/**
 * Abstract class that contain the CSS-like
 * Block Stylable attributes.
 *
 * @author Jim Parsons 
 * @author Justin Ross 
 * @version $Id: BlockStylable.java 287 2005-02-22 00:29:02Z sskracic $
 * */
abstract public class BlockStylable extends TextStylable
{

    public static final String versionId = "$Id: BlockStylable.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Left-align a component.
     */
    public static final int LEFT = 1 << 0;

    /**
     * Center a component.
     */
    public static final int CENTER = 1 << 1;

    /**
     * Right-align a component.
     */
    public static final int RIGHT = 1 << 2;

    /**
     * Align the top of a component.
     */
    public static final int TOP = 1 << 3;

    /**
     * Align the middle of a component.
     */
    public static final int MIDDLE = 1 << 4;

    /**
     * Align the bottom of a component.
     */
    public static final int BOTTOM = 1 << 5;

    /**
     * Lay out a component across the full width of the panel.
     */
    public static final int FULL_WIDTH = 1 << 6;

    /**
     * Insert the child component assuming it is printed in a table with the
     * same number of columns.
     */
    public static final int INSERT = 1 << 7;

    /**
     *      Constant for specifying ABSMIDDLE alignment of this image input.
     *      See the <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">W3C HTML 4.01
     *      Specification</a>
     *      for a description of this attribute.
     */
    public static final int ABSMIDDLE = 1 << 8;

    /**
     *      Constant for specifying ABSBOTTOM alignment of this image input.
     *      See the <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">W3C HTML 4.01
     *      Specification</a>
     *      for a description of this attribute.
     */
    public static final int ABSBOTTOM = 1 << 9;

    /**
     *      Constant for specifying TEXTOP alignment of this image input.
     *      (See the <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">
     *      W3C HTML 4.01 Specification</a> for a description of this attribute.)
     */
    public static final int TEXTTOP = 1 << 10;

    /**
     *      Constant for specifying BASELINE alignment of this image input.
     *      (See the <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">
     *      W3C HTML 4.01 Specification</a> for a description of this attribute.)
     */
    public static final int BASELINE = 1 << 11;



    /*
     * This is a helper class for generating attribute names for style
     * attributes in setBorder, setPadding, and setMargin.
     */
    private String getSideStub(int sideEnum) {
        switch (sideEnum) {
        case TOP:
            return "top";
        case BOTTOM:
            return "bottom";
        case LEFT:
            return "left";
        case RIGHT:
            return "right";
            //This fallthrough needs a better guard clause, like
            //catching an IllegalArgumentException
        default:
            return "";
        }
    }

    /**
     * Sets this component's padding.
     *
     * @param size the size for this component's padding
     * @see BlockStylable#setPadding(Size, int)
     * @pre size != null
     */
    public void setPadding(Size size) {
        setPadding(size, TOP);
        setPadding(size, BOTTOM);
        setPadding(size, LEFT);
        setPadding(size, RIGHT);
    }

    /**
     * Sets the padding of one of this component's sides.
     *
     * @param sideEnum the side to set
     * @param size the size to set the padding to
     * @see BlockStylable#setPadding(Size)
     * @pre size != null
     */
    public void setPadding(Size size, int sideEnum) {
        setAttribute(getSideStub(sideEnum) + "Padding", size.toString());
    }

    /**
     * Sets this component's border.
     *
     * @param size the size to set for this component's border
     * @see BlockStylable#setBorder(Size, int)
     * @pre size != null
     */
    public void setBorder(Size size) {
        setBorder(size, TOP);
        setBorder(size, BOTTOM);
        setBorder(size, LEFT);
        setBorder(size, RIGHT);
    }

    /**
     * Sets the border size for one side of this component.
     *
     * @param size the size to set for the border
     * @param sideEnum the side to set
     * @see BlockStylable#setBorder(Size)
     * @pre size != null
     */
    public void setBorder(Size size, int sideEnum) {
        setAttribute(getSideStub(sideEnum) + "Border", size.toString());
    }

    /**
     * Sets the color of this component's border.
     *
     * @param borderColor the color for the border
     * @pre borderColor != null
     */
    public void setBorderColor(Color borderColor) {
        setAttribute("borderColor", borderColor.toString());
    }

    /**
     * Sets this component's margin.
     *
     * @param size the size to set this component's margin to
     * @see BlockStylable#setMargin(Size, int)
     * @pre size != null
     */
    public void setMargin(Size size) {
        setMargin(size, TOP);
        setMargin(size, BOTTOM);
        setMargin(size, LEFT);
        setMargin(size, RIGHT);
    }

    /**
     * Sets the margin of one of this component's sides.
     *
     * @param size the size to set the margin to
     * @param sideEnum the side to set
     * @see BlockStylable#setMargin(Size)
     * @pre size != null
     */
    public void setMargin(Size size, int sideEnum) {
        setAttribute(getSideStub(sideEnum) + "Margin", size.toString());
    }

    /**
     * Sets the horizontal alignment of this component.
     *
     * @param alignmentEnum the horizontal alignment (LEFT, RIGHT, or
     * CENTER)
     */
    public void setHorizontalAlignment(int alignmentEnum) {
        String alignmentLiteral = "";

        switch (alignmentEnum) {
        case LEFT:
            alignmentLiteral = "left";
            break;
        case RIGHT:
            alignmentLiteral = "right";
            break;
        case CENTER:
            alignmentLiteral = "center";
            break;
        default:
            throw new IllegalArgumentException("Undefined Arg in setHorizontalAlignment");
        }

        setAttribute("horizontalAlignment", alignmentLiteral);
    }

    /**
     * Sets the vertical alignment of this component.
     *
     * @param alignmentEnum the vertical alignment (TOP, BOTTOM, or
     * MIDDLE)
     */
    public void setVerticalAlignment(int alignmentEnum) {
        String alignmentLiteral = "";

        switch (alignmentEnum) {
        case TOP:
            alignmentLiteral = "top";
            break;
        case BOTTOM:
            alignmentLiteral = "bottom";
            break;
        case CENTER:
            alignmentLiteral = "center";
            break;
        default:
            throw new IllegalArgumentException("Undefined Arg in setVerticalAlignment");
        }

        setAttribute("verticalAlignment", alignmentLiteral);
    }

}
