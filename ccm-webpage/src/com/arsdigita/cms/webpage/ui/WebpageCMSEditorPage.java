package com.arsdigita.cms.webpage.ui;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.MultipleSelect;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import static com.arsdigita.bebop.util.PanelConstraints.RIGHT;
import static com.arsdigita.bebop.util.PanelConstraints.TOP;
// import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionServlet;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleDefinitionCollection;
import com.arsdigita.cms.lifecycle.Phase;
import com.arsdigita.cms.lifecycle.PhaseCollection;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.cms.webpage.Webpage;
import com.arsdigita.cms.webpage.WebpageConstants;
import com.arsdigita.cms.workflow.CMSTask;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.Task;
import com.arsdigita.workflow.simple.TaskCollection;
import com.arsdigita.workflow.simple.Workflow;
import com.arsdigita.workflow.simple.WorkflowTemplate;
import com.arsdigita.xml.Document;

/**
 * @author Peter Kopunec
 */
public class WebpageCMSEditorPage extends CMSPage implements WebpageConstants {
	
	private static final Logger s_log = Logger.getLogger(WebpageCMSEditorPage.class);
	
	private WebpageCMSEditorForm m_createForm;
	private WebpageCMSEditorForm m_editForm;
	
	public WebpageCMSEditorPage() {
		super("Story Editor", new SimpleContainer());
		
		SimpleContainer header = new SimpleContainer(HEADER_ELEMENT, XML_NS);
		SimpleContainer body = new SimpleContainer(BODY_ELEMENT, XML_NS);
		SimpleContainer footer = new SimpleContainer(FOOTER_ELEMENT, XML_NS);
		
		m_createForm = new WebpageCMSEditorForm(true);
		m_editForm = new WebpageCMSEditorForm(false);
		body.add(m_createForm);
		body.add(m_editForm);
		
		add(header);
		add(body);
		add(footer);
	}
	
	protected void buildPage() {
		super.buildPage();
		setClassAttr("webpage");
	}
	
	public void generateXML(PageState state, Document parent) {
		BigDecimal id = null;
		try {
			HttpServletRequest request = state.getRequest();
			id = new BigDecimal(request.getParameter(Webpage.ID));
		}
		catch (Exception ex) {
		}
		m_createForm.setVisible(state, id == null);
		m_editForm.setVisible(state, id != null);
		super.generateXML(state, parent);
	}
	
	public class WebpageCMSEditorForm extends Form implements FormInitListener, FormValidationListener, FormProcessListener {
		
		Hidden m_itemID;
		TextField m_author, m_title;
		MultipleSelect m_categories;
		CMSDHTMLEditor m_body;
		CMSDHTMLEditor m_desc;
		Submit m_save;
		Date m_startDate;
		TextField m_startHour;
		TextField m_startMinute;
		SingleSelect m_startAmpm;
		Date m_endDate;
		TextField m_endHour;
		TextField m_endMinute;
		SingleSelect m_endAmpm;
		boolean m_displayDates;
		
		public WebpageCMSEditorForm(boolean displayDates) {
			super("WebpageCMSEditorForm");
			
			m_displayDates = displayDates;
			
			m_title = new TextField(new StringParameter("title"));
			m_title.setSize(35);
			add(new Label("Title:"), RIGHT | TOP);
			add(m_title);

			m_author = new TextField(Webpage.AUTHOR);
			add(new Label(new AuthorLabelPrinter()), RIGHT | TOP);
			add(m_author);

			m_desc = new CMSDHTMLEditor("desc");//CMSDHTMLEditor.m_configWithoutToolbar
			m_desc.setRows(10);
			m_desc.setCols(80);

			add(new Label("Lead:", false), RIGHT | TOP);//<br /><font size=-1>(this field is currently not displayed on the website)</font>
			add(m_desc);
			
			add(new Label("Location:"), RIGHT | TOP);
			m_categories = new MultipleSelect("categories");
			m_categories.setSize(5);
			add(m_categories);

			add(new Label("Body:"), RIGHT | TOP);
			StringParameter bodyParam = new StringParameter(Webpage.BODY);
			m_body = new CMSDHTMLEditor(bodyParam);
			m_body.setCols(80);
			m_body.setRows(20);
			add(m_body);
			
			if (displayDates) {
				add(new Label("Start date:"), RIGHT | TOP);
				m_startDate = new Date(new DateParameter("start_date") {
					protected final Calendar getCalendar(final HttpServletRequest sreq) {
						final Calendar cal = super.getCalendar(sreq);
						cal.setLenient(false);
						return cal;
					}
				});
				add(m_startDate);
				
				add(new Label("Start time:"), RIGHT | TOP);
				BoxPanel startTime = new BoxPanel(BoxPanel.HORIZONTAL);
				m_startHour = new TextField(new IntegerParameter("start_hour"));
				m_startHour.setSize(3);
				startTime.add(m_startHour);
				m_startMinute = new TextField(new IntegerParameter("start_minute"));
				m_startMinute.setSize(3);
				startTime.add(m_startMinute);
				m_startAmpm = new SingleSelect(new IntegerParameter("start_ampm"));
				m_startAmpm.addOption(new Option("0", "am"));
				m_startAmpm.addOption(new Option("1", "pm"));
				startTime.add(m_startAmpm);
				startTime.add(new Label(new TimeZonePrinter()));
				add(startTime);
				
				add(new Label("End date:"), RIGHT | TOP);
				m_endDate = new Date(new DateParameter("end_date") {
					protected final Calendar getCalendar(final HttpServletRequest sreq) {
						final Calendar cal = super.getCalendar(sreq);
						cal.setLenient(false);
						return cal;
					}
				});
				add(m_endDate);

				add(new Label("End time:"), RIGHT | TOP);
				BoxPanel endTime = new BoxPanel(BoxPanel.HORIZONTAL);
				m_endHour = new TextField(new IntegerParameter("end_hour"));
				m_endHour.setSize(3);
				endTime.add(m_endHour);
				m_endMinute = new TextField(new IntegerParameter("end_minute"));
				m_endMinute.setSize(3);
				endTime.add(m_endMinute);
				m_endAmpm = new SingleSelect(new IntegerParameter("end_ampm"));
				m_endAmpm.addOption(new Option("0", "am"));
				m_endAmpm.addOption(new Option("1", "pm"));
				endTime.add(m_endAmpm);
				endTime.add(new Label(new TimeZonePrinter()));
				add(endTime);
			}
			
			m_itemID = new Hidden(Webpage.ID);
			add(m_itemID);
			
			SimpleContainer buttons = new SimpleContainer();
			m_save = new Submit("Save");
			buttons.add(m_save);
			buttons.add(new Label(""));
			buttons.add(new Submit("Cancel"));
			
			add(new Label(""));
			add(buttons);
			
			addInitListener(this);
			addValidationListener(this);
			addProcessListener(this);
		}
		
		public void init(FormSectionEvent e) throws FormProcessException {
			s_log.debug("init");
			PageState state = e.getPageState();
			HttpServletRequest request = state.getRequest();
        //  ContentSection section = ContentSectionDispatcher.getContentSection(request);
            ContentSection section = ContentSectionServlet.getContentSection(request);
			
			Webpage webpage = null;
			try {
				BigDecimal id = new BigDecimal(request.getParameter(Webpage.ID));
				webpage = new Webpage(id);
				if (!webpage.getContentSection().equals(section)) {
					s_log.error("content section doesn't match! create new webpage entity.");
					webpage = null;
				}
			}
			catch (Exception ex) {
			}
			
			m_categories.setPrintListener(new CategoriesPrintListener(section));
			
			if (webpage != null) {
				m_itemID.setValue(state, webpage.getID().toString());
				m_author.setValue(state, webpage.getAuthor());
				m_title.setValue(state, webpage.getTitle());
				m_desc.setValue(state, webpage.getDescription());
				m_body.setValue(state, webpage.getBody());
				
				ArrayList assignedCats = new ArrayList();
                                CategoryCollection cc = webpage.getCategoryCollection();
				while (cc.next()) {
					String catID = cc.getCategory().getID().toString();
					assignedCats.add(catID);
				}
				m_categories.setValue(state, assignedCats.toArray());
			}
			else {
				m_categories.setValue(state, new String[]{request.getParameter("categoryID")});
				if (m_displayDates) {
					final java.util.Date start = new java.util.Date(System.currentTimeMillis());
					m_startDate.setValue(state, start);
					
					final Calendar calendar = Calendar.getInstance();
					calendar.setTime(start);
					// If the hour is 12, then Calendar.get(Calendar.HOUR)
					// returns 0 (from the 24 hour time - 12). We want it to
					// return 12.
					if (calendar.get(Calendar.HOUR) == 0) {
						m_startHour.setValue(state, new Integer(12));
					}
					else {
						m_startHour.setValue(state, new Integer(calendar.get(Calendar.HOUR)));
					}
					final Integer min = new Integer(calendar.get(Calendar.MINUTE));
					if (min.intValue() < 10) {
						m_startMinute.setValue(state, "0" + min.toString());
					}
					else {
						m_startMinute.setValue(state, min.toString());
					}
					m_startAmpm.setValue(state, new Integer(calendar.get(Calendar.AM_PM)));
				}
			}
		}
		
		public void validate(FormSectionEvent e) throws FormProcessException {
			s_log.debug("validate");
			PageState state = e.getPageState();
			boolean valid = true;
			if (m_save.isSelected(state)) {
				String title = (String) m_title.getValue(state);
				if (title == null || title.length() == 0) {
					m_title.addError("This parameter is required");
					valid = false;
				}
				if (title != null && (title.length() < 1 || title.length() > 200)) {
					m_title.addError("This parameter is not between 1 and 200 characters long");
					valid = false;
				}
				String desc = (String) m_desc.getValue(state);
				if (desc != null && (desc.length() < 1 || desc.length() > 4000)) {
					m_desc.addError("This parameter is not between 1 and 4000 characters long");
					valid = false;
				}
				if (m_displayDates) {
					Integer startHour = (Integer) m_startHour.getValue(state);
					if (startHour != null && (startHour.intValue() < 1 || startHour.intValue() > 12)) {
						m_startHour.addError("This parameter is not between 1 and 12");
						valid = false;
					}
					Integer startMin = (Integer) m_startMinute.getValue(state);
					if (startMin != null && (startMin.intValue() < 0 || startMin.intValue() > 59)) {
						m_startMinute.addError("This parameter is not between 0 and 59");
						valid = false;
					}
					Integer endHour = (Integer) m_endHour.getValue(state);
					if (endHour != null && (endHour.intValue() < 1 || endHour.intValue() > 12)) {
						m_endHour.addError("This parameter is not between 1 and 12");
						valid = false;
					}
					Integer endMin = (Integer) m_endMinute.getValue(state);
					if (endMin != null && (endMin.intValue() < 0 || endMin.intValue() > 59)) {
						m_endMinute.addError("This parameter is not between 0 and 59");
						valid = false;
					}
				}
			}
			if (! valid) {
				throw new FormProcessException("There is an error on the form");
			}
		}
		
		public void process(FormSectionEvent e) throws FormProcessException {
			s_log.debug("process");
			PageState state = e.getPageState();
			HttpServletRequest request = state.getRequest();
         // ContentSection section = ContentSectionDispatcher.getContentSection(request);
            ContentSection section = ContentSectionServlet.getContentSection(request);
			
			if (m_save.isSelected(state)) {
				Webpage webpage = null;
				
				boolean commitTransaction = false;
				boolean isMyTransaction = false;
				TransactionContext txn = null;
				try {
					Session session = SessionManager.getSession();
					txn = session.getTransactionContext();
					if (!txn.inTxn()) {
						txn.beginTxn();
						isMyTransaction = true;
					}
					try {
						String idTxt = (String) m_itemID.getValue(state);
						if (idTxt != null && idTxt.length() > 0) {
							BigDecimal id = new BigDecimal(idTxt);
							webpage = new Webpage(id);
						}
					}
					catch (Exception ex) {
						throw new FormProcessException(ex);
					}
					
					if (section == null) {
						throw new FormProcessException("ContentSection is null");
					}
					SecurityManager sm = new SecurityManager(section);
					User user = Web.getWebContext().getUser();
					
					if (webpage != null) {
						if ( !sm.canAccess(user, SecurityConstants.EDIT_ITEM, webpage ) ) {
							throw new FormProcessException("Insufficient permissions");
						}
						if (!webpage.getContentSection().equals(section)) {
							throw new FormProcessException("content section doesn't match!");
						}
						webpage.setTitle((String) m_title.getValue(state));
						webpage.setName("webpage" + webpage.getID());
						webpage.setBody((String) m_body.getValue(state));
						webpage.setAuthor((String) m_author.getValue(state));
						webpage.setCategories((String[]) m_categories.getValue(state));
						webpage.setDescription((String) m_desc.getValue(state));
						webpage.save();
					}
					else {
						if ( !sm.canAccess(user, SecurityConstants.NEW_ITEM ) ) {
							throw new FormProcessException("Insufficient permissions");
						}
						webpage = new Webpage();
						webpage.setLanguage("en");
						webpage.setName("webpage" + webpage.getID());
						webpage.setTitle((String) m_title.getValue(state));
						webpage.setBody((String) m_body.getValue(state));
						webpage.setAuthor((String) m_author.getValue(state));
						webpage.setDescription((String) m_desc.getValue(state));
						webpage.save();
						
						ContentBundle bundle = new ContentBundle(webpage);
						bundle.setContentSection(section);
						bundle.setParent(section.getRootFolder());
						webpage.setCategories((String[]) m_categories.getValue(state));
						
						// get lifecycleDefinition
						LifecycleDefinitionCollection lfColl = null;
						LifecycleDefinition lifecycleDefinition = null;
						try {
							lfColl = section.getLifecycleDefinitions();
							if (lfColl.next()) {
								lifecycleDefinition = lfColl.getLifecycleDefinition();
							}
						}
						finally {
							if (lfColl != null) {
								lfColl.close();
							}
						}
						
						// set workflow
						TaskCollection taskColl = null;
						try {
							taskColl = section.getWorkflowTemplates();
							if (taskColl.next()) {
								Task task = taskColl.getTask();
								final WorkflowTemplate wfTemp = new WorkflowTemplate(task.getID());
								final Workflow flow = wfTemp.instantiateNewWorkflow();
				                flow.setObjectID(webpage.getID());
				                flow.start(Web.getWebContext().getUser());
				                flow.save();
							}
						}
						finally {
							if (taskColl != null) {
								taskColl.close();
							}
						}
						
						// finish all task
						Workflow workflow = Workflow.getObjectWorkflow(webpage);
						
						Engine engine = Engine.getInstance();
						Assert.exists(engine, Engine.class);
						Iterator i = engine.getEnabledTasks(Web.getWebContext().getUser(), workflow.getID()).iterator();
						CMSTask task;
						do {
							while (i.hasNext()) {
								task = (CMSTask) i.next();
								try {
									task.finish();
								}
								catch (Exception ex) {
									throw new FormProcessException(ex);
								}
							}
							
							i = engine.getEnabledTasks(Web.getWebContext().getUser(), workflow.getID()).iterator();
						}
						while (i.hasNext());
						
						// publish
						if (lifecycleDefinition != null) {
							if (m_displayDates) {
								final Integer startHour = (Integer) m_startHour.getValue(state);
								Integer startMinute = (Integer) m_startMinute.getValue(state);
								if (startMinute == null) {
									startMinute = new Integer(0);
								}
								final Integer startAmpm = (Integer) m_startAmpm.getValue(state);
								final Integer endHour = (Integer) m_endHour.getValue(state);
								Integer endMinute = (Integer) m_endMinute.getValue(state);
								if (endMinute == null) {
									endMinute = new Integer(0);
								}
								final Integer endAmpm = (Integer) m_endAmpm.getValue(state);
								
								java.util.Date startDate = (java.util.Date) m_startDate.getValue(state);
								final Calendar start = Calendar.getInstance();
								start.setTime(startDate);
								start.set(Calendar.AM_PM, startAmpm.intValue());
								start.set(Calendar.MINUTE, startMinute.intValue());
								start.set(Calendar.AM_PM, startAmpm.intValue());
								if (startHour.intValue() != 12) {
									start.set(Calendar.HOUR_OF_DAY, 12 * startAmpm.intValue() + startHour.intValue());
									start.set(Calendar.HOUR, startHour.intValue());
								}
								else {
									if (startAmpm.intValue() == 0) {
										start.set(Calendar.HOUR_OF_DAY, 0);
										start.set(Calendar.HOUR, 0);
									}
									else {
										start.set(Calendar.HOUR_OF_DAY, 12);
										start.set(Calendar.HOUR, 0);
									}
								}
								startDate = start.getTime();
								
								java.util.Date endDate = (java.util.Date) m_endDate.getValue(state);
								if (endDate != null) {
									final Calendar end = Calendar.getInstance();
									end.setTime(endDate);
									end.set(Calendar.AM_PM, endAmpm.intValue());
									end.set(Calendar.MINUTE, endMinute.intValue());
									end.set(Calendar.AM_PM, endAmpm.intValue());
									if (endHour.intValue() != 12) {
										end.set(Calendar.HOUR_OF_DAY, 12 * endAmpm.intValue() + endHour.intValue());
										end.set(Calendar.HOUR, endHour.intValue());
									}
									else {
										if (endAmpm.intValue() == 0) {
											end.set(Calendar.HOUR_OF_DAY, 0);
											end.set(Calendar.HOUR, 0);
										}
										else {
											end.set(Calendar.HOUR_OF_DAY, 12);
											end.set(Calendar.HOUR, 0);
										}
									}
									endDate = end.getTime();
								}
								
								webpage.publish(lifecycleDefinition, startDate);
								
								if (endDate != null) {
									final Lifecycle lifecycle = webpage.getLifecycle();
									
									// update individual phases
									final PhaseCollection phases = lifecycle.getPhases();
									
									while (phases.next()) {
										final Phase phase = phases.getPhase();
										java.util.Date thisEnd = phase.getEndDate();
										if (thisEnd == null || thisEnd.compareTo(endDate) > 0) {
											phase.setEndDate(endDate);
											phase.save();
										}
									}
								}
							}
							else {
								webpage.publish(lifecycleDefinition, new java.util.Date());
							}
							
							// Force the lifecycle scheduler to run to avoid any
							// scheduler delay for items that should be published
							// immediately.
							webpage.getLifecycle().start();
							
							webpage.save();
						}
					}
					commitTransaction = true;
				}
				finally {
					if (txn != null && isMyTransaction) {
						if (commitTransaction) {
							txn.commitTxn();
						}
						else {
							txn.abortTxn();
						}
					}
				}
			}
			
			try {
				DispatcherHelper.sendRedirect(request, state.getResponse(), section.getPath());
			}
			catch (IOException ex) {
				throw new FormProcessException(ex);
			}
		}
		
	    public class TimeZonePrinter implements PrintListener {
			public void prepare(PrintEvent e) {
				final Label target = (Label) e.getTarget();
				final PageState state = e.getPageState();
				final Calendar mStart = Calendar.getInstance();
				java.util.Date st = (java.util.Date) m_startDate.getValue(state);

				if (st != null) {
					mStart.setTime((java.util.Date) m_startDate.getValue(state));
				}

				final String zone = mStart.getTimeZone().getDisplayName(true, TimeZone.SHORT);

				target.setLabel(zone);
			}
		}
	}
}
