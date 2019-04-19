<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#function getSciMemberList listId="itemList">
    <#return model["/bebop:page/nav:sci-member-list[@id='${listId}']"]>
</#function>

<#function getMembers list>
    <#return list["./member"]>
</#function>

<#function getSurnameFilterValue list>
    <#return list["./filters/surname"]>
</#function>

<#function getCount list>
    <#return list["./paginator/@count"]>
</#function>

<#function getCurrentPage list>
    <#return list["./paginator/@currentPage"]>
</#function>

<#function getLimit list>
    <#return list["./paginator/@limit"]>
</#function>

<#function getMaxPages list>
    <#return list["./paginator/@maxPages"]>
</#function>

<#function getNextPageLink list>
    <#return list["./paginator/@nextPageLink"]>
</#function>

<#function getPrevPageLink list>
    <#return list["./paginator/@prevPageLink"]>
</#function>

<#function getOffset list>
    <#return list["./paginator/@offset"]>
</#function>

<#function getMemberItemId item>
    <#return item["./item-id"]>
</#function>

<#function getMemberItemName item>
    <#return item["./name"]>
</#function>

<#function getMemberItemTitle item>
    <#return item["./title"]>
</#function>

<#function getMemberItemSurname item>
    <#return item["./surname"]>
</#function>

<#function getMemberItemGivenName item>
    <#return item["./givenname"]>
</#function>

<#function getMemberItemTitlePre item>
    <#return item["./title-pre"]>
</#function>

<#function getMemberItemTitlePost item>
    <#return item["./title-post"]>
</#function>

<#function getMemberItemContactEntries item>
    <#return item["./contact-entries"]>
</#function>

<#function getMemberItemContactEntry item key>
    <#return item["./contact-entries/contact-entry[./@key='${key}']"]>
</#function>