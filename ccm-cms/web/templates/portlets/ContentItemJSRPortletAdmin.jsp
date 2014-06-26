<%-- 
    Document   : ContentItemJSRPortletAdmin
    Created on : 20.06.2014, 13:08:01
    Author     : Jens Pelzetter <jens@jp-digital.de>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
     prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<!DOCTYPE html>
<div>
    <h1>Hello World from ContentItemJSRPortletAdmin.jsp</h1>
    
    <form action='<portlet:actionURL/>' method="post">
        <label for="contentSectionSelect">
            Content Section
        </label>
        <select id="contentSectionSelect" name="contentSectionSelect">
            <c:forEach var="section" 
                       items="${contentSections}">
                <option value="${section.specificOID}" ${section.specificOID  == selectedContentSection ? 'selected="selected"' : ''}>${section.displayName}</option>
            </c:forEach>
        </select>
        
        <label for="contentItemSearchString">
            Search string
        </label>
        <input id="contentSearchString" 
               name="contentItemSearchString" 
               type="text" 
               maxlength="1000" 
               size="40"/>
        
        <input type="submit" value="Find"/>
    </form>
    
    <c:if test="${matchingItems != null}">
        <table>
            <thead>
                <tr>
                    <th>Path</th>
                    <th>Title</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="matchingItem" items="${matchingItems}">
                <tr>
                    <td>${matchingItem.path}</td>
                    <td>${matchingItem.title}</td>
                    <td>This will be the select link for ${matchingItem.oid}</td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <%-- <h1>Content Sections</h1>
    <ul>
        <c:forEach var="section" items="${comArsdigitaCMSContentItemJSRPortletAdmin.contentSections}">
            <li>${section.displayName}</li>
        </c:forEach>
    </ul>--%>
</div>