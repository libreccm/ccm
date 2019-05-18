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
    <#elseif (model["/bebo p:page/nav:customizableObjectList[@id='${listId}']/nav:objectList"]?size > 0)>
        <#--  <pre>Found customizable object list ${listId}</pre>  -->
        <#return model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>
        <#--  <#assign items=model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>  -->
    <#else>
        <#return []>
        <#--  <pre>no item list with id ${listId}</pre>      -->
    </#if>
</#function>

<#function getObjectCount listId>
    <#return model["/bebop:page/*[id='${listId}']/nav:objectList/nav:paginator/@objectCount"]@@text>
</#function>

<#function getPaginatorBaseUrl listId>
    <#assign baseUrl = model["/bebop:page/*[id='${listId}']/nav:objectList/nav:paginator/@baseURL"]>
    <#if (baseUrl?contains("?"))>
        <#return baseUrl + "&">
    <#else>
        <#return baseUrl + "?">
    </#if>
</#function>

<#function getPaginatorBegin listId>
    <#return model["/bebop:page/*[id='${listId}']/nav:objectList/nav:paginator/@objectBegin>
</#function>

<#function getPaginatorEnd listId>
    <#return model["/bebop:page/*[id='${listId}']/nav:objectList/nav:paginator/@objectEnd>
</#function>

<#function getPageCount listId>
    <#return model["/bebop:page/*[id='${listId}']/nav:objectList/nav:paginator/@pageCount>
</#function>

<#function getPageNumber listId>
    <#return model["/bebop:page/*[id='${listId}']/nav:objectList/nav:paginator/@pageNumber>
</#function>

<#function getPageParam listId>
    <#return model["/bebop:page/*[id='${listId}']/nav:objectList/nav:paginator/@pageParam>
</#function>

<#function getPageSize listId>
    <#return model["/bebop:page/*[id='${listId}']/nav:objectList/nav:paginator/@pageSize>
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

<#function getItemLink item>
    <#return item["./nav:path"]>
</#function>

<#function getImageId item>
    <#return item["./nav:attribute[@name='imageAttachments.image.id'][1]"]>
</#function>

<#function getImageUrl item>
    <#return dispatcherPrefix + '/cms-service/stream/image/?image_id=' + getImageId(item)>
</#function>

<#function getImageCaption item>
    <#return item["./nav:attribute[@name='imageAttachments.image.caption'][1]"].@@text>
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
    <#/if>
</#function>

<#function getCategoryFilterCategoryGroups filter>
    <#if filter["./@type"] == "categoryFilter">
        <#return filter["./categories/categoryGroup"]>
    <#else>
        <#return []>
    <#/if>
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
    <#return category["."]@@text>
</#function>


