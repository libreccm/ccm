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

/**
 * Initializer
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/17 $
 **/

public class JobInitializer extends ContentTypeInitializer {

    public final static String versionId = "$Id: JobInitializer.java 757 2005-09-02 14:12:21Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";


    public JobInitializer() {
        super("ccm-cms-types-job.pdl.mf", Job.BASE_DATA_OBJECT_TYPE);
    }

    public String getTraversalXML() {
        return "WEB-INF/traversal-adapters/com/arsdigita/cms/contenttypes/Job.xml";
    }

    public String[] getStylesheets() {
        return new String[] {"/static/content-types/com/arsdigita/cms/contenttypes/Job.xsl"};
    }


}
