package com.arsdigita.cms.contenttypes.ui.contact;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Contact;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;

public class AddContactToItemForm extends FormSection implements
		FormInitListener, FormValidationListener, FormProcessListener,
		FormSubmissionListener {

	private ContentItem m_item;

	private ItemSearchWidget m_itemSearch;

	private SaveCancelSection m_saveCancelSection;

	private ItemSelectionModel m_itemSelectionModel;

	private final String CONTACT_SEARCH = "contact";

	private Contact m_contact;

	private Label m_searchFormLabel;

	private AddContactPropertiesStep m_step;

	private AuthoringKitWizard m_parent;

	private Label m_removeLinkText = new Label(GlobalizationUtil
			.globalize("Remove contact"));

	private ActionLink m_removeLink;

	public AddContactToItemForm(AddContactPropertiesStep step) {
		m_step = step;
		m_parent = m_step.getParent();

		m_itemSelectionModel = m_step.getItemSelectionModel();
		addInitListener(this);
		addWidgets();
		addSaveCancelSection();
		addProcessListener(this);
		addValidationListener(this);
		addSubmissionListener(this);

	}

	public void addWidgets() {

		m_removeLink = new ActionLink(m_removeLinkText);

		m_removeLink.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Contact contact = m_step.getContact(e.getPageState());
				if (contact != null) {
					PageState state = e.getPageState();
					contact.removeContentItem(m_step.getItem(state));
					m_step.m_contactSelectionModel.setSelectedObject(state,
							null);
					try {
						m_step.toggleDisplay(state);
					} catch (FormProcessException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		add(m_removeLink, ColumnPanel.FULL_WIDTH);

		m_searchFormLabel = new Label("Search for Contact:");
		add(m_searchFormLabel);
		m_itemSearch = new ItemSearchWidget(CONTACT_SEARCH,
				Contact.BASE_DATA_OBJECT_TYPE);
		add(m_itemSearch);
	}

	public AddContactToItemForm getThis() {
		return this;
	}

	/** Adds the saveCancelSection */
	public void addSaveCancelSection() {
		m_saveCancelSection = new SaveCancelSection();
		m_saveCancelSection.getSaveButton().setButtonLabel("Add");
		add(m_saveCancelSection, ColumnPanel.FULL_WIDTH);
	}

	protected ContentItem getContentItem(PageState s) {
		return (ContentItem) m_itemSelectionModel.getSelectedObject(s);
	}

	public void validate(FormSectionEvent e) throws FormProcessException {
		FormData data = e.getFormData();
		if (data.get(CONTACT_SEARCH) == null) {
			throw new FormProcessException("Contact selection is required.");
		}
	}

	public void process(FormSectionEvent e) throws FormProcessException {
		FormData data = e.getFormData();
		PageState ps = e.getPageState();
		m_contact = (Contact) data.get(CONTACT_SEARCH);
		m_contact.addContentItem(getContentItem(ps));
		init(e);
	}

	public void submitted(FormSectionEvent e) throws FormProcessException {
		if (m_saveCancelSection.getCancelButton().isSelected(e.getPageState())) {
			m_parent.reset(e.getPageState());
			throw new FormProcessException("cancelled");
		}
	}

	public void init(FormSectionEvent e) throws FormProcessException {
		PageState ps = e.getPageState();
		m_item = getContentItem(ps);
		m_contact = Contact.getContactForItem(m_item);
		if (m_contact != null) {
			m_removeLink.setVisible(ps, true);
			m_searchFormLabel.setVisible(ps, false);
			m_itemSearch.setVisible(ps, false);
			m_saveCancelSection.setVisible(ps, false);
		} else {
			m_removeLink.setVisible(ps, false);
			m_searchFormLabel.setVisible(ps, true);
			m_itemSearch.setVisible(ps, true);
			m_saveCancelSection.setVisible(ps, true);
		}
	}

}
