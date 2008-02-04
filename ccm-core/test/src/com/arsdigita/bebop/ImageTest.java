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


/**
 * Regression tests for the Image component.
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public class ImageTest extends XMLComponentRegressionBase {

    public static final String versionId = "$Id: ImageTest.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public ImageTest (String id) {
        super(id);
    }

    /**
     *  Test an empty Image.
     */
    public void testEmptyImage() {
        testComponent(new Image("http://www.arsdigita.com/graphics/eyes-18a.jpg"),"empty");
    }

    /**
     *  Test an empty Image with an ALT tag.
     */
    public void testImageWithAlt() {
        testComponent(new Image("http://www.arsdigita.com/graphics/eyes-18a.jpg","The Eyes"),"with-alt");
    }

    /**
     *  Test ALT, but using the set method.
     */
    public void testImageAlt() {
        Image eyes = new Image("http://www.arsdigita.com/graphics/eyes-18a.jpg");
        eyes.setAlt("The Eyes");
        testComponent(eyes,"with-alt");
    }

    /**
     *  Test setHeight, setWidth, setBorder.
     */
    public void testImageWidthHeightBorder() {
        Image eyes = new Image("http://www.arsdigita.com/graphics/eyes-18a.jpg");
        eyes.setWidth("481");
        eyes.setHeight("118");
        eyes.setBorder("0");
        testComponent(eyes,"width-height-border");
    }

}
