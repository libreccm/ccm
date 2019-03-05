<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--
    Passed the content item provided by the model to the nested content if 
    the model provides a content item.
-->
<#macro contentPage>

    <#nested model["/bebop:page/cms:contentPanel/cms:item"]>

</#macro>

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

<#--
    Outputs the title of the provided content item.
-->
<#macro title item>
    <#if (item["./title"]?size > 0)>
        ${item["./title"]}
    <#elseif (item["./nav:attribute[@name='title']"]?size > 0)>
        ${item["./nav:attribute[@name='title']"]}
    </#if>
</#macro>
