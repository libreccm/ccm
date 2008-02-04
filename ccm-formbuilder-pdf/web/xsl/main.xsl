<?xml version="1.0" encoding="UTF-8"?>
 <xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:fo="http://www.w3.org/1999/XSL/Format">
 <xsl:template match="/">
         <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
                 <fo:layout-master-set>
                         <fo:simple-page-master master-name="simple" page-height="29.7cm" page-width="21cm" margin-top="0cm" margin-bottom="2cm" margin-left="2.5cm" margin-right="2.5cm">
                                  <fo:region-body margin-top="3cm" />
                                  <fo:region-before extent="3cm" />
                                  <fo:region-after extent="1.5cm" />
                          </fo:simple-page-master>
                  </fo:layout-master-set>

        <fo:page-sequence master-reference="simple">
                <fo:static-content flow-name="xsl-region-before">
                       <fo:block color="#336699" >London Borough of Camden </fo:block>
               </fo:static-content>

                <fo:static-content flow-name="xsl-region-after">
                       <fo:block text-align="center" > London Borough of CAMDEN </fo:block>

                </fo:static-content>

                <fo:flow flow-name="xsl-region-body">
                               <fo:block font-size="14pt" font-weight="bold" space-after="5pt" text-align="center">Content Management System</fo:block>
				<fo:block align="center">   </fo:block>
                               <fo:block align="center"> <xsl:apply-templates/>   </fo:block>
			       	<fo:block space-before="0.1in" space-after="0.1in"> </fo:block>

                               <fo:block color="#336699" font-size="10pt" text-align="left" space-after="25pt">InfoAxon provides businesses around the world with Information Technology solutions that are reliable, cost effective and continuously delight our customers.
                                Development services where we pass the benefits of applied software engineering principles; our bright technical team; our offshore development center based in India and strong technical project management .         </fo:block>
                 </fo:flow>

         </fo:page-sequence>

  </fo:root>
 </xsl:template>


  <xsl:template match="Section">
        <fo:table width="140mm" border-spacing="3pt" >
                <fo:table-column width="70mm"/>
                <fo:table-column width="70mm"/>
        <fo:table-body>
    <fo:table-row>

      <fo:table-cell >
        <fo:block text-align="left" background-color="white" padding="1pt">
            <xsl:apply-templates select="Caption"/>
        </fo:block>
      </fo:table-cell>
        
      <fo:table-cell>
        <fo:block font-family="Arial" font-size="9pt" line-height="1.6" border="thin solid #000000 1px" text-align="justify" background-color="white" space-after="3pt" >
            <xsl:apply-templates select="Value"/>
        </fo:block>
      </fo:table-cell>
     </fo:table-row>
     </fo:table-body>
     </fo:table>

  </xsl:template>
</xsl:stylesheet>
