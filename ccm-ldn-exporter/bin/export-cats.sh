#!/bin/bash

if [ -z "$7" ]; then
  echo "syntax: export-cats.sh [app URL] [context] [export dir] [key] [url] [title] [version]"
  echo " e.g. export-cats.sh /navigation/ DEFAULT export APLAWS-NAV http://www.aplaws.org.uk/standards/nav/1.02/termslist.xml 'APLAWS Navigation List' 1.02"

  exit 1;
fi

set -e

ccm-run com.arsdigita.london.exporter.CategoryExportTool "$@"

