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
  Hier werden die Forum Formulare verarbeitet 
-->

<!-- EN
  Processing forum forms
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

  <xsl:template match="bebop:form[@name='newPostForm'] | bebop:form[@name='editPostForm'] | bebop:form[@name='replyPostForm']">
    <div class="forumForm">
      <h2>
        <xsl:choose>
          <xsl:when test="forum:postForm | forum:postFormFiles">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="concat('forms/', @name, '/post')"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="concat('forms/', @name, '/confirm')"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </h2>
      <xsl:if test="@message">
        <div class="formMessage">
          <xsl:value-of select="@message"/>
        </div>
      </xsl:if>
      
      <form>
        <xsl:if test="not(@method)">
          <xsl:attribute name="method">post</xsl:attribute>
        </xsl:if>
        <xsl:call-template name="mandalay:processAttributes"/>
        <xsl:apply-templates/>
      </form>
    </div>
  </xsl:template>
  
  <xsl:template match="forum:postForm">
    <xsl:apply-templates select="bebop:formWidget[@type='hidden']"/>
    <div>
    <label for="subject">
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="'forum'"/>
        <xsl:with-param name="id" select="'forms/subject'"/>
      </xsl:call-template>
    </label>
      <xsl:apply-templates select="bebop:formWidget[@name='subject']"/>
    </div>
    <div>
      <label for="ta_message">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'forum'"/>
          <xsl:with-param name="id" select="'forms/message'"/>
        </xsl:call-template>
      </label>
      <xsl:apply-templates select="bebop:textarea[@name='message'] | bebop:xinha[@name='message']"/>
    </div>
    <xsl:if test="bebop:select[@name='bodyType']">
      <div>
        <label for="bodyType">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'forum'"/>
            <xsl:with-param name="id" select="'forms/format'"/>
          </xsl:call-template>
        </label>
        <xsl:apply-templates select="bebop:select[@name='bodyType']"/>
      </div>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="bebop:select[@name='postTopic']">
        <div>
          <label for="postTopic">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'forms/topic'"/>
            </xsl:call-template>
          </label>
            <xsl:apply-templates select="bebop:select[@name='postTopic']"/>
        </div>
      </xsl:when>
      <xsl:when test="forum:message">
        <div>
          <label for="">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'forum'"/>
              <xsl:with-param name="id" select="'forms/inReply'"/>
            </xsl:call-template>
          </label>
          <xsl:apply-templates select="forum:message"/>
        </div>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="forum:postConfirm">
    <div class="postConfirm">
      <span class="label">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'forum'"/>
          <xsl:with-param name="id" select="'messages/subject'"/>
        </xsl:call-template>
      </span>
      <span class="value">
        <xsl:value-of select="subject"/>
      </span>
      <br/>
      <span class="label">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'forum'"/>
          <xsl:with-param name="id" select="'messages/message'"/>
        </xsl:call-template>
      </span>
      <span class="value">
        <xsl:value-of disable-output-escaping="yes" select="body"/>
      </span>
      <br/>
      <h3>
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'forum'"/>
          <xsl:with-param name="id" select="'forms/attachedFiles/heading'"/>
        </xsl:call-template>
      </h3>
      <xsl:apply-templates select="forum:files | files"/>
      <br/>
    </div>
  </xsl:template>

  <xsl:template match="forum:postFormFiles">
    <h3>
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="'forum'"/>
        <xsl:with-param name="id" select="'forms/attachedFiles/heading'"/>
      </xsl:call-template>
    </h3>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="forum:attachedFiles">
    <ul>
      <xsl:apply-templates/>
    </ul>
    <div class="formDescription">
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="'forum'"/>
        <xsl:with-param name="id" select="'forms/attachFile/description'"/>
      </xsl:call-template>
    </div>
  </xsl:template>

  <xsl:template match="forum:file">
    <li>
      <span class="name">
        <xsl:value-of select="@name"/>
      </span>
      <xsl:if test="@deleteLink">
        &nbsp;
        <a class="deleteLink">
          <xsl:attribute name="href">
            <xsl:value-of select="@deleteLink"/>
          </xsl:attribute>
          <xsl:attribute name="title">
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'forum'"/>
            <xsl:with-param name="id" select="'forms/attachedFiles/delete'"/>
          </xsl:call-template>
          </xsl:attribute>
          <xsl:call-template name="mandalay:getStaticText">
            <xsl:with-param name="module" select="'forum'"/>
            <xsl:with-param name="id" select="'forms/attachedFiles/delete'"/>
          </xsl:call-template>
        </a>
      </xsl:if>
      &nbsp;
      <span class="description">
        <xsl:value-of select="@description"/>
      </span>
    </li>
  </xsl:template>

</xsl:stylesheet>
