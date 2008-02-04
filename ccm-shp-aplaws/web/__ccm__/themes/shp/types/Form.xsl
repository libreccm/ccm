<?xml version="1.0" encoding="utf-8"?>
<!-- Hier wird die Darstellung der ContentItems vom Typ Form realisiert. -->
<!-- Dafür werden auch alle Formular-spezifischen Inhalt hier defiiert,  -->
<!-- d.h. Input-Felder und der ContentType Form Section.                 -->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:shp="http://www.shp.de"
                exclude-result-prefixes="xsl bebop cms"
                version="1.0 RC 1">

  <!-- Dieses Template wird für die Behandlung von Formularen aufgerufen -->
  <xsl:template name="CT_Form_graphics">
    <form>
      <xsl:attribute name="method">post</xsl:attribute>
      <xsl:attribute name="name"><xsl:value-of select="./name"/></xsl:attribute>

      <xsl:choose>
        <xsl:when test="./remote = 'true'">
          <xsl:attribute name="action"><xsl:value-of select="./remoteUrl"/></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="action"><xsl:value-of select="@formAction"/></xsl:attribute>
          <input type="hidden" value="visited">
            <xsl:attribute name="name">form.<xsl:value-of select="name"/></xsl:attribute>
          </input>
        </xsl:otherwise>
      </xsl:choose>

      <!-- Verarbeite die Formular-Komponenten in der angegebenen Reihenfolge -->
      <!-- Überspringe alle com.arsdigita.formabuilder.Widget, da diese Fehler verursachen -->
      <!-- und die Informationen auch als Unterpunkt in WidgetLabel vohanden sind.         -->
      <xsl:for-each select="./form/component[
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
        <xsl:call-template name="shp:Form_Components"/>
      </xsl:for-each>

    </form>
  </xsl:template>

  <!-- Hier werden die einzelnen Komponenten unterschieden und entsprechende -->
  <!-- Templates aufgerufen                                                  -->
  <xsl:template name="shp:Form_Components">

      <!-- Unterscheide die einzelnen Komponentenarten -->
      <xsl:choose>

        <!-- Formular-Komponenten, die aus mehreren Teilen bestehen -->
        <!-- Form Section -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.cms.formbuilder.FormSectionWrapper'">
          <xsl:call-template name="shp:Form_Section"/>
        </xsl:when>

        <!-- Label mit Widget -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.WidgetLabel'">
          <xsl:call-template name="shp:Form_WidgetLabel"/>
        </xsl:when>

        <!-- CheckboxGroup -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentCheckboxGroup'">
          <xsl:call-template name="shp:Form_ButtonGroup"/>
        </xsl:when>

        <!-- RadiobuttonGroup -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentRadioGroup'">
          <xsl:call-template name="shp:Form_ButtonGroup"/>
        </xsl:when>

        <!-- SingleSelect -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSingleSelect'">
          <xsl:call-template name="shp:Form_Select"/>
        </xsl:when>

        <!-- MultipleSelect -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentMultipleSelect'">
          <xsl:call-template name="shp:Form_Select"/>
        </xsl:when>

        <!-- DataDrivenSelect -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.DataDrivenSelect'">
          <xsl:call-template name="shp:Form_Select"/>
        </xsl:when>


        <!-- Einfache Komponenten -->
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHeading'">
          <xsl:call-template name="shp:Form_Heading"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentText'">
          <xsl:call-template name="shp:Form_Text"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentTextField'">
          <xsl:call-template name="shp:Form_TextField"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentPassword'">
          <xsl:call-template name="shp:Form_Password"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHidden'">
          <xsl:call-template name="shp:Form_Hidden"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.HiddenIDGenerator'">
          <xsl:call-template name="shp:Form_HiddenIDGenerator"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentTextArea'">
          <xsl:call-template name="shp:Form_TextArea"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentEmailField'">
          <xsl:call-template name="shp:Form_EmailField"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentDate'">
          <xsl:call-template name="shp:Form_Date"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSubmit'">
          <xsl:call-template name="shp:Form_Button"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentOption'">
          <xsl:call-template name="shp:Form_Option"/>
        </xsl:when>

        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHorizontalRule'">
          <xsl:call-template name="shp:Form_Ruler"/>
        </xsl:when>

<!--
        <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.'">
          <xsl:call-template name="shp:Form_"/>
        </xsl:when>
-->

        <!-- Sonst gebe eine Fehlermeldung aus -->
        <xsl:otherwise>
          <xsl:call-template name="shp:Form_Unknown_Component"/>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>


  <!-- Hier kommen die Templates für die Formular-Komponenten, die untergeordnete Komponenten enthalten. -->
  <!-- Diese Komponenten bekommen ein umschließendes DIV-Tag und führen eine Rekursion aus.-->
  <xsl:template name="shp:Form_Section">
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
        <xsl:call-template name="shp:Form_Components"/>
      </xsl:for-each>
    </div>
  </xsl:template>

  <!-- WidgetLabel -->
  <xsl:template name="shp:Form_WidgetLabel">
    <div id="component">

      <!-- Untergeordnete Komonenten verarbeiten -->
      <xsl:for-each select="./widget">
        <xsl:sort data-type="number" select="./link/orderNumber"/>
        <xsl:call-template name="shp:Form_Components"/>
      </xsl:for-each>
    </div>
  </xsl:template>

  <xsl:template name="shp:Form_ButtonGroup">
    <div id="group">
      <xsl:call-template name="shp:Form_Title"/>

      <!-- Untergeordnete Komponenten verarbeiten -->
      <xsl:for-each select="./component">
        <xsl:sort data-type="number" select="./link/orderNumber"/>
        <xsl:call-template name="shp:Form_Components"/>
      </xsl:for-each>

      <!-- Other Option verarbeiten -->
      <xsl:call-template name="shp:Form_Other"/>

    </div>
  </xsl:template>

  <xsl:template name="shp:Form_Select">
    <div id="select">
      <xsl:call-template name="shp:Form_Title"/>

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
              <option value="">-- Bitte wählen --</option>
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
                <xsl:call-template name="shp:Form_Components"/>
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

      <xsl:call-template name="shp:Form_Other"/>

      <!-- Ende der Div-Box markieren, damit der floating Span komplett in der Div-Box ist -->
      <span id="boxend"></span>
    </div>
  </xsl:template>


  <!-- Ab hier kommen die Templates für die einfachen Komponenten -->
  <xsl:template name="shp:Form_Heading">
    <div id="heading">
      <xsl:value-of disable-output-escaping="yes" select="./description"/>
    </div>
  </xsl:template>

  <xsl:template name="shp:Form_Text">
    <div id="text">
      <xsl:value-of disable-output-escaping="yes" select="./description"/>
    </div>
  </xsl:template>

  <xsl:template name="shp:Form_TextField">
    <xsl:call-template name="shp:Form_Label"/>
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

  <xsl:template name="shp:Form_Password">
    <xsl:call-template name="shp:Form_Label"/>
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

  <xsl:template name="shp:Form_Hidden">
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

  <xsl:template name="shp:Form_HiddenIDGenerator">
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

  <xsl:template name="shp:Form_EmailField">
    <xsl:call-template name="shp:Form_Label"/>
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
<!--
       <xsl:choose>
         <xsl:when test="string-length(./defaultValue) > 0">
           <xsl:attribute name="value"><xsl:value-of select="./defaultValue"/></xsl:attribute>
         </xsl:when>
         <xsl:otherwise>
           <xsl:attribute name="value"><xsl:value-of select="/bebop:page/ui:userBanner/@primaryEmail"/>
         </xsl:otherwise>
       </xsl:choose>
-->
       <xsl:attribute name="value"></xsl:attribute>
      </input>

    </span>
  </xsl:template>

  <xsl:template name="shp:Form_Date">
    <xsl:call-template name="shp:Form_Label"/>
    <span id="date">

      <!-- Tag -->
      <input>
        <xsl:attribute name="type">text</xsl:attribute>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/>.day</xsl:attribute>
        <xsl:attribute name="size">2</xsl:attribute>
        <xsl:attribute name="maxlength">2</xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="./defaultValue/day"/></xsl:attribute>
      </input>
.
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
.
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
  
  <xsl:template name="shp:Form_TextArea">
    <div id="textarea">
    <xsl:call-template name="shp:Form_Title"/>
      <textarea>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/></xsl:attribute>
        <xsl:attribute name="rows"><xsl:value-of select="./rows"/></xsl:attribute>
        <xsl:attribute name="cols"><xsl:value-of select="./cols"/></xsl:attribute>
        <xsl:value-of select="./defaultValue"/>
      </textarea>
    </div>
  </xsl:template>

  <xsl:template name="shp:Form_Ruler">
    <span id="ruler">
      <hr />
    </span>
  </xsl:template>

  <xsl:template name="shp:Form_Option">

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

<!--
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

  <xsl:template name="shp:Form_Button">
    <span id="button">
      <input>
        <xsl:attribute name="type"><xsl:value-of select="./parameterName"/></xsl:attribute>
        <xsl:attribute name="name"><xsl:value-of select="./parameterName"/></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="./defaultValue"/></xsl:attribute>
      </input>
    </span>
  </xsl:template>

  <!-- Ausgabe der Fehlermeldung für unbekannte Komponenten -->
  <xsl:template name="shp:Form_Unknown_Component">
    <span style="color:#ff0000">
      !!! Unknown Component <xsl:value-of select="./defaultDomainClass"/> !!!
    </span>
  </xsl:template>

  <!-- Hilfstemplates -->
  <!-- Setze den Label der Komponente -->
  <xsl:template name="shp:Form_Label">
    <span id="label">
      <xsl:if test="./widgetrequired = 'true'">
        <xsl:attribute name="class">mandatory</xsl:attribute>
      </xsl:if>
      <xsl:value-of disable-output-escaping="yes" select="../label"/>
    </span>
  </xsl:template>

  <!-- Setze den Titel einer Box -->
  <xsl:template name="shp:Form_Title">
    <span id="title">
      <xsl:if test="./widgetrequired = 'true'">
        <xsl:attribute name="class">mandatory</xsl:attribute>
      </xsl:if>
      <xsl:value-of disable-output-escaping="yes" select="../label"/>
    </span>
  </xsl:template>

  <!-- Verarbeite die Other-Option -->
  <xsl:template name="shp:Form_Other">
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
<!--
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


