/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr.ui.tree;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ExternalLink;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeCellRenderer;
import com.arsdigita.cms.CMS;
import com.arsdigita.web.URL;

/**
 * Customized Bebop Tree that shows the accessible Repositories
 * at the root level and the inidividual File trees beneath it.
 * The root level is constructed expanded.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 */
public class DocFolderContentTreeRenderer implements TreeCellRenderer {

    private static final org.apache.log4j.Logger s_log = 
        org.apache.log4j.Logger.getLogger(DocFolderContentTreeRenderer.class);

    public Component getComponent(Tree tree,
                                  PageState state,
                                  Object value,
                                  boolean isSelected,
                                  boolean isExpanded,
                                  boolean isLeaf,
                                  Object key) {

        String m_key = (String) key;

        try {
            s_log.debug("state is "+state.stateAsURL());
        } catch (java.io.IOException ioex) {
        }

        URL u1 = state.toURL();

        //URL u2 = new URL
        //    (u1.getScheme(),
        //     u1.getServerName(),
        //     u1.getServerPort(),
        //     u1.getContextPath(),
        //     u1.getServletPath(),
        //     "/"+CMS.getContext().getContentSection().getName()
        //     +"/"+key.toString(),
        //     new ParameterMap(Web.getRequest()));
        //s_log.debug("u2 is "+ u2.toString());

        return new ExternalLink
            (value.toString(),
             u1.getServerURI()+
             u1.getServletPath()+
             "/"+CMS.getContext().getContentSection().getName()+
             "/"+key.toString()+
             "?"+u1.getQueryString());
    }

}
