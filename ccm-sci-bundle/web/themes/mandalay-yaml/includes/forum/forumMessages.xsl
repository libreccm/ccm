<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
  
    This file is part of Mandalay.

    Mandalay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Mandalay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.
-->

<!-- DE
  Hier werden die Forum Nachrichten verarbeitet 
-->

<!-- EN
  Processing forum messages
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
  xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
  xmlns:forum="http://www.arsdigita.com/forum/1.0"
  exclude-result-prefixes="xsl bebop cms nav mandalay portal portlet forum"
  version="1.0">

  <xsl:template match="forum:message">
    <xsl:param name="layoutTree" select="."/>
  
    <xsl:variable name="setSender">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/message/setSender"/>
        <xsl:with-param name="module" select="'forum'"/>
        <xsl:with-param name="setting" select="'message/setSender'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setMailToSender">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/message/setMailToSender"/>
        <xsl:with-param name="module" select="'forum'"/>
        <xsl:with-param name="setting" select="'message/setMailToSender'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setDate">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/message/setDate"/>
        <xsl:with-param name="module" select="'forum'"/>
        <xsl:with-param name="setting" select="'message/setDate'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setStatus">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="node" select="$layoutTree/message/setStatus"/>
        <xsl:with-param name="module" select="'forum'"/>
        <xsl:with-param name="setting" select="'message/setStatus'"/>
        <xsl:with-param name="default" select="'true'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <div class="forumMessage">
      <div class="body ">
      <div class="head startLeftFloat">
        <xsl:if test="$setSender = 'true'">
          <span class="messageSender">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'messages/sender'"/>
            </xsl:call-template>
            <xsl:choose>
              <xsl:when test="$setMailToSender = 'true'">
                <a href="mailto:{sender/primaryEmail}">
                  <xsl:value-of select="sender/displayName"/>
                </a>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="sender/displayName"/>
              </xsl:otherwise>
            </xsl:choose>
          </span>
        </xsl:if>
        <xsl:if test="$setDate = 'true'">
          <span class="messageDate">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'messages/date'"/>
            </xsl:call-template>
            <xsl:value-of select="sent"/>
          </span>
        </xsl:if>
        <xsl:if test="$setStatus = 'true'">
          <xsl:if test="status != 'approved'">
            <span class="messageState">
              <strong>
                <xsl:value-of select="status"/>
              </strong>
            </span>
          </xsl:if>
        </xsl:if>
      </div>
        <div class="subject">
          <xsl:value-of select="subject"/>
        </div>
        <div class="message">
          <xsl:value-of disable-output-escaping="yes" select="body"/>
        </div>
      </div>
      <div class="endFloat"/>
      <div class="forumFileAttachment">
        <xsl:apply-templates select="files">
          <xsl:sort select="fileOrder" data-type="number"/>
        </xsl:apply-templates>
        <div class="endFloat"/>
      </div>
      <div class="messageAction">
<!--
        <xsl:if test="@approveURL">
          <a class="approve" href="{@approveURL}">
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'thread/approve/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="alt">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'thread/approve/alt'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'thread/approve/link'"/>
            </xsl:call-template>
          </a>
        </xsl:if>
        <xsl:if test="@rejectURL">
          <a class="reject" href="{@rejectURL}">
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'thread/reject/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="alt">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'thread/reject/alt'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'thread/reject/link'"/>
            </xsl:call-template>
          </a>
        </xsl:if>
-->
        <xsl:if test="@editURL">
          <a class="edit" href="{@editURL}">
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'thread/edit/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="alt">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'thread/edit/alt'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'thread/edit/link'"/>
            </xsl:call-template>
          </a>
        </xsl:if>
        <xsl:if test="@replyURL">
          <a class="reply" href="{@replyURL}">
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'thread/reply/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="alt">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'thread/reply/alt'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'thread/reply/link'"/>
            </xsl:call-template>
          </a>
        </xsl:if>
        <xsl:if test="@deleteURL">
          <a class="delete" href="{@deleteURL}">
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'thread/delete/title'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="alt">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'forum'"/>
                <xsl:with-param name="id" select="'thread/delete/alt'"/>
              </xsl:call-template>
            </xsl:attribute>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'thread/delete/link'"/>
            </xsl:call-template>
          </a>
        </xsl:if>
      </div>
      <div class="endFloat"/>
    </div>
  </xsl:template>
  
  <xsl:template match="forum:files | files">
    <xsl:variable name="separator">
      <xsl:call-template name="mandalay:getSetting">
<!--        <xsl:with-param name="node" select="$layoutTree/separator" /> -->
        <xsl:with-param name="module" select="'forum'" />
        <xsl:with-param name="setting" select="'separator'" />
        <xsl:with-param name="default" select="' | '" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setFileAttachmentName">
      <xsl:call-template name="mandalay:getSetting">
<!--        <xsl:with-param name="node" select="$layoutTree/setFileAttachmentName" /> -->
        <xsl:with-param name="module" select="'forum'" />
        <xsl:with-param name="setting" select="'setFileAttachmentName'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setFileAttachmentNameFallback">
      <xsl:call-template name="mandalay:getSetting">
<!--        <xsl:with-param name="node" select="$layoutTree/setFileAttachmentName" /> -->
        <xsl:with-param name="module" select="'forum'" />
        <xsl:with-param name="setting" select="'setFileAttachmentNameFallback'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setFileAttachmentDescription">
      <xsl:call-template name="mandalay:getSetting">
<!--        <xsl:with-param name="node" select="$layoutTree/setFileAttachmentDescription" /> -->
        <xsl:with-param name="module" select="'forum'" />
        <xsl:with-param name="setting" select="'setFileAttachmentDescription'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setFileAttachmentLinkSeperator">
      <xsl:call-template name="mandalay:getSetting">
<!--        <xsl:with-param name="node" select="$layoutTree/setFileAttachmentLinkSeperator" /> -->
        <xsl:with-param name="module" select="'forum'" />
        <xsl:with-param name="setting" select="'setFileAttachmentLinkSeperator'" />
        <xsl:with-param name="default" select="': '" />
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="setFileAttachmentSize">
      <xsl:call-template name="mandalay:getSetting">
<!--        <xsl:with-param name="node" select="$layoutTree/setFileAttachmentSize" /> -->
        <xsl:with-param name="module" select="'forum'" />
        <xsl:with-param name="setting" select="'setFileAttachmentSize'" />
        <xsl:with-param name="default" select="'true'" />
      </xsl:call-template>
    </xsl:variable>
    
    <!-- DE Berechne die Größe zur Basis 2, verwende im Test dezimale Angaben, 
         so daß die nächstöhere Einheit bereits früher verwendet wird. -->
    <xsl:variable name="size">
      <xsl:choose>
        <xsl:when test="length &lt; 1000">
          <xsl:variable name="unit">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'global'"/>
              <xsl:with-param name="id" select="'filesize/byte'"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:value-of select="format-number(length, concat('0.##', $unit))"/>
        </xsl:when>
        <xsl:when test="length >= 1000 and length &lt; 1000000">
          <xsl:variable name="unit">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'global'"/>
              <xsl:with-param name="id" select="'filesize/kibibyte'"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:value-of select="format-number(length div 1024, concat('0.##', $unit))"/>
        </xsl:when>
        <xsl:when test="length >= 1000000 and length &lt; 1000000000">
          <xsl:variable name="unit">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'global'"/>
              <xsl:with-param name="id" select="'filesize/mebibyte'"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:value-of select="format-number(length div 1048576, concat('0.##', $unit))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="unit">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'global'"/>
              <xsl:with-param name="id" select="'filesize/gibibyte'"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:value-of select="format-number(length div 1073741824, concat('0.##', $unit))"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="isDeleted">
      <xsl:value-of select="@isDeleted"/>
    </xsl:variable>
    
    <span class="forumFileAttachment {$isDeleted}">
      <a>
        <xsl:attribute name="href">
           <xsl:value-of select="concat('/redirect?oid=', @oid)"/>
        </xsl:attribute>
        <xsl:attribute name="alt">
          <xsl:value-of select="description"/>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:value-of select="description"/>
        </xsl:attribute>
        <xsl:attribute name="name">
          <xsl:value-of select="name"/>
        </xsl:attribute>
        <xsl:if test="name and ($setFileAttachmentName = 'true' or ($setFileAttachmentNameFallback = 'true' and not(description)))">
          <span class="forumFilename">
            <xsl:value-of select="name"/>
          </span>
        </xsl:if>
        <xsl:if test="$setFileAttachmentName = 'true' and name and
                      $setFileAttachmentDescription = 'true' and description">
          <span class="forumFileSeperator">
            <xsl:value-of select="$setFileAttachmentLinkSeperator"/>
          </span>
        </xsl:if>
        <xsl:if test="$setFileAttachmentDescription = 'true' and description">
          <span class="forumFileDescription">
            <xsl:value-of select="description"/>
          </span>
        </xsl:if>

        <xsl:if test="$setFileAttachmentSize = 'true' and $size">
          <xsl:value-of select="' '"/>
          <span class="forumFileSize">
          <xsl:value-of select="concat('(', $size, ')')"/>
          </span>
        </xsl:if>
      </a>
      <xsl:if test="this != last()">
        <xsl:value-of select="@separator"/>
      </xsl:if>
    </span>
  </xsl:template>

</xsl:stylesheet>
