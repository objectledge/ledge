#! /bin/sh

#
# capture log
#
TEMPFILE=/tmp/cvslog$$.tmp
cat > $TEMPFILE

#
# Invoke spackle
#
$CVSROOT/CVSROOT/log_accum.pl $USER ${1} < $TEMPFILE

#
# Invoke cvsspam
#
$CVSROOT/CVSROOT/collect_diffs.rb --from $USER --to objectledge-cvshtml@lists.caltha.pl ${2} < $TEMPFILE

#
# clean up
#
echo ${1} ${2}
#rm $TEMPFILE