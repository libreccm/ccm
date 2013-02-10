package com.arsdigita.portalworkspace.ui.admin;

import com.arsdigita.portalworkspace.ui.ApplicationSelectionModel;
import org.apache.log4j.Logger;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.portalworkspace.util.GlobalizationUtil;
import com.arsdigita.categorization.Category;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.RedirectSignal;

/**
 * {@link Component} providing an {@link ActionLink} to delete an
 * {@link Application}. Provides two constructors. The first takes just an
 * {@link ApplicationSelectionModel}. The second also takes an
 * {@link ApplicationType}. If the target Application is not of this type then
 * it will not be possible to delete the Application.
 * 
 * We also check that the Application is not the root Application and that it
 * has no children. If either of these conditions are false then it will not be
 * possible to delete the Application.
 * 
 * By default the ActionLink has a Label which reads "Permanently delete this
 * application". However you can use the setActionLink method to use your own
 * ActionLink.
 * 
 * By default, when the Application is deleted we return a RedirectSignal to
 * /ccm/portal/admin/sitemap.jsp Again this can be changed with the
 * setRedirectSignal method.
 * 
 * Methods exist to modify the messages displayed when it isn't possible to
 * delete an Application.
 * 
 * @author matt
 * 
 */

public class DeleteApplicationComponent extends SimpleContainer {

	private static final Logger s_log = Logger
			.getLogger(DeleteApplicationComponent.class);

	private ApplicationSelectionModel m_appModel;

	private ActionLink m_delete;

	private ApplicationType m_appType;

	private Label m_hasChildrenError = new Label(
			"Cannot delete an application that has children.");

	private Label m_rootApplicationError = new Label(
			"Cannot delete the root application.");

	private Label m_applicationTypeError = new Label(
			"Cannot delete this type of application");

	private Label m_linkLabel = new Label((String) GlobalizationUtil.globalize(
			"portal.ui.admin.delete_application").localize());

	private RedirectSignal m_redirect = new RedirectSignal(
			"/", true);

	private class ApplicationDeleteActionListener implements ActionListener {

		private ApplicationSelectionModel model;

		public ApplicationDeleteActionListener(
				ApplicationSelectionModel applicationSelectionModel) {
			model = applicationSelectionModel;
		}

		public void actionPerformed(final ActionEvent e) {
			PageState ps = e.getPageState();
			Application app = model.getSelectedApplication(ps);
			s_log.debug("Application title is " + app.getTitle());
			s_log.debug("number of ancestors for this app is "
					+ app.getAncestorApplications().size());
			s_log.debug("number of children is "
					+ app.getChildApplications().size());

			if (app.getAncestorApplications().size() <= 0) {
				m_rootApplicationError.setVisible(ps, true);
			} else if (!app.getChildApplications().isEmpty()) {
				m_hasChildrenError.setVisible(ps, true);
			} else if (!canDeleteApplicationType(app)) {
				m_applicationTypeError.setVisible(ps, true);
			} else {
				s_log.debug("clearing the root object");
				Category.clearRootForObject(app);
				s_log.debug("deleting the app");
				app.delete();
				ps.reset(getComponent());
				s_log.debug("redirecting");
				throw m_redirect;
			}
		}
	}

	private DeleteApplicationComponent getComponent() {
		return this;
	}

	private boolean canDeleteApplicationType(Application app) {
		if (m_appType == null) {
			return true;
		}
		return m_appType.getApplicationObjectType().equals(
				app.getApplicationType().getApplicationObjectType());
	}

	public void register(Page page) {
		super.register(page);
		page.setVisibleDefault(m_hasChildrenError, false);
		page.setVisibleDefault(m_rootApplicationError, false);
		page.setVisibleDefault(m_applicationTypeError, false);
	}

	/**
	 * Default constructor.
	 * 
	 * @param applicationSelectionModel
	 */
	public DeleteApplicationComponent(
			ApplicationSelectionModel applicationSelectionModel) {
		s_log.debug("DeleteApplicationComponent instantiated");

		setNamespace(WorkspacePage.PORTAL_XML_NS);
		setTag("portal:workspaceDelete");
		m_appModel = applicationSelectionModel;
		if (m_delete == null)
			m_delete = new ActionLink(m_linkLabel);
		m_delete.setClassAttr("actionLink");
		m_delete.addActionListener(new ApplicationDeleteActionListener(
				m_appModel));
		add(m_delete);
		add(m_rootApplicationError);
		add(m_hasChildrenError);
		add(m_applicationTypeError);
	}

	/**
	 * Allows you to specify an ApplicationType. If the Application is not of
	 * this type a delete link is not added to this Component.
	 * 
	 * @param applicationSelectionModel
	 * @param applicationType
	 */
	public DeleteApplicationComponent(
			ApplicationSelectionModel applicationSelectionModel,
			ApplicationType applicationType) {
		this(applicationSelectionModel);
		m_appType = applicationType;
	}

	/**
	 * Allows you to set the ActionLink used so that you can change the Label or
	 * any of the other properties of an ActionLink.
	 * 
	 * @param link
	 */
	public void setActionLink(ActionLink link) {
		m_delete = link;
	}

	/**
	 * Allows you to set the RedirectSignal which is returned after deleting the
	 * Application.
	 * 
	 * @param redirect
	 */
	public void setRedirectSignal(RedirectSignal redirect) {
		m_redirect = redirect;
	}

	/**
	 * Allows you to modify the Label used when it is not possible to delete an
	 * Application with children.
	 * 
	 * @param label
	 */
	public void setHasChildrenMessage(Label label) {
		m_hasChildrenError = label;
	}

	/**
	 * Allows you to modify the Label used when it is not possible to delete the
	 * root Application.
	 * 
	 * @param label
	 */
	public void setRootApplicationMessage(Label label) {
		m_rootApplicationError = label;
	}

	/**
	 * Allows you to modify the Label used when it is not possible to delete an
	 * Application because it is not of the correct ApplicationType.
	 * 
	 * @param label
	 */
	public void setApplicatinTypeMessage(Label label) {
		m_applicationTypeError = label;
	}

}
