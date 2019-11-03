<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing image items.
-->

<#--doc
    Get the artist of the image.

    @param The model of the image item to use.

    @return The value of the `artist` property of the image.
-->
<#function getArtist item>
    <#if (item["./artist"]?size > 0)>
        <#return item["./artist"]>
    <#else if (item["./nav:attribute[@name = 'artist']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'artist']"]
    </#if>
</#function>

<#--doc
    Get the copyright of the image.

    @param The model of the image item to use.

    @return The value of the `copyright` property of the image.
-->
<#function getCopyright item>
    <#if (item["./copyright"]?size > 0)>
        <#return item["./copyright"]>
    <#else if (item["./nav:attribute[@name = 'copyright']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'copyright']"]
    </#if>
</#function>

<#--doc
    Get the description of the image.

    @param The model of the image item to use.

    @return The value of the `description` property of the image.
-->
<#function getDescription item>
    <#if (item["./description"]?size > 0)>
        <#return item["./description"]>
    <#else if (item["./nav:attribute[@name = 'description']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'description']"]
    </#if>
</#function>

<#--doc
    Get the license of the image.

    @param The model of the image item to use.

    @return The value of the `license` property of the image.
-->
<#function getLicense item>
    <#if (item["./license"]?size > 0)>
        <#return item["./license"]>
    <#else if (item["./nav:attribute[@name = 'license']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'license']"]
    </#if>
</#function>

<#--doc
    Get the material of the image.

    @param The model of the image item to use.

    @return The value of the `material` property of the image.
-->
<#function getMaterial item>
    <#if (item["./material"]?size > 0)>
        <#return item["./material"]>
    <#else if (item["./nav:attribute[@name = 'material']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'material']"]
    </#if>
</#function>

<#--doc
    Get the publish date of the image.

    @param The model of the image item to use.

    @return The value of the `publishDate` property of the image.
-->
<#function getPublishDate item>
    <#if (item["./publishDate"]?size > 0)>
        <#return item["./publishDate"]>
    <#else if (item["./nav:attribute[@name = 'publishDate']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'publishDate']"]
    </#if>
</#function>

<#--doc
    Get the width of the image.

    @param The model of the image item to use.

    @return The value of the `width` property of the image.
-->
<#function getWidth item>
    <#if (item["./images/width"]?size > 0)>
        <#return item["./image/width"]>
    <#else if (item["./nav:attribute[@name = 'image-width']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'image-width']"]
    </#if>
</#function>

<#--doc
    Get the height of the image.

    @param The model of the image item to use.

    @return The value of the `height` property of the image.
-->
<#function getHeight item>
    <#if (item["./images/height"]?size > 0)>
        <#return item["./images/height"]>
    <#else if (item["./nav:attribute[@name = 'image-height']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'image-height']"]
    </#if>
</#function>

<#--doc
    Get the main text of the image.

    @param The model of the image item to use.

    @return The value of the `main text` property of the image.
-->
<#function getMainText item>
    <#return item["./textAsset/content"]>
</#function>

<#--doc
    Get the origin of the image.

    @param The model of the image item to use.

    @return The value of the `origin` property of the image.
-->
<#function getOrigin item>
    <#if (item["./origin"]?size > 0)>
        <#return item["./origin"]>
    <#else if (item["./nav:attribute[@name = 'origin']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'origin']"]
    </#if>
</#function>

<#--doc
    Get the original size of the image.

    @param The model of the image item to use.

    @return The value of the `originalSize` property of the image.
-->
<#function getOriginalSize item>
    <#if (item["./origSize"]?size > 0)>
        <#return item["./origSize"]>
    <#else if (item["./nav:attribute[@name = 'origSize']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'origSize']"]
    </#if>
</#function>

<#--doc
    Get the URL of the image.

    @param The model of the image item to use.

    @return The value of the `url` property of the image.
-->
<#function getUrl item>
    <#return "${contextPrefix}/cms-service/stream/image/?image_id=${getImageId(item)}">
</#function>

<#--doc
    Get the caption of the image.

    @param The model of the image item to use.

    @return The value of the `caption` property of the image.
-->
<#function getCaption item>
    <#return item["./caption"]>
</#function>

<#--doc
    Get the ID of the image.

    @param The model of the image item to use.

    @return The value of the `id` property of the image.
-->
<#function getImageId item>
    <#return item["./image/id"]>
</#function>

<#--doc
    Get the thumbnail id of the image.

    @param The model of the image item to use.

    @return The value of the `thumbnail/id` property of the image.
-->
<#function getThumbnailId item>
    <#return item["./thumbnail/id"]>
</#function>

<#--doc
    Get the width of the thumbnail of the image.

    @param The model of the image item to use.

    @return The width of the thumbnail of the image.
-->
<#function getThumbnailWidth item>
    <#return item["./thumbnail/width"]>
</#function>

<#--doc
    Get the height of the thumbnail of the image.

    @param The model of the image item to use.

    @return The height of the thumbnail of the image.
-->
<#function getThumbnailHeight item>
    <#return item["./thumbnail/height"]>
</#function>

<#--doc
    Get the site of the image.

    @param The model of the image item to use.

    @return The value of the `site` property of the image.
-->
<#function getSite item>
    <#return item["./site"]>
</#function>

<#--doc
    Get the source of the image.

    @param The model of the image item to use.

    @return The value of the `source` property of the image.
-->
<#function getSource item>
    <#return item["./source"]>
</#function>

<#--doc
    Get the technique of the image.

    @param The model of the image item to use.

    @return The value of the `technique` property of the image.
-->
<#function getTechnique item>
    <#return item["./technique"]>
</#function>