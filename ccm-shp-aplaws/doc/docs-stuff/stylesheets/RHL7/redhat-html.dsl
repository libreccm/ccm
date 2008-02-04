<!DOCTYPE style-sheet PUBLIC "-//James Clark//DTD DSSSL Style Sheet//EN" [
<!ENTITY dbstyle SYSTEM "/usr/lib/sgml/stylesheets/nwalsh-modular/html/docbook.dsl" CDATA DSSSL>
]>
<style-sheet>
<style-specification id="html" use="docbook">
<style-specification-body>

;;#######################################################################
;;#                                                                     #
;;#        Custom DocBook Stylesheet for Red Hat Documentation          #
;;#                 by Dave Mason dcm@redhat.com                        #
;;#            Based on Norman Walsh's Modular Stylesheets              #
;;#                                                                     #
;;#                 Modified by Tammy Fox tfox@redhat.com               #
;;#                          November 2000                              #
;;#                                                                     #
;;#                            To use:                                  #
;;#    jade -t sgml -ihtml -d /path/to/redhat-html.dsl yoursgml.sgml    #
;;#                                                                     #
;;#######################################################################

(declare-characteristic preserve-sdata?
          "UNREGISTERED::James Clark//Characteristic::preserve-sdata?"
          #f)

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

;;What graphics extensions allowed?
(define %graphic-extensions% 
'("gif" "jpg" "jpeg" "tif" "tiff" "eps" "epsf" ))

;;What is the default extension for images?
(define %graphic-default-extension% "gif")

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
;;Book Stuff
;;=========================

;;Do you want a TOC for Books?
(define %generate-book-toc% 
  #t)

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
  #t)

;;Should part TOCs be on their titlepages?
(define %generate-part-toc-on-titlepage%
  #t)

;;Do you want a title page for your parts?
(define %generate-part-titlepage% 
  #t)

;;Should the Part intro be on the part title page?
(define %generate-partintro-on-titlepage%
 #t)

;;=========================
;;Article Stuff
;;=========================

;;Should Articles have a TOC?
(define %generate-article-toc% 
  #t)

;;========================
;;Chapter Stuff
;;=======================

;;Should Chapters have a small TOC?
(define $generate-chapter-toc$
 (lambda ()
    #f))

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
        (normalize "legalnotice")
	(normalize "corpauthor")
	(normalize "authorgroup")
	(normalize "author")
	(normalize "orgname")
	(normalize "publisher")
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
        (normalize "legalnotice")
	(normalize "releaseinfo")
        (normalize "pubdate")
	(normalize "revhistory")
        (normalize "affiliation")
        (normalize "copyright")))

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

</style-specification-body>
</style-specification>
<external-specification id="docbook" document="dbstyle">
</style-sheet>
