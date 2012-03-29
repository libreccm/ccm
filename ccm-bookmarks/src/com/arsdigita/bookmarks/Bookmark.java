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

package com.arsdigita.bookmarks;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * Main domain class of the Bookmark application.
 * 
 * Main entry point into Bookmarks and provides services to save and retrieve
 * bookmarks.
 *
 * @author Jim Parsons
 */
public class Bookmark extends ACSObject {

    private static final int SORT_KEY_JUMP = 10;

    private Bookmarks m_bmrkapp = null;

    /**
     * The type of the {@link com.arsdigita.persistence.DataObject}
     * that stands behind this {@link
     * com.arsdigita.domain.DomainObject}.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.workspace.Bookmark";

    /**
     * Retrieve base DataObject for client classes.
     * @return 
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Constructor
     * 
     * @param dataObject 
     */
    public Bookmark(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Constructor
     * 
     * @param name
     * @param URLstring 
     */
    public Bookmark(String name, String URLstring) {
        super(BASE_DATA_OBJECT_TYPE);

        setName(name);
        setURL(URLstring);
        setCreateDate();
        setSortKey(50);
        setAuthor();
        setVisits(0);
    }

    /**
     * Constructor retrieving an existing Bookmark based on an ID.
     *
     */
    public static Bookmark retrieveBookmark(BigDecimal bmrkID) {
        Assert.exists(bmrkID);

        return Bookmark.retrieveBookmark(new OID(BASE_DATA_OBJECT_TYPE, bmrkID));
    }

    /**
     * Constructor retrieving an existing Bookmark based on a data object.
     *
     * @param dataObject the data object of the Bookmark to retrieve.
     * @return an existing Bookmark.  Note that the return value may be
     * null if no Bookmark data object for this ID exists.
     * @pre dataObject != null
     */
    public static Bookmark retrieveBookmark(DataObject dataObject) {
        Assert.exists(dataObject);

        return new Bookmark(dataObject);
    }

    /**
     * Retrieve an existing Bookmark based on an OID.
     *
     * @param oid the OID of the Bookmark to retrieve.
     * @pre oid != null
     */
    public static Bookmark retrieveBookmark(OID oid) {
        Assert.exists(oid);

        DataObject dataObject = SessionManager.getSession().retrieve(oid);

        return Bookmark.retrieveBookmark(dataObject);
    }

    /**
     * 
     * @return 
     */
    public Bookmarks getBookmarkApplication() {
        if(m_bmrkapp == null) {
           DataObject bmrkdata = (DataObject)get("bookmarkapp");
           if(bmrkdata != null) {
              m_bmrkapp = new Bookmarks(bmrkdata);
           }
        }
        return m_bmrkapp;
    }

    /**
     * 
     * @param bmrkapp 
     */
    public void setBookmarkApplication(Bookmarks bmrkapp) {
        m_bmrkapp = bmrkapp;
        setAssociation("bookmarkapp",bmrkapp);
    }

    /**
     * 
     * @return 
     */
    public static BookmarkCollection retrieveAllBookmarks() {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        BookmarkCollection bmrkCollection = new BookmarkCollection
            (dataCollection);

        return bmrkCollection;
    }

    // ///////////////////////////////////////////////////////////////////////
    // Accessors
    // ///////////////////////////////////////////////////////////////////////

    /**
     * Get the title of this Bookmark.
     *
     * @return this Bookmarks title.
     */
    public String getName() {
        String name = (String)get("bookmark_name");

        Assert.exists(name);

        return name;
    }

    /**
     * Get the URL of this Bookmark.
     *
     */
    public String getURL() {
        String url = (String)get("bookmark_url");

        Assert.exists(url);

        return url;
    }

    /**
     * Get the Description of this Bookmark.
     *
     */
    public String getDescription() {
        String description = (String)get("bookmark_desc");

        Assert.exists(description);

        return description;
    }

    /**
     * Get the Creation Date of this Bookmark.
     *
     */
    public String getCreateDate() {
        String creation = (String)get("create_date");

        //This could be null...
        return creation;
    }

    /**
     * Get the last modification date of this Bookmark.
     *
     */
    public String getModDate() {
        String mod = (String)get("mod_date");

        //if mod == null, then bmrk was never modified...
        if(mod == null)
          mod = (String)get("create_date");

        return mod;
    }

    public boolean getNewWindow() {
        return "1".equals(get("new_window"));
    }

    /**
     * Get the original creator's name for this Bookmark.
     *
     * @return this portal's title.
     * @post return != null
     */
    public String getAuthor() {
        String author = (String)get("author");

        return author;
    }

    /**
     * Get the number of times this Bookmark has been visited.
     *
     */
    private int getVisits() {

        int v = ((Integer)get("visits")).intValue();

        return v;
    }

    /**
     * Get the number of times this Bookmark has been visited,
     * and return value as a String.
     *
     */
    public String getNumVisits() {

        Integer v = (Integer)get("visits");

        return v.toString();
    }


    /**
     * Set the title of this Bookmark.
     *
     */
    public void setName(String name) {
        Assert.exists(name);

        set("bookmark_name", name);
    }

    /**
     * Set the URL of this Bookmark.
     *
     */
    public void setURL(String url) {
        Assert.exists(url);

        if(url.startsWith("http://"))
            set("bookmark_url", url);
        else if(url.startsWith("HTTP://"))
            set("bookmark_url", url);
        else if(url.startsWith("https://"))
            set("bookmark_url", url);
        else if(url.startsWith("HTTPS://"))
            set("bookmark_url", url);
        else
            {
                //if someone inputs 'HtTp://', we are in trouble...!
                //but the mistake will be visible in the edit form...
                String newURL = "http://" + url;
                set("bookmark_url", newURL);
            }

    }

    /**
     * Set the description for this Bookmark.
     *
     */
    public void setDescription(String desc) {
        //Assert.exists(desc);

        set("bookmark_desc",desc);
    }


    private void setCreateDate() {
        java.util.Date currentDate = new java.util.Date();
        String date = java.text.DateFormat.getDateTimeInstance().format(currentDate);

        set("create_date",date);
    }

    /**
     * Set the mod date for this Bookmark.
     *
     */
    public void setModDate() {
        java.util.Date currentDate = new java.util.Date();
        String date = java.text.DateFormat.getDateTimeInstance().format(currentDate);

        set("mod_date",date);
    }

    public void setNewWindow(boolean newWindow) {
        set("new_window", (newWindow ? "1" : "0"));
    }

    /**
     * Set the author for this Bookmark.
     *
     */
    private void setAuthor() {
        Party party = Kernel.getContext().getParty();
        String name = party.getDisplayName();

        set("author", name);
    }

    /**
     * Set the number of visits to this Bookmark.
     *
     */
    private void setVisits(int visits) {

        set("visits",new Integer(visits));
    }

    /**
     * Increment the counter for number of visits to this Bookmark.
     *
     */
    public void visitsPlusPlus() {
        int visits = getVisits();

        setVisits(visits++);
    }


    /**
     * Get the sort key of this Bookmark.  The sort key is used
     * to order a set of bookmarks in a particular Workspace.
     *
     * @return the bookmark sort key.
     */
    public int getSortKey() {
        return ((Integer)get("sortKey")).intValue();
    }

    public void setSortKey(int sortKey) {
        set("sortKey", new Integer(sortKey));
    }

    @Override
    protected void afterSave() {
        super.afterSave();
        PermissionService.setContext(this, getBookmarkApplication());
    }

}
