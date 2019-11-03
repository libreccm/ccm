<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import "../ccm-cms/content-item.ftl" as ContentItem>

<#--filedoc
    Functions for publications of the types ArticleInCollectedVolume.
    ArticleInJournal and InProceedings.
-->

<#--doc
    Constructs the link to the detail view for the article.

    @param article The article to use.

    @return The link to the detail view of the article.
-->
<#function getHref article>
    <#return ContentItem.generateContentItemLink(article["./@oid"])>
</#function>
