package SpackleConf;

#
# $Id: SpackleConf.pm.in,v 1.8 2003/10/30 13:15:16 coar Exp $
#
# Copyright (c) 2002 by Ken A L Coar.  All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted according to the terms of the Apache
# licence.
#

#
# Perl module to access the configuration parameters for a spackled
# CVS repository.  Used by other elements of the spackle package.
#

use strict;
use vars qw($VERSION $REVISION $REVNUM @TRUEVALS @BOOLEAN_PARAMS);
use Carp;

$VERSION = '0.1b3-dev';
$REVISION = '$Id: SpackleConf.pm.in,v 1.8 2003/10/30 13:15:16 coar Exp $';
($REVNUM = $REVISION) =~ s/.*,v\s+(\d+\.\d+)\s+.*/$1/;

#
# Provide canonical names for the parameters we recognise, for
# prettier reporting.  (The same hash can be used for validity
# checking.)
#
my @canonical_configs = ('automerge',
                         'strict'
                        );
my %canonical_configs = map { lc($_) => $_ } @canonical_configs;

my @canonical_params = ('AllowCommitWithoutEmail',
                        'Enable_ACLs',
                        'Enable_Logging',
                        'Enable_passwd',
                        'Enable_readers',
                        'Enable_writers',
                        'IncludeRCSinfo',
                        'Karma_File',
                        'Maintainer',
                        'Maintainer_Email',
                        'PatchThreshold',
                        'Subject_Prefix'
                       );
my %canonical_params = map { lc($_) => $_ } @canonical_params;

#
# Some internal-use-only functions.  Public methods get PODded below.
#

#
# Normalise Boolean keywords into an integer value.
#
@TRUEVALS = (1, 'Yes', 'On', 'True');
my $TRUEVALS = join('|', @TRUEVALS);
$TRUEVALS = qr/^(?:$TRUEVALS)$/;

sub _boolean {
    my ($val) = @_;
    return ((! defined($val)) || ($val !~ /$TRUEVALS/i)) ? 0 : 1;
}

#
# Load the karma file.
#

#
# Do any group-name expansions on the list of names provided.  Time-bound,
# so only the memberships of groups as they exist at the time of the call
# are used.
#
sub _resolve_id {
    my $self = shift;
    my ($idlist) = @_;
    return '' if (! defined($idlist));
    my (@ids) = split(/\s*,\s*/, $idlist);
    my @results;
    if (@ids > 1) {
        for (@ids) {
            push(@results, $self->_resolve_id($_));
        }
    }
    else {
        if (substr($idlist, 0, 1) ne ':') {
            return $idlist;
        }
        else {
            my $gname = substr($idlist, 1);
            @results = @{$self->{_karma}->{_groups}->{$gname}};
        }
    }
    return @results;
}

#
# Load the mailmap file, which describes how messages should
# be distributed.
#
sub _load_mailmap {
    my $self = shift;
    if ($self->{'mailmap'}) {
        #
        # Already loaded..
        #
        return 1;
    }
    my $mapfile = $self->config('cvsroot') . '/mailmap';
    if (-r $mapfile) {
        my $opened = open(MAILMAP, "< $mapfile");
        if (! $opened) {
            carp("Can't open $mapfile: $!");
            return 0;
        }
        while (<MAILMAP>) {
            chomp;
            next if (/^\s*#/ or ($_ eq ''));
            my ($mapre, $flags, $eddress) = split(/\s*:\s*/, $_, 3);
            $mapre =~ s/^\s*(\S.*\S)\s*$/$1/;
            $eddress =~ s/^\s*(\S.*\S)\s*$/$1/;
            $flags =~ s/^\s*(\S.*\S)\s*$/$1/;
            push(@{$self->{'mailmap'}},
                 { 'pattern' => $mapre,
                   'flags'   => $flags,
                   'eddress' => $eddress });
        }
    }
    close(MAILMAP);
    return 1;
}

#
# Actually read the karma file and store it internally.
#

sub _load_karma {
    my $self = shift;

    my $kfile = $self->karma_file();
    open(KFILE, "< $kfile")
        || croak("Unable to access karma file '$kfile': $!");
    my $lnum = 0;
    while (my $line = <KFILE>) {
        $lnum++;
        chomp($line);
        next if (($line =~ /^\s*#/) || ($line =~ /^\s*$/));
        my $lkey = sprintf('%05d', $lnum);
        $self->{_karma}->{$lkey}->{_raw} = $line;
        $line =~ s/\s+//g;
        my ($kw, $who, $what) = split(m:\|:, $line);
        $self->{_karma}->{$lkey}->{_keyword} = $kw;
        $self->{_karma}->{$lkey}->{_who} = $who;
        $self->{_karma}->{$lkey}->{_what} = $what;
        $kw = lc($kw);
        if ($kw eq 'group') {
            if ($who !~ /^\w+$/) {
                carp("Invalid group name '$who' on line $lnum of $kfile");
            }
            else {
                $self->{_karma}->{_groups}->{$who} =
                    [$self->_resolve_id($what)];
            }
        }
        else {
            #
            # Use any group definitions as they exist at this point.
            #
            $self->{_karma}->{$lkey}->{_who} =
                join(',', $self->_resolve_id($who));
        }
    }
    close(KFILE);
}

=pod

=head1 NAME

SpackleConf - Access Spackle Configuration Parameters

=head1 SYNOPSIS

 use lib $ENV{'CVSROOT'} . '/CVSROOT';

 $sc = new SpackleConf;
 $sc->config('strict', 'yes');
 $curval = $sc->param('keyword');
 $origval = $sc->param('keyword', newval);
 $bool = $sc->is_member_of('username', 'groupname');
 $bool = $sc->module_matches('module', 'repository list');
 $sc->report_params(\*STDERR);
 $string = $sc->mail_to('modulename');
 $string = $sc->mail_subject('modulename');

=head1 DESCRIPTION

This module is part of the L<spackle(5)> package, and is
used by it to access configuration settings for the CVS
repository.  If changes are made to any parameters using
this package, they are I<not> saved.

This module is generally not for public consumption.

=head1 METHODS

=head2 new SpackleConf([$CVSROOT]);

Creates and returns a new configuration object using the settings
from the C<spackle.conf> file in the specified directory.  If the
directory is omitted, C<$ENV{'CVSROOT} . '/CVSROOT'> is assumed,
since this module is generally only used by scripts within
the repository CVSROOT itself.

If the configuration file cannot be read, a fatal error will
be thrown.

=cut

#
# Constructor; reads the Spackle configuration file.
#
sub new {
    my ($class, $cfgdir) = @_;
    my $cfgfile;
    my %params;
    my $self = {};

    $cfgdir = $ENV{'CVSROOT'} . '/CVSROOT'
        if (! $cfgdir);
    $cfgfile = "$cfgdir/spackle.conf";
    open(CONF, "< $cfgfile")
        || croak("Can't access configuration file '$cfgfile': $!");

    #
    # Read the entire file and skip comments later; that way we
    # know the line numbers for error reporting.
    #
    my @config = <CONF>;
    chomp(@config);
    my $module;
    my $mkey;

    for (my $i = 0; $i <= $#config; $i++) {
        my $line = $config[$i];
        next if (($line =~ /^\s*#/) || ($line =~ /^\s*$/));
        if ($line =~ /^\s*\[(.*)\]\s*(?:#.*)?$/) {
            #
            # Start of a [new] module-specific stanza, or possibly a
            # reversion to the global one..
            #
            $module = $1;
            if ($module =~ /^(?:global|endmodule)$/) {
                $module = undef;
            }
            else {
                $module =~ s/^module\s+//i;
                $mkey = "module $module";
            }
        }
        elsif ($line !~ /=/) {
            #
            # Some sort of error; not a key=value line nor the
            # start of a module stanza
            #
        }
        else {
            my ($key, $value) = split(/\s*=\s*/, $line, 2);
            $value =~ s/^\s*(.*\S)\s*$/$1/;
            if ($module) {
                ${$params{$mkey}}{lc($key)} = $value;
            }
            else {
                $params{lc($key)} = $value;
            }
        }
    }
    if (! defined($params{'karma_file'})) {
        $params{'karma_file'} = 'avail';
    }
    $self->{_params} = \%params;
    $self->{_config} = {'cvsroot' => $cfgdir};
    bless($self, $class);
    _load_karma($self);
    return $self;
}

=pod

=head2 config($keyword [, $newval])

Gets or sets controls on how the SpackleConf package operates.
Keywords:

=over 2

=over 4

=item B<automerge>

This control affects the operation of the B<module_param>() method.
If set to a TRUE value, the B<module_param>() method will return
the global setting of the parameter if no module-specific one
exists.

=item B<strict>

If this control is set to a TRUE value, the B<param>() and
B<module_param>() methods cannot store nor access keywords
that aren't in the known set of parameters.  Default setting is FALSE.

=back

=back

Keywords are case-insensitive.

=cut

sub config {
    my $self = shift;
    my ($kw, $newval) = @_;
    my $key = lc($kw);

    my $exists = defined($self->{_config}->{$key});
    my $curval = $exists ? $self->{_config}->{$key} : undef;
    if ($#_ > 0) {
        if ($canonical_configs{$key} || (! $self->{_config}->{'strict'})) {
            $self->{_config}->{$key} = $newval;
        }
    }
    return $curval;
}

=pod

=head2 is_member_of($username, $namelist)

Returns a true value if the specified username appears in the
comma-separated namelist; otherwise returns false.  Group names
of the form B<:gname> are expanded just as they are in the karma
file.

=cut

sub is_member_of {
    my $self = shift;
    my ($user, $group) = @_;
    return grep(/^$user$/, $self->_resolve_id($group)) ? 1 : 0;
}

=pod

=head2 karma_file( void )

Returns a path to the repository karma file, either absolute or
relative according to the setting of the B<CVSROOT> environment
variable and how the SpackleConf object was created.

The name of the file may be set by the B<Karma_File> configuration
parameter.

=cut

sub karma_file {
    my $self = shift;
    return $self->config('cvsroot')
        . '/' . $self->param('Karma_File');
}

=pod

=head2 module_matches($modname, $modlist)

Returns a true value if B<$modname> is a proper descendent of
one (or more) of the comma-separated repository modules listed
in B<$modlist>.  An empty B<$modlist> imposes no restrictions
and all modules will match it.

=cut

sub module_matches {
    my $self = shift;
    my ($mname, $pattern) = @_;
    return 1 if (! $pattern);
    my $suffix = '(?:/|$)';
    $pattern =~ s:,:\|:g;
    return ($mname =~ m§^(?:$pattern)$suffix§) ? 1 : 0;
}

=pod

=head2 param($keyword [, $newvalue])

Returns the current setting of the specified configuration parameter,
or C<undef> if there is no such parameter defined.  If a second
argument is passed, the parameter will be set to the specified
value; the return value is I<still> the original setting or C<undef>.
Previously nonexistent parameters may be created this way.

Parameter names are case-insensitive.

=cut

@BOOLEAN_PARAMS = ('AllowCommitWithoutEmail', 'Debug',
                   'Enable_\w+', 'ReadOnly');
my $BOOLEANS = join('|', @BOOLEAN_PARAMS);
$BOOLEANS = qr§^(?:$BOOLEANS)$§;

sub param {
    my $self = shift;
    my ($key, $value) = @_;

    my $rkey = lc($key);
    if ($self->config('strict') && (! defined($canonical_params{$rkey}))) {
        carp("Unknown parameter '$key'");
        return undef;
    }
    my $exists = defined($self->{_params}->{$rkey});
    my $curval = $exists ? $self->{_params}->{$rkey} : undef;
    #
    # Special treatment for Booleans.  If they're one of the
    # true keywords, return 1; else 0.  When setting, use
    # 'On' and 'Off' respectively -- just in case we ever
    # write this stuff back to disk.
    #
    if ($key =~ /$BOOLEANS/i) {
        $curval = _boolean($curval);
        if ($value) {
            $value = _boolean($value) ? 'On' : 'Off';
        }
    }
    if ($#_ > 0) {
        $self->{_params}->{$rkey} = $value;
        $canonical_params{$rkey} = $key if (! $exists);
    }
    return $curval;
}

=pod

=head2 module_param($modulename, $keyword [, $newvalue])

Similar to the C<param()> method, but returns parameter
settings specific to the named module.  If the
C<automerge> control is not set, C<undef> will be returned
for any parameter that doesn't have a module-specific value;
if C<automerge> I<is> set, any global setting will be returned
if there is none specific to the module.

Returns the current setting of the specified configuration parameter,
or C<undef> if there is no such parameter defined.  If a second
argument is passed, the parameter will be set to the specified
value; the return value is I<still> the original setting or C<undef>.
Previously nonexistent parameters may be created this way.

Parameter names are case-insensitive.

=cut

sub module_param {
    my $self = shift;
    my ($module, $key, $value) = @_;
    my $mkey = "module $module";

    my $rkey = lc($key);
    if ($self->config('strict') && (! defined($canonical_params{$rkey}))) {
        carp("Unknown parameter '$key'");
        return undef;
    }
    my $exists = defined($self->{_params}->{$mkey}->{$rkey}) || 0;
    my $curval = ($exists
                  ? $self->{_params}->{$mkey}->{$rkey}
                  : ($self->config('automerge')
                     ? $self->param($rkey)
                     : undef));
    #
    # Special treatment for Booleans.  If they're one of the
    # true keywords, return 1; else 0.  When setting, use
    # 'On' and 'Off' respectively -- just in case we ever
    # write this stuff back to disk.
    #
    if ($key =~ /$BOOLEANS/i) {
        if ($curval) {
            $curval = _boolean($curval);
        }
        if ($value) {
            $value = _boolean($value) ? 'On' : 'Off';
        }
    }
    if ($#_ > 1) {
        $self->{_params}->{$mkey}->{$rkey} = $value;
        $canonical_params{$rkey} = $key if (! $exists);
    }
    return $curval;
}

=pod

=head2 report_groups([*FILE])

Simply displays a list of the groups as they exist at the time of the
call.  Unless called from within the karma-file parser, what will
be shown is the final definitions.  Intended for debugging
purposes only.

=cut

sub report_groups {
    my $self = shift;
    my ($fh) = @_;
    if ($#_ < 0) {
        $fh = \*STDOUT;
    }
    print $fh 'Groups from file: '
        . $self->config('cvsroot') . '/'
        . $self->param('karma_file')
        . "\n";
    for (sort(keys(%{$self->{_karma}->{_groups}}))) {
        print $fh "$_: "
            . join(',', @{$self->{_karma}->{_groups}->{$_}})
            . "\n";
    }
}

=pod

=head2 report_karma([*FILE])

Simply displays a list of all of the non-comment lines from the
karma file.  Groups are not expanded.  Intended for debugging
purposes only.

=cut

sub report_karma {
    my $self = shift;
    my ($fh) = @_;
    if ($#_ < 0) {
        $fh = \*STDOUT;
    }
    print $fh 'Karma from file: '
        . $self->config('cvsroot') . '/'
        . $self->param('karma_file')
        . "\n";
    for (sort(keys(%{$self->{_karma}}))) {
        next if ($_ eq '_groups');
        print $fh "$_: " . ${$self->{_karma}->{$_}}{_raw} . "\n";
    }
}

=pod

=head2 report_params([*FILE])

Reports the current parameter settings to the indicated file (STDOUT
if not specified) in the format

C<parameter = value>

=cut

sub report_params {
    my $self = shift;
    my ($file) = @_;
    if ($#_ < 0) {
        $file = \*STDOUT;
    }
    my %params = %{$self->{_params}};
    for (sort(keys(%params))) {
        my $key = $canonical_params{$_} || $_;
        print $file $key . " = " . $params{$_} . "\n";
    }
    return 1;
}

=pod

=head2 mail_subject($modulename [, $branch-tag])

Returns the canonical subject for the commit message.
This is determined from the module name, repository
parameters, and possibly I<per>-module settings as
well.  See the I<spackle.conf(5)> man page for details.

=cut

sub mail_subject {
    my $self = shift;
    my ($module, $tag) = @_;
    my $merge_was = $self->config('automerge', 1);
    my $prefix = $self->module_param($module, 'Subject_Prefix');
    $self->config('automerge', $merge_was);
    my $string;

    if ($prefix) {
        if (! $tag) {
            #
            # No tag, so elide the effectors from the string.
            #
            $prefix =~ s/!B//g;
            $prefix =~ s/%B{.*?}B%//g;
        }
        else {
            #
            # First, substitute the tag name.
            #
            $prefix =~ s/!B/$tag/g;
            #
            # Now remove the effector delimiters, since their content is
            # to stay.
            #
            $prefix =~ s/(?:%B{|}B%)//g;
        }
    }
    my $subject = (($prefix ? "$prefix " : '')
                   . 'cvs commit: ' . $module);
    return $subject;
}

=pod

=head2 mail_to($modulename)

Returns the canonical distribution list for commit messages
for the specified module, as determined from the
C<mailmap> file.  See the I<mailmap(5)> man page for details.

=cut

sub mail_to {
    my $self = shift;
    my ($module) = @_;
    $self->_load_mailmap();
    my @maplines = @{$self->{'mailmap'}};
    my $to;
    my $fallback;

    for (@maplines) {
        my %line = %{$_};
        #
        # See if the pattern matches the module name.
        #
        my $always = ($line{'flags'} =~ /\balways\b/i);
        next if (($module !~ qr/$line{'pattern'}/)
                 && (! $always));
        next if ($line{'flags'} =~ /\bnever\b/i);
        my $eddress = $line{'eddress'};

        if (($line{'flags'} =~ qr:\bfallback\b:i)
            && (! $always)) {
            #
            # If it's a fallback address (to be used if no explicit ones
            # are found), save it for later.
            #
            $fallback .= ($fallback ? ', ' : '') . $eddress;
        }
        else {
            #
            # It's an explicit mapping, so record it.
            #
            $to .= ($to ? ', ' : '') . $eddress;
        }
        #
        # Endit if the matching line said "I'm the last".
        #
        last if ($line{'flags'} =~ qr:\blast\b:i);
    }
    #
    # Use the fallback list if no explicit list was found.
    #
    if ((! $to) && $fallback) {
        $to = $fallback;
    }
    #
    # Of course, if there's no fallback list either, we might
    # end up returning nothing.  Use the administrator's eddress
    # in such a case.
    #
    if (! $to) {
        $to = $self->param('Maintainer_Email')
            || 'nobody'
            || 'Ken.Coar@Golux.Com';
    }
    return $to;
}

1;

=pod

=head1 AUTHOR

Rodent of Unusual Size <Ken.Coar@Golux.Com>

=head1 SEE ALSO

L<spackle(5)>,
L<spackle.conf(5)>,
L<spackle-mailmap(5)>

=cut
#
# Local Variables:
# mode: cperl
# cperl-indent-level: 4
# cperl-continued-statement-offset: 4
# cperl-under-as-char: nil
# End:
#
