package org.undp.weblog;

import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.ui.admin.ApplicationManagers;

import org.apache.log4j.Logger;
import org.undp.weblog.ui.WebLogPortlet;


/**
 * @author Peter Kopunec
 */
public class Initializer extends CompoundInitializer {

    /** Creates a s_logging category with name = full name of class */
    private static final Logger s_log = Logger.getLogger(Initializer.class);

    /**
     * 
     */
    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer(
                   new ManifestSource("ccm-weblog.pdl.mf",
                                      new NameFilter(DbHelper.
                                          getDatabaseSuffix(database), "pdl"))));
    }

    /**
     * 
     * @param e 
     */
    @Override
    public void init(DomainInitEvent e) {
        s_log.info("WebLog Initializer starting.");
        super.init(e);

        // register application
/*      DomainObjectInstantiator instantiator = new ACSObjectInstantiator() {
            @Override
            protected DomainObject doNewInstance(DataObject dataObject) {
                return new WebLogApplication(dataObject);
            }
        };
        DomainObjectFactory.registerInstantiator(
                WebLogApplication.BASE_DATA_OBJECT_TYPE, instantiator);   */

        /* Register object instantiator for Bookmarks Application   */
        e.getFactory().registerInstantiator(
            WebLogApplication.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new WebLogApplication(dataObject);
                }
            });
 
        // Register the portlets
/*      instantiator = new ACSObjectInstantiator() {
            @Override
            protected DomainObject doNewInstance(DataObject dataObject) {
                return new WebLogPortlet(dataObject);
            }
        };
        DomainObjectFactory.registerInstantiator(
                WebLogPortlet.BASE_DATA_OBJECT_TYPE, instantiator);  */

        /* Register object instantiator for Bookmarks Portlet   */
        e.getFactory().registerInstantiator(
                WebLogPortlet.BASE_DATA_OBJECT_TYPE,
                new ACSObjectInstantiator() {
                    public DomainObject doNewInstance(DataObject dataObject) {
                        return new WebLogPortlet(dataObject);
                    }
                });

        //Register the ApplicationManager implementation for this application
        ApplicationManagers.register(new WebLogAppManager());
        
        s_log.debug("WebLog Initializer done.");
    }

}
