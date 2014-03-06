package org.undp.weblog.ui;

import java.math.BigDecimal;

import org.undp.weblog.WebLog;
import org.undp.weblog.WebLogComment;
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
import com.arsdigita.web.Web;

/**
 * @author Peter Kopunec
 */
public class WebLogCommentEditForm extends Form implements FormInitListener,
		FormValidationListener, FormProcessListener {

	private static final String NAME_COMMENT = "comment";

	private final WebLogView m_parent;

	private Submit m_save;

	private final boolean m_createNew;

	public WebLogCommentEditForm(WebLogView parent, boolean createNew) {
		super("webLogCommentEditForm");
		m_parent = parent;
		m_createNew = createNew;

		setMethod("POST");

		add(new Label(GlobalizationUtil
				.localize("webLogCommentEditForm.comment")),
				BlockStylable.RIGHT | BlockStylable.TOP);
		DHTMLEditor body = new DHTMLEditor(NAME_COMMENT);
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
			// BigDecimal enityID = (BigDecimal)
			// m_parent.getEntityIDParam().transformValue(e.getPageState().getRequest());
			BigDecimal enityID = m_parent.getEntityID(e.getPageState());
			if (enityID != null) {
				WebLogComment entity = new WebLogComment(enityID);
				FormData data = e.getFormData();
				data.put(NAME_COMMENT, entity.getComment());
			}
		}
	}

	public void validate(FormSectionEvent e) throws FormProcessException {
		if (m_save.isSelected(e.getPageState())) {
			validate(e.getFormData());
		}
	}

	private boolean validate(FormData data) {
		String comment = (String) data.get(NAME_COMMENT);
		if (comment == null || comment.length() == 0) {
			data.addError(NAME_COMMENT, GlobalizationUtil
					.localize("error.parameterRequired"));
		} else {
			if (comment.length() > 4000) {
				data.addError(NAME_COMMENT, GlobalizationUtil
						.localize("error.parameterSize4000"));
			}
		}

		return data.isValid();
	}

	public void process(FormSectionEvent e) throws FormProcessException {
		PageState ps = e.getPageState();
		if (m_save.isSelected(ps)) {
			FormData data = e.getFormData();
			if (validate(data)) {
				String comment = (String) data.get(NAME_COMMENT);

				WebLogComment entity;
				if (m_createNew) {
					entity = new WebLogComment();
					entity
							.setWebLog(new WebLog((BigDecimal) m_parent
									.getParentIDParam().transformValue(
											ps.getRequest())));
				} else {
					BigDecimal enityID = (BigDecimal) m_parent
							.getEntityIDParam().transformValue(
									e.getPageState().getRequest());
					entity = new WebLogComment(enityID);
				}

				entity.setOwner(Web.getWebContext().getUser());
				entity.setComment(comment);
				entity.save();
				processBack(ps);
			}
		} else {
			processBack(ps);
		}
	}

	protected void processBack(PageState ps) {
		m_parent.setEntityID(ps, (BigDecimal) m_parent.getParentIDParam()
				.transformValue(ps.getRequest()));
		m_parent.displayWebLogDetail(ps);
	}
}
