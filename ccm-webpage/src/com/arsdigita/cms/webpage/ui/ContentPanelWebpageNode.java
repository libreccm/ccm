package com.arsdigita.cms.webpage.ui;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.ContentPanel;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.cms.webpage.Webpage;
import com.arsdigita.cms.webpage.WebpageConstants;
import com.arsdigita.kernel.User;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

/**
 * @author Peter Kopunec
 */
public class ContentPanelWebpageNode extends ContentPanel {
	
	private static final Logger s_log = Logger.getLogger(ContentPanelWebpageNode.class);
	
	public ContentPanelWebpageNode() {
		super();
	}
	
	/**
	 * Generates XML that represents a content item.
	 *
	 * @param state The page state
	 * @param parent The parent DOM element
	 * @see com.arsdigita.cms.dispatcher.XMLGenerator
	 */
	public void generateXML(PageState state, Element parent) {
		ContentSection section = CMS.getContext().getContentSection();
		Assert.exists(section, ContentSection.class);
		if (isVisible(state)) {
			super.generateXML(state, parent);

			User user = Web.getWebContext().getUser();
			SecurityManager sm = new SecurityManager(section);
			if (user != null && sm != null) {
				modifyContentPanelElements(parent.getChildren(), section, user, sm);
			}
		}
	}
	
	private void modifyContentPanelElements(List elements, ContentSection section, User user, SecurityManager sm) {
		s_log.debug("modifyContentPanelElements - webpage");
		Element element;
		for (int i = 0; elements != null && i < elements.size(); i++) {
			element = (Element) elements.get(i);
			if ("cms:contentPanel".equals(element.getName())) {
				modifyItemElements(element.getChildren(), section, user, sm);
			}
		}
	}
	
	private void modifyItemElements(List elements, ContentSection section, User user, SecurityManager sm) {
		Element element;
		for (int i = 0; elements != null && i < elements.size(); i++) {
			element = (Element) elements.get(i);
			if ("cms:item".equals(element.getName())) {
				if (Webpage.BASE_DATA_OBJECT_TYPE.equals(getTextOfElement(element.getChildren(), Webpage.OBJECT_TYPE))) {
					s_log.debug("found webpage");
					BigDecimal id = null;
					try {
						id = new BigDecimal(getTextOfElement(element.getChildren(), Webpage.ID));
					}
					catch (Exception e) {
						s_log.error("modifyItemElements", e);
						continue;
					}
					String sectionURL = section.getURL();
					StringBuffer onClick;
					Webpage webpage = new Webpage(id);
					if (sm.canAccess(user, SecurityConstants.EDIT_ITEM, webpage)) {
						Element newWebpageElement = element.newChildElement("webpage:button", WebpageConstants.XML_NS);
						newWebpageElement.addAttribute("name", "_ew");
						newWebpageElement.addAttribute("value", "Edit webpage");
						onClick = new StringBuffer();
						onClick.append("location.href='").append(sectionURL);
						if (!sectionURL.endsWith("/")) {
							onClick.append("/");
						}
						onClick.append("cmsWebpageEdit.jsp?");
						onClick.append(Webpage.ID).append("=").append(id.toString());
						onClick.append("';");
						newWebpageElement.addAttribute("onClick", onClick.toString());
					}
					
					Element previewWebpageElement = element.newChildElement("webpage:button", WebpageConstants.XML_NS);
					previewWebpageElement.addAttribute("name", "_pw");
					previewWebpageElement.addAttribute("value", "Preview webpage");
					onClick = new StringBuffer();
					onClick.append("location.href='").append(sectionURL);
					if (!sectionURL.endsWith("/")) {
						onClick.append("/");
					}
					onClick.append("cmsWebpagePreview.jsp?");
					onClick.append(Webpage.ID).append("=").append(id.toString());
					onClick.append("';");
					previewWebpageElement.addAttribute("onClick", onClick.toString());
				}
			}
		}
	}
	
	private String getTextOfElement(List elements, String elementName) {
		if (elements != null && elementName != null) {
			Element element;
			for (int i = 0; i < elements.size(); i++) {
				element = (Element) elements.get(i);
				if (elementName.equals(element.getName())) {
					return element.getText();
				}
			}
		}
		return null;
	}
}