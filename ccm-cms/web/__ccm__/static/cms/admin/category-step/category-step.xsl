<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  version="1.0">

  <xsl:output method="html" indent="yes"/>

  <xsl:template match="cms:categoryStep">
    <h2>Assign categories</h2>
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <xsl:template match="cms:categoryStepSummary">
    <xsl:for-each select="cms:categoryRoots/cms:categoryRoot">
      <xsl:sort  select="@name"/>
      <xsl:variable name="name">
        <xsl:value-of select="@name"/>
      </xsl:variable>
      <h3><xsl:value-of select="$name"/></h3>
      
      <xsl:if test="@addAction">
        <script LANGUAGE="JavaScript">
          <![CDATA[ <!-- begin script ]]>
          <![CDATA[ document.write('<a href="]]><xsl:value-of select="@addJSAction"/><![CDATA["><img src="/assets/action-add.png" border="0"/></a>')]]>
          <![CDATA[ document.write("\<!--") ]]>
          <![CDATA[ // end script --> ]]>
        </script>
        <a href="{@addAction}">
          <img src="/assets/action-add.png" border="0"/>
        </a>
        <script LANGUAGE="JavaScript">
          <![CDATA[ <!-- begin script ]]>
          <![CDATA[ document.write("--\>") ]]>
          <![CDATA[ // end script --> ]]>
        </script>
        
        <script LANGUAGE="JavaScript">
          <![CDATA[ <!-- begin script ]]>
          <![CDATA[ document.write('<a href="]]><xsl:value-of select="@addJSAction"/><![CDATA[">Add categories</a>')]]>
          <![CDATA[ document.write("\<!--") ]]>
          <![CDATA[ // end script --> ]]>
        </script>
        <xsl:text>&#160;</xsl:text>
        <a href="{@addAction}">
          <xsl:text>Add categories</xsl:text>
        </a>
        <script LANGUAGE="JavaScript">
          <![CDATA[ <!-- begin script ]]>
          <![CDATA[ document.write("--\>") ]]>
          <![CDATA[ // end script --> ]]>
        </script>
      </xsl:if>
       
      <xsl:choose>
        <xsl:when test="count(../../cms:itemCategories/cms:itemCategory[starts-with(@path, $name)]) = 0">
          <div>
            There are no categories assigned in this context
          </div>
        </xsl:when>
        <xsl:otherwise>
          <ul>
            <xsl:for-each select="../../cms:itemCategories/cms:itemCategory[starts-with(@path, $name)]">
              <xsl:sort select="@path"/>
              <li>
                <xsl:value-of select="substring(@path, string-length($name) + 5)"/>&#160;
                <xsl:if test="@deleteAction">
                  <a href="{@deleteAction}"><img src="/assets/action-delete.png" border="0"/></a>
                  <xsl:text>&#160;</xsl:text>
                  <a href="{@deleteAction}">Remove</a>
                </xsl:if>
              </li>
            </xsl:for-each>
          </ul>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="cms:categoryWidget">
    <xsl:choose>
      <xsl:when test="@mode = 'javascript'">
        <xsl:apply-templates select="." mode="cms:javascript"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="." mode="cms:plain"/>        
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="cms:categoryWidget" mode="cms:javascript">
    <script type="text/javascript" src="/assets/prototype.js"/>
    <script type="text/javascript" src="/assets/category-step.js"/>
    <div>
      <xsl:apply-templates select="cms:category" mode="cms:javascriptCat">
        <xsl:with-param name="expand" select="'block'"/>
      </xsl:apply-templates>
    </div>
    <h3>Selected categories</h3>
    <select id="catWd" size="5" onClick="catDeselect()" style="width: 400px; height=200px">
    </select>
    <select id="catWdHd" name="{@name}" size="5" multiple="multiple" style="display: none">
    </select>
  </xsl:template>

  <xsl:template match="cms:category" mode="cms:javascriptCat">
    <xsl:param name="expand" select="'none'"/>
    <xsl:variable name="linkStyle">
      <xsl:choose>
        <xsl:when test="@isAbstract != '1' and @isSelected != '1'">inline</xsl:when>
        <xsl:otherwise>none</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="nameStyle">
      <xsl:choose>
        <xsl:when test="@isAbstract != '1' and @isSelected != '1'">none</xsl:when>
        <xsl:otherwise>inline</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="toggle">
    	
      <xsl:choose>
        <xsl:when test="@root = '1' or ancestor::cms:category/@expand='all'">catBranchToggle</xsl:when>
        <xsl:otherwise>catToggle</xsl:otherwise>
      </xsl:choose> 
    </xsl:variable>

    <div id="catSelf{@id}">
      <xsl:choose>
        <xsl:when test="$expand='none' and (@root='1' or count(cms:category) > 0)">
          <a href="#" onClick="{$toggle}('{@node-id}');"><img id="catTog{@node-id}" src="/assets/action-add.png" width="14" height="14" border="0"/></a>
        </xsl:when>
        <xsl:when test="$expand!='none' and (@root='1' or count(cms:category) > 0)">
          <a href="#" onClick="{$toggle}('{@node-id}');"><img id="catTog{@node-id}" src="/assets/action-delete.png" width="14" height="14" border="0"/></a>
        </xsl:when>
        <xsl:otherwise>
          <img src="/assets/action-generic.png" width="14" height="14" border="0"/>
        </xsl:otherwise>
      </xsl:choose>
      <a id="catLn{@id}" href="#" style="padding-left: 6px; display: {$linkStyle}">
        <xsl:attribute name="onclick">catSelect('<xsl:value-of select="@id"/>', '<xsl:call-template name="escape-apostrophes">
          <xsl:with-param name="text" select="@fullname"/>
        </xsl:call-template>')</xsl:attribute>

        <xsl:if test="@description">
          <xsl:attribute name="title">
            <xsl:value-of select="@description"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:call-template name="cat-widget-cat-name"/>
      </a>
      <span id="catNm{@id}" style="padding-left: 6px; display: {$nameStyle}">
        <xsl:if test="@description">
          <xsl:attribute name="title">
            <xsl:value-of select="@description"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:call-template name="cat-widget-cat-name"/>
      </span>
    </div>
    <div id="catCh{@node-id}" style="margin-left: 20px; display: {$expand}">
      <xsl:apply-templates select="cms:category" mode="cms:javascriptCat">
        <xsl:sort data-type="number" select="@sortKey"/>
      </xsl:apply-templates>
    </div>
  </xsl:template>

  <xsl:template name="cat-widget-cat-name">
    <xsl:value-of select="@name"/>
  </xsl:template>

  <xsl:template match="cms:categoryWidget" mode="cms:plain">
    <select name="{@name}" size="30" multiple="multiple">
      <xsl:apply-templates select="cms:category[position() = 1]/cms:category[@isAbstract = '0']" mode="cms:plainCat"/>
    </select>
  </xsl:template>

  <xsl:template match="cms:category" mode="cms:plainCat">
    <xsl:if test="@isSelected != '1' and @isAbstract != '1'">
      <option value="{@id}"><xsl:value-of select="@fullname"/></option>
    </xsl:if>
    
    <xsl:apply-templates select="cms:category[@isAbstract = '0']" mode="cms:plainCat">
      <xsl:sort data-type="number" select="@sortKey"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="escape-apostrophes">
    <xsl:param name="text"/>
    <xsl:variable name="apostrophe">'</xsl:variable>
    <xsl:variable name="escaped-apostrophe">\'</xsl:variable>
    <xsl:call-template name="do-replace">
      <xsl:with-param name="text" select="$text"/>
      <xsl:with-param name="replace" select="$apostrophe"/>
      <xsl:with-param name="by" select="$escaped-apostrophe"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="do-replace">
    <xsl:param name="text"/>
    <xsl:param name="replace"/>
    <xsl:param name="by"/>
    <xsl:choose>
      <xsl:when test="contains($text, $replace)">
        <xsl:value-of select="substring-before($text, $replace)"/>
        <xsl:value-of select="$by"/>
        <xsl:call-template name="do-replace">
          <xsl:with-param name="text" select="substring-after($text, $replace)"/>
          <xsl:with-param name="replace" select="$replace"/>
          <xsl:with-param name="by" select="$by"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
