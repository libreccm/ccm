/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.kernel;

import java.util.Locale;

/**
 * Provides access to protected kernel facilities for tests outside the kernel package.
 */
public class TestHelper {

    public static Party setCurrentSystemParty(Party newParty) {
        KernelContext ctx = Kernel.getContext();
        Party oldParty = ctx.getParty();
        ctx.setParty(newParty);
        return oldParty;
    }

    public static Locale setLocale(Locale newLocale) {
        KernelContext ctx = Kernel.getContext();
        Locale oldLocale = ctx.getLocale();
        ctx.setLocale(newLocale);
        return oldLocale;
    }

}
