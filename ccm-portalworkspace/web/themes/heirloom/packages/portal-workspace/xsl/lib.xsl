<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
              xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
             xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
            xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
  version="1.0">


  <xsl:template match="portlet:simple">
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="bebop:portlet[portlet:action]" name="default-portlet-edit">
    <table width="100%" border="0" cellspacing="2" cellpadding="0">
      <tr>
        <td bgcolor="#cccccc">
          <table width="100%" cellspacing="2" cellpadding="4" border="0">
            <tr>
              <th class="split_pane_header" width="100%">
                <xsl:text>Edit </xsl:text><xsl:value-of select="@title"/>
              </th>
              <xsl:for-each select="portlet:action">
                <th class="#cccccc" nowrap="nowrap" align="right">
                  <xsl:variable name="title">
                    <xsl:choose>
                      <xsl:when test="@name = 'moveDown'">
                        <xsl:text>Move Down</xsl:text>
                      </xsl:when>
                      <xsl:when test="@name = 'moveUp'">
                        <xsl:text>Move Up</xsl:text>
                      </xsl:when>
                      <xsl:when test="@name = 'moveLeft'">
                        <xsl:text>Move Left</xsl:text>
                      </xsl:when>
                      <xsl:when test="@name = 'moveRight'">
                        <xsl:text>Move Right</xsl:text>
                      </xsl:when>
                      <xsl:when test="@name = 'customize'">
                        <xsl:text>Configure</xsl:text>
                      </xsl:when>
                      <xsl:when test="@name = 'delete'">
                        <xsl:text>Delete</xsl:text>
                      </xsl:when>
                    </xsl:choose>
                  </xsl:variable>
                  
                  <a href="{@url}" title="{$title}">
                    <img src="../images/{@name}.gif" border="0" alt="{$title}"/>
                  </a>
                </th>
              </xsl:for-each>
            </tr>
            <tr>
              <td class="table_cell" bgcolor="#ffffff" colspan="20">
                <xsl:apply-templates select="*[not(bebop:portlet)]"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template match="bebop:portlet">
    <table width="100%" border="0" cellspacing="2" cellpadding="0">
      <tr>
        <td bgcolor="#cccccc">
          <table width="100%" cellspacing="2" cellpadding="4" border="0">
            <tr>
              <th class="split_pane_header" width="100%">
                <xsl:value-of select="@title"/>
              </th>
            </tr>
            <tr>
              <td class="table_cell" bgcolor="#ffffff" colspan="20">
                <xsl:apply-templates select="*"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </xsl:template>
   
  <xsl:template match="portlet:stateful-portlet-in-edit-mode">
     This portlet will become visible when you finish customising the page<p/>
  </xsl:template>  
  
  <xsl:template match="portal:portal">
    <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr valign="top">
        <xsl:apply-templates select="." mode="columns">
          <xsl:with-param name="format" select="@layout"/>
        </xsl:apply-templates>
      </tr>
    </table>
  </xsl:template>

  <xsl:template match="portal:portal" mode="columns">
    <xsl:param name="format" select="'100%'"/>
    <xsl:param name="column" select="'1'"/>

    <xsl:choose>
      <xsl:when test="contains($format, ',')">
        <xsl:variable name="base">
          <xsl:value-of select="substring-before($format, ',')"/>
        </xsl:variable>
        <xsl:variable name="rest">
          <xsl:value-of select="substring-after($format, ',')"/>
        </xsl:variable>

        <xsl:apply-templates select="." mode="column">
          <xsl:with-param name="width" select="$base"/>
          <xsl:with-param name="cellNumber" select="$column"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="columns">
          <xsl:with-param name="format" select="$rest"/>
          <xsl:with-param name="column" select="$column+1"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="." mode="column">
          <xsl:with-param name="width" select="$format"/>
          <xsl:with-param name="cellNumber" select="$column"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="portal:portal" mode="column">
    <xsl:param name="width" select="'100%'"/>
    <xsl:param name="cellNumber" select="'1'"/>

    <td width="{$width}">
      <xsl:for-each select="bebop:portlet[@cellNumber = $cellNumber]">
        <xsl:sort select="@row" data-type="number" order="ascending"/>
        <xsl:apply-templates select="."/>
      </xsl:for-each>
    </td>
  </xsl:template>

  <xsl:template match="portal:portalList">
    <xsl:apply-templates select="." mode="tabs"/>
  </xsl:template>
  
  <xsl:template match="portal:portalDetails" mode="tabs">
    <xsl:choose>

      <xsl:when test="@isSelected = 'false'">
        <td class="tab-label">
          <a href="{@moveLeftAction}">
            <img src="../images/moveLeft.gif" border="0" style="margin-left: 5px"/>
          </a>
          <a href="{@moveRightAction}">
            <img src="../images/moveRight.gif" border="0" style="margin-left: 5px"/>
          </a>
          <a href="{@selectAction}">
            <xsl:value-of select="title"/>
          </a>
          <a href="{@deleteAction}" onclick="return confirm('Are you sure you want to delete this pane')">
            <img src="../images/delete.gif" border="0" style="margin-left: 5px"/>
          </a>
        </td>
        <td class="tab-end"/>
      </xsl:when>

      <xsl:when test="../bebop:form[@name='editPortal']">
        <td class="current-tab-label">
          <table>
            <tr>
              <td>
                <a href="{@moveLeftAction}">
                  <img src="../images/moveLeft.gif" border="0" style="margin-left: 5px"/>
                </a>
              </td>
              <td>
                <a href="{@moveRightAction}">
                  <img src="../images/moveRight.gif" border="0" style="margin-left: 5px"/>
                </a>
              </td>
              <td>
                <xsl:apply-templates select="../bebop:form[@name='editPortal']"/>
              </td>
              <td>
                <a href="{@deleteAction}" onclick="return confirm('Are you sure you want to delete this pane')">
                  <img src="../images/delete.gif" border="0" style="margin-left: 5px"/>
                </a>
              </td>
            </tr>
          </table>
        </td>
        <td class="current-tab-end"/>
      </xsl:when>

      <xsl:otherwise>
        <td class="current-tab-label">
          <a href="{@moveLeftAction}">
            <img src="../images/moveLeft.gif" border="0" style="margin-left: 5px"/>
          </a>
          <a href="{@moveRightAction}">
            <img src="../images/moveRight.gif" border="0" style="margin-left: 5px"/>
          </a>
          <xsl:value-of select="title"/>
          <a href="{@deleteAction}" onclick="return confirm('Are you sure you want to delete this pane')">
            <img src="../images/delete.gif" border="0" style="margin-left: 5px"/>
          </a>
        </td>
        <td class="current-tab-end"/>
      </xsl:otherwise>

    </xsl:choose>
    <td class="tab-spacer"/>
  </xsl:template>
  
  <xsl:template match="portal:portalList" mode="tabs">
    <div class="tabbed-pane">
      <table class="tab-set">
        <tr>
          <xsl:apply-templates select="portal:portalDetails" mode="tabs">
            <xsl:sort select="sortKey" data-type="number"/>
          </xsl:apply-templates>
          <xsl:if test="bebop:form[@name='editPortal']">
            <td><xsl:apply-templates select="bebop:form[@name='editLayout']"/></td>
          </xsl:if>
        </tr>
      </table>
      <table class="rule">
        <tr><td></td></tr>
      </table>
    </div>
    <div><xsl:text>&#160;</xsl:text></div>
  </xsl:template>


  <xsl:template match="portal:workspace">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="portal:workspaceDetails">
    <table width="100%">
      <tr>
        <td align="right">
        <a>
        <xsl:attribute name="href">
        <xsl:value-of select="/bebop:page/@dispatcherServletPath"/>
        <xsl:value-of select="primaryURL"/>
        </xsl:attribute>
        Back to workspace
        </a>
        </td>
      </tr>
     </table>
  </xsl:template>

</xsl:stylesheet>
