<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    The user banner component provides several informations about the current
    user.
-->

<#--doc
    Retreives the value the `greeting` property from the user banner.

    @return The value of the `greeting` property of the user banner.
-->
<#function getGreeting>
    <#return model["./ui:userBanner/@greeting"]>
</#function>

<#--doc
    Determines if the current user is logged in.

    @return `true` if the current user is logged in, `false` if not.
-->
<#function isLoggedIn>
    <#return (model["./ui:userBanner/@screenName"]?size > 0)>
</#function>

<#--doc
    Determines if the current user is *not* logged in.

    @return `true` if the current user is *not* logged in, `false` if the user
    is logged in.
-->
<#function isNotLoggedIn>
    <#return (model["./ui:userBanner/@screenName"]?size == 0)>
</#function>

<#--doc
    Retrieves the URL for changing the password. Only available if the current
    user is logged in.

    @return The URL for changing the password.
-->
<#function getChangePasswordUrl>
    <#return model["./ui:userBanner/@changePasswordURL"]>
</#function>

<#--doc
    Retrieves the link to the login form. Only available if the current user
    is not logged in.

    @return The link to the login form.
-->
<#function getLoginLink>
    <#return model["./ui:userBanner/@loginExcursionURL"]>
</#function>

<#--doc
    Retrieves the link for logging out. Only available if the current user
    is logged in.

    @return The link for logging out.
-->
<#function getLogoutLink>
    <#return model["./ui:userBanner/@logoutURL"]>
</#function>

<#--doc
    Retrieves the screen name (user name) of the current user. Only available
    if the current user is logged in.

    @return The screen name of the current user.
-->
<#function getScreenName>
    <#return model["./ui:userBanner/@screenName"]>
</#function>

<#--doc
    The given name of the current user. Only available if the current user is 
    logged in.

    @return The given name of the current user.
-->
<#function getUserGivenName>
    <#return model["./ui:userBanner/@givenName"]>
</#function>

<#--doc
    The family name of the current user. Only available if the current user is 
    logged in.

    @return The family name of the current user.
-->
<#function getUserFamilyName>
    <#return model["./ui:userBanner/@familyName"]>
</#function>