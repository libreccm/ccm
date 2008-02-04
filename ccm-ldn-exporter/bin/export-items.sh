#!/bin/sh

if [ -z "$3" ]; then
  echo "syntax: export-items.sh [item dir] [asset dir] [content section]"
  exit 1;
fi

set -e

ccm-run com.arsdigita.london.exporter.ItemExportTool "$@"
