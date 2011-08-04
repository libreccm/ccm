package com.arsdigita.cms.publicpersonalprofile;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationSetup;
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

                /*ApplicationSetup setup = new ApplicationSetup(logger);
                
                setup.setApplicationObjectType(
                PublicPersonalProfiles.BASE_DATA_OBJECT_TYPE);
                setup.setKey("profiles");
                setup.setTitle("PublicPersonalProfiles");
                setup.setDescription("Display public personal profiles");
                
                setup.setInstantiator(new ACSObjectInstantiator() {
                
                @Override
                public DomainObject doNewInstance(
                final DataObject dataObject) {
                return new PublicPersonalProfiles(dataObject);
                }
                });
                
                ApplicationType type = setup.run();                
                type.save();
                
                if (!Application.isInstalled(
                PublicPersonalProfiles.BASE_DATA_OBJECT_TYPE,
                "/profiles/")) {
                /*Application app = Application.createRootApplication(type,
                "profiles",
                false);*/
                /*Application app = Application.createApplication(type,
                "profiles",
                "profiles",
                null);
                
                app.save();
                }*/

                ApplicationType type =
                                new ApplicationType("PublicPersonalProfile",
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
