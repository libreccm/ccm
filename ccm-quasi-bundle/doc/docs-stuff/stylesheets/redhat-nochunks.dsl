<!DOCTYPE style-sheet PUBLIC "-//James Clark//DTD DSSSL Style Sheet//EN" [
<!ENTITY % html "IGNORE">
<![%html;[
<!ENTITY % print "IGNORE">
<!ENTITY docbook.dsl PUBLIC "-//Norman Walsh//DOCUMENT DocBook HTML Stylesheet//EN" CDATA dsssl>
]]>
<!ENTITY % print "INCLUDE">
<![%print;[
<!ENTITY docbook.dsl PUBLIC "-//Norman Walsh//DOCUMENT DocBook Print Stylesheet//EN" CDATA dsssl>
]]>
]>

<!--
Changelog
* Feb 8 2002 Tammy Fox <tfox@redhat.com>
- redesign first page of chapters and appendixes
- use official redhat logo for title page

* Feb 04 2002 Tammy Fox <tfox@redhat.com>
- Remove HRs from articles if nochunks is specified

* Nov 15 2001 Tammy Fox <tfox@redhat.com>
- Use id as HTML filename for legalnotice (thanks to twaugh)

* Nov 12 2001 Tammy Fox <tfox@redhat.com>
- Make footnotes appear on the bottom of the page instead of end of chapter

* Oct 2001 Tammy Fox <tfox@redhat.com>
- Added print stylesheet specification
- Updated html stylesheet specification with new stuff in docbook-utils.dsl from RHL 7.2
- Renamed redhat.dsl
- Removed ulinks from index
- Enumurate sections in print version
- Added section to format title page
- Changed body indention to 0
- Made article and book titlepages more spaced out
- Added [] around keycap in print version
- Made part divider page have larger text and centered
- Changed paper size, margins, and font sizes
- Added publisher to book title page
- Don't show url for ulink in parenthesis for print
- Made page numbers bold and upright and on header
- Decrease header and footer margins
- Made header bold and upright
- Increased spacing between column line and text in printed tables
- Added Red Hat copyright on footer

* Feb 2001 Ed Bailey <ed@redhat.com>
- Further hacked

* Nov 2000 Tammy Fox <tfox@redhat.com>
- Tried to make html and print stylesheets more similar
- Fixed address formatting on first html page
- Added stuff to make html for articles look ok

* Dave Mason <dcm@redhat.com>
- original creator of stylesheet based on Norman Walsh's Modular Stylesheets
-->

<style-sheet>

<style-specification id="print" use="docbook">
<style-specification-body> 

;;==========================================================================
;;                               PRINT
;;==========================================================================

;;======================================
;;General Options
;;======================================

;;Do you want to print on both sides of the paper?
;;Set to true to alternate outer and inner headers
(define %two-side% 
 #t)

;;Do you want enumerated sections? (E.g, 1.1, 1.1.1, 1.2, etc.)
(define %section-autolabel% 
 #t)

;;What is the default extension for graphics?
(define %graphic-default-extension% 
  "eps")

;;Show URL links? If the text of the link and the URL are identical,
;;the parenthetical URL is suppressed.
(define %show-ulinks%
 #f)

;Make Ulinks footnotes to stop bleeding in the edges - this increases
;'jade --> print' time tremendously keep this in mind before
;complaining!
;changed to f - tfox oct2001
(define %footnote-ulinks%
  #f)

;; footnotes at the bottom of the page instead of the bottom of the chapter
(define bop-footnotes 
  #t)

;;Tex Backend on
(define tex-backend 
 #t)

;;Define Line Spacing
(define %line-spacing-factor% 1.1)

;;Define the Paragraph Style
(define para-style
  (style
   font-size: %bf-size%
   font-weight: 'medium
   font-posture: 'upright
   font-family-name: %body-font-family%
   line-spacing: (* %bf-size% %line-spacing-factor%)))

(define ($object-titles-after$)
  (list (normalize "figure")))

;;======================================
;;Book Options
;;======================================


;;Do you want a title page for a Book?
(define %generate-book-titlepage%
 #t)

;;Do you want a separate page for the title?
(define %generate-book-titlepage-on-separate-page%
 #t)

;;Generate Book TOC?
(define %generate-book-toc% 
 #f)

;;What depth should the TOC generate?
;;!Only top level of appendixes!
(define (toc-depth nd)
  (if (string=? (gi nd) (normalize "book"))
      3
      (if (string=? (gi nd) (normalize "appendix"))
        0
        1)))

;;Do you want a TOC for the element part?
(define %generate-part-toc% 
 #f)

;;Do you want the part toc on the part titlepage or separate?
(define %generate-part-toc-on-titlepage%
 #t)

;;Generate Part Title Page?
(define %generate-part-titlepage% 
  #f)

;;Do you want the Part intro on the part title page?
(define %generate-partintro-on-titlepage%
  #t)

;;What elements should have a LOT?
(define ($generate-book-lot-list$)
  (list (normalize "equation")))

;;Do you want chapters enumerated?
(define %chapter-autolabel% 
 #t)

;;Do you want Chapter's and Appendix's 
;;to have automatic labels?
(define %chap-app-running-head-autolabel% 
  #t)

;;======================================
;;Book Title Page
;;======================================

(define (book-titlepage-recto-elements)
  (list (normalize "title")
        (normalize "subtitle")
	(normalize "publisher")))
;;	(normalize "copyright")))

(mode book-titlepage-recto-mode
  (element title 
    (make paragraph
      use: set-titlepage-recto-style
      font-size: (HSIZE 10)
      font-posture: 'italic
      font-family-name: %title-font-family%
      line-spacing: (* (HSIZE 10) %line-spacing-factor%)
      space-before: (* (HSIZE 5) %head-before-factor%)
      quadding: 'end
      keep-with-next?: #t
      (with-mode title-mode
        (process-children-trim))))
  (element subtitle 
    (make paragraph
      use: set-titlepage-recto-style
      font-size: (HSIZE 10)
      font-family-name: %title-font-family%
      line-spacing: (* (HSIZE 10) %line-spacing-factor%)
      space-before: (* (HSIZE 30) %head-before-factor%)
      quadding: %division-title-quadding%
      keep-with-next?: #t
      (with-mode title-mode
        (process-children-trim))))
  (element publisher
    (make paragraph
      use: set-titlepage-recto-style
      font-size: (HSIZE 6)
      font-family-name: %title-font-family%
      line-spacing: (* (HSIZE 10) %line-spacing-factor%)
      space-before: (* (HSIZE 30) %head-before-factor%)
      quadding: %division-title-quadding%
      keep-with-next?: #t
      (with-mode title-mode
        (process-children-trim))))
  (element copyright
    (make paragraph
      use: article-titlepage-recto-style
      space-before: (* (HSIZE 10) %head-before-factor%)
      quadding: %article-title-quadding%
      (literal (gentext-element-name (current-node)))
      (literal "\no-break-space;")
      (literal (dingbat "copyright"))
      (literal "\no-break-space;")
      (process-children)))
  (element (copyright year)
    (make sequence
      (process-children)
      (if (not (last-sibling? (current-node)))
          (literal ", ")
          (literal (string-append " " (gentext-by) " ")))))
  (element (copyright holder) ($charseq$))

)

;;======================================
;;Part Title
;;======================================

(define ($parttitle$)
  (make simple-page-sequence
	top-margin: %top-margin%
	bottom-margin: %bottom-margin%
	left-margin: %left-right-margin%
	right-margin: %left-right-margin%
	input-whitespace-treatment: 'collapse
	(make paragraph
	      font-family-name: %title-font-family%
	      font-weight: 'bold
	      font-size: (HSIZE 5)
	      line-spacing: (* (HSIZE 5) %line-spacing-factor%)
	      space-before: (* (HSIZE 50) %head-before-factor%)
;	      space-after: (* (HSIZE 3) %head-after-factor% 4)
	      quadding: 'center
	      keep-with-next?: #t
	      (process-children-trim))))

(element (part title) ($parttitle$))

;;======================================
;;Article Options
;;======================================

;;Do you want a title page for an Article?
(define %generate-article-titlepage%
 #f)

;;Generate Article TOC?
(define %generate-article-toc% 
 #f)

;;Do you want a separate page for the title?
(define %generate-article-titlepage-on-separate-page%
 #t)

;;Do you want the article toc on the titlepage or separate?
(define %generate-article-toc-on-titlepage%
 #f)

;;Do you want to start new page numbers with each article?
(define %article-page-number-restart%
 #f)

;;Titlepage Separate?
(define (chunk-skip-first-element-list)
  '())

;;Titlepage Not Separate
;(define (chunk-skip-first-element-list)
;  (list (normalize "sect1")
;	(normalize "section")))

;;========================
;;Title Pages for Articles
;;========================
;;added tfox

;;Which elements should appear 
;;on title page?
(define (article-titlepage-recto-elements)
  (list (normalize "title")
	(normalize "subtitle")
        (normalize "copyright")
        (normalize "authorgroup")
	(normalize "revhistory")
	(normalize "abstract")
	))
;;removed for now until I can figure
;;out how to make a new page for legalnotice
;;        (normalize "legalnotice"))) 

(mode article-titlepage-recto-mode
  (element title 
    (make paragraph
      use: set-titlepage-recto-style
      font-size: (HSIZE 10)
      line-spacing: (* (HSIZE 10) %line-spacing-factor%)
      space-before: (* (HSIZE 5) %head-before-factor%)
      quadding: %division-title-quadding%
      keep-with-next?: #t
      (with-mode title-mode
        (process-children-trim))))
  (element subtitle 
    (make paragraph
      use: set-titlepage-recto-style
      font-size: (HSIZE 10)
      line-spacing: (* (HSIZE 10) %line-spacing-factor%)
      space-before: (* (HSIZE 10) %head-before-factor%)
      quadding: %division-title-quadding%
      keep-with-next?: #t
      (with-mode title-mode
        (process-children-trim))))
  (element copyright
    (make paragraph
      use: article-titlepage-recto-style
      space-before: (* (HSIZE 10) %head-before-factor%)
      quadding: %article-title-quadding%
      (literal (gentext-element-name (current-node)))
      (literal "\no-break-space;")
      (literal (dingbat "copyright"))
      (literal "\no-break-space;")
      (process-children)))
  (element (copyright year)
    (make sequence
      (process-children)
      (if (not (last-sibling? (current-node)))
          (literal ", ")
          (literal (string-append " " (gentext-by) " ")))))
  (element (copyright holder) ($charseq$))
  (element authorgroup
    (make paragraph
      use: set-titlepage-recto-style
      font-size: (HSIZE 3)
      line-spacing: (* (HSIZE 10) %line-spacing-factor%)
      space-before: (* (HSIZE 5) %head-before-factor%)
      quadding: %division-title-quadding%
      keep-with-next?: #t
      (with-mode title-mode
        (process-children-trim))))
  (element abstract
    (make display-group
      use: article-titlepage-verso-style ;; EVEN THOUGH IT'S RECTO!
      quadding: 'start
      start-indent: (+ (inherited-start-indent) 0.5in)
      end-indent: (+ (inherited-end-indent) 0.5in)
      ($semiformal-object$)))
  (element (abstract title) (empty-sosofo))

;;end of article-titlepage-recto-mode 
)

;;======================================
;;Columns
;;======================================

;;How many columns do you want?
(define %page-n-columns%
 1)

;;How much space between columns?
(define %page-column-sep%
 0.2in)

;;How many Columns on the titlepage?
(define %titlepage-n-columns%
  1)

;;Balance columns?
(define %page-balance-colums%
#t)

;;======================================
;;Divisions
;;======================================


(define ($divtitlepage$)
  (make simple-page-sequence
	top-margin: %top-margin%
	bottom-margin: %bottom-margin%
	left-margin: %left-right-margin%
	right-margin: %left-right-margin%
	input-whitespace-treatment: 'collapse
	(make paragraph
	      font-family-name: %title-font-family%
	      font-weight: 'bold
	      font-size: (HSIZE 10)
	      line-spacing: (* (HSIZE 3) %line-spacing-factor%)
	      space-before: (* (HSIZE 3) %head-before-factor%)
	      space-after: (* (HSIZE 3) %head-after-factor% 4)
	      quadding: 'start
	      keep-with-next?: #t
	      (process-children-trim))))

(element (SET TITLE) ($divtitlepage$))
(element (BOOK TITLE) ($divtitlepage$))
(element (ARTHEADER TITLE) ($divtitlepage$))
(element (ARTICLEINFO TITLE) ($divtitlepage$))

;;======================================
;;Fonts
;;======================================

;;Defines the general size of the text in the document. normal(10),
;;presbyopic(12), and large-type(24). 
(define %visual-acuity%
 "normal")

;;What font would you like for titles?
(define %title-font-family% 
  "Helvetica")

;;What font would you like for the body?
(define %body-font-family% 
 "Palatino")

;;What font would you like for mono-seq?
(define %mono-font-family% 
 "Courier New")

;;If the base fontsize is 10pt, and '%hsize-bump-factor%' is
;; 1.2, hsize 1 is 12pt, hsize 2 is 14.4pt, hsize 3 is 17.28pt, etc
(define %hsize-bump-factor% 
 1.1)

;;What size do you want the body fonts?
(define %bf-size%
 (case %visual-acuity%
    (("tiny") 6pt)
    (("normal") 8pt)
    (("presbyopic") 10pt)
    (("large-type") 20pt)))

;;(define %bf-size%
;; (case %visual-acuity%
;;    (("tiny") 8pt)
;;    (("normal") 10pt)
;;    (("presbyopic") 12pt)
;;    (("large-type") 24pt)))

(define-unit em %bf-size%)

;;=====================================================
;;Spacing before First Page of Chapters and Appendixes
;;=====================================================

(define ($component-title$)
  (let* ((info (cond
                ((equal? (gi) (normalize "appendix"))
                 (select-elements (children (current-node)) (normalize "docinfo")))
                ((equal? (gi) (normalize "article"))
                 (node-list-filter-by-gi (children (current-node))
                                         (list (normalize "artheader")
                                               (normalize "articleinfo"))))
                ((equal? (gi) (normalize "bibliography"))
                 (select-elements (children (current-node)) (normalize "docinfo")))
                ((equal? (gi) (normalize "chapter"))
                 (select-elements (children (current-node)) (normalize "docinfo")))
                ((equal? (gi) (normalize "dedication"))
                 (empty-node-list))
                ((equal? (gi) (normalize "glossary"))
                 (select-elements (children (current-node)) (normalize "docinfo")))
                ((equal? (gi) (normalize "index"))
                 (select-elements (children (current-node)) (normalize "docinfo")))
                ((equal? (gi) (normalize "preface"))
                 (select-elements (children (current-node)) (normalize "docinfo")))
                ((equal? (gi) (normalize "reference"))
                 (select-elements (children (current-node)) (normalize "docinfo")))
                ((equal? (gi) (normalize "setindex"))
                 (select-elements (children (current-node)) (normalize "docinfo")))
                (else
                 (empty-node-list))))
         (exp-children (if (node-list-empty? info)
                           (empty-node-list)
                           (expand-children (children info)
                                            (list (normalize "bookbiblio")
                                                  (normalize "bibliomisc")
                                                  (normalize "biblioset")))))
         (parent-titles (select-elements (children (current-node)) (normalize
								    "title")))
         (info-titles   (select-elements exp-children (normalize "title")))
         (titles        (if (node-list-empty? parent-titles)
                            info-titles
                            parent-titles))
         (subtitles     (select-elements exp-children (normalize "subtitle"))))
    ;; ==================== Changed for Chapter ======================

    (if (or (equal? (gi) (normalize "chapter"))
	    (equal? (gi) (normalize "appendix"))
	    (equal? (gi) (normalize "preface")))
	(make sequence
	  (make paragraph
	    font-family-name: %title-font-family%
	    font-weight: 'semi-light
	    font-size: (HSIZE 6)
	    space-before: 1.5in
	    quadding: 'end
	    heading-level: (if %generate-heading-level% 1 0)
	    keep-with-next?: #t
	    (if (string=? (element-label) "")
		(empty-sosofo)
		(literal (gentext-element-name-space (current-node))
			 (element-label)
			 (gentext-label-title-sep (gi)))))
	  ;; Chapter title
	  (make paragraph
	    font-family-name: %title-font-family%
	    font-weight: 'bold
	    font-size: (HSIZE 6)
	    line-spacing: (* (HSIZE 2) %line-spacing-factor%)
	    space-before: (* (HSIZE 2) %line-spacing-factor%)
	    space-after: (* (HSIZE 1) %head-after-factor% 4)
	    start-indent: 0pt
	    first-line-start-indent: 0pt
	    quadding: 'end
	    heading-level: (if %generate-heading-level% 1 0)
	    keep-with-next?: #t
	    (if (node-list-empty? titles)
		(element-title-sosofo) ;; get a default!
		(with-mode chapter-title-mode
		  (make sequence
		    (process-node-list titles))))))
    ;; If not Chapter:
    (make sequence
      (make paragraph
	font-family-name: %title-font-family%
	font-weight: 'bold
	font-size: (HSIZE 4)
	line-spacing: (* (HSIZE 4) %line-spacing-factor%)
	space-before: (* (HSIZE 4) %head-before-factor%)
	start-indent: 0pt
	first-line-start-indent: 0pt
	quadding: %component-title-quadding%
	heading-level: (if %generate-heading-level% 1 0)
	keep-with-next?: #t
	
	(if (string=? (element-label) "")
	    (empty-sosofo)
	    (literal (gentext-element-name-space (current-node))
		     (element-label)
		     (gentext-label-title-sep (gi))))
	
	(if (node-list-empty? titles)
	    (element-title-sosofo) ;; get a default!
	    (with-mode component-title-mode
		    (make sequence
		      (process-node-list titles)))))
	    
      (make paragraph
	font-family-name: %title-font-family%
	font-weight: 'bold
	font-posture: 'italic
	font-size: (HSIZE 3)
	line-spacing: (* (HSIZE 3) %line-spacing-factor%)
	space-before: (* 0.5 (* (HSIZE 3) %head-before-factor%))
	space-after: (* (HSIZE 4) %head-after-factor%)
	start-indent: 0pt
	first-line-start-indent: 0pt
	quadding: %component-subtitle-quadding%
	keep-with-next?: #t
	      
	(with-mode component-title-mode
	  (make sequence
	    (process-node-list subtitles))))))))

;; Chapter-Title Mode
(mode chapter-title-mode
  (element title
    (make sequence
      (process-children))))
  
;;======================================
;;Margins
;;======================================
;;default margins defined in /usr/share/sgml/docbook/dsssl-stylesheets/print/dbparam.dsl

(define %left-right-margin% 3pi)

;;How much indentation for the body?
(define %body-start-indent% 
 0pi)

;;How big is the left margin? (relative to physical page)
(define %left-margin% 
 3pi) ;white-paper-column

;;How big is the right margin? (relative to physical page)
(define %right-margin% 
 3pi) ;white-paper-column

;;How big do you want the margin at the top?
(define %top-margin%
(if (equal? %visual-acuity% "large-type")
      4pi
      2pi))

;;How big do you want the margin at the bottom?
(define %bottom-margin% 
 (if (equal? %visual-acuity% "large-type")
      4pi 
      2pi))

(define %header-margin% 
  ;; Height of header margin
  (if (equal? %visual-acuity% "large-type") 
      4pi 
      2pi))

(define %footer-margin% 
  ;; Height of footer margin
  2pi)

;;Define the text width. (Change the elements in the formula rather
;;than the formula itself)
;(define %text-width% (- %page-width% (* %left-right-margin% 2)))
(define %text-width%  (- %page-width% (+ %left-margin% %right-margin%)))

;;Define the body width. (Change the elements in the formula rather
;;than the formula itself)
(define %body-width% 
 (- %text-width% %body-start-indent%))

;;Define distance between paragraphs
(define %para-sep% 
 (/ %bf-size% 2.0))

;;Define distance between block elements (figures, tables, etc.).
(define %block-sep% 
 (* %para-sep% 2.0))

;;Indent block elements?
(define %block-start-indent% 
  0pt)
;0pt

;;======================================
;;Admon Graphics
;;======================================

;;Do you want admon graohics on?
(define %admon-graphics%
 #t)

;;Where are the admon graphics?
(define %admon-graphics-path%
 "./rh-sgml/stylesheet-images/")

;;define which images to use
(define ($admon-graphic$ #!optional (nd (current-node)))
  (cond ((equal? (gi nd) (normalize "tip"))
     (string-append %admon-graphics-path% "tip.eps"))
    ((equal? (gi nd) (normalize "note"))
     (string-append %admon-graphics-path% "note.eps"))
    ((equal? (gi nd) (normalize "important"))
     (string-append %admon-graphics-path% "important.eps"))
    ((equal? (gi nd) (normalize "caution"))
     (string-append %admon-graphics-path% "caution.eps"))
    ((equal? (gi nd) (normalize "warning"))
     (string-append %admon-graphics-path% "warning.eps"))
    (else (error (string-append (gi nd) " is not an admonition.")))))

(define ($admon-graphic-width$ #!optional (nd (current-node)))
  ;; REFENTRY admon-graphic-width
  ;; PURP Admonition graphic file width
  ;; DESC
  ;; Given an admonition node, returns the width of the graphic that will
  ;; be used for that admonition.
  ;;
  ;; All of the default graphics in the distribution are 0.3in wide.
  ;; /DESC
  ;; AUTHOR N/A
  ;; /REFENTRY
  ;; tfox - changed to 0.2
  0.2in)

;;======================================
;;Quadding
;;======================================

;;What quadding do you want by default; start, center, justify, or end?
(define %default-quadding%
 'justify)

;;What quadding for component titles(Chapter, Appendix, etc)?
(define %component-title-quadding% 
 'start)

;;What quadding for section titles?
(define %section-title-quadding% 
 'start)

;;What quadding for section sub-titles?
(define %section-subtitle-quadding%
 'start)

;;What quadding for article title?
(define %article-title-quadding% 
 'center)

;;What quadding for article sub-titles?
(define %article-subtitle-quadding%
 'center)

;;What quadding for division subtitles?
(define %division-subtitle-quadding% 
  'start)

;;What quadding for component subtitles?
(define %component-subtitle-quadding% 
  'start)

;;======================================
;;Paper Options
;;======================================

;;If you change the paper size, you
;;need to change it in indexed-ps
;;and indexed-pdf as well.

;;What size paper do you need? A4, USletter, USlandscape, or RedHat?
(define %paper-type%
 "RedHat")
;; "USletter")

;;Now define those paper types' width
(define %page-width%
 (case %paper-type%
    (("A4") 210mm)
    (("USletter") 8.5in)
    (("USlandscape") 11in)
;;    (("RedHat") 7.25in)))
      (("RedHat") 5.49in)))

;;Now define those paper types' height
(define %page-height%
 (case %paper-type%
    (("A4") 297mm)
    (("USletter") 11in)
    (("USlandscape") 8.5in)
;;    (("RedHat") 9.25in)))
    (("RedHat") 8.26in)))

;;======================================
;;Headers and Footers
;;======================================

(define ($page-number-header-footer$)
  (let ((component (ancestor-member (current-node)
                                    (append (division-element-list)
                                            (component-element-list)))))
    (make sequence
      font-weight: 'bold
      font-posture: 'upright
      (literal 
       (gentext-page)
       (if %page-number-restart%
           (cond
            ((equal? (gi component) (normalize "appendix") )
             (string-append
              (element-label component #t)
              (gentext-intra-label-sep "_pagenumber")))
            ((equal? (gi component) (normalize "chapter"))
             (string-append
              (element-label component #t)
              (gentext-intra-label-sep "_pagenumber")))
            (else ""))
           ""))
      (page-number-sosofo))))

(define ($title-header-footer$)
  (let* ((title (if (equal? (gi) (normalize "refentry"))
                   ($refentry-header-footer-element$)
                   ($title-header-footer-element$))))
    (make sequence
      font-weight: 'bold
      font-posture: 'upright
      (with-mode hf-mode
	(process-node-list title)))))

;; not currently using
(define ($rule-header$)
  (let* ((title (if (equal? (gi) (normalize "refentry"))
		    ($refentry-header-footer-element$)
		    ($title-header-footer-element$))))
    (make sequence
      font-weight: 'bold
      font-posture: 'upright
      (make paragraph
	quadding: 'start
	(with-mode hf-mode
	  (process-node-list title)))
    (make rule
	orientation: 'escapement
	line-thickness: 1pt
	length: 7.2in
	layer: 0))))

;;must have a mediaobject declared inside bookinfo
(define ($shadowman-header$)
  (let* (
	 (bookinfo
	  (select-elements (children (sgml-root-element)) (normalize
							   "bookinfo")))
	 (mediaobject
	  (select-elements (children bookinfo) (normalize "mediaobject"))))
  (make paragraph
    quadding: `start
    (with-mode hf-mode
      (process-node-list mediaobject))
    )))

(define (page-inner-header gi)
  (cond
   ((equal? (normalize gi) (normalize "dedication")) (empty-sosofo))
   ((equal? (normalize gi) (normalize "lot")) (empty-sosofo))
   ((equal? (normalize gi) (normalize "part")) (empty-sosofo))
   ((equal? (normalize gi) (normalize "toc")) (empty-sosofo))
   (else ($title-header-footer$))))

(define (page-center-header gi)
  (empty-sosofo))

(define (page-outer-header gi)
  (cond
   ((equal? (normalize gi) (normalize "dedication")) (empty-sosofo))
   ((equal? (normalize gi) (normalize "lot")) (empty-sosofo))
   ((equal? (normalize gi) (normalize "part")) (empty-sosofo))
   ((equal? (normalize gi) (normalize "toc")) (empty-sosofo))
   (else ($page-number-header-footer$))))

;(define ($red-hat-header-footer$)
;  (let ((component (ancestor-member (current-node)
;                                    (append (division-element-list)
;                                            (component-element-list)))))
;    (make sequence
;      font-weight: 'bold
;      font-posture: 'upright
;     (literal 
;       "Copyright")
;      (literal "\no-break-space;")
;      (literal (dingbat "copyright"))
;      (literal "\no-break-space;")
;      (literal "2001 Red Hat, Inc."))))

(define (page-inner-footer gi)
  (empty-sosofo))

(define (page-center-footer gi)
  (empty-sosofo))
;  (cond
;   ((equal? (normalize gi) (normalize "dedication")) (empty-sosofo))
;   ((equal? (normalize gi) (normalize "lot")) (empty-sosofo))
;   ((equal? (normalize gi) (normalize "part")) (empty-sosofo))
;   ((equal? (normalize gi) (normalize "toc")) (empty-sosofo))
;   (else ($red-hat-header-footer$))))

(define (page-outer-footer gi)
  (empty-sosofo))

;; define headers and footers for first pages (i.e. first page of chapters)

(define (first-page-center-header gi)
  (cond
   ((equal? (normalize gi) (normalize "dedication")) (empty-sosofo))
   ((equal? (normalize gi) (normalize "lot")) (empty-sosofo))
   ((equal? (normalize gi) (normalize "part")) (empty-sosofo))
   ((equal? (normalize gi) (normalize "toc")) (empty-sosofo))
   (else ($shadowman-header$))))

(define (first-page-center-footer gi)
  (empty-sosofo))

(define (first-page-outer-footer gi)
  (empty-sosofo))

;;======================================
;;Page Number Format
;;======================================

(define ($page-number-format$ #!optional (gi (gi)))
  (cond
   ((equal? (normalize gi) (normalize "toc")) "i")
   ((equal? (normalize gi) (normalize "lot")) "i")
   ((equal? (normalize gi) (normalize "preface")) "i")
   (else "1")))

;;======================================
;;Tables
;;======================================

;;Add space between column line and text
(define %cals-cell-before-column-margin% 3pt)

;;======================================
;;PDF Bookmarks
;;======================================

;;(declare-characteristic heading-level
;;   "UNREGISTERED::James Clark//Characteristic::heading-level" 2)

;;======================================
;;Variable Lists
;;======================================
;;fix indented variablelists until
;;bug #469318 is fixed

(element (varlistentry term)
    (make paragraph
          space-before: (if (first-sibling?)
                            %block-sep%
                            0pt)
          keep-with-next?: #t
          first-line-start-indent: 0pt
          (process-children))) 

;;======================================
;;Functions
;;======================================

(define (OLSTEP)
  (case
   (modulo (length (hierarchical-number-recursive "ORDEREDLIST")) 4)
	((1) 1.2em)
	((2) 1.2em)
	((3) 1.6em)
	((0) 1.4em)))

(define (ILSTEP) 1.0em)

(define (PROCSTEP ilvl)
  (if (> ilvl 1) 1.8em 1.4em))

(define (PROCWID ilvl)
  (if (> ilvl 1) 1.8em 1.4em))


(define ($comptitle$)
  (make paragraph
	font-family-name: %title-font-family%
	font-weight: 'bold
	font-size: (HSIZE 2)
	line-spacing: (* (HSIZE 2) %line-spacing-factor%)
	space-before: (* (HSIZE 2) %head-before-factor%)
	space-after: (* (HSIZE 2) %head-after-factor%)
	start-indent: 0pt
	first-line-start-indent: 0pt
	quadding: 'start
	keep-with-next?: #t
	(process-children-trim)))

;;Callouts are confusing in Postscript... fix them.
(define %callout-fancy-bug% 
 #f)


;;By default perils are centered and dropped into a box with a really
;;big border - I have simply decreased the border thickness -
;;unfortunately it takes all this to do it - sigh.
(define ($peril$)
  (let* ((title     (select-elements 
		     (children (current-node)) (normalize "title")))
	 (has-title (not (node-list-empty? title)))
	 (adm-title (if has-title 
			(make sequence
			  (with-mode title-sosofo-mode
			    (process-node-list (node-list-first title))))
			(literal
			 (gentext-element-name 
			  (current-node)))))
	 (hs (HSIZE 2)))
  (if %admon-graphics%
      ($graphical-admonition$)
      (make display-group
	space-before: %block-sep%
	space-after: %block-sep%
	font-family-name: %admon-font-family%
	font-size: (- %bf-size% 1pt)
	font-weight: 'medium
	font-posture: 'upright
	line-spacing: (* (- %bf-size% 1pt) %line-spacing-factor%)
	(make box
	  display?: #t
	  box-type: 'border
	  line-thickness: .5pt
	  start-indent: (+ (inherited-start-indent) (* 2 (ILSTEP)) 2pt)
	  end-indent: (inherited-end-indent)
	  (make paragraph
	    space-before: %para-sep%
	    space-after: %para-sep%
	    start-indent: 1em
	    end-indent: 1em
	    font-family-name: %title-font-family%
	    font-weight: 'bold
	    font-size: hs
	    line-spacing: (* hs %line-spacing-factor%)
	    quadding: 'center
	    keep-with-next?: #t
	    adm-title)
	  (process-children))))))


;;======================================
;;Non-printing Elements
;;======================================
(element TITLEABBREV (empty-sosofo))
(element SUBTITLE (empty-sosofo))
(element SETINFO (empty-sosofo))
(element BOOKINFO (empty-sosofo))
(element BIBLIOENTRY (empty-sosofo))
(element BIBLIOMISC (empty-sosofo))
(element BOOKBIBLIO (empty-sosofo))
(element SERIESINFO (empty-sosofo))
(element DOCINFO (empty-sosofo))
(element ARTHEADER (empty-sosofo))
(element ADDRESS (empty-sosofo))

;;Show comment element?
(define %show-comments%
  #t)

;;Redefine comment for LSB
(element comment
  (if %show-comments%
      (make paragraph
	start-indent: 0pt
	first-line-start-indent: -10pt
	font-posture: 'italic
	font-size: (* (inherited-font-size) 0.9)
	(make sequence
	  (make line-field 
	    field-width: 10pt
	    quadding: 'center
	    (literal "BEGIN RATIONALE:   "))
	  (process-children))
	(literal "END RATIONALE:   "))
      (empty-sosofo)))

;; In DocBook V4.0 comment became remark
(element remark
  (if %show-comments%
      (make paragraph
	start-indent: 0pt
	first-line-start-indent: -10pt
	font-posture: 'italic
	font-size: (* (inherited-font-size) 0.9)
	(make sequence
	  (make line-field 
	    field-width: 10pt
	    quadding: 'center
	    (literal "BEGIN RATIONALE:   "))
	  (process-children))
	(literal "END RATIONALE:   "))
      (empty-sosofo)))

;;======================================
;;Inlines
;;======================================

;;Define Red Hat element attributes
(element application ($bold-seq$))
(element command ($mono-seq$))
(element filename ($mono-seq$))
(element function ($mono-seq$))
(element guibutton ($bold-seq$))
(element guiicon ($bold-seq$))
(element guilabel ($bold-seq$))
(element guimenu ($bold-seq$))
(element guimenuitem ($bold-seq$))
(element hardware ($bold-seq$))
(element keycap 
   (make sequence
    (literal "[")
    ($charseq$)
    (literal "]")))
(element literal ($mono-seq$))
(element parameter ($italic-mono-seq$))
(element prompt ($mono-seq$))
(element symbol ($charseq$))
(element emphasis ($italic-seq$))

</style-specification-body>
</style-specification>


<!-- 
;;===========================================================================
;;                                HTML
;;===========================================================================
-->

<style-specification id="html" use="docbook">
<style-specification-body>

;; this is necessary because right now jadetex does not understand
;; symbolic entities, whereas things work well with numeric entities.
(declare-characteristic preserve-sdata?
          "UNREGISTERED::James Clark//Characteristic::preserve-sdata?"
          #f)

;;=========================
;;Header HTML 4.0.1
;;=========================

(define %html-pubid% "-//W3C//DTD HTML 4.01 Transitional//EN")


;;=========================
;;General Items
;;=========================

;;Should there be a link to the legalnotice?
(define %generate-legalnotice-link%
  #t)

;;Should Admon Graphics be used?
(define %admon-graphics%
  #t)

;;Where are those admon graphics?
(define %admon-graphics-path%
  "./stylesheet-images/")

;;define which images to use
(define ($admon-graphic$ #!optional (nd (current-node)))
  (cond ((equal? (gi nd) (normalize "tip"))
     (string-append %admon-graphics-path% "tip.png"))
    ((equal? (gi nd) (normalize "note"))
     (string-append %admon-graphics-path% "note.png"))
    ((equal? (gi nd) (normalize "important"))
     (string-append %admon-graphics-path% "important.png"))
    ((equal? (gi nd) (normalize "caution"))
     (string-append %admon-graphics-path% "caution.png"))
    ((equal? (gi nd) (normalize "warning"))
     (string-append %admon-graphics-path% "warning.png"))
    (else (error (string-append (gi nd) " is not an admonition.")))))

;;Given an admonition node, returns 
;;the width of the graphic that will
;;be used for that admonition.
(define ($admon-graphic-width$ #!optional (nd (current-node)))
  "25")

;;What graphics extensions allowed?
(define %graphic-extensions% 
'("png" "gif" "jpg" "jpeg" "tif" "tiff" "eps" "epsf" ))

;;What is the default extension for images?
(define %graphic-default-extension% "png")

;;Use element ids as filenames?
(define %use-id-as-filename%
 #t)

;;Make titles of formal objects appear 
;;after the objects defined in the list
(define ($object-titles-after$)
  (list (normalize "figure")))

;;=========================
;;HTML Attributes
;;=========================

;;What attributes should be hung off of 'body'?
(define %body-attr%
 (list
   (list "BGCOLOR" "#FFFFFF")
   (list "TEXT" "#000000")
   (list "LINK" "#0000FF")
   (list "VLINK" "#840084")
   (list "ALINK" "#0000FF")))

;;Default extension for filenames?
(define %html-ext% 
  ".html")

;;=========================
;;HTML Attributes
;;=========================
;; From Tim Waugh
;; Use id for HTML filename

(define ($legalnotice-link-file$ legalnotice)
  (if (and %use-id-as-filename%
           (attribute-string (normalize "id") legalnotice))
      (string-append (case-fold-down (attribute-string (normalize "id")
                                                       legalnotice))
                     %html-ext%)
      (string-append "ln"
                     (number->string (all-element-number legalnotice))
                     %html-ext%)))

;;=========================
;;Book Stuff
;;=========================

;;Do you want a TOC for Books?
(define %generate-book-toc% 
  #f)

;;What depth does the TOC reach? 
;; This depends on the elements used 
;; and which is the highest level.
;; part(1), chapter(2), sect(3) -
;; chapter(1), sect1(2), sect2(3) - etc
(define (toc-depth nd)
  (if (string=? (gi nd) (normalize "book"))
      3 ;this is the important one!
      1))

;;What elements should have an LOT? 
(define ($generate-book-lot-list$)
  (list (normalize "equation")))

;;Do you want a title page for your Book?
(define %generate-book-titlepage%
#t)

;;=========================
;;Part Stuff
;;=========================

;;Should parts have TOCs?
(define %generate-part-toc% 
  #f)

;;Should part TOCs be on their titlepages?
(define %generate-part-toc-on-titlepage%
  #f)

;;Do you want a title page for your parts?
(define %generate-part-titlepage% 
  #t)

;;Should the Part intro be on the part title page?
(define %generate-partintro-on-titlepage%
 #t)

(define %para-autolabel%
 #t)

;;=========================
;;Article Stuff
;;=========================

;;Should Articles have a TOC?
(define %generate-article-toc% 
  #f)

;;========================
;;Title Pages for Articles
;;========================

;;This allows you to define what elements you 
;;wish to have on the article titlepage. 
;;Any element allowed in artheader is available.

(define (article-titlepage-recto-elements)
  (list (normalize "title")
	(normalize "subtitle")
        (normalize "authorgroup")
	(normalize "author")
	(normalize "releaseinfo")
        (normalize "pubdate")
	(normalize "revhistory")
        (normalize "affiliation")
        (normalize "copyright")
	(normalize "legalnotice")
	(normalize "abstract")
	))

;;Redefine Titlepage Separator on Articles
(define (article-titlepage-separator side)
      (empty-sosofo))

(mode article-titlepage-recto-mode

  (element abstract
    (make element gi: "DIV"
	  ($dcm-abstract-object$)))
  
  (element (abstract title) (empty-sosofo))

;;Author name is too big - change it!
(element author
    (let ((author-name  (author-string))
      (author-affil (select-elements (children (current-node)) 
                     (normalize "affiliation"))))
      (make sequence      
    (make element gi: "H4"
attributes: (list (list "CLASS" (gi)))
          (make element gi: "A"
attributes: (list (list "NAME" (element-id)))
            (literal author-name)))
    (process-node-list author-affil))))


) <!-- end of article mode -->

;;Stole this from Dave's stylesheet so the
;;above puts the abstract in a pretty gray box
(define ($dcm-abstract-object$)
  (make element gi: "TABLE"
	attributes: '(("BORDER" "0")
                      ("BGCOLOR" "#E0E0E0")
                      ("WIDTH" "50%")
                      ("CELLSPACING" "0")
                      ("CELLPADDING" "0")
                      ("ALIGN" "CENTER"))
        (make element gi: "TR"
	      (make element gi: "TD"
		    attributes: '(("VALIGN" "TOP"))
                    (make element gi: "B"
			  (literal "Abstract"))))
        (make element gi: "TR"
	      (make element gi: "TD"
		    attributes: '(("VALIGN" "TOP"))
		    (process-children)))))



;;========================
;;Chapter Stuff
;;=======================

;;Should Chapters have a small TOC?
(define $generate-chapter-toc$
 (lambda ()
    #f))

;;========================
;;Nochunks Stuff
;;========================

;;Don't use HR tags in release notes

(define ($component-separator$) 
  (if (or (not nochunks) (node-list=? (current-node) (sgml-root-element)))
      (empty-sosofo)
      (make empty-element gi: "")))

(define ($section-separator$) 
  (let* (;; There are several situations in which we don't want a
	 ;; separator here:
	 ;; 1. This document is being chunked:
	 (chunks (not nochunks))
	 ;; 2. This node is the root element of the document:
	 (isroot (node-list=? (current-node) (sgml-root-element)))
	 ;; 3. This node is the first section in the root element
	 ;;    and no other content (except the *info elements and
	 ;;    the title) precedes it.  This means that the
	 ;;    titlepage-separator was the last thing we put out.
	 ;;    No one expects two separators in a row, or the Spanish
	 ;;    inquisition.
	 (s1ofrt  (node-list=? (parent (current-node)) (sgml-root-element)))
	 (precnd  (ipreced (current-node)))
	 (infond  (info-element (parent (current-node))))
	 (isfirst (or (equal? (gi precnd) (normalize "title"))
		      (node-list=? precnd infond))))
    (if (or chunks isroot isfirst)
	(empty-sosofo)
	(make empty-element gi: ""))))

;;=========================
;;Navigation
;;=========================

;;Should there be navigation at top?
(define %header-navigation%
 #t)

;;Should there be navigation at bottom?
(define %footer-navigation%
  #t)

;;Use tables to create the navigation? 
;;Please make #t if you have any taste ;)
(define %gentext-nav-use-tables%
 #t)

;;If tables are used for navigation, how wide should they be? 
(define %gentext-nav-tblwidth% 
"100%")


;;=========================
;;Tables and Lists
;;=========================

;;Should Variable lists be tables?
(define %always-format-variablelist-as-table%
 #f)

;;What is the length of the 'Term' in a variablelist?
(define %default-variablelist-termlength%
  20)

;;When true::If the terms are shorter than the termlength above then
;;the variablelist will be formatted as a table.
(define %may-format-variablelist-as-table%
#f)

;; This is a personal preference of how tables should look. 
;; It also handles the tables better for use in Netscape
;; It changes the table background color, cell spacing and cell padding.
(element tgroup
  (let* ((wrapper   (parent (current-node)))
	 (frameattr (attribute-string (normalize "frame") wrapper))
	 (pgwide    (attribute-string (normalize "pgwide") wrapper))
	 (footnotes (select-elements (descendants (current-node)) 
				     (normalize "footnote")))
	 (border (if (equal? frameattr (normalize "none"))
		     '(("BORDER" "0"))
		     '(("BORDER" "1"))))
	 (bgcolor '(("BGCOLOR" "#E0E0E0")))
	 (width (if (equal? pgwide "1")
		    (list (list "WIDTH" ($table-width$)))
		    '()))
	 (head (select-elements (children (current-node)) (normalize "thead")))
	 (body (select-elements (children (current-node)) (normalize "tbody")))
	 (feet (select-elements (children (current-node)) (normalize "tfoot"))))
    (make element gi: "TABLE"
	  attributes: (append
		       border
		       width
		       bgcolor
		       '(("CELLSPACING" "0"))
		       '(("CELLPADDING" "4"))
		       (if %cals-table-class%
			   (list (list "CLASS" %cals-table-class%))
			   '()))
	  (process-node-list head)
	  (process-node-list body)
	  (process-node-list feet)
	  (make-table-endnotes))))



;;=========================
;;Elements
;;=========================

;;Indent Literal layouts?
(define %indent-literallayout-lines% 
  #f)

;;Indent Programlistings?
(define %indent-programlisting-lines%
  #f)

;;Number lines in Programlistings?
(define %number-programlisting-lines%
 #f)

;;Should verbatim items be 'shaded' with a table?
(define %shade-verbatim% 
 #t)

;;Define shade-verbatim attributes
(define ($shade-verbatim-attr$)
 (list
  (list "BORDER" "0")
  (list "BGCOLOR" "#E0E0E0")
  (list "WIDTH" ($table-width$))))


;;=================INLINES====================


;;Define your own series of fonts 
;;and font formatting for various elements
(element application ($bold-seq$))
(element command ($mono-seq$))
(element filename ($mono-seq$))
(element function ($mono-seq$))
(element guibutton ($bold-seq$))
(element guiicon ($bold-seq$))
(element guilabel ($bold-seq$))
(element guimenu ($bold-seq$))
(element guimenuitem ($bold-seq$))
(element hardware ($bold-seq$))
(element keycap 
  ($charseq$
   (make element  
    (literal "[")
    (process-children)
    (literal "]"))))
(element literal ($mono-seq$))
(element parameter ($italic-mono-seq$))
(element prompt ($mono-seq$))
(element symbol ($charseq$))
(element emphasis ($italic-seq$))

;;Netscape doesn't handle trademark entity; at all!!
;;Make it a superscript "TM"
(element trademark
  (make sequence
    (process-children)
    (make element gi: "sup"
    (literal "TM"))))

;=============================================

;;========================
;;Title Pages for Books
;;=======================

;;This allows you to define what elements you 
;;wish to have on the book titlepage. 
;;Any element allowed in bookinfo is available.

(define (book-titlepage-recto-elements)
  (list (normalize "title")
	(normalize "subtitle")
        (normalize "copyright")
;	(normalize "mediaobject")
        (normalize "legalnotice")
	(normalize "corpauthor")
	(normalize "authorgroup")
	(normalize "author")
	(normalize "orgname")
;	(normalize "publisher")
	(normalize "isbn")))

(mode book-titlepage-recto-mode

;;Prepend ISBN to ISBN number
;;tfox@redhat.com
(element isbn
  ($charseq$
   (make sequence
    (make element
	  attributes: (list
		       (list "CLASS" (gi)))
	  (literal "ISBN: ")
	  (process-children)))))

;;Make publishername bold
;;tfox@redhat.com

(element publishername
  ($bold-seq$
  (make sequence
    (make element
	  attributes: (list
		       (list "CLASS" (gi)))
	  (process-children)))))

;;Format address on titlepage
;;tfox@redhat.com

(element (publisher address)
    (let (       

         (street     (select-elements (children (current-node)) (normalize "STREET")))
         (city       (select-elements (children (current-node)) (normalize"CITY")))
         (state      (select-elements (children (current-node)) (normalize "STATE")))
         (postcode   (select-elements (children (current-node)) (normalize "POSTCODE")))
         (country    (select-elements (children (current-node)) (normalize "COUNTRY")))
         (phone      (select-elements (children (current-node)) (normalize "PHONE")))
         (fax        (select-elements (children (current-node)) (normalize "FAX")))
         (email      (select-elements (children (current-node)) (normalize "EMAIL")))
         (pob        (select-elements (children (current-node)) (normalize "POB")))
         (otheraddr  (select-elements (children (current-node)) (normalize "OTHERADDR")))
         )

      (make sequence
	(make paragraph
	  (if(node-list-empty? street)
	     (empty-sosofo)
	     (make element gi: "BR"
		   (process-node-list street)))
	  
	  (if(node-list-empty? pob)
	     (empty-sosofo)
	     (make element gi: "BR"
		   (literal "PO Box  ")
		   (process-node-list pob)))
	  
	  (make paragraph
	    (if(node-list-empty? city)
	       (empty-sosofo)
	       (make sequence
		 (process-node-list city)
		 (literal ", ")))
	    
	    (if(node-list-empty? state)
	       (empty-sosofo)
	       (make sequence
		 (process-node-list state)
		 (literal " ")))
	    
	    (make sequence
	      (process-node-list postcode)))
	  
	  (make element gi: "BR"
		(process-node-list country))
	  
	  (if(node-list-empty? phone)
	     (empty-sosofo)
	     (make paragraph
	       (literal "Phone: ")
	       (process-node-list phone)))
	  
	  (if(node-list-empty? otheraddr)
	     (empty-sosofo)
	     (make element gi: "BR"
		   (literal "Phone: ")
		   (process-node-list otheraddr)))
	  
	  (make paragraph
	    (if(node-list-empty? fax)
	       (empty-sosofo)
	       (make sequence
		 (literal "Fax: ")
		 (process-node-list fax))))
	  
	  (if(node-list-empty? email)
	     (empty-sosofo)
	     (make element gi: "BR"
		   (literal "Email: ")
		   (process-node-list email))) 
	  ))))

) <!--end of mode-->

;;============================
;;Title for Formal Paragraphs
;;============================

;;The way formal paragraphs are displayed by default 
;;is ugly! This makes it a much more useful element!

(element formalpara
  (make element gi: "DIV"
	attributes: (list
		     (list "CLASS" (gi)))
  	(make element gi: "P"
	      (process-children))))

;(element (formalpara title) ($lowtitle$ 5))
(element (formalpara title) 
  (make element gi: "B"
	($runinhead$)))

;;====================
;; General Formatting
;;====================

;; Handle qanda labelling with Q: A:
(define (qanda-defaultlabel)
  (normalize "qanda"))

;;From FreeBSD Sheets (Thanks!) Display Q and A in bigger bolder fonts

(element question
  (let* ((chlist   (children (current-node)))
	 (firstch  (node-list-first chlist))
	 (restch   (node-list-rest chlist)))
    (make element gi: "DIV"
	  attributes: (list (list "CLASS" (gi)))
	  (make element gi: "P" 
		(make element gi: "BIG"
		      (make element gi: "A"
			    attributes: (list
					 (list "NAME" (element-id)))
			    (empty-sosofo))
		      (make element gi: "B"
			    (literal (question-answer-label
				      (current-node)) " ")
			    (process-node-list (children firstch)))))
	  (process-node-list restch))))

</style-specification-body>
</style-specification>
<external-specification id="docbook" document="docbook.dsl">
</style-sheet>
