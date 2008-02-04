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
 * href="http://www.w3.org/TR/REC-html40/sgml/entities.html#h-24.4">Character 
 * entity references for markup-significant and internationalization
 * characters</a>. (See also <a
 * href="http://www.htmlhelp.com/reference/html40/entities/special.html">Special
 * Entities</a>.)
 *
 * <p>The reason public final fields in this class do not have all upper-case
 * names is to make it easier for people to find the character they are looking
 * for.  For example, <code>&amp;oelig;</code> and <code>&amp;Oelig</code> are
 * different characters. To resolve the upper-case name collision, we would have
 * to introduce names like <code>SMALL_OELIG</code> and
 * <code>CAPITAL_OELIG</code>. Java coding standards have been sacrificed in
 * favor of preserving familiar names in this case. Therefore, the two
 * characters can be accessed as {@link #oelig} and {@link #OElig},
 * respectively.</p>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-08-30
 * @version $Id: HTMLspec.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class HTMLspec {


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
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=22">&#34;</a>
     * - quotation mark = APL quote, U+0022 ISOnum.
     **/
    public static final String quot   = s_cre.registerEntity("quot", "&#34;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=26">&#38;</a>
     * - ampersand, U+0026 ISOnum.
     **/
    public static final String amp    = s_cre.registerEntity("amp", "&#38;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3c">&#60;</a>
     * - less-than sign, U+003C ISOnum.
     **/
    public static final String lt     = s_cre.registerEntity("lt", "&#60;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3e">&#62;</a>
     * - greater-than sign, U+003E ISOnum.
     **/
    public static final String gt     = s_cre.registerEntity("gt", "&#62;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=152">&#338;</a>
     * - latin capital ligature OE, U+0152 ISOlat2.
     **/
    public static final String OElig  = s_cre.registerEntity("OElig", "&#338;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=153">&#339;</a>
     * - latin small ligature oe, U+0153 ISOlat2.
     **/
    public static final String oelig  = s_cre.registerEntity("oelig", "&#339;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=160">&#352;</a>
     * - latin capital letter S with caron, U+0160 ISOlat2.
     **/
    public static final String Scaron = s_cre.registerEntity("Scaron", "&#352;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=161">&#353;</a>
     * - latin small letter s with caron, U+0161 ISOlat2.
     **/
    public static final String scaron = s_cre.registerEntity("scaron", "&#353;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=178">&#376;</a>
     * - latin capital letter Y with diaeresis, U+0178 ISOlat2.
     **/
    public static final String Yuml   = s_cre.registerEntity("Yuml", "&#376;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2c6">&#710;</a>
     * - modifier letter circumflex accent, U+02C6 ISOpub.
     **/
    public static final String circ   = s_cre.registerEntity("circ", "&#710;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2dc">&#732;</a>
     * - small tilde, U+02DC ISOdia.
     **/
    public static final String tilde  = s_cre.registerEntity("tilde", "&#732;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2002">&#8194;</a>
     * - en space, U+2002 ISOpub.
     **/
    public static final String ensp   = s_cre.registerEntity("ensp", "&#8194;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2003">&#8195;</a>
     * - em space, U+2003 ISOpub.
     **/
    public static final String emsp   = s_cre.registerEntity("emsp", "&#8195;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2009">&#8201;</a>
     * - thin space, U+2009 ISOpub.
     **/
    public static final String thinsp = s_cre.registerEntity("thinsp", "&#8201;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=200c">&#8204;</a>
     * - zero width non-joiner, U+200C NEW RFC 2070.
     **/
    public static final String zwnj   = s_cre.registerEntity("zwnj", "&#8204;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=200d">&#8205;</a>
     * - zero width joiner, U+200D NEW RFC 2070.
     **/
    public static final String zwj    = s_cre.registerEntity("zwj", "&#8205;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=200e">&#8206;</a>
     * - left-to-right mark, U+200E NEW RFC 2070.
     **/
    public static final String lrm    = s_cre.registerEntity("lrm", "&#8206;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=200f">&#8207;</a>
     * - right-to-left mark, U+200F NEW RFC 2070.
     **/
    public static final String rlm    = s_cre.registerEntity("rlm", "&#8207;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2013">&#8211;</a>
     * - en dash, U+2013 ISOpub.
     **/
    public static final String ndash  = s_cre.registerEntity("ndash", "&#8211;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2014">&#8212;</a>
     * - em dash, U+2014 ISOpub.
     **/
    public static final String mdash  = s_cre.registerEntity("mdash", "&#8212;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2018">&#8216;</a>
     * - left single quotation mark, U+2018 ISOnum.
     **/
    public static final String lsquo  = s_cre.registerEntity("lsquo", "&#8216;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2019">&#8217;</a>
     * - right single quotation mark, U+2019 ISOnum.
     **/
    public static final String rsquo  = s_cre.registerEntity("rsquo", "&#8217;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=201a">&#8218;</a>
     * - single low-9 quotation mark, U+201A NEW.
     **/
    public static final String sbquo  = s_cre.registerEntity("sbquo", "&#8218;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=201c">&#8220;</a>
     * - left double quotation mark, U+201C ISOnum.
     **/
    public static final String ldquo  = s_cre.registerEntity("ldquo", "&#8220;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=201d">&#8221;</a>
     * - right double quotation mark, U+201D ISOnum.
     **/
    public static final String rdquo  = s_cre.registerEntity("rdquo", "&#8221;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=201e">&#8222;</a>
     * - double low-9 quotation mark, U+201E NEW.
     **/
    public static final String bdquo  = s_cre.registerEntity("bdquo", "&#8222;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2020">&#8224;</a>
     * - dagger, U+2020 ISOpub.
     **/
    public static final String dagger = s_cre.registerEntity("dagger", "&#8224;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2021">&#8225;</a>
     * - double dagger, U+2021 ISOpub.
     **/
    public static final String Dagger = s_cre.registerEntity("Dagger", "&#8225;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2030">&#8240;</a>
     * - per mille sign, U+2030 ISOtech.
     **/
    public static final String permil = s_cre.registerEntity("permil", "&#8240;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2039">&#8249;</a>
     * - single left-pointing angle quotation mark, U+2039 ISO proposed.
     **/
    public static final String lsaquo = s_cre.registerEntity("lsaquo", "&#8249;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=203a">&#8250;</a>
     * - single right-pointing angle quotation mark, U+203A ISO proposed.
     **/
    public static final String rsaquo = s_cre.registerEntity("rsaquo", "&#8250;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=20ac">&#8364;</a>
     * - euro sign, U+20AC NEW.
     **/
    public static final String euro   = s_cre.registerEntity("euro", "&#8364;");


}
