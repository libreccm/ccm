package org.undp.weblog;

import org.apache.log4j.Logger;
import org.undp.weblog.ui.WebLogPortlet;

import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
// import com.arsdigita.initializer.InitializationException;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.PackageType;
// import com.arsdigita.kernel.Stylesheet;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.web.ApplicationType;

/**
 * @author Peter Kopunec
 */

public class Initializer extends CompoundInitializer {

	private static final Logger s_log = Logger.getLogger(Initializer.class);

//ublic Initializer() throws InitializationException {
	public Initializer()  {
		final String url = RuntimeConfig.getConfig().getJDBCURL();
		final int database = DbHelper.getDatabaseFromURL(url);

		add(new PDLInitializer(new ManifestSource("ccm-weblog.pdl.mf",
				new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
	}

    @Override
	public void init(DomainInitEvent e) {
		s_log.info("WebLog Initializer starting.");

		boolean isMyTransaction = false;
		TransactionContext txn = SessionManager.getSession()
				.getTransactionContext();
		if (!txn.inTxn()) {
			txn.beginTxn();
			isMyTransaction = true;
		}

		// register application
		DomainObjectInstantiator instantiator = new ACSObjectInstantiator() {
            @Override
			protected DomainObject doNewInstance(DataObject dataObject) {
				return new WebLogApplication(dataObject);
			}
		};
		DomainObjectFactory.registerInstantiator(
				WebLogApplication.BASE_DATA_OBJECT_TYPE, instantiator);
		checkSetup();

		// Register the portlets
		instantiator = new ACSObjectInstantiator() {
            @Override
			protected DomainObject doNewInstance(DataObject dataObject) {
				return new WebLogPortlet(dataObject);
			}
		};
		DomainObjectFactory.registerInstantiator(
				WebLogPortlet.BASE_DATA_OBJECT_TYPE, instantiator);

		if (isMyTransaction) {
			txn.commitTxn();
		}

		s_log.debug("WebLog Initializer done.");
	}

	private void checkSetup() {
		try {
			s_log.debug("WebLog Initializer - verifying setup.");
			PackageType entityType = PackageType.findByKey("weblog");
		} catch (DataObjectNotFoundException e) {
			setup();
		}
	}

	private void setup() {
		s_log.info("WebLog Initializer - setting up new package");

		PackageType entityType = PackageType.create("weblog", "WebLog",
				"WebLogs", "http://www.undp.org/weblog");
		s_log.debug("Just added package type WebLog ");


		entityType.setDispatcherClass(WebLogDispatcher.class.getName());

		entityType.save();

		final ApplicationType entityAppType = ApplicationType
				.createApplicationType(entityType, "WebLog Application",
						WebLogApplication.BASE_DATA_OBJECT_TYPE);
		entityAppType.save();

		// portlet
		AppPortletType portletType = AppPortletType.createAppPortletType(
				"WebLog Portlet", AppPortletType.WIDE_PROFILE,
				WebLogPortlet.BASE_DATA_OBJECT_TYPE);
		portletType.setProviderApplicationType(entityAppType);
		portletType.setPortalApplication(true);
		portletType.save();

	}
}
