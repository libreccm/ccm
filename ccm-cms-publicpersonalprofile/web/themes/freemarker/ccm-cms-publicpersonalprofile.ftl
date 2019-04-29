<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getProfileOwner item>
    <#return item["./profileOwner"]>
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