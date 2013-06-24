package com.arsdigita.london.contenttypes.ui;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.london.contenttypes.Contact;
import com.arsdigita.cms.contenttypes.ui.ResettableContainer;
import com.arsdigita.london.contenttypes.util.ContactGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.workflow.WorkflowLockedContainer;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.util.Assert;

/** 
 *
 */
public class ContactPropertiesAddStep extends ResettableContainer {

	private static final Logger s_log = Logger
			.getLogger(ContactPropertiesAddStep.class);

	public static final String EDIT_SHEET_NAME = "edit";

	public Form m_form;

	private ItemSelectionModel m_itemSelectionModel;

	public ContactSelectionModel m_contactSelectionModel;

	private AuthoringKitWizard m_parent;

	protected Component m_contactPropertySheet;

	private ContactDisplay m_display;

	public ContactPropertiesAddStep(ItemSelectionModel itemModel,
			AuthoringKitWizard parent) {

		m_parent = parent;
		m_itemSelectionModel = itemModel;
		m_contactSelectionModel = new ContactSelectionModel(
				m_itemSelectionModel);

		Form display = new Form("display");
		display.add(getContactDisplay());
		add(display);

		m_form = new Form("contactSearchForm");
		m_form.add(getFindContactSheet());

		WorkflowLockedContainer edit = new WorkflowLockedContainer(itemModel);
		edit.add(m_form);
		add(edit);

	}

	protected Component getContactDisplay() {
		m_display = new ContactDisplay();
		return m_display;
	}

	public void toggleDisplay(PageState state) throws FormProcessException {
		m_display.toggle(state);
	}

	private Component getFindContactSheet() {
		return new ContactToItemAddForm(this);
	}

	public ItemSelectionModel getItemSelectionModel() {
		return m_itemSelectionModel;
	}

	public ItemSelectionModel getContactSelectionModel() {
		return m_contactSelectionModel;
	}

	public AuthoringKitWizard getParent() {
		return m_parent;
	}

	public ContentItem getItem(PageState state) {
		return m_itemSelectionModel.getSelectedItem(state);
	}

	public Contact getContact(PageState state) {
		return (Contact) m_contactSelectionModel.getSelectedItem(state);
	}

	protected class ContactSelectionModel extends ItemSelectionModel {

		private RequestLocal m_contact;

		public ContactSelectionModel(ItemSelectionModel m) {
			super(m);

			m_contact = new RequestLocal() {
                @Override
				protected Object initialValue(PageState s) {
					ContentItem item = (ContentItem) (
                            (ItemSelectionModel) getSingleSelectionModel())
							.getSelectedObject(s);
					Assert.exists(item);
					return Contact.getContactForItem(item);

				}
			};
		}

        @Override
		public Object getSelectedKey(PageState s) {
			Contact contact = (Contact) getSelectedObject(s);
			return (contact == null) ? null : contact.getID();
		}

        @Override
		public DomainObject getSelectedObject(PageState s) {
			return (DomainObject) m_contact.get(s);
		}

        @Override
		public void setSelectedObject(PageState s, DomainObject o) {
			m_contact.set(s, o);
		}

        @Override
		public void setSelectedKey(PageState s, Object key) {
			throw new UnsupportedOperationException((String) GlobalizationUtil
					.globalize("cms.ui.authoring.not_implemented").localize());
		}

        @Override
		public boolean isSelected(PageState s) {
			return (getSelectedObject(s) != null);
		}
	}

	private class ContactDisplay extends FormSection implements
			FormInitListener {

		private Label m_noContact;

		public ContactDisplay() {

			addInitListener(this);
			addWidgets();
		}

		public void toggle(PageState state) {
			s_log.debug("toggle");
			m_contactPropertySheet.setVisible(state, false);
			m_noContact.setVisible(state, true);

		}

		private void addWidgets() {
			m_contactPropertySheet = getContactPropertySheet(m_contactSelectionModel);
			m_noContact = new Label(ContactGlobalizationUtil.globalize(
                          "london.contenttypes.ui.contact.no_contacts_yet"));
			m_noContact.setFontWeight(Label.ITALIC);
			add(m_contactPropertySheet);
			add(m_noContact);
		}

		public void init(FormSectionEvent e) throws FormProcessException {
			s_log.debug("init");
			PageState state = e.getPageState();
			if (m_contactSelectionModel.getSelectedItem(state) != null) {
				m_contactPropertySheet.setVisible(state, true);
				m_noContact.setVisible(state, false);
			} else {
				m_noContact.setVisible(state, true);
				m_contactPropertySheet.setVisible(state, false);
			}

		}

		private Component getContactPropertySheet(ItemSelectionModel itemModel) {
			DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
					itemModel);
			sheet.add(ContactGlobalizationUtil.globalize(
                          "com.arsdigita.london.contenttypes.ui.name"), 
                      Contact.NAME);

			sheet.add(ContactGlobalizationUtil.globalize(
                          "com.arsdigita.london.contenttypes.ui.title"), 
                      Contact.TITLE);

			sheet.add(ContactGlobalizationUtil.globalize(
                          "com.arsdigita.london.contenttypes.ui.contact_givenname"),
				      Contact.GIVEN_NAME);

			sheet.add(ContactGlobalizationUtil.globalize(
                          "com.arsdigita.london.contenttypes.ui.contact_familyname"),
					  Contact.FAMILY_NAME);

			sheet.add(ContactGlobalizationUtil.globalize(
                          "com.arsdigita.london.contenttypes.ui.contact_type"),
					  Contact.CONTACT_TYPE,
					  new DomainObjectPropertySheet.AttributeFormatter() {
                          public String format(DomainObject item,
                                               String attribute, 
                                               PageState state) {
                              Contact contact = (Contact) item;
                              if (contact != null
                                  && contact.getContactType() != null) {
                                  return contact.getContactTypeName();
                              } else {
                                  return (String)GlobalizationUtil
                                                 .globalize("cms.ui.unknown")
                                                 .localize();
                              }
                          }
                      });

			sheet.add(ContactGlobalizationUtil.globalize(
                          "com.arsdigita.london.contenttypes.ui.description"),
					  Contact.DESCRIPTION);

			sheet.add(ContactGlobalizationUtil.globalize(
                          "com.arsdigita.london.contenttypes.ui.contact_emails"),
					  Contact.EMAILS);

			sheet.add(ContactGlobalizationUtil.globalize(
                          "com.arsdigita.london.contenttypes.ui.contact_suffix"),
					  Contact.SUFFIX);

			sheet.add(ContactGlobalizationUtil.globalize(
                          "com.arsdigita.london.contenttypes.ui.contact_orgname"),
					  Contact.ORG_NAME);

			sheet.add(ContactGlobalizationUtil.globalize(
                          "com.arsdigita.london.contenttypes.ui.contact_deptname"),
					  Contact.DEPT_NAME);

			sheet.add(ContactGlobalizationUtil.globalize(
                          "com.arsdigita.london.contenttypes.ui.contact_role"),
					  Contact.ROLE);
			return sheet;
		}

	}

}
