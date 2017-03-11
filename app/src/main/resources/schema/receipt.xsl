<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" 
                              xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" version="1.0" indent="yes" />

<xsl:template match="receipt">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master 
                page-width="80mm" 
                margin="10mm 10mm 10mm 10mm" 
                master-name="PageMaster" 
                >
                <fo:region-body margin-top="1cm" margin-bottom="1cm"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <fo:page-sequence master-reference="PageMaster">
            <fo:flow flow-name="xsl-region-body" >
                <xsl:apply-templates select="header" />
                <xsl:apply-templates select="body" />
                <xsl:apply-templates select="footer" />
            </fo:flow>
        </fo:page-sequence>
    </fo:root>
</xsl:template>

<!-- Header style definition -->
<xsl:template match="header">
    <fo:block     font-family="Helvetica" font-size="10pt"
                  text-align="center">
        <xsl:for-each select="*">
            <fo:block>
                <xsl:apply-templates />
            </fo:block>
        </xsl:for-each>
    </fo:block>
</xsl:template>

<!-- Body style definition -->
<xsl:template match="body">
  <fo:block>
    <xsl:apply-templates  />
  </fo:block>
</xsl:template>


<!-- footer style definition -->
<xsl:template match="footer">
<fo:block>
  <fo:inline font-weight="bold">
      <xsl:apply-templates />
  </fo:inline>
</fo:block>
</xsl:template>

</xsl:stylesheet >