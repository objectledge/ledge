/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kupuanchortool.js,v 1.1 2005-03-22 06:28:08 zwierzem Exp $

function AnchorTool() {

  	this.KupuTool = KupuTool;
  	this.KupuTool();
  	this.LinkTool = LinkTool;
  	this.LinkTool();

	this.LinkTool_createContextMenuElements = this.createContextMenuElements;
    this.createContextMenuElements = function(selNode, event) {
        var ret = this.LinkTool_createContextMenuElements(selNode, event);
		if(ret[0].action == this.createLinkHandler)
		{
			ret = new Array();
		}
        return ret;
    };

    this.createAnchor = function(name) {
    	this.createLink('nowhere', 'anchor', name);
    };
};

/** create and edit links toolbox */
function AnchorToolBox(inputid, addbuttonid, toolboxid, plainclass, activeclass) {

  	this.KupuToolBox = KupuToolBox;
  	this.KupuToolBox();
    
    this.input = document.getElementById(inputid);
    this.addbutton = document.getElementById(addbuttonid);
    this.toolboxel = document.getElementById(toolboxid);
    this.plainclass = plainclass;
    this.activeclass = activeclass;

    this.initialize = function(tool, editor) {
        this.tool = tool;
        this.editor = editor;

    	addEventHandler(this.addbutton, 'click', this.createAnchorHandler, this);
    }

    this.createAnchorHandler = function(event) {
        var name = this.input.value;
        this.tool.createAnchor(name);
    };

    /** if we're inside a link, update the anchor input, else empty it */
    this.updateState = function(selNode) {
        var linkel = this.editor.getNearestParentOfType(selNode, 'a');
        if (linkel && linkel.getAttribute('name')) {
            // check first before setting a class for backward compatibility
            if (this.toolboxel) {
                this.toolboxel.className = this.activeclass;
            };
			this.input.value = linkel.getAttribute('name');
			this.toolboxToggler.show();
        } else {
            // check first before setting a class for backward compatibility
            if (this.toolboxel) {
                this.toolboxel.className = this.plainclass;
            };
            this.input.value = '';
        }
    };
    
    this.setToggler = function(toolboxToggler) {
    	this.toolboxToggler = toolboxToggler;
    }
};

/** create and edit links toolbox with anchors support */
function AnchorLinkToolBox(inputid, buttonid, anchorSelectId, toolboxid, plainclass, activeclass) {

	this.LinkToolBox = LinkToolBox;
	this.LinkToolBox(inputid, buttonid, toolboxid, plainclass, activeclass);

    this.anchorSelect = document.getElementById(anchorSelectId);

    /** This method is intentionally overriden to remove 'blur' event on url input field. */
    this.initialize = function(tool, editor) {
        this.tool = tool;
        this.editor = editor;
        addEventHandler(this.button, "click", this.addLink, this);

	    addEventHandler(this.anchorSelect, 'focus', this._buildAnchorsList, this);
	    addEventHandler(this.anchorSelect, 'change', this._setAnchorAsLink, this);
  //    addEventHandler(document.getElementById(delLinkButtonId), 'click', this.deleteLink, this);
		this._buildAnchorsList();
    };
    
    this.LinkToolBox_updateState = this.updateState;   
    /** if we're inside a link, update the anchor input */
    this.updateState = function(selNode) {
        var link = this.input.value;
    	// super call
    	this.LinkToolBox_updateState(selNode);
    	// local logic
        var linkel = this.editor.getNearestParentOfType(selNode, 'A');
        if (linkel && linkel.getAttribute('href')) {
			this.toolboxToggler.show();
        }
        else {
	        this.input.value = link; // super implementation looses the link :(
        };
    };

	/** Fill up anchor selector with anchor names defined in the edited document. */
    this._buildAnchorsList = function ()
    {
        // 1. prepare anchor list
        var anchorNames = this.getAnchorNames();
        // 1.1. clear options
        if(this.anchorSelect.options != null)
        {
            this.anchorSelect.options.length = 0;
        }
        // 1.2. add empty anchor
        var oOption = document.createElement("OPTION");
        oOption.text = '-------';
        oOption.value = '';
        if(_SARISSA_IS_MOZ)
        {
            this.anchorSelect.add(oOption, null);
        }
        else // IE
        {
            this.anchorSelect.add(oOption);
        }
        // 1.3. add anchor names
        for(var i=0; i < anchorNames.length; i++)
        {
            var oOption = document.createElement("OPTION");
            oOption.text = anchorNames[i];
            oOption.value = anchorNames[i];
            if(_SARISSA_IS_MOZ)
            {
                this.anchorSelect.add(oOption, null);
            }
            else // IE
            {
                this.anchorSelect.add(oOption);
            }
        }
    };
    
    /** Retrieves names of anchor nodes in the edited document. */
    this.getAnchorNames = function ()
    {
        var anchorNodes = this.editor.getInnerDocument().getElementsByTagName("A");
        var anchorNames = [];
        for(var i = 0; i < anchorNodes.length; i++)
        {
            // differ links from anchors
            if(anchorNodes[i].getAttribute("name"))
            {
                anchorNames[anchorNames.length] = anchorNodes[i].getAttribute("name");
            }
        }
        return anchorNames;
    };
    
    this._setAnchorAsLink = function (event)
    {
    	if(this.anchorSelect.selectedIndex > 0)
    	{
	    	this.input.value = "#"+this.anchorSelect.value;
	    	this.anchorSelect.selectedIndex = 0;
		}
    };

    this.setToggler = function(toolboxToggler) {
    	this.toolboxToggler = toolboxToggler;
    }
};
