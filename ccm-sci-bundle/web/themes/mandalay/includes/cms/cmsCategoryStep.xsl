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
  Hier werden die Tags aus cmsCategoryStep verarbeitet
-->

<!-- EN
  Processing all the tags from cmsCategoryStep
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
  xmlns:cms="http://www.arsdigita.com/cms/1.0"
  xmlns:nav="http://ccm.redhat.com/navigation" 
  xmlns:mandalay="http://mandalay.quasiweb.de"
  exclude-result-prefixes="xsl bebop cms nav mandalay" 
  version="1.0">
  
  <!-- DE Anfang des CategoryStep -->
  <!-- EN Begin of category step -->
  <xsl:template match="cms:categoryStep">
    <h2>
      <xsl:call-template name="mandalay:getStaticText">
        <xsl:with-param name="module" select="'cms'"/>
        <xsl:with-param name="id" select="'categoryStep/header'"/>
      </xsl:call-template>
    </h2>
    <xsl:apply-templates/>
  </xsl:template>
  
  <!-- DE Hier werden die verschiedenen Kategorienbäume angezeigt -->
  <!-- EN Show all category roots -->
  <xsl:template match="cms:categoryRoots">
    <dl class="cmsCategoryRoots">
      <xsl:apply-templates/>
    </dl>
  </xsl:template>
  
  <!-- DE Zeige die zugewiesenen Kategorien und einen Link zum Hinzufügen von weiteren Kategorien an-->
  <!-- EN Show selected categories and a link zu select more -->
  <xsl:template match="cms:categoryRoot">
    
    <xsl:variable name="catName" select="@name"/>

    <dt class="cmsCategoryRoot">
      <xsl:value-of select="@name"/>
    </dt>
    <dd>
      <!-- DE Hier werden zwei verschiedene Links verwendet. addAction ist dabei der Link, der
              im folgenden kein Javascript verwendet. addJSAction verwendet hingegen Javascript
              und damit AJAX für die Manipulation der Kategorien. Der onClick-Event-Handler über-
              schreibt dabei den Standardlink mit der Javascript-Variante, wenn Javascript ausge-
              führt wird. -->
      <!-- EN Using two kinds of links. First, non-javascript link form addAction. Second, a 
              javascript link using ajax to manipulate categories. For the second method, the 
              onClick-event handler is overwriting the html link if javscript is running. -->
      <a href="{@addAction}" onclick="this.href='{@addJSAction}';">
        <xsl:call-template name="mandalay:getStaticText">
          <xsl:with-param name="module" select="'cms'"/>
          <xsl:with-param name="id" select="'categoryStep/addCategories'"/>
        </xsl:call-template>
      </a>
      <xsl:choose>
        <xsl:when test="count(../../cms:itemCategories/cms:itemCategory[starts-with(@path, $catName)]) = 0">
          <div>
            <xsl:call-template name="mandalay:getStaticText">
              <xsl:with-param name="module" select="'cms'"/>
              <xsl:with-param name="id" select="'categoryStep/noCategories'"/>
            </xsl:call-template>
          </div>
        </xsl:when>
        <xsl:otherwise>
          <ul>
            <xsl:apply-templates select="../../cms:itemCategories/cms:itemCategory[starts-with(@path, $catName)]" mode="list">
              <xsl:sort select="@path"/>
            </xsl:apply-templates>
          </ul>
        </xsl:otherwise>
      </xsl:choose>
    </dd>
    <br />
  </xsl:template>

  <!-- DE Zeige eine zugewiesene Kategorie an und biete die Möglichkeit, diese wieder zu entfernen. Da
          dieses Template nur aus dem cms:categoryRoot heraus aufgerufen werden soll, wird hier der 
          mode="list" gesetzt. Sonst wird die Liste ein zweites Mal unterhalb der Liste der Kategorien
          eingefügt. -->
  <!-- EN Show an assigned category and a link to remove this assignment. Because this template is meant
          to be called only by cms:categoryRoot, there is a mode="list" added. Otherwise there will be
          a duplicated list below the list of category roots. -->
  <xsl:template match="cms:itemCategory" mode="list">
    <xsl:variable name="setDeleteLink">
      <xsl:call-template name="mandalay:getSetting">
        <xsl:with-param name="module" select="'cms'"/>
        <xsl:with-param name="setting" select="'categoryStepSummary/setDeleteLink'"/>
        <xsl:with-param name="default" select="'false'"/>
      </xsl:call-template>
    </xsl:variable>
    
    <li>
      <xsl:choose>
        <xsl:when test="$setDeleteLink = 'true' and @deleteAction">
          <a href="{@deleteAction}">
            <xsl:attribute name="title">
              <xsl:call-template name="mandalay:getStaticText">
                <xsl:with-param name="module" select="'cms'"/>
                <xsl:with-param name="id" select="'categoryStep/removeCategory'"/>
              </xsl:call-template>
            </xsl:attribute>
            <img alt="[X]">
              <xsl:attribute name="src">
                <xsl:call-template name="mandalay:linkParser">
                  <xsl:with-param name="link">
                    <xsl:call-template name="mandalay:getSetting">
                      <xsl:with-param name="module" select="'cms'"/>
                      <xsl:with-param name="setting" select="'setImage/categoryDelete'"/>
                      <xsl:with-param name="default" select="'/images/cms/categoryDelete.png'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="prefix" select="$theme-prefix"/>
                </xsl:call-template>
              </xsl:attribute>
            </img>
            &nbsp;
            <xsl:value-of select="@name"/>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@name"/>
        </xsl:otherwise>
      </xsl:choose>
    </li>
  </xsl:template>

  <!-- DE Zeige die Kategorien zum Hinzufügen an -->
  <!-- EN Show categories to select -->
  <xsl:template match="cms:categoryWidget">
<!--    <script type="text/javascript" src="/assets/prototype.js"/> -->
    <script type="text/javascript" src="/assets/jquery.js"/>
    <script type="text/javascript" src="{$theme-prefix}/includes/cms/category-step.js"/>
    <xsl:choose>
      <xsl:when test="@mode='javascript'">
        <ul>
          <xsl:apply-templates select="cms:category" mode="javascript"/>
        </ul>
        <xsl:apply-templates select="cms:selectedCategories"/>
      </xsl:when>
      <xsl:otherwise>
        <select name="@name" size="30" multiple="multiple">
          <xsl:apply-templates mode="plain">
            <xsl:sort data-type="number" select="@sortKey"/>
          </xsl:apply-templates>
        </select>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- DE -->
  <!-- EN -->
  <xsl:template match="cms:selectedCategories">
    <select id="catWdHd" name="{../@name}" size="5" multiple="multiple" style="display: none">
      <xsl:apply-templates select="cms:category" mode="hidden"/>
    </select>
  </xsl:template>
  
  <!-- DE -->
  <!-- EN -->
  <xsl:template match="cms:category" mode="hidden">
    <option value="{@id}">
      <xsl:value-of select="@id"/>
    </option>
    <xsl:apply-templates select="cms:category" mode="hidden"/>
  </xsl:template>
  
  <!-- DE Teile des Kategorienbaums per AJAX aufklappen -->
  <!-- EN Toggle parts of the category tree with AJAX -->
  <!-- DE cms:category wird in zwei verschiedenen Syntax verwendet. Die andere ist in
          cmsSummary zu finden. -->
  <!-- EN cms:category is using to different syntax. The other one is located in cmsSummary. -->
  <xsl:template match="cms:category" mode="javascript">
    
    <xsl:variable name="onlyOneLevel">
      <xsl:choose>
        <xsl:when test="not(@root = '1' or cms:category/@expand = 'all') and not(//cms:categoryWidget)">
          <xsl:value-of select="'yes'"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'no'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <li id="catSelf{@id}">
      
      <!-- DE Baumfunktionen (auf- und zuklappen) -->
      <!-- EN Treefunctions (expand and collapse) -->
      <xsl:variable name="treeToggleMode">
        <xsl:choose>
          <xsl:when test="@root = '1' or cms:category/@expand = 'all'">
            <xsl:text>catBranchToggle</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>catToggle</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="selCats">
        <xsl:for-each select="//cms:categoryWidget/cms:selectedCategories/cms:category">
          <xsl:choose>
            <xsl:when test="position() != last()">
              <xsl:value-of select="concat(@id, ', ')"/>
            </xsl:when>
            <xsl:otherwise >
              <xsl:value-of select="@id"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="cms:category and $onlyOneLevel = 'no'">
          <a id="catTreeToggleLink{@node-id}" href="#" onclick="{$treeToggleMode}('{@node-id}', '{$selCats}');">
            <img id="catTreeToggleImage{@node-id}" alt="-">
              <xsl:attribute name="src">
                <xsl:call-template name="mandalay:linkParser">
                  <xsl:with-param name="link">
                    <xsl:call-template name="mandalay:getSetting">
                      <xsl:with-param name="module" select="'cms'"/>
                      <xsl:with-param name="setting" select="'setImage/categoryCollapse'"/>
                      <xsl:with-param name="default" select="'/images/cms/categoryCollapse.png'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="prefix" select="$theme-prefix"/>
                </xsl:call-template>
              </xsl:attribute>
            </img>
          </a>
        </xsl:when>
        <xsl:when test="(@root = '1' and not(cms:category)) or ($onlyOneLevel = 'yes' and cms:category)">
          <a id="catTreeToggleLink{@node-id}" href="#" onclick="{$treeToggleMode}('{@node-id}', '{$selCats}');">
            <img id="catTreeToggleImage{@node-id}" alt="+">
              <xsl:attribute name="src">
                <xsl:call-template name="mandalay:linkParser">
                  <xsl:with-param name="link">
                    <xsl:call-template name="mandalay:getSetting">
                      <xsl:with-param name="module" select="'cms'"/>
                      <xsl:with-param name="setting" select="'setImage/categoryExpand'"/>
                      <xsl:with-param name="default" select="'/images/cms/categoryExpand.png'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="prefix" select="$theme-prefix"/>
                </xsl:call-template>
              </xsl:attribute>
            </img>  
          </a>
        </xsl:when>
        <xsl:otherwise>
          <img id="catTreeToggleImage{@node-id}" alt=" ">
            <xsl:attribute name="src">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link">
                  <xsl:call-template name="mandalay:getSetting">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="setting" select="'setImage/categoryNode'"/>
                    <xsl:with-param name="default" select="'/images/cms/categoryNode.png'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="prefix" select="$theme-prefix"/>
              </xsl:call-template>
            </xsl:attribute>
          </img>
        </xsl:otherwise>
      </xsl:choose>
      &nbsp;
      
      <!-- DE Kategorien wählen -->
      <!-- EN Choose categories -->
      <xsl:choose>
        <xsl:when test="@isSelected = '1'">
          <a id="catToggleLink{@id}" href="#" onclick="catDeselect({@id});">
            <img id="catToggleImage{@id}" alt="[X]">
              <xsl:attribute name="src">
                <xsl:call-template name="mandalay:linkParser">
                  <xsl:with-param name="link">
                    <xsl:call-template name="mandalay:getSetting">
                      <xsl:with-param name="module" select="'cms'"/>
                      <xsl:with-param name="setting" select="'setImage/categorySelected'"/>
                      <xsl:with-param name="default" select="'/images/cms/categorySelected.gif'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="prefix" select="$theme-prefix"/>
                </xsl:call-template>
              </xsl:attribute>
            </img>
          </a>
        </xsl:when>
        <xsl:when test="@isAbstract = '1'">
          <img id="catToggleImage{@id}" alt="   ">
            <xsl:attribute name="src">
              <xsl:call-template name="mandalay:linkParser">
                <xsl:with-param name="link">
                  <xsl:call-template name="mandalay:getSetting">
                    <xsl:with-param name="module" select="'cms'"/>
                    <xsl:with-param name="setting" select="'setImage/categoryAbstract'"/>
                    <xsl:with-param name="default" select="'/images/cms/categoryAbstract.gif'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="prefix" select="$theme-prefix"/>
              </xsl:call-template>
            </xsl:attribute>
          </img>
        </xsl:when>
        <xsl:otherwise>
          <a id="catToggleLink{@id}" href="#" onclick="catSelect({@id});">
            <img id="catToggleImage{@id}" alt="[ ]" title="Select">
              <xsl:attribute name="src">
                <xsl:call-template name="mandalay:linkParser">
                  <xsl:with-param name="link">
                    <xsl:call-template name="mandalay:getSetting">
                      <xsl:with-param name="module" select="'cms'"/>
                      <xsl:with-param name="setting" select="'setImage/categoryUnselected'"/>
                      <xsl:with-param name="default" select="'/images/cms/categoryUnselected.gif'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="prefix" select="$theme-prefix"/>
                </xsl:call-template>
              </xsl:attribute>
            </img>
          </a>
        </xsl:otherwise>
      </xsl:choose>
      &nbsp;
      
      <!-- DE Name der Kategorie -->
      <!-- EN category name -->
      <xsl:value-of select="@name"/>
      <ul id="catBranch{@node-id}">
        <xsl:if test="$onlyOneLevel = 'yes'">
          <xsl:attribute name="style">
            <xsl:value-of select="'display: none;'"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="./cms:category">
          <xsl:choose>
            <xsl:when test="@order='sortKey'">
              <xsl:apply-templates mode="javascript">
                <xsl:sort data-type="number" select="@sortKey"/>
              </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates mode="javascript">
                <xsl:sort data-type="text" select="@name"/>
              </xsl:apply-templates>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
      </ul>
    </li>
  </xsl:template>
  
  <!-- DE Kategorieeintrag ohne Javascript -->
  <!-- EN Category entry without javascript -->
  <!-- DE cms:category wird in zwei verschiedenen Syntax verwendet. Die andere ist in
          cmsSummary zu finden. -->
  <!-- EN cms:category is using to different syntax. The other one is located in cmsSummary. -->
  <xsl:template match="cms:category" mode="plain">
    <option value="{@id}">
      <xsl:if test="@isSelected = '1'">
        <xsl:attribute name="selected">
          selected
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@isAbstract = '1' or @isSelected = '1'">
        <xsl:attribute name="disabled">
          disabled
        </xsl:attribute>
      </xsl:if>
      <xsl:value-of select="@fullname"/>
    </option>
    <xsl:apply-templates mode="plain">
      <xsl:sort data-type="number" select="@sortKey"/>
    </xsl:apply-templates>
  </xsl:template>

  <!-- DE -->
  <!-- EN -->
  <xsl:template match="cms:emptyPage[@title='childCategories']">
    <xsl:choose>
      <xsl:when test="cms:category/@order='sortKey'">
        <xsl:apply-templates select="cms:category/cms:category" mode="javascript">
          <xsl:sort data-type="number" select="@sortKey"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="cms:category/cms:category" mode="javascript">
          <xsl:sort data-type="text" select="@name"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- DE -->
  <!-- EN -->
  <xsl:template match="cms:emptyPage[@title='autoCategories']">
    <xsl:choose>
      <xsl:when test="cms:category/@order='sortKey'">
        <xsl:apply-templates select="cms:category" mode="javascript" >
          <xsl:sort data-type="number" select="@sortKey"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="cms:category" mode="javascript" >
          <xsl:sort data-type="text" select="@name"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
