#! /bin/sh

echo -${1}- -`echo ${1} | cut -d , -f 1`-

#
# capture log
#
TEMPFILE=/tmp/cvslog$$.tmp
cat > $TEMPFILE

#
# Invoke spackle
#
echo $CVSROOT/CVSROOT/log_accum.pl $USER `echo ${1} | cut -d , -f 1` < $TEMPFILE
$CVSROOT/CVSROOT/log_accum.pl $USER `echo ${1} | cut -d , -f 1` < $TEMPFILE

#
# Invoke cvsspam
#
echo $CVSROOT/CVSROOT/collect_diffs.rb --from $USER --to objectledge-cvshtml@lists.caltha.pl ${1} < $TEMPFILE
$CVSROOT/CVSROOT/collect_diffs.rb --from $USER --to objectledge-cvshtml@lists.caltha.pl ${1} < $TEMPFILE

#
# clean up
#

rm $TEMPFILE