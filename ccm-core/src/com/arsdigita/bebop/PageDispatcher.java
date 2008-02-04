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
package com.arsdigita.bebop;

import com.arsdigita.templating.PresentationManager;

/**
 * @deprecated Use {@link com.arsdigita.bebop.page.PageDispatcher}
 * instead.
 */
public class PageDispatcher
        extends com.arsdigita.bebop.page.PageDispatcher {
    /**
     * Creates a new page dispatcher for a page object and
     * a PresentationManager.
     */
    public PageDispatcher(Page p, PresentationManager pm) {
        super(p, pm);
    }

    /**
     * Creates a new page dispatcher for a page object.  Uses the
     * default presentation manager, BasePresentationManager.
     */
    public PageDispatcher(Page p) {
        super(p);
    }
}
