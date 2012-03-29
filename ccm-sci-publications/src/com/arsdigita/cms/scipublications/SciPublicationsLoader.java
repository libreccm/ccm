package com.arsdigita.cms.scipublications;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
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
