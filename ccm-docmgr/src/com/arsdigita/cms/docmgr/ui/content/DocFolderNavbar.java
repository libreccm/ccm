/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.cms.docmgr.ui.content;
import java.util.ArrayList;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.Repository;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;

/*  Navigation bar intended for documents content section.
 *
 *  @author Crag Wolfe
 */
public class DocFolderNavbar extends SimpleComponent {

    
    public void generateXML(PageState state, Element p) {
        
        if (!CMS.getContext().hasContentItem()) {
            return;
        }

        DimensionalNavbar dm = new DimensionalNavbar();
        ArrayList al = new ArrayList(7);

        ContentItem selectedItem = CMS.getContext().getContentItem();
        ContentItem curItem = null;
        ContentItem secondToLastItem = null;
        ContentItem lastItem = selectedItem;  // curve ball :-)
        if (selectedItem.getSpecificObjectType().equals
            ("com.arsdigita.cms.docmgr.DocFolder")) {
            al.add(new Label(selectedItem.getDisplayName()));
            curItem = (ContentItem) selectedItem.getParent();
        } else {
            // assume we are at the root folder
            al.add(new Label(selectedItem.getDisplayName()));
        }
        while (curItem != null) {
            al.add(new Link(curItem.getDisplayName(), 
                            generateURL(state,curItem)));
            secondToLastItem = lastItem;
            lastItem = curItem;
            curItem = (ContentItem) curItem.getParent();
        }
        if (al.size() > 1) {
            // don't maintain page state for content section root.
            al.set(al.size()-1,
                   new Link(lastItem.getDisplayName(),
                            "/"+CMS.getContext().getContentSection().getName()));
            // now, we have to fudge the name of the 1st doc folder
            // (root folder of the repository).  2 cases to deal with.  
            DocFolder df = (DocFolder) 
                ((DocFolder) secondToLastItem).getWorkingVersion();
            Repository rep = DocFolder.getRepository(df);
            if (al.size() == 2) {
                al.set(al.size()-2, 
                   new Label(rep.getDisplayName()));
            }
            if (al.size() > 2) {
                al.set(al.size()-2, 
                       new Link(rep.getDisplayName(), 
                                generateURL(state,df)));
            }
        }

        for( int i=al.size()-1; i>=0; i--) {
            dm.add((Component) al.remove(i));
        }
        dm.generateXML(state,p);

        // maybe help with garbage collection
        secondToLastItem = null; lastItem = null; selectedItem = null;
        dm = null; al = null;
    }

    private static String generateURL(PageState state, ContentItem ci) {
        URL u1 = state.toURL();
        return "/"+CMS.getContext().getContentSection().getName()+
            "/"+ci.getPath()+"?"+u1.getQueryString();
    }
}
