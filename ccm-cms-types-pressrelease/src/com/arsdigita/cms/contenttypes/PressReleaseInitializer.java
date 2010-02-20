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
package com.arsdigita.cms.contenttypes;

import org.apache.log4j.Logger;

/**
 * Initializer
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/17 $
 * @version $Id: PressReleaseInitializer.java 757 2005-09-02 14:12:21Z sskracic $
 **/

public class PressReleaseInitializer extends ContentTypeInitializer {

    private static final Logger s_log = Logger.getLogger(PressReleaseInitializer.class);

    /**
     * Constructor, sets the PDL manifest file and object type string.
     */
    public PressReleaseInitializer() {
        super("ccm-cms-types-pressrelease.pdl.mf", PressRelease.BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Retrieve location of this content types stylesheet. Overwrites parent
     * method with FormItem specific value for use by the parent class worker
     * methods.
     *
     * @return complete path info string
     */
    public String[] getStylesheets() {
        return new String[] {
            "/static/content-types/com/arsdigita/cms/contenttypes/PressRelease.xsl" };
    }

}
