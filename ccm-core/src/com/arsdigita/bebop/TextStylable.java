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


/**
 * Encapsulates standard methods to style text in a
 * Bebop component.
 *
 * @version $Id: TextStylable.java 287 2005-02-22 00:29:02Z sskracic $
 * */
abstract public class TextStylable extends SimpleComponent {

    public static final String versionId = "$Id: TextStylable.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Sets a component's foreground or text color.
     *
     * @param color the color to set for this component
     * @pre color != null
     */
    public void setColor(Color color) {
        setAttribute("color", color.toString());
    }

    /**
     * Sets a component's background color.
     *
     * @param backgroundColor the color to set for this component's background
     * @pre backgroundColor != null
     */
    public void setBackgroundColor(Color backgroundColor) {
        setAttribute("backgroundColor", backgroundColor.toString());
    }

}
