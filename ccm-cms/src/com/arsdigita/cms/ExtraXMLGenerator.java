/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
 */

package com.arsdigita.cms;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.xml.Element;

/**
 * Implement this interface to specify what extra XML
 * your content types may have.
 *
 * @author Fabrice Retkowsky (fabrice@runtime-collective.com)
 * @version $Revision: #17 $ $Date: 2004/08/17 $
 */
public interface ExtraXMLGenerator {

    public static final String versionId = "$Id: ContentType.java 285 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    /**
     * Specify the XML for a given content item.
     */
    public void generateXML(ContentItem item, Element element, PageState state);

    /**
     * Add all required global parameters.
     */
    public void addGlobalStateParams(Page p);
}
