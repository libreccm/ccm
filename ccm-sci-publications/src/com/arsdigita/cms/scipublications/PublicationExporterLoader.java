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
public class PublicationExporterLoader extends PackageLoader {

    private static final Logger logger = Logger.getLogger(
            PublicationExporterLoader.class);

    @Override
    public void run(final ScriptContext ctx) {

        new KernelExcursion() {

            @Override
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                ApplicationSetup setup = new ApplicationSetup(logger);

                setup.setApplicationObjectType(
                        PublicationExporter.BASE_DATA_OBJECT_TYPE);
                setup.setKey("scipubliationsexporter");
                setup.setTitle("sci-publication Exporter");
                setup.setDescription(
                        "Exports publication data in various formats");
                //setup.setSingleton(true);
                setup.setInstantiator(new ACSObjectInstantiator() {
                   @Override
                   public DomainObject doNewInstance(DataObject dataObject) {
                       return new PublicationExporter(dataObject);
                   }
                });

                ApplicationType type = setup.run();
                type.save();

                if (!Application.isInstalled(
                        PublicationExporter.BASE_DATA_OBJECT_TYPE, "/scipublicationsexporter/")) {
                    Application app = Application.createApplication(type,
                                                                    "scipublicationsexporter",
                                                                    "PublicationExporter",
                                                                    null);
                    app.save();
                }

            }
        }.run();
    }
}
