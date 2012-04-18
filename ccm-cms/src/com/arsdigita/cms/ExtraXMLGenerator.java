/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
 */

package com.arsdigita.cms;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
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
     * @param page The page which contains to item to render
     */
    public void addGlobalStateParams(Page page);
    
    /**
     * This method is called by the {@link SimpleXMLGenerator} to forward the
     * value of the listMode property. This method may does nothing. In other
     * cases it allows it to disable the output of some properties, for example
     * to avoid endless loops (item a includes item b in its XML output and 
     * item b includes the XML output of item a).
     * 
     * @param listMode 
     */
    public void setListMode(boolean listMode);
}
