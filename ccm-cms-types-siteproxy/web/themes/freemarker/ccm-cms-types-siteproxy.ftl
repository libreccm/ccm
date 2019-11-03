<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for Site Proxy items.
-->

<#--doc
    Gets the content of the site proxy item.

    @param item The site proxy item to use.

    @return The content of the site proxy item.
-->
<#function getContent item>
    <#return item["./cms:siteProxyPanel/*"]>
</#function>