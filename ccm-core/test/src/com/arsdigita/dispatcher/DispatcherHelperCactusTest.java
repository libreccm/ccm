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

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.InitialRequestContext;
import com.arsdigita.dispatcher.RequestContext;
import org.apache.cactus.ServletTestCase;

public class DispatcherHelperCactusTest extends ServletTestCase {


    public DispatcherHelperCactusTest(String theName) {
        super(theName);
    }


    public void setUp() {
    }

    public void testResourcePath() {

    }

    /** Tests stashing away and retrieving RequestContext objects
     */

    public void testRequextContextHelper()
        throws javax.servlet.ServletException, java.io.IOException {

        RequestContext reqCtx = new
            InitialRequestContext(request, config.getServletContext());

        DispatcherHelper.setRequestContext(request, reqCtx);
        RequestContext otherReqCtx =
            DispatcherHelper.getRequestContext(request);

        assertEquals(reqCtx, otherReqCtx);
    }

    public void testforwardingRequests() {
    }

}



/*
  methods to test
  public static String getCurrentResourcePath

  public static void forwardRequestByPath(String path,
  public static void forwardRequestByPath(String path,
  public static void forwardRequestByName(String name,

  public static HttpServletRequest restoreOriginalRequest
  public static HttpServletRequest restoreRequestWrapper
  public static void saveOriginalRequest(HttpServletRequest req,


  public static String resolveAbstractFile(File abstractFile,

  public static void sendRedirect(HttpServletResponse resp,

  public final static boolean emptyString(Object o)
*/
