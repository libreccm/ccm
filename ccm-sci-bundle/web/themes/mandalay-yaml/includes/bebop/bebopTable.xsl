<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
  
    This file is part of Mandalay.

    Mandalay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Mandalay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.
-->

<!-- DE
  Hier werden die Beboptabellen verarbeitet.
-->

<!-- En
  Processing bebop tables
-->

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  exclude-result-prefixes="xsl bebop cms nav mandalay" 
  version="1.0">
  
  <!-- DE Erzeuge Tabelle. Da die einzelnen Bestandteile (thead, tbody und tfoot) nicht 
          in der von HTML vorgesehenen Reihenfolge im XML stehen, müssen diese hier in
          der richtigen Reihenfolge manuell aufgerufen werden. -->
  <!-- EN Create a table. Because the parts of a table (thead, tbody and tfoot) are not
          in the required order for html, these templates have to be called manually in
          correct order. -->
  <xsl:template match="bebop:table">
    <table>
      <xsl:call-template name="mandalay:processAttributes"/>
      <xsl:apply-templates select="bebop:thead"/>
      <xsl:apply-templates select="bebop:tfoot"/>
      <xsl:apply-templates select="bebop:tbody"/>
    </table>
  </xsl:template>
  
  <!-- DE Tabellenkopf -->
  <!-- EN Table header -->
  <xsl:template match="bebop:thead">
    <thead>
      <tr>
        <xsl:call-template name="mandalay:processAttributes"/>
        <xsl:apply-templates mode="tableHead"/>
      </tr>
    </thead>
  </xsl:template>

  <!-- DE Tabellenkörper -->
  <!-- EN Table body -->
  <xsl:template match="bebop:tbody">
    <tbody>
      <xsl:call-template name="mandalay:processAttributes"/>
      <xsl:apply-templates/>
    </tbody>
  </xsl:template>
  
  <!-- DE Tabellenfuß -->
  <!-- EN Table footer -->
  <xsl:template match="bebop:tfoot">
    <tfoot>
      <xsl:call-template name="mandalay:processAttributes"/>
      <xsl:apply-templates/>
    </tfoot>
  </xsl:template>
  
  <!-- DE Einzeugt eine Zeile in der Tabelle. Kann diese Zeile alternierend mit zwei verschieden
          Klassen versehen. -->
  <!-- EN Creates a table row. is able to set two different class attributes -->
  <xsl:template match="bebop:trow">
    <tr>
      <xsl:call-template name="mandalay:processAttributes"/>
      <xsl:choose>
        <xsl:when test="(../@striped or ../../@class = 'dataTable') and (position() mod 2) = 1">
          <xsl:attribute name="class">bebopTableRowOdd</xsl:attribute>
        </xsl:when>
        <xsl:when test="(../@striped or ../../@class = 'dataTable') and (position() mod 2) = 0">
          <xsl:attribute name="class">bebopTableRowEven</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="class">bebopTableRow</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates mode="table"/>
    </tr>
  </xsl:template>

  <!-- DE Eine Tabellenzelle für den Tabellenkopf -->
  <!-- EN A table cell for table header -->
  <xsl:template match="bebop:cell" mode="tableHead">
    <th>
      <xsl:call-template name="mandalay:processAttributes"/>
      <xsl:apply-templates select="."/>
    </th>
  </xsl:template>
  
  <!-- DE Eine Tabellenzelle für den Tabellenkörper oder Tabellenfuß -->
  <!-- EN A table cell for table body or table footer -->
  <xsl:template match="bebop:cell" mode="table">
    <td>
      <xsl:call-template name="mandalay:processAttributes"/>
        <xsl:apply-templates/>      
    </td>
  </xsl:template>

  <!-- DE Eine Bebop-Zelle. Dieses Tag ist stark überladen. Es wird an vielen verschiedenen
          Stellen verwendet. Diese speziellen Versionen werden in Mandalay in den entsprechenden
          Templatedateien verarbeitet. -->
  <!-- EN A bebop cell. This tag is heavily overloaded. It is used in many different places.
          These special versions of bebop:cell are defined in the corresponding template file
          of Mandalay. -->
  <xsl:template match="bebop:cell">
    <xsl:call-template name="mandalay:processAttributes"/>
    <xsl:apply-templates/>
  </xsl:template>
 
 
  <!-- DE Sonderbehandlung für die grafischen Checkboxen -->
  <!-- EN Special treatment for graphical checkboxes -->
  <xsl:template match="bebop:link[@class = 'checkBoxChecked']">
    <xsl:call-template name="bebop:link">
      <xsl:with-param name="alt" select="'[X]'"/>
      <xsl:with-param name="title" select="'[X]'"/>
      <xsl:with-param name="src">
        <xsl:call-template name="mandalay:linkParser">
          <xsl:with-param name="link">
            <xsl:call-template name="mandalay:getSetting">
              <xsl:with-param name="module" select="'bebop'"/>
              <xsl:with-param name="setting" select="'table/setImage/checkboxChecked'"/>
              <xsl:with-param name="default" select="'/images/bebop/checkBoxChecked.gif'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="prefix" select="$theme-prefix"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="bebop:link[@class = 'checkBoxUnchecked']">
    <xsl:call-template name="bebop:link">
      <xsl:with-param name="alt" select="'[ ]'"/>
      <xsl:with-param name="title" select="'[ ]'"/>
      <xsl:with-param name="src">
        <xsl:call-template name="mandalay:linkParser">
          <xsl:with-param name="link">
            <xsl:call-template name="mandalay:getSetting">
              <xsl:with-param name="module" select="'bebop'"/>
              <xsl:with-param name="setting" select="'table/setImage/checkboxUnchecked'"/>
              <xsl:with-param name="default" select="'/images/bebop/checkBoxUnchecked.gif'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="prefix" select="$theme-prefix"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="bebop:label[@class = 'checkBoxGreyChecked']">
    <xsl:apply-templates select="." mode='image'>
      <xsl:with-param name="alt" select="'{X}'"/>
      <xsl:with-param name="title" select="'{X}'"/>
      <xsl:with-param name="src">
        <xsl:call-template name="mandalay:linkParser">
          <xsl:with-param name="link">
            <xsl:call-template name="mandalay:getSetting">
              <xsl:with-param name="module" select="'bebop'"/>
              <xsl:with-param name="setting" select="'table/setImage/checkboxGreyChecked'"/>
              <xsl:with-param name="default" select="'/images/bebop/checkBoxGreyChecked.gif'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="prefix" select="$theme-prefix"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="bebop:label[@class = 'checkBoxGreyUnchecked']">
    <xsl:apply-templates select="." mode='image'>
      <xsl:with-param name="alt" select="'{ }'"/>
      <xsl:with-param name="title" select="'{ }'"/>
      <xsl:with-param name="src">
        <xsl:call-template name="mandalay:linkParser">
          <xsl:with-param name="link">
            <xsl:call-template name="mandalay:getSetting">
              <xsl:with-param name="module" select="'bebop'"/>
              <xsl:with-param name="setting" select="'table/setImage/checkboxGreyUnchecked'"/>
              <xsl:with-param name="default" select="'/images/bebop/checkBoxGreyUnchecked.gif'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="prefix" select="$theme-prefix"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:apply-templates>
  </xsl:template>
  
</xsl:stylesheet>
