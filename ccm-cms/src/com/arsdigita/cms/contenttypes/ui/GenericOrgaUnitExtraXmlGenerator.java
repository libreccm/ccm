/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * <p> Base class for {@link ExtraXMLGenerator}s for sub classes of
 * {@link GenericOrganizationalUnit}. The only method which has to be
 * overwritten is {@link #getTabConfig()}. This method will return the tabs
 * (instances of implementations of {@link GenericOrgaUnitTab}). The
 * {@link #generateXML(com.arsdigita.cms.ContentItem, com.arsdigita.xml.Element, com.arsdigita.bebop.PageState)}
 * method delegates the XML creation to this objects. </p> <p>
 * {@link GenericOrganizationalUnit} does not include this generator. The
 * subclasses of {@link GenericOrganizationalUnit} are responsible for
 * integrating the subclasses of this class by overwriting the
 * {@link ContentPage#getExtraXMLGenerators()}. </p>
 *
 * @author Jens Pelzetter
 * @version $Id: GenericOrgaUnitExtraXmlGenerator.java 1186 2011-10-21 18:20:36Z
 * jensp $
 */
public abstract class GenericOrgaUnitExtraXmlGenerator
                implements ExtraXMLGenerator {

    private final static Logger LOGGER =
                                Logger.getLogger(
            GenericOrgaUnitExtraXmlGenerator.class);
    private boolean listMode = false;
    private final static String SELECTED_TAB_PARAM = "selectedTab";
    private String showOnly;

    @Override
    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof GenericOrganizationalUnit)) {
            throw new IllegalArgumentException(
                    "This ExtraXMLGenerator supports "
                    + "only instances of GenericOrganizationalUnit only.");
        }

        if (listMode) {
            return;
        }

        final Element orgaUnitTabsElem = element.newChildElement("orgaUnitTabs");

        final Element availableTabsElem = orgaUnitTabsElem.newChildElement(
                "availableTabs");

        final GenericOrganizationalUnit orgaunit =
                                        (GenericOrganizationalUnit) item;
        final Map<String, GenericOrgaUnitTab> tabs =
                                              processTabConfig(
                getTabConfig());
        String selected = state.getRequest().getParameter(
                SELECTED_TAB_PARAM);
        if (showOnly != null && !showOnly.isEmpty()) {
            selected = showOnly;
        }
        if ((selected == null) || selected.isEmpty()) {
            selected = new ArrayList<String>(tabs.keySet()).get(0);
        }
        final long availableStart = System.currentTimeMillis();
        if ((showOnly == null) || showOnly.isEmpty()) {
            for (Map.Entry<String, GenericOrgaUnitTab> entry :
                 tabs.entrySet()) {
                if (entry.getValue().hasData(orgaunit, state)) {
                    createAvailableTabElem(availableTabsElem,
                                           entry.getKey(),
                                           selected);
                }
            }
        }
        LOGGER.debug(String.format(
                "Created available tabs XML for "
                + "GenericOrganizationalUnit '%s' in %d ms.",
                orgaunit.getName(),
                System.currentTimeMillis()
                - availableStart));

        if (tabs.containsKey(selected)
            && tabs.get(selected).hasData(orgaunit, state)) {
            final GenericOrgaUnitTab selectedTab = tabs.get(selected);
            final Element selectedTabElem =
                          orgaUnitTabsElem.newChildElement(
                    "selectedTab");
            selectedTab.generateXml(orgaunit, selectedTabElem, state);
        } else {
            orgaUnitTabsElem.newChildElement("selectedTabNotAvailable");
        }
    }

    /**
     * Can be used from a JSP template for Navigation to show only a specific
     * tab in category.
     *
     * @param showOnly
     */
    public void setShowOnly(final String showOnly) {
        this.showOnly = showOnly;
    }

    private void createAvailableTabElem(final Element parent,
                                        final String key,
                                        final String selected) {
        final Element availableTabElem = parent.newChildElement("availableTab");
        availableTabElem.addAttribute("label", key);
        if (key.equals(selected)) {
            availableTabElem.addAttribute("selected", "true");
        } else {
            availableTabElem.addAttribute("selected", "false");
        }
    }

    /**
     * <p> This method should return a string containing all tabs to use. The
     * string must have to following format: </p> <p>
     * <code>
     * tabName:fullyQualifedClassName;...
     * </code> </p> <p> Example: </p> <p>
     * <code>
     * foo:com.arsdigita.cms.contenttypes.ui.FooTab;bar:com.arsdigita.cms.contenttypes.BarTab;fooBar:com.arsdigita.cms.contenttypes.ui.FooBarTab
     * </code> </p>
     *
     * @return
     */
    public abstract String getTabConfig();

    private Map<String, GenericOrgaUnitTab> processTabConfig(
            final String tabConfig) {
        final long start = System.currentTimeMillis();
        final String[] tokens = tabConfig.split(";");

        final Map<String, GenericOrgaUnitTab> tabs =
                                              new LinkedHashMap<String, GenericOrgaUnitTab>();

        for (String token : tokens) {
            processTabConfigToken(tabs, token);
        }

        LOGGER.debug(String.format("Processed tab config in %d ms",
                                   System.currentTimeMillis() - start));
        return tabs;
    }

    @SuppressWarnings("unchecked")
    private void processTabConfigToken(
            final Map<String, GenericOrgaUnitTab> tabs,
            final String tabConfigToken) {
        final String[] tokens = tabConfigToken.split(":");

        if (tokens.length != 2) {
            throw new IllegalArgumentException(String.format(
                    "Invalid tab configuration token. Found more or less than"
                    + "two tokens in string '%s'.",
                    tabConfigToken));
        }

        final String tabName = tokens[0];
        final String tabClassName = tokens[1];
        final GenericOrgaUnitTab tab = createTabInstance(tabClassName);
        tab.setKey(tabName);
        tabs.put(tabName, tab);
    }

    @SuppressWarnings("unchecked")
    private GenericOrgaUnitTab createTabInstance(final String tabClassName) {
        final GenericOrgaUnitTab tab;

        try {
            tab = (GenericOrgaUnitTab) Class.forName(tabClassName).
                    newInstance();
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(String.format(
                    "Can't find tab class '%s'.",
                    tabClassName),
                                               ex);
        } catch (InstantiationException ex) {
            throw new IllegalArgumentException(String.format(
                    "Can't instantiate tab class '%s'.",
                    tabClassName),
                                               ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(String.format(
                    "Can't instantiate tab class '%s'.",
                    tabClassName),
                                               ex);
        }

        return tab;
    }

    public void addGlobalStateParams(final Page page) {
        //Nothing yet
    }

    @Override
    public void setListMode(final boolean listMode) {
        this.listMode = listMode;
    }

}
