<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for accessing the properties of the content item of type person
    or of subtype of person.
-->

<#--doc
    Get the surname of the provided person item.

    @param item The item from which the property is read.

    @return The value of the `surname` property of the provided item.
-->
<#function getSurname item>
    <#return item["./surname"]>
</#function>

<#--doc
    Get the given name of the provided person item.

    @param item The item from which the property is read.

    @return The value of the `givenname` property of the provided item.
-->
<#function getGivenName item>
    <#return item["./givenname"]>
</#function>

<#--doc
    Get the name prefix of the provided person item.

    @param item The item from which the property is read.

    @return The value of the `titlepre` property of the provided item.
-->
<#function getTitlePre item>
    <#return item["./titlepre"]>
</#function>

<#--doc
    Get the name suffix of the provided person item.

    @param item The item from which the property is read.

    @return The value of the `titlepost` property of the provided item.
-->
<#function getTitlePost item>
    <#return item["./titlepost"]>
</#function>

<#--doc
    Get the link to the homepage from the contact data of the provided person 
    item.

    @param item The item from which the property is read.

    @param contactType The type of the contact to use.

    @param entry The contact entry providing the link to the homepage.

    @return The link to the homepage.
-->
<#function getHomepageLink item contactType="commonContact" entry="homepage">
    <#return item["./contacts/contact[./@contactType='${contactType}']/contactentries[./keyId='${entry}']/value"]>
</#function>

<#--doc
    Retrieve (the model) of the an contact item associated with the provided
    person item.

    @param item The person item.

    @param contactType The type of the contact to retrieve.

    @return The model of the associated contact item.
-->
<#function getContact item contactType="commonContact">
    <#return item["./contacts/contact[@contactType='${contactType}']"]>
</#function>

<#--doc
    Retreives the contact entries of a contact associated with a person item.

    @param item The person item.

    @param contactType The type of the contact to retrieve.

    @return The models of the contact entries of the associated contact item.
-->
<#function getContactEntries item contactType="commonContact">
    <#return item["./contacts/contact[@contactType='${contactType}']/contactentries"]>
</#function>

<#--doc
    Retreives the address item associated with the contact associated with the 
    provided person item.

    @param item The person item.

    @param contactType The type of the contact to retrieve.

    @return The model of the assocaiated address item.
-->
<#function getAddress item contactType="commonContact">
    <#return item["./contacts/contact[@contactType='${contactType}']/address"]>
</#function>
