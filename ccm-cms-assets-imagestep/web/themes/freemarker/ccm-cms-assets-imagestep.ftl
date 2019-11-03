<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Provides functions for processing the image attachemnts of a content item.
-->

<#--doc
    Creates a sorted sequence of hashes (see Freemarker docuementation) for the 
    image attachments of a content item.

    @param item The model of the content item to use.

    @return A sequence of hashes. The following keys are available in each hash:
    * `imageId`: The ID of the image.
    * `name`: The name of the image file.
    * `caption`: The caption of the image.
    * `sortKey`: The sort key of the image.
    * `width`: The orginal width of the image.
    * `height`: The height width of the image.
    * `imageUrl`: The URL of the image.
-->
<#function getImageAttachments item>

    <#return item["./imageAttachments"]?map(
        image -> {
            "imageId": image["./image/id"].@@text,
            "name": image["./image/name"].@@text,
            "caption": image["./caption"].@@text, 
            "sortKey": image["./sortKey"].@@text?number,
            "width": image["./image/width"].@@text,
            "height": image["./image/height"].@@text,
            "imageUrl": dispatcherPrefix + "/cms-service/stream/image/?image_id=" + image["./image/id"].@@text
        }
    )?sort_by("sortKey")>
</#function>

<#--  <#function getImageId image>
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
</#function>  -->
