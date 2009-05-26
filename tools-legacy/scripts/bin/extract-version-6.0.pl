#!/usr/bin/perl -w
#

use strict;
use File::Spec;

local $/ = undef;
my $file = File::Spec->catfile($ENV{'CCM_SCRIPTS_HOME'},'bin',"extract-version-5x.pl");
if (-f $file) {
    open (IN, $file) or die "could not open $file";
    my $contents = <IN>;
    close IN;
    eval $contents;
    exit;
} else {
    die "$file does not exist";
}
