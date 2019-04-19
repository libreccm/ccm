<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#import ../ccm-cms.ftl as CMS>

<#function getHref proceedings>
    <#return CMS.generateContentItemLink(proceedings["./@oid"])>
</#function>

<#function getPaperHref paper>
    <#return CMS.generateContentItemLink(paper["./@oid"])>
</#function>