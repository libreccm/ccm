package com.arsdigita.cms.publicpersonalprofile;

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
 * @version $Id$
 */
public class PublicPersonalProfilesLoader extends PackageLoader {

    private static final Logger logger =
                                Logger.getLogger(
            PublicPersonalProfilesLoader.class);

    @Override
    public void run(final ScriptContext ctx) {

        new KernelExcursion() {

            @Override
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                final ApplicationType type = new ApplicationType(
                        "PublicPersonalProfile",
                        PublicPersonalProfiles.BASE_DATA_OBJECT_TYPE);
                type.setDescription("PublicPersonalProfile Viewer");

                Application.createApplication(
                        PublicPersonalProfiles.BASE_DATA_OBJECT_TYPE,
                        "profiles",
                        "PublicPersonalProfiles",
                        null);
            }
        }.run();


    }
}
