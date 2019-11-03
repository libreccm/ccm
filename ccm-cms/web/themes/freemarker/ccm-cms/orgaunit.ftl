<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for retrieving several properties from a content item
    of type OrganizationalUnit.
-->

<#--doc
    Provides a list of the available tabs for the item.

    @param item The organizatinal unit item.

    @return A list of the available tabs.
-->
<#function getAvailableTabs item>
    <#return item["./orgaUnitTabs/availableTabs/availableTab"]>
</#function>

<#--doc
    Retrieves the type of the item

    @param The item to use.

    @return The type the item.
-->
<#function getTypeNameKey item>
    <#return item["./type/label"]>
</#function>

<#--doc
    The label of the provided tab.

    @param The tab model (as provided by `getAvailableTabs`) from the which the 
    label is read

    @return The label of the tab.
-->
<#function getTabLabel tab>
    <#return tab["./@label"]>
</#function>

<#--doc
    Checks if the provided tab is selected.

    @param The tab model (from `getAvailableTabs`).

    @return `true` If the tab is selected, `false` otherwise.
-->
<#function isTabSelected tab>
    <#return (tab["./@selected"] == "true")>
</#function>

<#--doc
    Creates the link the provided tab.

    @param The tab model (from `getAvailableTab`).

    @return The link to the tab.
-->
<#function getTabLink tab>
    <#return "?selectedTab=" + tab["./@label"]>
</#function>

<#--doc
    Gets the model of the selected tab from the item model.

    @param The item (model) from the tab model is retrieved.

    @return The model of the selected tab.
-->
<#function getSelectedTab item>

    <#--  <#assign selectedTab=item["./orgaUnitTabs/availableTabs/*[@selected='true']/@label"]>

    <#return item["./tab[@name='${selectedTab}']"]>  -->

    <#return item["./orgaUnitTabs/selectedTab/*"]>
</#function>

<#--doc
    Gets the type the selected tab.

    @param item The item (model)

    @return The type of the selected tab.
-->
<#function getTypeOfSelectedTab item>

    <#assign nodeName = item["./orgaUnitTabs/selectedTab/*"]?node_name>
    <#if (nodeName == "text")>
        <#return item["./orgaUnitTabs/selectedTab/text/@key"]>
    <#else>
        <#return nodeName>
    </#if>
</#function>

<#--doc
    Reads a property from tab model (retrieved by `getSelectedTab`).

    @param tab The tab model.

    @param name The name of the property.

    @return The value of the property.
-->
<#function getPropertyFromTab tab name>
    <#return tab["./*[name()='${name}']"]>
</#function>

<#--doc
    Gets the complete content of a tab model

    @param tab The tab model.

    @return The content of the tab.
-->
<#function getTabContent tab>
    <#return tab["."]>
</#function>

<#--doc
    Gets the value of the `addendum`property from the overview tab.

    @param The data model to use.

    @return The value of the addendum property.
-->
<#function getAddendum data>
    <#return data["./addendum"].@@text>
</#function>

<#--doc
    Get the members of an organizational unit from the data of the members tab.

    @param data The data model of a members tab.

    @return The data models for the members of the organizational unit.
-->
<#function getMembers data>
    <#return data["./members"]>
</#function>

<#--doc
    Get the role of a member.

    @param The model of a member.

    @return The role of the member.
-->
<#function getMemberRole member>
    <#return member["./@role"]>
</#function>

<#--doc
    Get the status of a member.

    @param The model of a member.

    @return The status of the member.
-->
<#function getMemberStatus member>
    <#return member["./@status"]>
</#function>

<#--doc
    Get the contact entries of a member.

    @param The model of a member.

    @return The contact entries of the member.
-->
<#function getContactEntries member>
    <#return member["contacts/contact[@contactType = $contact-type]/contactentries"]>
</#function>

<#--doc
    Checks if the organizational unit item has an associated contact item.

    @param The data model of the overview tab.

    @return `true` if the organizational unit has an associated contact item, 
    `false` if not.
-->
<#function hasOrgaUnitContact data>
    <#return (data["./contacts/contact"]?size > 0)>
</#function>

<#--doc
    Gets the associated contact item.

    @param The data model of the overview tab.

    @return The model of the assocatiated contact item.
-->
<#function getOrgaUnitContact data>
    <#return data["./contacts/contact[1]"]>
</#function>

<#--doc
    Gets the person associated with the orga unit contact.

    @param The data model of the overview tab.

    @return The model of the associated person.
-->
<#function getOrgaUnitContactPerson data>
    <#return data["./contacts/contact[1]/person"]>
</#function>

<#--doc
    Gets the contact entries of the associated contact item.

    @param The data model of the overview tab.

    @return The contact entries of the associated contact item.
-->
<#function getOrgaUnitContactEntries data>
    <#return data["./contacts/contact[1]/contactentries"]>
</#function>


