<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import ../ccm-cms/ContentItem.ftl as ContentItem>

<#function getHref article>
    <#return ContentItem.generateContentItemLink(article["./@oid"])>
</#function>
