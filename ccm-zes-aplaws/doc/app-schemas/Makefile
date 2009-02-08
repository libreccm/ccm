
schemas = $(wildcard *.xsd)

all: app-schemas.html

app-schemas.html: app-schemas.htmc $(schemas) $(xmldocs)
	htmltoc.pl $< > $@

clean:
	rm -f app-schemas.html *.bak *~
