package org.undp.weblog.ui;

import java.math.BigDecimal;

import org.undp.weblog.WebLog;
import org.undp.weblog.WebLogApplication;
import org.undp.weblog.util.GlobalizationUtil;

import com.arsdigita.bebop.BlockStylable;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.web.Web;

/**
 * @author Peter Kopunec
 */
public class WebLogEditForm extends Form implements FormInitListener,
		FormValidationListener, FormProcessListener {

	private static final String NAME_TITLE = "title";

	private static final String NAME_LEAD = "lead";

	private static final String NAME_BODY = "body";

	private final WebLogView m_parent;

	private Submit m_save;

	private final boolean m_createNew;

	public WebLogEditForm(WebLogView parent, boolean createNew) {
		super("webLogEditForm");
		m_parent = parent;
		m_createNew = createNew;

		setMethod("POST");

		add(new Label(GlobalizationUtil.localize("webLogEditForm.title")),
				BlockStylable.RIGHT | BlockStylable.TOP);
		add(new TextField(NAME_TITLE));

		add(new Label(GlobalizationUtil.localize("webLogEditForm.lead")),
				BlockStylable.RIGHT | BlockStylable.TOP);
		TextArea lead = new TextArea(NAME_LEAD);
		lead.setCols(80);
		lead.setRows(12);
		add(lead);

		add(new Label(GlobalizationUtil.localize("webLogEditForm.body")),
				BlockStylable.RIGHT | BlockStylable.TOP);
		DHTMLEditor body = new DHTMLEditor(NAME_BODY);
		body.setCols(80);
		body.setRows(20);
		add(body);

		add(new Label(""));
		SimpleContainer buttons = new SimpleContainer();
		m_save = new Submit(GlobalizationUtil.localize("saveButton"));
		buttons.add(m_save);
		buttons.add(new Submit(GlobalizationUtil.localize("cancelButton")));
		add(buttons);

		addInitListener(this);
		addValidationListener(this);
		addProcessListener(this);
	}

	public void init(FormSectionEvent e) throws FormProcessException {
		if (!m_createNew) {
			BigDecimal enityID = m_parent.getEntityID(e.getPageState());
			if (enityID != null) {
				WebLog entity = new WebLog(enityID);
				FormData data = e.getFormData();
				data.put(NAME_TITLE, entity.getTitle());
				data.put(NAME_LEAD, entity.getLead());
				data.put(NAME_BODY, entity.getBody());
			}
		}
	}

	public void validate(FormSectionEvent e) throws FormProcessException {
		if (m_save.isSelected(e.getPageState())) {
			validate(e.getFormData());
		}
	}

	private boolean validate(FormData data) {
		if (!m_parent.canUserAdminApplication()) {
			data.addError("Insufficient rights");
		}

		String title = (String) data.get(NAME_TITLE);
		if (title == null || title.length() == 0) {
			data.addError(NAME_TITLE, GlobalizationUtil
					.localize("error.parameterRequired"));
		} else {
			if (title.length() > 200) {
				data.addError(NAME_TITLE, GlobalizationUtil
						.localize("error.parameterSize200"));
			}
		}

		String lead = (String) data.get(NAME_LEAD);
		if (lead == null || lead.length() == 0) {
			data.addError(NAME_LEAD, GlobalizationUtil
					.localize("error.parameterRequired"));
		} else {
			if (lead.length() > 4000) {
				data.addError(NAME_LEAD, GlobalizationUtil
						.localize("error.parameterSize4000"));
			}
		}

		String body = (String) data.get(NAME_BODY);
		if (body == null || body.length() == 0) {
			data.addError(NAME_BODY, GlobalizationUtil
					.localize("error.parameterRequired"));
		}

		return data.isValid();
	}

	public void process(FormSectionEvent e) throws FormProcessException {
		PageState ps = e.getPageState();
		if (m_save.isSelected(ps)) {
			FormData data = e.getFormData();
			if (validate(data)) {
				String title = (String) data.get(NAME_TITLE);
				String lead = (String) data.get(NAME_LEAD);
				String body = (String) data.get(NAME_BODY);

				WebLog entity;
				if (m_createNew) {
					entity = new WebLog();
					entity.setApplication((WebLogApplication) Web.getWebContext()
							.getApplication());
					entity.setOwner(Web.getWebContext().getUser());
				} else {
					BigDecimal enityID = (BigDecimal) m_parent
							.getEntityIDParam().transformValue(
									e.getPageState().getRequest());
					entity = new WebLog(enityID);
				}
				entity.setTitle(title);
				entity.setLead(lead);
				entity.setBody(body);
				entity.save();
				processBack(ps);
			}
		} else {
			processBack(ps);
		}
	}

	protected WebLogView getParent() {
		return m_parent;
	}

	protected void processBack(PageState ps) {
		m_parent.displayWebLogsList(ps);
	}
}
