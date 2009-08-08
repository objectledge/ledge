/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kuputoolboxtoggler.js,v 1.1 2005-03-22 06:28:08 zwierzem Exp $

function getChildrenByTagName(element, tagName) {
	var nodeList = new Array();
	for(var i=0; i<element.childNodes.length; i++)	{
		var child = element.childNodes.item(i);
		if(child.nodeType == Node.ELEMENT_NODE
		&& child.tagName == tagName) {
			nodeList.push(child);
		}
	}
	return nodeList;
}

function ToolboxTogglerManager() {
	
	this.togglers = new Array();
	
	this.registerToggler = function(toggler) {
		this.togglers[this.togglers.length] = toggler;
	};
	
	this.show = function(toggler) {
		for(var i=0; i<this.togglers.length; i++) {
			if(this.togglers[i] != toggler && this.togglers[i].radioHiding) {
				this.togglers[i].hide();
			}
		}
	};
};

function ToolboxToggler(toolbox, plainClass, selectedClass,
	clickableElementName, hidingElementName) {
	
	this.init = function (togglerManager) {
    	if(this.registered && togglerManager) {
		    this.togglerManager = togglerManager;
		    this.togglerManager.registerToggler(this);
    	}
	};

    /** Returns true if the element is hidden. */
    this.hidden = function() {
		return (this.hidingElement.style.display == 'none');
    };

    /** Hides the toolbox. */
    this.hide = function() {
		this.hidingElement.style.display = 'none';
  		this.clickableElement.className = this.plainClass;
    };

    /** Shows the toolbox. */
    this.show = function() {
    	if(this.togglerManager && this.radioHiding) {
    		this.togglerManager.show(this);
    	}
		this.hidingElement.style.display = 'block';
   		this.clickableElement.className = this.selectedClass;
    };

    /** Toggles the element visibility. */
    this.toggle = function() {
    	if(this.hidden()) {
    		this.show();
    	}
    	else {
    		this.hide();
    	}
    };

    /** Hack for heading highlighting. */
    this.headHighlight = function(event) {
    	if(!this.hidden() || event.type.match(/over/)) {
    		this.clickableElement.className = this.selectedClass;
    	}
    	else {
    		this.clickableElement.className = this.plainClass;
    	}
    };

    this.setRadioHiding = function(val) {
    	if(val == true) {
			this.radioHiding = true;
    	}
    	else {
			this.radioHiding = false;
    	}
    };

	this.radioHiding = true;
	this.registered = false;
	if(toolbox.setToggler) {
		toolbox.setToggler(this);
		this.registered = true;
	}

	this.plainClass = plainClass;
	this.selectedClass = selectedClass;

    var parentElement = toolbox.toolboxel;
	hidingElementName = hidingElementName || "DIV";
	var nodes = getChildrenByTagName(parentElement, hidingElementName);
	this.hidingElement = nodes[0];

	clickableElementName = clickableElementName || "H1";
	nodes = getChildrenByTagName(parentElement, clickableElementName);
	this.clickableElement = nodes[0];
	addEventHandler(this.clickableElement, 'click', this.toggle, this);
	addEventHandler(this.clickableElement, 'mouseover', this.headHighlight, this);
	addEventHandler(this.clickableElement, 'mouseout', this.headHighlight, this);
};

