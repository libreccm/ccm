<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing member lists.
-->

<#--doc
    Retrieve a member list from the model

    @param listId The ID of the list to retrieve.

    @return The model of the member list.
-->
<#function getSciMemberList listId="itemList">
    <#return model["/bebop:page/nav:sci-member-list[@id='${listId}']"]>
</#function>

<#--doc
    Get the members from a list

    @param list The list model as returned by `getSciMemberList`.

    @return A sequence with the member models.
-->
<#function getMembers list>
    <#return list["./member"]>
</#function>

<#--doc
    Get the current value of the surname filter.

    @param list The list model as returned by `getSciMemberList`.

    @return The value of the surname filter list.
-->
<#function getSurnameFilterValue list>
    <#return list["./filters/surname"]>
</#function>

<#--doc
    Get the number of members in the list.

    @param list The list model as returned by `getSciMemberList`.

    @return The number of members in the list.
-->
<#function getCount list>
    <#return list["./paginator/@count"]>
</#function>

<#--doc
    Get the current page of the list.

    @param list The list model as returned by `getSciMemberList`.

    @return The current page of the list.
-->
<#function getCurrentPage list>
    <#return list["./paginator/@currentPage"]>
</#function>

<#--doc
    Gets the maximum number of items per page..

    @param list The list model as returned by `getSciMemberList`.

    @return The maximum member of items per page.
-->
<#function getLimit list>
    <#return list["./paginator/@limit"]>
</#function>

<#--doc
    Gets the number of pages of the list.

    @param list The list model as returned by `getSciMemberList`.

    @return The number of pages of the list.
-->
<#function getMaxPages list>
    <#return list["./paginator/@maxPages"]>
</#function>

<#--doc
    Gets the link to the next page of the list.

    @param list The list model as returned by `getSciMemberList`.

    @return The link to the next page of the list.
-->
<#function getNextPageLink list>
    <#return list["./paginator/@nextPageLink"]>
</#function>

<#--doc
    Gets the link to the previous page of the list.

    @param list The list model as returned by `getSciMemberList`.

    @return The link to the previous page of the list.
-->
<#function getPrevPageLink list>
    <#return list["./paginator/@prevPageLink"]>
</#function>

<#--doc
    Gets the index to the first visible item.

    @param list The list model as returned by `getSciMemberList`.

    @return The index of the first visible item.
-->
<#function getOffset list>
    <#return list["./paginator/@offset"]>
</#function>

<#--doc
    Gets the ID of the member.

    @param item The model of the member item to use (as returned by `getMembers`)

    @return The ID of the provided member item.
-->
<#function getMemberItemId item>
    <#return item["./item-id"]>
</#function>

<#--doc
    Gets the name of the member.

    @param item The model of the member item to use (as returned by `getMembers`)

    @return The name of the provided member item.
-->
<#function getMemberItemName item>
    <#return item["./name"]>
</#function>

<#--doc
    Gets the title of the member.

    @param item The model of the member item to use (as returned by `getMembers`)

    @return The title of the provided member item.
-->
<#function getMemberItemTitle item>
    <#return item["./title"]>
</#function>

<#function getMemberItemSurname item>
    <#return item["./surname"]>
</#function>

<#--doc
    Gets the given name of the member.

    @param item The model of the member item to use (as returned by `getMembers`)

    @return The given name of the provided member item.
-->
<#function getMemberItemGivenName item>
    <#return item["./givenname"]>
</#function>

<#--doc
    Gets the name prefix of the member.

    @param item The model of the member item to use (as returned by `getMembers`)

    @return The name prefix of the provided member item.
-->
<#function getMemberItemTitlePre item>
    <#return item["./title-pre"]>
</#function>

<#--doc
    Gets the name suffix of the member.

    @param item The model of the member item to use (as returned by `getMembers`)

    @return The name suffix of the provided member item.
-->
<#function getMemberItemTitlePost item>
    <#return item["./title-post"]>
</#function>

<#--doc
    Gets the contact entries of the member.

    @param item The model of the member item to use (as returned by `getMembers`)

    @return The contact entries of the provided member item.
-->
<#function getMemberItemContactEntries item>
    <#return item["./contact-entries"]>
</#function>

<#--doc
    Get the contact entry with the specified key.

     @param item The model of the member item to use (as returned by `getMembers`).

     @param key The key of the contact entry.

     @return The contact entry.
-->
<#function getMemberItemContactEntry item key>
    <#return item["./contact-entries/contact-entry[./@key='${key}']"]>
</#function>