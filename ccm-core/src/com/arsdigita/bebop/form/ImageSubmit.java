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
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.parameters.ParameterModel;

/**
 *    A class representing an image HTML form element.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Rory Solomon 
 *    @author Michael Pih 
 *    @version $Id: ImageSubmit.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ImageSubmit extends Widget {

    public static final String versionId = "$Id: ImageSubmit.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     *      Constant for specifying LEFT alignment of this image input.
     *      See <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">here</a>
     *      for a description of what this attribute does.
     */
    //    public static final int LEFT = 0;

    /**
     *      Constant for specifying RIGHT alignment of this image input.
     *      See <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">here</a>
     *      for a description of what this attribute does.
     */
    //   public static final int RIGHT = 1;

    /**
     *      Constant for specifying TOP alignment of this image input.
     *      See <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">here</a>
     *      for a description of what this attribute does.
     */
    //  public static final int TOP = 2;

    /**
     *      Constant for specifying ABSMIDDLE alignment of this image input.
     *      See <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">here</a>
     *      for a description of what this attribute does.
     */
    // public static final int ABSMIDDLE = 3;

    /**
     *      Constant for specifying ABSBOTTOM alignment of this image input.
     *      See <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">here</a>
     *      for a description of what this attribute does.
     */
    //public static final int ABSBOTTOM = 4;

    /**
     *      Constant for specifying TEXTOP alignment of this image input.
     *      See <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">here</a>
     *      for a description of what this attribute does.
     */
    // public static final int TEXTTOP = 5;

    /**
     *      Constant for specifying MIDDLE alignment of this image input.
     *      See <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">here</a>
     *      for a description of what this attribute does.
     */
    // public static final int MIDDLE = 6;

    /**
     *      Constant for specifying BASELINE alignment of this image input.
     *      See <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">here</a>
     *      for a description of what this attribute does.
     */
    //public static final int BASELINE = 7;

    /**
     *      Constant for specifying BOTTOM alignment of this image input.
     *      See <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">here</a>
     *      for a description of what this attribute does.
     */
    //public static final int BOTTOM = 8;

    public ImageSubmit(String name) {
        super(name);
    }

    public ImageSubmit(ParameterModel model) {
        super(model);
    }

    /**
     *      Returns a string naming the type of this widget.
     */
    public String getType() {
        return "image";
    }

    /**
     *      Sets the <tt>SRC</tt> attribute for the <tt>INPUT</tt> tag
     *      used to render this form element.
     */
    public void setSrc(String location) {
        setAttribute("src",location);
    }

    /*
     * Sets the <tt>ALRT</tt> attribute for the <tt>INPUT</tt> tag
     * used to render this form element.
     */
    public void setAlt(String alt) {
        setAttribute("alt",alt);
    }

    /**
     *      Sets the <tt>ALIGN</tt> attribute for the <tt>INPUT</tt> tag
     *      used to render this form element.
     */
    public void setAlign(int align) {
        String alignString = null;

        switch (align) {
        case LEFT:
            alignString = "left";
            break;
        case RIGHT:
            alignString = "right";
            break;
        case TOP:
            alignString = "top";
            break;
        case ABSMIDDLE:
            alignString = "absmiddle";
            break;
        case ABSBOTTOM:
            alignString = "absbottom";
            break;
        case TEXTTOP:
            alignString = "texttop";
            break;
        case MIDDLE:
            alignString = "middle";
            break;
        case BASELINE:
            alignString = "baseline";
            break;
        case BOTTOM:
            alignString = "botton";
            break;
        }

        if (alignString != null)
            setAttribute("align",alignString);
    }

    public boolean isCompound() {
        return false;
    }

    /**
     *      Callback method for rendering this Image widget in a visitor.
     */
    /*  public void accept(FormVisitor visitor) throws IOException {
        visitor.visitImage(this);
        }*/


}
