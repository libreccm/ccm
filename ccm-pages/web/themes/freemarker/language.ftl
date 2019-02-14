<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#macro availableLanguages>

    <#assign langs=["empty"]>
    <#if (model["/bebop:page/cms:contentPanel"]?size > 0)>
        <#assign langs=model["/bebop:page/cms:contentPanel/availableLanguages/language/@locale"]>
    <#elseif (model["/bebop:page/nav:greetingItem"]?size > 0)>
        <#assign langs=model["/bebop:page/nav:greetingItem/availableLanguages/language/@locale"]>
    <#else>
        <#assign langs=model["/bebop:page/ui:siteBanner/supportedLanguages/language/@locale"]>
    </#if>
        
    <#list langs?sort as lang>
        <#nested lang, lang==negotiatedLanguage>
    </#list>
        
</#macro>
