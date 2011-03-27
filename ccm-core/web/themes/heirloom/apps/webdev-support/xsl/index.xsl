<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:devsup="http://xmlns.redhat.com/waf/webdevsupport/1.0"
  version="1.0">
  
  <xsl:import href="../../../../packages/login/xsl/login_en.xsl"/> 
  
  <xsl:template match="devsup:configList">
    <script language="JavaScript1.2">
      <![CDATA[
        
        <!-- begin hiding


        function toggle(id) {
        var elTab = document.getElementById(id);

        if (elTab.style.display != "block") {
             elTab.style.display = "block";
        } else {
             elTab.style.display = "none";
        }
        return false;
        }
        // -->

        ]]>
    </script>
      
    <style type="text/css">
        table.config thead tr {
          background: rgb(168,168,168);
          padding: 5px;
        }
        table.config tr.even td {
          background: rgb(212,212,212);
        }
        table.config {
          margin: 0px;
          padding: 0px;
        }
        table.config tr {
          margin: 0px;
          padding: 0px;
        }
        table.config tr td {
          margin: 0px;
          padding: 3px;
        }
        table.config tr td table.details {
          border: 1px solid black;
          margin-left: 2em;
        }
        table.config tr td table.details tr th {
          text-align: right;
        }
    </style>
      

    <xsl:for-each select="application">

      <h2><xsl:value-of select="@key"/></h2>

      <xsl:variable name="appid">
        <xsl:value-of select="position()"/>
      </xsl:variable>
        

      <xsl:for-each select="context">
        <xsl:sort select="@class"/>

        <h3><xsl:value-of select="@class"/></h3>

        <xsl:variable name="ctxid">
          <xsl:value-of select="position()"/>
        </xsl:variable>
        

        <table class="config" cellpadding="0" cellspacing="0">
          <thead>
            <tr>
              <th>Name</th>
              <th>Value</th>
              <th>&#160;</th>
            </tr>
          </thead>
          <tbody>
            <xsl:for-each select="param">
              <xsl:sort select="@name"/>

              <xsl:variable name="id">
                <xsl:value-of select="concat('p',$appid, '_', $ctxid,'_', position())"/>
              </xsl:variable>
              
              <xsl:variable name="class">
                <xsl:choose>
                  <xsl:when test="(position() mod 2) = 0">
                    <xsl:text>odd</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>even</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <tr class="{$class}">
                <td><xsl:value-of select="@name"/></td>
                <td><xsl:value-of select="@value"/></td>
                <td><a href="#" onClick="return toggle('{$id}')">Toggle details &gt;&gt;&gt;</a></td>
              </tr>
              <tr class="{$class}">
                <td colspan="3">
                  <div id="{$id}" style="display: none">
                    <table class="details">
                      <tr>
                        <th>Class</th><td><xsl:value-of select="@class"/></td>
                      </tr>
                      <tr>
                        <th>Is Required</th><td><xsl:value-of select="@isRequired"/></td>
                      </tr>
                      <tr>
                        <th>Title</th><td><xsl:value-of select="@title"/></td>
                      </tr>
                      <tr>
                        <th>Purpose</th><td><xsl:value-of select="@purpose"/></td>
                      </tr>
                      <tr>
                        <th>Format</th><td><xsl:value-of select="@format"/></td>
                      </tr>
                      <tr>
                        <th>Example</th><td><xsl:value-of select="@example"/></td>
                      </tr>
                    </table>
                  </div>
                </td>
              </tr>
            </xsl:for-each>
          </tbody>
        </table>
      </xsl:for-each>
      <xsl:if test="count(context) = 0">
        <div>
          This application has no config records
        </div>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
