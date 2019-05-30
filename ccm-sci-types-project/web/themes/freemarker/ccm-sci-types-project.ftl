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
        <#return item["./nav:attribute[@name = 'projectBegin']"]>
    </#if>
</#function>

<#function getEnd item>
    <#if (item["./projectEnd"]?size > 0)>
        <#return item["./projectEnd"]>
    <#elseif (item["./lifeSpan/end"])>
        <#return item["./lifeSpan/end"]>
    <#elseif (item["./nav:attribute[@name = 'projectEnd']"])>
        <#return item["./nav:attribute[@name = 'projectEnd']"]>
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
    <#if (item["./projectShortDesc"]?size > 0)>
        <#return item["./projectShortDesc"]>
    <#elseif (item["./shortDesc"]?size > 0)>
        <#return item["./shortDesc"]>
    <#elseif (item["./"]?size > 0)>
        <#return item["./shortDescription"]>
    </#if>
</#function>

<#function getSponsors item>
    <#return item[".//sponsors/sponsor"]>
</#function>

<#function getSponsorName sponsor>
    <#return sponsor["."]>
</#function>

<#function hasSponsorFundingCode sponsor>
    <#return (sponsor["./@fundingCode"]?size > 0)>
</#function>

<#function getSponsorFundingCode sponsor>
    <#return sponsor["./@fundingCode"]>
</#function>

<#function getSponsorLink sponsor>
    <#return sponsor["./@href"]>
</#function>

<#function hasFunding item>
    <#return (item["./funding"]?size > 0)>
</#function>

<#function getFunding item>
    <#return item[".//funding"]>
</#function>

<#function hasFundingVolume item>
    <#return (item[".//fundingVolume"]?size > 0)>
</#function>

<#function getFundingVolume item>
    <#return item[".//fundingVolume"][1].@@text>
</#function>

<#function getMembers item>
    <#return item[".//members/member"]>
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
    <#return item[".//involvedOrganizations/organization"]>
</#function>

<#function getInvolvedOrganizationName orga>
    <#return orga["./title"]>
</#function>

<#function getInvolvedOrganizationLink orga>
    <#return orga["./links[1]/targetURI"]>
</#function>

