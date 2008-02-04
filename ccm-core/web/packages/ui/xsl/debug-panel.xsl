<?xml version="1.0"?>
<!DOCTYPE stylesheet [
  <!ENTITY lf   "&#xa;" >]>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  exclude-result-prefixes="xsl bebop ui">

	<xsl:output 
	method="xml"
	doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
	doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
	indent="yes" />

  <xsl:template match="ui:debugPanel">
    <hr width="100%"/>
    <h3>Debug Options</h3>
    <table>
      <xsl:apply-templates select="ui:debugLink"/>
    </table>
  </xsl:template>

  <xsl:template match="ui:debugLink">
    <tr>
      <td>
        <a href="{@url}">
          <img src="/assets/action-{@type}.png" width="14" height="14" border="0" alt="Debug"/>
        </a>
        <xsl:text>&#160;</xsl:text>
        <a href="{@url}">
          <xsl:value-of select="@title"/>
        </a>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="bebop:structure">
    <xsl:apply-templates select="bebop:state"/>
    <xsl:apply-templates select="bebop:params"/>
    <xsl:variable name="selectedIdx" select="bebop:state/bebop:selected"/>
    <h1>Page structure</h1>
    <h2>Legend:</h2>
    <ul>
      <li style="color: FireBrick;">Branch visible, self visible</li>
      <li style="color: HotPink;">Branch visible, self invisible</li>
      <li style="color: LightPink;">Branch invisible, self visible</li>
      <li style="color: LightSteelBlue;">Branch invisible, self-invisible</li>
      <li style="font-weight: bold;">bold if selected</li>
    </ul>
    <pre>
      <xsl:for-each select="bebop:component">
        <xsl:call-template name="bebopComponent">
          <xsl:with-param name="selectedIdx" select="$selectedIdx"/>
        </xsl:call-template>
      </xsl:for-each>
    </pre>
    <h1>XML before transformation</h1>
    <pre>
      <xsl:for-each select="/*">
        <xsl:call-template name="bebopElement"/>
      </xsl:for-each>
    </pre>
    <h1>Stack traces</h1>
    <dl>
      <xsl:for-each select="/*">
        <xsl:call-template name="bebopClassnames"/>
      </xsl:for-each>
    </dl>
  </xsl:template>

  <xsl:template match="bebop:state">
    <h1>State</h1>
    <xsl:if test="bebop:selected">
      <p>Selected: <a href="#selectedComponent"><xsl:value-of select="bebop:selected"/></a></p>
    </xsl:if>
    <xsl:if test="bebop:eventName/self::text()">
      <p>Event: <xsl:value-of select="bebop:eventName"/>
      Value: <xsl:value-of select="bebop:eventValue"/>
      </p>
    </xsl:if>
  </xsl:template>

  <xsl:template match="bebop:params">
    <h1>Params</h1>
    <table border="0" cellpadding="1" cellspacing="0">
      <xsl:for-each select="*">
        <tr>
          <td style="font-weight: bold;"><xsl:value-of select="@name"/></td>
          <td><xsl:value-of select="."/></td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>

  <xsl:template name="bebopComponent">
    <xsl:param name="padding"/>
    <xsl:param name="branchVisible" select="@isVisible"/>
    <xsl:param name="selectedIdx"/>

    <xsl:variable name="selfVisible" select="@isVisible = 'yes'"/>

    <xsl:variable name="newPadding">
      <xsl:choose>
        <xsl:when test="count(following-sibling::*) > 0">
          <xsl:value-of select="concat($padding, '|   ')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat($padding, '    ')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <span style="color: LightGrey;">
      <xsl:value-of select="concat($padding, '+-')"/>
    </span>
    <span>
      <xsl:attribute name="style">
        <xsl:call-template name="componentVisibility">
          <xsl:with-param name="branchVisible" select="$branchVisible"/>
          <xsl:with-param name="selfVisible" select="$selfVisible"/>
        </xsl:call-template>
        <xsl:if test="$selectedIdx = @idx">
          font-weight: bold;
        </xsl:if>
      </xsl:attribute>
      <xsl:if test="$selectedIdx = @idx">
        <xsl:attribute name="id">selectedComponent</xsl:attribute>
      </xsl:if>

      <xsl:value-of select="@name"/>
    </span>
    <xsl:text>&#xa;</xsl:text>

    <xsl:for-each select="*">
      <xsl:call-template name="bebopComponent">
        <xsl:with-param name="branchVisible" select="$branchVisible and $selfVisible"/>
        <xsl:with-param name="selectedIdx" select="$selectedIdx"/>
        <xsl:with-param name="padding" select="$newPadding"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="componentVisibility">
    <xsl:param name="branchVisible"/>
    <xsl:param name="selfVisible"/>

    <xsl:choose>
      <!-- test for the most common case first -->
      <xsl:when test="not($branchVisible) and $selfVisible">color: LightPink;</xsl:when>
      <xsl:when test="$branchVisible and $selfVisible">color: FireBrick;</xsl:when>
      <xsl:when test="$branchVisible and not($selfVisible)">color: HotPink;</xsl:when>
      <xsl:when test="not($branchVisible) and not($selfVisible)">color: LightSteelBlue;</xsl:when>
      <xsl:otherwise>CANNOT GET HERE</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="bebopElement">
    <xsl:param name="breadCrumbs" select="1"/>
    <xsl:param name="padding"/>

    <xsl:variable name="newPadding">
      <xsl:choose>
        <xsl:when test="count(following-sibling::*) > 0">
          <xsl:value-of select="concat($padding, '|   ')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat($padding, '    ')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:call-template name="bebopPadding">
      <xsl:with-param name="padding" select="concat($padding, '+-')"/>
    </xsl:call-template>

    <xsl:call-template name="bebopTag">
      <xsl:with-param name="str" select="concat('&lt;', name())"/>
    </xsl:call-template>
    <xsl:call-template name="bebopAttributes">
      <xsl:with-param name="breadCrumbs" select="$breadCrumbs"/>
    </xsl:call-template>

    <xsl:choose>
      <xsl:when test="*|text()">
        <xsl:call-template name="bebopTag">
          <xsl:with-param name="str" select="'>'"/>
        </xsl:call-template>
        <xsl:call-template name="bebopClassnameAnchor">
          <xsl:with-param name="breadCrumbs" select="$breadCrumbs"/>
        </xsl:call-template>
        <xsl:text>&lf;</xsl:text>

        <xsl:for-each select="*[not(self::bebop:structure)]|text()">
          <xsl:choose>
            <xsl:when test="self::text()">
              <xsl:call-template name="bebopPadding">
                <xsl:with-param name="padding" select="concat($newPadding, '+-')"/>
              </xsl:call-template>
              <span style="color: SlateGrey;">
                <xsl:call-template name="abbreviateString">
                  <xsl:with-param name="str" select="normalize-space(.)"/>
                </xsl:call-template>
              </span>
              <xsl:text>&lf;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="bebopElement">
                <xsl:with-param name="breadCrumbs" select="concat($breadCrumbs, '.', count(preceding-sibling::*) + 1)"/>
                <xsl:with-param name="padding" select="$newPadding"/>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>

        <xsl:call-template name="bebopPadding">
          <xsl:with-param name="padding">
            <xsl:choose>
              <xsl:when test="count(following-sibling::*) > 0">
                <xsl:value-of select="concat($padding, '| ')"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="concat($padding, '  ')"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="bebopTag">
          <xsl:with-param name="str" select="concat('&lt;/', name(), '>')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="bebopTag">
          <xsl:with-param name="str" select="'/>'"/>
        </xsl:call-template>
        <xsl:call-template name="bebopClassnameAnchor">
          <xsl:with-param name="breadCrumbs" select="$breadCrumbs"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>&lf;</xsl:text>
  </xsl:template>

  <xsl:template name="bebopAttributes">
    <xsl:param name="breadCrumbs"/>
    <xsl:for-each select="@*[not(name() = 'bebop:classname')]">
      <xsl:call-template name="bebopAttribute">
        <xsl:with-param name="name" select="name()"/>
        <xsl:with-param name="value" select="."/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="bebopAttribute">
    <xsl:param name="name"/>
    <xsl:param name="value"/>

    <xsl:text> </xsl:text>
    <span style="color: DarkGreen;">
      <xsl:value-of select="$name"/>
    </span>
    <xsl:text>=</xsl:text>
    <span style="color: SaddleBrown;">
      <xsl:value-of select="concat('&quot;', $value, '&quot;')"/>
    </span>
  </xsl:template>

  <xsl:template name="bebopTag">
    <xsl:param name="str"/>
   <span style="color: Maroon; font-weight: bold;">
      <xsl:value-of select="$str"/>
    </span>
  </xsl:template>

  <xsl:template name="bebopPadding">
    <xsl:param name="padding"/>
    <span style="color: LightGrey;">
      <xsl:value-of select="$padding"/>
    </span>
  </xsl:template>

  <xsl:template name="abbreviateString">
    <xsl:param name="str"/>
    <xsl:variable name="strlen" select="string-length($str)"/>

    <xsl:choose>
      <xsl:when test="$strlen &lt; 60">
        <xsl:value-of select="$str"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat(substring($str, 1, 30),
                                     '...',
                                     substring($str, $strlen - 30))"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="bebopClassnameAnchor">
    <xsl:param name="breadCrumbs"/>
    <xsl:if test="@bebop:classname">
      <xsl:variable name="anchor" select="$breadCrumbs"/>
      <sup><a href="#{$anchor}" id="back{$anchor}"><xsl:value-of select="$anchor"/></a></sup>
    </xsl:if>
  </xsl:template>


  <xsl:template name="bebopClassnames">
    <xsl:param name="breadCrumbs" select="1"/>

    <xsl:if test="@bebop:classname">
      <xsl:variable name="anchor" select="$breadCrumbs"/>
      <dt><a id="{$anchor}" href="#back{$anchor}"><xsl:value-of select="$anchor"/></a></dt>
      <dd>
        <pre>
          <xsl:value-of select="@bebop:classname"/>
        </pre>
      </dd>
    </xsl:if>

    <xsl:for-each select="*">
      <xsl:call-template name="bebopClassnames">
        <xsl:with-param name="breadCrumbs" select="concat($breadCrumbs, '.', count(preceding-sibling::*) + 1)"/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
