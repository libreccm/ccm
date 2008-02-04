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
package com.arsdigita.cms;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.log4j.Logger;

/**
 *
 * A CMS excursion is a way of making your code execute under an
 * alternative environment (context). Override the excurse method to
 * create a CMSExcursion. For example:
 *
 * <pre>
 *      CMSExcursion excursion = new CMSExcursion() {
 *              public void excurse() {
 *                  // Set up specific context variables.
 *                  setContentItem(ItemDispatcher.getContentItem());
 *
 *                  // Execute code in new context.
 *
 *              }};
 *
 *      excursion.run();
 * </pre>
 *
 * @author Daniel Berrange
 * @see com.arsdigita.cms.CMS
 * @see com.arsdigita.kernel.KernelExcursion
 */
public abstract class CMSExcursion {
    public static final String versionId =
        "$Id: CMSExcursion.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger(CMSExcursion.class);

    /**
     * Begins execution of the excursion. This locks
     * the pending context, sets it to be the current
     * context and invokes the excuse method. The
     * original context is restored upon termination of
     * the excurse method
     */
    public final void run()
        throws ServletException, IOException {

        s_log.debug("Running excursion");

        CMSContext context = CMS.getContext();

        CMS.setContext(context.copy());

        try {
            final ServletException[] servletException = { null };
            final IOException[] ioException = { null };

            new KernelExcursion() {
                protected final void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());

                    try {
                        CMSExcursion.this.excurse();
                    } catch (ServletException se) {
                        servletException[0] = se;
                    } catch (IOException ioe) {
                        ioException[0] = ioe;
                    }
                }
            }.run();

            if (servletException[0] != null) {
                throw servletException[0];
            }

            if (ioException[0] != null) {
                throw ioException[0];
            }
        } finally {
            CMS.setContext(context);
        }
    }

    /**
     * <p>When this method is called the current CMSContext is
     * copied and the code inside the excurse() method is given the
     * opportunity to modify the new context. Any code then called
     * from within the excurse() method is executed in this new
     * context. After the excurse() method completes the old context
     * is restored exactly as it was before. This makes it unnecessary
     * to write error prone code like this:</p>
     *
     * <blockquote><pre>
     *   ContentItem oldItem = context.getContentItem();
     *   context.setContentItem(newItem);
     *   ...
     *   // do something
     *   ...
     *   context.setCOntentItem(oldItem); // If this is forgotten, bad
     *                               // things can happen.
     * </pre></blockquote>
     */
    protected abstract void excurse() throws ServletException, IOException;

    /**
     * Sets the current content section.
     *
     * @param section the new content section
     */
    protected final void setContentSection(ContentSection section) {
        CMS.getContext().setContentSection(section);
    }

    /**
     * Sets the current content item.
     *
     * @param item the new content item
     */
    protected final void setContentItem(ContentItem item) {
        CMS.getContext().setContentItem(item);
    }

    /**
     * Sets the current security manager.
     *
     * @param security the new <code>SecurityManager</code>
     */
    protected final void setSecurityManager(final SecurityManager security) {
        CMS.getContext().setSecurityManager(security);
    }
}
