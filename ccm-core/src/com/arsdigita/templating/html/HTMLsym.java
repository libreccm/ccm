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
 * href="http://www.w3.org/TR/REC-html40/sgml/entities.html#h-24.3">Character
 * entity references for symbols, mathematical symbols, and Greek letters</a>.
 * (See also <a
 * href="http://www.htmlhelp.com/reference/html40/entities/symbols.html">Entities
 * for Symbols and Greek Letters</a>.)
 *
 * <p>The reason public final fields in this class do not have all upper-case
 * names is to make it easier for people to find the character they are looking
 * for.  For example, <code>&amp;alpha;</code> and <code>&amp;Alpha</code> are
 * different characters. To resolve the upper-case name collision, we would have
 * to introduce names like <code>SMALL_ALPHA</code> and
 * <code>CAPITAL_ALPHA</code>. Java coding standards have been sacrificed in
 * favor of preserving familiar names in this case. Therefore, the two
 * characters can be accessed as {@link #alpha} and {@link #Alpha},
 * respectively.</p>
 *
 * <p><span style="color: FireBrick; font-weight: bold">Note</span>: The only
 * exception to this is the {@link #integral} field that is actually
 * called "&amp;int;" in HTML. We cannot call it "int" due to a name conflict
 * with the
 * <a href="http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#229308">reserved
 * Java keyword</a> <code>int</code>. </p>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-08-30
 * @version $Id: HTMLsym.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class HTMLsym {


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
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=192">&#402;</a>
     * - latin small f with hook = function = florin, U+0192 ISOtech.
     **/
    public static final String fnof   = s_cre.registerEntity("fnof", "&#402;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=391">&#913;</a>
     * - greek capital letter alpha, U+0391.
     **/
    public static final String Alpha  = s_cre.registerEntity("Alpha", "&#913;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=392">&#914;</a>
     * - greek capital letter beta, U+0392.
     **/
    public static final String Beta   = s_cre.registerEntity("Beta", "&#914;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=393">&#915;</a>
     * - greek capital letter gamma, U+0393 ISOgrk3.
     **/
    public static final String Gamma  = s_cre.registerEntity("Gamma", "&#915;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=394">&#916;</a>
     * - greek capital letter delta, U+0394 ISOgrk3.
     **/
    public static final String Delta  = s_cre.registerEntity("Delta", "&#916;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=395">&#917;</a>
     * - greek capital letter epsilon, U+0395.
     **/
    public static final String Epsilon = s_cre.registerEntity("Epsilon", "&#917;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=396">&#918;</a>
     * - greek capital letter zeta, U+0396.
     **/
    public static final String Zeta   = s_cre.registerEntity("Zeta", "&#918;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=397">&#919;</a>
     * - greek capital letter eta, U+0397.
     **/
    public static final String Eta    = s_cre.registerEntity("Eta", "&#919;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=398">&#920;</a>
     * - greek capital letter theta, U+0398 ISOgrk3.
     **/
    public static final String Theta  = s_cre.registerEntity("Theta", "&#920;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=399">&#921;</a>
     * - greek capital letter iota, U+0399.
     **/
    public static final String Iota   = s_cre.registerEntity("Iota", "&#921;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=39a">&#922;</a>
     * - greek capital letter kappa, U+039A.
     **/
    public static final String Kappa  = s_cre.registerEntity("Kappa", "&#922;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=39b">&#923;</a>
     * - greek capital letter lambda, U+039B ISOgrk3.
     **/
    public static final String Lambda = s_cre.registerEntity("Lambda", "&#923;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=39c">&#924;</a>
     * - greek capital letter mu, U+039C.
     **/
    public static final String Mu     = s_cre.registerEntity("Mu", "&#924;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=39d">&#925;</a>
     * - greek capital letter nu, U+039D.
     **/
    public static final String Nu     = s_cre.registerEntity("Nu", "&#925;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=39e">&#926;</a>
     * - greek capital letter xi, U+039E ISOgrk3.
     **/
    public static final String Xi     = s_cre.registerEntity("Xi", "&#926;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=39f">&#927;</a>
     * - greek capital letter omicron, U+039F.
     **/
    public static final String Omicron = s_cre.registerEntity("Omicron", "&#927;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3a0">&#928;</a>
     * - greek capital letter pi, U+03A0 ISOgrk3.
     **/
    public static final String Pi     = s_cre.registerEntity("Pi", "&#928;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3a1">&#929;</a>
     * - greek capital letter rho, U+03A1.
     **/
    public static final String Rho    = s_cre.registerEntity("Rho", "&#929;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3a3">&#931;</a>
     * - greek capital letter sigma, U+03A3 ISOgrk3.
     **/
    public static final String Sigma  = s_cre.registerEntity("Sigma", "&#931;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3a4">&#932;</a>
     * - greek capital letter tau, U+03A4.
     **/
    public static final String Tau    = s_cre.registerEntity("Tau", "&#932;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3a5">&#933;</a>
     * - greek capital letter upsilon, U+03A5 ISOgrk3.
     **/
    public static final String Upsilon = s_cre.registerEntity("Upsilon", "&#933;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3a6">&#934;</a>
     * - greek capital letter phi, U+03A6 ISOgrk3.
     **/
    public static final String Phi    = s_cre.registerEntity("Phi", "&#934;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3a7">&#935;</a>
     * - greek capital letter chi, U+03A7.
     **/
    public static final String Chi    = s_cre.registerEntity("Chi", "&#935;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3a8">&#936;</a>
     * - greek capital letter psi, U+03A8 ISOgrk3.
     **/
    public static final String Psi    = s_cre.registerEntity("Psi", "&#936;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3a9">&#937;</a>
     * - greek capital letter omega, U+03A9 ISOgrk3.
     **/
    public static final String Omega  = s_cre.registerEntity("Omega", "&#937;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3b1">&#945;</a>
     * - greek small letter alpha, U+03B1 ISOgrk3.
     **/
    public static final String alpha  = s_cre.registerEntity("alpha", "&#945;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3b2">&#946;</a>
     * - greek small letter beta, U+03B2 ISOgrk3.
     **/
    public static final String beta   = s_cre.registerEntity("beta", "&#946;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3b3">&#947;</a>
     * - greek small letter gamma, U+03B3 ISOgrk3.
     **/
    public static final String gamma  = s_cre.registerEntity("gamma", "&#947;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3b4">&#948;</a>
     * - greek small letter delta, U+03B4 ISOgrk3.
     **/
    public static final String delta  = s_cre.registerEntity("delta", "&#948;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3b5">&#949;</a>
     * - greek small letter epsilon, U+03B5 ISOgrk3.
     **/
    public static final String epsilon = s_cre.registerEntity("epsilon", "&#949;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3b6">&#950;</a>
     * - greek small letter zeta, U+03B6 ISOgrk3.
     **/
    public static final String zeta   = s_cre.registerEntity("zeta", "&#950;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3b7">&#951;</a>
     * - greek small letter eta, U+03B7 ISOgrk3.
     **/
    public static final String eta    = s_cre.registerEntity("eta", "&#951;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3b8">&#952;</a>
     * - greek small letter theta, U+03B8 ISOgrk3.
     **/
    public static final String theta  = s_cre.registerEntity("theta", "&#952;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3b9">&#953;</a>
     * - greek small letter iota, U+03B9 ISOgrk3.
     **/
    public static final String iota   = s_cre.registerEntity("iota", "&#953;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3ba">&#954;</a>
     * - greek small letter kappa, U+03BA ISOgrk3.
     **/
    public static final String kappa  = s_cre.registerEntity("kappa", "&#954;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3bb">&#955;</a>
     * - greek small letter lambda, U+03BB ISOgrk3.
     **/
    public static final String lambda = s_cre.registerEntity("lambda", "&#955;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3bc">&#956;</a>
     * - greek small letter mu, U+03BC ISOgrk3.
     **/
    public static final String mu     = s_cre.registerEntity("mu", "&#956;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3bd">&#957;</a>
     * - greek small letter nu, U+03BD ISOgrk3.
     **/
    public static final String nu     = s_cre.registerEntity("nu", "&#957;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3be">&#958;</a>
     * - greek small letter xi, U+03BE ISOgrk3.
     **/
    public static final String xi     = s_cre.registerEntity("xi", "&#958;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3bf">&#959;</a>
     * - greek small letter omicron, U+03BF NEW.
     **/
    public static final String omicron = s_cre.registerEntity("omicron", "&#959;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3c0">&#960;</a>
     * - greek small letter pi, U+03C0 ISOgrk3.
     **/
    public static final String pi     = s_cre.registerEntity("pi", "&#960;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3c1">&#961;</a>
     * - greek small letter rho, U+03C1 ISOgrk3.
     **/
    public static final String rho    = s_cre.registerEntity("rho", "&#961;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3c2">&#962;</a>
     * - greek small letter final sigma, U+03C2 ISOgrk3.
     **/
    public static final String sigmaf = s_cre.registerEntity("sigmaf", "&#962;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3c3">&#963;</a>
     * - greek small letter sigma, U+03C3 ISOgrk3.
     **/
    public static final String sigma  = s_cre.registerEntity("sigma", "&#963;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3c4">&#964;</a>
     * - greek small letter tau, U+03C4 ISOgrk3.
     **/
    public static final String tau    = s_cre.registerEntity("tau", "&#964;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3c5">&#965;</a>
     * - greek small letter upsilon, U+03C5 ISOgrk3.
     **/
    public static final String upsilon = s_cre.registerEntity("upsilon", "&#965;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3c6">&#966;</a>
     * - greek small letter phi, U+03C6 ISOgrk3.
     **/
    public static final String phi    = s_cre.registerEntity("phi", "&#966;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3c7">&#967;</a>
     * - greek small letter chi, U+03C7 ISOgrk3.
     **/
    public static final String chi    = s_cre.registerEntity("chi", "&#967;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3c8">&#968;</a>
     * - greek small letter psi, U+03C8 ISOgrk3.
     **/
    public static final String psi    = s_cre.registerEntity("psi", "&#968;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3c9">&#969;</a>
     * - greek small letter omega, U+03C9 ISOgrk3.
     **/
    public static final String omega  = s_cre.registerEntity("omega", "&#969;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3d1">&#977;</a>
     * - greek small letter theta symbol, U+03D1 NEW.
     **/
    public static final String thetasym = s_cre.registerEntity("thetasym", "&#977;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3d2">&#978;</a>
     * - greek upsilon with hook symbol, U+03D2 NEW.
     **/
    public static final String upsih  = s_cre.registerEntity("upsih", "&#978;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=3d6">&#982;</a>
     * - greek pi symbol, U+03D6 ISOgrk3.
     **/
    public static final String piv    = s_cre.registerEntity("piv", "&#982;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2022">&#8226;</a>
     * - bullet = black small circle, U+2022 ISOpub.
     **/
    public static final String bull   = s_cre.registerEntity("bull", "&#8226;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2026">&#8230;</a>
     * - horizontal ellipsis = three dot leader, U+2026 ISOpub.
     **/
    public static final String hellip = s_cre.registerEntity("hellip", "&#8230;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2032">&#8242;</a>
     * - prime = minutes = feet, U+2032 ISOtech.
     **/
    public static final String prime  = s_cre.registerEntity("prime", "&#8242;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2033">&#8243;</a>
     * - double prime = seconds = inches, U+2033 ISOtech.
     **/
    public static final String Prime  = s_cre.registerEntity("Prime", "&#8243;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=203e">&#8254;</a>
     * - overline = spacing overscore, U+203E NEW.
     **/
    public static final String oline  = s_cre.registerEntity("oline", "&#8254;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2044">&#8260;</a>
     * - fraction slash, U+2044 NEW.
     **/
    public static final String frasl  = s_cre.registerEntity("frasl", "&#8260;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2118">&#8472;</a>
     * - script capital P = power set = Weierstrass p, U+2118 ISOamso.
     **/
    public static final String weierp = s_cre.registerEntity("weierp", "&#8472;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2111">&#8465;</a>
     * - blackletter capital I = imaginary part, U+2111 ISOamso.
     **/
    public static final String image  = s_cre.registerEntity("image", "&#8465;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=211c">&#8476;</a>
     * - blackletter capital R = real part symbol, U+211C ISOamso.
     **/
    public static final String real   = s_cre.registerEntity("real", "&#8476;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2122">&#8482;</a>
     * - trade mark sign, U+2122 ISOnum.
     **/
    public static final String trade  = s_cre.registerEntity("trade", "&#8482;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2135">&#8501;</a>
     * - alef symbol = first transfinite cardinal, U+2135 NEW.
     **/
    public static final String alefsym = s_cre.registerEntity("alefsym", "&#8501;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2190">&#8592;</a>
     * - leftwards arrow, U+2190 ISOnum.
     **/
    public static final String larr   = s_cre.registerEntity("larr", "&#8592;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2191">&#8593;</a>
     * - upwards arrow, U+2191 ISOnum-->.
     **/
    public static final String uarr   = s_cre.registerEntity("uarr", "&#8593;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2192">&#8594;</a>
     * - rightwards arrow, U+2192 ISOnum.
     **/
    public static final String rarr   = s_cre.registerEntity("rarr", "&#8594;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2193">&#8595;</a>
     * - downwards arrow, U+2193 ISOnum.
     **/
    public static final String darr   = s_cre.registerEntity("darr", "&#8595;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2194">&#8596;</a>
     * - left right arrow, U+2194 ISOamsa.
     **/
    public static final String harr   = s_cre.registerEntity("harr", "&#8596;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=21b5">&#8629;</a>
     * - downwards arrow with corner leftwards = carriage return, U+21B5 NEW.
     **/
    public static final String crarr  = s_cre.registerEntity("crarr", "&#8629;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=21d0">&#8656;</a>
     * - leftwards double arrow, U+21D0 ISOtech.
     **/
    public static final String lArr   = s_cre.registerEntity("lArr", "&#8656;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=21d1">&#8657;</a>
     * - upwards double arrow, U+21D1 ISOamsa.
     **/
    public static final String uArr   = s_cre.registerEntity("uArr", "&#8657;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=21d2">&#8658;</a>
     * - rightwards double arrow, U+21D2 ISOtech.
     **/
    public static final String rArr   = s_cre.registerEntity("rArr", "&#8658;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=21d3">&#8659;</a>
     * - downwards double arrow, U+21D3 ISOamsa.
     **/
    public static final String dArr   = s_cre.registerEntity("dArr", "&#8659;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=21d4">&#8660;</a>
     * - left right double arrow, U+21D4 ISOamsa.
     **/
    public static final String hArr   = s_cre.registerEntity("hArr", "&#8660;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2200">&#8704;</a>
     * - for all, U+2200 ISOtech.
     **/
    public static final String forall = s_cre.registerEntity("forall", "&#8704;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2202">&#8706;</a>
     * - partial differential, U+2202 ISOtech.
     **/
    public static final String part   = s_cre.registerEntity("part", "&#8706;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2203">&#8707;</a>
     * - there exists, U+2203 ISOtech.
     **/
    public static final String exist  = s_cre.registerEntity("exist", "&#8707;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2205">&#8709;</a>
     * - empty set = null set = diameter, U+2205 ISOamso.
     **/
    public static final String empty  = s_cre.registerEntity("empty", "&#8709;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2207">&#8711;</a>
     * - nabla = backward difference, U+2207 ISOtech.
     **/
    public static final String nabla  = s_cre.registerEntity("nabla", "&#8711;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2208">&#8712;</a>
     * - element of, U+2208 ISOtech.
     **/
    public static final String isin   = s_cre.registerEntity("isin", "&#8712;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2209">&#8713;</a>
     * - not an element of, U+2209 ISOtech.
     **/
    public static final String notin  = s_cre.registerEntity("notin", "&#8713;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=220b">&#8715;</a>
     * - contains as member, U+220B ISOtech.
     **/
    public static final String ni     = s_cre.registerEntity("ni", "&#8715;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=220f">&#8719;</a>
     * - n-ary product = product sign, U+220F ISOamsb.
     **/
    public static final String prod   = s_cre.registerEntity("prod", "&#8719;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2211">&#8721;</a>
     * - n-ary sumation, U+2211 ISOamsb.
     **/
    public static final String sum    = s_cre.registerEntity("sum", "&#8721;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2212">&#8722;</a>
     * - minus sign, U+2212 ISOtech.
     **/
    public static final String minus  = s_cre.registerEntity("minus", "&#8722;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2217">&#8727;</a>
     * - asterisk operator, U+2217 ISOtech.
     **/
    public static final String lowast = s_cre.registerEntity("lowast", "&#8727;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=221a">&#8730;</a>
     * - square root = radical sign, U+221A ISOtech.
     **/
    public static final String radic  = s_cre.registerEntity("radic", "&#8730;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=221d">&#8733;</a>
     * - proportional to, U+221D ISOtech.
     **/
    public static final String prop   = s_cre.registerEntity("prop", "&#8733;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=221e">&#8734;</a>
     * - infinity, U+221E ISOtech.
     **/
    public static final String infin  = s_cre.registerEntity("infin", "&#8734;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2220">&#8736;</a>
     * - angle, U+2220 ISOamso.
     **/
    public static final String ang    = s_cre.registerEntity("ang", "&#8736;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2227">&#8743;</a>
     * - logical and = wedge, U+2227 ISOtech.
     **/
    public static final String and    = s_cre.registerEntity("and", "&#8743;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2228">&#8744;</a>
     * - logical or = vee, U+2228 ISOtech.
     **/
    public static final String or     = s_cre.registerEntity("or", "&#8744;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2229">&#8745;</a>
     * - intersection = cap, U+2229 ISOtech.
     **/
    public static final String cap    = s_cre.registerEntity("cap", "&#8745;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=222a">&#8746;</a>
     * - union = cup, U+222A ISOtech.
     **/
    public static final String cup    = s_cre.registerEntity("cup", "&#8746;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=222b">&#8747;</a>
     * - integral, U+222B ISOtech.
     **/
    public static final String integral = s_cre.registerEntity("integral", "&#8747;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2234">&#8756;</a>
     * - therefore, U+2234 ISOtech.
     **/
    public static final String there4 = s_cre.registerEntity("there4", "&#8756;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=223c">&#8764;</a>
     * - tilde operator = varies with = similar to, U+223C ISOtech.
     **/
    public static final String sim    = s_cre.registerEntity("sim", "&#8764;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2245">&#8773;</a>
     * - approximately equal to, U+2245 ISOtech.
     **/
    public static final String cong   = s_cre.registerEntity("cong", "&#8773;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2248">&#8776;</a>
     * - almost equal to = asymptotic to, U+2248 ISOamsr.
     **/
    public static final String asymp  = s_cre.registerEntity("asymp", "&#8776;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2260">&#8800;</a>
     * - not equal to, U+2260 ISOtech.
     **/
    public static final String ne     = s_cre.registerEntity("ne", "&#8800;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2261">&#8801;</a>
     * - identical to, U+2261 ISOtech.
     **/
    public static final String equiv  = s_cre.registerEntity("equiv", "&#8801;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2264">&#8804;</a>
     * - less-than or equal to, U+2264 ISOtech.
     **/
    public static final String le     = s_cre.registerEntity("le", "&#8804;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2265">&#8805;</a>
     * - greater-than or equal to, U+2265 ISOtech.
     **/
    public static final String ge     = s_cre.registerEntity("ge", "&#8805;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2282">&#8834;</a>
     * - subset of, U+2282 ISOtech.
     **/
    public static final String sub    = s_cre.registerEntity("sub", "&#8834;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2283">&#8835;</a>
     * - superset of, U+2283 ISOtech.
     **/
    public static final String sup    = s_cre.registerEntity("sup", "&#8835;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2284">&#8836;</a>
     * - not a subset of, U+2284 ISOamsn.
     **/
    public static final String nsub   = s_cre.registerEntity("nsub", "&#8836;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2286">&#8838;</a>
     * - subset of or equal to, U+2286 ISOtech.
     **/
    public static final String sube   = s_cre.registerEntity("sube", "&#8838;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2287">&#8839;</a>
     * - superset of or equal to, U+2287 ISOtech.
     **/
    public static final String supe   = s_cre.registerEntity("supe", "&#8839;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2295">&#8853;</a>
     * - circled plus = direct sum, U+2295 ISOamsb.
     **/
    public static final String oplus  = s_cre.registerEntity("oplus", "&#8853;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2297">&#8855;</a>
     * - circled times = vector product, U+2297 ISOamsb.
     **/
    public static final String otimes = s_cre.registerEntity("otimes", "&#8855;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=22a5">&#8869;</a>
     * - up tack = orthogonal to = perpendicular, U+22A5 ISOtech.
     **/
    public static final String perp   = s_cre.registerEntity("perp", "&#8869;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=22c5">&#8901;</a>
     * - dot operator, U+22C5 ISOamsb.
     **/
    public static final String sdot   = s_cre.registerEntity("sdot", "&#8901;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2308">&#8968;</a>
     * - left ceiling = apl upstile, U+2308 ISOamsc.
     **/
    public static final String lceil  = s_cre.registerEntity("lceil", "&#8968;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2309">&#8969;</a>
     * - right ceiling, U+2309 ISOamsc.
     **/
    public static final String rceil  = s_cre.registerEntity("rceil", "&#8969;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=230a">&#8970;</a>
     * - left floor = apl downstile, U+230A ISOamsc.
     **/
    public static final String lfloor = s_cre.registerEntity("lfloor", "&#8970;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=230b">&#8971;</a>
     * - right floor, U+230B ISOamsc.
     **/
    public static final String rfloor = s_cre.registerEntity("rfloor", "&#8971;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2329">&#9001;</a>
     * - left-pointing angle bracket = bra, U+2329 ISOtech.
     **/
    public static final String lang   = s_cre.registerEntity("lang", "&#9001;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=232a">&#9002;</a>
     * - right-pointing angle bracket = ket, U+232A ISOtech.
     **/
    public static final String rang   = s_cre.registerEntity("rang", "&#9002;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=25ca">&#9674;</a>
     * - lozenge, U+25CA ISOpub.
     **/
    public static final String loz    = s_cre.registerEntity("loz", "&#9674;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2660">&#9824;</a>
     * - black spade suit, U+2660 ISOpub.
     **/
    public static final String spades = s_cre.registerEntity("spades", "&#9824;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2663">&#9827;</a>
     * - black club suit = shamrock, U+2663 ISOpub.
     **/
    public static final String clubs  = s_cre.registerEntity("clubs", "&#9827;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2665">&#9829;</a>
     * - black heart suit = valentine, U+2665 ISOpub.
     **/
    public static final String hearts = s_cre.registerEntity("hearts", "&#9829;");

    /**
     * <a href="http://www.eki.ee/letter/chardata.cgi?ucode=2666">&#9830;</a>
     * - black diamond suit, U+2666 ISOpub.
     **/
    public static final String diams  = s_cre.registerEntity("diams", "&#9830;");


}
