<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getBegin item>
    <#if (item["./projectBegin"]?size > 0)>
        <#return item["./projectBegin"]>
    <#elseif (item["./lifeSpan/begin"])>
        <#return item["./lifeSpan/begin"]>
    <#elseif (item["./nav:attribute[@name = 'projectBegin']"])>
        <#return ./nav:attribute[@name = 'projectBegin']"]>
    </#if>
</#function>

<#function getEnd item>
    <#if (item["./projectEnd"]?size > 0)>
        <#return item["./projectEnd"]>
    <#elseif (item["./lifeSpan/end"])>
        <#return item["./lifeSpan/end"]>
    <#elseif (item["./nav:attribute[@name = 'projectEnd']"])>
        <#return ./nav:attribute[@name = 'projectEnd']"]>
    </#if>
</#function>

<#function getDescription item>
    <#if (item["./projectDesc"])>
        <#return item["./projectDesc"]>
    <#elseif (item["./description"])>
        <#return item["./description"]>
    </#if>
</#function>

<#function getShortDescription item>
    <#if (item["./projectShortDesc"])>
        <#return item["./projectShortDesc"]>
    <#elseif (item["./shortDesc"])>
        <#return item["./shortDesc"]>
    <#elseif (item["./"])>
        <#return item["./shortDescription"]>
    </#if>
</#function>

<#function getSponsors item>
    <#return item["./sponsors"]>
</#function>

<#function getSponsorName sponsor>
    <#return sponsor["."]>
</#function>

<#function getSponsorFundingCode sponsor>
    <#return sponsor["./@fundingCode"]>
</#function>

<#function getSponsorLink sponsor>
    <#return sponsor["./@href"]>
</#function>

<#function getFunding item>
    <#return item["./funding"]>
</#function>

<#function getFundingVolume item>
    <#return item["./fundingVolume"]>
</#function>

<#function getMembers item>
    <#return item["./members"]>
</#function>

<#function getMemberRole member>
    <#return member["./@role"]>
</#function>

<#function getMemberStatus member>
    <#return member["./@status"]>
</#function>

<#function getMemberId member>
    <#return member["./masterVersion/id"] + "_" + member["./@name"]>
</#function>

<#function getMemberLink member>
    <#return CMS.generateContentItemLink(member["./@oid"])>
</#function>

<#function getMemberSurname member>
    <#return member["./surname"]>
</#function>

<#function getMemberGivenName member>
    <#return member["./givenName"]>
</#function>

<#function getMemberTitlePre member>
    <#return member["./titlePre"]>
</#function>

<#function getMemberTitlePost member>
    <#return member["./titlePost"]>
</#function>

<#function getInvolvedOrganizations item>
    <#return item["./involvedOrganizations"]>
</#function>

<#function getInvolvedOrganizationName orga>
    <#return orga["./title"]>
</#function>

<#function getInvolvedOrganizationLink orga>
    <#return orga["./links[1]/targetURI]>
</#function>

