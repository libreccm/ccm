/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms;

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectTraversalAdapter;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.xml.Element;

/**
 *
 * @author quasi
 */

public class ContentItemXMLRenderer extends DomainObjectXMLRenderer {

    public ContentItemXMLRenderer(Element root) {
        super(root);
    }

    protected void walk(final DomainObjectTraversalAdapter adapter,
            final DomainObject obj,
            final String path,
            final String context,
            final DomainObject linkObject) {

        DomainObject nObj = obj;

        if (nObj instanceof ContentBundle) {

            nObj = ((ContentBundle) obj).negotiate(DispatcherHelper.getRequest().getLocales());
        }

        super.walk(adapter, nObj, path, context, linkObject);
    }
}
