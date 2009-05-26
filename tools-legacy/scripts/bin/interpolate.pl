#!/usr/bin/perl -wT

use strict;

if ($#ARGV < 0) {
  print "syntax: interpolate.pl key1=val2 key2=val2 ...\n";
  exit -1;
}

my %keys;

foreach (@ARGV) {
  if (/^(\w+)=(.*)$/) {
    $keys{$1} = eval "qq{$2}";
  } else {
    print "cannot parse key=val pair $_\n";
    exit -1;
  }
}

while (<STDIN>) {
  s/::(\w+)::/exists $keys{$1} ? $keys{$1} : die "no value supplied for variable $1"/gex;
  print;
}

exit 0;
