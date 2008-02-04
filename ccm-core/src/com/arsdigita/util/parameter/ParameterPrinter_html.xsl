<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:template match="/records">
    <html>
      <head>
        <title>WAF Configuration Reference</title>
        <style>
          h1 {
            font-size: large;
          }

          div.purpose {
            padding: 0 0 4px 0;
          }

          table.detail {
            border: 0;
            border-collapse: collapse;
            padding: 6px;
          }

          table.detail td, th {
            text-align: left;
            margin: 0;
            padding: 0 6px 0 0;
          }

          table.detail td {
            font-family: courier, monospace;
          }

          dt {
            padding: 8px 0 4px 0;
          }

          dt.required span.key {
            color: red;
            font-weight: bold;
          }

          span.key {
            font-family: courier, monospace;
          }
        </style>
      </head>
      <body>
        <h1>WAF Configuration Reference</h1>

        <p>Parameters appearing in <span style="color: red;
        font-weight: bold; font-family: courier, monospace">bold
        red</span> are required and have no default value.  They must
        be set in order to start a working WAF environment.</p>

        <dl>
          <xsl:for-each select="record/parameter">
            <xsl:sort select="name"/>

            <dt>
              <xsl:if test="required and not(default)">
                <xsl:attribute name="class">required</xsl:attribute>
              </xsl:if>

              <span class="key"><xsl:value-of select="name"/></span>

              <xsl:if test="title">
                - <xsl:value-of select="title"/>
              </xsl:if>
            </dt>

            <dd>
              <xsl:if test="purpose">
                <div class="purpose"><xsl:value-of select="purpose"/></div>
              </xsl:if>

              <table class="detail">
                <xsl:if test="format">
                  <tr>
                    <th>Format:</th>
                    <td><xsl:value-of select="format"/></td>
                  </tr>
                </xsl:if>

                <xsl:if test="example">
                  <tr>
                    <th>Example:</th>
                    <td><xsl:value-of select="example"/></td>
                  </tr>
                </xsl:if>

                <tr>
                  <xsl:choose>
                    <xsl:when test="default">
                      <th>Default:</th>
                      <td><xsl:value-of select="default"/></td>
                    </xsl:when>
                    <xsl:otherwise>
                      <th>Default:</th>
                      <td><em style="font-family: serif">None</em></td>
                    </xsl:otherwise>
                  </xsl:choose>
                </tr>
              </table>
            </dd>
          </xsl:for-each>
        </dl>

      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
