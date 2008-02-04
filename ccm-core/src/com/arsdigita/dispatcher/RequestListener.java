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
package com.arsdigita.dispatcher;

import java.util.EventListener;

/**
 * Used to register callbacks for the code to run
 * when a request starts or ends.
 *
 * @author Bill Schneider 
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 * @since 4.5 */
public interface RequestListener extends EventListener {

    public static final String versionId = "$Id: RequestListener.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Called when ACSServlet starts processing an incoming request.
     * @param e the event
     */
    public void requestStarted(RequestEvent e);

    /**
     * Called when ACSServlet finishes processing an incoming request.
     * @param e the event
     */
    public void requestFinished(RequestEvent e);
}
