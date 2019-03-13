<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#-- 
    Extracts the image attachments of an item if any and passes them to the 
    nested content. 
#-->
<#macro imageAttachments item>
    <#if (item["./imageAttachments"]?size > 0)>
        <#nested item["./imageAttachments"]>
    </#if>
</#macro>

<#--
    Passes the data of a single image attachments to the nested content. The 
    following data is passed to the nested content:

    * id of the image
    * file name of the image
    * caption of the images
    * sort key of the image
    * width of the image
    * height of the image
-->
<#macro imageAttachment image>
    <#nested image["./image/id"], image["./image/name"], image["./caption"] image["./sortKey"], image["./image/width"], image["./image/height"]> 
</#macro>