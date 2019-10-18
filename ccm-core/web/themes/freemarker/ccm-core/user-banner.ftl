<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getGreeting>
    <#return model["./ui:userBanner/@greeting"]>
</#function>

<#function isLoggedIn>
    <#return (model["./ui:userBanner/@screenName"]?size > 0)>
</#function>

<#function isNotLoggedIn>
    <#return (model["./ui:userBanner/@screenName"]?size == 0)>
</#function>

<#function getChangePasswordUrl>
    <#return model["./ui:userBanner/@changePasswordURL"]>
</#function>

<#function getLoginLink>
    <#return model["./ui:userBanner/@loginExcursionURL"]>
</#function>

<#function getLogoutLink>
    <#return model["./ui:userBanner/@logoutURL"]>
</#function>

<#function getScreenName>
    <#return model["./ui:userBanner/@screenName"]>
</#function>

<#function getUserGivenName>
    <#return model["./ui:userBanner/@givenName"]>
</#function>

<#function getUserFamilyName>
    <#return model["./ui:userBanner/@familyName"]>
</#function>