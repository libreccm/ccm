<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing the special properties of content items of the 
    type address.
-->

<#--doc
    Gets the value of the `address` text property of the provided address item.

    @param item The address item to use.

    @return The value of the `address` property of the provided address item.
-->
<#function getAddressText item>
    <#return item["./address"]>
</#function>

<#--doc
    Gets the value of the `city` property of the provided address item.

    @param item The address item to use.

    @return The value of the `city` property of the provided address item.
-->
<#function getCity item>
    <#return item["./city"]>
</#function>

<#--doc
    Gets the value of the `postalCode` property of the provided address item.

    @param item The address item to use.

    @return The value of the `postalCode` property of the provided address item.
-->
<#function getPostalCode item>
    <#return item["./postalCode"]>
</#function>

<#--doc
    Gets the value of the `state` property of the provided address item.

    @param item The address item to use.

    @return The value of the `state` property of the provided address item.
-->
<#function getState item>
    <#return item["./state"]>
</#function>

<#--doc
    Gets the value of the `country` property of the provided address item.

    @param item The address item to use.

    @return The value of the `country` property of the provided address item.
-->
<#function getCountry item>
    <#return item["./country"]>
</#function>

<#--doc
    Gets the value of the `isoCountryCode` property of the provided address item.

    @param item The address item to use.

    @return The value of the `isoCountryCode` property of the provided address item.
-->
<#function getIsoCountryCode item>
    <#return item["./isoCountryCode"]>
</#function>