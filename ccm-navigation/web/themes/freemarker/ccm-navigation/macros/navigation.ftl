<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--
    Output all path tokens of the current category path. Provides the 
    following parameters for nested content:
    
    1: title
    2: url
    3: id

    of the category.
-->
<#macro categoryPath>
    
    <#list model["/bebop:page/nav:categoryPath/nav:category"] as token>
        <#nested token["./@title"], token["./@url"], token["./@id"]>
    </#list>
    
</#macro>

<#--
    Provides the following data about the current navigation instance to 
    the nested content:

    1. ID of the navigation menu from which the data was retrieved
    2. URL to the root of the navigation
    3. title of the navigation
-->
<#macro navigationRoot navId="categoryMenu">

    <#assign url=model["/bebop:page/nav:categoryMenu[@id='${navId}']/nav:category/@url"]>
    <#assign title=model["/bebop:page/nav:categoryMenu[@id='categoryMenu']/nav:category/@title"]>

    <#nested navId, url, title> 

</#macro>

<#--
    Checks if the a categoryMenu is available in the model and shows the 
    nested content if one is available. The optional parameter 
    menuId can be used to select a specific categoryMenu is used. If no value
    value the parameter is provided the default value "categoryMenu" is used.

    The categories are passed to the nested content. To iterate over the 
    categories the "categories" macro can be used.

-->
<#macro categoryMenu menuId="categoryMenu">

    <#if (model["/bebop:page/nav:categoryMenu[@id='${menuId}']"]?size > 0) >
        <#nested model["/bebop:page/nav:categoryMenu[@id='${menuId}']/nav:category/nav:category"]>
    </#if>

    <#--
    <#assign categories=model["/bebop:page/nav:categoryMenu[@id='${menuId}']/nav:category/nav:category"]>

    <#list categories as category>
        <#nested category["./@id"], category["./@url"], category["./@title"], category>
    </#list>
    -->

</#macro>

<#--
    Checks if an categoryHierachy is avialable in the model and shows the 
    nested content if one is avialable. The optional parameter hierarchyId
    can be used to select a specific categoryHierarchy. If no value for the 
    parameter is provided the default value "categoryNav" is used.
-->
<#macro categoryHierarchy hierarchyId="categoryNav">

    <#if (model["/bebop:page/nav:categoryHierarchy[@id='${hierarchyId}']"]?size > 0)>
        <#nested model["/bebop:page/nav:categoryHierarchy[@id='${hierarchyId}']/nav:category"]>
    </#if>

    <#-- 
    <#assign categories=model["/bebop:page/nav:categoryHierarchy[@id='${hierarchyId}']/nav:category"]>

    <#list categories as category>
        <#nested category["./@id"], category["./@url"], category["./@title"], category>
    </#list>
    -->

</#macro>

<#--
    Iterates over the provided sequence of categories. For each category 
    the following parameters are passed to the nested content:

    1. ID of the category
    2. URL of the category
    3. Title of the category
    4. The category itself.
-->
<#macro categories categories>

    <#list categories as category>
        <#nested category["./@id"], category["./@url"], category["./@title"], category>
    </#list>

</#macro>

<#macro subCategories ofCategory>

    <#if (ofCategory["./nav:category"]?size > 0)>
        <#nested ofCategory["./nav:category"]>
    </#if>

</#macro>
