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
package com.arsdigita.cms.publishToFile;
import java.net.URLConnection;


/**
 * Interface defining a method which sets one or more "passwords" (i.e.
 * cookies) on the request that PublishToFile.readHTML makes to itself to
 * get content to publish.  The cookies can be inspected by application
 * specific code that processes the request to insure that the request
 * is from the server.
 *
 * @see PublishToFile
 *
 * @author Jeff Teeters (teeters@arsdigita.com)
 * @version $Revision: #7 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: SetLocalRequestPassword.java 2090 2010-04-17 08:04:14Z pboy $
 */


public interface SetLocalRequestPassword {

  /**
   * Sets one or more passwords (cookies) on a connection that is the
   * request to the server.
   * @param con  connection being made to server.
   */
  public void setPassword(URLConnection con);

}
