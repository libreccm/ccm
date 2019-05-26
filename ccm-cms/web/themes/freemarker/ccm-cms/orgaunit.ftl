<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getAvailableTabs item>
    <#return item["./orgaUnitTabs/availableTabs/availableTab"]>
</#function>

<#function getTypeNameKey item>
    <#return item["./type/label"]>
</#function>

<#function getTabLabel tab>
    <#return tab["./@label"]>
</#function>

<#function isTabSelected tab>
    <#return tab["./@selected"]>
</#function>

<#function getTabLink tab>
    <#return "?selectedTab=" + tab["./@label"]>
</#function>

<#function getSelectedTab item>

    <#--  <#assign selectedTab=item["./orgaUnitTabs/availableTabs/*[@selected='true']/@label"]>

    <#return item["./tab[@name='${selectedTab}']"]>  -->

    <#return item["./orgaUnitTabs/selectedTab/*"]>
</#function>

<#function getTypeOfSelectedTab item>

    <#assign nodeName = item["./orgaUnitTabs/selectedTab/*"]?node_name>
    <#if (nodeName == "text")>
        <#return item["./orgaUnitTabs/selectedTab/text/@key"]>
    <#else>
        <#return nodeName>
    </#if>
</#function>

<#function getPropertyFromTab tab name>
    <#return tab["./*[name()='${name}']"]>
</#function>

<#function getTabContent tab>
    <#return tab["."]>
</#function>

<#function getAddendum data>
    <#return data["./addendum"].@@text>
</#function>

<#function getMembers data>
    <#return data["./members"]>
</#function>

<#function getMemberRole member>
    <#return member["./@role"]>
</#function>

<#function getMemberStatus member>
    <#return member["./@status"]>
</#function>

<#function getContactEntries member>
    <#return member["contacts/contact[@contactType = $contact-type]/contactentries"]>
</#function>

<#function hasOrgaUnitContact data>
    <#return (data["./contacts/contact"]?size > 0)>
</#function>

<#function getOrgaUnitContact data>
    <#return data["./contacts/contact[1]"]>
</#function>

<#function getOrgaUnitContactPerson data>
    <#return data["./contacts/contact[1]/person"]>
</#function>

<#function getOrgaUnitContactEntries data>
    <#return data["./contacts/contact[1]/contactentries"]>
</#function>


