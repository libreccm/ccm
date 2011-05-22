/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.cms.installer.xml;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.runtime.ConfigError;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses and XML file definition of content items in a folder.
 * the XML configuration should look like this:
 * 
 * <PRE>
 * &lt;ccm:content-items&gt; &lt;!-- The Document Node --&gt;
 *      &lt;ccm:folder clone="1"
 *                     depth="3"
 *                     label="testFolder" &gt;
 *          &lt;!-- Folders can be nested to any level. clone, depth and label are
 *               all required properties. Clone and depth minimum values are 1
 *               which result in no action. The entire nested subtree is cloned
 *               and replicated to the specified depth --&gt;
 *          &lt;ccm:content-item
 *                          clone="2"
 *                          helperClass="com.arsdigita.cms.installer.xml.GenericArticleHelper"&gt;
 *              &lt;!-- ContentItems can be cloned but cannot have a depth. The
 *                   helperClass is used to create the object described here --&gt;
 *              &lt;ccm:content-type
 *                      classname="com.arsdigita.cms.contenttypes.Article"
 *                      objectType="com.arsdigita.cms.contenttypes.Article"/&gt;
 *              &lt;ccm:item-properties title="ArticleItem" language="en" &gt;
 *                  &lt;!-- Item properties are set here. A few tags may be supported
 *                       example,Body-text, but the set tags are usually used to
 *                       to call appropriate methods through the helper class on
 *                       the ContentItem. language is an optional attribute and
 *                       defaults to en. --&gt;
 *                  &lt;ccm:body-text&gt;
 *                      Optional Body Text
 *                  &lt;/ccm:body-text&gt;
 *                  &lt;ccm:item-property method="setSpecies"
 *                                     argClass="java.lang.String"
 *                                     argValue="TestSpecies"/&gt;
 *
 *                  &lt;ccm:item-property method="setWingspan"
 *                                     argClass="java.lang.Double"
 *                                     argValue="10.0"/&gt;
 *              &lt;/ccm:item-properties&gt;
 *
 *              &lt;ccm:content-item
 *                                helperClass="com.arsdigita.cms.installer.xml.ContentBundleHelper"
 *                                association-name="birdWatch" &gt;
 *                  &lt;!-- Nested ContentItems (nested within another content-item)
 *                       are created and associated with the encapsulating item.
 *                       These are defined as regular content-items, except for a
 *                       few notable differences:
 *                       1) They must have an association-name tag that is the
 *                       name of the encapsulating item's association.
 *                       2) They do not have a clone field, they are cloned as
 *                       often as the encapsulating item.
 *
 *                       Content-items can be nested any number of times --&gt;
 *              &lt;/ccm:content-item&gt;
 *
 *          &lt;/ccm:content-item&gt;
 *    &lt;/ccm:folder&gt;
 * &lt;/ccm:content-items&gt;
 * </PRE>
 *
 */


public class XMLContentItemHandler extends DefaultHandler {
    private static final Logger s_log 
        = Logger.getLogger(XMLContentItemHandler.class);

    public static final String CONTENT_ITEMS = "ccm:content-items";
    public static final String CONTENT_ITEM = "ccm:content-item";
    public static final String ASSOCIATED_ITEM = "ccm:associated-item";
    public static final String CONTENT_TYPE = "ccm:content-type";
    public static final String ITEM_PROPERTIES = "ccm:item-properties";
    public static final String ITEM_PROPERTY = "ccm:item-property";
    public static final String FOLDER = "ccm:folder";
    public static final String BODY_TEXT = "ccm:body-text";

    private xmlContentItem currItem;

    private Folder m_folder;
    private FolderTree currFolderTree;
    private Stack associated_items;
    boolean isAssociated = false;
    private ContentSection m_section;
    private String m_body = "foo!";


    /**
     *   @param section the ContentSection where the items will be
     *   created 
     */
        
    public XMLContentItemHandler(ContentSection section) {
        super();
        s_log.debug(XMLContentItemHandler.class.getName());
        m_section = section;
        currFolderTree = new FolderTree ( null );
        associated_items = new Stack();
    }


    public void startElement( String uri, String name,  
                              String qName, Attributes atts) {

        if (qName.equals(CONTENT_ITEMS) ) {
            // Start of document do nothing
        } else if (qName.equals(FOLDER)) {
            /*
             * Creates a new folder and sets currFolderTree to point to it.
             * Adds existing tree to the FolderTree stack
             */
            FolderHelper new_folderHelper = new FolderHelper ( m_section );
            new_folderHelper.setCloneCount( Integer.parseInt(atts.getValue("clone")));
            new_folderHelper.setName( validateTitle(atts.getValue("label")));
            new_folderHelper.setDepth( Integer.parseInt(atts.getValue("depth")));

            FolderTree newFolderTree = new FolderTree ( new_folderHelper );
            newFolderTree.setParentTree ( currFolderTree );
            currFolderTree.addSubTree ( newFolderTree );
            currFolderTree = newFolderTree;

            m_folder = (Folder) currFolderTree.getFolderHelper().createContentItem ( true );

        } else if ( qName.equals(CONTENT_ITEM)) { 
            /*
             * Creates a new ContentItem and points to it. (does not save
             * it yet) If the item is nested within another item, it is
             * created as an association and added to the association stack.
             * (So that associations can be nested). Otherwise the new
             * item is added to the main currFolderTree
             */
            xmlContentItem newItem = new xmlContentItem ();
            newItem.setHelperClass (atts.getValue("helperClass"));
            newItem.setCloneCount (atts.getValue("clone"));
            if ( m_folder != null ) {
                newItem.getHelperClass().setParent ( m_folder );
            }
            
            if ( currItem != null ) {
                if ( isAssociated ) {
                    associated_items.push ( currItem );
                }
                isAssociated = true;

                newItem.setParentItem ( currItem );
                final String associationName = atts.getValue ("association-name");
                Assert.exists( associationName, String.class );
                currItem.setAssociation ( associationName, newItem );
            } else {
                // only add to folderTree if not an association
                currFolderTree.addContentItem ( newItem );
            }
            currItem = newItem;

        } else if ( qName.equals(CONTENT_TYPE) ) {
            String objectType = atts.getValue("objectType");
            Assert.exists(objectType, String.class);
            currItem.setContentType ( objectType );
        } else if ( qName.equals(ITEM_PROPERTIES)) {
            final String itemName = atts.getValue("title");
            Assert.exists(itemName, String.class);
            validateTitle(itemName);
            currItem.setName ( itemName );
            String l_lang = atts.getValue("language");
            if ( l_lang != null && !l_lang.equals("") ) {
                currItem.getHelperClass().setLanguage(l_lang);
            }
            currItem.create (false);
        } else if ( qName.equals(BODY_TEXT)) {
            s_log.warn("Begin Body text");
            // do nothing
        } else if ( qName.equals(ITEM_PROPERTY)) {
            s_log.debug("setting property");

            currItem.getHelperClass().set(
                atts.getValue("method"),
                atts.getValue("argClass"),
                atts.getValue("argValue")
            );
            
       } else {
            s_log.debug("Unknown tag: " + name);
        }
    }
    
    public void characters(char[] ch, 
                           int start, 
                           int length) {
        
        m_body = new String(ch, start, length);
    }
    
    public void endElement( String uri, String name,  
                            String qName) {

        if ( qName.equals(BODY_TEXT) ) {
            s_log.warn("Setting body text");
            currItem.getHelperClass().setBodyText(m_body);
        } else if ( qName.equals(ITEM_PROPERTIES)) {
        } else if ( qName.equals(CONTENT_TYPE)) {
        } else if ( qName.equals(CONTENT_ITEM) ) {
            /*
             * Saves the item. Sets the current Item to null, or
             * to the encapsulating item if it was an association
             */
            currItem.save();
            if ( isAssociated && ! associated_items.isEmpty() ) {
                currItem = (xmlContentItem) associated_items.pop();
            } else if ( isAssociated && associated_items.isEmpty() ) {
                isAssociated = false;
            }

            if ( ! isAssociated ) {
                currItem = currItem.getParentItem();
            }
        } else if (qName.equals(FOLDER)) {
            s_log.debug ( "Reached folder end item with folderHelper: " + currFolderTree );
            currFolderTree.getFolderHelper().save();
            currFolderTree = currFolderTree.getParentTree();
            if ( ! currFolderTree.isRoot() ) {
                m_folder = (Folder) currFolderTree.getFolderHelper().create ( true );
            } else {
                //set this to be the root folder, null acts as default right now
                m_folder = null;
            }
            s_log.debug ( "Done Folder endElement");
        } else if (qName.equals(CONTENT_ITEMS)) { 
            /*
             * Reached the end of the xml document. Traverse the FolderTree and
             * expand out the items and folders that need to be cloned / 
             * replicated to a certain depth
             */
            expandFolderTree ( currFolderTree, m_section.getRootFolder() );

        } else {
            s_log.debug("Unknown tag: " + name);
        }
    }


    /**
     * Instantiates the helper class of type <code>classname</code>
     * with a single argument constructor, (section)
     */
    private ContentItemHelper getHelperClass(String classname) {
        
        if (!StringUtils.emptyString(classname)) {
            try {
                s_log.warn("Trying to create " + classname);
                Class classDef = Class.forName(classname);
                
                Class[] args = {ContentSection.class};
                Object[] argv = { m_section };
                Constructor constructor = 
                    classDef.getConstructor(args);
                s_log.warn("Got constructor " + constructor.getName());
                return (ContentItemHelper)constructor.newInstance(argv);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw new UncheckedWrapperException(e);
            } catch (InstantiationException e) {
                throw new UncheckedWrapperException(e);
            } catch (IllegalAccessException e) {
                throw new UncheckedWrapperException(e);
            } catch (ClassNotFoundException e) {
                throw new UncheckedWrapperException(e);
            } catch (Exception e ) {
                throw new UncheckedWrapperException(e);
            }
        } else {
            s_log.warn("Using default ContentItemHelper");
            return new ContentPageHelper(m_section);
        }
    }


    public ContentType getContentType(String typeName) 
        throws UncheckedWrapperException {
        ContentType type = null;
        s_log.debug("TypeName " + typeName);
        // Look for the contenttype
        try {
            type = ContentType.findByAssociatedObjectType(typeName);
        } catch (DataObjectNotFoundException ex) {
            throw new UncheckedWrapperException( 
                (String) GlobalizationUtil.globalize(
                    "cms.installer.cannot_find_content_type")
                .localize() + typeName, ex);
        }
        // make sure that the type is added to the section
        m_section.addContentType(type);
        m_section.save();
            
        s_log.debug("Content type is: " + type.getClassName());
        return type;
        
    }

    // Utilities
    private String validateTitle(String name) {
        Perl5Util util = new Perl5Util();
        String pattern = "/[^A-Za-z_0-9\\-]+ /";
        if (util.match(pattern, name)) {
            throw new ConfigError(
                "The \"" + name + 
                "\" name parameter must contain only alpha-numeric " +
                "characters, underscores, and/or hyphens.");
        }
        return name;
    }


    /**
     * This convenience method calls {@link #cloneFolderTree} with the parameters
     * required to expand the FolderTree <code>toClone</code> with the parent
     * folder <code>toAttachTo</code>.
     *
     * @see #cloneFolderTree
     */
    protected void expandFolderTree ( FolderTree toClone, Folder toAttachTo ) {
        cloneFolderTree ( toClone, toAttachTo, toAttachTo, 1, true, true, true, 1 );
    }
    

    /**
     * This recursive method traverses the FolderTree <code>toClone</code>
     * from top to bottom, expanding out nodes to clone, replicating nodes
     * to their specified depth, and then expanding out these new trees as
     * well. This is a fairly complicated method for a number of reasons, you
     * should never need to call it directly, use {@link #expandFolderTree}
     *
     * @param toClone The FolderTree to clone and expand
     * @param toAttachTo The Folder to attach the result of the expansion to
     * @param parent The parent of the node to attach to
     * @param cloneTime The clone number for this particular folder
     * @param firstTime If this is the first time the method is being called
     *                  . ie. not a recursive call. If so it will not attempt
     *                  to clone toClone's root folder.
     * @param mainline If the parent node is no the mainlin, ie. the orignal
     *                 folder tree and not along one of the branches created
     *                 via cloning or depth expansion. If it is along the
     *                 mainline, it will not attempt to replicate it's
     *                 subfolders.
     * @param clone Whether this Folder should attempt to clone itself. False
     *              if it is iteslf a clone and thus should not attempt to
     *              clone itself
     * @param depthTime The depth level that this folder is at. If it is at
     *                  it's maximum depth, don't replicate itself as a child
     *                  of itself any further.
     */
    protected void cloneFolderTree ( FolderTree toClone,
                                     Folder toAttachTo,
                                     Folder parent,
                                     int cloneTime,
                                     boolean firstTime,
                                     boolean mainline,
                                     boolean clone,
                                     int depthTime ) {
                                                                                                   
        /*
         * Clone self as many times as necessary. Folder's created as clones of
         * this should not clone themselves.
         */
        if ( clone && ! firstTime ) {
            int selfNumClone = toClone.getFolderHelper().getCloneCount();

            while ( cloneTime < selfNumClone ) {
                Folder clonedFolder = (Folder) toClone.getFolderHelper().cloneItem (
                                            cloneTime, parent );
                copyFolderItems ( toClone, clonedFolder );
                cloneTime++;
                cloneFolderTree ( toClone, clonedFolder, parent, cloneTime, 
                                  false, false, false, depthTime );
            }
        }

        /*
         * Expand yourself to the specified depth. If a depth is specified, this
         * folder should replicate and expand itself as a child of itself. The
         * children should remember what depth they are at. The entire subTree
         * needs to be expanded
         */
        if ( ! firstTime && ( depthTime < toClone.getFolderHelper().getDepth() ) ) {
            depthTime++;
            cloneFolderTree ( toClone, replicateFolder ( toClone, toAttachTo ),
                              toAttachTo, 1, false, false, true, depthTime );
        }

        /*
         * Expand out the subfolders along all the lines, main and cloned. If on
         * the mainline, grab the node to attachTo from the cloneTree itself,
         * otherwise replicate the folder to attach to.
         */
        List subFolders = toClone.getSubTrees();
        for ( int i=0; i < subFolders.size(); i++ ) {
            Folder newToAttachTo; 
            if ( mainline ) {
                newToAttachTo = ((Folder)((FolderTree)subFolders.get(i))
                        .getFolderHelper().getContentItem());
            } else {
                newToAttachTo = replicateFolder ( (FolderTree)subFolders.get(i), toAttachTo );
            }
            cloneFolderTree ( (FolderTree) subFolders.get(i), newToAttachTo,
                              toAttachTo, 1, false, mainline, true, 1 );
        }

        /*
         * Clone all the children for this Folder.
         */
        List childItems = toClone.getContentItems();
        for ( int i=0; i < childItems.size(); i++ ) {
            autoCloneChild ( (xmlContentItem) childItems.get(i), toAttachTo );
        }

    }


    /**
     * Clones The child the number of times it needs to be cloned and
     * attaches all the children to <code>parent</code>. Cloning an
     * xmlContentItem automatically clones all it's associations as well
     *
     * @param child the xmlContentItem to clone
     * @param parent the folder to attach all the new children to
     */
    private void autoCloneChild ( xmlContentItem child, Folder parent ) {
        final int numClone = child.getHelperClass().getCloneCount();
        for ( int i=1; i<numClone; i++ ) {
            child.clone (
                    i,
                    parent,
                    true );
        }
    }
    

    /**
     * Creates a new Folder as a child of <code>parent<code> and replicates
     * all content Items (only items, not nested folders etc) of 
     * <code>toCopy</code> to attach to it.
     *
     * @param toCopy the FolderTree whose items we wish to copy to the
     *               new Folder returned.
     * @param parent the parent of the new folder returned
     * @return A child folder of parent that has replicated copies of
     *         all of toCopy's children
     */
    private Folder replicateFolder ( FolderTree toCopy, Folder parent ) {

        Folder newFolder = (Folder) toCopy.getFolderHelper().cloneItem ( 
                toCopy.getFolderHelper().getName(), parent , true );
        return copyFolderItems ( toCopy, newFolder );
    }


    /**
     * Same as {@link #replicateFolder(FolderTree, Folder)} except that
     * it does not create a new folder, but rather attaches the child
     * copies to <code>copyTo</code>
     */
    private Folder copyFolderItems ( FolderTree toCopy, Folder copyTo ) {

        List childContent = toCopy.getContentItems();
        for ( int i=0; i < childContent.size() ; i++ ) {
            xmlContentItem child = (xmlContentItem) childContent.get(i);
            final String name = child.getHelperClass().getName();
            child.replicate(name,
                    copyTo,
                    true );
        }
        return copyTo;
    }


    private class FolderHelper extends ContentItemHelper {
        int m_treeDepth;
        
        public FolderHelper(ContentSection section) {
            super(section);
            m_treeDepth = 0;
            setContentType(Folder.BASE_DATA_OBJECT_TYPE);
        }

        public void setDepth(int depth) {
            m_treeDepth = depth;
            s_log.debug("Depth is now " + m_treeDepth);
        }
        public int getDepth() {
            return m_treeDepth;
        }

        public ContentItem createContentItem ( boolean save ) {
            s_log.warn("creating folder");
            Folder folder = (Folder)super.createContentItem( false );
            folder.setLabel(folder.getName());
            setContentItem(folder);
            if ( save ) {
                save ();
            }
            return folder;
        }

        public ContentItem cloneItem ( String name, Folder parent, boolean save ) {
            Folder folder= (Folder)super.cloneItem(name, parent, save);
            folder.setLabel(folder.getName());
            folder.save();
            return folder;
        }
    }


    /**
     * This helper class acts as a Folder node that can be used to build
     * a tree of folders. It maintains a copy of the FolderHelper that
     * created this particular Folder, it's parent. As well as a List of
     * FolderTrees that represent it's subfolders, and a List of
     * xmlContentItems that represent all it's children
     */
    private class FolderTree  { 

        private List m_subFolders;
        private List m_contentItems;
        private FolderTree m_parentTree;
        private FolderHelper m_helper;

        public FolderTree ( FolderHelper helper ) {
            m_subFolders = new Vector();
            m_contentItems = new Vector();
            m_helper = helper;
        }

        public FolderHelper getFolderHelper () {
            return m_helper;
        }
        
        public void setParentTree ( FolderTree parentTree ) {
            m_parentTree = parentTree;
            if ( parentTree != null && parentTree.getFolderHelper() != null ) {
                m_helper.setParent ( (Folder)parentTree.getFolderHelper().create(false) );
            }
        }

        public FolderTree getParentTree () {
            return m_parentTree;
        }

        public void addSubTree ( FolderTree subTree ) {
            m_subFolders.add ( subTree );
        }

        public List getSubTrees () {
            return m_subFolders;
        }

        public void addContentItem ( xmlContentItem item ) {
            m_contentItems.add ( item );
        }
    
        public List getContentItems () {
            return m_contentItems ;
        }

        public boolean isRoot () {
            if ( m_parentTree == null ) {
                return true;
            }
            return false;
        }
    }


    /**
     * This helper class is used to maintain information about each
     * item As well as all it's associations. It stores the helper class
     * that created the item, it's parent and lists of all associated
     * xmlContentItems. Cloning this item will automatically created
     * and assign clones of all it's associations as well.
     */
    private class xmlContentItem {

        /** The helper class that created this item */
        private ContentItemHelper xml_page_helper; 
        /** The List of associations. Maps of association names to
         *  xmlContentItems */
        private Vector xml_assocs;
        /** The parent Item */
        private xmlContentItem xml_parent_item;

        public xmlContentItem () {
            xml_assocs = new Vector();
        }
        
        /** 
         * Instantiates and sets the specified helper class
         */
        public void setHelperClass ( String l_helperClass ) {
            xml_page_helper = XMLContentItemHandler.this.getHelperClass (
                                    l_helperClass );
        }

        public ContentItemHelper getHelperClass () {
            return xml_page_helper;
        }

        public void setParentItem ( xmlContentItem l_parent ) {
            xml_parent_item = l_parent;
        }

        public xmlContentItem getParentItem () {
            return xml_parent_item;
        }

        public void setCloneCount ( String l_count ) {
            xml_page_helper.setCloneCount( Integer.parseInt( l_count ) );
        }

        public void setContentType ( String l_objectType ) {
            getHelperClass().setContentType(
                    (getContentType(l_objectType)).getClassName());
        }

        public void setName ( String l_name ) {
            s_log.debug ( "Setting Name to:" + l_name );
            getHelperClass().setName( l_name );
        }

        public void setAssociation ( String assocName, xmlContentItem assoc ) {
            Map newAssoc = new Hashtable();
            newAssoc.put ( assocName, assoc );
            xml_assocs.add ( newAssoc );
        }

        public List getAssociations () {
            return xml_assocs;
        }

        public void create ( boolean save ) {
            getHelperClass().create(save);
        }

        /**
         * Save this item. This will save all associated Items and set
         * the appropriate associations as well.
         */
        public void save () {
            getHelperClass().save();
            s_log.debug("About to save all the associations");
            ContentItem theItem = getHelperClass().getContentItem();
            for ( Iterator i = xml_assocs.iterator() ; i.hasNext() ; ) {
                Map m_assoc = (Map) i.next();
                for ( Iterator j = m_assoc.keySet().iterator() ; j.hasNext() ; ) {
                    String key = (String) j.next();
                    xmlContentItem value = (xmlContentItem) m_assoc.get(key);
                    DataAssociation da = (DataAssociation) theItem.get ( key );
                    value.getHelperClass().getContentItem().addToAssociation ( da );
                    s_log.debug("Just saved a data association" );
                }
             }
             theItem.save();
        }

        /**
         * Clones and returns the clone for this contentItem. All associated
         * items are cloned and associated to the new clone.
         *
         * @param cloneNumber This integer is used to create the name for the
         *                    clone returned.
         * @param parent The parent of the new cloned item
         * @param save If true, the new item will be saved before being returned
         */
        public ContentItem clone ( int cloneNumber, Folder parent, boolean save ) {
            return cloneItem ( "", cloneNumber, parent, save, false );
        }


        /**
         * Clones and returns the clone for this contentItem. All associated
         * items are cloned and associated to the new clone.
         *
         * @param name This will be the name of the clone returned 
         * @param parent The parent of the new cloned item
         * @param save If true, the new item will be saved before being returned
         */
        public ContentItem replicate ( String name, Folder parent, boolean save ) {
            return cloneItem ( name, 0, parent, save, true );
        }


        /**
         * FIXME: Need to refactor code here
         * <P>Clones the item as well as it's associations and associates them
         * appropriately.</P>
         *
         * @param name The name of the new clone, only used if
         *             <code>replicate</code> is true.
         * @param cloneNumber if <code>replicate</code> is false, this will be
         *             used to form the new name for the clone.
         *             {@link #clone(int,Folder,boolean)}
         * @param parent The parent of the new clone
         * @param save If true, the new item will be saved before it is returned
         * @param replicate will decide if the name of the clone should be based
         *             on <code>name</code> or <code>cloneNumber</code>
         */
        private ContentItem cloneItem ( String name,
                                       int cloneNumber,
                                       Folder parent,
                                       boolean save,
                                       boolean replicate
                                     )
        {
            // clone and set associations here as well
            if ( replicate ) {
                s_log.debug ( "About to replicate: "
                              + name + " with parent: " + parent.getLabel() );
            } else {
                name = getHelperClass().cloneName ( cloneNumber );
                s_log.debug ( "About to clone: " 
                              + name + " with parent: " + parent.getLabel() );
            }

            ContentItem clone = getHelperClass().cloneItem ( name, parent, save );
            //clone each association and associate it
            for ( int i=0; i < xml_assocs.size() ; i++ ) {
                Map m_assoc = (Map) xml_assocs.get ( i );
                for ( Iterator j = m_assoc.keySet().iterator() ; j.hasNext() ; ) {
                    String key = (String) j.next();
                    xmlContentItem value = (xmlContentItem) m_assoc.get(key);
                    String cloneAssocName;
                    if ( replicate ) {
                        cloneAssocName = value.getHelperClass().getName();
                    } else {
                        cloneAssocName = value.getHelperClass().cloneName(cloneNumber);
                    }
                    ContentItem cloneValue = value.getHelperClass().cloneItem ( 
                            cloneAssocName, parent, true );
                    
                    s_log.debug ( "Value of item: "
                                  + getHelperClass().getContentItem() );
                    s_log.debug ( "Value of cloned item: " + clone );
                    s_log.debug ( "Value of association: "
                                  + value.getHelperClass().getContentItem() );
                    s_log.debug ( "Value of cloned association : " + cloneValue );

                    DataAssociation da = (DataAssociation) clone.get ( key );
                    da.clear();
                    cloneValue.addToAssociation ( da );
                    s_log.debug ("Just saved a data association" );
                }
            }
            clone.save();
            return clone;
        }
    }
}
