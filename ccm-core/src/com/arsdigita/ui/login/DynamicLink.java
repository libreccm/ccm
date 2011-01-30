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
package com.arsdigita.ui.login;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.kernel.security.LegacyInitializer;

/**
 * Package-private class that generates the URL for a link dynamically from
 * the kernel page map.  This class will be removed or changes when the page
 * map is replaced by package parameters.
 *
 * @author Sameer Ajmani
 **/
class DynamicLink extends Link {
    public static final String versionId = 
        "$Id: DynamicLink.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    DynamicLink(final String labelKey, final String targetKey) {
        super(new Label(LoginHelper.getMessage(labelKey)),
              new PrintListener() {
                  public void prepare(PrintEvent e) {
                      Link link = (Link) e.getTarget();

                      String url = LegacyInitializer.getFullURL
                          (targetKey, e.getPageState().getRequest());

                      link.setTarget(url);
                  }
              });
    }
}
