##############################################################################
#
# Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
#
# This software is distributed under the terms of the Kupu
# License. See LICENSE.txt for license text. For a list of Kupu
# Contributors see CREDITS.txt.
#
##############################################################################

# $Id: Makefile 7619 2004-11-23 14:48:17Z duncan $

XSLTPROC = /usr/bin/env xsltproc
XSL_DEBUG = --param debug true\(\)
XSLTPROC_PARAMS = --nonet --novalid --xinclude
XSL_FILE = make.xsl

all: clean kupu.html kupuform.html kupumulti.html zope2macros plonemacros silvamacros lenyamacros kupucnf.html cyklotronmacros

kupu.html:
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o common/kupu.html $(XSL_FILE) dist.kupu

zope2macros:
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o common/kupumacros.html $(XSL_FILE) dist-zope2.kupu

kupuform.html:
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o common/kupuform.html $(XSL_FILE) dist-form.kupu

kupumulti.html:
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o common/kupumulti.html $(XSL_FILE) dist-multi.kupu

kupucnf.html:
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o common/kupucnf.html $(XSL_FILE) dist-cnf.kupu

plonemacros:
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o plone/kupu_plone_layer/kupu_wysiwyg_support.html $(XSL_FILE) dist-plone.kupu

silvamacros:
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o silva/kupumacros.html $(XSL_FILE) dist-silva.kupu

lenyamacros:
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o apache-lenya/kupu/kupumacros.html $(XSL_FILE) dist-apache-lenya.kupu

cyklotronmacros:
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o cyklotron/kupumacros.html $(XSL_FILE) dist-cyklotron.kupu

cyklotronmacros2:
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o cyklotron2/kupumacros.html $(XSL_FILE) dist-cyklotron2.kupu

clean:
	rm -f common/kupu.html
	rm -f common/kupumacros.html
	rm -f common/kupuform.html
	rm -f plone/kupu_plone_layer/kupu_wysiwyg_support.html
	rm -f silva/kupumacros.html
	rm -f common/kupumulti.html
	rm -f common/kupucnf.html
	rm -f cyklotron/kupumacros.html
	rm -f cyklotron2/kupumacros.html

debug:
	$(XSLTPROC) $(XSL_DEBUG) $(XSLTPROC_PARAMS) -o common/kupu.html $(XSL_FILE) dist.kupu
