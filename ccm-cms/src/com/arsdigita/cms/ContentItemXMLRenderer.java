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
 * This is a special ContentItemXMLRenderer for CMS to get a more transparent
 * way to handle ContentBundles during XML output.
 *
 *  The problem was to change RelatedLinks and therefore Link to always link to
 *  the corresponding ContentBundle instead of the content item. To get the
 *  corresponding content item during XML generation, I have to test for
 *  ContentBundle and negotiate the language version.
 *  This is not possible in com.arsdigita.ccm
 *
 * @author quasi
 */

public class ContentItemXMLRenderer extends DomainObjectXMLRenderer {

    public ContentItemXMLRenderer(Element root) {
        super(root);
    }


    // This method will be called by DomainObjectTraversal.walk()
    // It's purpose is to test for ContentBundle objects and if found, replace
    // that object with the negotiated version of the content item.
    // Otherwise this methd will do nothing.
    @Override
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
