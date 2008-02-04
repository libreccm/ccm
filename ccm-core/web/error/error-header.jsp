<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title><%= request.getParameter("title") %></title>
    <link rel="stylesheet" type="text/css" 
          href="<%= request.getContextPath() %>/css/error.css">
  </head>
  <body>
    <table class="globalHeader">
      <tr>
        <td class="globalNavigation">Notice</td>
      </tr>
    </table>
    <table class="setInside">
      <tr>
        <td class="setInside">
          <table class="localHeader">
            <tr>
              <td class="localTitle"><%= request.getParameter("title") %></td><td class="localControl"></td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
    <table class="setInside">
      <tr>
        <td class="setInside">
          <table class="topRuleNoTabs">
            <tr>
              <td></td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
    <table class="setInside">
      <tr>
        <td class="setInside">
