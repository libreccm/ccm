#!/usr/bin/python

## script to add css to RHL HTML pages
## Copyright (C) 2002 Red Hat, Inc.
## Copyright (C) 2002 Tammy Fox <tfox@redhat.com>

## Author: Tammy Fox

import sys
import string
import os
import re
import tempfile

#grab files specified on command line
html_files = sys.argv[1:]

for file in html_files:

    ## add header
    #open file
    f = open(file, "rw")

    #read in contents of file
    content =  f.read()
    #reg expression to find BODY tag
    replace_this = re.search('</HEAD', content, re.DOTALL | re.IGNORECASE)

    css_line="</HEAD><link rel=\"stylesheet\" type=\"text/css\" href=\"rhdocs-man.css\""

    path = os.getcwd()

    #open temp file
    try:
        tempfile.tempdir = path
        tmpfilename = tempfile.mktemp()
        tmpfile = open(tmpfilename, 'w')
        os.chmod(tmpfilename, 0664)
    except IOError:
        print "Error: Can't create temp file"
        sys.exit(0)

    try:
        new_content = re.sub(replace_this.group(), css_line, content)
    except:
        print "Error: Can't find content to replace in header in " + file
        tmpfile.close()
        os.remove(tmpfilename)
        sys.exit(0)

    tmpfile.write(new_content)

    #close files
    f.close()
    tmpfile.close()

    #mv temp file to real file
    os.rename(tmpfilename, file)

