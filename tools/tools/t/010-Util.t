# $Id: 010-Util.t 711 2005-08-17 14:39:50Z apevec $

BEGIN { $| = 1; print "1..14\n"; }
END { print "not ok 1\n" unless $loaded; }

use CCM::Util;
use File::Find;
use File::Temp;

my $test_num = 1;

$loaded = 1;
print "ok 1\n";
$test_num++;

&testVersionSort(undef, undef);
&testVersionSort([""], [""]);
&testVersionSort(["1","2"], ["1","2"]);
&testVersionSort(["2","1"], ["1","2"]);
&testVersionSort(["2","1","3"], ["1","2","3"]);
&testVersionSort(["2","1","3","10"], ["1","2","3","10"]);
&testVersionSort(["1.10","1.2"], ["1.2","1.10"]);
&testVersionSort(["2","1","3","1.0"], ["1","1.0","2","3"]);
&testVersionSort(["2","1","3","1.0","1.1"], ["1","1.0","1.1","2","3"]);
&testVersionSort(["2","1","3","1.0","1.1", "2.2"], ["1","1.0","1.1","2","2.2","3"]);
&testVersionSort(["1.10.20","1.2.1"], ["1.2.1","1.10.20"]);
&testVersionSort(["1.10.20","1.20.1"], ["1.10.20","1.20.1"]);
&testVersionSort(["1.10.20","1.10.1"], ["1.10.1","1.10.20"]);

sub testVersionSort {
    my $input = shift;
    my $expected = shift;

    my @result = CCM::Util::versionSort(@$input);
    if ($#result eq $#{$expected}) {
        $good = 1;
        foreach (0..$#result) {
            if ($result[$_] ne $expected->[$_]) {
                $good = 0;
                last;
            }
        }
    } else {
        $good = 0;
    }

    if ($good) {
        #print ("in  '" . join (',', @$input) . "'\n");
        #print ("out '" . join (',', @result) . "'\n");
        #print ("exp '" . join (',', @$expected) . "'\n");
        print "ok $test_num\n";
    } else {
        print ("in  '" . join (',', @$input) . "'\n");
        print ("out '" . join (',', @result) . "'\n");
        print ("exp '" . join (',', @$expected) . "'\n");
        print "not ok $test_num\n";
    }
    $test_num++;
}


