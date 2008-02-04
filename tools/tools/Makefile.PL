# $Id: Makefile.PL 288 2005-02-22 00:55:45Z sskracic $
# -*- perl -*-

use ExtUtils::MakeMaker;

# The following pre-requisite checking code is borrowed from
# Net::FTPServer

# PREREQ_PM is crap! It doesn't enforce the prerequisites, and
# doesn't print an intelligible error message. Nor does it understand
# that some modules are optional, while others are absolutely
# required. Do our own prerequisite checking here. A lot of this
# code was borrowed from the Makefile.PL supplied with libwww-perl.
#
# NB. 'status' field is either 'required', for modules which are
# required, or anything else for modules which are optional.

my %modules  = (
                'File::Copy' => { status => "required" },
                'File::Find' => { status => "required" },
                'File::Path' => { status => "required" },
                'Carp' => { status => "required" },
                'POSIX' => { status => "required" },
                );

$| = 1;

# Check for modules.

my $missing_modules = 0;
my $missing_required_modules = 0;

foreach (sort keys %modules) {
    print "Checking for $modules{$_}{status} ";
    print "module $_ ";
    print ">= $modules{$_}{version} " if $modules{$_}{version};
    print "... ";

    my $eval = "require $_; ";
    $eval .= "$_->VERSION >= $modules{$_}{version}" if $modules{$_}{version};

    my $r = eval $eval;
    if ($@ || !$r) {
        $missing_modules++;
        $missing_required_modules++ if $modules{$_}{status} eq "required";

        print "not found.\n\n";

        if (exists $modules{$_}{package}) {
            print
                "This module is provided by the $modules{$_}{package} ",
                "package.\n\n";
        }

        if (exists $modules{$_}{message}) {
            print "*** ", $modules{$_}{message}, "\n";
        }
    } else {
        print "ok.\n";
    }
}
print "\n";

if ($missing_modules) {
    print "Obtain missing modules from CPAN [http://www.cpan.org/].\n";
}

if ($missing_required_modules) {
    print "Required modules are missing. Install these first.\n";
    exit 1;
}

# End of pre-requisite checking code

# The code has only been verified on versions of Perl >= than
# the following.
if ($] < 5.6.0)
  {
    print <<EOT;
Perl version >= 5.6.0 is required.

EOT
    exit 1;
  }


WriteMakefile(
              MAKEFILE => "Makefile.perl",
              NAME => "CCM",
              dist => {
                  COMPRESS => 'gzip --force --best',
              },
              clean => {
                  FILES => '*.bak *~',
              },
              );

__END__
