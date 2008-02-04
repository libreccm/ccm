<?xml version="1.0" encoding="UTF-8"?>

<!--
This stylesheet transforms junit_result_diff reports into HTML.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>
  <!-- solid line -->
  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="/acs6/rhdocs-man.css"></link>
      </head>
      <title>JUnit Test Results for <xsl:value-of select="junit_result_diff/@name" /></title>
      <body>
        <xsl:apply-templates select="junit_result_diff"/>
      </body>

    </html>
  </xsl:template>
  <xsl:template match="junit_result_diff">
        <H1>JUnit Test Results for <xsl:value-of select="@name" /></H1>
      <h2>Changelist <xsl:value-of select="@previous_changelist"/> vs <xsl:value-of select="@current_changelist"/></h2>
        <xsl:apply-templates select="diff"/>
        <xsl:apply-templates select="regressions" />
        <xsl:apply-templates select="missing_tests" />
        <h2>Test Results</h2>
        <ul>
          <xsl:apply-templates select="testcase" />
        </ul>
  </xsl:template>

    <xsl:template match="diff">
        <p>Prior Test Run: <xsl:value-of select="previous/@tests"/> Failures: <xsl:value-of select="previous/@failures"/> Errors: <xsl:value-of select="previous/@errors"/></p>
        <p>Current Test Run: <xsl:value-of select="current/@tests"/> Failures: <xsl:value-of select="current/@failures"/> Errors: <xsl:value-of select="current/@errors"/></p>
    </xsl:template>

  <xsl:template match="regressions">
    <h2>Regressions</h2>
    <p>These tests did not fail in the prior run.</p>
    <ul>
      <xsl:for-each select="regression">
        <li>Test: <xsl:value-of select="@name" /> Type: <xsl:value-of select="@type" /></li>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="missing_tests">
    <h2>Missing Tests</h2>
    <p>These tests have disappeared since the prior run.</p>
    <ul>
      <xsl:for-each select="missing">
        <li>Test: <xsl:value-of select="@name" /></li>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="testcase">
    <li>Test: <xsl:value-of select="@name" />
      <xsl:apply-templates select="failure" />
      <xsl:apply-templates select="error" />
    </li>
  </xsl:template>

  <xsl:template match="failure">
    <xsl:text> FAILED. </xsl:text> <xsl:value-of select="@message" /><p/>
    <table border="0" bgcolor="#E0E0E0" width="100%">
    <tr>
    <td>
    <pre class="PROGRAMLISTING">
    <xsl:value-of select="text()" />
    </pre>
    </td>
    </tr>
    </table>

  </xsl:template>

  <xsl:template match="error">
    <xsl:text> ERROR.</xsl:text><p/>
    <table border="0" bgcolor="#E0E0E0" width="100%">
    <tr>
    <td>
    <pre class="PROGRAMLISTING">
    <xsl:value-of select="text()" />
    </pre>
    </td>
    </tr>
    </table>

  </xsl:template>

</xsl:stylesheet>
