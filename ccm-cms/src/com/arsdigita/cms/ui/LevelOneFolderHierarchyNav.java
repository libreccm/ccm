/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

import java.util.Stack;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.Folder;
import com.arsdigita.xml.Element;

/**
 * <p>
 * 18/01/2006 - moved to ccm-cms by erik@runtime-collective.com
 * </p>
 * 
 * <p>
 * 06/01/2004 - moved to a new file by steve@runtime-collective.com
 *
 * <p>
 * 22/12/2003 - Alteration by Runtime Collective. Changed this class to reflect
 * the actual folder structure that is currently used by UNDP, i.e. top-level
 * is just below root rather than two below root.
 * </p>
 *
 * Generates XML intended for a sidebar nav.  Includes toplevel
 * folders, with the current path expanded.  In this case, toplevel
 * folders are defined as folders two levels below a content section
 * root folder.
 *
 * For example, if a request is made to
 * /ccm/my-content-section/shapes/rectangle/square/square-article
 * and the shapes folder contains subfolders of rectangle, round, and
 * convex, the xml generated by this class would reflect this
 * structure:
 * 
 *     /rectangle
 *       /square
 *     /round
 *     /convex
 * 
 * The motivation for using 2-level deep folders as toplevel folders
 * instead of 1-level deep folders, is it allows additional "sites" to
 * be created with their own navigation schemes by creating addtional
 * 1-level deep folders.  For example, if we added a 1-level deep
 * folder of "colors" with subfolders of red, blue and green, the
 * toplevel folders displayed for requests within this "site" would
 * be:
 *
 *    /red
 *    /blue
 *    /green
 *
 * @author Peter Kopunec
 */
public class LevelOneFolderHierarchyNav extends SimpleComponent {
	
	public LevelOneFolderHierarchyNav() {
	}
	
	/**
	 * Returns true if the pre conditions has been passed.
	 */
	private boolean validPreConditions(PageState state) {
		return (isVisible(state) && CMS.getContext().hasContentItem());
	}
	
	/**
	 * Generates XML for the left hand side nav bar.
	 * 
	 * @param state The page state
     * @param parent The parent DOM element
	 */
	public void generateXML(PageState state, Element parent) {
		if (!validPreConditions(state)) {
			return;
		}
		
		Element parentElement = parent.newChildElement("cms:folderNavLinks", CMS.CMS_XML_NS);
		
		String contentSectionURL = CMS.getContext().getContentSection().getURL();
		
		Tree root = getTree(contentSectionURL);
		
		makeChildElement(parentElement, root);
	}
	
	private void makeChildElement(Element parentElement, Tree child) {
		Element childElement = parentElement.newChildElement("cms:folderNavLink", CMS.CMS_XML_NS);
		childElement.addAttribute("title", child.label);
		childElement.addAttribute("url", child.url);
		if (!child.isRoot) {
			if (child.isSelected) {
				childElement.addAttribute("selected", "1");
			}
			else {
				if (child.isOpen) {
					childElement.addAttribute("open", "1");
				}
			}
		}
		
		if (child.children != null) {
			while (!child.children.empty()) {
				makeChildElement((child.isRoot ? parentElement : childElement), (Tree) child.children.pop());
			}
		}
	}
	
	private Tree getTree(String contentSectionURL) {
		Tree tree = null;
		
		Tree prevTree = null;
		Tree child;
		Stack children;
		Stack folders;
		Folder currFolder = getCurrentFolder();
		Folder folder;
		boolean wasSelected = false;
		StringBuffer path;
		String currFolderPath;
		while (currFolder != null) {
			tree = new Tree();
			tree.id = currFolder.getID().intValue();
			tree.label = currFolder.getLabel();
			path = new StringBuffer(contentSectionURL);
			currFolderPath = currFolder.getPath();
			if (currFolderPath != null && currFolderPath.length() > 0) {
				path.append(currFolderPath).append('/');
			}
			tree.url = path.toString();
			tree.isOpen = true;
			if (!wasSelected) {
				wasSelected = true;
				tree.isSelected = true;
			}
			
			folders = getChildFolders(currFolder);
			children = new Stack();
			while (!folders.empty()) {
				folder = (Folder) folders.pop();
				child = new Tree();
				child.id = folder.getID().intValue();
				if (prevTree != null && prevTree.id == child.id) {
					child = prevTree;
					prevTree = null;
				}
				else {
					child.label = folder.getLabel();
					child.url = new StringBuffer(contentSectionURL).append(folder.getPath()).append('/').toString();
				}
				children.push(child);
			}
			tree.children = children;
			
			prevTree = tree;
			
			currFolder = (Folder) currFolder.getParent();
		}
		
		if (tree != null) {
			tree.isRoot = true;
			tree.label = "Home";
		}
		
		return tree;
	}
	
	/**
	 * Get the folder that the current content item is in.
	 */
	private Folder getCurrentFolder() {
		ContentItem curItem = CMS.getContext().getContentItem();
		Folder folder;
		try {
			folder = (Folder) curItem;
		}
		catch (ClassCastException ccex) {
			folder = (Folder) ((ContentBundle) curItem.getParent()).getParent();
		}
		
		return folder;
	}
	
	/**
	 * Get all the child folders of the supplied folders.
	 */
	private Stack getChildFolders(Folder folder) {
		Stack folders = new Stack();
		Folder.ItemCollection coll = folder.getItems();
		coll.addFolderFilter(true);
		
		while (coll.next()) {
			folders.push((Folder) coll.getContentItem());
		}
		coll.close();
		
		return folders;
	}
	
	class Tree {
		int id;
		String label;
		String url;
		Stack children;
		boolean isOpen = false;
		boolean isSelected = false;
		boolean isRoot = false;
	}
}
