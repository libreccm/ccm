/*
 * Copyright (C) 2002-2006 Runtime Collective Ltd. All Rights Reserved.
 *
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

package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.xml.Element;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This component generates a list of all live content items which have been placed
 * inside the specified folder. The parameters are:
 * <ul>
 * <li> contentSection: the name of the section
 * <li> folderName: the name of the folder, which has to be in the root for now.
 * The component should be extended to support "aaa/bbb/ccc".
 * <li> randomOne: (optional) if "true", only one item is rendered, chosen randomly
 * </ul>
 * <p>
 * To use this component, you can copy this example:<br>
 * &lt;define:component name="folderContent"
 * classname="com.arsdigita.cms.ui.FolderContent" /&gt;
 *   &lt;jsp:scriptlet&gt;<br>
 *     ((com.arsdigita.cms.ui.FolderContent) folderContent).setContentSection("content");<br>
 *     ((com.arsdigita.cms.ui.FolderContent) folderContent).setFolderName("myfolder");<br>
 *     ((com.arsdigita.cms.ui.FolderContent) folderContent).setRandomOne("true");<br>
 *   &lt;jsp:scriptlet&gt;
 * <p>
 *
 * @version $Revision: 1.2 $ $Date: 2005/01/07 14:59:36 $
 */
public class FolderContent extends SimpleComponent {

    public static final String versionId = "$Id: FolderContent.java 1166 2006-06-14 11:45:15Z fabrice $";

    private static Logger log = Logger.getLogger(FolderContent.class);;

    private static final String TAG_FOLDERCONTENT = "cms:folderContent";
    private static final String TAG_FOLDERCONTENT_SECTION = "section";
    private static final String TAG_FOLDERCONTENT_NAME = "name";
    private static final String TAG_FOLDERCONTENT_RANDOM = "random";

    private static final String TAG_ITEM = "cms:item";

    private static final String CONTENTITEM_CLASS_NAME = ContentItem.class.getName();

    public FolderContent() {
        super();
    }

    /** The name of the content section where the folder is to be found. */
    protected String contentSection = null;
    public String getContentSection() {
        return contentSection;
    }
    public void setContentSection(String name) {
        contentSection = name;
    }

    /** The name of the folder whose content we will output. */
    protected String folderName = null;
    public String getFolderName() {
        return folderName;
    }
    public void setFolderName(String name) {
        folderName = name;
    }

    /** The name of the folder whose content we will output. */
    protected String randomOne = null;
    public String getRandomOne() {
        return randomOne;
    }
    public void setRandomOne(String random) {
        randomOne = random;
    }

    /** Whether to pull out live items only (the default), or all items. */
    protected boolean liveOnly = true;
    public boolean getLiveOnly() {
        return liveOnly;
    }
    public void setLiveOnly(boolean liveOnly) {
        this.liveOnly = liveOnly;
    }

    /** The folder to look into. */
    protected Folder folder;
    public Folder getFolder() {
        return folder;
    }
    public void setFolder(Folder f) {
        folder = f;
        setFolderName((f != null) ? f.getName() : null);
    }

    /**
     * Generates the XML.
     *
     * @param state The page state
     * @param parent The parent DOM element
     */
    public void generateXML(PageState state, Element p) {

        // put the two attributes on the tag, can be useful for XSL
        Element parent = p.newChildElement(TAG_FOLDERCONTENT, CMS.CMS_XML_NS);
        parent.addAttribute(TAG_FOLDERCONTENT_SECTION, getContentSection());
        parent.addAttribute(TAG_FOLDERCONTENT_NAME, getFolderName());
        parent.addAttribute(TAG_FOLDERCONTENT_RANDOM, getRandomOne());

        // get the content section
        ContentSectionCollection sections = ContentSection.getAllSections();
        ContentSection section = null;
        while (sections.next()) {
            if (sections.getContentSection().getName().equals(getContentSection())) {
                section = sections.getContentSection();
                break;
            }
        }
        if (section == null) {
            log.warn("FolderContent couldn't find the section '"+getContentSection()+"'.");
            return;
        }

        // get the folder by name if necessary
        if (folder == null) {
        Folder root = section.getRootFolder();
        Folder.ItemCollection folders = root.getItems(true);
        folders.addFolderFilter(true);
        while (folders.next()) {
            log.debug("FolderContent looking at folder '"+folders.getContentItem().getName()+"'.");
            if (folders.isFolder() && folders.getContentItem().getName().equals(getFolderName())) {
                folder = (Folder) folders.getContentItem();
                break;
            }
        }
        }
        if (folder == null) {
            log.warn("FolderContent couldn't find the folder '"+getFolderName()+"'.");
            return;
        }

        // get the folder's live items
        List liveItems = new ArrayList();
        Folder.ItemCollection items = folder.getPrimaryInstances();
        //folders.addFolderFilter(false);
        while (items.next()) {
            log.debug("FolderContent looking at item '"+items.getDisplayName()+"'.");
            if (!items.isFolder()) {
                log.debug("FolderContent: this is an item, not a Folder.");
                if (!liveOnly || items.isLive()) {
                    log.debug("FolderContent accepts item.");
                liveItems.add(items.getContentItem());
                }
            }
        }

        // output the XML: either one random item, or all
        if ("true".equals(getRandomOne())) {
            if (liveItems.size() > 0) {
                int index = (int) (Math.random()*liveItems.size());
                log.debug("FolderContent rendering one random item, index: "+index);
                renderItem((ContentItem) liveItems.get(index), parent);
            }

        } else {
            log.debug("FolderContent rendering all items.");
            for (int i=0; i<liveItems.size(); i++) {
                renderItem((ContentItem) liveItems.get(i), parent);
            }
        }
    }


    public static void renderItem(ContentItem item, Element parent) {
        
        log.debug("Rendering item '"+item.getName()+"'.");

        Element itemElement = parent.newChildElement(TAG_ITEM, CMS.CMS_XML_NS);
        DomainObjectXMLRenderer renderer = new DomainObjectXMLRenderer(itemElement);

        // not sure these are necessary
        renderer.setWrapAttributes(true);
        renderer.setWrapRoot(false);
        renderer.setWrapObjects(false);
        renderer.walk(item, SimpleXMLGenerator.ADAPTER_CONTEXT);
    }
}
