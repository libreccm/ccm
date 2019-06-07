# Freemarker functions for Form items

Import Path
: `/ccm-cms-types-formitem.md`

## `getDescription(item: Node): String`

Gets the description of the form item.

## `getFormAction(item: Node): String`

Gets the URL to which the form is send.

## `hasHoneypot(item: Node): boolean`

Returns `true` if the form contains a honeypot field for catching bots.

## `getHoneypotName(item: Node): String`

Gets the name of the honeypot field.

## `hasMinTimeCheck(item: Node): boolean`

Returns `true` if a check of the time the user needs to fill out the form should be added to the form. Bots are normally extremly fast (faster than any human).

## `getMinTimeCheckValue(item: Node): String`

The minium time for the min time check.

## `hasVisitedField(item: Node): boolean`

Returns `true` if a visited field is part of the form.

## `getVisitedField(item: Node): String`

Gets the value of the visited field.

## `getVisitedFieldName(item: Node): String`

Returns the name of the visited field.

## `getPageStateFieldName(item: Node): String`

Gets the name to use for the (hidden) field holding the page state.

## `getPageStateFieldValue(item: Node): String`

Gets the value of the (hidden) field holding the page state.

## `getComponents(item: Node): Sequence<Node>`

Returns the compnents of the form.

## `getComponentName(component: Node): String`

Returns the name of the provided component.

## `getComponentDefaultValue(component: Node): String`

Returns the default value of the provided component.

## `getComponentDescription(component: Node): String`

Returns the description of the provided component.

## `getComponentParameterName(component: Node): String`

Returns the parameter name of the provided component.

## `getComponentType(component: Node): String`

Returns the type of the provided component.

## `getFormSectionTitle(formSection: Node): String`

Gets the title of the provided form section.

## `getFormSectionComponents(formSection: Node): String`

Gets the components of the form section.

## `getLabelComponents(label: Node): Sequence<Node>`

Gets the components of a label.

## `getButtonGroupComponents(buttonGroup: Node): Sequence<Node>`

Gets the components of a button group.

## `isMultipleSelect(select: Node): boolean`

Returns `true` if the provided select component allows multiple values.

## `isDataDrivenSelect(select: Node): boolean`

Returns `true` if the provided select component is a data driven select.

## `getDataOptions(select: Node): Sequence<Node>`

Gets the options of a data driven select.

## `getDataOptionLabel(option: Node): String`

Gest the label of an option of an data driven select.

## `getDataOptionId(option: Node): String`

Gest the id of an option of an data driven select.

## `getDataOptionCompoents(option: Node): Sequence<Node>`

Gest the components of an option of an data driven select.

## `hasOtherOption(select: Node): boolean`

Returns `true` if the provided select component has an `other` option.

## `getOtherOptionLabel(select: Node): String`

Returns the label for the `other` option.

## `getOtherOptionValue(select: Node): String`

Returns the value for the `other` option.

## `hasMaxLength(component: Node): boolean`

Returns `true` if the provided component has a `maxLength` property.

## `getMaxLength(component: Node): String`

Gets the value of the `maxLength` property of the provided component.

## `hasSize(component: Node): boolean`

Returns `true` if the provided component has a `size` property.

## `getMaxSize(component: Node): String`

Gets the value of the `size` property of the provided component.

## `getRequired(component: Node): String`

Determines of the provided component represents a mandantory field of the form.

## `getDateFieldDayParamName(component: Node): String`

Returns the name of the day param of a date input component.

## `getDateFieldMonthParamName(component: Node): String`

Returns the name of the month param of a date input component.

## `getDateFieldYearParamName(component: Node): String`

Returns the name of the year param of a date input component.

## `getDateFieldDefaultValueDay(component: Node): String`

Returns the default value for the day of the provided date input 
component.

## `getDateFieldDefaultValueMonth(component: Node): String`

Returns the default value for the month of the provided date input 
component.

## `getDateFieldDefaultValueYear(component: Node): String`

Returns the default value for the year of the provided date input 
component.

## `getDateFieldMonthList(component: Node): Sequence<Node>`

Gets the list of permitted months.

## `getDateFieldYearList(component: Node): Sequence<Node>`

Gets the list of permitted years.

## `getMonthLabel(month: Node): String`

Gets the label for the provided month.

## `getYearLabel(year: Node): String`

Gets the label for the provided year.

## `getTextAreaRows(textArea: Node): String`

Gets the number of rows for the provided text area component.

## `getTextAreaCols(textArea: Node): String`

Gets the number of cols for the provided text area component.