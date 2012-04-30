#!/usr/bin/perl -w


while (<STDIN>) {
    next if /^\s*#/;
    next if /^\s*$/;

    if (/^(.+),(.+),(.+)$/) {
        my $src = $1;
        my $dst = $2;
        my $xsl = $3;
        
        if (-x "/usr/bin/xsltproc") {
            print "wget -q -O - $src | /usr/bin/xsltproc $xsl - | xmllint -format - > $dst\n";
            (system "wget -q -O - $src | /usr/bin/xsltproc $xsl - | xmllint -format - > $dst") == 0
                or die "cannot run xsltproc: $@";
        } else {
	    #FR: not working for me
            #print "wget -q -O - $src | ccm-run com.arsdigita.london.terms.util.ApplyTemplates $xsl - - | xmllint -format - > $dst\n";
            #(system "wget -q -O - $src | ccm-run com.arsdigita.london.terms.util.ApplyTemplates $xsl - - | - > $dst") == 0
            #    or die "cannot can ApplyTemplates: $@";
            print "Processing $src \n";
            (system "wget -q -O - $src > src.xml; ccm-run com.arsdigita.london.terms.util.ApplyTemplates $xsl src.xml $dst; rm src.xml") == 0
	        or die "cannot can ApplyTemplates: $@";
        }
    }
}
