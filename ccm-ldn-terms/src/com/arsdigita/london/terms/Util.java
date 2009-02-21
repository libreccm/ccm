/*
 * Created on 11-Nov-05
 *
 */
package com.arsdigita.london.terms;

import org.apache.log4j.Logger;

// unused import
// import com.arsdigita.cms.ContentBundle;
// import com.arsdigita.cms.ContentItem;
// import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * Some useful methods that may be used by different applications
 */
public class Util {

		private static Logger s_log = Logger.getLogger(Util.class);

	
	public static Domain getApplicationDomain(Application app) {
			Domain applicationDomain;
			DataQuery domains =
				SessionManager.getSession().retrieveQuery(
					"com.arsdigita.london.terms.DefaultApplicationDomain");
			domains.setParameter("path", app.getPath() + "/");
			if (domains.next()) {
				applicationDomain =
					Domain.retrieve((String) domains.get("domainKey"));
			} else {
				throw new UncheckedWrapperException(
                      "No Default Navigation domain found for application " +
                      app == null ? null : app.getTitle());
			}
			domains.close();
		
		return applicationDomain;
	}

	

	/**
	 * retrieve a unique integer to allocate to a new term.
	 * Useful for applications that dynamically generate terms.
	 */
	
	public static Integer getNextTermID(Domain domain) {

		DomainCollection terms = domain.getTerms();
		terms.addOrder(Term.UNIQUE_ID + " desc");
		int id = 1;
		if (terms.next()) {
			Term other = (Term) terms.getDomainObject();
			id = other.getUniqueID().intValue() + 1;
			terms.close();
		}
		return new Integer(id);
	}

	
}

