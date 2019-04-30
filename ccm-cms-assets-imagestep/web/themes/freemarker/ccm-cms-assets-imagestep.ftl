<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getImageAttachments item>
    <#return item["./imageAttachments"]>
</#function>

<#function getImageId image>
    <#return image["./image/id"]>
</#function>

<#function getImageName image>
    <#return image["./image/name"]>
</#function>

<#function getImageCaption image>
    <#return image["./caption"]>
</#function>

<#function getImageSortKey image>
    <#return image["./sortKey"]>
</#function>

<#function getImageWidth image>
    <#return image["./image/width"]>
</#function>

<#function getImageHeight image>
    <#return image["./image/height"]>
</#function>

<#function getImageUrl image>
    <#return dispatcherPrefix + '/cms-service/stream/image/?image_id=' + getImageId(image)>
</#function>
