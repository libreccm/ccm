<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#---
    Retrieves the languages in which the content of the current page is available.

    @return A sequence of the available languages (as ISO language codes)
#-->
<#function getAvailableLanguages>
    <#if (model["/bebop:page/cms:contentPanel"]?size > 0)>
        <#return model["/bebop:page/cms:contentPanel/availableLanguages/language/@locale"]>
    <#elseif (model["/bebop:page/nav:greetingItem"]?size > 0)>
        <#return model["/bebop:page/nav:greetingItem/availableLanguages/language/@locale"]>
    <#else>
        <#return model["/bebop:page/ui:siteBanner/supportedLanguages/language/@locale"]>
    </#if>
</#function>

