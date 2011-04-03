/*
 * Created on 06-Apr-05
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.portalworkspace;

/**
 *Implementers of Stateful Portlets must implement a factory to retrieve the
 * correct renderer.
 *
 * @author cgyg9330
 */
public interface StatefulPortletRendererFactory {

    public StatefulPortletRenderer getRenderer();

}
