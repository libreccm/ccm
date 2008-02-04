package org.undp.weblog;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

/**
 * @author Peter Kopunec
 */
public class WebLog extends ACSObject {

	public static final String BASE_DATA_OBJECT_TYPE = WebLog.class.getName();

	public static final String PARAM_TITLE = "title";

	public static final String PARAM_LEAD = "lead";

	public static final String PARAM_BODY = "body";

	public static final String PARAM_MODIFIED = "modified";

	public static final String PARAM_APPLICATION = "webLogApp";

	public static final String PARAM_OWNER = "owner";

	private static final Logger s_log = Logger.getLogger(WebLog.class);

	public WebLog() {
		super(BASE_DATA_OBJECT_TYPE);
	}

	public WebLog(DataObject dataObject) {
		super(dataObject);
	}

	public WebLog(BigDecimal id) {
		super(new OID(BASE_DATA_OBJECT_TYPE, id));
	}

	protected String getBaseDataObjectType() {
		return BASE_DATA_OBJECT_TYPE;
	}

	public void setTitle(String title) {
		set(PARAM_TITLE, title);
	}

	public void setLead(String lead) {
		set(PARAM_LEAD, lead);
	}

	public void setBody(String body) {
		set(PARAM_BODY, body);
	}

	public void setApplication(WebLogApplication app) {
		setAssociation(PARAM_APPLICATION, app);
	}

	public void setOwner(User owner) {
		setAssociation(PARAM_OWNER, owner);
	}

	public String getTitle() {
		return (String) get(PARAM_TITLE);
	}

	public String getLead() {
		return (String) get(PARAM_LEAD);
	}

	public String getBody() {
		return (String) get(PARAM_BODY);
	}

	public Date getModified() {
		return (Date) get(PARAM_MODIFIED);
	}

	public WebLogApplication getApplication() {
		DataObject entityAppData = (DataObject) get(PARAM_APPLICATION);
		if (entityAppData != null) {
			return new WebLogApplication(entityAppData);
		}
		return null;
	}

	public User getOwner() {
		DataObject entityData = (DataObject) get(PARAM_OWNER);
		if (entityData != null) {
			return User.retrieve(entityData);
		}
		return null;
	}

	protected void beforeSave() {
		set(PARAM_MODIFIED, new Date());
		super.beforeSave();
	}

	protected void beforeDelete() {
		DataCollection coll = SessionManager.getSession().retrieve(
				WebLogComment.BASE_DATA_OBJECT_TYPE);
		coll.addEqualsFilter(WebLogComment.PARAM_WEBLOG + '.' + ID, getID());
		WebLogComment comment;
		while (coll.next()) {
			comment = new WebLogComment(coll.getDataObject());
			comment.delete();
		}
		coll.close();
		super.beforeDelete();
	}

	public DataCollection getComments() {
		DataCollection coll = SessionManager.getSession().retrieve(
				WebLogComment.BASE_DATA_OBJECT_TYPE);
		coll.addEqualsFilter(WebLogComment.PARAM_WEBLOG + '.' + ID, getID());
		coll.addOrder(WebLogComment.PARAM_MODIFIED + " desc");
		return coll;
	}

	protected void afterSave() {
		super.afterSave();
		PermissionService.setContext(this, getApplication());
	}

}
