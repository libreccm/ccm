<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:theme="http://ccm.redhat.com/london/theme/1.0"
  version="1.0">

  <xsl:import href="../../../../../ROOT/packages/bebop/xsl/bebop.xsl"/>
  <xsl:import href="../../../../../ROOT/packages/ui/xsl/ui.xsl"/>
  <xsl:import href="layout-panel.xsl"/>
  <xsl:import href="section.xsl"/>
  <xsl:import href="action-group.xsl"/>

  <xsl:output method="html"/>
  
  <xsl:template name="bebop:pageCSS">
    <xsl:call-template name="bebop:pageCSSMain"/>
  </xsl:template>

  <xsl:template match="theme:folder|theme:file" name="themeFolder">
      <tr>
      <td align="left">
      <img src="/assets/pix.gif"><xsl:attribute name="width"><xsl:value-of select="@depth"/></xsl:attribute></img>
      </td>

      <xsl:if test="name()='theme:folder'">
      <td colspan="4">
        <i><xsl:value-of select="@name"/></i>      
      </td>
      </xsl:if>

      <xsl:if test="name()='theme:file'">
        <xsl:choose>
          <xsl:when test="@isDeleted='true' or @inWhiteList!='true'">
            <td><strike><xsl:value-of select="@name"/></strike></td>
            <td><strike><xsl:value-of select="@size"/></strike></td>
            <td><strike><xsl:value-of select="@lastModified"/></strike></td>
            <td>
              <xsl:choose>
                <xsl:when test="@isDeleted='true'">
                  <font color="red">(scheduled for removal)</font>
                </xsl:when>
                <xsl:otherwise>
                  (ignored)
                </xsl:otherwise>
              </xsl:choose>
            </td>
          </xsl:when>
          <xsl:otherwise>
            <td><xsl:value-of select="@name"/></td>
            <td><xsl:value-of select="@size"/></td>
            <td><xsl:value-of select="@lastModified"/></td>
            <td>
              <xsl:if test="@removeURL">
                <a>
                  <xsl:attribute name="href">
                    <xsl:value-of select="@removeURL"/>
                  </xsl:attribute>
                  <xsl:attribute name="onclick">return confirm('Are you sure?')</xsl:attribute>
                  Remove
                </a>
              </xsl:if>
            </td>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>

      </tr>
      <xsl:apply-templates select="theme:file">
         <xsl:sort select="./@name" data-type="text" order="ascending"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="theme:folder">
         <xsl:sort select="./@name" data-type="text" order="ascending"/>
      </xsl:apply-templates>
  </xsl:template>                  

  <xsl:template match="theme:fileList">
      <p/>
      <b><xsl:value-of select="bebop:label[@class='heading']"/></b>
      <br/>

      <table border="0">
      <tr>
      <th></th>
      <th align="left">File Name</th>
      <th align="left">Size</th>
      <th align="left">Last Modified</th>
      </tr>
      <xsl:apply-templates select="theme:folder"/>
      </table>
  </xsl:template>

  <xsl:template match="theme:xslWarnings">
     <p/>
     <b>Warning Messages:</b><br/>
     <xsl:apply-templates select="theme:xslErrorInfo"/>
     <p/>
  </xsl:template>

  <xsl:template match="theme:xslErrors">
     <p/>
     <b>Error Messages:</b><br/>
     <xsl:apply-templates select="theme:xslErrorInfo"/>
     <p/>
  </xsl:template>

  <xsl:template match="theme:xslFatals">
     <p/>
     <b>Fatal Messages:</b><br/>
     <xsl:apply-templates select="theme:xslErrorInfo"/>
     <p/>
  </xsl:template>

  <xsl:template match="theme:xslErrorInfo">
    <b><xsl:value-of select="position()"/>.</b> 
       <xsl:choose>
          <xsl:when test="@message"><xsl:value-of select="@message"/></xsl:when>
          <xsl:otherwise><xsl:value-of select="@location"/></xsl:otherwise>
       </xsl:choose>
       <blockquote>
       <xsl:if test="@message and @location"><b>Location:</b> <xsl:value-of select="@location"/><br/></xsl:if>
       <xsl:if test="@column and @line and (@column>-1 or @test>-1)">
       <b>Line:</b> <xsl:value-of select="@line"/> <br/>
       <b>Column:</b> <xsl:value-of select="@column"/> <br/>
       </xsl:if>
       <xsl:choose>
         <xsl:when test="@causeMessage and not(@causeMessage = @message)">
            <b>Message From Original Exception:</b> <xsl:value-of select="@causeMessage"/>
         </xsl:when>
       </xsl:choose>
       </blockquote>
    <br/>
  </xsl:template>

</xsl:stylesheet>
