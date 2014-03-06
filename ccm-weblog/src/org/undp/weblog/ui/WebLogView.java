package org.undp.weblog.ui;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.undp.weblog.WebLog;
import org.undp.weblog.WebLogConstants;
import org.undp.weblog.util.GlobalizationUtil;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

/**
 * @author Peter Kopunec
 */
public class WebLogView extends ModalContainer implements WebLogConstants {

	private static final Logger s_log = Logger.getLogger(WebLogView.class);

	private GridPanel m_webLogsList;

	private ActionLink m_addTopic;

	private WebLogEditForm m_webLogCreateForm;

	private WebLogEditForm m_webLogEditForm;

	private WebLogEditForm m_webLogEditFormBack2detail;

	private WebLogDetailPane m_webLogDetail;

	private GridPanel m_webLogCommentCreateForm;

	private GridPanel m_webLogCommentEditForm;

	private final RequestLocal m_entityID = new RequestLocal();

	private final RequestLocal m_parentID = new RequestLocal();

	private final BigDecimalParameter m_entityIDparam;

	private final BigDecimalParameter m_parentIDparam;

	private final RequestLocal parentEntityRequest = new RequestLocal() {
		protected Object initialValue(PageState ps) {
			BigDecimal detailID = getParentID(ps);
			if (detailID == null) {
				detailID = (BigDecimal) getParentIDParam().transformValue(
						ps.getRequest());
			}
			return new WebLog(detailID);
		}
	};

	public WebLogView(BigDecimalParameter entityIDparam,
			BigDecimalParameter parentIDparam) {
		m_entityIDparam = entityIDparam;
		m_parentIDparam = parentIDparam;
		m_webLogsList = new GridPanel(1);
		m_webLogsList.add(new WebLogsList(this));
		m_addTopic = new ActionLink(GlobalizationUtil
				.localize("webLogView.addTopic"));
		m_addTopic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (canUserAdminApplication()) {
					PageState ps = e.getPageState();
					displayWebLogCreateForm(ps);
				}
			}
		});
		m_webLogsList.add(m_addTopic);
		add(m_webLogsList);
		setDefaultComponent(m_webLogsList);

		m_webLogCreateForm = new WebLogEditForm(this, true);
		add(m_webLogCreateForm);
		m_webLogEditForm = new WebLogEditForm(this, false);
		add(m_webLogEditForm);
		m_webLogEditFormBack2detail = new WebLogEditForm(this, false) {
			protected void processBack(PageState ps) {
				WebLogView parent = getParent();
				parent.setEntityID(ps, (BigDecimal) parent.getEntityIDParam()
						.transformValue(ps.getRequest()));
				parent.displayWebLogDetail(ps);
			}
		};
		add(m_webLogEditFormBack2detail);

		m_webLogDetail = new WebLogDetailPane(this);
		add(m_webLogDetail);

		m_webLogCommentCreateForm = new GridPanel(1);
		m_webLogCommentCreateForm.add(new WebLogDetail(parentEntityRequest));
		m_webLogCommentCreateForm.add(new WebLogCommentEditForm(this, true));
		add(m_webLogCommentCreateForm);

		m_webLogCommentEditForm = new GridPanel(1);
		m_webLogCommentEditForm.add(new WebLogDetail(parentEntityRequest));
		m_webLogCommentEditForm.add(new WebLogCommentEditForm(this, false));
		add(m_webLogCommentEditForm);
	}

	public void displayWebLogsList(PageState ps) {
		setVisibleComponent(ps, m_webLogsList);
	}

	public void displayWebLogDetail(PageState ps) {
		setVisibleComponent(ps, m_webLogDetail);
	}

	public void displayWebLogCreateForm(PageState ps) {
		setVisibleComponent(ps, m_webLogCreateForm);
	}

	public void displayWebLogEditForm(PageState ps) {
		displayWebLogEditForm(ps, false);
	}

	public void displayWebLogEditForm(PageState ps, boolean backToDetail) {
		if (backToDetail) {
			setVisibleComponent(ps, m_webLogEditFormBack2detail);
		} else {
			setVisibleComponent(ps, m_webLogEditForm);
		}
	}

	public void displayWebLogCommentCreateForm(PageState ps) {
		setVisibleComponent(ps, m_webLogCommentCreateForm);
	}

	public void displayWebLogCommentEditForm(PageState ps) {
		setVisibleComponent(ps, m_webLogCommentEditForm);
	}

	public void generateXML(PageState ps, Element p) {
		m_addTopic.setVisible(ps, canUserAdminApplication());// this is the
		// line that
		// sets up the
		// link

		HttpServletRequest request = ps.getRequest();
		try {
			BigDecimal detailID = new BigDecimal(request
					.getParameter(PARAM_WEBLOG_DETAIL_ID));
			setEntityID(ps, detailID);
			displayWebLogDetail(ps);
		} catch (Exception e) {
		}
		super.generateXML(ps, p);
	}

	protected BigDecimal getEntityID(PageState ps) {
		return (BigDecimal) m_entityID.get(ps);
	}

	protected void setEntityID(PageState ps, BigDecimal id) {
		ps.setValue(m_entityIDparam, id);
		m_entityID.set(ps, id);
	}

	protected BigDecimal getParentID(PageState ps) {
		return (BigDecimal) m_parentID.get(ps);
	}

	protected void setParentID(PageState ps, BigDecimal id) {
		ps.setValue(m_parentIDparam, id);
		m_parentID.set(ps, id);
	}

	protected BigDecimalParameter getEntityIDParam() {
		return m_entityIDparam;
	}

	protected BigDecimalParameter getParentIDParam() {
		return m_parentIDparam;
	}

	protected boolean canUserAdminApplication() {
		Application app = Web.getWebContext().getApplication();
		User user = Web.getWebContext().getUser();
		PermissionDescriptor perm = new PermissionDescriptor(
				PrivilegeDescriptor.ADMIN, app, user);
		boolean result = PermissionService.checkPermission(perm);
		s_log.debug("canUserAdminApplication result is " + result);
		return result;
	}

	protected boolean canUserEditApplication() {
		PermissionDescriptor perm = new PermissionDescriptor(
				PrivilegeDescriptor.EDIT, Web.getWebContext().getApplication(),
				Web.getWebContext().getUser());
		return PermissionService.checkPermission(perm);
	}
}
