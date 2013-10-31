/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.scipublications;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;
import org.apache.log4j.Logger;

/**
 * Loader for the SciPublications application.
 * 
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciPublicationsLoader extends PackageLoader {

    private static final Logger logger = Logger.getLogger(
            SciPublicationsLoader.class);

    @Override
    public void run(final ScriptContext ctx) {

        new KernelExcursion() {

            @Override
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                final ApplicationType type = new ApplicationType(
                        "SciPublications",
                        SciPublications.BASE_DATA_OBJECT_TYPE);
                type.setSingleton(true);
                type.setDescription("Publications Import and Export");
                
                Application.createApplication(
                        SciPublications.BASE_DATA_OBJECT_TYPE, 
                        "scipublications", 
                        "SciPublications", 
                        null);

            }
        }.run();
    }
}
