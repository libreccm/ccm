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

import org.apache.log4j.Logger;
import java.util.Locale;

/**
 *
 * A kernel excursion is a way of making your code execute under an
 * alternative environment (context). Override the excurse method to
 * create a KernelExcursion. For example:
 *
 * <code>
 *      KernelExcursion rootExcursion = new KernelExcursion() {
 *              public void excurse() {
 *                  // Set up specific context variables.
 *                  setEffectiveParty(Kernel.getSystemParty());
 *
 *                  // Execute code in new context.
 *
 *              }};
 *
 *      rootExcursion.run();
 * </code>
 * @version $Id: KernelExcursion.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class KernelExcursion implements Runnable {

    private static final Logger s_log = Logger.getLogger
        (KernelExcursion.class);

    @Override
    public final void run() {
        s_log.debug("Running excursion");

        KernelContext context = Kernel.getContext();

        Kernel.setContext(context.copy());

        try {
            excurse();
        } finally {
            Kernel.setContext(context);
        }
    }

    /**
     * <p>When this method is called the current KernelContext is
     * copied and the code inside the excurse() method is given the
     * opportunity to modify the new context. Any code then called
     * from within the excurse() method is executed in this new
     * context. After the excurse() method completes the old context
     * is restored exactly as it was before. This makes it unnecessary
     * to write error prone code like this:</p>
     *
     * <blockquote><pre>
     *   Party oldParty = context.getParty();
     *   context.setParty(newParty);
     *   ...
     *   // do something
     *   ...
     *   context.setParty(oldParty); // If this is forgotten, bad
     *                               // things can happen.
     * </pre></blockquote>
     */
    protected abstract void excurse();

    protected final void setEffectiveParty(Party party) {
        Kernel.getContext().setEffectiveParty(party);
    }

    protected final void setParty(Party party) {
        Kernel.getContext().setParty(party);
    }

    protected final void setResource(Resource app) {
        Kernel.getContext().setResource(app);
    }

    protected final void setLocale(Locale locale) {
        Kernel.getContext().setLocale(locale);
    }

    protected final void setSessionID(String sessionID) {
        Kernel.getContext().setSessionID(sessionID);
    }

    protected final void setTransaction(DatabaseTransaction transaction) {
        Kernel.getContext().setTransaction(transaction);
    }
}
