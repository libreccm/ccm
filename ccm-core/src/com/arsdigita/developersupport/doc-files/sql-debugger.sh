#!/bin/bash

thisdir=`dirname $0`
stylesheet=$thisdir/sql-debugger.xsl
if [ ! -f "$stylesheet" ]; then
    echo "$stylesheet does not exist."
    exit 1
fi

index_file=/tmp/sql-debugger.html

echo > $index_file <<EOF
<html>
<head>
  <title>Debug files</title>
</head>
<body>
<ul>
EOF

for ff in `ls -t /tmp/*.xml | sed 's/\.xml$//'`; do
    xml_file="${ff}.xml"
    html_file="${ff}.html"
    xsltproc $stylesheet "$xml_file" > "$html_file"
    echo "  <li><a href=\"$html_file\">$html_file</a></li>" >> $index_file
done

tstamp=`date`

cat >> $index_file <<EOF
</ul>

<p align="right"><em>$tstamp</em></p>
</body>
</html>
EOF

echo "Output:"
echo $index_file
