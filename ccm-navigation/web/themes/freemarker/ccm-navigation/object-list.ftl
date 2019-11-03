<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for accessing objects lists and their properties.
-->

<#--doc
    Retrieve the item (models) of an object list. This function can deal with 
    several different types of object lists, including `SimpleObjectList`, 
    `ComplexObjectList` and `CustomziableObjectList`.

    @param listId The ID of the object list from the the items are retrieved.

    @return The models for the entries in the object list. If no list with 
    the provided `listId` is found and empty sequence is returned.
-->
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
        <#return model["/bebop:page/nav:customizableObjectList[@id='${listId}']/nav:objectList/nav:item"]>
        <#--  <#assign items=model["/bebop:page/nav:simpleObjectList[@id='${listId}']/nav:objectList/nav:item"]>  -->
    <#else>
        <#return []>
        <#--  <pre>no item list with id ${listId}</pre>      -->
    </#if>
</#function>

<#--doc
    Gets the number of objects/items in an object list.

    @param listId The ID of the object list to use.

    @return The number of objects in the list. If no list with the provided
    `listId` is found `0` is returned.
-->
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

<#--doc
    Gets the base URL for the paginator of an object list.

    @param listId The ID of the object list to use.

    @return The base URL for the the paginator.
-->
<#function getPaginatorBaseUrl listId>
    <#assign baseUrl = model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@baseURL"]>
    <#if (baseUrl?contains("?"))>
        <#return baseUrl + "&">
    <#else>
        <#return baseUrl + "?">
    </#if>
</#function>

<#--doc
    Gets the index of the first item shown on the current page.

    @param listId The ID of the object list to use.

    @return The index of the first item shown on the current page.
-->
<#function getPaginatorBegin listId>
    <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@objectBegin"]?number>
</#function>

<#--doc
    Gets the index of the last item shown on the curent page.

    @param listId The ID of the object list to use.

    @return The index of the last item shown on the current page.
-->
<#function getPaginatorEnd listId>
    <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@objectEnd"]?number>
</#function>

<#--doc
    Get the number of pages of an object list.

    @param listId The ID of the object list to use.

    @return The number of pages of the object list. If no list with the 
    provided `listId` is found the function will return `0`.
-->
<#function getPageCount listId>
    <#if (model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@pageCount"]?size == 1)>
        <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@pageCount"]?number>
    <#else>
        <#return 0>
    </#if>
    
</#function>

<#--doc
    Gets the number of the current page.

    @param listId The ID of the object list to use.

    @return The number of the page currently shown.
-->
<#function getPageNumber listId>
    <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@pageNumber"]?number>
</#function>

<#--doc
    Gets the name of the page URL parameter.

    @param listId The ID of the object list to use.

    @return The name of the page URL parameter.
-->
<#function getPageParam listId>
    <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@pageParam"]>
</#function>

<#--doc
    Gets the maximum number of items on a page.

    @param listId The ID of the object list to use.

    @return The maxium number of items on a page.
-->
<#function getPageSize listId>
    <#return model["/bebop:page/*[@id='${listId}']/nav:objectList/nav:paginator/@pageSize"]?number>
</#function>

<#--doc
    Gets the link to the previous page.

    @param listId The ID of the object list to use.

    @return The link to the previous page.
-->
<#function getPrevPageLink listId>
    <#return getPaginatorBaseUrl(itemId) + getPageParam(listId) + "="  + (getPageNumber(listId) - 1)>
</#function>

<#--doc
    Gets the link to the next page.

    @param listId The ID of the object list to use.

    @return The link to the next page.
-->
<#function getNextPageLink listId>
    <#return getPaginatorBaseUrl(itemId) + getPageParam(listId) + "="  + (getPageNumber(listId) + 1)>
</#function>

<#--doc
    Gets the link to the first page.

    @param listId The ID of the object list to use.

    @return The link to the first page.
-->
<#function getFirstPageLink listId>
    <#return getPaginatorBaseUrl(itemId) + getPageParam(listId) + "=1">
</#function>

<#--doc
    Gets the link to the last page.

    @param listId The ID of the object list to use.

    @return The link to the last page.
-->
<#function getLastPageLink listId>
    <#return getPaginatorBaseUrl(itemId) + getPageParam(listId) + "=" + getPageCount(itemId)>
</#function>

<#--doc
    Gets the title of an item from an object list.

    @param item The model of the list entry for the item.

    @return The title of the item.
-->
<#function getItemTitle item>
    <#return item["./nav:attribute[@name='title']"]>
</#function>

<#--doc
    Gets the link to the detail view of an item in a list.

    @param item The model of the list entry for the item.

    @return The link to the detail view of the item.
-->
<#function getItemLink item>
    <#if (item["./nav:path"]?size > 0)>
        <#return item["./nav:path"].@@text>
    <#else>
        <#return "/redirect/?oid=" + item["./@oid"]>
    </#if>
</#function>

<#--doc
    Checks if the model of the list entry of an item contains a value for the
    `lead` property.

    @param item The model of the list entry for the item.

    @return `true` if the model provides a value of the `lead` property, 
    `false` if not.
-->
<#function hasItemLead item>
    <#return (item["./nav:attribute[@name='lead']"]?size > 0)>
</#function>

<#--doc
    Gets the value of the `lead` property of an item.

    @param item The model of the list entry for the item.

    @return The value of the `lead` property.
-->
<#function getItemLead item>
    <#return item["./nav:attribute[@name='lead']"]>
</#function>

<#--doc
    A generic function for checking if the model of a list entry for an item
    has a specific property.

    @param item The model of the list entry for the item.

    @param property The name of the property to check for.

    @return `true` If the provided model provides an value for the property, 
    `false` if not.
-->
<#function hasItemProperty item property>
    <#return (item["./nav:attribute[@name='${property}']"]?size > 0)>
</#function>

<#--doc
    A generic function for retrieving the value of an property from the model
    for a list entry for an item.

    @param item The model of the list entry for the item.

    @param property The name of the property to retrieve.

    @return The value of the property.
-->
<#function getItemProperty item property>
    <#return item["./nav:attribute[@name='${property}']"]>
</#function>

<#--doc
    Checks if the provided item has an associated image. 

    @param item The model of the list entry for the item.

    @return `true` if the item has an associated image, `false` if not.
-->
<#function hasImage item>
    <#return (item["./nav:attribute[@name='imageAttachments.image.id']"]?size > 0 || item["./imageAttachments"]?size > 0)>
</#function>

<#--doc
    Get the ID of an associated image.

    @param item The model of the list entry for the item.

    @return The ID of the associated image.
-->
<#function getImageId item>
    <#if (item["./nav:attribute[@name='imageAttachments.image.id']"]?size > 0)>
        <#return item["./nav:attribute[@name='imageAttachments.image.id'][1]"]>
    <#elseif (item["./imageAttachments"]?size > 0)>
        <#return item["./imageAttachments[1]/image/id"].@@text>
    </#if>
</#function>

<#--doc
    Generates the URL for an associated image.

    @param item The model of the list entry for the item.

    @return The URL of the associated image. 
-->
<#function getImageUrl item>
    <#return dispatcherPrefix + '/cms-service/stream/image/?image_id=' + getImageId(item)>
</#function>

<#--doc
    Gets the caption for the associated image.

    @param item The model of the list entry for the item.

    @return The caption for the associated image.
-->
<#function getImageCaption item>
    <#if (item["./nav:attribute[@name='imageAttachments.image.id']"]?size > 0)>
        <#return item["./nav:attribute[@name='imageAttachments.image.caption'][1]"].@@text>
    <#elseif (item["./imageAttachments"]?size > 0)>
        <#return item["./imageAttachments[1]/image/caption"].@@text>
    </#if>
</#function>

<#--doc
    Retrieves the filters for an object list.

    @param listId The ID of the list to use.

    @return The filters for the list. An empty sequence is returned if no list 
    with the provided `listId`  could be found or if the list does not have any filters.
-->
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

<#--doc
    Get the label of a filter.

    @param filter model of a filter as returned by `getFilters`.

    @return The label of the filter.
-->
<#function getFilterLabel filter>
    <#return filter["./@label"]>
</#function>

<#--doc
    Get the type of a filter.

    @param filter model of a filter as returned by `getFilters`.

    @return The type of the filter.
-->
<#function getFilterType filter>
    <#return filter["./@type"]>
</#function>

<#--doc
    Get the options of a select filter.

    @param filter model of a filter as returned by `getFilters`.

    @return The options of the select filter.
-->
<#function getSelectFilterOptions filter>
    <#if (filter["./@type"] == "select")>
        <#return filter["./option"]>
    <#else>
        <#return []>
    </#if>
</#function>

<#--doc
    Get the currently selected option of a select filter.

    @param filter model of a filter as returned by `getFilters`.

    @return The currently selected option.
-->
<#function getSelectFilterSelection filter>
    <#return filter["./selected"]>
</#function>

<#--doc
    The label for an option of a select filter.

    @param filter model of an option as returned by `getSelectFilterOptions`.

    @return The label of the option.
-->
<#function getSelectFilterOptionLabel option>
    <#return option["./label"]>
</#function>

<#--doc
    Gets the search string of an category filter.

    @param filter model of a filter as returned by `getFilters`.

    @return The search string.
-->
<#function getCategoryFilterSearchString filter>
    <#if filter["./@type"] == "categoryFilter">
        <#return filter["./searchString"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#--doc
    Gets the separator for a category filter.

    @param filter The model of a filter as returned by `getFilters`.

    @return The separator for separating the categories in the search string.
-->
<#function getCategoryFilterSeparator filter>
    <#if filter["./@type"] == "categoryFilter">
        <#return filter["./separator"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#--doc
    Determines if a category allows the selection of multiple categories.

    @param filter The model of a filter as returned by `getFilters`.

    @return `true` if the filter allows multiple selections, `false` otherwise.
-->
<#function getCategoryFilterMultiple filter>
    <#if filter["./@type"] == "categoryFilter">
        <#return filter["./multiple"] == "true">
    <#else>
        <#return false>
    </#if>
</#function>

<#--doc
    Gets the selectable categories for a category filter.

    @param filter The model of a filter as returned by `getFilters`.

    @return The selectable categories.
-->
<#function getCategoryFilterCategories filter>
    <#if filter["./@type"] == "categoryFilter">
        <#return filter["./categories/categories"]>
    <#else>
        <#return []>
    </#if>
</#function>

<#--doc
    Gets the groups of a category filter.

    @param filter The model of a filter as returned by `getFilters`.

    @return The groups of a category filter.
-->
<#function getCategoryFilterCategoryGroups filter>
    <#if filter["./@type"] == "categoryFilter">
        <#return filter["./categories/categoryGroup"]>
    <#else>
        <#return []>
    </#if>
</#function>

<#--doc
    Gets the label of a category group.

    @param group The model of a category group as returned by 
    `getCategoryFilterCategoryGroups`.

    @return The label for the group.
-->
<#function getCategoryGroupLabel group>
    <#return group["./label"]>
</#function>

<#--doc
    Gets the categories in a group.

    @param group The model of a category group as returned by 
    `getCategoryFilterCategoryGroups`.

    @return The categories in the group.
-->
<#function getCategoryFilterCategoryGroupsCategories groups>
   
    <#return group["./categories/categoryGroup/category"]>
   
</#function>

<#--doc
    Gets the ID of a category of a category filter.

    @param category The model of a category as returned by 
    `getCategoryFilterCategoryGroupsCategories`.

    @return The ID of the category.
-->
<#function getCategoryFilterCategoryId category>
    <#return category["./@id"]>
</#function>

<#--doc
    Gets the label of a category of a category filter.

    @param category The model of a category as returned by 
    `getCategoryFilterCategoryGroupsCategories`.

    @return The label of the category.
-->
<#function getCategoryFilterCategoryLabel category>
    <#return category.@@text>
</#function>


