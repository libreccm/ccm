<%@ taglib prefix="define" uri="/WEB-INF/bebop-define.tld" %>
<%@ taglib prefix="show" uri="/WEB-INF/bebop-show.tld" %>
<%@ page import="com.arsdigita.bebop.*" %>
<%@ page import="com.arsdigita.bebop.form.*" %>
<%@ page import="com.arsdigita.bebop.event.*" %>

<define:page name="p" title="foo">
 <define:form name="myForm">
   <define:radioGroup name="radio">
    <define:option name="foo" value="fooValue"/>
    <define:option name="bar" value="barValue"/>
    <define:option name="baz" value="bazValue"/>
   </define:radioGroup>
   <define:checkboxGroup name="cb">
    <define:option name="foo" value="fooValue"/>
    <define:option name="bar" value="barValue"/>
    <define:option name="baz" value="bazValue"/>
   </define:checkboxGroup>

   <define:select name="select">
    <define:option name="foo" value="fooValue"/>
    <define:option name="bar" value="barValue"/>
    <define:option name="baz" value="bazValue"/>
   </define:select>

   <define:multipleSelect name="multi">
    <define:option name="foo" value="fooValue"/>
    <define:option name="bar" value="barValue"/>
    <define:option name="baz" value="bazValue"/>
   </define:multipleSelect>
 </define:form>
</define:page>

<show:page>
showing page with option groups.

<show:form name="myForm">

<p>
radio group:
<show:component name="radio"/>

<p>
checkbox group:
<show:component name="cb"/>

<p>
single select:
<show:component name="select"/>

<p>
multi select:
<show:component name="multi"/>

</show:form>

</show:page>
