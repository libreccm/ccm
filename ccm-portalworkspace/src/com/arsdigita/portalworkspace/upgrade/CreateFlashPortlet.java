/*
 * Copyright (C) 2008 Permeance Technologies Ptd Ltd. All Rights Reserved.
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
 */

package com.arsdigita.portalworkspace.upgrade;

import org.apache.commons.cli.CommandLine;

import com.arsdigita.portalworkspace.portlet.FlashPortlet;
import com.arsdigita.portalworkspace.portlet.FlashPortletInitializer;
import com.arsdigita.london.util.Transaction;
import com.arsdigita.packaging.Program;

/**
 * Loads the {@link FlashPortlet}.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 * @see FlashPortletInitializer#loadPortletType()
 */

public final class CreateFlashPortlet extends Program
{
    private static final String PROGRAM_NAME = CreateFlashPortlet.class.getName().substring(
            CreateFlashPortlet.class.getName().lastIndexOf('.') + 1);

    public static void main(String[] args)
    {
        new CreateFlashPortlet().run(args);
    }

    protected void doRun(CommandLine cmdLine)
    {
        new Transaction()
        {
            protected void doRun()
            {
                FlashPortletInitializer.loadPortletType();
            }
        }.run();
    }

    private CreateFlashPortlet()
    {
        super(PROGRAM_NAME, "1.0.0", "");
    }
}
