<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:admin="http://www.arsdigita.com/permissions-ui/1.0"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                exclude-result-prefixes="admin">

<xsl:import href="../../bebop/xsl/bebop.xsl"/>
<xsl:import href="../../ui/xsl/ui.xsl"/>


<xsl:template match="bebop:dimensionalNavbar[@class='permNavBar']">
    <xsl:comment>bebop:dimensionalNavbar</xsl:comment>
    <xsl:for-each select="*">
      <xsl:apply-templates select="."/>
      <xsl:if test="position()!=last()">&#160;&gt;&#160;</xsl:if>
    </xsl:for-each>
    <p />
    <xsl:comment>/bebop:dimensionalNavbar</xsl:comment>
</xsl:template>

<xsl:template match="bebop:list[@class='bulletList']">    
    <ul>
      <xsl:for-each select="bebop:cell">
        <li><xsl:apply-templates/></li>
      </xsl:for-each>
    </ul>
</xsl:template>

<xsl:template match="bebop:label[@class='errorBullet']">    
    <ul><font color="red">
        <li><xsl:apply-templates/></li>
        </font>
    </ul>
</xsl:template>

<xsl:template match="bebop:label[@class='heading']">
    <h2><xsl:apply-templates/></h2>
</xsl:template>

<xsl:template match="bebop:link[@class='checkBoxChecked']">
  <a href="{@href}" onclick="{@onclick}">
    <img src="{$contextPath}/assets/checkbox-checked.gif" border="0" width="12" height="12">
       <xsl:attribute name="alt">
         <xsl:apply-templates/>
       </xsl:attribute>
    </img>
  </a>
  <xsl:text>&#160;</xsl:text>
  <a href="{@href}" onclick="{@onclick}" class="action_link">
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="bebop:link[@class='checkBoxUnchecked']">
     <a href="{@href}" onclick="{@onclick}">
    <img src="{$contextPath}/assets/checkbox-unchecked.gif" border="0" width="12" height="12">
       <xsl:attribute name="alt">
         <xsl:apply-templates/>
       </xsl:attribute>
    </img>
  </a>
  <xsl:text>&#160;</xsl:text>
  <a href="{@href}" onclick="{@onclick}" class="action_link">
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="bebop:label[@class='checkBoxGreyChecked']">
    <img src="{$contextPath}/assets/checkbox-checked-gray.gif" border="0" width="12" height="12">
       <xsl:attribute name="alt">
         <xsl:apply-templates/>
       </xsl:attribute>
    </img>
  <xsl:text>&#160;</xsl:text>
</xsl:template>

<xsl:template match="bebop:label[@class='checkBoxGreyUnchecked']">
    <img src="{$contextPath}/assets/checkbox-unchecked-gray.gif" border="0" width="12" height="12">
       <xsl:attribute name="alt">
         <xsl:apply-templates/>
       </xsl:attribute>
    </img>
  <xsl:text>&#160;</xsl:text>
</xsl:template>



<!-- Access Denied Page -->
<xsl:template match="bebop:label[@class='AccessDenied']">
  <hr />
  <p>You don't have permission to perform the requested action.</p>
  <hr />
</xsl:template>

<!-- Display search error message -->
<xsl:template match="bebop:list[@class='UserSearchList'][count(bebop:cell)=0]">
   <p><font color="red">Your search returned no results.</font></p>
</xsl:template>

</xsl:stylesheet>

