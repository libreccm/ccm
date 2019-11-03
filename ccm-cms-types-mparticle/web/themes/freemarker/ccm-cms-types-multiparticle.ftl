<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing MultiPartArticles.
-->

<#--doc
    Get the summary of a multi part article.

    @param item The model of the multi part article to use.

    @return The summary of the multi part article.
-->
<#function getSummary item>
    <#if (item["./summary"]?size > 0)>
        <#return item["./summary"]>
    <#elseif (item["./nav:attribute[@name = 'summary']"]?size > 0)>
        <#return item["./nav:attribute[@name = 'summary']"]>
    <#else>
        <#return "">
    </#if>
</#function>

<#--doc
    Get the visible sections of a multi part article.

    @param item The model of the multi part article to use.

    @return The visible sections of the multi part article.
-->
<#function getSections item>
    <#return item["./cms:articleSectionPanel/cms:item"]>
</#function>

<#--doc
    Gets the title of a section.

    @param section The model of the section to use, as returned by `getSections`.

    @return The title of the section.
-->
<#function getSectionTitle section>
    <#return section["./title"]>
</#function>

<#--doc
    Gets the content of a section.

    @param section The model of the section to use, as returned by `getSections`.

    @return The content of the section.
-->
<#function getSectionContent section>
    <#return section["./text/content"]>
</#function>

<#--doc
    Gets the rank (sort key) of a section.

    @param section The model of the section to use, as returned by `getSections`.

    @return The rank of the section.
-->
<#function getSectionRank section>
    <#return section["./rank"]>
</#function>

<#--doc
    Gets the number of the current page.

    @param item The model of the multi part article to use.

    @return The number of the current page.
-->
<#function getPageNumber item>
    <#return item["./articleSectionPanel/@pageNumber"]>
</#function>

<#--doc
    Gets the number of the pages.

    @param item The model of the multi part article to use.

    @return The number of the pages.
-->
<#function getNumberOfPages item>
    <#return item["./cms:mpadata/numberOfPages"]>
</#function>

<#--doc
    Determines if a link to the previous page is available.

    @param item The model of the multi part article to use.

    @return `true` if the link is available, `false` otherwise.
-->
<#function hasPreviousPage item>
    <#return (item["./cms:articleSectionPanel/@pageNumber"] != "all" && item["./cms:articleSectionPanel/@pageNumber"]?number > 1)>
</#function>

<#--doc
    Determines if a link to the next page is available.

    @param item The model of the multi part article to use.

    @return `true` if the link is available, `false` otherwise.
-->
<#function hasNextPage item>
    <#return (item["./cms:articleSectionPanel/@pageNumber"] != "all" && item["./cms:articleSectionPanel/@pageNumber"]?number < item["./cms:mpadata/numberOfPages"]?number)>
</#function>

<#--doc
    Determines of the multi part article has multiple pages.

    @param item The model of the multi part article to use.

    @return `true` if the article has multiple pages, `false` otherwise.
-->
<#function hasMultiplePages item>
    <#return (item["./cms:articleSectionPanel/@pageNumber"] != "all") && item["./cms:articleSectionPanel/@pageNumber"] != "1">
</#function>

<#--doc
    Gets the link to the previous page.

    @param item The model of the multi part article to use.

    @return The link to the previous page.
-->
<#function getLinkToPreviousPage item>
    <#return "?page${item['./cms:articleSectionPanel/@pageNumber']?number - 1}">
</#function>

<#--doc
    Gets the link to the next page.

    @param item The model of the multi part article to use.

    @return The link to the next page.
-->
<#function getLinkToNextPage item>
    <#return "?page${item['./cms:articleSectionPanel/@pageNumber']?number + 1}">
</#function>

<#--doc
    Gets the link for showing all sections on one page..

    @param item The model of the multi part article to use.

    @return The link for showing all sections on one page.
-->
<#function getAllSectionsLink item>
    <#return "?page=all">
</#function>

