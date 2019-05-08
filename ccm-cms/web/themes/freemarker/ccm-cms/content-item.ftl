<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getContentItem>
    <#return model["/bebop:page/cms:contentPanel/cms:item"]>
</#function>

<#--
    Includes the template for the type of the provided content item 
    for the provided view and style. The paramters view and style are
    optional. If not provided the default value "detail" is used for 
    the view parameter.
-->
<#macro contentItem item view="detail" style="">

    <#--<pre>
        item["/objectType"]: ${item["./objectType"]?size}
        item["/nav:attribute[@name='objectType']"]: ${item["./nav:attribute[@name='objectType']"]?size}
    </pre>-->

    
    <#if (item["./objectType"]?size > 0)>
        <#include getContentItemTemplate(item["./objectType"], view, style)>
    <#elseif (item["./nav:attribute[@name='objectType']"]?size > 0)>
        <#include getContentItemTemplate(item["./nav:attribute[@name='objectType'][1]"], view, style)>
    <#else>
        <#include getContentItemTemplate("com.arsdigita.cms.ContentItem", view, style)>
    </#if>
    
    
</#macro>

<#function getItemTitle item>
    <#if (item["./title"]?size > 0)>
        <#return item["./title"]>
    <#elseif (item["./nav:attribute[@name='title']"]?size > 0)>
        <#return item["./nav:attribute[@name='title']"]>
    <#else>
        <#return ''>
    </#if>
</#function>

<#function getPageTitle useCategoryMenu='categoryMenu' useRootIndexItemTitle=false>

    <#if (model["/bebop:page//cms:contentPanel"]?size > 0)>
        <#if (model["/bebop:page//cms:contentPanel/cms:item/type/label"].@@text == 'Glossary Item')>
            <#return getLocalizedText('page.title.glossary')>
        <#elseif (model["/bebop:page//cms:contentPanel/cms:item/type/label"].@@text == 'FAQ Item')>
            <#return getLocalizedText('page.title.faq')>
        <#else>
            <#return model["/bebop:page//cms:contentPanel/cms:item/title[1]"].@@text>
        </#if>
    <#elseif (model["/bebop:page/bebop:title"].@@text == 'AtoZ')>
        <#return getLocalizedText('layout.page.title.atoz')>
    <#elseif (model["/bebop:page/bebop:title"].@@text == 'Search')>
        <#return getLocalizedText('layout.page.title.search')>
    <#elseif (model["/bebop:page/@application"] == 'login')>
        <#return getLocalizedText('layout.page.title.login')>
    <#elseif (model["/bebop:page/@id"] == 'sitemapPage')>
        <#return getLocalizedText('layout.page.title.sitemap')>
    <#elseif (model["/bebop:page/@application"] == 'PublicPersonalProfile' && model["/bebop:page/nav:greetingItem"]?size > 0)>
        <#return model["/bebop:page/nav:greetingItem/cms:item/title"].@@text>
    <#else>
        <#if (model["/bebop:page/nav:categoryMenu[@id='${useCategoryMenu}']//nav:category[@isSelected='true']"]?size > 0)>
            <#if useRootIndexItemTitle && model["/bebop:page/nav:categoryMenu[@id='${useCategoryMenu}']//nav:category[@isSelected='true']"]?size > 0>
                <#if (model["/bebop:page//title"]?size > 0)>
                    <#return model["/bebop:page//title[1]"].@@text>
                <#else>
                    <#return ''>
                </#if>
            <#else>
                <#return model["(/bebop:page/nav:categoryMenu[@id='${useCategoryMenu}']//nav:category[@isSelected='true'])[last()]/@title"]>
            </#if>
        <#else>
            <#return ''>
        </#if>
    </#if>

</#function>

<#function getItemSummary item>
    <#if (item["./nav:attribute[./@name='lead']"]?size > 0)>
        <#return item["./nav:attribute[./@name='lead']"]>
    <#elseif (item["./nav:attribute[./@name='summary']"]?size > 0)>
        <#return item["./nav:attribute[./@name='summary']"]>
    <#elseif (item["./lead"]?size > 0)>
        <#return item["./lead"]>
    <#elseif (item["./summary"]?size > 0)>
        <#return item["./summary"]>
    </#if>    
</#function>

<#function getPageDescription item>
    <#if (item["./pageDescription"]?size > 0)>
        <#return item["./pageDescription"]>
    <#elseif (item["./nav:attribute[./@name='pageDescription']"]?size > 0)>
        <#return item["./nav:attribute[./@name='pageDescription']"]>
    </#if>
</#function>

<#function generateContentItemLink oid>
    <#return "${contextPrefix}/redirect/?oid=${oid}">
</#function>
