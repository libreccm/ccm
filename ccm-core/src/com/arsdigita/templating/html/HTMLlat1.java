/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.templating.html;

/**
 * Defines <a
 * href="http://www.w3.org/TR/REC-html40/sgml/entities.html#iso-88591">Character
 * entity references for ISO 8859-1 characters</a>. (See also <a
 * href="http://www.htmlhelp.com/reference/html40/entities/latin1.html">Latin-1
 * Entities</a>.)
 *
 * <p>The reason public final fields in this class do not have all upper-case
 * names is to make it easier for people to find the character they are looking
 * for.  For example, <code>&amp;auml;</code> and <code>&amp;Auml</code> are
 * different characters. To resolve the upper-case name collision, we would have
 * to introduce names like <code>SMALL_AUML</code> and
 * <code>CAPITAL_AUML</code>. Java coding standards have been sacrificed in
 * favor of preserving familiar names in this case. Therefore, the two
 * characters can be accessed as {@link #auml} and {@link #Auml},
 * respectively.</p>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-08-30
 * @version $Id: HTMLlat1.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class HTMLlat1 {


    private final static CharacterReferenceEntities s_cre =
        new CharacterReferenceEntities();

    public static String getDecimalReference(String character) {
        return s_cre.getDecimalReference(character);
    }

    /**
     * Returns the name of the character reference entity for this character.
     * For example, <code>HTMLlat1.getName(HTMLlat1.sect)</code> returns the
     * string <code>"sect"</code>.
     **/
    public static String getName(String character) {
        return s_cre.getName(character);
    }

    /**
     * Returns a string of the form <code>&lt;!ENTITY sect "&amp;#167;"></code>,
     * if called like so:
     *
     * <pre>
     * HTMLlat1.getEntityDeclaration(HTMLlat1.sect);
     * </pre>
     **/
    public static String getEntityDeclaration(String character) {
        return s_cre.getEntityDeclaration(character);
    }

    /**
     * Returns all character entity declarations as a single string of the
     * following form:
     *
     * <pre>
     * &lt;!ENTITY nbsp   "&amp;#160;">
     * &lt;!ENTITY iexcl  "&amp;#161;">
     *   [ ... skipped for brevity ...]
     * &lt;!ENTITY yacute "&amp;#253;">
     * &lt;!ENTITY thorn  "&amp;#254;">
     * &lt;!ENTITY yuml   "&amp;#255;">
     * </pre>
     **/
    public static String getAllEntityDeclarations() {
        return s_cre.getAllEntityDeclarations();
    }

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=a0">&#160;</a>
     * - no-break space = non-breaking space, U+00A0 ISOnum.
     **/
    public static final String nbsp   = s_cre.registerEntity("nbsp", "&#160;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=a1">&#161;</a>
     * - inverted exclamation mark, U+00A1 ISOnum.
     **/
    public static final String iexcl  = s_cre.registerEntity("iexcl", "&#161;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=a2">&#162;</a>
     * - cent sign, U+00A2 ISOnum.
     **/
    public static final String cent   = s_cre.registerEntity("cent", "&#162;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=a3">&#163;</a>
     * - pound sign, U+00A3 ISOnum.
     **/
    public static final String pound  = s_cre.registerEntity("pound", "&#163;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=a4">&#164;</a>
     * - currency sign, U+00A4 ISOnum.
     **/
    public static final String curren = s_cre.registerEntity("curren", "&#164;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=a5">&#165;</a>
     * - yen sign = yuan sign, U+00A5 ISOnum.
     **/
    public static final String yen    = s_cre.registerEntity("yen", "&#165;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=a6">&#166;</a>
     * - broken bar = broken vertical bar, U+00A6 ISOnum.
     **/
    public static final String brvbar = s_cre.registerEntity("brvbar", "&#166;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=a7">&#167;</a>
     * - section sign, U+00A7 ISOnum.
     **/
    public static final String sect   = s_cre.registerEntity("sect", "&#167;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=a8">&#168;</a>
     * - diaeresis = spacing diaeresis, U+00A8 ISOdia.
     **/
    public static final String uml    = s_cre.registerEntity("uml", "&#168;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=a9">&#169;</a>
     * - copyright sign, U+00A9 ISOnum.
     **/
    public static final String copy   = s_cre.registerEntity("copy", "&#169;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=aa">&#170;</a>
     * - feminine ordinal indicator, U+00AA ISOnum.
     **/
    public static final String ordf   = s_cre.registerEntity("ordf", "&#170;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ab">&#171;</a>
     * - left-pointing double angle quotation mark = left pointing guillemet, U+00AB ISOnum.
     **/
    public static final String laquo  = s_cre.registerEntity("laquo", "&#171;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ac">&#172;</a>
     * - not sign = discretionary hyphen, U+00AC ISOnum.
     **/
    public static final String not    = s_cre.registerEntity("not", "&#172;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ad">&#173;</a>
     * - soft hyphen = discretionary hyphen, U+00AD ISOnum.
     **/
    public static final String shy    = s_cre.registerEntity("shy", "&#173;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ae">&#174;</a>
     * - registered sign = registered trade mark sign, U+00AE ISOnum.
     **/
    public static final String reg    = s_cre.registerEntity("reg", "&#174;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=af">&#175;</a>
     * - macron = spacing macron = overline = APL overbar, U+00AF ISOdia.
     **/
    public static final String macr   = s_cre.registerEntity("macr", "&#175;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=b0">&#176;</a>
     * - degree sign, U+00B0 ISOnum.
     **/
    public static final String deg    = s_cre.registerEntity("deg", "&#176;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=b1">&#177;</a>
     * - plus-minus sign = plus-or-minus sign, U+00B1 ISOnum.
     **/
    public static final String plusmn = s_cre.registerEntity("plusmn", "&#177;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=b2">&#178;</a>
     * - superscript two = superscript digit two = squared, U+00B2 ISOnum.
     **/
    public static final String sup2   = s_cre.registerEntity("sup2", "&#178;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=b3">&#179;</a>
     * - superscript three = superscript digit three = cubed, U+00B3 ISOnum.
     **/
    public static final String sup3   = s_cre.registerEntity("sup3", "&#179;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=b4">&#180;</a>
     * - acute accent = spacing acute, U+00B4 ISOdia.
     **/
    public static final String acute  = s_cre.registerEntity("acute", "&#180;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=b5">&#181;</a>
     * - micro sign, U+00B5 ISOnum.
     **/
    public static final String micro  = s_cre.registerEntity("micro", "&#181;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=b6">&#182;</a>
     * - pilcrow sign = paragraph sign, U+00B6 ISOnum.
     **/
    public static final String para   = s_cre.registerEntity("para", "&#182;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=b7">&#183;</a>
     * - middle dot = Georgian comma = Greek middle dot, U+00B7 ISOnum.
     **/
    public static final String middot = s_cre.registerEntity("middot", "&#183;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=b8">&#184;</a>
     * - cedilla = spacing cedilla, U+00B8 ISOdia.
     **/
    public static final String cedil  = s_cre.registerEntity("cedil", "&#184;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=b9">&#185;</a>
     * - superscript one = superscript digit one, U+00B9 ISOnum.
     **/
    public static final String sup1   = s_cre.registerEntity("sup1", "&#185;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ba">&#186;</a>
     * - masculine ordinal indicator, U+00BA ISOnum.
     **/
    public static final String ordm   = s_cre.registerEntity("ordm", "&#186;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=bb">&#187;</a>
     * - right-pointing double angle quotation mark = right pointing guillemet, U+00BB ISOnum.
     **/
    public static final String raquo  = s_cre.registerEntity("raquo", "&#187;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=bc">&#188;</a>
     * - vulgar fraction one quarter = fraction one quarter, U+00BC ISOnum.
     **/
    public static final String frac14 = s_cre.registerEntity("frac14", "&#188;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=bd">&#189;</a>
     * - vulgar fraction one half = fraction one half, U+00BD ISOnum.
     **/
    public static final String frac12 = s_cre.registerEntity("frac12", "&#189;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=be">&#190;</a>
     * - vulgar fraction three quarters = fraction three quarters, U+00BE ISOnum.
     **/
    public static final String frac34 = s_cre.registerEntity("frac34", "&#190;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=bf">&#191;</a>
     * - inverted question mark = turned question mark, U+00BF ISOnum.
     **/
    public static final String iquest = s_cre.registerEntity("iquest", "&#191;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=c0">&#192;</a>
     * - latin capital letter A with grave = latin capital letter A grave, U+00C0 ISOlat1.
     **/
    public static final String Agrave = s_cre.registerEntity("Agrave", "&#192;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=c1">&#193;</a>
     * - latin capital letter A with acute, U+00C1 ISOlat1.
     **/
    public static final String Aacute = s_cre.registerEntity("Aacute", "&#193;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=c2">&#194;</a>
     * - latin capital letter A with circumflex, U+00C2 ISOlat1.
     **/
    public static final String Acirc  = s_cre.registerEntity("Acirc", "&#194;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=c3">&#195;</a>
     * - latin capital letter A with tilde, U+00C3 ISOlat1.
     **/
    public static final String Atilde = s_cre.registerEntity("Atilde", "&#195;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=c4">&#196;</a>
     * - latin capital letter A with diaeresis, U+00C4 ISOlat1.
     **/
    public static final String Auml   = s_cre.registerEntity("Auml", "&#196;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=c5">&#197;</a>
     * - latin capital letter A with ring above = latin capital letter A ring, U+00C5 ISOlat1.
     **/
    public static final String Aring  = s_cre.registerEntity("Aring", "&#197;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=c6">&#198;</a>
     * - latin capital letter AE = latin capital ligature AE, U+00C6 ISOlat1.
     **/
    public static final String AElig  = s_cre.registerEntity("AElig", "&#198;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=c7">&#199;</a>
     * - latin capital letter C with cedilla, U+00C7 ISOlat1.
     **/
    public static final String Ccedil = s_cre.registerEntity("Ccedil", "&#199;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=c8">&#200;</a>
     * - latin capital letter E with grave, U+00C8 ISOlat1.
     **/
    public static final String Egrave = s_cre.registerEntity("Egrave", "&#200;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=c9">&#201;</a>
     * - latin capital letter E with acute, U+00C9 ISOlat1.
     **/
    public static final String Eacute = s_cre.registerEntity("Eacute", "&#201;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ca">&#202;</a>
     * - latin capital letter E with circumflex, U+00CA ISOlat1.
     **/
    public static final String Ecirc  = s_cre.registerEntity("Ecirc", "&#202;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=cb">&#203;</a>
     * - latin capital letter E with diaeresis, U+00CB ISOlat1.
     **/
    public static final String Euml   = s_cre.registerEntity("Euml", "&#203;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=cc">&#204;</a>
     * - latin capital letter I with grave, U+00CC ISOlat1.
     **/
    public static final String Igrave = s_cre.registerEntity("Igrave", "&#204;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=cd">&#205;</a>
     * - latin capital letter I with acute, U+00CD ISOlat1.
     **/
    public static final String Iacute = s_cre.registerEntity("Iacute", "&#205;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ce">&#206;</a>
     * - latin capital letter I with circumflex, U+00CE ISOlat1.
     **/
    public static final String Icirc  = s_cre.registerEntity("Icirc", "&#206;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=cf">&#207;</a>
     * - latin capital letter I with diaeresis, U+00CF ISOlat1.
     **/
    public static final String Iuml   = s_cre.registerEntity("Iuml", "&#207;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=d0">&#208;</a>
     * - latin capital letter ETH, U+00D0 ISOlat1.
     **/
    public static final String ETH    = s_cre.registerEntity("ETH", "&#208;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=d1">&#209;</a>
     * - latin capital letter N with tilde, U+00D1 ISOlat1.
     **/
    public static final String Ntilde = s_cre.registerEntity("Ntilde", "&#209;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=d2">&#210;</a>
     * - latin capital letter O with grave, U+00D2 ISOlat1.
     **/
    public static final String Ograve = s_cre.registerEntity("Ograve", "&#210;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=d3">&#211;</a>
     * - latin capital letter O with acute, U+00D3 ISOlat1.
     **/
    public static final String Oacute = s_cre.registerEntity("Oacute", "&#211;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=d4">&#212;</a>
     * - latin capital letter O with circumflex, U+00D4 ISOlat1.
     **/
    public static final String Ocirc  = s_cre.registerEntity("Ocirc", "&#212;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=d5">&#213;</a>
     * - latin capital letter O with tilde, U+00D5 ISOlat1.
     **/
    public static final String Otilde = s_cre.registerEntity("Otilde", "&#213;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=d6">&#214;</a>
     * - latin capital letter O with diaeresis, U+00D6 ISOlat1.
     **/
    public static final String Ouml   = s_cre.registerEntity("Ouml", "&#214;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=d7">&#215;</a>
     * - multiplication sign, U+00D7 ISOnum.
     **/
    public static final String times  = s_cre.registerEntity("times", "&#215;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=d8">&#216;</a>
     * - latin capital letter O with stroke = latin capital letter O slash, U+00D8 ISOlat1.
     **/
    public static final String Oslash = s_cre.registerEntity("Oslash", "&#216;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=d9">&#217;</a>
     * - latin capital letter U with grave, U+00D9 ISOlat1.
     **/
    public static final String Ugrave = s_cre.registerEntity("Ugrave", "&#217;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=da">&#218;</a>
     * - latin capital letter U with acute, U+00DA ISOlat1.
     **/
    public static final String Uacute = s_cre.registerEntity("Uacute", "&#218;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=db">&#219;</a>
     * - latin capital letter U with circumflex, U+00DB ISOlat1.
     **/
    public static final String Ucirc  = s_cre.registerEntity("Ucirc", "&#219;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=dc">&#220;</a>
     * - latin capital letter U with diaeresis, U+00DC ISOlat1.
     **/
    public static final String Uuml   = s_cre.registerEntity("Uuml", "&#220;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=dd">&#221;</a>
     * - latin capital letter Y with acute, U+00DD ISOlat1.
     **/
    public static final String Yacute = s_cre.registerEntity("Yacute", "&#221;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=de">&#222;</a>
     * - latin capital letter THORN, U+00DE ISOlat1.
     **/
    public static final String THORN  = s_cre.registerEntity("THORN", "&#222;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=df">&#223;</a>
     * - latin small letter sharp s = ess-zed, U+00DF ISOlat1.
     **/
    public static final String szlig  = s_cre.registerEntity("szlig", "&#223;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=e0">&#224;</a>
     * - latin small letter a with grave = latin small letter a grave, U+00E0 ISOlat1.
     **/
    public static final String agrave = s_cre.registerEntity("agrave", "&#224;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=e1">&#225;</a>
     * - latin small letter a with acute, U+00E1 ISOlat1.
     **/
    public static final String aacute = s_cre.registerEntity("aacute", "&#225;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=e2">&#226;</a>
     * - latin small letter a with circumflex, U+00E2 ISOlat1.
     **/
    public static final String acirc  = s_cre.registerEntity("acirc", "&#226;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=e3">&#227;</a>
     * - latin small letter a with tilde, U+00E3 ISOlat1.
     **/
    public static final String atilde = s_cre.registerEntity("atilde", "&#227;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=e4">&#228;</a>
     * - latin small letter a with diaeresis, U+00E4 ISOlat1.
     **/
    public static final String auml   = s_cre.registerEntity("auml", "&#228;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=e5">&#229;</a>
     * - latin small letter a with ring above = latin small letter a ring, U+00E5 ISOlat1.
     **/
    public static final String aring  = s_cre.registerEntity("aring", "&#229;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=e6">&#230;</a>
     * - latin small letter ae = latin small ligature ae, U+00E6 ISOlat1.
     **/
    public static final String aelig  = s_cre.registerEntity("aelig", "&#230;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=e7">&#231;</a>
     * - latin small letter c with cedilla, U+00E7 ISOlat1.
     **/
    public static final String ccedil = s_cre.registerEntity("ccedil", "&#231;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=e8">&#232;</a>
     * - latin small letter e with grave, U+00E8 ISOlat1.
     **/
    public static final String egrave = s_cre.registerEntity("egrave", "&#232;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=e9">&#233;</a>
     * - latin small letter e with acute, U+00E9 ISOlat1.
     **/
    public static final String eacute = s_cre.registerEntity("eacute", "&#233;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ea">&#234;</a>
     * - latin small letter e with circumflex, U+00EA ISOlat1.
     **/
    public static final String ecirc  = s_cre.registerEntity("ecirc", "&#234;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=eb">&#235;</a>
     * - latin small letter e with diaeresis, U+00EB ISOlat1.
     **/
    public static final String euml   = s_cre.registerEntity("euml", "&#235;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ec">&#236;</a>
     * - latin small letter i with grave, U+00EC ISOlat1.
     **/
    public static final String igrave = s_cre.registerEntity("igrave", "&#236;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ed">&#237;</a>
     * - latin small letter i with acute, U+00ED ISOlat1.
     **/
    public static final String iacute = s_cre.registerEntity("iacute", "&#237;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ee">&#238;</a>
     * - latin small letter i with circumflex, U+00EE ISOlat1.
     **/
    public static final String icirc  = s_cre.registerEntity("icirc", "&#238;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ef">&#239;</a>
     * - latin small letter i with diaeresis, U+00EF ISOlat1.
     **/
    public static final String iuml   = s_cre.registerEntity("iuml", "&#239;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=f0">&#240;</a>
     * - latin small letter eth, U+00F0 ISOlat1.
     **/
    public static final String eth    = s_cre.registerEntity("eth", "&#240;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=f1">&#241;</a>
     * - latin small letter n with tilde, U+00F1 ISOlat1.
     **/
    public static final String ntilde = s_cre.registerEntity("ntilde", "&#241;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=f2">&#242;</a>
     * - latin small letter o with grave, U+00F2 ISOlat1.
     **/
    public static final String ograve = s_cre.registerEntity("ograve", "&#242;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=f3">&#243;</a>
     * - latin small letter o with acute, U+00F3 ISOlat1.
     **/
    public static final String oacute = s_cre.registerEntity("oacute", "&#243;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=f4">&#244;</a>
     * - latin small letter o with circumflex, U+00F4 ISOlat1.
     **/
    public static final String ocirc  = s_cre.registerEntity("ocirc", "&#244;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=f5">&#245;</a>
     * - latin small letter o with tilde, U+00F5 ISOlat1.
     **/
    public static final String otilde = s_cre.registerEntity("otilde", "&#245;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=f6">&#246;</a>
     * - latin small letter o with diaeresis, U+00F6 ISOlat1.
     **/
    public static final String ouml   = s_cre.registerEntity("ouml", "&#246;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=f7">&#247;</a>
     * - division sign, U+00F7 ISOnum.
     **/
    public static final String divide = s_cre.registerEntity("divide", "&#247;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=f8">&#248;</a>
     * - latin small letter o with stroke, = latin small letter o slash, U+00F8 ISOlat1.
     **/
    public static final String oslash = s_cre.registerEntity("oslash", "&#248;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=f9">&#249;</a>
     * - latin small letter u with grave, U+00F9 ISOlat1.
     **/
    public static final String ugrave = s_cre.registerEntity("ugrave", "&#249;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=fa">&#250;</a>
     * - latin small letter u with acute, U+00FA ISOlat1.
     **/
    public static final String uacute = s_cre.registerEntity("uacute", "&#250;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=fb">&#251;</a>
     * - latin small letter u with circumflex, U+00FB ISOlat1.
     **/
    public static final String ucirc  = s_cre.registerEntity("ucirc", "&#251;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=fc">&#252;</a>
     * - latin small letter u with diaeresis, U+00FC ISOlat1.
     **/
    public static final String uuml   = s_cre.registerEntity("uuml", "&#252;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=fd">&#253;</a>
     * - latin small letter y with acute, U+00FD ISOlat1.
     **/
    public static final String yacute = s_cre.registerEntity("yacute", "&#253;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=fe">&#254;</a>
     * - latin small letter thorn with, U+00FE ISOlat1.
     **/
    public static final String thorn  = s_cre.registerEntity("thorn", "&#254;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=ff">&#255;</a>
     * - latin small letter y with diaeresis, U+00FF ISOlat1.
     **/
    public static final String yuml   = s_cre.registerEntity("yuml", "&#255;");


}
