<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#macro objectList listId="itemList">

    <#if (model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList"]?size > 0)>
        <#--  <#assign items=model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>  -->
        <#--  <pre>Found simple object list ${listId}</pre>
        <pre></pre>  -->
        <#nested model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>
    <#elseif (model["/bebop:page/nav:complexObjectList[@id='${listId}']/nav:objectList"]?size > 0)>
        <#--  <pre>Found complex object list ${listId}</pre>  -->
        <#nested model["/bebop:page/nav:complexObjectList[@id='${listId}']/nav:objectList/nav:item"]>
        <#--  <#assign items=model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>  -->
    <#elseif (model["/bebop:page/nav:customizableObjectList[@id='${listId}']/nav:objectList"]?size > 0)>
        <#--  <pre>Found customizable object list ${listId}</pre>  -->
        <#nested model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>
        <#--  <#assign items=model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>  -->
    <#else>
        <#--  <pre>no item list with id ${listId}</pre>      -->
    </#if>

    <#-- <#nested items> -->

</#macro>

<#function itemLink item>
    <#return item["./nav:path"]>
</#function>

<#function funImageId item>
    <#return item["./nav:attribute[@name='imageAttachments.image.id'][1]"]>
</#function>

<#function funImageCaption item>
    <#return item["./nav:attribute[@name='imageAttachments.image.caption'][1]"].@@text>
</#function>
