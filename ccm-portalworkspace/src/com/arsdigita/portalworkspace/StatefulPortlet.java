/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.arsdigita.portalworkspace;

import java.util.HashMap;
import java.util.Map;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.portal.Portlet;
import com.arsdigita.xml.Element;

/**
 * In order to register stateful components with the page, stateful portlets 
 * must have the ability to supply renderers that are not tied to any particular
 * portlet data object.
 * 
 * This abstract class provides a means of registering a 
 * StatefulPortletRendererFactory that is used  when the page is being built,
 * and provides a final implementation of doGetPortletRenderer that is used
 * by a portal page in edit mode.
 * 
 * The framework allows the empty renderers that are registered to a page to be 
 * populated via RequestLocals with actual Portlet data objects ready for
 * rendering
 * 
 * To create a stateful portlet:- 
 * 
 * implement a StatefulPortletRendererFactory 
 * 
 * eg 
 * 
 * <pre>
 *  
 * public class StatefulExamplePortletRendererFactory
 *        implements StatefulPortletRendererFactory {
 *
 *	public StatefulPortletRenderer getRenderer() {
 *		return new StatefulExamplePortletRenderer();
 *	}
 * *	
 * </pre>
 * 
 * register this during initialisation
 * 
 * <pre>
 * 
 * public void init(DomainInitEvent e) {
 *		super.init(e);
 *
 *		StatefulPortlet.registerRendererFactory(
 *                          StatefulExamplePortlet.BASE_DATA_OBJECT_TYPE,
 *                          new StatefulExamplePortletRendererFactory());
 *		
 * 
 * ensure that your portlet class extends StatefulPortlet, but apart from that
 * treat is as any other domain object
 * <pre>
 *  
 *
 * 
 * public class StatefulExamplePortlet extends StatefulPortlet {
 *
 *
 *	public String getLabel1 () {
 *		return (String) get ("label1");
 *	}
 *	
 *	public String getLabel2 () {
 *		return (String) get ("label2);
 * }
 * </pre>
 *
 *
 * In your statefulPortletRenderer, you can create any hierarchy of stateful 
 * components (bebop or otherwise) and pass then a reference to the 
 * requestLocal called portlet defined in StatefulPortletRenderer
 * 
 * During rendering, this requestLocal holds a portlet data object
 * which can be interrogated as normal once cast to your 
 * StatefulPortlet type
 *  
 * 
 * 
 * <pre>
 * public class StatefulExamplePortletRenderer
 *	extends StatefulPortletRenderer {
 *
 *	private Label label1;
 *	private Label label2;
 *  private ActionLink link;
 *
 *	public StatefulExamplePortletRenderer() {
 *
 *		label1 = new Label("label 1");
 *		label1.addPrintListener (new PrintListener() {
 *			public void prepare(PrintEvent event) {
 *				PageState state = event.getState();
 *				StatefulExamplePortlet thisPortlet =
 *                                     (StatefulExamplePortlet)portlet.get(state);
 *				Label label = event.getTarget();
 *				label.setLabel(thisPortlet.getLabel1());
 *			}
 *
 *		});
 *		
 *		
 *		label2 = new Label("label 2");
 *		label2.addPrintListener (new PrintListener() {
 *			public void prepare(PrintEvent event) {
 *				PageState state = event.getState();
 *				StatefulExamplePortlet thisPortlet =
 *                                     (StatefulExamplePortlet)portlet.get(state);
 *				Label label = event.getTarget();
 *				label.setLabel(thisPortlet.getLabel2());
 *			} 
 *
 *		});
 *		action = new ActionLink("press me");
 *		action.addActionListener(new ActionListener() {  
 *
 *			public void actionPerformed(ActionEvent event) {
 *				PageState state = event.getPageState();
 *				if (label1.isVisible(state)) {
 *					label1.setVisible(state, false);
 *					label2.setVisible(state, true);
 *				} else {
 *					label1.setVisible(state, true);
 *					label2.setVisible(state, false);
 *				}
 *			}
 *		});
 *		
 *		add(label1);
 *		add(label2);
 *		add(action);
 *	}
 *	
 *	public void register(Page p) {
 *			super.register(p);
 *			p.setVisibleDefault(label1, true);
 *			p.setVisibleDefault(label2, false);
 *			
 *		}	
 *
 *	}
 *</pre> 
 *
 *
 * @author cgyg9330 &lt;chris.gilbert@westsussex.gov.uk&gt
 * @version $Id: StatefulPortlet.java 1271 2006-07-18 13:36:43Z cgyg9330 $
 */
public abstract class StatefulPortlet extends Portlet {

	private static Map rendererFactories = new HashMap();

	protected StatefulPortlet(DataObject dataObject) {
		super(dataObject);
	}

	public static void registerRendererFactory(
                                     String portletType,
                                     StatefulPortletRendererFactory factory) {
		rendererFactories.put(portletType, factory);
	}
	
	public static StatefulPortletRendererFactory getRendererFactory(
                                                     String portletType) {
		return (StatefulPortletRendererFactory)rendererFactories.get(portletType);
	}


	/**
	 * 
	 * stateful portlets cannot be displayed when the portal is in customise mode, as 
	 * the portletrenderer is contained within a SimplePortlet which is not stateful
	 * 
	 */
    @Override
	protected final AbstractPortletRenderer doGetPortletRenderer() {
		return new AbstractPortletRenderer() {
			protected void generateBodyXML(PageState state, Element document) {
				document.newChildElement(
					"portlet:stateful-portlet-in-edit-mode",
						WorkspacePage.PORTLET_XML_NS);
			}
		};

	}
}
