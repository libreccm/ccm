<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getArtist item>
    <#if (item["./artist"]?size > 0)>
        <#return item["./artist"]>
    <#else if (item["./nav:attribute[@name = 'artist']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'artist']"]
    </#if>
</#function>

<#function getCopyright item>
    <#if (item["./copyright"]?size > 0)>
        <#return item["./copyright"]>
    <#else if (item["./nav:attribute[@name = 'copyright']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'copyright']"]
    </#if>
</#function>

<#function getDescription item>
    <#if (item["./description"]?size > 0)>
        <#return item["./description"]>
    <#else if (item["./nav:attribute[@name = 'description']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'description']"]
    </#if>
</#function>

<#function getLicense item>
    <#if (item["./license"]?size > 0)>
        <#return item["./license"]>
    <#else if (item["./nav:attribute[@name = 'license']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'license']"]
    </#if>
</#function>

<#function getMaterial item>
    <#if (item["./material"]?size > 0)>
        <#return item["./material"]>
    <#else if (item["./nav:attribute[@name = 'material']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'material']"]
    </#if>
</#function>

<#function getPublishDate item>
    <#if (item["./publishDate"]?size > 0)>
        <#return item["./publishDate"]>
    <#else if (item["./nav:attribute[@name = 'publishDate']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'publishDate']"]
    </#if>
</#function>

<#function getWidth item>
    <#if (item["./images/width"]?size > 0)>
        <#return item["./image/width"]>
    <#else if (item["./nav:attribute[@name = 'image-width']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'image-width']"]
    </#if>
</#function>

<#function getHeight item>
    <#if (item["./images/height"]?size > 0)>
        <#return item["./images/height"]>
    <#else if (item["./nav:attribute[@name = 'image-height']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'image-height']"]
    </#if>
</#function>

<#function getMainText item>
    <#return item["./textAsset/content"]>
</#function>

<#function getOrigin item>
    <#if (item["./origin"]?size > 0)>
        <#return item["./origin"]>
    <#else if (item["./nav:attribute[@name = 'origin']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'origin']"]
    </#if>
</#function>

<#function getOriginalSize item>
    <#if (item["./origSize"]?size > 0)>
        <#return item["./origSize"]>
    <#else if (item["./nav:attribute[@name = 'origSize']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'origSize']"]
    </#if>
</#function>

<#function getUrl item>
    <#return "${contextPrefix}/cms-service/stream/image/?image_id=${getImageId(item)}">
</#function>

<#function getCaption item>
    <#return item["./caption"]>
</#function>

<#function getImageId item>
    <#return item["./image/id"]>
</#function>

<#function getThumbnailId item>
    <#return item["./thumbnail/id"]>
</#function>

<#function getThumbnailWidth item>
    <#return item["./thumbnail/width"]>
</#function>

<#function getThumbnailHeight item>
    <#return item["./thumbnail/height"]>
</#function>

<#function getSite item>
    <#return item["./site"]>
</#function>

<#function getSource item>
    <#return item["./source"]>
</#function>

<#function getTechnique item>
    <#return item["./technique"]>
</#function>