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
package com.arsdigita.web;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.util.Assert;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * @deprecated This class is still considered experimental
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: CachePolicy.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class CachePolicy {
    public static final String versionId =
        "$Id: CachePolicy.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(CachePolicy.class);

    public static final CachePolicy USER = new UserPolicy();
    public static final CachePolicy WORLD = new WorldPolicy();
    public static final CachePolicy DISABLE = new DisablePolicy();

    public final void implement(final HttpServletRequest sreq,
                                final HttpServletResponse sresp) {
        if (Assert.isEnabled()) {
            Assert.isTrue(!sresp.isCommitted());
        }

        doImplement(sreq, sresp);

        if (Assert.isEnabled()) {
            Assert.isTrue(!sresp.isCommitted());
        }
    }

    public abstract void doImplement(final HttpServletRequest sreq,
                                     final HttpServletResponse sresp);

    private static class UserPolicy extends CachePolicy {
        public final void doImplement(final HttpServletRequest sreq,
                                      final HttpServletResponse sresp) {
            DispatcherHelper.maybeCacheForUser(sresp);
        }
    }

    private static class WorldPolicy extends CachePolicy {
        public final void doImplement(final HttpServletRequest sreq,
                                      final HttpServletResponse sresp) {
            DispatcherHelper.maybeCacheForWorld(sresp);
        }
    }

    private static class DisablePolicy extends CachePolicy {
        public final void doImplement(final HttpServletRequest sreq,
                                      final HttpServletResponse sresp) {
            DispatcherHelper.maybeCacheDisable(sresp);
        }
    }

}
