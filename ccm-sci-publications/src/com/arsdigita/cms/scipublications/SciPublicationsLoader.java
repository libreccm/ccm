package com.arsdigita.cms.scipublications;

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

                ApplicationSetup setup = new ApplicationSetup(logger);

                setup.setApplicationObjectType(
                        SciPublications.BASE_DATA_OBJECT_TYPE);
                setup.setKey("scipubliations");
                setup.setTitle("sci-publications");
                setup.setDescription(
                        "Provides several functions like export and import for publications.");
                //setup.setSingleton(true);
                setup.setInstantiator(new ACSObjectInstantiator() {
                   @Override
                   public DomainObject doNewInstance(DataObject dataObject) {
                       return new SciPublications(dataObject);
                   }
                });

                ApplicationType type = setup.run();
                type.save();

                //Install the application and mount the app at 'scipublications'.
                if (!Application.isInstalled(
                        SciPublications.BASE_DATA_OBJECT_TYPE, "/scipublications/")) {
                    Application app = Application.createApplication(type,
                                                                    "scipublications",
                                                                    "SciPublications",
                                                                    null);
                    app.save();
                }

            }
        }.run();
    }
}
