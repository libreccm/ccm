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
  Hier werden die Informationen aus dem FormBuilder verarbeitet 
-->

<!-- EN
  Processing informations from form builder
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
  xmlns:cms="http://www.arsdigita.com/cms/1.0" 
  xmlns:nav="http://ccm.redhat.com/navigation"
  xmlns:ui="http://www.arsdigita.com/ui/1.0"
  xmlns:mandalay="http://mandalay.quasiweb.de" 
  exclude-result-prefixes="xsl bebop cms nav ui mandalay"
  version="1.0">

  <!-- Hier werden die einzelnen Komponenten unterschieden und entsprechende -->
  <!-- Templates aufgerufen                                                  -->
  <xsl:template name="mandalay:Form_Components">

      <!-- Unterscheide die einzelnen Komponentenarten -->
      <xsl:choose>

        <!-- Formular-Komponenten, die aus mehreren Teilen bestehen -->
        <!-- Form Section -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.cms.formbuilder.FormSectionWrapper'">
          <xsl:call-template name="mandalay:Form_Section"/>
        </xsl:when>

        <!-- Label mit Widget -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.WidgetLabel'">
          <xsl:call-template name="mandalay:Form_WidgetLabel"/>
        </xsl:when>

        <!-- CheckboxGroup -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentCheckboxGroup'">
          <xsl:call-template name="mandalay:Form_ButtonGroup"/>
        </xsl:when>

        <!-- RadiobuttonGroup -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentRadioGroup'">
          <xsl:call-template name="mandalay:Form_ButtonGroup"/>
        </xsl:when>

        <!-- SingleSelect -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSingleSelect'">
          <xsl:call-template name="mandalay:Form_Select"/>
        </xsl:when>

        <!-- MultipleSelect -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentMultipleSelect'">
          <xsl:call-template name="mandalay:Form_Select"/>
        </xsl:when>

        <!-- DataDrivenSelect -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.DataDrivenSelect'">
          <xsl:call-template name="mandalay:Form_Select"/>
        </xsl:when>


        <!-- Einfache Komponenten -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHeading'">
          <xsl:call-template name="mandalay:Form_Heading"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentText'">
          <xsl:call-template name="mandalay:Form_Text"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentTextField'">
          <xsl:call-template name="mandalay:Form_TextField"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentPassword'">
          <xsl:call-template name="mandalay:Form_Password"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHidden'">
          <xsl:call-template name="mandalay:Form_Hidden"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.HiddenIDGenerator'">
          <xsl:call-template name="mandalay:Form_HiddenIDGenerator"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentTextArea'">
          <xsl:call-template name="mandalay:Form_TextArea"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentEmailField'">
          <xsl:call-template name="mandalay:Form_EmailField"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentDate'">
          <xsl:call-template name="mandalay:Form_Date"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSubmit'">
          <xsl:call-template name="mandalay:Form_Button"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentOption'">
          <xsl:call-template name="mandalay:Form_Option"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHorizontalRule'">
          <xsl:call-template name="mandalay:Form_Ruler"/>
        </xsl:when>

<!-- Template
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.'">
          <xsl:call-template name="mandalay:Form_"/>
        </xsl:when>
-->

        <!-- Sonst gebe eine Fehlermeldung aus -->
        <xsl:otherwise>
          <xsl:call-template name="mandalay:Form_Unknown_Component"/>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>


  <!-- Hier kommen die Templates für die Formular-Komponenten, die untergeordnete Komponenten enthalten. -->
  <!-- Diese Komponenten bekommen ein umschließendes DIV-Tag und führen eine Rekursion aus.-->
  <xsl:template name="mandalay:Form_Section">
    <div id="section">
      <span id="title">
        <xsl:value-of select="./formSectionItem/title"/>
      </span>

      <!-- Verarbeite die Formular-Komponenten in der angegebenen Reihenfolge -->
      <!-- Überspringe alle com.arsdigita.formabuilder.Widget, da diese Fehler verursachen -->
      <!-- und die Informationen auch als Unterpunkt in WidgetLabel vohanden sind.         -->
      <xsl:for-each select="./formSectionItem/formSection/component[
                              (
                                objectType != 'com.arsdigita.formbuilder.Widget' and 
                                objectType != 'com.arsdigita.formbuilder.DataDrivenSelect'
                              ) or 
                              (
                                defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSubmit' or
                                defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHidden' or
                                defaultDomainClass = 'com.arsdigita.formbuilder.HiddenIDGenerator'
                              )
                              ]">
        <xsl:sort data-type="number" select="./link/orderNumber"/>
        <xsl:call-template name="mandalay:Form_Components"/>
      </xsl:for-each>
    </div>
  </xsl:template>

  <!-- WidgetLabel -->
  <xsl:template name="mandalay:Form_WidgetLabel">
    <div id="component">

      <!-- Untergeordnete Komonenten verarbeiten -->
      <xsl:for-each select="./widget">
        <xsl:sort data-type="number" select="./link/orderNumber"/>
        <xsl:call-template name="mandalay:Form_Components"/>
      </xsl:for-each>
    </div>
  </xsl:template>

  <xsl:template name="mandalay:Form_ButtonGroup">
    <div id="group">
      <xsl:call-template name="mandalay:Form_Title"/>

      <!-- Untergeordnete Komponenten verarbeiten -->
      <xsl:for-each select="./component">
        <xsl:sort data-type="number" select="./link/orderNumber"/>
        <xsl:call-template name="mandalay:Form_Components"/>
      </xsl:for-each>

      <!-- Other Option verarbeiten -->
      <xsl:call-template name="mandalay:Form_Other"/>

    </div>
  </xsl:template>

  <xsl:template name="mandalay:Form_Select">
    <div id="select">
      <xsl:call-template name="mandalay:Form_Title"/>

        <select>
          <xsl:attribute name="name"><xsl:value-of select="./parameterName"/></xsl:attribute>

          <!-- Bei MultiSelects die entsprechenden Attribute übergeben -->
          <xsl:if test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentMultipleSelect' or (./defaultDomainClass = 'com.arsdigita.formbuilder.DataDrivenSelect' and ./multiple = 'true')">
            <xsl:attribute name="multiple">true</xsl:attribute>
          </xsl:if>

          <!-- Bei SingleSelects den ersten Eintrag anlegen, wenn er nicht vorhanden ist -->
          <xsl:if test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSingleSelect' or (./defaultDomainClass = 'com.arsdigita.formbuilder.DataDrivenSelect' and ./multiple = 'false')">
<!--
            <xsl:if test="(./component/parameterValue = '')]">
-->
          <option value="">
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'Form'" />
              <xsl:with-param name="id" select="'please_select'" />
            </xsl:call-template>
          </option>
<!--
            </xsl:if>
-->
          </xsl:if>

          <xsl:choose>

            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.DataDrivenSelect'">
              <!-- Untergeordnete Komponenten verarbeiten -->
              <xsl:for-each select="./selectOptions/option">
                <xsl:sort select="@label"/>
                <option>
                  <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
                  <xsl:value-of select="@label"/>
                </option>
              </xsl:for-each>
            </xsl:when>

            <xsl:otherwise>
              <!-- Untergeordnete Komponenten verarbeiten -->
              <xsl:for-each select="./component">
                <xsl:sort data-type="number" select="./link/orderNumber"/>
                <xsl:call-template name="mandalay:Form_Components"/>
              </xsl:for-each>
            </xsl:otherwise>

          </xsl:choose>

          <!-- Wenn es die OtherOption gibt, dann füge ein Sonstiges Feld ein -->
          <xsl:if test="./optiongroupother = 'true'">
            <option>
              <xsl:attribute name="value"><xsl:value-of select="./optiongroupothervalue"/></xsl:attribute>
              <xsl:value-of select="./optiongroupotherlabel"/>
            </option>
          </xsl:if>

        </select>

      <!-- Other Option verarbeiten -->
      <xsl:call-template name="mandalay:Form_Other"/>

      <!-- Ende der Div-Box markieren, damit der floating Span komplett in der Div-Box ist -->
      <span id="boxend"></span>
    </div>
  </xsl:template>


  <!-- Ab hier kommen die Templates für die einfachen Komponenten -->
  <xsl:template name="mandalay:Form_Heading">
    <div id="heading">
      <xsl:value-of disable-output-escaping="yes" select="./description"/>
    </div>
  </xsl:template>

  <xsl:template name="mandalay:Form_Text">
    <div id="text">
      <xsl:value-of disable-output-escaping="yes" select="./description"/>
    </div>
  </xsl:template>

  <xsl:template name="mandalay:Form_TextField">
    <xsl:call-template name="mandalay:Form_Label"/>
    <span id="textfield">
      <input>
        <xsl:attribute name="type">text</xsl:attribute>
        <xsl:if test="./size > 0">
          <xsl:attribute name="size"><xsl:value-of select="./size"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="./size = 0 and ./maxlength > 0">
          <xsl:attribute name="size"><xsl:value-of select="./maxlength"/></xsl:attribute>
        </xsl:if>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/></xsl:attribute>
        <xsl:if test="./maxlength > 0">
          <xsl:attribute name="maxlength"><xsl:value-of select="./maxlength"/></xsl:attribute>
        </xsl:if>
        <xsl:attribute name="value"><xsl:value-of select="./defaultValue"/></xsl:attribute>
      </input>
    </span>
  </xsl:template>

  <xsl:template name="mandalay:Form_Password">
    <xsl:call-template name="mandalay:Form_Label"/>
    <span id="textfield">
      <input>
        <xsl:attribute name="type">password</xsl:attribute>
        <xsl:if test="./size > 0">
          <xsl:attribute name="size"><xsl:value-of select="./size"/></xsl:attribute>
        </xsl:if>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/></xsl:attribute>
        <xsl:if test="./maxlength > 0">
          <xsl:attribute name="maxlength"><xsl:value-of select="./maxlength"/></xsl:attribute>
        </xsl:if>
        <xsl:attribute name="value"><xsl:value-of select="./defaultValue"/></xsl:attribute>
      </input>
    </span>
  </xsl:template>

  <xsl:template name="mandalay:Form_Hidden">
    <span id="hidden">
      <input>
        <xsl:attribute name="type">hidden</xsl:attribute>
        <xsl:if test="./size > 0">
          <xsl:attribute name="size"><xsl:value-of select="./size"/></xsl:attribute>
        </xsl:if>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/></xsl:attribute>
        <xsl:if test="./maxlength > 0">
          <xsl:attribute name="maxlength"><xsl:value-of select="./maxlength"/></xsl:attribute>
        </xsl:if>
        <xsl:attribute name="value"><xsl:value-of select="./defaultValue"/></xsl:attribute>
      </input>
    </span>
  </xsl:template>

  <xsl:template name="mandalay:Form_HiddenIDGenerator">
    <span id="hidden">
      <input>
        <xsl:attribute name="type">hidden</xsl:attribute>
        <xsl:if test="./size > 0">
          <xsl:attribute name="size"><xsl:value-of select="./size"/></xsl:attribute>
        </xsl:if>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/></xsl:attribute>
        <xsl:if test="./maxlength > 0">
          <xsl:attribute name="maxlength"><xsl:value-of select="./maxlength"/></xsl:attribute>
        </xsl:if>
      </input>
    </span>
  </xsl:template>

  <xsl:template name="mandalay:Form_EmailField">
    <xsl:call-template name="mandalay:Form_Label"/>
    <span id="textfield">
      <input>
        <xsl:attribute name="type">text</xsl:attribute>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/></xsl:attribute>

        <xsl:if test="./size > 0">
          <xsl:attribute name="size"><xsl:value-of select="./size"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="./maxlength > 0">
          <xsl:attribute name="maxlength"><xsl:value-of select="./maxlength"/></xsl:attribute>
        </xsl:if>
        <xsl:choose>
          <xsl:when test="string-length(./defaultValue) > 0">
            <xsl:attribute name="value"><xsl:value-of select="./defaultValue"/></xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="value">
              <xsl:value-of select="/bebop:page/ui:userBanner/@primaryEmail"/>
            </xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
      </input>

    </span>
  </xsl:template>

  <xsl:template name="mandalay:Form_Date">
    <xsl:call-template name="mandalay:Form_Label"/>
    <span id="date">

<!-- setting ??-->
<!-- static ??-->

      <!-- Tag -->
      <input>
        <xsl:attribute name="type">text</xsl:attribute>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/>.day</xsl:attribute>
        <xsl:attribute name="size">2</xsl:attribute>
        <xsl:attribute name="maxlength">2</xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="./defaultValue/day"/></xsl:attribute>
      </input>

      <!-- Monat -->
      <select>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/>.month</xsl:attribute>
        <xsl:for-each select="./monthList/month">
          <xsl:sort data-type="number" select="@value"/>
          <option>
            <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
            <xsl:if test="@selected = 'selected'">
              <xsl:attribute name="selected"><xsl:value-of select="@selected"/></xsl:attribute>
            </xsl:if>
            <xsl:value-of select="."/>
          </option>
        </xsl:for-each>
      </select>
      
      <!-- Jahr -->
      <select>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/>.year</xsl:attribute>
        <xsl:for-each select="./yearList/year">
          <xsl:sort data-type="number" order="descending" select="@value"/>
          <option>
            <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
            <xsl:if test="@selected = 'selected'">
              <xsl:attribute name="selected"><xsl:value-of select="@selected"/></xsl:attribute>
            </xsl:if>
            <xsl:value-of select="."/>
          </option>
        </xsl:for-each>
      </select>
    </span>
  </xsl:template>
  
  <xsl:template name="mandalay:Form_TextArea">
    <div id="textarea">
    <xsl:call-template name="mandalay:Form_Title"/>
      <textarea>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/></xsl:attribute>
        <xsl:attribute name="rows"><xsl:value-of select="./rows"/></xsl:attribute>
        <xsl:attribute name="cols"><xsl:value-of select="./cols"/></xsl:attribute>
        <xsl:value-of select="./defaultValue"/>
      </textarea>
    </div>
  </xsl:template>

  <xsl:template name="mandalay:Form_Ruler">
    <span id="ruler">
      <hr />
    </span>
  </xsl:template>

  <xsl:template name="mandalay:Form_Option">
    <!-- Unterscheide zwischen ButtonGroups und Select -->
    <xsl:choose>

      <!-- ButtonGroups -->
      <xsl:when test="../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentCheckboxGroup' or ../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentRadioGroup'">
        <div id="option">
          <input>

            <!-- Unterscheide die Art der Auswahlliste-->
            <xsl:choose>

              <!-- CheckboxGroup-->
              <xsl:when test="../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentCheckboxGroup'">
                <xsl:attribute name="type">checkbox</xsl:attribute>
              </xsl:when>

              <!-- RadioButtonGroup -->
              <xsl:when test="../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentRadioGroup'">
                <xsl:attribute name="type">radio</xsl:attribute>
              </xsl:when>

<!-- Template
              <xsl:when test="../defaultDomainClass = 'com.arsdigita.formbuilder.Persistent">
                <xsl:attribute name="type"></xsl:attribute>
              </xsl:when>
-->
            </xsl:choose>

            <xsl:attribute name="name"><xsl:value-of select="../parameterName"/></xsl:attribute>
            <xsl:attribute name="id"><xsl:value-of select="../parameterName"/>:<xsl:value-of select="./parameterValue"/></xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="./parameterValue"/></xsl:attribute>
          </input>
          <label>
            <xsl:attribute name="for"><xsl:value-of select="../parameterName"/>:<xsl:value-of select="./parameterValue"/></xsl:attribute>
            <xsl:value-of disable-output-escaping="yes" select="./label"/>
          </label>
        </div>
      </xsl:when>

      <!-- Selects -->
      <xsl:when test="../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSingleSelect' or ../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentMultipleSelect'">
        <option>
          <xsl:attribute name="value"><xsl:value-of select="./parameterValue"/></xsl:attribute>
          <xsl:value-of disable-output-escaping="yes" select="./label"/>
        </option>
      </xsl:when>

    </xsl:choose>
  </xsl:template>

  <xsl:template name="mandalay:Form_Button">
    <span id="button">
      <input>
        <xsl:attribute name="type"><xsl:value-of select="./parameterName"/></xsl:attribute>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="./defaultValue"/></xsl:attribute>
      </input>
    </span>
  </xsl:template>

  <!-- Ausgabe der Fehlermeldung für unbekannte Komponenten -->
  <xsl:template name="mandalay:Form_Unknown_Component">
    <span style="color:#ff0000">
      !!! Unknown Component <xsl:value-of select="./defaultDomainClass"/> !!!
    </span>
  </xsl:template>

  <!-- Hilfstemplates -->
  <!-- Setze den Label der Komponente -->
  <xsl:template name="mandalay:Form_Label">
    <span id="label">
      <xsl:if test="./widgetrequired = 'true'">
        <xsl:attribute name="class">mandatory</xsl:attribute>
      </xsl:if>
      <xsl:value-of disable-output-escaping="yes" select="../label"/>
    </span>
  </xsl:template>

  <!-- Setze den Titel einer Box -->
  <xsl:template name="mandalay:Form_Title">
    <span id="title">
      <xsl:if test="./widgetrequired = 'true'">
        <xsl:attribute name="class">mandatory</xsl:attribute>
      </xsl:if>
      <xsl:value-of disable-output-escaping="yes" select="../label"/>
    </span>
  </xsl:template>

  <!-- Verarbeite die Other-Option -->
  <xsl:template name="mandalay:Form_Other">
    <xsl:if test="./optiongroupother = 'true'">
      <div>
        <xsl:choose>

          <!-- Selects -->
          <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSingleSelect' or ./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentMultipleSelect' or ./defaultDomainClass = 'com.arsdigita.formbuilder.DataDrivenSelect'">
            <xsl:attribute name="id">other</xsl:attribute>
            <xsl:if test="./optiongroupotherheight = 1">
              <span id="label">
                <xsl:value-of select="./optiongroupotherlabel"/>
              </span>
            </xsl:if>
            <xsl:if test="./optiongroupotherheight > 1">
              <span id="title">
                <xsl:value-of select="./optiongroupotherlabel"/>
              </span>
            </xsl:if>
          </xsl:when>

          <xsl:otherwise> 
            <xsl:attribute name="id">option</xsl:attribute>
            <input>

              <!-- Unterscheide die Art der Auswahlliste-->
              <xsl:choose>

                <!-- CheckboxGroup-->
                <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentCheckboxGroup'">
                  <xsl:attribute name="type">checkbox</xsl:attribute>
                </xsl:when>

                <!-- RadioButtonGroup -->
                <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentRadioGroup'">
                  <xsl:attribute name="type">radio</xsl:attribute>
                </xsl:when>

                <!-- -->
<!-- Template
                <xsl:when test="../defaultDomainClass = 'com.arsdigita.formbuilder.Persistent">
                  <xsl:attribute name="type"></xsl:attribute>
                </xsl:when>
-->
              </xsl:choose>

              <xsl:attribute name="name"><xsl:value-of select="./parameterName"/></xsl:attribute>
              <xsl:attribute name="id"><xsl:value-of select="./parameterName"/>:<xsl:value-of select="./optiongroupotherlabel"/></xsl:attribute>
              <xsl:attribute name="value"><xsl:value-of select="./optiongroupothervalue"/></xsl:attribute>
            </input>
          </xsl:otherwise>

        </xsl:choose>

        <!-- Eingabefeld -->
          <xsl:if test="./optiongroupotherheight = 1">
            <label>
              <xsl:attribute name="for"><xsl:value-of select="./parameterName"/>:<xsl:value-of select="./optiongroupotherlabel"/></xsl:attribute>
              <input>
                <xsl:attribute name="name"><xsl:value-of select="./parameterName"/>.other</xsl:attribute>
                <xsl:attribute name="width"><xsl:value-of select="./optiongroupotherwidth"/></xsl:attribute>
              </input>
            </label>
          </xsl:if>
          <xsl:if test="./optiongroupotherheight > 1">
            <textarea>
              <xsl:attribute name="name"><xsl:value-of select="./parameterName"/>.other</xsl:attribute>
              <xsl:attribute name="rows"><xsl:value-of select="./optiongroupotherheight"/></xsl:attribute>
              <xsl:attribute name="cols"><xsl:value-of select="./optiongroupotherwidth"/></xsl:attribute>
            </textarea>
          </xsl:if>

      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
