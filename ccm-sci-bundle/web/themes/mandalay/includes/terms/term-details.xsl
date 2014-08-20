<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:terms="http://xmlns.redhat.com/london/terms/1.0"
    version="1.0">

    <xsl:template match="terms:termDetails">
        <table class="termDetails">
            <thead>
                <tr>
                    <th colspan="2">Term details</th>
                </tr>
            </thead>
            <tbody>
                <tr class="even">
                    <th>Unique ID:</th>
                    <td>
                        <xsl:value-of select="uniqueID"/>
                    </td>
                </tr>
                <tr class="odd">
                    <th>Name</th>
                    <td>
                        <xsl:value-of select="model/name"/>
                    </td>
                </tr>
                <tr class="even">
                    <th>Description:</th>
                    <td>
                        <xsl:value-of select="model/description"/>
                    </td>
                </tr>
                <tr class="odd">
                    <th>Shortcut:</th>
                    <td>
                        <xsl:value-of select="shortcut"/>
                    </td>
                </tr>
                <tr class="even">
                    <th>In A-Z:</th>
                    <td>
                        <xsl:value-of select="inAtoZ"/>
                    </td>
                </tr>
            </tbody>
            <tfoot>
                <tr>
                    <td></td>
                    <td>
                        <!--<a href="{terms:action[@name='edit']/@url}">
                            <img src="{$context-prefix}/assets/images/action-generic.png" width="14" height="14" border="0"/>
                        </a>-->
                        <a href="{terms:action[@name='edit']/@url}">Edit</a>
                        <xsl:text>&#160;</xsl:text>
                        <!--<a href="{terms:action[@name='delete']/@url}">
                            <img src="{$context-prefix}/assets/images/action-delete.png" width="14" height="14" border="0"/>
                        </a>-->
                        <a href="{terms:action[@name='delete']/@url}">Delete</a>
                        <xsl:text>&#160;</xsl:text>
                    </td>
                </tr>
            </tfoot>
        </table>
    </xsl:template>
  
</xsl:stylesheet>
