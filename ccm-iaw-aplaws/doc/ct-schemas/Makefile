
SERVER = http://dev.london.redhat.com:9068/

types = Address \
        Agenda \
	Article \
	Event \
	FileStorageItem \
	Job \
	LegalNotice \
	Minutes \
	MultiPartArticle \
	ArticleSection \
	NewsItem \
	PressRelease \
	Service \
	FAQItem \
	GlossaryItem \
	MOTDItem \
    Organization
    

schemas = $(types:%=%.xsd)

all: ct-schemas.html

ct-schemas.html: ct-schemas.htmc $(schemas)
	htmltoc.pl $< > $@

%.xsd: 
	wget -O $@.raw '$(SERVER)aplaws/schema.jsp?type=com.arsdigita.cms.contenttypes.$(@:%.xsd=%)&context=com.arsdigita.cms.dispatcher.SimpleXMLGenerator&wrapAttr=true&wrapRoot=false'
	xmllint -format $@.raw > $@
	rm $@.raw

clean:
	rm -f $(schemas) ct-schemas.html *.bak *~
