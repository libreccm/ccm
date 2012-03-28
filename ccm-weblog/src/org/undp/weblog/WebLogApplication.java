package org.undp.weblog;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;

/**
 * Application domain class for the WebLog application.
 * 
 * @author Peter Kopunec
 * @version $Id: WebLogApplication.java $
 */
public class WebLogApplication extends Application {

    /** Logger instance for debugging  */
	private static final Logger s_log = Logger
			.getLogger(WebLogApplication.class);

    // pdl stuff (constants)
	public static final String BASE_DATA_OBJECT_TYPE = WebLogApplication.class
			.getName();

    /**
     * Constructs a service domain object from the underlying data object.
     * 
     * @param obj the DataObject
     */
    public WebLogApplication(DataObject obj) {
		super(obj);
	}

    /**
     * Constructor retrieving WebLogApplication from the database usings its OID.
     *
     * @param obj 
     * @throws DataObjectNotFoundException
     */
	public WebLogApplication(OID oid) throws DataObjectNotFoundException {
		super(oid);
	}

	public WebLogApplication(BigDecimal id) throws DataObjectNotFoundException {
		this(new OID(BASE_DATA_OBJECT_TYPE, id));
	}

    /**
     * Getter to retrieve the base database object type name
     *
     * @return base data aoject type as String
     */
    @Override
	protected String getBaseDataObjectType() {
		return BASE_DATA_OBJECT_TYPE;
	}

	/**
	 * This method should be used to create a new WebLogApplication object
	 * everywhere except in the constructor of a subclass of WebLog.
	 */
	// this constructor is unused AFAICT
	public static WebLogApplication create(String urlName, String title,
			Application parent) {
		WebLogApplication webLogApplication = (WebLogApplication) Application
				.createApplication(BASE_DATA_OBJECT_TYPE, urlName, title,
						parent);

		webLogApplication.save();
		return webLogApplication;
	}

	/**
	 * Overrides the superclass adding some permissions.
	 */
    @Override
	protected void afterSave() {
		super.afterSave();
		Party currentParty = Kernel.getContext().getParty();
		final PermissionDescriptor pd = new PermissionDescriptor(
				PrivilegeDescriptor.ADMIN, WebLogApplication.this, currentParty);
		new KernelExcursion() {
			protected void excurse() {

				setParty(Kernel.getSystemParty());
				PermissionService.grantPermission(pd);
			}
		}.run();
	}

    /**
     * 
     */
    @Override
    public String getServletPath() {
        return "/weblog";
    }
}
