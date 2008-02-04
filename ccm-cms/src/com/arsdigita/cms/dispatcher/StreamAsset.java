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
package com.arsdigita.cms.dispatcher;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.arsdigita.cms.BinaryAsset;


/**
 * A resource handler which streams out a blob from the database.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #13 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class StreamAsset extends BaseAsset {

    public static final String versionId = "$Id: StreamAsset.java 1166 2006-06-14 11:45:15Z fabrice $ by $Author: fabrice $, $DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger(StreamAsset.class);

    public final static String ASSET_ID = BaseAsset.ASSET_ID;

    public StreamAsset() {
        super(false);
    }

    // do not set Content-Disposition for asset streaming 
    protected void setFilenameHeader(HttpServletResponse response,
            BinaryAsset asset) {
    }
}
