/*
 * Created on 06-Apr-05
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.london.portal;

/**
 * @author cgyg9330
 *
 *Implementers of Stateful Portlets must implement a factory to retrieve the correct renderer.
 */
public interface StatefulPortletRendererFactory {
	
	
	
	public StatefulPortletRenderer getRenderer();

}
