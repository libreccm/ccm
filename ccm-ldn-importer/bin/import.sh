#!/bin/sh

if [ "x$3" = "x" ]; then
  echo "syntax: import.sh [index file] [item dir] [asset dir]"
  exit 1
fi

exec ccm-run com.arsdigita.london.importer.cms.ItemImportTool $@
