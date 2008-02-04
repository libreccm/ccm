#!/usr/bin/perl -w
#
# Extract error reports
# 
# syntax: cat acs.log | extract-reports.pl
#
#         cat acs.log | extract-reports.pl <error code>
#
# Written by Daniel P. Berrange <berrange@redhat.com>

use strict;

my $report;

if (@ARGV) {
  $report = shift @ARGV;
} 

my @report;
my $inrep = 0;
my $code = undef;

while (<STDIN>) {
  if (!$inrep) {
    if (/Begin Error Report/) {
      $inrep = 1;
    }
  } else {
#    print $_;
    if (/End Error Report/) {
      die "no error code" unless defined $code;
    
      if (defined $report) {
        if ($code eq $report) {
	  foreach my $line (@report) {
	    print $line;
	  }
	}
      } else {
        open FILE, ">$code" 
          or die "cannot create file $code";
      
        foreach my $line (@report) {
          print FILE  $line;
        }
        close FILE;
      }
      
      @report = ();
      $code = undef;      
      $inrep = 0;
    } elsif (/ACS Error Report Code:\s*(.*?)\s/) {
      $code = $1;
      push @report, $_;
    } else {
      if (/ACS Error Code/) {
        print "Feck $_";
      }
      push @report, $_;
    }
  }
}
