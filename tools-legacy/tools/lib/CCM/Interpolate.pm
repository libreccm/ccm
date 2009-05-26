#
# CCM::Interpolate
#
# Copyright (C) 2003 Red Hat, Inc.
#
# $Id: Interpolate.pm 288 2005-02-22 00:55:45Z sskracic $

=pod

=head1 NAME

CCM:Interpolate

=head1 SYNOPSIS

=head1 DESCRIPTION

=head1 METHODS

=over 4

=cut

package CCM::Interpolate;

use strict;
use vars qw(@ISA @EXPORT_OK);
use Carp qw(confess);
require Exporter;

@ISA = qw(Exporter);
@EXPORT_OK = qw(interpolate_string interpolate_file);  # symbols to export on request

my $regexp = '[-\.\w\d]+';

sub interpolate_string {
    my %params = @_;
    confess "text parameter is required" unless exists $params{text};
    confess "vars parameter is required" unless exists $params{vars};

    $params{text} =~ s/::($regexp)::/&lookup($1, \%params)/gex;

    return $params{text};
}

sub interpolate_file {
    my %params = @_;
    confess "source parameter is required" unless exists $params{source};
    confess "destination parameter is required" unless exists $params{destination};
    confess "vars parameter is required" unless exists $params{vars};

    open SRC, "<$params{source}"
        or die "cannot read $params{source}: $!";
    open DST, ">$params{destination}"
        or die "cannot write $params{destination}: $!";

    while (<SRC>) {
        s/::($regexp)::/&lookup($1, \%params)/gex;
        print DST;
    }

    close SRC;
    close DST;
}

sub lookup {
    my $key = shift or confess "no key supplied";
    my $params = shift or confess "no parameters supplied";
    confess "no vars supplied" unless exists $params->{vars};

    my $vars = $params->{vars} || {};
    my $extravars = $params->{extravars} || {};
    my $method = $params->{method} || \&ccmGet;
    my $expand_vars = exists $params->{expand_vars} ? $params->{expand_vars} : 0;

    if (defined $extravars && exists $extravars->{$key}) {
        if ($expand_vars) {
            return eval "qq{$extravars->{$key}}";
        } else {
            return $extravars->{$key};
        }
    }

    if (defined $vars && exists $vars->{$key}) {
        if ($expand_vars) {
            return eval "qq{$vars->{$key}}";
        } else {
            return $vars->{$key};
        }
    }

    return ($extravars->{$key} = &$method($key));
}

sub ccmGet {
    my $key = shift;

    if ($key =~ /^($regexp)$/) {
        $key = $1;
    } else {
        die "key '$key' must only contain alphanumerics plus '.','_','-'";
    }

    $ENV{'PATH'} =~ /^(.*)$/;
    $ENV{'PATH'} = $1;

    my $command = "ccm get --value $key";
    my $output = `$command`;

    if (! defined ($output)) {
        die "error running '$command': $!\n";
    }

    chomp($output);

    return $output;
}

sub appendVars {
    my $file = shift;
    my $vars = shift;

    if ( open (IN, "< $file") ) {
        local $/;
        $/ = undef;

        my $file_contents = <IN>;

        while ( $file_contents =~ /([-\w]+)[\s]*=>[\s]*(.+?)[\s]*$/gm ) {
            $vars->{$1} = $2 unless exists $vars->{$1};
        }
        close IN;
    }
}

1 # So that the require or use succeeds.

__END__

=back 4

=head1 AUTHORS

=head1 COPYRIGHT

=head1 SEE ALSO

L<perl(1)>

=cut
