<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="3.0"   xmlns:fo="http://www.w3.org/1999/XSL/Format"
                                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                xmlns:pdf="http://xmlgraphics.apache.org/fop/extensions/pdf">

<xsl:output method="xml" version="1.0" indent="yes" />

<xsl:template match="receipt">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master 
                page-width="80mm"
                margin-top="0mm"
                margin-left="0mm"
                margin-bottom="10mm"
                margin-right="10mm"
                master-name="PageMaster" 
                >
                <fo:region-body margin-top="1cm" margin-bottom="1cm"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <fo:page-sequence master-reference="PageMaster">
            <fo:flow flow-name="xsl-region-body" >
                <xsl:apply-templates />
            </fo:flow>
        </fo:page-sequence>
    </fo:root>
</xsl:template>

<!-- Header style definition -->
<xsl:template match="header">
<fo:block font-family="SourceSansPro" language="hu" font-size="6pt" >
<fo:table  width="100%" text-align="center">
<fo:table-body >
    <xsl:apply-templates  select="*"/>
</fo:table-body>
</fo:table>
</fo:block>
<fo:block text-align="center" border-top-style="double" border-top-width="4pt"/>
</xsl:template>

<xsl:template match="header/restaurant_name">
    <fo:table-row>
        <fo:table-cell  number-columns-spanned="3">
            <fo:block font-weight="bold" font-size="10pt" padding-before="2pt">
                <xsl:apply-templates  />
            </fo:block>
        </fo:table-cell>
    </fo:table-row>
</xsl:template>

<xsl:template match="header/restaurant_social_media_info|header/restaurant_website|header/restaurant_address">
    <fo:table-row>
        <fo:table-cell  number-columns-spanned="3">
            <fo:block font-size="6pt" padding-before="2pt">
                <xsl:apply-templates  />
            </fo:block>
        </fo:table-cell>
    </fo:table-row>
</xsl:template>

<xsl:template match="header/restaurant_logo_path">
    <fo:table-row>
    <fo:table-cell  number-columns-spanned="3">
    <fo:block font-weight="bold"  padding-before="2pt">
        <fo:external-graphic src="{.}" width="100%" />
    </fo:block>
    </fo:table-cell>
    </fo:table-row>
</xsl:template>

<!-- Body style definition -->
<xsl:template match="body">
    <fo:block font-family="SourceSansPro" language="hu" font-size="8pt">
            <fo:table  width="100%">
                <fo:table-header 
                    font-family="SourceSansPro" language="hu" 
                    font-weight="bold"
                    font-size="6pt" 
                    border-bottom-style="solid" 
                    border-bottom-width="2pt" >
                    <fo:table-row>
                        <xsl:apply-templates  select="body_header/*" />   
                    </fo:table-row>
                </fo:table-header >
                <fo:table-body>
                    <xsl:apply-templates select="entry" />
                    <xsl:apply-templates select="body_footer" />
                </fo:table-body>
            </fo:table>
    </fo:block>
</xsl:template>

<xsl:template match="body_header/*">
    <fo:table-cell>
      <fo:block padding-before="4pt" padding-after="4pt" text-align="end">
        <xsl:apply-templates  />
      </fo:block>
    </fo:table-cell>
</xsl:template>

<xsl:template match="body_footer">
    <fo:table-row border-top-style="solid" border-top-width="2pt" font-weight="bold">
        <xsl:apply-templates  select="total_tag|total|total_currency"/>
    </fo:table-row>
    <fo:table-row font-weight="bold">
        <xsl:apply-templates  select="total_rounded_tag|total_rounded|total_rounded_currency"/>
    </fo:table-row >
    <fo:table-row font-weight="bold">
        <xsl:apply-templates  select="payment_method_tag|payment_method"/>
    </fo:table-row>
</xsl:template>

<xsl:template match="body_footer/total_tag|body_footer/total_rounded_tag|body_footer/payment_method_tag">
    <fo:table-cell number-columns-spanned="2">
      <fo:block font-weight="bold"  padding-before="2pt">
        <xsl:apply-templates  />:
      </fo:block>
    </fo:table-cell>
</xsl:template>

<xsl:template match="body_footer/total|body_footer/total_rounded|body_footer/total_currency|body_footer/total_rounded_currency|body_footer/payment_method">
    <fo:table-cell>
      <fo:block font-weight="bold"  padding-before="2pt" text-align="end">
        <xsl:apply-templates  />
      </fo:block>
    </fo:table-cell>
</xsl:template>


<xsl:template match="entry">
    <fo:table-row>
        <xsl:apply-templates  select="name"/>
    </fo:table-row>
    <fo:table-row>
        <fo:table-cell>
            <fo:block />
        </fo:table-cell>
        <xsl:apply-templates select="qty|qty_price|total" />
    </fo:table-row>
</xsl:template>

<xsl:template match="entry/name">
    <fo:table-cell number-columns-spanned="4">
        <fo:block >
            <xsl:value-of select="."/>
        </fo:block>
    </fo:table-cell>
</xsl:template>

<xsl:template match="entry/total">
        <fo:table-cell>
            <fo:block  text-align="end">
                <xsl:apply-templates  />
            </fo:block>
        </fo:table-cell>
</xsl:template>

<xsl:template match="entry/qty">
        <fo:table-cell>
            <fo:block text-align="end">
                <xsl:apply-templates  /> x
            </fo:block>
        </fo:table-cell>
</xsl:template>

<xsl:template match="entry/qty_price">
        <fo:table-cell>
            <fo:block text-align="end">
                <xsl:apply-templates  /> =
            </fo:block>
        </fo:table-cell>
</xsl:template>


    <!-- footer style definition -->
<xsl:template match="footer">
    <fo:block font-family="SourceSansPro" language="hu" font-size="8pt">
            <fo:table  width="100%">
                <fo:table-body border-top-style="double" border-top-width="4pt">
                    <xsl:apply-templates />
                </fo:table-body>
            </fo:table>
    </fo:block>
</xsl:template>

<xsl:template match="footer/disclaimer|footer/greet|footer/vendor_info">
    <fo:table-row>
        <fo:table-cell number-columns-spanned="2" text-align="center">
            <fo:block padding-before="8pt" padding-after="8pt">
                <xsl:apply-templates />
            </fo:block>
        </fo:table-cell>
    </fo:table-row>
</xsl:template>

<xsl:template match="footer/note">
    <fo:table-row border-left-style="dashed" border-right-style="dashed" border-top-style="dashed" border-bottom-style="dashed">
        <fo:table-cell number-columns-spanned="2" text-align="center">
            <fo:block padding-before="8pt" padding-after="8pt">
                <xsl:apply-templates />
            </fo:block>
        </fo:table-cell>
    </fo:table-row>
</xsl:template>

<xsl:template match="footer/receipt_id_tag|footer/datetime">
    <fo:table-row>
        <fo:table-cell number-columns-spanned="2" text-align="end">
            <fo:block> 
                <xsl:apply-templates />
            </fo:block>
        </fo:table-cell>
    </fo:table-row>
</xsl:template>

</xsl:stylesheet >