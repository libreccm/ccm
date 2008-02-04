;; from common/dbl1*.dsl
;; %p is replaced by the number of the page on which target occurs
;; %g is replaced by the (gentext-element-name)
;; %n is replaced by the label
;; %t is replaced by the title

(define (en-xref-strings)
  (list (list (normalize "appendix")    (if %chapter-autolabel%
					    "&Appendix; %n %t"
					    "the &appendix; called %t"))
	(list (normalize "article")     (string-append %gentext-en-start-quote%
						       "%t"
						       %gentext-en-end-quote%))
	(list (normalize "bibliography") "%t")
	(list (normalize "book")        "%t")
	(list (normalize "chapter")     (if %chapter-autolabel%
					    "&Chapter; %n %t"
					    "the &chapter; called %t"))
	(list (normalize "equation")    "&Equation; %n")
	(list (normalize "example")     "&Example; %n")
	(list (normalize "figure")      "&Figure; %n")
	(list (normalize "glossary")    "%t")
	(list (normalize "index")       "%t")
	(list (normalize "listitem")    "%n")
	(list (normalize "part")        "&Part; %n %t")
	(list (normalize "preface")     "%t")
	(list (normalize "procedure")   "&Procedure; %n, %t")
	(list (normalize "reference")   "&Reference; %n, %t")
	(list (normalize "section")     (if %section-autolabel%
					    "&Section; %n %t"
					    "the &section; called %t"))
	(list (normalize "sect1")       (if %section-autolabel%
					    "&Section; %n %t"
					    "the &section; called %t"))
	(list (normalize "sect2")       (if %section-autolabel%
					    "&Section; %n %t"
					    "the &section; called %t"))
	(list (normalize "sect3")       (if %section-autolabel%
					    "&Section; %n %t"
					    "the &section; called %t"))
	(list (normalize "sect4")       (if %section-autolabel%
					    "&Section; %n %t"
					    "the &section; called %t"))
	(list (normalize "sect5")       (if %section-autolabel%
					    "&Section; %n %t"
					    "the &section; called %t"))
	(list (normalize "simplesect")  (if %section-autolabel%
					    "&Section; %n %t"
					    "the &section; called %t"))
	(list (normalize "sidebar")     "the &sidebar; %t")
	(list (normalize "step")        "&step; %n")
	(list (normalize "table")       "&Table; %n")))

;;redefined so outer parent of part is not included in xref to part
;;part is in cont-divn and division-element-list
;;from common/dbcommon.dsl

(define (auto-xref-indirect? target ancestor)
  ;; This function answers the question: should an indirect reference
  ;; to ancestor be made for target?  For example:
  ;;
  ;; (auto-xref-indirect? SECT1 CHAP)
  ;;
  ;; should return #t iff a reference of the form "in [CHAP-xref]" should
  ;; be generated for a reference to SECT1 if SECT1 is in a different
  ;; chapter than the XREF to SECT1.
  ;;
  ;; This function _does not_ have to consider the case of whether or
  ;; not target and the xref are in the same ancestor.
  ;;
  (cond
   ;; this is what I changed!
   ;; changed from Always to Never to prevent book titles in part xrefs
   ;; Nevers add indirect references to another book
   ((member (gi ancestor) (book-element-list))
    #f)
   ;; Add indirect references to the section or component a block
   ;; is in iff chapters aren't autolabelled.  (Otherwise "Figure 1-3"
   ;; is sufficient)
   ((and (member (gi target) (block-element-list))
         (not %chapter-autolabel%))
    #t)
   ;; Add indirect references to the component a section is in if
   ;; the sections are not autolabelled
   ((and (member (gi target) (section-element-list))
         (member (gi ancestor) (component-element-list))
         (not %section-autolabel%))
    #t)
   (else #f)))

