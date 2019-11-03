<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing contact items.
-->

<#--doc
    Gets the address associated with the provided contact item.

    @param item The model of the contact item to use.

    @return The model of the address item associated with the contact item.
-->
<#function getAddress item>
    <#return item["./address"]>
</#function>

<#--doc
    Gets the person associated with the provided contact item.

    @param item The model of the contact item to use.

    @return The model of the person item associated with the contact item.
-->
<#function getPerson item>
    <#return item["./person"]>
</#function>

<#--doc
    Gets the contact entries of the provided contact item.

    @param item The model of the contact item to use.

    @return A sequence of the models of the contact entries of the contact item.
-->
<#function getContactEntries item>
    <#return item["./contactentries"]>
</#function>

<#--doc
    Gets the specific contact entry by its `keyId`.

    @param item The model of the contact item to use.

    @param keyId The ID of the entry to retrieve.

    @return The model of the contact entry with the provided ID.
-->
<#function getContactEntry item keyId>
    <#return item["./contactentries[keyId='${keyId}']"]>
</#function>

<#--doc
    Gets the label of a contact entry.

    @param entry The model of the contact entry to use.

    @return The label of the contact entry.
-->
<#function getContactEntryLabel entry>
    <#return entry["./key"]>
</#function>

<#--doc
    Gets the key of a contact entry.

    @param entry The model of the contact entry to use.

    @return The key of the contact entry.
-->
<#function getContactEntryKey entry>
    <#return entry["./key"]>
</#function>

<#--doc
    Gets the value of a contact entry.

    @param entry The model of the contact entry to use.

    @return The value of the contact entry.
-->
<#function getContactEntryValue entry>
    <#return entry["./value"]>
</#function>

