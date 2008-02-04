<?xml version="1.0" encoding="UTF-8"?>

<!--
This stylesheet transforms junit_index report into an index HTML.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>
  <!-- solid line -->
  <xsl:template match="junit_index">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="/acs6/rhdocs-man.css"></link>
      </head>
      <title>JUnit Test Results <xsl:value-of select="@previous_changelist"/> vs <xsl:value-of select="@current_changelist"/></title>
      <body>

          <H1>JUnit Test Results <xsl:value-of select="@previous_changelist"/> vs <xsl:value-of select="@current_changelist"/></H1>
              Numbers are: Current Count / Delta From Last Changelist<br/>
              Red higlighting indicates new errors.<br/>
    <p/>
          
          <table border="1">
              <tr>
                  <th>Test Name</th>
                  <th>Tests</th>
                  <th>Failures</th>
                  <th>Errors</th>
                  <th>New Tests</th>
                  <th>Missing Tests</th>
              </tr>
          <xsl:apply-templates select="test">
            <xsl:sort select="@warning" order="descending"/>
          </xsl:apply-templates>
          </table>
      </body>

    </html>
  </xsl:template>
    <xsl:template match="test">


    <tr>
        <xsl:choose>
            <xsl:when test="@warning ='true'">
        <xsl:attribute name="style">
        <xsl:text>color: red</xsl:text>
        </xsl:attribute>
            </xsl:when>
        </xsl:choose>
        <td>
            <xsl:element name = "a">
                <xsl:attribute name="href"><xsl:value-of select="@name"/>.html</xsl:attribute>
                <xsl:value-of select="@name"/>
            </xsl:element>
        </td>
        <td><xsl:value-of select="@tests"/> / <xsl:value-of select="@test_delta"/> </td>
        <td><xsl:value-of select="@failures"/> / <xsl:value-of select="@failure_delta"/> </td>
        <td><xsl:value-of select="@errors"/> / <xsl:value-of select="@error_delta"/> </td>
        <td><xsl:value-of select="@new_tests"/>  </td>
        <td><xsl:value-of select="@missing_tests"/>  </td>
    </tr>
    </xsl:template>
</xsl:stylesheet>
