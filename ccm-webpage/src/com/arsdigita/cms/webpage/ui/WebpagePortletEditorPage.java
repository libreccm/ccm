package com.arsdigita.cms.webpage.ui;

import java.io.IOException;
import java.math.BigDecimal;
// import java.util.ArrayList;
// import java.util.Iterator;
import org.apache.log4j.Logger;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Hidden;
// import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
// import com.arsdigita.categorization.Category;
// import com.arsdigita.categorization.CategoryCollection;
//import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.webpage.Webpage;
import com.arsdigita.cms.webpage.WebpageConstants;
// import com.arsdigita.cms.webpage.installer.Initializer;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.portal.Portlet;
//import com.arsdigita.portalserver.CWPage;
//import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

public class WebpagePortletEditorPage extends CMSPage {

	public static final Logger s_log = Logger.getLogger(WebpagePortletEditorPage.class);

	public static final String PORTLET_ID = "portletID";
	public static final String CATEGORIES = "categories";

	public WebpagePortletEditorPage() {
		super("Webpage Editor", new SimpleContainer());

                // EE 20051125 - removed code referencing CWPage
		SimpleContainer header = new SimpleContainer(WebpageConstants.HEADER_ELEMENT, WebpageConstants.XML_NS);
		SimpleContainer body = new SimpleContainer(WebpageConstants.BODY_ELEMENT, WebpageConstants.XML_NS);
		SimpleContainer footer = new SimpleContainer(WebpageConstants.FOOTER_ELEMENT, WebpageConstants.XML_NS);

		WebpagePortletEditorForm editForm = new WebpagePortletEditorForm();
		
		DimensionalNavbar navbar = new DimensionalNavbar();
		navbar.setClassAttr("portalNavbar");
		navbar.add(new Link(new CurrentApplicationLinkPrinter(editForm)));
		header.add(navbar);
		
		body.add(editForm);

		add(header);
		add(body);
		add(footer);
	}

    @Override
	protected void buildPage() {
		super.buildPage();
		setClassAttr("portalserver");
	}

	public class WebpagePortletEditorForm extends Form implements FormInitListener, FormProcessListener {

		private Submit editSubmit1 = new Submit("Edit");
//		private Submit editSubmit2 = new Submit("Edit");
		
		public WebpagePortletEditorForm() {
			super("WebpagePortletEditorForm");
			addWidgets();

			addInitListener(this);
			addProcessListener(this);
		}

		public void addWidgets() {
			SimpleContainer buttons;
			add(new Label("Body:"));
			StringParameter bodyParam = new StringParameter(Webpage.BODY);
			CMSDHTMLEditor body = new CMSDHTMLEditor(bodyParam);
			body.setCols(80);
			body.setRows(20);
			add(body);

			buttons = new SimpleContainer();
			buttons.add(editSubmit1);
			buttons.add(new Label(" "));
			buttons.add(new Submit("Cancel"));
			add(buttons);
			add(new Label(" "));

			add(new Label("Title:"));
			add(new TextField(Webpage.TITLE));

			add(new Label(new AuthorLabelPrinter()));
			add(new TextField(Webpage.AUTHOR));

//			add(new Label("Categories"));
//			MultipleSelect catSelect = new MultipleSelect(CATEGORIES);
//			catSelect.setSize(5);
//			try {
//				ContentSection section = Initializer.getConfig().getWebpageSection();
//
//				catSelect.addPrintListener(new CategoriesPrintListener(section));
//			}
//			catch (java.util.TooManyListenersException tmex) {
//				throw new UncheckedWrapperException(tmex.getMessage());
//			}
//			add(catSelect);
//
//			add(new Label("Description:<br /><font size=-1>(this field is currently not displayed on the website)</font>", false));
//			CMSDHTMLEditor description = new CMSDHTMLEditor(Webpage.DESCRIPTION, CMSDHTMLEditor.m_configWithoutToolbar);
//			description.setCols(80);
//			description.setRows(20);
//			add(description);

			BigDecimalParameter portletIDParameter = new BigDecimalParameter(PORTLET_ID);
			add(new Hidden(portletIDParameter));

//			buttons = new SimpleContainer();
//			buttons.add(editSubmit2);
//			buttons.add(new Label(" "));
//			buttons.add(new Submit("Cancel"));
//			add(buttons);
//			add(new Label(" "));
		}

		public void init(FormSectionEvent e) throws FormProcessException {
			FormData data = e.getFormData();
			WebpagePortlet portlet = getWebpagePortlet(e.getPageState());
			Webpage webpage = portlet.getWebpage();

			data.put(Webpage.TITLE, webpage.getTitle());
			data.put(Webpage.AUTHOR, webpage.getAuthor());
//			data.put(Webpage.DESCRIPTION, webpage.getDescription());
			data.put(Webpage.BODY, webpage.getBody());

//			ArrayList assignedCats = new ArrayList();
//			Iterator i = webpage.getCategories();
//			while (i.hasNext()) {
//				String catID = ((Category) i.next()).getID().toString();
//				assignedCats.add(catID);
//			}
//			data.put(CATEGORIES, assignedCats.toArray());
		}

		public void process(FormSectionEvent e) throws FormProcessException {
			FormData data = e.getFormData();
			BigDecimal portletID = (BigDecimal) data.get(PORTLET_ID);
			WebpagePortlet portlet = getWebpagePortlet(e.getPageState(), portletID);
			PageState ps = e.getPageState();
			if (editSubmit1.isSelected(ps)/* || editSubmit2.isSelected(ps)*/) {
			Webpage webpage = portlet.getWebpage();
			portlet.setTitle((String) data.get(Webpage.TITLE));
//				portlet.setDescription((String) data.get(Webpage.DESCRIPTION));

			webpage.setTitle((String) data.get(Webpage.TITLE));
			webpage.setAuthor((String) data.get(Webpage.AUTHOR));
//				webpage.setDescription((String) data.get(Webpage.DESCRIPTION));
			webpage.setBody((String) data.get(Webpage.BODY));
//				webpage.setCategories((String[]) data.get(CATEGORIES));

			portlet.save();
			webpage.save();
			}

			Application site = (Application) portlet.getParentResource();
			try {
				DispatcherHelper.sendRedirect(ps.getRequest(), ps.getResponse(), site.getPath());
			}
			catch (IOException ex) {
				throw new FormProcessException(ex);
			}
		}

		private WebpagePortlet getWebpagePortlet(PageState ps) throws FormProcessException {
			String portletIDString = ps.getRequest().getParameter(PORTLET_ID);
			if (portletIDString == null) {
				throw new FormProcessException("Illegal portlet ID: " + portletIDString);
			}

			BigDecimal portletID = new BigDecimal(portletIDString);
			return getWebpagePortlet(ps, portletID);
		}

		protected WebpagePortlet getWebpagePortlet(PageState ps, BigDecimal portletID) throws FormProcessException {
			WebpagePortlet portlet = (WebpagePortlet) Portlet.retrievePortlet(portletID);
			if (portlet == null) {
				throw new FormProcessException("Illegal Webpage Portlet ID: " + portletID);
			}

			User user = Web.getContext().getUser();
			PermissionDescriptor perm = new PermissionDescriptor(PrivilegeDescriptor.EDIT,
					portlet.getParentResource(), user);
			if (!PermissionService.checkPermission(perm)) {
				throw new FormProcessException("You do not have permission to edit this item.");
			}
			return portlet;
		}
	}

	protected class CurrentApplicationLinkPrinter implements PrintListener {
		private final WebpagePortletEditorForm parentEditForm;
		
		public CurrentApplicationLinkPrinter(WebpagePortletEditorForm aParentEditForm) {
			parentEditForm = aParentEditForm;
}

		public void prepare(PrintEvent e) {
			Link link = (Link) e.getTarget();
			PageState pageState = e.getPageState();
			
			try {
				WebpagePortlet portlet = parentEditForm.getWebpagePortlet(pageState);
				Application app = (Application) portlet.getParentResource();
				
				link.setChild(new Label(app.getTitle()));
				link.setTarget(app.getPath());
			}
			catch (FormProcessException ex) {
				s_log.error("CurrentApplicationLinkPrinter.prepare", ex);
				throw new RuntimeException(ex.getMessage());
			}
		}
	}
}
