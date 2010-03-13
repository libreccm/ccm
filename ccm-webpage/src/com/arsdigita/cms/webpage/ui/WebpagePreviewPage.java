package com.arsdigita.cms.webpage.ui;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.dispatcher.ContentItemDispatcher;
import com.arsdigita.cms.dispatcher.ContentSectionDispatcher;
import com.arsdigita.cms.dispatcher.XMLGenerator;
//import com.arsdigita.cms.ui.ContentSectionComponent;
import com.arsdigita.cms.webpage.Webpage;
import com.arsdigita.ui.UIConstants;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

/**
 * @author Peter Kopunec
 */
public class WebpagePreviewPage extends CMSPage {
	
	public WebpagePreviewPage() {
		super("Story Preview", new SimpleContainer());
		
		setClassAttr("simplePage");
	}
	
	protected Element generateXMLHelper(PageState ps, Document parentDoc) {
		Element parent = super.generateXMLHelper(ps, parentDoc);
		
		Element uiSimplePageContent = parent.newChildElement("ui:simplePageContent", UIConstants.UI_XML_NS);
		
		Element content = uiSimplePageContent.newChildElement("cms:contentPanel", CMS.CMS_XML_NS);
		
		XMLGenerator xmlGenerator = getXMLGenerator(ps);
		xmlGenerator.generateXML(ps, content, null);
		
                // EE 20051125 - commented out reference to ContentSectionComponent
		//new ContentSectionComponent().generateXML(ps, uiSimplePageContent);
		
		return parent;
	}
	
	protected XMLGenerator getXMLGenerator(PageState ps) {
		HttpServletRequest request = ps.getRequest();
		ContentSection section = ContentSectionDispatcher.getContentSection(request);
		Assert.exists(section, ContentSection.class);
		return section.getXMLGenerator();
	}
	
	public ContentItem getContentItem(HttpServletRequest request) {
		ContentSection section = ContentSectionDispatcher.getContentSection(request);
		
		Webpage webpage = null;
		ContentItem item = ContentItemDispatcher.getContentItem(request);
		if (item != null && item instanceof Webpage) {
			webpage = (Webpage) item;
		}
		else {
			try {
				BigDecimal id = new BigDecimal(request.getParameter(Webpage.ID));
				webpage = new Webpage(id);
			}
			catch (Exception ex) {
			}
		}
		Assert.exists(webpage, Webpage.class);
		Assert.isTrue(webpage.getContentSection().equals(section), "content section doesn't match!");
		
		return webpage;
	}
}
