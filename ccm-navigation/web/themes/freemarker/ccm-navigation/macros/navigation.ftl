<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<%--
    Output all path tokens of the current category path. Provides the 
    following parameters for nested content:
    
    1: title
    2: url
    3: id

    of the category.
--%>
<#macro categoryPath>
    
    <#list model["/bebop:page/nav:categoryPath/nav:category"] as token>
        <#nested token["./@title"], token["./@url"], token["./@id"]>
    </#list>
    
</#macro>

<%--
    Provides the following data about the current navigation instance to 
    the nested content:

    1. ID of the navigation menu from which the data was retrieved
    2. URL to the root of the navigation
    3. title of the navigation
--%>
<#macro navigationRoot navId="categoryMenu">

    <assign urlPath="/bebop:page/nav:categoryMenu[@id='${navId}']/nav:category/@url">
    <assign titlePath="/bebop:page/nav:categoryMenu[@id='${navId}']/nav:category/@title">

    <#assign url=model["/bebop:page/nav:categoryMenu[@id='${navId}']/nav:category/@url"]>
    <#assign title=model["/bebop:page/nav:categoryMenu[@id='categoryMenu']/nav:category/@title"]>

    <#nested navId, url, title> 

</#macro>
