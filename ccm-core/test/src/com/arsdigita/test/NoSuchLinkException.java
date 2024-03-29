/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.test;

import com.meterware.httpunit.WebResponse;
import java.io.IOException;

/**
 * Class NoSuchLinkException
 * 
 * @author jorris@redhat.com
 * @version $Revision $1 $ $Date: 2004/08/16 $
 */
public class NoSuchLinkException extends HttpUnitException {

    public NoSuchLinkException(final String linkText, final WebResponse resp) {
        super("No link named " +
                linkText +
                System.getProperty("line.separator") +
                getPageHTML(resp));
    }

    private static String getPageHTML(final WebResponse resp) {
        String text;
        try {
            text = resp.getText();
        } catch (IOException e) {
            text = "ERROR: Could not get HTML from WebResponse";
        }
        return text;
    }

}
