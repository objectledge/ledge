#! /bin/sh

#
# Invoke spackle
#
$CVSROOT/CVSROOT/log_accum.pl $USER ${1}

#
# Invoke cvsspam
#
$CVSROOT/CVSROOT/collect_diffs.rb --from $USER --to objectledge-cvshtml@lists.caltha.pl ${2}