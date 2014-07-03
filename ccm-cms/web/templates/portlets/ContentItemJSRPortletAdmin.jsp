<%-- 
    Document   : ContentItemJSRPortletAdmin
    Created on : 20.06.2014, 13:08:01
    Author     : Jens Pelzetter <jens@jp-digital.de>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
           prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
           prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<!DOCTYPE html>
<div>
    <fmt:setBundle basename="com.arsdigita.cms.portlet.ContentItemJSRPortletResources"/>

    <h1>Hello World from ContentItemJSRPortletAdmin.jsp</h1>

    <ul class="errors">
        <c:forEach var="error" 
                   items="${errors}">
            <li>${error}</li>
            </c:forEach>
    </ul>

    <div>
        <h2>Currently selected item</h2>
        <c:if test="${selectedItemOID != null}">
            <dl>
                <dt>OID</dt>
                <dd>${selectedItemOID}</dd>
                
                <dt>Path</dt>
                <dd>${selectedItemPath}</dd>
                
                <dt>Title</dt>
                <dd>${selectedItemTitle}</dd>
                
                <dt>Type</dt>
                <dd>${selectedItemType}</dd>
                
                <dt>Status</dt>
                <dd>${selectedItemStatus}</dd>
            </dl>
        </c:if>
    </div>

    <form action='<portlet:actionURL/>' method="post">
        <fieldset>
            <legend><fmt:message key="contentItemJSRPortlet.edit.fieldset.label"/></legend>
        <label for="contentSectionSelect">
            <!--Content Section-->
            <fmt:message key="contentItemJSRPortlet.edit.contentSectionSelect.label"/>
        </label>                
        <select id="contentSectionSelect" name="contentSectionSelect">
            <c:forEach var="section" 
                       items="${contentSections}">
                <option value="${section.specificOID}" ${section.specificOID  == selectedContentSection ? 'selected="selected"' : ''}>${section.displayName}</option>
            </c:forEach>
        </select>

        <label for="contentItemSearchString">
            <fmt:message key="contentitemJSRPortlet.edit.search.label"/>
        </label>
        <input id="contentSearchString" 
               name="contentItemSearchString" 
               type="text" 
               maxlength="1000" 
               size="40"
               value="${contentItemSearchString}"/>

        <input type="submit" value="Find"/>
        </fieldset>
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
                        <td>${matchingItem.displayName}</td>
                        <td>
                            <portlet:actionURL portletMode="edit"
                                               var="selectURL">
                                <portlet:param name="action" value="selectItem"/>
                                <portlet:param name="selectedItem" value="${matchingItem.OID}"/>
                            </portlet:actionURL>
                            <!--This will be the select link for ${matchingItem.OID}-->
                            <a href="${selectURL}">Select</a>
                        </td>
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