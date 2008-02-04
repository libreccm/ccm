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
package com.arsdigita.cms.publishToFile;

/**
 * RetrievedFile
 *
 * Class for storing properties of file read from server (that
 * is to be written to the file system.
 *
 */
final class RetrievedFile {
   final String    body;          // body of file
   final String    contentType;   // content Type (e.g. text/html)

   /***
    * Constructor, saves passed in values
    ***/
   public RetrievedFile (String bdy, String ct) {
     body = bdy;
     contentType = ct;
   }
}
