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
  Hier werden die cmsSortableList verarbeitet 
-->

<!-- EN
  Processing cmsSortableList
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
  
    <!-- DE Erzeuge eine sortierbare Liste (mit Sortierpfeilen) -->
    <!-- EN Create a sortable list (with sorting buttons) -->
    <xsl:template match="cms:sortableList">
        <div class="cmsSortableList">
            <xsl:call-template name="mandalay:processAttributes"/>
            <ul>
                <xsl:apply-templates mode="sortableList"/>
            </ul>
        </div>
    </xsl:template>  
  
    <!-- DE Spezielles bebop:cell für die sortierbaren Listen, daß die Pfeile mit erzeugt -->
    <!-- EN A special bebop:cell for sortable list, which will create sorting buttons -->
    <xsl:template match="bebop:cell" mode="sortableList">
        <li>
            <xsl:if test="@configure">
                <span class="sortButtons">
                    <span class="sortButtonUp">
                        <xsl:choose>
                            <xsl:when test="@prevURL">
                                <a href="{@prevURL}">
                                    <img alt="^" src="{$theme-prefix}/images/cms/arrowUp.gif"/>
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                &nbsp;
                            </xsl:otherwise>
                        </xsl:choose>
                    </span>
                    <span class="sortButtonDown">
                        <xsl:choose>
                            <xsl:when test="@nextURL">
                                <a href="{@nextURL}">
                                    <img alt="v" src="{$theme-prefix}/images/cms/arrowDown.gif"/>
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                &nbsp;
                            </xsl:otherwise>
                        </xsl:choose>
                    </span>
                </span>
            </xsl:if>
            <xsl:apply-templates/>
        </li>
    </xsl:template>

</xsl:stylesheet>
