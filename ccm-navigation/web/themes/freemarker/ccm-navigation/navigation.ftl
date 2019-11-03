<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing the data of the components provided by 
    ccm-navigation.
-->

<#--doc 
    Get all categories from the category path.

    @return All categories in the current category path.
-->
<#function getCategoryPath>
    <#return model["/bebop:page/nav:categoryPath/nav:category"]>
</#function>

<#--doc
    Determines if the current page is the root page of a navigation.

    @return `true` if teh current page is a root page, `false` otherwise.
-->
<#function isRootPage>
    <#return (model["/bebop:page/nav:categoryPath/nav:category"]?size <= 1)>
</#function>

<#--doc
    Gets the currently selected category.

    @return The currently selected category.
-->
<#function getSelectedCategory>
    <#return model["/bebop:page/nav:categoryPath/nav:category[last()]"]>
</#function>

<#--doc
    Gets the ID of the currently selected category.

    @return The ID of the currently selected category.
-->
<#function getSelectedCategoryId>
    <#return model["/bebop:page/nav:categoryPath/nav:category[last()]/@id"]>
</#function>

<#--doc
    Get the title of the provided category.

    @param The model of a category as returned by several functions in this 
    library.

    @return The title of the category.
-->
<#function getCategoryTitle category>
    <#return category["./@title"]>
</#function>

<#--
    Get the URL of the provided category.

    @param The model of a category as returned by several functions in this 
    library.

    @return The URL of the category.
-->
<#function getCategoryUrl category>
    <#return category["./@url"]>
</#function>

<#--
    Get the ID get the provided category.

    @param The model of a category as returned by several functions in this 
    library.

    @return The ID of the provided category.
-->
<#function getCategoryId category>
    <#return category["./@id"]>
</#function>

<#--doc
    Determines of the provided category is selected.

    @param The model of a category as returned by several functions in this 
    library.

    @return `true` if the category is selected, `false` if not.
-->
<#function isCategorySelected category>
    <#return (category["./@isSelected='true'"] == true)>
</#function>

<#--doc
    Get the URL of the root category of the navigation with the provided id.

    @param navigationId The ID of the navigation system to use.

    @return The URL of the root category of the navigation system with the 
    provided ID.
-->
<#function getNavigationRootUrl navigationId="categoryMenu">
    <#return model["/bebop:page/nav:categoryMenu[@id='${navigationId}']/nav:category/@url"]>
</#function>

<#--doc
    Get title of the navigation with the provided id.

    @param navigationId The ID of the navigation system to use.

    @return The title of the navigation.
-->
<#function getNavigationTitle navigationId="categoryMenu">
    <#return model["/bebop:page/nav:categoryMenu[@id='${navigationId}']/nav:category/@title"]>
</#function>

<#--doc
    Retrieves the first level of categories from the category menu with the provided ID. 
    If no id is provided "categoryMenu" is used.

    @param menuId The ID of the category menu to use.

    @return The first level of categories in the menu.
-->
<#function getCategoryMenu menuId="categoryMenu">
    <#return model["/bebop:page/nav:categoryMenu[@id='${menuId}']/nav:category/nav:category"]>
</#function>

<#--doc
    Retrieves the first level of categories from the category hierachy with the 
    provided ID. If no id is provided 'categoryNav' is used.

    @param hierarchyId The ID of the category hierachy to use.

    @return The first level of categories in the hierarchy.
-->
<#function getCategoryHierarchy hierarchyId="categoryNav">
    <#return model["/bebop:page/nav:categoryHierarchy[@id='${hierarchyId}']/nav:category"]>
</#function>

<#--doc
    Gets the subcategories of the provided category.

    @param ofCategory The model of the category.

    @return The sub categories of the provided category.
-->
<#function getSubCategories ofCategory>
    <#return ofCategory["./nav:category"]>
</#function>

<#--doc
    Gets the subcategories of the category with the provided id.

    @param categoryId The ID of the category to use.

    @return The sub categories of the category with the provided ID.
-->
<#function getSubCategoriesOfCategoryWithId categoryId>
    <#return model["/bebop:page/nav:categoryMenu//nav:category[@id=${categoryId}]/nav:category"]>
</#function>

<#--doc
    Gets the greeting/index item of the current navigation page. The returned
    model can be processed with usual functions for processing content items.

    @return The model of the index item.
-->
<#function getGreetingItem>    
    <#return model["/bebop:page/nav:greetingItem/cms:item"]>
</#function>