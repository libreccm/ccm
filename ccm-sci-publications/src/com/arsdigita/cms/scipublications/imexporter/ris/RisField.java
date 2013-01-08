/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.scipublications.imexporter.ris;

/**
 * <p>
 * Fields supported by RIS for describing a publication, excluding the
 * fields <code>TY</code> (type of reference) and <code>ER</code> (end of 
 * reference). These fields are automatically set by the converters.
 * </p>
 * <p>
 * The descriptions of the fields or tags as they are called in the RIS
 * specification are copied from the specification
 * </p>
 *
 * @author Jens Pelzetter
 */
public enum RisField {

    /**
     * Reference ID. According to the specification not used by reference
     * managers.
     */
    ID,
    /**
     * Title Primary. Note that the BT tag maps to this field only for
     * Whole Book and Unpublished Work references. This field can contain
     * alphanumeric characters; there is no practical length limit to this
     * field.
     */
    T1,
    /**
     * @see #T1
     */
    TI,
    /**
     * @see #T1
     * @see #T2
     */
    CT,
    /**
     * @see #T1
     * @see #T2
     */
    BT,
    /**
     * <p>
     * Author Primary. Each author must be on a separate line, preceded by this
     * tag. Each reference can contain
     * unlimited author fields, and can contain up to 255 characters for each
     * field. The author name must be in the following syntax:
     * </p>
     * <pre>
     * Lastname,Firstname,Suffix
     * </pre>
     * <p>
     * For Firstname, you can use full names, initials, or both. The format for
     * the author’s first name is as follows:
     * </p>
     * <pre>
     * Phillips,A.J.
     * Phillips,Albert John
     * Phillips,Albert
     * </pre>
     * <dl>
     * <dt><code>Lastname</code></dt>
     * <dd>Any string of letters, spaces, and hyphens</dd>
     * <dt><code>Firstname</code></dt>
     * <dd>Any string of letters, spaces, and hyphens</dd>
     * <dt><code>Initial</code></dt>
     * <dd>Any single letter followed by a period</dd>
     * <dt><code>Full Name</code></dt>
     * <dd>Any string of letters, spaces, and hyphens</dd>
     * <dt><code>Suffix</code></dt>
     * <dd>Jr/Sr/II/III/MD etc. (Phillips,A.J.,Sr.); use of the suffix is
     * optional</dd>
     * </dl>
     */
    A1,
    /**
     * @see #A1
     */
    AU,
    /**
     * <p>
     * Date Primary. This date must be in the following format:
     * </p>
     * <pre>
     * YYYY/MM/DD/other info
     * </pre>
     * <p>
     * The year, month and day fields are all numeric. The other info field can
     * be any string of letters, spaces and hyphens. Note that each specific
     * date information is optional, however the slashes (“/”) are not. For
     * example, if you just had the <code>year</code> and
     * <code>other info</code>, then the output
     * would look like:
     * </p>
     * <pre>
     * “1993///Spring.”
     * </pre>
     */
    Y1,
    /**
     * @see #Y1
     */
    PY,
    /**
     * Notes. These are free text fields and can contain
     * alphanumeric characters; there is no practical length limit to this
     * field.
     */
    N1,
    /**
     * @see #N1
     */
    AB,
    /**
     * Keywords. Each keyword or phrase must be on its own line, preceded by
     * this tag. A keyword can consist of multiple words (phrases) and can be
     * up to
     * 255 characters long. There can unlimited keywords in a reference.
     */
    KW,
    /**
     * <p>
     * Reprint status. This optional field can contain one of three status
     * notes. Each must be in uppercase, and the date after “ON REQUEST” must
     * be in
     * USA format, in parentheses: (MM/DD/YY). If this field is blank in your
     * downloaded text file, the Import function assumes the reprint status is
     * “NOT IN FILE.”
     * </p>
     * <p>The three options are:</p>
     * <dl>
     * <dt><code>IN FILE</code></dt>
     * <dd>This is for references that you have a physical copy of in your
     * files.</dd>
     * <dt><code>NOT IN FILE</code></dt>
     * <dd>This is for references that you do not have physical copies of in
     * your files.</dd>
     * <dt><code>ON REQUEST (mm/dd/yy)</code></dt>
     * <dd>This means that you have sent for a reprint of the reference;
     * the date is the date on which the reprint was requested (in mm/dd/yy
     * format).</dd>
     * </dl>
     */
    RP,
    /**
     * Start page number; an alphanumeric string, there is no practical length
     * limit to this field.
     */
    SP,
    /**
     * Ending page number, as above.
     * @see #SP
     */
    EP,
    /**
     * Periodical name: full format. This is an alphanumeric field of up to
     * 255 characters.
     */
    JF,
    /**
     * Periodical name: standard abbreviation. This is the 362 Appendix C—RIS
     * Format Specifications
     */
    JO,
    /**
     * periodical in which the article was (or is to be, in the case of in-
     * press references) published. This is an alphanumeric field of up to 255
     * characters. If possible, periodical names should be abbreviated in the
     * Index Medicus style, with periods after the abbreviations. If this is
     * not possible (your large bibliography file in your wordprocessor has no
     * periods after abbreviations), you can use the “RIS Format (Adds periods)”
     * Import filter definition. This definition uses the Periodical Word
     * Dictionary.
     */
    JA,
    /**
     * Periodical name: user abbreviation 1. This is an alphanumeric field of
     * up to 255 characters.
     */
    J1,
    /**
     * Periodical name: user abbreviation 2. This is an alphanumeric field of
     * up to 255 characters.
     */
    J2,
    /**
     * Volume number. This is an optional field, there is no practical length
     * limit to this field.
     */
    VL,
    /**
     * Title Secondary. Note that the BT tag maps to this field for all 
     * reference types except for Whole Book and Unpublished Work
     * references. This field can contain alphanumeric characters; there is no
     * practical length limit to this field.
     */
    T2,
    /**
     * Author Secondary. Each author must be on a separate line, preceded by
     * this tag. Each reference can contain unlimited author
     * fields. The author name must be in the correct syntax (refer to A1 and AU
     * fields). This author name can be up to 255 characters long.
     * @see #A1
     */
    A2,
    /**
     * @see #A2
     */
    ED,
    /**
     * Issue. This is an alphanumeric field, there is no practical
     * length limit to this field.
     */
    IS,
    /**
     * @see #IS
     */
    CP,
    /**
     * City of publication; this is an alphanumeric field; there is no
     * practical length limit to this field.
     */
    CY,
    /**
     * Publisher; this is an alphanumeric field; there is no practical length
     * limit to this field.
     */
    PB,
    /**
     * Title Series. This field can contain alphanumeric characters; there is
     * no practical length limit to this field.
     */
    T3,
    /**
     * Author Series. Each author must be on a separate line, preceded by
     * this tag. Each reference can unlimited author fields. The author name must be
     * in the correct syntax (refer to A1 and AU fields). Each author name can be up
     * to 255 characters long.
     */
    A3,
    /**
     * Used in GEN type
     */
    A4,
    /**
     *  Abstract. This is a free text field and can contain alphanumeric
     * characters; there is no practical length limit to this field.
     */
    N2,
    /**
     * ISSN/ISBN. This field can contain alphanumeric characters. There is no
     * practical length limit to this field.
     */
    SN,
    /**
     * Availability. This field can contain alphanumeric characters. There is
     * no practical length limit to this field.
     */
    AV,
    /**
     * Date Secondary. (Refer to Y1 and PY fields).
     * @see #Y1
     * @see #PY
     */
    Y2,
    /**
     * Miscellaneous 1. This field can contain alphanumeric characters. There
     * is no practical length limit to this field.
     */
    M1,
    /**
     * Miscellaneous 2. This field can contain alphanumeric characters. There
    is no practical length limit to this field.
     */
    M2,
    /**
     * Miscellaneous 3. This field can contain alphanumeric characters. There
     * is no practical length limit to this field.
     */
    M3,
    /**
     * Address. This is a free text field and contain alphanumeric
     * characters; there is no practical length limit to this field.
     */
    AD,
    /**
     * Web/URL. There is no practical length limit to this field. URL
     * addresses can be entered individually, one per tag or multiple addresses can
     * be entered on one line using a semi-colon as a separator.
     */
    UR,
    /**
     * Link to PDF. There is no practical length limit to this field. URL
     * addresses can be entered individually, one per tag or multiple addresses can
     * be entered on one line using a semi-colon as a separator.
     */
    L1,
    /**
     * Link to Full-text. There is no practical length limit to this field.
     * URL addresses can be entered Reference Manager User’s Guide 365 individually,
     * one per tag or multiple addresses can be entered on one line using a semi-
     * colon as a separator.
     */
    L2,
    /**
     * Related Records. There is no practical length limit to this field.
     */
    L3,
    /**
     * Images. There is no practical length limit to this field.
     */
    L4,
    /**
     * Edition. Not found in the specification, but used but by some other
     * exports.
     */
    ET,
    /**
     * Reviewed item
     */
    RI,
    /**
     * DA
     */
    DA,
    C1,
    C2,
    C3,
    C4,
    C5,
    /**
     * Number of volumes
     */
    NV,
    /**
     * DOI
     */
    DO,
    /**
     * Begin of a new reference. Value is the type of the reference.
     */
    TY,
    /**
     * End of reference. Last tag of a dataset. Has no value.
     */
    ER,
}
