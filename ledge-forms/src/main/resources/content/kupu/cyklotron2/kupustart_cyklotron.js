/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kupustart_cyklotron.js,v 1.1 2005-03-22 06:28:08 zwierzem Exp $

function startKupu(formName, controlName) {
	var theForm = document.forms[formName];
    // prepare frame
    var frame=document.getElementById('kupu-editor-'+controlName);
    // TODO: init multi kupu
	var kupu = initKupu(frame, theForm, controlName);
	kupu.initialize();
    // init document
	kupu.getInnerDocument().documentElement.getElementsByTagName('BODY').item(0).innerHTML
		= document.getElementById(frame.id+'-textarea').value;
};
