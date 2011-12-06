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
package com.arsdigita.cms.contentitem;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.Folder;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * <P> This Helper class is used to parse input from xml files that represent
 * ContentItems stored as bundles. The Helper makes it virtually transparent
 * to save Items as Bundles. The Bundles are stored internally, and it is not
 * recommended that subclasses make use of them directly. Instead, save the Item
 * as you would were you using {@link ContentItemHelper}
 *
 * @see ContentItemHelper
 * @author Aizaz Ahmed
 */
public class ContentBundleHelper extends ContentItemHelper {

    private static final Logger s_log = Logger.getLogger(ContentBundleHelper.class);

    /*
     *  All Items are saved as content bundles. we need to keep the content
     *  bundles around because sometimes we only want to save at the very end
     *  and adding a content bundle to a folder implicitly saves
     */
    private ContentBundle m_bundle;

    public ContentBundleHelper(ContentSection section) {
        super ( section );
        m_bundle = null;
    }


    /**
     * <P> Note: If the create (or one of it's variants) has already been
     * called, setting the parent implicitly saves the object </P>
     */
    @Override
    public void setParent(Folder parent) {
        super.setParent ( parent );
        if (m_bundle != null ) {
            m_bundle.setParent(parent);
        }
    }


    /*
     * FIXME: need to know how to handle bundles in this scenario 
     */
    @Override
    public void setContentItem(ContentItem item) {
        super.setContentItem ( item );
        m_bundle = null;
    }


    /**
     * <P>Saves the Item and it's associated ContentBundle</P>
     */
    @Override
    public void save() {
        s_log.debug ( "About to save bundle" );
        //getParent().addItem(m_bundle);
        //m_bundle.setParent ( getParent() );
        m_bundle.save();
        m_item.save();
        s_log.debug ( "Saved bundle");
    }


    /**
     * <P> Adds to {@link ContentItemHelper#createContentItem(boolean)}
     * so that new Items are saved in appropriate bundles. If the bundle does
     * not exist, it is created. If it does exist, it is retrieved and the
     * new ContentItem is added to it. If this bundle already contains
     * an instance of this language, it will fail on Assert. </P>
     */
    @Override
    protected ContentItem createContentItem( boolean save ) {
    
        // if it exists, m_item gets set here.
        super.createContentItem ( save );

        if ( m_item instanceof ContentBundle ) {
            s_log.warn ( "Item already existed as a ContentBundle");
            //set the bundle and unravel the item
            m_bundle = (ContentBundle) m_item;
            m_item =  m_bundle.getInstance ( m_language );
            if ( m_item == null ) {
                //have a bundle, but not for this language
                m_item = createNewContentItem ();
                m_bundle.addInstance ( m_item );
            }
        } else {
            s_log.warn ( "Wrapping new Item in ContentBundle" );
            // FIXME: what about another object, previously saved, same name?
            m_bundle = new ContentBundle( m_item );
            m_bundle.setContentSection(getContentSection());
            m_bundle.setParent ( getParent() );
        }

        if ( save ) {
            save ();
        }
        s_log.warn ( "Created bundled Item: " + getName() + "  " + m_item );
        return m_item;
    }


    /**
     * <P>Sets the title of the ContentItem returned by the super method
     * to be the same as the item name </P>
     */
    @Override
    protected ContentItem createNewContentItem () {
        ContentItem toReturn = super.createNewContentItem();
        ((ContentPage)toReturn).setTitle ( toReturn.getName() );
        return toReturn;
    }


    /**
     * <P> Clones the item and the corresponding ContentBundles </P>
     * <P>Note: Cloning is done on an item basis. Ie. if you clone this item
     * it will only clone this language instance and create a new ( or
     * retrieve the appropriate) content bundle to add it to.
     */
    @Override
    public ContentItem cloneItem(String name, Folder parent, boolean save) {
        ContentBundle originalBundle = ((ContentPage)m_item).getContentBundle();
        s_log.debug ("Original bundle before cloning: "
                     + originalBundle.getName()
                     + " "  + originalBundle );
        ContentBundle cloneBundle = (ContentBundle) getContentItemByName ( name, parent );
        ContentItem clone = super.newCloneItem ( name, parent );
        ((ContentPage)clone).setTitle ( clone.getName() );
        /*
         * FIXME: This is an ugly hack. Cloning an item apparently changes the
         * name and parent of it's former Bundle to correspond with the new
         * item! Fortunately the actual associations are not affected.
         * This resets the original Bundle
         */
        originalBundle.setName ( getName() );
        originalBundle.setParent ( getParent() );
        s_log.debug ("Original bundle after cloning: "
                     + originalBundle.getName()
                     + " "  + originalBundle );
        if ( cloneBundle != null ) {
            // a bundle for this already exists
            cloneBundle.addInstance ( clone );
        } else {
            cloneBundle = new ContentBundle ( clone );
            cloneBundle.setContentSection ( clone.getContentSection() );
            cloneBundle.setParent ( parent );
        }
        s_log.debug ( "Used Bundle: " + cloneBundle );
        if (save) {
            clone.save();
            cloneBundle.save();
        }
        Assert.exists(clone);
        return clone;
    }
}
