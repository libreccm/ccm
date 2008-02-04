package org.undp.weblog;

import java.math.BigDecimal;
import java.util.Date;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * @author Peter Kopunec
 */
public class WebLogComment extends ACSObject {

	public static final String BASE_DATA_OBJECT_TYPE = WebLogComment.class
			.getName();

	public static final String PARAM_COMMENT = "comment";

	public static final String PARAM_MODIFIED = "modified";

	public static final String PARAM_WEBLOG = "webLog";

	public static final String PARAM_OWNER = "owner";

	public WebLogComment() {
		super(BASE_DATA_OBJECT_TYPE);
	}

	public WebLogComment(DataObject dataObject) {
		super(dataObject);
	}

	public WebLogComment(BigDecimal id) {
		super(new OID(BASE_DATA_OBJECT_TYPE, id));
	}

	protected String getBaseDataObjectType() {
		return BASE_DATA_OBJECT_TYPE;
	}

	public void setComment(String comment) {
		set(PARAM_COMMENT, comment);
	}

	public void setWebLog(WebLog webLog) {
		setAssociation(PARAM_WEBLOG, webLog);
	}

	public void setOwner(User owner) {
		setAssociation(PARAM_OWNER, owner);
	}

	public String getComment() {
		return (String) get(PARAM_COMMENT);
	}

	public Date getModified() {
		return (Date) get(PARAM_MODIFIED);
	}

	public WebLog getWebLog() {
		DataObject entityData = (DataObject) get(PARAM_WEBLOG);
		if (entityData != null) {
			return new WebLog(entityData);
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
}
