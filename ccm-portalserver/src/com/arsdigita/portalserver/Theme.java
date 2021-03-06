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
package com.arsdigita.portalserver;

import com.arsdigita.kernel.ACSObject;
// import com.arsdigita.kernel.Kernel;
// import com.arsdigita.kernel.Party;
// import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * A theme. 
 * -
 * A theme is a collection of color choices and (possibly) images
 * that style a Portal. A theme is inherently tied to to the 
 * underlying design of the portal page; for example, a theme that 
 * stores and sets a color for a context bar is only useful if the
 * design of the Portal that uses such a theme has, in fact, a context
 * bar.
 * <p>
 * A particular portal design/layout is made up of components
 * such as context bar, colored horizontal rules, tabbed panes...etc. which
 * we refer to as a <i>skin</i>. A skin can have a theme applied to it, which
 * renders components with desired colors, fonts, and background images. This
 * Theme class is for enhancing the default skin that ships with Portal Server.
 * <p>
 * If a developer desires to create their own Portal design or skin, then
 * the following files would be need to be implemented:
 * <ul>
 * <li> A PDL file describing the data model for the skin's thematic elements.</li>
 * <li> A class like this one that implements Themes, with getter/setters for
 *      thematic elements</li>
 * <li> A CSS sheet with class names that correspond to the class names that
 *      the <code>buildStyleBlock() </code>method uses (see below)</li>
 * <li>Appropriate XSL sheet that utilizes the CSS class names.</li></ul>
 * <p>
 * This class provides setters and getters for all of the elements in the
 * default Portal Server skin, plus a method that builds a block of CSS tags
 * that are inserted into the output stream and used to override default
 * choices for the default skin.
 *
 * 
 *
 * @author Jim Parsons
 *
 */

public class Theme extends ACSObject implements Themes {


    /**
     * The type of the {@link com.arsdigita.persistence.DataObject}
     * that stands behind this {@link
     * com.arsdigita.domain.DomainObject}.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workspace.Theme";

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public Theme(DataObject dataObject) {
        super(dataObject);
    }

    public Theme(String name) {
        super(BASE_DATA_OBJECT_TYPE);
        setName(name);
        setContextBarColor("#006600");
        setContextBarColor("#000000");
        setActiveTabColor("#550000");
        setInactiveTabColor("#000055");
        setActiveTabTextColor("#000000");
        setInactiveTabTextColor("#000000");
        setTopRuleColor("#770033");
        setBottomRuleColor("#770033");
        setPortletHeaderColor("#440500");
        setPortletIconColor("#440500");
        setPortletBorderColor("#000048");
        setPortletBorderStyle("solid");
        setPortletHeaderTextColor("#000000");
        setPageBGColor("#ffffff");
        setPageBGImage("");
        setNarrowBGColor("#ffffff");
        setBodyTextColor("#000000");
    }


    /**
     * Retrieve an existing Theme based on an ID.
     *
     */
    public static Theme retrieveTheme(BigDecimal themeID) {
     // Assert.assertNotNull(themeID);
        Assert.exists(themeID);

        return Theme.retrieveTheme(new OID(BASE_DATA_OBJECT_TYPE, themeID));
    }

    /**
     * Retrieve an existing Theme based on a data object.
     *
     * @param dataObject the data object of the Theme to retrieve.
     * @return an existing Theme.  Note that the return value may be
     * null if no Theme data object for this ID exists.
     * @pre dataObject != null
     */
    public static Theme retrieveTheme(DataObject dataObject) {
     // Assert.assertNotNull(dataObject);
        Assert.exists(dataObject);

        return new Theme(dataObject);
    }

    /**
     * Retrieve an existing Theme based on an OID.
     *
     * @param oid the OID of the Theme to retrieve.
     * @pre oid != null
     */
    public static Theme retrieveTheme(OID oid) {
     // Assert.assertNotNull(oid);
        Assert.exists(oid);

        DataObject dataObject = SessionManager.getSession().retrieve(oid);

        return Theme.retrieveTheme(dataObject);
    }


    public static ThemeCollection retrieveAllThemes() {
        DataCollection dataCollection =
            SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);

        ThemeCollection themeCollection = new ThemeCollection
            (dataCollection);

        return themeCollection;
    }

    //
    // Accessors
    //

    /**
     * Get the title of this Theme.
     *
     * @return this Themes title.
     */
    public String getName() {
        String name = (String)get("theme_name");

     // Assert.assertNotNull(name);
        Assert.exists(name);

        return name;
    }

    /**
     * Set the title of this Theme.
     *
     */
    public void setName(String name) {
     // Assert.assertNotNull(name);
        Assert.exists(name);

        set("theme_name", name);
    }

    /**
     * Get the Description of this Theme.
     *
     */
    public String getDescription() {
        String description = (String)get("theme_desc");

     // Assert.assertNotNull(description);
        Assert.exists(description);

        return description;
    }

    /**
     * Set the description for this Theme.
     *
     */
    public void setDescription(String desc) {
        //Assert.assertNotNull(desc);

        set("theme_desc",desc);
    }


    public void setContextBarColor(String color) {
        set("ctx_bar_color", color);
    }

    public String getContextBarColor() {
        String cbc = (String)get("ctx_bar_color");

        return cbc;
    }

    public void setContextBarTextColor(String color) {
        set("ctx_bar_text_color", color);
    }

    public String getContextBarTextColor() {
        String cbtc = (String)get("ctx_bar_text_color");

        return cbtc;
    }

    public void setActiveTabColor(String color) {
        set("active_tab_color",color);
    }

    public String getActiveTabColor() {
        String atc = (String)get("active_tab_color");

        return atc;
    }


    public void setInactiveTabColor(String color) {
        set("inactive_tab_color",color); 
    }

    public String getInactiveTabColor() {
        String itc = (String)get("inactive_tab_color");

        return itc;
    }


    public void setActiveTabTextColor(String color) {
        set("active_tab_text_color",color); 
    }

    public String getActiveTabTextColor() {
        String attc = (String)get("active_tab_text_color");

        return attc;
    }


    public void setInactiveTabTextColor(String color) {
        set("inactive_tab_text_color",color); 
    }

    public String getInactiveTabTextColor() {
        String ittc = (String)get("inactive_tab_text_color");

        return ittc;
    }


    public void setTopRuleColor(String color) {
        set("top_rule",color); 
    }

    public String getTopRuleColor() {
        String trc = (String)get("top_rule");

        return trc;
    }


    public void setBottomRuleColor(String color) {
        set("bottom_rule",color); 
    }

    public String getBottomRuleColor() {
        String brc = (String)get("bottom_rule");

        return brc;
    }


    public void setPortletHeaderColor(String color) {
        set("portlet_head",color); 
    }

    public String getPortletHeaderColor() {
        String phc = (String)get("portlet_head");

        return phc;
    }


    public void setPortletIconColor(String color) {
        set("portlet_icon",color); 
    }

    public String getPortletIconColor() {
        String pic = (String)get("portlet_icon");

        return pic;
    }


    public void setPortletBorderColor(String color) {
        set("portlet_border_color",color); 
    }

    public String getPortletBorderColor() {
        String pbc = (String)get("portlet_border_color");

        return pbc;
    }


    public void setPortletBorderStyle(String style) {
        set("portlet_border_style",style); 
    }

    public String getPortletBorderStyle() {
        String pbs = (String)get("portlet_border_style");

        return pbs;
    }


    public void setPortletHeaderTextColor(String color) {
        set("portlet_header_text_color",color); 
    }

    public String getPortletHeaderTextColor() {
        String phtc = (String)get("portlet_header_text_color");

        return phtc;
    }


    public void setPageBGColor(String color) {
        set("page_bg_color",color); 
    }

    public String getPageBGColor() {
        String pbgc = (String)get("page_bg_color");

        return pbgc;
    }


    public void setPageBGImage(String url) {
        set("page_bg_image",url); 
    }

    public String getPageBGImage() {
        String pbgi = (String)get("page_bg_image");

        return pbgi;
    }


    public void setNarrowBGColor(String color) {
        set("narrow_bg_color",color); 
    }

    public String getNarrowBGColor() {
        String nbc = (String)get("narrow_bg_color");

        return nbc;
    }


    public void setBodyTextColor(String color) {
        set("body_text_color",color); 
    }

    public String getBodyTextColor() {
        String btc = (String)get("body_text_color");

        return btc;
    }


    @Override
    protected void afterSave() {
        super.afterSave();
    }
    
    public StringBuffer buildStyleBlock() {

        StringBuffer buffer = new StringBuffer();

        buffer.append("<STYLE type=\"text/css\"> <!--");

        buffer.append(" table.globalHeader { background-color: "
                        + getContextBarColor() + ";} ");

        buffer.append(" table.globalHeader { color: "
                        + getContextBarTextColor() + ";} ");

        buffer.append(" table.tabs td.activeTab { background: "
                        + getActiveTabColor() + ";} ");

        buffer.append(" table.tabs td.tabBeginning { background: "
                        + getActiveTabColor() + ";} ");

        buffer.append(" table.tabs td.tabEnd { background: "
                        + getActiveTabColor() + ";} ");


        buffer.append(" table.tabs td.inactiveTab { background: "
                        + getInactiveTabColor() + ";} ");

        buffer.append(" table.tabs td.tabBeginningOff { background: "
                        + getInactiveTabColor() + ";} ");

        buffer.append(" table.tabs td.tabEndOff { background: "
                        + getInactiveTabColor() + ";} ");


        buffer.append(" table.tabs td.activeTab { color: "
                        + getActiveTabTextColor() + ";} ");

        buffer.append(" table.tabs td.inactiveTab { color: "
                        + getInactiveTabTextColor() + ";} ");


        buffer.append(" table.topRuleUnderTabs { background: "
                        + getTopRuleColor() + ";} ");

        buffer.append(" table.topRuleNoTabs { background: "
                        + getTopRuleColor() + ";} ");

        buffer.append(" table.bottomRule { background: "
                        + getBottomRuleColor() + ";} ");

        buffer.append(" table.portlet td.portletHeader { background: "
                        + getPortletHeaderColor() + ";} ");

        buffer.append(" table.portlet td.portletIcon { background: "
                        + getPortletIconColor() + ";} ");

        buffer.append(" table.portlet td.portletHeader { color: "
                        + getPortletHeaderTextColor() + ";} ");
        buffer.append(" body { background: " + getPageBGColor() + ";} ");

        if(getPageBGImage() != null)
        buffer.append(" body {background-image: url("
                      + getPageBGImage() + ");} ");

        buffer.append(" td.narrowColumn td.portletBody { background: "
                        + getNarrowBGColor() + ";} ");

        buffer.append(" td.narrowColumnLeft td.portletBody { background: "
                        + getNarrowBGColor() + ";} ");

        buffer.append(" td.narrowColumnRight td.portletBody { background: "
                        + getNarrowBGColor() + ";} ");

        buffer.append("--></STYLE>");

        return (buffer);

  }
}
