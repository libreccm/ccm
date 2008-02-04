<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:search="http://rhea.redhat.com/search/1.0"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  version="1.0">

  <xsl:output method="html"/>

  <xsl:template match="search:object">
    <xsl:param name="class" select="none"/>

    <tr class="{$class}">
      <td><xsl:value-of select="@score"/></td>
      <td><a href="{@url}&amp;context=draft"><xsl:value-of select="@title"/></a></td>
      <td><em><xsl:value-of select="@summary"/></em></td>
    </tr>
  </xsl:template>

  <xsl:template match="search:object[@class='jsButton']">
    <xsl:param name="class" select="none"/>

    <tr class="{$class}">
      <td><xsl:value-of select="@rank"/></td>
      <td><a href="{@url}"><xsl:value-of select="@title"/></a></td>
      <td><em><xsl:value-of select="@summary"/></em></td>
      <td>
        <xsl:value-of disable-output-escaping="yes" select="search:jsAction"/>
        <a onClick="{search:jsAction/@name}" href="javascript:{search:jsAction/@name}">
          <img src="/__ccm__/static/cms/admin/action-group/action-generic.png" width="14" height="14" border="0"/>
          <xsl:text> Select item</xsl:text>
        </a>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="search:object[@class='radioButton']">
    <xsl:param name="class" select="none"/>

    <tr class="{$class}">
      <td><xsl:value-of select="@rank"/></td>
      <td><input type="radio" name="{@field}" value="{@oid}"/><a href="{@url}"><xsl:value-of select="@title"/></a></td>
      <td><em><xsl:value-of select="@summary"/></em></td>
      <td>
        <xsl:value-of disable-output-escaping="yes" select="search:jsAction"/>
        <a onClick="{search:jsAction/@name}" href="javascript:{search:jsAction/@name}">
          <img src="/__ccm__/static/cms/admin/action-group/action-generic.png" width="14" height="14" border="0"/>
          <xsl:text> Select item</xsl:text>
        </a>
      </td>
    </tr>
  </xsl:template>


  <xsl:template match="search:filter[@type='contentType']">
    <th align="right">
      <xsl:text>Types:</xsl:text>
    </th>
    <td>
      <select size="10" name="{@param}" multiple="multiple">
        <xsl:for-each select="search:contentType">
          <xsl:sort select="@title"/>
          <option value="{@name}">
            <xsl:if test="@isSelected">
              <xsl:attribute name="selected">selected</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="@title"/>
          </option>
        </xsl:for-each>
      </select>
    </td>
  </xsl:template>

  <xsl:template match="search:filter[@type='creationUser']">
     <xsl:apply-templates select="search:partyText">
       <xsl:with-param name="filterName"><xsl:text>Creation User:</xsl:text></xsl:with-param>
     </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="search:filter[@type='lastModifiedUser']">
     <xsl:apply-templates select="search:partyText">
       <xsl:with-param name="filterName"><xsl:text>Last Modified By:</xsl:text></xsl:with-param>
     </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="search:partyText">
	<xsl:param name="filterName"/>
    <th align="right">
      <xsl:value-of select="$filterName"/>
    </th>
    <td>
    <input size="30">
       <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
       <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
    </input>
    </td>
  </xsl:template>


  <xsl:template match="search:filter[@type='launchDate']">
     <xsl:call-template name="search:dateRangeFilter">
       <xsl:with-param name="filterName"><xsl:text>Launch Date:</xsl:text></xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <xsl:template match="search:filter[@type='lastModifiedDate']">
     <xsl:call-template name="search:dateRangeFilter">
       <xsl:with-param name="filterName"><xsl:text>Last Modified Date:</xsl:text></xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <xsl:template match="search:filter[@type='creationDate']">
     <xsl:call-template name="search:dateRangeFilter">
       <xsl:with-param name="filterName"><xsl:text>Creation Date:</xsl:text></xsl:with-param>
     </xsl:call-template>
  </xsl:template>

  <xsl:template name="search:dateRangeFilter">
	<xsl:param name="filterName"/>
    <th align="right">
      <xsl:value-of select="$filterName"/>
    </th>
    <td>
      <table>
        <tr>
          <td></td>
          <th>Day</th>
          <th>Month</th>
          <th>Year</th>
        </tr>
        <tr>
          <th align="right">From:</th>
          <td>
            <input size="2" type="text" name="{@param}.start.day">
              <xsl:if test="search:day/@startDay">
                <xsl:attribute name="value">
                  <xsl:value-of select="@search:day/@startDay"/>
                </xsl:attribute>
              </xsl:if>
            </input>
          </td>
          <td>
            <select name="{@param}.start.month">
              <xsl:for-each select="search:month">
                <option value="{@value}">
                  <xsl:if test="@startMonth">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:value-of select="@title"/>
                </option>
              </xsl:for-each>
            </select>
          </td>
          <td>
            <select name="{@param}.start.year">
              <xsl:for-each select="search:year">
                <option value="{@value}">
                  <xsl:if test="@startYear">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:value-of select="@title"/>
                </option>
              </xsl:for-each>
            </select>
          </td>
        </tr>
        <tr>
          <th align="right">To:</th>
          <td>
            <input size="2" type="text" name="{@param}.end.day">
              <xsl:if test="search:day/@endDay">
                <xsl:attribute name="value">
                  <xsl:value-of select="@search:day/@endDay"/>
                </xsl:attribute>
              </xsl:if>
            </input>
          </td>
          <td>
            <select name="{@param}.end.month">
              <xsl:for-each select="search:month">
                <option value="{@value}">
                  <xsl:if test="@endMonth">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:value-of select="@title"/>
                </option>
              </xsl:for-each>
            </select>
          </td>
          <td>
            <select name="{@param}.end.year">
              <xsl:for-each select="search:year">
                <option value="{@value}">
                  <xsl:if test="@endYear">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:value-of select="@title"/>
                </option>
              </xsl:for-each>
            </select>
          </td>
        </tr>
      </table>
    </td>
  </xsl:template>
</xsl:stylesheet>
