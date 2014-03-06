package com.arsdigita.cms.docmgr.ui;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

/**
 * @author Peter Kopunec
 */
public class FileDimensionalNavbar extends DimensionalNavbar implements DMConstants {
	
	private static final Logger s_log = Logger.getLogger(FileDimensionalNavbar.class);
	
	private final static String BEBOP_XML_NS = "http://www.arsdigita.com/bebop/1.0";
	
	private final RequestLocal m_file;
	
	public FileDimensionalNavbar(RequestLocal file) {
		m_file = file;
	}

    public void generateXML(PageState state, Element parent) {
		Element navbar = parent.newChildElement("bebop:dimensionalNavbar", BEBOP_XML_NS);
		navbar.addAttribute("startTag", "");
		navbar.addAttribute("endTag", "");
		navbar.addAttribute("delimiter", "");
		navbar.addAttribute("align", "right");
		exportAttributes(navbar);
		
		try {
	        ArrayList list = new ArrayList();
	        Link link;
	        Application rootApp = Web.getWebContext().getApplication();
	        Application app = rootApp;
	        while (app != null) {
	        	list.add(new Link(app.getTitle(), app.getPath()));

                // not very clean, but to avoid dependencies on ccm-ldn-portal or ccm-portalserver
	        	// if (app instanceof PortalSite) {
                // 	 break;
                // }
                if (app.getClass().getName().equals("com.arsdigita.portalserver.PortalSite")
                    || app.getClass().getName().equals("com.arsdigita.london.portal.Workspace")) {
                    break;
                }

	        	app = app.getParentApplication();
	        }
	        for (int i = list.size() -1; i >= 0; i--) {
	        	((Component) list.get(i)).generateXML(state, navbar);
	        }
	        
	        list.clear();
	        Document doc = (Document) m_file.get(state);
	        Object parentObj = ((ContentBundle) doc.getParent()).getParent();
	        DocFolder df;
	        while (parentObj != null && parentObj instanceof DocFolder && !(df = (DocFolder) parentObj).isRoot()) {
	        	df = (DocFolder) parentObj;
	        	link = new Link(df.getTitle(), rootApp.getPath());
	        	link.setVar(OPEN_FOLDER_ID_PARAM_NAME, df.getID().toString());
	        	list.add(link);
	        	parentObj = df.getParent();
	        }
	        for (int i = list.size() -1; i >= 0; i--) {
	        	((Component) list.get(i)).generateXML(state, navbar);
	        }
	        
	        (new Label(doc.getTitle())).generateXML(state, navbar);
		}
		catch (Exception e) {
			s_log.error("generateXML", e);
		}
	}
}
