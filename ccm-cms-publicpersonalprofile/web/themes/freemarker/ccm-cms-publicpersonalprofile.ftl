<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getProfileOwner data>
    <#return data["./profileOwner"]>
</#function>

<#function getProfileOwnerSurname owner>
    <#return owner["./surname"]>
</#function>

<#function getProfileOwnerGivenName owner>
    <#return owner["./givenname"]>
</#function>

<#function getProfileOwnerTitlePre owner>
    <#return owner["./titlePre"]>
</#function>

<#function getProfileOwnerTitlePost owner>
    <#return owner["./titlePost"]>
</#function>

<#function getProfileOwnerContact owner>
    <#return owner["./contact"]>
</#function>

<#function getProfileImage data>
    <#if (data["./ppp:profile/ppp:profileImage"]?size > 0)>
        <#return data["./ppp:profile/ppp:profileImage/imageAttachments[1]/*"]>
    <#elseif (data["./nav:greetingdata/cms:data/profileOwner/owner/imageAttachments"]?size > 0)>
        <#return data["./nav:greetingdata/cms:data/profileOwner/owner/imageAttachments[1]/*"]>
    </#if>
</#function>

<#function getProfileOwnerName data>
    <#return data["./ppp:profile/ppp:ownerName"]>
</#function>

<#function getPersonalPublications data>
    <#return data["./ppp:profile/personalPublications"]>
</#function>

<#function getPersonalPublicationsAvailablePublicationGroups data>
    <#return data["./ppp:profile/personalPublications/availablePublicationGroups"]>
</#function>

<#function getPublicationGroupId group>
    <#return group["./@name"]>
</#function>

<#function getPublicationGroupLink group>
    <#if (data["ppp:profile/personalPublications/publications/@all"] == "all")>
        <#return "#" + group["./@name"]>
    <#else>
        <#return "?group=" + group["./@name"]>
    </#if>
</#function>

<#function getPublicationGroups data>
    <#return data["./ppp:profile/personalPublications/publications/publicationGroup"]>
</#function>

<#function getPublicationsOfGroup data>
    <#return data["./publications"]>
</#function>

<#function gtAvailableProjectGroups data>
    <#return data["./ppp:profile/personalProjects/availableProjectGroups/availableProjectGroup"]>
</#function>

<#function getProjectGroupId group>
    <#return group["./@name"]>
<#function>

<#function getProjectGroupLink group>
    <#if (data["ppp:profile/personalPublications/publications/@all"] == "all")>
        <#return "#" + group["./@name"]>
    <#else>
        <#return "?group=" + group["./@name"]>
    </#if>
</#function>

<#function getProjectGroups data>
    <#return data["./ppp:profile/personalProjects/projects/projectGroup"]>
</#function>

<#function getProjectsOfGroup data>
    <#return data["./ppp:profile/personalProjects/projects/projectGroup/project"]>
</#function>







