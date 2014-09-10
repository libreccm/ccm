<?xml version="1.0"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!--

    Copyright 2014 Jens Pelzetter for the LibreCCM Foundation
    
    This file is part of the Foundry Theme Engine for LibreCCM
    
    Foundry is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Foundry is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foundry  If not, see <http://www.gnu.org/licenses/>.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                version="1.0">

    <xsl:output method="html"
                    indent="yes"
                    encoding="utf-8"/>

    
    <xsl:import href="lib.xsl"/>
    
    <xsl:template match="bebop:page">
        
        <div>
            <h1>Hello from Foundry</h1>
        </div>
        
    </xsl:template>

</xsl:stylesheet>