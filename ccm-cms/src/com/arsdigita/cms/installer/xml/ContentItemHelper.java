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
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;

import java.lang.reflect.Method;

/**
 * <P>Helper class for processing XML input. Used to create, retrieve, and clone
 * ContentItems based on xml input.</P>
 *
 * @author Nobuko Asakai (nasakai@redhat.com)
 * @see com.arsdigita.cms.installer.xml.XMLContentItemHandler
 * @see com.arsdigita.cms.installer.xml.ContentItemLoader
 * */
public class ContentItemHelper {
    private static final Logger s_log = Logger.getLogger(ContentItemHelper.class);
    /* Default language for this item instance */
    private static final String DEFAULT_LANG = "en";
    private String m_name;
    private Folder m_parent;
    private ContentSection m_section;
    private int m_cloneCount;
    /* An instance of the actual item */
    protected ContentItem m_item;
    /* The language of this content Item */
    protected String m_language;
    private String m_contentType;
    private Class m_contentTypeClass;

    public ContentItemHelper(ContentSection section) {
        m_section = section;
        m_parent = section.getRootFolder();
        m_cloneCount = 0;
        m_language = DEFAULT_LANG;
    }

    public void setContentSection(ContentSection section) {
        m_section = section;
    }

    public ContentSection getContentSection() {
        return m_section;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setContentType(String type) {
        m_contentType = type;
    }

    public String getContentType() {
        return m_contentType;
    }


    public void setParent(Folder parent) {
        m_parent = parent;
        if (m_item != null ) {
            m_item.setParent(parent);
        }
    }

    public Folder getParent() {
        return m_parent;
    }

    /** Number of times this item should be cloned */
    public void setCloneCount(int cloneCount) {
        m_cloneCount =  cloneCount;
    }

    public int getCloneCount() {
        return m_cloneCount;
    }

    public ContentItem getContentItem() {
        return m_item;
    }

    public void setContentItem(ContentItem item) {
        m_item = item;
    }

//    public void clear () {
//        m_item = null;
//    }

    public void setLanguage ( String lang ) {
        m_language = lang;
    }

    /**
     * <P> Convenience method to set the item's body text. This is a
     * no-op, subclasses should override it.</P>
     *
     */
    public void setBodyText ( String body ) {
        //no-op
        s_log.warn ( "Body Text not set" );
    }

    /**
     * <P> Convenience method that creates (or retrieves) the ContentItem
     * without saving it </P>
     *
     * @see #createContentItem(boolean)
     */
    public ContentItem create() {
        return createContentItem(false);
    }

    /**
     * <P> Convenience method that creates (or retrives) the ContentItem
     * and optionally saves it according to the value of <code>save</code></P>
     *
     * @see #createContentItem(boolean)
     */
    public ContentItem create( boolean save ) {
        return createContentItem(save);
    }


    /**
     * <P> Convenience method that creates (or retrives) the ContentItem
     * without saving it </P>
     *
     * @see #createContentItem(boolean)
     */
    public ContentItem createContentItem() {
        return createContentItem(false);
    }


    /**
     * <P> Saves the ContentItem. The parent is also set at this point, as
     * setting the parent implicitly saves the item </P>
     */
    public void save() {
        // FIXME: would be better to set parent in the create code, but that
        // apparently saves the object implicitly
        m_item.setParent(getParent());
        m_item.save();
        s_log.debug ( "Done saving item :" + m_item.getName() + " " + m_item);
    }


    /**
     * <P>Creates (or retrieves) a ContentItem of given type that
     * can be upcasted. The new item is optionally saved if the
     * <code>save</code> parameter is true.
     *
     * @see #createNewContentItem()
     */
    protected ContentItem createContentItem( boolean save ) {
        try {
            //needs to be set even if new item is not created.
            m_contentTypeClass = Class.forName(getContentType());
        } catch ( ClassNotFoundException e ) {
            throw new UncheckedWrapperException (e);
        }
        m_item = getContentItemByName( getName(), getParent() );
        if (m_item == null) {
            m_item = createNewContentItem();
        }
        s_log.debug ( "Done calling create new on Content item");

        // We sometimes need to defer saving until the subclass
        // not null attributes are set
        if (save) {
        s_log.debug ( "Trying to prematurely save Content item");
            save();
        }

        Assert.exists(m_item);
        s_log.debug ( "Successfully created content item");
        return m_item;
    }


    /**
     * <P> This method is used to create a new ContentItem. It does not
     * check to see if this item already exists. The new ContentItem is
     * not saved.
     *
     * @see #createContentItem(boolean)
     */
    protected ContentItem createNewContentItem () {
        ContentItem l_item;
        try {
            m_contentTypeClass = Class.forName(getContentType());
            s_log.debug ("Creating item with type: " + m_contentTypeClass.getClass().getName()
                        + " Name: " + getName()
                        + " parent: " + getParent().getName());

            l_item = (ContentItem)m_contentTypeClass.newInstance();
            // Do things that needs to be done to items in general
            l_item.setName(getName());
            l_item.setLanguage(m_language);
            l_item.setContentSection(getContentSection());
            //calling this implicitly saves apparently.
            //l_item.setParent ( getParent() );

        } catch (InstantiationException e) {
            s_log.warn ( "New Exception", e );
            throw new UncheckedWrapperException("InstantiationException", e);
        } catch (IllegalAccessException e) {
            s_log.warn ( "New Exception", e );
            throw new UncheckedWrapperException(e);
        } catch ( ClassNotFoundException e ) {
            s_log.warn ( "New Exception", e );
            throw new UncheckedWrapperException (e);
        }

        Assert.exists ( l_item );
        return l_item;
    }


    /* Only call this method after create() has been called. */
    public void set(String methodName, String argClass, String value) {
        Assert.exists(m_item);
        Assert.exists(m_contentTypeClass);
        if (methodName != null ) {
            s_log.debug("setting property with : " + methodName );
        }

        if ( argClass != null ) {
            s_log.debug("property class is : " + argClass);
        } else {
            s_log.debug("missing argClass");
        }
        if (value != null ) {
            s_log.debug("value is : " + value);
        }

        if (methodName != null || argClass != null
            || value != null ) {
            try {
                Assert.exists(argClass);
                Class[] args = {Class.forName(argClass)};
                s_log.debug("npe2?");
                Method method = m_contentTypeClass.getMethod(methodName, args);
                s_log.debug("npe3?");
                Object[]argv = getArgClassArray ( args[0], value );
                s_log.debug("npe4?");
                method.invoke(m_item, argv);
                s_log.debug("npe5?");
            } catch (IllegalAccessException e) {
                throw new UncheckedWrapperException(e);
            } catch (ClassNotFoundException e) {
                throw new UncheckedWrapperException(e);
            } catch ( Exception e ) {
                s_log.error ( "Unchecked Exception", e );
                throw new UncheckedWrapperException(e);
            }
        }
    }


    /**
     * <P>Returns a single element object Array of an instance of
     * <code>argClass</code> specified by it's string representation
     * <code>value</code>. The method needs to be explicitly taught how to
     * convert value into an instance of argClass. Right now, {@link String}
     * and {@Link Double} are supported.
     *
     * @param argClass the class to create a new instance of
     * @param value the String representation of argClass
     * @return a Single element Object array of the new instace of argClass
     */
    private Object [] getArgClassArray ( Class argClass, String value )
                                                    throws IllegalArgumentException {
        if ( argClass.equals ( String.class ) ) {

            Object [] returnArray = { value };
            return returnArray;
        } else if ( argClass.equals ( Double.class ) ) {
            Object [] returnArray = { Double.valueOf ( value ) };
            return returnArray;
        } else {
            throw new IllegalArgumentException (
                                "Do not know how to instantiate specified class" );
        }
    }


    /**
     * Look for ContentItems with the same name
     */
    protected ContentItem getContentItemByName(String name, Folder parent) {
        Assert.exists(parent);
        // Also check that there aren't any duplicates
        ContentItem item = null;
        ItemCollection items = parent.getItems();
        items.addNameFilter(name);
        s_log.debug ("Looking for: " + name);
        if (! items.isEmpty()) {
            while (items.next()) {
                item = items.getContentItem();
                s_log.debug ("Found: " + item.getName() + " " + item );
            }
        }
        return item;
    }


    /** Generate an acceptable title out of the name */
    private String generateName(String name) {
        String title;
        Perl5Util re = new Perl5Util();
        // just remove spaces
        title = re.substitute("s/ /-/g", name);
        return title.toLowerCase();
    }

    public ContentItem cloneItem(int count, Folder parent) {
        return cloneItem(count, parent, true);
    }

    protected ContentItem cloneItem(int count, Folder parent, boolean save) {
        Assert.exists(m_item);
        final String name = m_item.getName() + "_" + count;

        return cloneItem(name, parent, save);
    }

    protected ContentItem cloneItem(final String name, final Folder parent, final boolean save) {
        ContentItem clone = getContentItemByName(name, parent);
        if (clone == null) {
            clone = m_item.copy(parent, true);
            clone.setName(name);
            s_log.debug("creating clone: " + clone.getName()
                        + " parent: " + parent.getName());
        }
        if (save) {
            clone.save();
        }
        Assert.exists(clone);
        return clone;

    }

    /**
     * <P>Creates and returns a new Clone for the name and parent specified.
     * It does not check to see if the Item already exits, but creates it.
     * Subclasses should override this method if the ContentItem requires
     * special cloning procedures</P>
     */
    protected ContentItem newCloneItem ( String name, Folder parent ) {
        ContentItem clone = m_item.copy();
        clone.copyServicesFrom(m_item);
        clone.setName(name);
        clone.setContentSection(getContentSection());
        clone.setParent(parent);
        s_log.debug("creating clone: " + clone.getName()
                    + " parent: " + parent.getName());
        return clone;
    }


    /**
     * <P>Returns a name suitable to be used by a clone. Based of the name of
     * the ContentItem and the parameter <code>count</code></P>
     */
    public String cloneName ( int count ) {
        Assert.exists(m_item);
        return m_item.getName() + "_" + count;
    }
}
