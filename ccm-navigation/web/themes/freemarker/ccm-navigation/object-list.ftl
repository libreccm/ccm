<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getItems listId>
    <#if (model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList"]?size > 0)>
        <#--  <#assign items=model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>  -->
        <#--  <pre>Found simple object list ${listId}</pre>
        <pre></pre>  -->
        <#return model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>
    <#elseif (model["/bebop:page/nav:complexObjectList[@id='${listId}']/nav:objectList"]?size > 0)>
        <#--  <pre>Found complex object list ${listId}</pre>  -->
        <#return model["/bebop:page/nav:complexObjectList[@id='${listId}']/nav:objectList/nav:item"]>
        <#--  <#assign items=model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>  -->
    <#elseif (model["/bebop:page/nav:customizableObjectList[@id='${listId}']/nav:objectList"]?size > 0)>
        <#--  <pre>Found customizable object list ${listId}</pre>  -->
        <#return model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>
        <#--  <#assign items=model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>  -->
    <#else>
        <#return []>
        <#--  <pre>no item list with id ${listId}</pre>      -->
    </#if>
</#function>

<#function getObjectCount listId>
    <#if (model["/bebop:page/*[@id='${listId}']/nav:noContent"]?size > 0)>
        <#return 0>
    <#elseif (model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@objectCount"]?size > 0)>
        <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@objectCount"]?number>
    <#elseif (model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/count"]?size > 0)>
        <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/count"]?number>
    <#else>
        <#return 0>
    </#if>
</#function>

<#function getPaginatorBaseUrl listId>
    <#assign baseUrl = model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@baseURL"]>
    <#if (baseUrl?contains("?"))>
        <#return baseUrl + "&">
    <#else>
        <#return baseUrl + "?">
    </#if>
</#function>

<#function getPaginatorBegin listId>
    <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@objectBegin"]?number>
</#function>

<#function getPaginatorEnd listId>
    <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@objectEnd"]?number>
</#function>

<#function getPageCount listId>
    <#if (model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@pageCount"]?size == 1)>
        <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@pageCount"]?number>
    <#else>
        <#return 0>
    </#if>
    
</#function>

<#function getPageNumber listId>
    <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@pageNumber"]?number>
</#function>

<#function getPageParam listId>
    <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@pageParam"]>
</#function>

<#function getPageSize listId>
    <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@pageSize"]?number>
</#function>

<#function getPrevPageLink listId>
    <#return getPaginatorBaseUrl(itemId) + getPageParam(listId) + "="  + (getPageNumber(listId) - 1)>
</#function>

<#function getNextPageLink listId>
    <#return getPaginatorBaseUrl(itemId) + getPageParam(listId) + "="  + (getPageNumber(listId) + 1)>
</#function>

<#function getFirstPageLink listId>
    <#return getPaginatorBaseUrl(itemId) + getPageParam(listId) + "=1">
</#function>

<#function getLastPageLink listId>
    <#return getPaginatorBaseUrl(itemId) + getPageParam(listId) + "=" + getPageCount(itemId)>
</#function>

<#function getItemTitle item>
    <#return item["/nav:attribute[@name='title']"]>
</#function>

<#function getItemLink item>
    <#if (item["./nav:path"]?size > 0)>
        <#return item["./nav:path"].@@text>
    <#else>
        <#return "/redirect/?oid=" + item["./@oid"]>
    </#if>
</#function>

<#function getItemLead item>
    <#return item["./nav:attribute[@name='lead']"]>
</#function>

<#function getItemProperty item property>
    <#return item["./nav:atttribute[@name='${property}']"]>
</#function>

<#function hasImage item>
    <#return (item["./nav:attribute[@name='imageAttachments.image.id']"]?size > 0 || item["./imageAttachments"]?size > 0)>
</#function>

<#function getImageId item>
    <#if (item["./nav:attribute[@name='imageAttachments.image.id']"]?size > 0)>
        <#return item["./nav:attribute[@name='imageAttachments.image.id'][1]"]>
    <#elseif (item["./imageAttachments"]?size > 0)>
        <#return item["./imageAttachments[1]/image/id"].@@text>
    </#if>
</#function>

<#function getImageUrl item>
    <#return dispatcherPrefix + '/cms-service/stream/image/?image_id=' + getImageId(item)>
</#function>

<#function getImageCaption item>
    <#if (item["./nav:attribute[@name='imageAttachments.image.id']"]?size > 0)>
        <#return item["./nav:attribute[@name='imageAttachments.image.caption'][1]"].@@text>
    <#elseif (item["./imageAttachments"]?size > 0)>
        <#return item["./imageAttachments[1]/image/caption"].@@text>
    </#if>
</#function>

<#function getFilters listId>
    <#if (model["/bebop:page/nav:simpleObjectList[@id='${listId}']/filterControls"]?size > 0)>
        <#return model["/bebop:page/nav:simpleObjectList[@id='${listId}']//filterControls/filters/filter"]>
    <#elseif (model["/bebop:page/nav:complexObjectList[@id='${listId}']//filterControls"]?size > 0)>
        <#return model["/bebop:page/nav:complexObjectList[@id='${listId}']/nav:objectList/filters/filter"]>
    <#elseif (model["/bebop:page/nav:customizableObjectList[@id='${listId}']//filterControls"]?size > 0)>
        <#return model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/filters/filter"]>
    <#else>
        <#return []>
    </#if>
</#function>

<#function getFilterLabel filter>
    <#return filter["./@label"]>
</#function>

<#function getFilterType filter>
    <#return filter["./@type"]>
</#function>

<#function getSelectFilterOptions filter>
    <#if (filter["./@type"] == "select")>
        <#return filter["./option"]>
    <#else>
        <#return []>
    </#if>
</#function>

<#function getSelectFilterSelection filter>
    <#return filter["./selected"]>
</#function>

<#function getSelectFilterOptionLabel option>
    <#return option["./label"]>
</#function>

<#function getCategoryFilterSearchString filter>
    <#if filter["./@type"] == "categoryFilter">
        <#return filter["./searchString"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#function getCategoryFilterSeparator filter>
    <#if filter["./@type"] == "categoryFilter">
        <#return filter["./separator"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#function getCategoryFilterMultiple filter>
    <#if filter["./@type"] == "categoryFilter">
        <#return filter["./multiple"] == "true">
    <#else>
        <#return false>
    </#if>
</#function>

<#function getCategoryFilterCategories filter>
    <#if filter["./@type"] == "categoryFilter">
        <#return filter["./categories/categories"]>
    <#else>
        <#return []>
    </#if>
</#function>

<#function getCategoryFilterCategoryGroups filter>
    <#if filter["./@type"] == "categoryFilter">
        <#return filter["./categories/categoryGroup"]>
    <#else>
        <#return []>
    </#if>
</#function>

<#function getCategoryGroupLabel group>
    <#return group["./label"]>
</#function>

<#function getCategoryFilterCategoryGroupsCategories groups>
   
    <#return group["./categories/categoryGroup/category"]>
   
</#function>

<#function getCategoryFilterCategoryId category>
    <#return category["./@id"]>
</#function>

<#function getCategoryFilterCategoryLabel category>
    <#return category.@@text>
</#function>


