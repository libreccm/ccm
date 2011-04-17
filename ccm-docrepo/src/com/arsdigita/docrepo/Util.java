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
package com.arsdigita.docrepo;

import com.arsdigita.bebop.PageState;
import com.arsdigita.web.LoginSignal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * 
 * 
 */
public class Util {
    private static Logger s_log = Logger.getLogger(Util.class);

    public static void redirectToLoginPage(PageState ps) {
        throw new LoginSignal(ps.getRequest());
    }

    /**
     * Guess the content type for a file by checking the file
     * extension agains the know database of types, or for the
     * existence of a MIME type header in an HttpServletRequest.  If
     * these both fail, or if the content type set in the request is
     * not one of the recognized types on the system, return a content
     * type of "application/octet-stream".
     *
     * @param name the name of a file to be used for an
     * extension-based type lookup
     * @param request an HttpServletRequest which might contain a
     * Content-Type header, and can be null
     */
    public static String guessContentType(String name,
                                          HttpServletRequest request) {

        s_log.debug("CALLED", new Throwable());
        // Try looking up the type based on the filename extensions

        com.arsdigita.mimetypes.MimeType mimeType =
            com.arsdigita.mimetypes.MimeType.guessMimeTypeFromFile(name);

        // Try looking up from the request.  We require that the
        // resolved type correspond to a known MIME type in the

        if (mimeType == null && request != null) {
            String contentType = request.getHeader("Content-Type");
            s_log.debug("Retrieved content type " + contentType +
                        "from request " + request);
            if (contentType != null) {
                mimeType = com.arsdigita.mimetypes.MimeType.loadMimeType(contentType);
                if (mimeType == null) {
                    s_log.warn("Couldn't load mime type for " + contentType);
                }
            }
        }

        if (mimeType != null) {
            return mimeType.getMimeType();
        } else {
            return File.DEFAULT_MIME_TYPE;
        }
    }
}
