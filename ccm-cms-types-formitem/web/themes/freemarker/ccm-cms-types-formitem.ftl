<#ftl ns_prefixes={
"bebop":"http://www.arsdigita.com/bebop/1.0",
"cms":"http://www.arsdigita.com/cms/1.0",
"nav":"http://ccm.redhat.com/navigation",
"ui": "http://www.arsdigita.com/ui/1.0"}
>

<#--filedoc
    Functions for processing form items.
-->

<#--doc
    Gets the description of the form item.

    @param The model of the form items to use.

    @return The description of the form item.
-->
<#function getDescription item>
    <#return item["./form/description"]>
</#function>

<#--doc
    Gets the action of the form item.

    @param The model of the form items to use.

    @return The action of the form item.
-->
<#function getFormAction item>
    <#if (item["./remote"] == "true")>
        <#return item["./remoteURL"]>
    <#else>
        <#return item["./@formAction"]>
    </#if>
</#function>

<#--doc
    Determines if the form item has a honeypot field.

    @param The model of the form items to use.

    @return `true` if the form item has a honeypot, `false` otherwise.
-->
<#function hasHoneypot item>
    <#return (item["./honeypot"]?size > 0)>
</#function>

<#--doc
    Gets the name of the honeypot field of the form item.

    @param The model of the form items to use.

    @return The name of the honeypot field of the form item.
-->
<#function getHoneypotName item>
    <#return item["./honeypot/@name"]>
</#function>

<#--doc
    Determines if the form item has a min time check.

    @param The model of the form items to use.

    @return `true` if the form item has a min time check, `false` otherwise.
-->
<#function hasMinTimeCheck item>
    <#return (item["./minTimeCheck"]?size > 0)>
</#function>

<#--doc
    Gets the value for the min time check of the form item.

    @param The model of the form items to use.

    @return The value for the min time check field of the form item.
-->
<#function getMinTimeCheckValue item>
    <#return item["./minTimeCheck/@generated"]>
</#function>

<#--doc
    Determines if the form item has a visited field.

    @param The model of the form items to use.

    @return `true` if the form item has a visited, `false` otherwise.
-->
<#function hasVisitedField item>
    <#return (item["./remote"] != "true")>
</#function>

<#--doc
    Gets the name of the visited field of the form item.

    @param The model of the form items to use.

    @return The name of the visited field of the form item.
-->
<#function getVisitedFieldName item>
    <#return item["./name"]>
</#function>

<#--doc
    Gets the name of the page state field of the form item.

    @param The model of the form items to use.

    @return The name of the page state field of the form item.
-->
<#function getPageStateFieldName item>
    <#return item["./formbuilder:pageState/bebop:pageState/@name"]>
</#function>

<#--doc
    Gets the value of the page state field of the form item.

    @param The model of the form items to use.

    @return The value of the page state field of the form item.
-->
<#function getPageStateFieldValue item>
    <#return item["./formbuilder:pageState/bebop:pageState/@value"]>
</#function>

<#--doc
    Gets the components for the form.

    @param The model of the form items to use.

    @return A sequence of the models of the compoents of the form item.
-->
<#function getComponents item>
    <#return item["./form/component[(objectType != 'com.arsdigita.formbuilder.Widget' and objectType != 'com.arsdigita.formbuilder.DataDrivenSelect' or (defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSubmit' or defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHidden' or defaultDomainClass = 'com.arsdigita.formbuilder.HiddenIDGenerator')]"]>
</#function>

<#--doc
    Gets the name of the component.

    @param component The model of the component.

    @return The name of the component.
-->
<#function getComponentName component>
    <#return component["./parameterName"]>
</#function>

<#--doc
    Gets the default value of the component.

    @param component The model of the component.

    @return The default value of the component.
-->
<#function getComponentDefaultValue component>
    <#return component["./defaultValue"]>
</#function>

<#--doc
    Gets the description of the component.

    @param component The model of the component.

    @return The description of the component.
-->
<#function getComponentDescription component>
    <#return component["./description"]>
</#function>

<#--doc
    Gets the name of the parameter of the component.

    @param component The model of the component.

    @return The name of the parameter of the component.
-->
<#function getComponentParameterName component>
    <#return component["./parameterName"]>
</#function>

<#--doc
    Gets the type of the component.

    @param component The model of the component.

    @return The type of the component.
-->
<#function getComponentType component>
    <#return component["./defaultDomainClass"]>
</#function>

<#--doc
    Gets the title of a form section.

    @param component The model of form section.

    @return The name of the form section.
-->
<#function getFormSectionTitle formSection>
    <#return formSection["./formSectionItem/title]>
</#function>

<#--doc
    Gets the components of a form section.

    @param component The model of form section.

    @return A sequence of the models of the compoents of the form item.
-->
<#function getFormSectionComponents formSection>
    <#return formSection["./formSectionItem/formSection/component[(objectType != 'com.arsdigita.formbuilder.Widget' and objectType != 'com.arsdigita.formbuilder.DataDrivenSelect' or (defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSubmit' or defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHidden' or defaultDomainClass = 'com.arsdigita.formbuilder.HiddenIDGenerator')]"]>
</#function>

<#function getLabelComponents label>
    <#return label["./widget"]>
</#function>

<#function getButtonGroupComponents buttonGroup>
    <#return buttonGroup["./component"]>
</#function>

<#function isMultipleSelect select>
    <#return select["./multiple"] == "true">
</#function>

<#function isDataDrivenSelect select>
    <#return (select["./defaultDomainClass"] == "com.arsdigita.formbuilder.DataDrivenSelect")> 
</#function>

<#function getDataOptions select>
    <#return select["./selectOptions/option"]>
</#function>

<#function getDataOptionLabel option>
    <#return select["./@label"]>
</#function>

<#function getDataOptionId option>
    <#return select["./@id"]>
</#function>

<#function getOptionComponents select>
    <#return select["./component"]>
</#function>

<#function hasOtherOption select>
    <#return (select["./optiongroupother"] == "true")>
</#function>

<#function getOtherOptionLabel select>
    <#return select["./optiongroupotherlabel"]>
</#function>

<#function getOtherOptionValue select>
    <#return select["./optiongroupothervalue"]>
</#function>

<#function hasMaxLength component>
    <#return (component["./maxlength"]?size > 0)>
</#function>

<#function getMaxLength component>
    <#return component["./maxlength"]>
</#function>

<#function hasSize component>
    <#return (component["./size"]?size > 0)>
</#function>

<#function getSize component>
    <#return component["./size"]>
</#function>

<#function isRequired component>
    <#return (component["./widgetrequired"] == "true")>
</#function>

<#function getDateFieldDayParamName component>
    <#return component["./parameterName"] + ".day">
</#function>

<#function getDateFieldMonthParamName component>
    <#return component["./parameterName"] + ".month">
</#function>

<#function getDateFieldYearParamName component>
    <#return component["./parameterName"] + ".year">
</#function>

<#function getDateFieldDefaultValueDay component">
    <#return component["./defaultValue/day"]>
</#function>

<#function getDateFieldDefaultValueMonth component">
    <#return component["./monthList/month[@selected='selected']"]>
</#function>

<#function getDateFieldDefaultValueYear component">
    <#return component["./yearList/year[@selected='selected']"]>  
</#function>

<#function getDateFieldMonthList component>
    <#return component["./monthList/month"]>
</#function>

<#function getDateFieldYearList component>
    <#return component["./yearList/year"]>
</#function>

<#function getMonthLabel month>
    <#return month["."]>
</#function>

<#function getMonthValue month>
    <#return month["./@value"]>
</#function>

<#function getYearLabel year>
    <#return year["."]>
</#function>

<#function getYearValue year>
    <#return year["./@value"]>
</#function>

<#function getTextAreaRows textArea>
    <#return textArea["./rows"]>
</#function>

<#function getTextAreaCols textArea>
    <#return textArea["./cols"]>
</#function>
