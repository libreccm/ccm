<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#-- 
    Get all categories from the category path.
-->
<#function getCategoryPath>
    <#return model["/bebop:page/nav:categoryPath/nav:category"]>
</#function>

<#function isRootPage>
    <#return (model["/bebop:page/nav:categoryPath/nav:category"]?size <= 1)>
</#function>

<#function getSelectedCategory>
    <#return model["/bebop:page/nav:categoryPath/nav:category[last()]"]>
</#function>

<#--
    Get the title of the provided category.
-->
<#function getCategoryTitle category>
    <#return category["./@title"]>
</#function>

<#--
    Get the URL of the provided category.
-->
<#function getCategoryUrl category>
    <#return category["./@url"]>
</#function>

<#--
    Get the ID get the provided category.
-->
<#function getCategoryId category>
    <#return category["./@id"]>
</#function>

<#function isCategorySelected category>
    <#return (category["./@isSelected='true'"] == true)>
</#function>

<#--
    Get the URL of the root category of the navigation with the provided id.
-->
<#function getNavigationRootUrl navigationId="categoryMenu">
    <#return model["/bebop:page/nav:categoryMenu[@id='${navigationId}']/nav:category/@url"]>
</#function>

<#--
    Get title of the navigation with the provided id.
-->
<#function getNavigationTitle navigationId="categoryMenu">
    <#return model["/bebop:page/nav:categoryMenu[@id='${navigationId}']/nav:category/@title"]>
</#function>

<#--
    Retrieves the first level of categories from the category menu with the provided ID. 
    If no id is provided "categoryMenu" is used.
-->
<#function getCategoryMenu menuId="categoryMenu">
    <#return model["/bebop:page/nav:categoryMenu[@id='${menuId}']/nav:category/nav:category"]>
</#function>

<#--
    Retrieves the first level of categories from the category hierachy with the provided ID.
    If no id is provided 'categoryNav' is used.
-->
<#function getCategoryHierarchy hierarchyId="categoryNav">
    <#return model["/bebop:page/nav:categoryHierarchy[@id='${hierarchyId}']/nav:category"]>
</#function>

<#--
    Gets the subcategories of the provided category.
-->
<#function getSubCategories ofCategory>
    <#return ofCategory["./nav:category"]>
</#function>

<#function getGreetingItem>    
    <#return model["/bebop:page/nav:greetingItem/cms:item"]>
</#function>