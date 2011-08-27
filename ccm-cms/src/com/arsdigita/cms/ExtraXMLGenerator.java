/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
 */

package com.arsdigita.cms;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.xml.Element;

/**
 * Implement this interface to specify what extra XML
 * your content types may have.
 *
 * @author Fabrice Retkowsky (fabrice@runtime-collective.com)
 * @version $Id: ContentType.java 285 2005-02-22 00:29:02Z sskracic $
 */
public interface ExtraXMLGenerator {

    /**
     * Specify the XML for a given content item.
     * @param item The content item to render
     * @param element The element to add the rendered content to
     * @param state The current page state
     */
    public void generateXML(ContentItem item, Element element, PageState state);

    /**
     * Add all required global parameters.
     * @param p The page which contains to item to render
     */
    public void addGlobalStateParams(Page p);
}
